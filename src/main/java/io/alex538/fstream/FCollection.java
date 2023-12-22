package io.alex538.fstream;

import java.util.function.Consumer;

public interface FCollection<T> {

    void add(T value);

    void forEach(Consumer<T> consumer);

    FModifyingStream<T> stream();

    void destroy();

}
