package se.ugli.habanero.j;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import se.ugli.habanero.j.batch.SqlScript;
import se.ugli.habanero.j.datasource.H2DataSource;

public class SqlScriptTest {

	@Test
	public void test() {
		final H2DataSource dataSource = new H2DataSource();
		final InputStream source = getClass().getResourceAsStream("/scripttest.sql");
		SqlScript.apply(dataSource).run(source);
		assertEquals(new Long(2), Habanero.apply(dataSource).queryOne(Long.class, "select count(*) from person").get());
	}

}
