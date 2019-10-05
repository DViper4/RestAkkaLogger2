package com.Oded.RestLogger;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

class Printer extends AbstractActor {
    PrintWriter writer;
    DateTimeFormatter dtf;

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    public Printer(String log_path, DateTimeFormatter dtf) throws FileNotFoundException, UnsupportedEncodingException {
        this.dtf = dtf;
        this.writer = new PrintWriter(log_path, "UTF-8");
    }
    static Props props(String log_path, DateTimeFormatter dtf) {
        return Props.create(Printer.class, log_path, dtf);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LogMessage.class, log_message -> {
                    LocalDateTime now = LocalDateTime.now();
                    writer.append(dtf.format(now) + " : " + log_message.level + " : " + log_message.content + "\n");
                    writer.flush();
                    log.info("LogMessage complete: time = {}, level = {}, content = {}\n", dtf.format(now), log_message.level, log_message.content);
                })
                .matchAny(o -> log.info("Invalid message\n"))
                .build();
    }
}