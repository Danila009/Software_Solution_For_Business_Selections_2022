package ru.selection.plugins

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.selection.database.Employees
import ru.selection.database.Positions
import ru.selection.database.Posts
import java.io.File

fun Application.configureRouting() {

    routing {
        get("/employees") {
            newSuspendedTransaction {
                val search = call.request.queryParameters["search"]
                val positionId = call.request.queryParameters["positionId"]?.toIntOrNull()
                val postId = call.request.queryParameters["postId"]?.toIntOrNull()
                val response = Employees.getAll(
                    search, positionId, postId
                )
                call.respond(response)
            }
        }

        get("/employee/{id}") {
            newSuspendedTransaction {
                val id = call.parameters["id"]!!.toInt()
                val response = Employees.getById(id)
                call.respond(response)
            }
        }

        get("/post") {
            newSuspendedTransaction {
                val response = Posts.getAll()
                call.respond(response)
            }
        }

        get("/post/{id}") {
            newSuspendedTransaction {
                val id = call.parameters["id"]!!.toInt()
                val response = Posts.getById(id)
                call.respond(response)
            }
        }

        get("/positions") {
            newSuspendedTransaction {
                val response = Positions.getAll()
                call.respond(response)
            }
        }

        get("/positions/{id}") {
            newSuspendedTransaction {
                val id = call.parameters["id"]!!.toInt()
                val response = Positions.getById(id)
                call.respond(response)
            }
        }

        get("/photo") {
            newSuspendedTransaction {
                val fio = call.request.queryParameters["fio"]
                val file = File("file/$fio.jpg")
                call.respondFile(file)
            }
        }

        get("/employee/check") {
            newSuspendedTransaction {
                val fio = call.request.queryParameters["fio"] ?: ""
                val response = Employees.checkByFIO(fio)
                call.respond(response)
            }
        }
    }
}
