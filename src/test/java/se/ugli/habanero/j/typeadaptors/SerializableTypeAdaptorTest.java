package se.ugli.habanero.j.typeadaptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Test;

import se.ugli.habanero.j.Habanero;
import se.ugli.habanero.j.util.Option;

public class SerializableTypeAdaptorTest {

	private final Habanero habanero = Habanero.apply();

	@Test
	public void crud() {
		habanero.execute("create table x(a varchar)");
		assertFalse(habanero.queryOne(Point.class, "select * from x").isDefined());
		habanero.update("insert into x(a) values(?)", new Point(3, 3));
		final Option<Point> pointOpt = habanero.queryOne(Point.class, "select * from x");
		assertTrue(pointOpt.isDefined());
		assertEquals(3, pointOpt.get().x);
		assertEquals(3, pointOpt.get().y);
	}

}
