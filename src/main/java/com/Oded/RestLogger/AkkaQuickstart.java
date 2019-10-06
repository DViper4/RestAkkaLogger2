package com.Oded.RestLogger;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.*;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.concurrent.CompletionStage;

public class AkkaQuickstart extends AllDirectives {

    private final ActorRef auction;
    private final String log_path;
    private Config conf;
    private HashSet<String> allowed_levels;
    static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    //    static final LocalDateTime now = LocalDateTime.now();

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        AkkaQuickstart app = new AkkaQuickstart(system);


        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("0.0.0.0", 8080), materializer);

        System.out.println("Server listening on port 8080/\nPress RETURN to stop...\n");
        System.in.read();

        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    AkkaQuickstart(final ActorSystem system) {
        conf =  ConfigFactory.load();
        log_path = conf.getString("log-file.path");
        auction = system.actorOf(Printer.props(log_path, dtf), "auction");

        allowed_levels = new HashSet<>();
        allowed_levels.add("trace");
        allowed_levels.add("debug");
        allowed_levels.add("info");
        allowed_levels.add("warn");
        allowed_levels.add("error");
        allowed_levels.add("critical");
    }

    Route createRoute() {
        return concat(
                put(() ->
                        concat(
                    pathSingleSlash(() ->
                            complete(HttpEntities.create(ContentTypes.TEXT_HTML_UTF8, "<html><body>Hello world!</body></html>"))
                    ),
                    path("log", () ->

                    parameter("level", level ->
                    validate(() -> allowed_levels.contains(level),
                            "Not a valid level\n",
                            () -> concat(
                                    put(() ->
                                            parameter("level", level2 ->
                                                    parameter("content", content -> {
                                                        auction.tell(new LogMessage(level2, content), ActorRef.noSender());
                                                        return complete(StatusCodes.ACCEPTED, "log message printed\n");
                                                    })
                                            ))))
            )
            )
                )),
                get(
                        () -> pathSingleSlash(() ->
                                complete(HttpEntities.create(ContentTypes.TEXT_HTML_UTF8, "<html><body>Hello world!</body></html>"))
                        )
                )
        );
    }
}