package io.alex538.fstream;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
class FFileCollectionTest {

    @Test
    public void testSimpleMapping() throws Exception {
        try (FCollection<String> versions = FCollection.create()) {
            versions.add("1");
            versions.add("2");
            versions.add("3");
            versions.add("4");
            versions.add("5");
            versions.add("6");

            try (FCollection<Integer> result = versions.stream()
                    .map(Integer::parseInt)
                    .filter(v -> v > 2)
                    .collect()
            ) {
                List<Integer> versionsList = new LinkedList<>();
                result.forEach(versionsList::add);

                assertEquals(List.of(3, 4, 5, 6), versionsList);
            }
        }
    }

    @Test
    public void testFlatMap() throws Exception {
        try (FCollection<Derived> items = FCollection.create()) {

            items.add(new Derived("1", List.of("one", "first")));
            items.add(new Derived("2", List.of("two", "second")));
            items.add(new Derived("3", List.of("three", "third")));

            try (FCollection<String> listOfSynonyms =
                         items.stream().flatMap(in -> in.getSynonyms().stream()).collect()
            ) {
                List<String> result = new LinkedList<>();
                listOfSynonyms.forEach(result::add);

                assertEquals(List.of("one", "first", "two", "second", "three", "third"), result);
            }
        }
    }

    @Test
    public void testMap() throws Exception {
        try (FCollection<Base> items = FCollection.create()) {

            items.add(new Base("1"));
            items.add(new Derived("2", List.of("two", "second")));
            items.add(new Base("3"));

            try (FCollection<String> listOfWords =
                         items.stream().map(Base::getWord).collect()
            ) {
                List<String> result = new LinkedList<>();
                listOfWords.forEach(result::add);

                assertEquals(List.of("1", "2", "3"), result);
            }
        }
    }

    @Test
    public void testSort() throws Exception {
        try (FCollection<Base> synonyms = FCollection.create()) {
            int max = 10;
            int min = 1;

            log.debug("preparing data...");
            for (int i = min; i <= max; i++) {
                synonyms.add(new Base(String.format("%03d", i)));
            }

            log.debug("preparing data done.");
            try (FCollection<String> listOfWords =
                         synonyms
                                 .stream()
                                 .map(Base::getWord)
                                 .sort((s1, s2) -> s1.compareTo(s2) > 0 ? -1 : s1.equals(s2) ? 0 : 1)
                                 .collect()
            ) {
                List<String> result = new LinkedList<>();
                listOfWords.forEach(result::add);

                List<String> expectedResult = new LinkedList<>();
                for (int i = max; i >= min; i--) {
                    expectedResult.add(String.format("%03d", i));
                }

                assertEquals(expectedResult, result);
            }
        }
    }

    static class Base implements Serializable {
        final String word;
        public Base(String word) {
            this.word = word;
        }
        public String getWord() {
            return word;
        }
    }

    static class Derived extends Base {
        final List<String> synonyms;

        public Derived(String word, List<String> synonyms) {
            super(word);
            this.synonyms = synonyms;
        }
        public List<String> getSynonyms() {
            return synonyms;
        }
    }

}