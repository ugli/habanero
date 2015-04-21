package se.ugli.habanero.j.typeadaptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.ugli.commons.Option;
import se.ugli.habanero.j.Habanero;

import com.google.common.collect.Iterables;

public class BooleanTypeAdaptorTest {

	@Test
	public void booleanType() {
		final Habanero habanero = Habanero.apply();
		habanero.execute("create table abc(x BOOLEAN)");
		assertFalse(habanero.queryOne(Boolean.class, "select x from abc where x=?", true).isDefined());
		assertEquals(1, habanero.update("insert into abc(x) values(?)", true));
		assertEquals(1, habanero.update("insert into abc(x) values(?)", false));
		assertEquals(2, Iterables.size(habanero.queryMany(Boolean.class, "select x from abc")));
		final Option<Boolean> boolOpt = habanero.queryOne(Boolean.class, "select x from abc where x=?", true);
		assertTrue(boolOpt.isDefined());
		assertEquals(boolOpt.get(), true);
	}

	@Test
	public void boolType() {
		final Habanero habanero = Habanero.apply();
		habanero.execute("create table abc(x BOOL)");
		assertFalse(habanero.queryOne(Boolean.class, "select x from abc where x=?", true).isDefined());
		assertEquals(1, habanero.update("insert into abc(x) values(?)", true));
		assertEquals(1, habanero.update("insert into abc(x) values(?)", false));
		assertEquals(2, Iterables.size(habanero.queryMany(Boolean.class, "select x from abc")));
		final Option<Boolean> boolOpt = habanero.queryOne(Boolean.class, "select x from abc where x=?", true);
		assertTrue(boolOpt.isDefined());
		assertEquals(boolOpt.get(), true);
	}

	@Test
	public void smalIntType() {
		final Habanero habanero = Habanero.apply();
		habanero.execute("create table abc(x SMALLINT)");
		assertFalse(habanero.queryOne(Boolean.class, "select x from abc where x=?", true).isDefined());
		assertEquals(1, habanero.update("insert into abc(x) values(?)", true));
		assertEquals(1, habanero.update("insert into abc(x) values(?)", false));
		assertEquals(2, Iterables.size(habanero.queryMany(Boolean.class, "select x from abc")));
		final Option<Boolean> boolOpt = habanero.queryOne(Boolean.class, "select x from abc where x=?", true);
		assertTrue(boolOpt.isDefined());
		assertEquals(boolOpt.get(), true);
	}

}
