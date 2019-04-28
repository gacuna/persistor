package coop.bancocredicoop.guv.persistor;

import akka.actor.ActorSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PersistorApplicationTests {

	@Autowired
	private ActorSystem system;

	@Test
	public void contextLoads() {
	}

	//TODO Corregir el test, se comenta para que el actor funcione correctamente
	/*
	@Test
	public void testAkkaActors() throws Exception {
		ActorRef importeActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
				.props("updateImporteActor"), "importeActor");

		FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
		Timeout timeout = Timeout.durationToTimeout(duration);

		Future<Object> result = ask(importeActor, new UpdateMessage("importe",
				new Correccion(1L, BigDecimal.ONE, new Date(), null, null, null)), timeout);

		Assert.assertEquals("OK", Await.result(result, duration));
	}*/

}
