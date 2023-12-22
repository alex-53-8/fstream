package io.alex538.fstream;

import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FJdkSerializerTest {

    @Test
    void testSerializationDeserialization() throws IOException, ClassNotFoundException {
        TestDto original = new TestDto();
        assertEquals("1234567890", original.name);

        FJdkSerializer subject = new FJdkSerializer();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        subject.serialize(os, original);
        byte[] serialized = os.toByteArray();
        assertTrue(serialized.length > 0);

        ByteArrayInputStream is = new ByteArrayInputStream(serialized);
        TestDto deserialized = (TestDto)subject.deserialize(is);
        assertEquals(original, deserialized);
    }

}

@EqualsAndHashCode
class TestDto implements Serializable {
    String name = "1234567890";
}