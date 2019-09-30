package com.Oded.RestLogger;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.BufferedWriter;


public class Printer extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final BufferedWriter writer;
    public Printer(BufferedWriter bw){
        this.writer = bw;
    }

    public static Props props( BufferedWriter bw) {
        return Props.create(Printer.class, () -> new Printer(bw));
    }

    static public class PrintOrder {
        public PrintOrder() {
        }
    }

    static public class LogMessage {
        public final String message;

        public LogMessage(String message) {
            this.message = message;
        }
    }

    @Override
    public akka.actor.AbstractActor.Receive createReceive(){
        return receiveBuilder()
                .match(
                        PrintOrder.class,
                        s -> {
                            writer.flush();
                        })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

}
