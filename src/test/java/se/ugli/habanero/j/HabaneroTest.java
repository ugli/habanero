package se.ugli.habanero.j;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.ugli.java.util.stream.Collectors.toImmutableList;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;

import se.ugli.habanero.j.test.HabaneroTestConfig;
import se.ugli.habanero.j.test.HabaneroTestRunner;
import se.ugli.java.util.ImmutableList;

@RunWith(HabaneroTestRunner.class)
@HabaneroTestConfig(schema = "/person.sql", dataset = "/person.xml")
public class HabaneroTest {

    private final static Habanero habanero = Habanero.apply();

    @Test
    public void queryManyByType() {
        try (Stream<String> stream = habanero.queryMany(String.class, "select name from person")) {
            final List<String> names = stream.collect(toList());
            assertEquals(3, names.size());
            assertEquals("fredde", names.get(0));
            assertEquals("lasse", names.get(1));
            assertEquals("banan", names.get(2));
        }
    }

    @Test(expected = HabaneroException.class)
    public void shouldQueryManyGenerateSqlError() {
        try (Stream<String> stream = habanero.queryMany(String.class, "select name from persons")) {

        }
    }

    private class Address {

        final String street;

        Address(final String street) {
            this.street = street;
        }

        @Override
        public String toString() {
            return "Address [street=" + street + "]";
        }

    }

    private class Person {
        final Optional<String> name;
        final Optional<Integer> age;
        final ImmutableList<Address> adresses;

        Person(final Optional<String> name, final Optional<Integer> age, final Stream<Address> adresses) {
            this.name = name;
            this.age = age;
            this.adresses = adresses.collect(toImmutableList());
        }

        @Override
        public String toString() {
            return "Person [name=" + name + ", age=" + age + ", adresses=" + adresses + "]";
        }

    }

    @Test
    public void queryManyResultSetTransform() {
        final String sql = "select name,age from person";
        final List<Person> persons = habanero.queryMany(sql).map(input -> {
            try {
                return new Person(Optional.of(input.getString("name")), Optional.of(input.getInt("age")),
                        Stream.empty());
            }
            catch (final SQLException e) {
                throw new HabaneroException(e);
            }
        }).collect(toList());
        assertEquals(3, persons.size());
        assertEquals("fredde", persons.get(0).name.get());
        assertEquals(new Integer(44), persons.get(0).age.get());
        assertEquals("banan", persons.get(2).name.get());
        assertEquals(new Integer(2), persons.get(2).age.get());
    }

    @Test
    public void queryManyResultMapTransform() {
        final String sql = "select name,age from person";
        final List<Person> persons = habanero.queryMany(sql).map(new ResultTupleFactory()).map(input -> {
            return new Person(input.get(String.class, "name"), input.get(Integer.class, "age"), Stream.empty());
        }).collect(toList());
        assertEquals(3, persons.size());
        assertEquals("fredde", persons.get(0).name.get());
        assertEquals(new Integer(44), persons.get(0).age.get());
        assertEquals("banan", persons.get(2).name.get());
        assertEquals(new Integer(2), persons.get(2).age.get());
    }

    Group<Person> personGrouping = Group.by(Person.class, "name")
            .with((cxt1,
                    rt1) -> Optional.of(new Person(rt1.get(String.class, "name"), rt1.get(Integer.class, "age"),
                            cxt1.groupBy(Address.class, "street")
                                    .with((cxt2, rt2) -> rt2.get(String.class, "street").map(Address::new)))));

    @Test
    public void queryManyGroup() {
        String sql = "select name,age,street from person ";
        sql += "left join address on name=person";
        final List<Person> persons = habanero.queryMany(personGrouping, sql).collect(toList());
        assertEquals(3, persons.size());
        {
            final Person person = persons.get(0);
            assertEquals("fredde", person.name.get());
            assertEquals(new Integer(44), person.age.get());
            assertEquals(2, person.adresses.size());
            assertEquals("sveavägen", person.adresses.get(0).street);
            assertEquals("götgatan", person.adresses.get(1).street);
        }
        {
            final Person person = persons.get(1);
            assertEquals("lasse", person.name.get());
            assertEquals(new Integer(43), person.age.get());
            assertEquals("långholmsgatan", person.adresses.iterator().next().street);
        }
        {
            final Person person = persons.get(2);
            assertEquals("banan", person.name.get());
            assertEquals(new Integer(2), person.age.get());
            assertEquals(true, person.adresses.isEmpty());
        }
    }

    @Test
    public void queryOneGroup() {
        String sql = "select name,age,street from person ";
        sql += "left join address on name=person ";
        sql += "where name=?";
        final Optional<Person> opt = habanero.queryOne(personGrouping, sql, "fredde");
        assertTrue(opt.isPresent());
        final Person person = opt.get();
        assertEquals("fredde", person.name.get());
        assertEquals(new Integer(44), person.age.get());
        assertEquals(2, person.adresses.size());
        assertEquals("götgatan", person.adresses.get(1).street);
        assertEquals("sveavägen", person.adresses.get(0).street);
    }

    @Test
    public void queryOneGroupNotFound() {
        final String sql = "select name,age from person where name=?";
        assertFalse(habanero.queryOne(personGrouping, sql, "laban").isPresent());
    }

    @Test
    public void shouldHandleCRUD() {
        assertFalse(habanero.queryOne(Integer.class, "select age from person where name=?", "ubbe").isPresent());
        assertEquals(1, habanero.update("insert into person(name,age) values(?,?)", "ubbe", 10));
        assertEquals(new Integer(10),
                habanero.queryOne(Integer.class, "select age from person where name=?", "ubbe").get());
        assertEquals(1, habanero.update("update person set age=? where name=?", 43, "ubbe"));
        assertEquals(new Integer(43),
                habanero.queryOne(Integer.class, "select age from person where name=?", "ubbe").get());
        assertEquals(1, habanero.update("delete from person where name=?", "ubbe"));
        assertFalse(habanero.queryOne(Integer.class, "select age from person where name=?", "ubbe").isPresent());
    }

    @Test(expected = HabaneroException.class)
    public void shoulUpdateSqlError() {
        habanero.update("delete from persons where name=?", "ubbe");
    }

    @Test
    public void shoulUpdateWithNullValue() {
        assertEquals(1, habanero.update("insert into person(name,age) values(?,?)", "urban", null));
        assertFalse(habanero.queryOne(Integer.class, "select age from person where name=?", "urban").isPresent());
    }

    @Test(expected = HabaneroException.class)
    public void shoulThrow() {
        final String sql = "select name,age from personX";
        habanero.queryMany(sql);
    }

}
