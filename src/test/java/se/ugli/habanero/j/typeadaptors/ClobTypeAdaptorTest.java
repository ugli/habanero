package se.ugli.habanero.j.typeadaptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import se.ugli.commons.Option;
import se.ugli.commons.Resource;
import se.ugli.habanero.j.Habanero;

import com.google.common.collect.Iterables;

public class ClobTypeAdaptorTest {

	@Test
	public void crud() {
		final Habanero habanero = Habanero.apply();
		habanero.execute("create table abc(x int, y clob)");
		assertFalse(habanero.queryOne(Boolean.class, "select y from abc where x=?", 1).isDefined());
		assertEquals(1, habanero.update("insert into abc(x,y) values(?,?)", 1, "hej"));
		assertEquals(1, habanero.update("insert into abc(x,y) values(?,?)", 2, "hej svejs"));
		assertEquals(2, Iterables.size(habanero.queryMany(Integer.class, "select x from abc")));
		{
			final Option<String> opt = habanero.queryOne(String.class, "select y from abc where x=?", 1);
			assertTrue(opt.isDefined());
			assertEquals("hej", opt.get());
		}
		{
			assertEquals(1, habanero.update("update abc set y=? where x=?", "tjo flöjt", 1));
			final Option<String> opt = habanero.queryOne(String.class, "select y from abc where x=?", 1);
			assertTrue(opt.isDefined());
			assertEquals("tjo flöjt", opt.get());
		}
	}

	@Test
	public void largeXmlFile() throws IOException {
		final byte[] bytes1 = IOUtils.toByteArray(Resource.apply("/pubmed-example.xml").getInputStream());
		final Habanero habanero = Habanero.apply();
		habanero.execute("create table abc(x int, y clob)");
		assertEquals(1, habanero.update("insert into abc(x,y) values(?,?)", 1, new String(bytes1)));
		final Option<String> opt = habanero.queryOne(String.class, "select y from abc where x=?", 1);
		assertTrue(opt.isDefined());
		assertEquals(new String(bytes1), opt.get());

	}

}
