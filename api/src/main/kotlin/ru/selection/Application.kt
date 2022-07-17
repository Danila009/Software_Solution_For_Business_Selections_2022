package ru.selection

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.selection.database.DatabaseFactory
import ru.selection.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DatabaseFactory.init()

        configureRouting()

        configureSerialization()
    }.start(wait = true)
}
