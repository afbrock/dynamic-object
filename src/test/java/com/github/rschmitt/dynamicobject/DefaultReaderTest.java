package com.github.rschmitt.dynamicobject;

import static com.github.rschmitt.dynamicobject.DynamicObject.serialize;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import clojure.java.api.Clojure;

public class DefaultReaderTest {
    @Test
    public void testUnknownReader() {
        String edn = "#some-namespace/some-record-name{:key :value}";
        Object obj = DynamicObject.deserialize(edn, Object.class);
        Unknown unknown = (Unknown) obj;

        assertEquals("some-namespace/some-record-name", unknown.getTag());
        assertEquals(Clojure.read("{:key :value}"), unknown.getElement());
        assertEquals(serialize(unknown), edn);
    }

    @Test(expected = RuntimeException.class)
    public void disableUnknownReader() {
        try {
            DynamicObject.setDefaultReader(null);
            DynamicObject.deserialize("#unknown{}", Object.class);
        } finally {
            DynamicObject.setDefaultReader(Unknown::new);
        }
    }

    @Test
    public void testUnknownSerialization() {
        Unknown map = new Unknown("tag", new HashMap());
        Unknown str = new Unknown("tag", "asdf");
        Unknown vec = new Unknown("tag", new ArrayList());

        assertEquals("#tag{}", serialize(map));
        assertEquals("#tag \"asdf\"", serialize(str));
        assertEquals("#tag []", serialize(vec));
    }
}
