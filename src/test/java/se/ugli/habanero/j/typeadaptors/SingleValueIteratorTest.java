package se.ugli.habanero.j.typeadaptors;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.Test;

import se.ugli.habanero.j.Habanero;
import se.ugli.habanero.j.HabaneroException;

public class SingleValueIteratorTest {

	private final Habanero habanero = Habanero.apply();

	@Test
	public void isntRegistered() {
		habanero.execute("create table x(a int)");
		try {
			habanero.queryOne(Point.class, "select * from x");
		} catch (final HabaneroException e) {
			assertEquals("java.awt.Point isn't registered.", e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void illegalArgumentException() {
		habanero.execute("create table y(a int)");
		habanero.queryOne((Class<?>) null, "select * from y");
	}

}
