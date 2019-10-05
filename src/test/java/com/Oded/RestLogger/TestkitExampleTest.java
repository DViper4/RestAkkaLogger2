package com.Oded.RestLogger;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import org.junit.Test;

import java.util.HashSet;

public class TestkitExampleTest extends JUnitRouteTest {
    ActorSystem system;
    TestRoute appRoute;

    public TestkitExampleTest(){
        system = ActorSystem.create("routes");
        appRoute = testRoute(new AkkaQuickstart(system).createRoute());
    }
    @Test
    public void test1() {
        appRoute.run(HttpRequest.PUT("/log?level=debug&content=test"))
                .assertStatusCode(202)
                .assertEntity("log message printed\n");
    }

    @Test
    public void test2() {
        appRoute.run(HttpRequest.PUT("/log?level=not_valid_level&content=test"))
                .assertStatusCode(400)
                .assertEntity("Not a valid level\n");
    }
}
