package se.ugli.habanero.j.typeadaptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import se.ugli.habanero.j.Habanero;

public class BooleanTypeAdaptorTest {

    @Test
    public void booleanType() {
        final Habanero habanero = Habanero.apply();
        habanero.execute("create table abc(x BOOLEAN)");
        assertFalse(habanero.queryOne(Boolean.class, "select x from abc where x=?", true).isPresent());
        assertEquals(1, habanero.update("insert into abc(x) values(?)", true));
        assertEquals(1, habanero.update("insert into abc(x) values(?)", false));
        assertEquals(2l, habanero.queryMany(Boolean.class, "select x from abc").count());
        final Optional<Boolean> boolOpt = habanero.queryOne(Boolean.class, "select x from abc where x=?", true);
        assertTrue(boolOpt.isPresent());
        assertEquals(boolOpt.get(), true);
    }

    @Test
    public void boolType() {
        final Habanero habanero = Habanero.apply();
        habanero.execute("create table abc(x BOOL)");
        assertFalse(habanero.queryOne(Boolean.class, "select x from abc where x=?", true).isPresent());
        assertEquals(1, habanero.update("insert into abc(x) values(?)", true));
        assertEquals(1, habanero.update("insert into abc(x) values(?)", false));
        assertEquals(2l, habanero.queryMany(Boolean.class, "select x from abc").count());
        final Optional<Boolean> boolOpt = habanero.queryOne(Boolean.class, "select x from abc where x=?", true);
        assertTrue(boolOpt.isPresent());
        assertEquals(boolOpt.get(), true);
    }

    @Test
    public void smalIntType() {
        final Habanero habanero = Habanero.apply();
        habanero.execute("create table abc(x SMALLINT)");
        assertFalse(habanero.queryOne(Boolean.class, "select x from abc where x=?", true).isPresent());
        assertEquals(1, habanero.update("insert into abc(x) values(?)", true));
        assertEquals(1, habanero.update("insert into abc(x) values(?)", false));
        assertEquals(2l, habanero.queryMany(Boolean.class, "select x from abc").count());
        final Optional<Boolean> boolOpt = habanero.queryOne(Boolean.class, "select x from abc where x=?", true);
        assertTrue(boolOpt.isPresent());
        assertEquals(boolOpt.get(), true);
    }

}
