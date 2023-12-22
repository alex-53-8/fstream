# FStream

## Intro
FStream is `Java Collections`-like library that stores serialized instances of classes in a file storage, 
and provides methods for performing sequential aggregations on elements of a collection. 

The library consists of three important components:
- *FCollection* manages adding and iterating elements of a collection in a file storage;
- *FSerializer* which serializes an instance of a Java class into a byte array and back, 
- *FStream* provides a set of methods for performing sequential aggregations on elements stored in a collection. 

`FCollection` is similar to [JDK's List](https://docs.oracle.com/javase/8/docs/api/java/util/List.html) and `FStream` is very similar to [JDK's Stream](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html), both with reduced number of methods.

# Example

This is how to start using the library.

```java
FCollection<SomeClassName> collection = FCollection.create();

// add elements to a collection
collection.add(instance);

// iterate elements of a collection
Iterator<SomeClassName> i = collection.iterator();
while (i.hasNext()) {
    consumer.accept(i.next());
}

// also iterates over all elements in a collection
collection.forEach(this::consumer);

// create a new collection
FCollection<AnotherClassName> collection2 = 
    collection.stream()
      .filter(o -> o.isActive() == true)
      .map(this::convert)
      .sort((o1, o2) -> o1.compareTo(o2))
      .collect();

// destroy collections' data in a file storage
collection.close();
collection2.close();
```

## Use-cases
The library was written for storing large objects off of RAM in a file storage to leave RAM free and operate over 
the objects via `FSteam` interface by reading objects from a file storage each time it is needed.

### For instance
A thread accumulates objects in RAM, there are a lot of objects, each object holds much data, and allocated memory for a process is going to exhaust. 
Other threads are doing similar activity. Definitely allocated memory is not enough for all threads to hold data, 
but data has to be retained until a thread finishes work, and saves all the objects in a persistent storage, for example a RDBMS.

To reduce memory usage, data can be stored temporarily in a file system. Use `FCollection` to store elements in a file storage,
`FCollection` enables a developer to operate over stored objects in a file system in a way similar to `Java Stream`.

# Description of modules
## fstream
Implementation of FStream library, the module contains all interfaces and classes related to the library.

```xml
<dependency>
    <groupId>io.alex538</groupId>
    <artifactId>fstream</artifactId>
    <version>ACTUAL_VERSION</version>
</dependency>
```

## fstream-fst-serializer
Implementation of a custom serializer based on [fast-serialization library](https://github.com/RuedigerMoeller/fast-serialization).
Add following maven dependency to start using the module in your project. Keep in mind that `fstream` and `fstream-fst-serializer`
should be the same version. 

```xml
<dependency>
    <groupId>io.alex538</groupId>
    <artifactId>fstream</artifactId>
    <version>ACTUAL_VERSION</version>
</dependency>
<dependency>
    <groupId>io.alex538</groupId>
    <artifactId>fstream-fst-serializer</artifactId>
    <version>ACTUAL_VERSION</version>
</dependency>
```

Instantiation
```java
FCollection<SomeClass> synonyms = FCollection.builder().serializer(new FstSerializer()).build(); 
```

## fstream-usage
The module contains few examples of usage of the library `fstream` - sample code, configuration, just get a vision
how the library can be used in a project.

# Requirements
- Java 11 or higher
- Maven

# Installation
All releases of the project can be found on releases page https://github.com/alex-53-8/fstream/releases and artifacts
are uploaded to GitHub packages. By configuring maven repositories to use GitHub packages of the project enables you
to start using the library.

