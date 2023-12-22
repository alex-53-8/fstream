package io.alex538.fstream;

import java.io.Serializable;
import java.util.*;

final class FFileStorageSorter {

    private FFileStorageSorter(){}
    
    static <T extends Serializable> FFileStorage<T> sort(Comparator<T> comparator, FFileStorage<T> storage, int externalSortingMaxItemsInMemory) {
        LinkedList<FFileStorage<T>> chunks = new LinkedList<>();
        Iterator<T> i = storage.iterator();
        List<T> sortingBlock = new LinkedList<>();
        int items = 0;

        while (i.hasNext()) {
            T value = i.next();
            sortingBlock.add(value);
            items++;

            if (items >= externalSortingMaxItemsInMemory) {
                items = 0;
                FFileStorage<T> ffs = new FFileStorage<>(storage.getSerializer(), storage.getStoragePath(), UUID.randomUUID().toString());
                chunks.add(ffs);

                sortingBlock.sort(comparator);
                sortingBlock.forEach(ffs::write);
                sortingBlock = new LinkedList<>();
            }
        }

        FFileStorage<T> ffs = new FFileStorage<>(storage.getSerializer(), storage.getStoragePath(), UUID.randomUUID().toString());
        chunks.add(ffs);

        sortingBlock.sort(comparator);
        sortingBlock.forEach(ffs::write);
        sortingBlock.clear();

        if (chunks.size() % 2 != 0) {
            chunks.add(new FFileStorage<>(storage.getSerializer(), storage.getStoragePath(), UUID.randomUUID().toString()));
        }

        while (chunks.size() > 1) {
            try (FFileStorage<T> s1 = chunks.pollFirst(); FFileStorage<T> s2 = chunks.pollFirst()) {
                FFileStorage<T> merged = new FFileStorage<>(storage.getSerializer(), storage.getStoragePath(), UUID.randomUUID().toString());
                merge(comparator, merged, s1, s2);
                chunks.addLast(merged);
            }
        }

        if (chunks.isEmpty()) {
            return new FFileStorage<>(storage.getSerializer(), storage.getStoragePath(), UUID.randomUUID().toString());
        } else if (chunks.size() == 1) {
            return chunks.pollFirst();
        } else {
            throw new RuntimeException("Number of sorted chunks is more than one");
        }
    }

    static <T extends Serializable> void merge(
            Comparator<T> comparator, FFileStorage<T> merged, FFileStorage<T> s1, FFileStorage<T> s2
    ) {
        Iterator<T> i1 = s1.iterator();
        Iterator<T> i2 = s2.iterator();
        boolean hasV1 = false;
        boolean hasV2 = false;
        T v1 = null;
        T v2 = null;

        while (i1.hasNext() && i2.hasNext()) {
            if (!hasV1) {
                v1 = i1.next();
                hasV1 = true;
            }

            if (!hasV2) {
                v2 = i2.next();
                hasV2 = true;
            }

            int comparisonResult = comparator.compare(v1, v2);

            if (comparisonResult == 0) {
                merged.write(v1);
                merged.write(v2);
                hasV1 = hasV2 = false;
            } else if (comparisonResult < 0) {
                merged.write(v1);
                hasV1 = false;
            } else {
                merged.write(v2);
                hasV2 = false;
            }
        }

        if (hasV1) {
            merged.write(v1);
        }

        if (hasV2) {
            merged.write(v2);
        }

        writeRemainItems(i1, merged);
        writeRemainItems(i2, merged);
    }

    private static <T extends Serializable> void writeRemainItems(Iterator<T> i, FFileStorage<T> destination) {
        while (i.hasNext()) {
            destination.write(i.next());
        }
    }

}
