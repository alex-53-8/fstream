package io.alex538.fstream;

import java.util.function.Consumer;

class FileCollection<T> implements FCollection<T> {
    @Override
    public void add(T value) {

    }

    @Override
    public void forEach(Consumer<T> consumer) {

    }

    @Override
    public FModifyingStream<T> stream() {
        return null;
    }

    @Override
    public void destroy() {

    }

}
