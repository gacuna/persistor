package coop.bancocredicoop.guv.persistor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import coop.bancocredicoop.guv.persistor.actors.UpdateImporteActor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static coop.bancocredicoop.guv.persistor.utils.SpringExtension.SPRING_EXTENSION_PROVIDER;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PersistorApplicationTests {

	@Autowired
	private ActorSystem system;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testAkkaActors() throws Exception {
		ActorRef importeActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
				.props("updateImporteActor"), "importeActor");

		FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
		Timeout timeout = Timeout.durationToTimeout(duration);

		Future<Object> result = ask(importeActor, new UpdateImporteActor.UpdateImporteMessage(), timeout);

		Assert.assertEquals("OK", Await.result(result, duration));
	}

}
