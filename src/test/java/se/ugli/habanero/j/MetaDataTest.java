package se.ugli.habanero.j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import se.ugli.habanero.j.metadata.SqlType;

public class MetaDataTest {

	@Test
	public void test() {
		final Habanero habanero = Habanero.apply();
		habanero.execute("create table abc(x int, y clob)");
		assertThat(habanero.metadata().getColumnType("abc", "x").get(), equalTo(SqlType.INTEGER));
		assertThat(habanero.metadata().getColumnType("abc", "y").get(), equalTo(SqlType.CLOB));
	}

}
