package org.libsql.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.io.Closeable
import java.net.URI

class Client : Closeable {

    private data class Config(
        val url: String,
        val authToken: String?,
    )

    private val config: Config
    private val httpClient: HttpClient

    private constructor(config: Config) {
        this.config = config
        httpClient = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    // This builder is fluent for Kotlin, but does not provide chainable methods for Java
    class Builder {
        var url: String? = null
        var authToken: String? = null

        fun build(): Client {
            val url = this.url ?: throw Exception("url not provided")
            val authToken = this.authToken

            val uri = URI(url)
            if (uri.scheme == "libsql") {
                throw Exception("libsql URLs are not yet supported")
            }

            return Client(Config(url = url, authToken = authToken))
        }
    }

    // This builder is java-friendly, but not fluent for Kotlin
    class BuilderJ {
        private var _url: String? = null
        private var _authToken: String? = null

        fun url(url: String): BuilderJ {
            this._url = url
            return this
        }

        fun authToken(authToken: String): BuilderJ {
            this._authToken = authToken
            return this
        }

        fun build(): Client {
            return Client.build {
                url = _url
                authToken = _authToken
            }
        }
    }

    fun batchBlocking(stmts: Array<String>): List<ResultSet> {
        return runBlocking {
            batch(stmts)
        }
    }

    suspend fun batch(stmts: Array<String>): List<ResultSet> {
        val response = httpClient.post(config.url) {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.Authorization, if (config.authToken != null) "Bearer ${config.authToken}" else null)
            contentType(ContentType.Application.Json)
            setBody(BatchRequest(stmts))
        }

        val results = response.body<Array<RawResultSet>>()
        return results.map { rrs ->
            ResultSet(
                columns = rrs.results.columns.toList(),
                rows = rrs.results.rows.map { row ->
                    row.map {
                        it.booleanOrNull
                            ?: it.longOrNull
                            ?: it.doubleOrNull
                            ?: it.contentOrNull
                    }
                }
            )
        }
    }

    override fun close() {
        httpClient.close()
    }

    @Serializable
    private class BatchRequest(
        val statements: Array<String>
    )

    @Serializable
    private class RawResultSet(
        val results: RawResults
    )

    @Serializable
    private class RawResults {
        val columns: Array<String> = arrayOf()
        val rows: Array<Array<JsonPrimitive>> = arrayOf()
    }
}

data class ResultSet(
    val columns: List<String>,
    val rows: List<List<Any?>>,
)
