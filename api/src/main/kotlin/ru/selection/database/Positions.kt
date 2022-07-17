package ru.selection.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.selectAll
import ru.selection.database.Employee.Companion.referrersOn

fun Position.mapToModel():PositionModel{
    return PositionModel(
        id = this.id.value,
        name = this.name,
        departmentId = this.departmentId
    )
}

@kotlinx.serialization.Serializable
data class PositionModel(
    val id:Int,
    val name:String,
    val departmentId:Int?
)

@kotlinx.serialization.Serializable
data class PositionModelDTO(
    val id:Int,
    val name:String,
    val employee:List<EmployeeModel>
)

class Position(id: EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<Position>(Positions)

    val name by Positions.name
    val departmentId by Positions.departmentId
}

object Positions : IntIdTable("Department","DepartmentId") {

    val name = varchar("DepartmentName", 64)
    val departmentId = integer("Department_DepartmentId").nullable()

    fun getAll():List<PositionModel> {
        return selectAll()
            .mapNotNull {
                Position[it[id]].mapToModel()
            }
    }

    fun getById(id:Int):PositionModel{
        return Position[id].mapToModel()
    }
}