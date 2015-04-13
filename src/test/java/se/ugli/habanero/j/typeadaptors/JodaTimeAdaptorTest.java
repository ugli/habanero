package se.ugli.habanero.j.typeadaptors;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import se.ugli.habanero.j.Habanero;

public class JodaTimeAdaptorTest {

	@Test
	public void shouldHandleTimestamp() {
		final Habanero habanero = Habanero.apply();
		habanero.execute("create table joda(x TIMESTAMP)");
		assertEquals(1, habanero.update("insert into joda(x) values(?)", new DateTime("2015-03-26")));
		assertEquals(new DateTime("2015-03-26"), habanero.queryOne(DateTime.class, "select * from joda").get());
	}

	@Test
	public void shouldHandleDate() {
		final Habanero habanero = Habanero.apply();
		habanero.execute("create table joda(x DATE)");
		assertEquals(1, habanero.update("insert into joda(x) values(?)", new DateTime("2015-03-26")));
		assertEquals(new DateTime("2015-03-26"), habanero.queryOne(DateTime.class, "select * from joda").get());
	}

	@Test
	public void shouldHandleTime() {
		final Habanero habanero = Habanero.apply();
		habanero.execute("create table joda(x TIME)");
		assertEquals(1, habanero.update("insert into joda(x) values(?)", new DateTime(2015, 3, 26, 10, 27)));
		assertEquals(new DateTime(1970, 1, 1, 10, 27), habanero.queryOne(DateTime.class, "select * from joda").get());
	}
}
