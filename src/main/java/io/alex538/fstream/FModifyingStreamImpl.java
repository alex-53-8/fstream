package io.alex538.fstream;

import java.util.function.Function;
import java.util.function.Predicate;

public class FModifyingStreamImpl<I> implements FModifyingStream<I> {

    public FModifyingStreamImpl(FModifyingStreamImpl<I> parent) {

    }

    @Override
    public FModifyingStream<I> filter(Predicate<I> filter) {
        return this;
    }

    @Override
    public <O> FModifyingStream<O> map(Function<I, ? extends O> mapper) {
        return null;
    }

    @Override
    public FCollection<I> collect() {
        FileCollection<I> fc = new FileCollection<>();
        return fc;
    }
}
