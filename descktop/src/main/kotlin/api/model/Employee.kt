package api.model

@kotlinx.serialization.Serializable
data class Employee(
    val id:Int,
    val firstName:String,
    val lastName:String,
    val lastPatronomic:String,
    val telegramId:Int?,
    val phone:String,
    val dateBirth:String,
    val post:Post
)

@kotlinx.serialization.Serializable
data class Post(
    val id:Int,
    val name:String,
    val position:Department
)