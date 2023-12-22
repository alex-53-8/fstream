package io.alex538.fstream.fst;

import io.alex538.fstream.FCollection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
class FFileCollectionTest {

    @Test
    @Disabled
    public void testSort_whenAmountOfDataExceedSortingBlockSize_thenMultipleBlocksAreUsedForSorting() throws Exception {
        try (FCollection<Item> synonyms = FCollection.builder().serializer(new FstSerializer()).build()) {
            int min = 1;
            int max = 10500;

            log.debug("preparing data...");
            for (int i = min; i <= max; i++) {
                synonyms.add(new Item(String.format("%05d", i)));
            }

            log.debug("preparing data done.");
            try (FCollection<String> listOfWords =
                         synonyms
                                 .stream()
                                 .map(Item::getWord)
                                 .sort((s1, s2) -> s1.compareTo(s2) > 0 ? -1 : s1.equals(s2) ? 0 : 1)
                                 .collect()
            ) {
                List<String> result = new LinkedList<>();
                listOfWords.forEach(result::add);

                List<String> expectedResult = new LinkedList<>();

                for (int i = max; i >= min; i--) {
                    expectedResult.add(String.format("%05d", i));
                }

                assertEquals(expectedResult, result);
            }
        }
    }

    static class Item implements Serializable {
        final String word;
        public Item(String word) {
            this.word = word;
        }
        public String getWord() {
            return word;
        }
    }

}