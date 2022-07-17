package api

import api.model.Employee
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class Api {

    private val client = HttpClient(Apache){
        install(ContentNegotiation){
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }
            )
        }
    }

    suspend fun getEmployeesCheckById(fio:String):Boolean {
        return client.get("http://localhost:8080/employee/check"){
            parameter("fio",fio)
        }.body()
    }

    suspend fun getAllEmployees(
        search:String = "",
        positionId:Int? = null,
        postId:Int? = null
    ):List<Employee>{
        return client.get("http://localhost:8080/employees"){
            parameter("search", search.ifEmpty { null })
            positionId?.let {
                parameter("positionId", positionId)
                parameter("postId", postId)
            }
        }.body()
    }
}