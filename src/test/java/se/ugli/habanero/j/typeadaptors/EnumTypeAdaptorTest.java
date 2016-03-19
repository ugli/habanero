package se.ugli.habanero.j.typeadaptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.google.common.collect.Iterables;

import se.ugli.habanero.j.Habanero;

public class EnumTypeAdaptorTest {

    private final Habanero habanero = Habanero.apply();

    private enum Abc {
        A, B, C, Z
    }

    @Test
    public void shouldHandleCRUD() {
        habanero.execute("create table abc(x VARCHAR(10))");
        assertFalse(habanero.queryOne(Abc.class, "select x from abc where x=?", Abc.B).isPresent());
        assertEquals(1, habanero.update("insert into abc(x) values(?)", Abc.A));
        assertEquals(1, habanero.update("insert into abc(x) values(?)", Abc.B));
        assertEquals(1, habanero.update("insert into abc(x) values(?)", Abc.C));
        assertEquals(3, Iterables.size(habanero.queryMany(Abc.class, "select x from abc")));
        assertEquals(Abc.B, habanero.queryOne(Abc.class, "select x from abc where x=?", Abc.B).get());
        assertEquals(1, habanero.update("update abc set x=? where x=?", Abc.Z, Abc.B));
        assertEquals(3, Iterables.size(habanero.queryMany(Abc.class, "select x from abc")));
        assertFalse(habanero.queryOne(Abc.class, "select x from abc where x=?", Abc.B).isPresent());
        assertEquals(Abc.Z, habanero.queryOne(Abc.class, "select x from abc where x=?", Abc.Z).get());
        assertEquals(1, habanero.update("delete from abc where x=?", Abc.Z));
        assertEquals(2, Iterables.size(habanero.queryMany(Abc.class, "select x from abc")));
    }

}
