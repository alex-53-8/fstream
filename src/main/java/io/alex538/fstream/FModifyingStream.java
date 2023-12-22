package io.alex538.fstream;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public interface FModifyingStream<I> {

    FModifyingStream<I> filter(Predicate<I> filter);

    <O> FModifyingStream<O> map(Function<I, ? extends O> mapper);

//    <N> FModifyingStream<N> flatMap(Function<T, Collection<N>> mapper);

    FCollection<I> collect();

}
