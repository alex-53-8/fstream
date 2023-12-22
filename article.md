# Storing a Java collection in a file storage

If you deal with a large datasets stored in a Java collection, lets say java.util.List, sooner or later you would encounter with a situation that allocated memory is not enough to hold all your data. It may be even small number of elements in a collection, but many of them take much of memory. It happens that a process must accumulate large dataset, and there is no way to process data in chunks to fit allocated memory. There are multiple ways to resolve an issue, from storing data in a file to temporarily store data in Redis.

We encountered with similar issue in a system which is developed for many years, and it is not so simple just to change way how data is accumulated in memory and utilized. A Java collection is full of large objects and there are multiple threads in the same app for that specific activity. Of course,  there are few instances of a service which processes data, sometimes a dataset is small, sometimes a dataset is large and that is really frustrating, it is not simple to choose a scaling model. In such cases I usually say - simpler, faster, and more reliable to rewrite rather than "fine-tune", but this time we do fine-tuning :) Let's leave behind a curtain why a system operates with data which does not fit allocated memory 🤠 it happens. Sometimes we can change an implementation in more robust way, but sometimes we need to find a compromise with existing solution.

We implemented a small library: a Java collection data is stored in a file system instead of RAM and a convenient, Java Stream like interface for operating on data is provided.

Okay, the library - `FStream`. A central class `FCollection` which is similar to `java.util.List` but with reduced number of methods. With `FCollection` you can add new items, sort them with a comparator, iterate elements, and create a instance of `FStream` which is also reduced version of `Java Stream`. With `FStream` you can apply sequential operations on elements of a collection.

# Example

See, in following code snapshot all data is stored in a file located in a temporary directory. Data is written to a file system immediately as data is added. But it it possible to operate on the items over `FStream`.

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
FCollection<AnotherClassName> collection2 = collection.stream()
        .filter(o -> o.isActive() == true)
        .map(this::convert)
        .sort((o1, o2) -> o1.compareTo(o2))
        .collect();

// destroy collections' data in a file storage
collection.close();
collection2.close();
```

# How it works

### Create a collection

When a collection is created, for instance with a method `create`, then a new file is created in a `/tmp` directory or in a custom directory if specified.

```java
FCollection<SomeClassName> collection = FCollection.create();
```

### Add items in a collection

Adding operation of a new item to a collection consists of an item serialization and writing to a collection's file in a file storage. Serialization is done by default with a `FJdkSerializer`, but it is possible to use a custom serializer. Customization is described below.

```java
// add elements to a collection
collection.add(instance);
```

### Apply operations on a collection

An approach here is absolutely the same with Java Stream - a developer can specify operations takes on each element of a collection in a function way. As result, a new collection is created, stored in a file storage.

```java
FCollection<AnotherClassName> collection2 = collection.stream()
        .filter(o -> o.isActive() == true)
        .map(this::convert)
        .sort((o1, o2) -> o1.compareTo(o2))
        .collect();
```

# Customization

So far it is possible to specify where to store temporary data of a collections, and assign a custom serializer for a collection. A serializer must implement `FSerializer` interface. After that a collection can be created with a builder.

```java
FCollection<String> c = FCollection.builder()
        .serializer(new CustomSerializer())
        .storageLocation("/your/location")
        .build();
```

# Want to try it out?

Visit project's GitHub repository: https://github.com/alex-53-8/fstream