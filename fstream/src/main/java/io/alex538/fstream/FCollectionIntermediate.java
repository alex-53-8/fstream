package io.alex538.fstream;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;

class FCollectionIntermediate<T extends Serializable> implements FCollection<T> {

    private final FCollection<T> original;

    private final FCollection.Configuration cfg;

    FCollectionIntermediate(FCollection<T> original, FCollection.Configuration cfg) {
        this.original = original;
        this.cfg = cfg;
    }

    @Override
    public <N extends Serializable> FCollection<N> newEmptyInstance() {
        return original.newEmptyInstance();
    }

    @Override
    public void add(T value) {
        original.add(value);
    }

    @Override
    public void forEach(Consumer<T> consumer) {
        original.forEach(consumer);
    }

    @Override
    public Iterator<T> iterator() {
        return original.iterator();
    }

    @Override
    public FStream<T> stream() {
        return new FStreamWrapper<>(this, (in)->in, cfg);
    }

    public void close() throws Exception {
        original.close();
    }

    @Override
    public void sort(Comparator<T> comparator) {
        original.sort(comparator);
    }

}
