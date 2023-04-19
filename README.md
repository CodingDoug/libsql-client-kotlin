# Kotlin libSQL client

Giant WIP!

- Kotlin-first API with coroutines
- Java-also with blocking API
  - Could be extended further to support Futures
- Uses ktor for HTTP requests (also supports websockets, TBD)
- Uses kotlin serialization for JSON
- Uses gradle for build (with fully kotlin syntax)
- Multi-module build
  - client module is a maven-publishable library
    - Maven group=libsql.org and artifact=kotlin-client
  - app module exercises the client in both kotlin and java
