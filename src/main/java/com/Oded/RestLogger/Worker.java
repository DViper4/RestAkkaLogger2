package com.Oded.RestLogger;

import akka.actor.*;
import akka.routing.*;
import com.typesafe.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ActorSystem;
import akka.actor.AbstractActor.Receive;

import javax.swing.text.MaskFormatter;

public class Worker extends AbstractActor {
    private final ActorRef printerActor;

    public Worker(ActorRef printerActor){
        this.printerActor = printerActor;
    }
    static public class LogLevel {
        public final String level;

        public LogLevel(String type) {
            this.level = type;
        }
    }

    static public class Message {
        private String log_message = "";
        public Message(String log_message) {
            this.log_message = log_message;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, ll -> {
                    System.out.println("in worker");
                    printerActor.tell(ll.log_message, getSelf());
                })
                .build();
    }

    public static Props props(ActorRef printerActor) {
        return Props.create(Worker.class, () -> new Worker(printerActor));
    }
}
