package io.alex538.fstream;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FFileStorageTest {

    @Test
    public void test_String() {
        try (FFileStorage<String> storage = new FFileStorage<>(new FJdkSerializer(), "/tmp", "storage-1")) {

            storage.write("1234567890");
            storage.write("2345678901");
            storage.write("3456789012");
            storage.write("4567890123");
            storage.write("5678901234");

            List<String> result = new LinkedList<>();

            storage.forEach(result::add);

            assertEquals(List.of("1234567890", "2345678901", "3456789012", "4567890123", "5678901234"), result);
        }
    }
    @Test
    public void test_TestDto() {
        try (FFileStorage<TestDto> storage = new FFileStorage<>(new FJdkSerializer(), "/tmp", "storage-1")) {
            var sample1 = new TestDto(100, 400);
            var sample2 = new TestDto(200, 500);
            var sample3 = new TestDto(300, 600);
            var sample4 = new TestDto(400, 700);

            storage.write(sample1);
            storage.write(sample2);
            storage.write(sample3);
            storage.write(sample4);

            List<TestDto> result = new LinkedList<>();

            storage.forEach(result::add);

            var expected = List.of(sample1, sample2, sample3, sample4);

            assertEquals(expected.size(), result.size());

            for(int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i), result.get(i));
            }
        }
    }

    @Value
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class TestDto implements Serializable {
        int one;
        int two;
    }

}