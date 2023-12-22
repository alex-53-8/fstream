package io.alex538.fstream;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

class FDownstream<In, Out> {
    private final BiConsumer<In, Consumer<Out>> current;

    FDownstream(Function<In, Out> current) {
        this.current = (in, consumer) -> {
            consumer.accept(current.apply(in));
        };
    }

    <CurrentOut> FDownstream(BiConsumer<In, Consumer<CurrentOut>> current, Function<CurrentOut, Out> next) {
        this(current, (in, consumer) -> {
            consumer.accept(next.apply(in));
        });
    }

    <CurrentOut> FDownstream(BiConsumer<In, Consumer<CurrentOut>> current, BiConsumer<CurrentOut, Consumer<Out>> next) {
        this.current = (in, consumer) -> {
            current.accept(in, (result) -> next.accept(result, consumer));
        };
    }

    <NewOut> FDownstream<In, NewOut> chain(BiConsumer<Out, Consumer<NewOut>> next) {
        return new FDownstream<>(current, next);
    }

    <NewOut> FDownstream<In, NewOut> chain(Function<Out, NewOut> next) {
        return new FDownstream<>(current, next);
    }

    void apply(In in, Consumer<Out> consumer) {
        current.accept(in, consumer);
    }

}
