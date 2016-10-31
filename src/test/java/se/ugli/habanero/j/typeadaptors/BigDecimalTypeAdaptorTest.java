package se.ugli.habanero.j.typeadaptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;

import se.ugli.habanero.j.Habanero;

public class BigDecimalTypeAdaptorTest {

    @Test
    public void NUMERIC() {
        final Habanero habanero = Habanero.apply();
        habanero.execute("create table abc(x NUMERIC)");
        assertFalse(
                habanero.queryOne(BigDecimal.class, "select x from abc where x=?", new BigDecimal(132345)).isPresent());
        assertEquals(1, habanero.update("insert into abc(x) values(?)", new BigDecimal(132345)));
        assertEquals(1, habanero.update("insert into abc(x) values(?)", new BigDecimal(132345.234)));
        assertEquals(2l, habanero.queryMany(Boolean.class, "select x from abc").count());
        final Optional<BigDecimal> boolOpt = habanero.queryOne(BigDecimal.class, "select x from abc where x=?",
                new BigDecimal(132345));
        assertTrue(boolOpt.isPresent());
        assertEquals(boolOpt.get(), new BigDecimal(132345));
    }

    @Test
    public void DECIMAL() {
        final Habanero habanero = Habanero.apply();
        habanero.execute("create table abc(x DECIMAL)");
        assertFalse(
                habanero.queryOne(BigDecimal.class, "select x from abc where x=?", new BigDecimal(132345)).isPresent());
        assertEquals(1, habanero.update("insert into abc(x) values(?)", new BigDecimal(132345)));
        assertEquals(1, habanero.update("insert into abc(x) values(?)", new BigDecimal(132345.234)));
        assertEquals(2l, habanero.queryMany(Boolean.class, "select x from abc").count());
        final Optional<BigDecimal> boolOpt = habanero.queryOne(BigDecimal.class, "select x from abc where x=?",
                new BigDecimal(132345));
        assertTrue(boolOpt.isPresent());
        assertEquals(boolOpt.get(), new BigDecimal(132345));
    }
}
