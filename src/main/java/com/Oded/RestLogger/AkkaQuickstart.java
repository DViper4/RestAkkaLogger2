package com.Oded.RestLogger;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AkkaQuickstart {

    private final int numWorkers = 3;
    private int currWorker = 0;
    static ActorRef[] workers = new ActorRef[3];
    @RequestMapping("/log/{input}")
    public void log(@PathVariable String input){
        workers[currWorker].tell(new Worker.Message("message 1"), ActorRef.noSender());
        currWorker = (currWorker++) % 3;
    }


    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("helloakka");
        try {

            FileWriter writer = new FileWriter("app.log");;
            BufferedWriter bw = new BufferedWriter(writer);

            final ActorRef printerActor =
                    system.actorOf(Printer.props(bw), "printerActor");
            workers[0] = system.actorOf(Worker.props(printerActor), "workerActor1");
            workers[1] = system.actorOf(Worker.props(printerActor), "workerActor2");
            workers[2] = system.actorOf(Worker.props(printerActor), "workerActor3");

            workers[0].tell(new Worker.Message("message 1"), ActorRef.noSender());
            workers[1].tell(new Worker.Message("message 2"), ActorRef.noSender());
            workers[2].tell(new Worker.Message("message 3"), ActorRef.noSender());

            SpringApplication.run(AkkaQuickstart.class, args);

        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }
    }
}
