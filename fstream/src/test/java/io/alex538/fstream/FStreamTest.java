package io.alex538.fstream;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FStreamTest {

    static class FCollectionInMemory<T extends Serializable> implements FCollection<T> {

        public <N extends Serializable> FCollection<N> newEmptyInstance() {
            return new FCollectionInMemory<>();
        }

        List<T> items = new LinkedList<>();

        @Override
        public void add(T value) {
            items.add(value);
        }

        @Override
        public void forEach(Consumer<T> consumer) {
            items.forEach(consumer);
        }

        @Override
        public Iterator<T> iterator() {
            return null;
        }

        @Override
        public FStream<T> stream() {
            return new FStreamWrapper<>(this, (in) -> in, null);
        }

        @Override
        public void close() {

        }

        @Override
        public void sort(Comparator<T> comparator) {

        }
    }

    // TODO
    @Test
    public void test() throws Exception{
        try (FCollectionInMemory<String> collection = new FCollectionInMemory<>()) {

            collection.add("0044");
            collection.add("002");
            collection.add("050");
            collection.add("550");
            collection.add("5050");

            try (FCollection<Integer> result = collection.stream()
                    .filter(in -> in.length() > 3)
                    .map(in -> in.substring(0, 4))
                    .map(Integer::valueOf)
                    .filter(in -> in > 40)
                    .collect()
            ) {

                List<Integer> output = new LinkedList<>();
                result.forEach(output::add);

                assertEquals(List.of(44, 5050), output);
            }
        }
    }

}