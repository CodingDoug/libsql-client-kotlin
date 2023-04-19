package org.libsql.app

import kotlinx.coroutines.runBlocking
import org.libsql.client.Client

fun main() {
    val envUrl = System.getenv("LIBSQL_DB_URL")
    val envAuthToken = System.getenv("LIBSQL_AUTH_TOKEN")

    val client = Client.build {
        url = envUrl
        authToken = envAuthToken
    }

//    val client = Client.BuilderJ()
//        .url("https://asdf")
//        .authToken("adf")
//        .build()

    client.use { client ->
        runBlocking {
            val rss = client.batch(arrayOf("select * from users"))
            println(rss)
        }
    }
    println("Done")
}
