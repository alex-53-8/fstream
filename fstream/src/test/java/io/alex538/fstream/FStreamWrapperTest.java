package io.alex538.fstream;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class FStreamWrapperTest {

    List<String> stepsCalled = new LinkedList<>();
    final Function<UUID, String[]> converter1 = (in) -> {
        stepsCalled.add("converter-1");
        return in.toString().split("-");
    };
    final BiConsumer<String[], Consumer<String[]>> filter1 = (in, ctx) -> stepsCalled.add("filter-1");
    final Function<String[], String> converter2 = (in) -> {
        stepsCalled.add("converter-2");
        return "12345";
    };
    final Function<String, byte[]> converter3 = (in) -> {
        stepsCalled.add("converter-3");
        return in.getBytes();
    };
    final Function<byte[], String> converter4 = (in) -> {
        stepsCalled.add("converter-4");
        return new String(in);
    };

    @Test
    public void test_whenNoFilterInChain_thenAllConvertersAreCalled() {
        UUID input = UUID.randomUUID();

        FDownstream<UUID, String[]> step1 = new FDownstream<>(converter1);
        FDownstream<UUID, String> step2 = step1.chain(converter2);
        FDownstream<UUID, byte[]> step3 = step2.chain(converter3);
        FDownstream<UUID, String> step4 = step3.chain(converter4);

        assertEquals(0, stepsCalled.size());

        String[] result = new String[]{null};
        step4.apply(input, (rs) -> result[0] = rs);

        assertEquals("12345", result[0]);
        assertFalse(stepsCalled.contains("filter-1"));

        assertEquals(List.of("converter-1", "converter-2", "converter-3", "converter-4"), stepsCalled);
    }

    @Test
    public void test_whenNoFilterIsInChain_thenConvertersAfterTheFilterAreNotCalled() {
        UUID input = UUID.randomUUID();

        FDownstream<UUID, String[]> step1 = new FDownstream<>(converter1);
        FDownstream<UUID, String[]> filterStep2 = step1.chain(filter1);
        FDownstream<UUID, String> step2 = filterStep2.chain(converter2);
        FDownstream<UUID, byte[]> step3 = step2.chain(converter3);
        FDownstream<UUID, String> step4 = step3.chain(converter4);

        assertEquals(0, stepsCalled.size());

        String[] result = new String[]{null};
        step4.apply(input, (rs) -> result[0] = rs);

        assertNull(result[0]);
        assertEquals(List.of("converter-1", "filter-1"), stepsCalled);
    }


}