package se.ugli.habanero.j.internal;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class HabaneroPropertiesTest {

    @Test
    public void shouldHandleExistingPropertyResource() {
        assertNotNull(HabaneroProperties.apply(HabaneroProperties.RESOURCE, false));
    }

    @Test
    public void shouldHandleNonExistingPropertyResource() {
        assertNotNull(HabaneroProperties.apply(HabaneroProperties.RESOURCE + ".apa", false));
    }
}
