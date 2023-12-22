package io.alex538.fstream;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
final class FStreamWrapper<In extends Serializable, Out extends Serializable> implements FStream<Out> {

    private final FDownstream<In, Out> downstream;

    private final FCollection<In> collection;

    private final FCollection.Configuration cfg;

    FStreamWrapper(FCollection<In> collection, Function<In, Out> converter, FCollection.Configuration cfg) {
        this.collection = collection;
        this.downstream = new FDownstream<>(converter);
        this.cfg = cfg;
    }

    FStreamWrapper(FCollection<In> collection, FDownstream<In, Out> downstream, FCollection.Configuration cfg) {
        this.collection = collection;
        this.downstream = downstream;
        this.cfg = cfg;
    }

    public FStreamWrapper<In, Out> filter(Predicate<Out> predicate) {
        BiConsumer<Out, Consumer<Out>> filter = (in, consumer) -> {
            if (predicate.test(in)) {
                consumer.accept(in);
            }
        };
        return new FStreamWrapper<>(collection, downstream.chain(filter), cfg);
    }

    public <NewOut extends Serializable> FStreamWrapper<In, NewOut> flatMap(Function<Out, ? extends Stream<NewOut>> mapper) {
        BiConsumer<Out, Consumer<NewOut>> flatMap = (in, consumer) -> {
            Stream<NewOut> stream = mapper.apply(in);

            Objects.requireNonNull(stream, "Output stream cannot be null");

            stream.forEach(consumer);
            stream.close();
        };
        return new FStreamWrapper<>(collection, downstream.chain(flatMap), cfg);
    }

    public FStream<Out> sort(Comparator<Out> comparator) {
        FCollection<Out> c = new FCollectionIntermediate<>(collection.newEmptyInstance(), cfg);
        collection.forEach((in) -> downstream.apply(in, c::add));
        c.sort(comparator);
        return c.stream();
    }

    public <NewOut extends Serializable> FStreamWrapper<In, NewOut> map(Function<Out, NewOut> mapper) {
        return new FStreamWrapper<>(collection, downstream.chain(mapper), cfg);
    }

    public FCollection<Out> collect() {
        FCollection<Out> c = collection.newEmptyInstance();

        try {
            collection.forEach((in) -> downstream.apply(in, c::add));
        } catch (Exception e) {
            log.trace(e.getMessage(), e);
            try {
                c.close();
            } catch (Exception e2) {
                log.trace(e.getMessage(), e2);
            }
        }

        if (collection instanceof FCollectionIntermediate) {
            try {
                collection.close();
            } catch (Exception e) {
                log.trace(e.getMessage(), e);
                try {
                    c.close();
                } catch (Exception e2) {
                    log.trace(e.getMessage(), e2);
                }

                throw new RuntimeException(e);
            }
        }

        return c;
    }

}
