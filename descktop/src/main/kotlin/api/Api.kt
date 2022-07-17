package api

import api.model.Department
import api.model.Employee
import api.model.Post
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

    suspend fun getPost():List<Post>{
        return client.get("http://localhost:8080/post")
            .body()
    }

    suspend fun getDepartment():List<Department> {
        return client.get("http://0.0.0.0:8080/positions").body()
    }

    suspend fun getDepartmentById(id:Int):Department {
        return client.get("http://0.0.0.0:8080/positions/$id").body()
    }
}