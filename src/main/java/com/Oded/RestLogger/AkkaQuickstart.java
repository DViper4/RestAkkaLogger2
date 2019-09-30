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
    final ActorSystem system;
    private final int numWorkers = 3;
    private int currWorker = 0;
    static ActorRef[] workers = new ActorRef[3];
    @RequestMapping("/log/{input}")
    public void log(@PathVariable String input){
        System.out.println("in log : " + input + "\n");
        workers[currWorker].tell(new Worker.Message(input), ActorRef.noSender());
        currWorker = (currWorker++) % 3;
    }

    public AkkaQuickstart(){
        system = ActorSystem.create("helloakka");
        try {
            FileWriter writer = new FileWriter("app.log");;
            BufferedWriter bw = new BufferedWriter(writer);

            final ActorRef printerActor =
                    system.actorOf(Printer.props(bw), "printerActor");
            workers[0] = system.actorOf(Worker.props(printerActor), "workerActor1");
            workers[1] = system.actorOf(Worker.props(printerActor), "workerActor2");
            workers[2] = system.actorOf(Worker.props(printerActor), "workerActor3");
        } catch (IOException ioe) {
        } finally {
        }
        }

    public static void main(String[] args) {
        SpringApplication.run(AkkaQuickstart.class, args);
    }
}
