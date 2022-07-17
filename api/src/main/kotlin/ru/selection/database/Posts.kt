package ru.selection.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.selectAll

fun Post.mapToModel():PostModel {
    return PostModel(
        id = this.id.value,
        name = this.name,
        position = position.mapToModel()
    )
}

@kotlinx.serialization.Serializable
data class PostModel(
    val id:Int,
    val name:String,
    val position:PositionModel
)

class Post(id: EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<Post>(Posts)

    val name by Posts.name
    val position by Position referencedOn Posts.position
}

object Posts : IntIdTable("Post","PostId") {

    val name = varchar("PostName", 32)
    val position = reference("Department_DepartmentId", Positions)

    fun getAll():List<PostModel>{
        return selectAll()
            .mapNotNull { Post[it[id]].mapToModel() }
    }

    fun getById(id:Int):PostModel{
        return Post[id].mapToModel()
    }
}