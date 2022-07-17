package api.model

@kotlinx.serialization.Serializable
data class Department(
    val id:Int,
    val name:String,
    val departmentId:Int?
)
