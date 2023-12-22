package io.alex538.fstream.usage;

import io.alex538.fstream.FCollection;
import io.alex538.fstream.fst.FstSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.UUID;

@Slf4j
public class FCollectionWithConfiguration {

    public static void main(String[] args) throws Exception {
        FCollection<UUID> collection =
                FCollection.builder()
                        .serializer(new FstSerializer())
                        .storageLocation("/tmp")
                        .externalSortingMaxItemsInMemory(300)
                        .build()
                ;

        log.debug("preparing data");
        for (int i = 0; i < 1000; i++) {
            collection.add(UUID.randomUUID());
        }
        log.debug("done");

        log.debug("sorting data");
        collection.sort(UUID::compareTo);
        log.debug("done");

        Iterator<UUID> i = collection.iterator();
        int index = 0;

        while (i.hasNext() && index++ <= 20) {
            log.debug("{}", i.next());
        }

        collection.close();
    }

}
