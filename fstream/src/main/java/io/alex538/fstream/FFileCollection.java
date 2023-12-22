package io.alex538.fstream;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

class FFileCollection<T extends Serializable> implements FCollection<T> {

    private FFileStorage<T> storage;

    private FCollection.Configuration cfg;

    FFileCollection(FSerializer serializer, File storagePath, FCollection.Configuration cfg) {
        this.storage = new FFileStorage<>(serializer, storagePath, UUID.randomUUID().toString());
        this.cfg = cfg;
    }

    public <N extends Serializable> FCollection<N> newEmptyInstance() {
        return new FFileCollection<>(this.storage.getSerializer(), this.storage.getStoragePath(), cfg);
    }

    @Override
    public void add(T value) {
        Objects.requireNonNull(value, "Value cannot be null");
        this.storage.write(value);
    }

    @Override
    public void forEach(Consumer<T> consumer) {
        Objects.requireNonNull(consumer, "Consumer cannot be null");
        storage.forEach(consumer);
    }

    @Override
    public Iterator<T> iterator() {
        return storage.iterator();
    }

    @Override
    public FStream<T> stream() {
        return new FStreamWrapper<>(this, (in)->in, cfg);
    }

    @Override
    public void close() {
        this.storage.close();
    }

    @Override
    public void sort(Comparator<T> comparator) {
        FFileStorage<T> tmp = this.storage;
        this.storage = FFileStorageSorter.sort(comparator, this.storage, cfg.getExternalSortingMaxItemsInMemory());
        tmp.close();
    }

}
