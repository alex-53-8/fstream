# FStream

File storage for a computation of a large dataset backed with a file system

- create a temporary storage in a file
- add initial data
- with using of a stream it is possible to iterate, filter, map, and then write back to a new file
- finally destroy a storage

## Initialize


## Pipeline

Filter -> Map -> Filter -> Collect

V -> V -> T -> M -> R