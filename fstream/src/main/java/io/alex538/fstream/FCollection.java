package io.alex538.fstream;

import lombok.Value;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * */
public interface FCollection<T extends Serializable> extends AutoCloseable {

    /**
     * Creates a new empty instance in the same location with the same type
     * @return FCollection
     * */
    <N extends Serializable> FCollection<N> newEmptyInstance();

    /**
     * Adds a new item
     * @param value not null
     * */
    void add(T value);

    /**
     * Sorts entire collection by a provided comparator
     * @param comparator not null
     * */
    void sort(Comparator<T> comparator);

    /**
     * Iterates over all items stored in a collection. Each item is sent to the specified consumer
     * */
    void forEach(Consumer<T> consumer);

    /**
     * Returns an iterator over elements in a collection
     * @return an iterator over elements in a collection
     * */
    Iterator<T> iterator();

    /**
     * Returns a sequential FStream with this collection as its source.
     * @return a sequential FStream with this collection as its source.
     * */
    FStream<T> stream();

    /**
     * Destroys a collection's data in a file storage
     * */
    void close() throws Exception;

    /***
     * Create a new <code>FCollection</code> with desired configuration
     * @return FCollection.Builder
     * @see FCollection.Builder
     */
    static FCollection.Builder builder() {
        return new Builder();
    }

    /***
     * Create a new <code>FCollection</code> instance with default configuration
     * @return FCollection
     */
    static <T extends Serializable> FCollection<T> create() {
        return new Builder().build();
    }

    /**
     * A builder for <code>FCollection</code>
     * */
    class Builder {

        private static final String DEFAULT_STORAGE_PATH = System.getProperty("java.io.tmpdir");

        private static final FSerializer DEFAULT_SERIALIZER = new FJdkSerializer();

        private static final int DEFAULT_EXTERNAL_SORTING_MAX_ITEMS_IN_MEMORY = 1000;

        private String storagePath = DEFAULT_STORAGE_PATH;

        private FSerializer serializer = DEFAULT_SERIALIZER;

        private int externalSortingMaxItemsInMemory = DEFAULT_EXTERNAL_SORTING_MAX_ITEMS_IN_MEMORY;

        /**
         * Specify a folder where a storage will be created, if not specified then <code>/tmp</code> folder is used
         * */
        public Builder storageLocation(String path) {
            Objects.requireNonNull(path, "Storage path cannot be null");
            this.storagePath = path;
            return this;
        }

        /**
         * It is possible to specify a custom serializer, which is instance of <code>{@link FSerializer}</code>
         * if not set then <code>{@link FJdkSerializer}</code> is used
         * */
        public Builder serializer(FSerializer serializer) {
            Objects.requireNonNull(serializer, "Serializer path cannot be null");
            this.serializer = serializer;
            return this;
        }

        /**
         * Specify how many items to keep in memory during sorting.
         * Small number: more time for sorting, less RAM needed.
         * Large number: less time for sorting, more RAM needed.
         * */
        public Builder externalSortingMaxItemsInMemory(int externalSortingMaxItemsInMemory) {
            if (externalSortingMaxItemsInMemory < 2) {
                throw new IllegalArgumentException("number of elements for sorting must not be less than two");
            }
            this.externalSortingMaxItemsInMemory = externalSortingMaxItemsInMemory;
            return this;
        }

        public <T extends Serializable> FCollection<T> build() {
            return new FFileCollection<>(
                    serializer,
                    new File(storagePath),
                    new Configuration(externalSortingMaxItemsInMemory)
            );
        }
    }

    @Value
    class Configuration {
        int externalSortingMaxItemsInMemory;
    }

}
