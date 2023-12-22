package io.alex538.fstream;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ChainTest {

    @Test
    public void test() {
        UUID input = UUID.randomUUID();

        Step<UUID, String[]> step1 = new Step<>((in) -> in.toString().split("-"));
        Step<UUID, String> step2 = step1.chain((in) -> "12345");
        Step<UUID, byte[]> step3 = step2.chain((in) -> in.getBytes());

        String[] result1 = step1.apply(input);
        String result2 = step2.apply(input);
        byte[] result3 = step3.apply(input);

        assertEquals("12345", result2);
    }

}

/*
 Enter   In           Out
 UUID    UUID         String[]
 UUID    String[]     String
 UUID    String       byte[]
 */

class Step<In, Out> {

    Function<In, Out> converter;

    public Step(Function<In, Out> converter) {
        this.converter = converter;
    }

    public <BeforeOut> Step(Function<In, BeforeOut> before, Function<BeforeOut, Out> next) {
        this.converter = (in) -> next.apply(before.apply(in));
    }

    public <NewOut> Step<In, NewOut> chain(Function<Out, NewOut> next) {
        return new Step<>(converter, next);
    }

    public Out apply(In in) {
        return converter.apply(in);
    }

}