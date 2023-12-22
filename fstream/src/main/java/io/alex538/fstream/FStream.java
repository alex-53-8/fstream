package io.alex538.fstream;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Methods for operating of elements stored in a file collection
 * All methods are similar to <code>java.util.stream.Stream</code>
 * */
public interface FStream<Out extends Serializable> {

    /**
     * Returns a stream of elements that match provided predicate
     * @param predicate stateless predicate which is applied to each element in a collection
     * @return a new FStream
     * */
    FStream<Out> filter(Predicate<Out> predicate);

    /**
     * Returns a stream of elements consisting of the results of applying provided function
     * @param mapper a stateless function which is applied to each element of the collection
     * @return a new FStream
     * */
    <NewOut extends Serializable> FStream<NewOut> map(Function<Out, NewOut> mapper);

    /**
     * Returns a new stream consisting of results of replacing each element of this stream with
     * the content of a mapped stream produced by applying the provided mapping function to each element.
     * @param mapper a stateless function which is applied to each element of the collection
     * @return a new FStream
     * */
    <NewOut extends Serializable> FStream<NewOut> flatMap(Function<Out, ? extends Stream<NewOut>> mapper);

    /**
     * Return a stream consisting of elements of this stream, sorted by a provided comparator
     * @param comparator a stateless comparator to be used for comparing elements of this stream
     * @return a new FStream
     * */
    FStream<Out> sort(Comparator<Out> comparator);

    /**
     * Performs forming of a new collection of elements of this stream after applying selected methods
     * on each element on this collection.
     * */
    FCollection<Out> collect();

}
