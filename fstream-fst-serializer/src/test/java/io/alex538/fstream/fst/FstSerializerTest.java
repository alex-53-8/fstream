package io.alex538.fstream.fst;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

class FstSerializerTest {

    @Test
    public void testSerializationAndDeserialization() throws IOException {
        Item test = new Item("name-test");

        FstSerializer subject = new FstSerializer();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        subject.serialize(os, test);

        byte[] serialized = os.toByteArray();
        Item deserialized = (Item)subject.deserialize(new ByteArrayInputStream(serialized));

        assertEquals(test, deserialized);
    }

}

@Value
@EqualsAndHashCode
class Item implements Serializable {
    String name;
}
