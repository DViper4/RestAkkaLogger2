package com.Oded.RestLogger;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletionStage;

public class AkkaQuickstart extends AllDirectives {

    private final ActorRef auction;
    private final String log_path;
    private Config conf;

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

    private AkkaQuickstart(final ActorSystem system) {
        conf =  ConfigFactory.load();
        log_path = conf.getString("log-file.path");
        auction = system.actorOf(Printer.props(log_path), "auction");
    }

    private Route createRoute() {
        return concat(
                path("log", () -> concat(
                        put(() ->
                                parameter("level", level ->
                                        parameter("content", content -> {
                                            // place a bid, fire-and-forget
                                            auction.tell(new LogMessage(level, content), ActorRef.noSender());
                                            return complete(StatusCodes.ACCEPTED, "log message printed\n");
                                        })
                                )))));
    }

    static class LogMessage {
        final String level;
        final String content;

        LogMessage(String level, String content) {
            this.level = level;
            this.content = content;
        }
    }

    static class Printer extends AbstractActor {
        PrintWriter writer;

        private final LoggingAdapter log = Logging.getLogger(context().system(), this);
        public Printer(String log_path) throws FileNotFoundException, UnsupportedEncodingException {
            this.writer = new PrintWriter(log_path, "UTF-8");
        }
        static Props props(String log_path) {
            return Props.create(Printer.class, log_path);
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(AkkaQuickstart.LogMessage.class, log_message -> {
                        writer.append(log_message.level + " : " + log_message.content + "\n");
                        writer.flush();
                        log.info("LogMessage complete: level = {}, content = {}\n", log_message.level, log_message.content);
                    })
                    .matchAny(o -> log.info("Invalid message\n"))
                    .build();
        }
    }
}