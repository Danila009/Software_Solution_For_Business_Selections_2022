package ru.selection.database

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

fun Employee.mapToModel():EmployeeModel{
    return EmployeeModel(
        id = this.id.value,
        firstName = this.firstName,
        lastName = this.lastName,
        lastPatronomic = this.lastPatronomic,
        telegramId = this.telegramId,
        phone = this.phone,
        dateBirth = this.dateBirth,
        post = this.post.mapToModel()
    )
}

@kotlinx.serialization.Serializable
data class EmployeeModel(
    val id: Int,
    val firstName:String,
    val lastName:String,
    val lastPatronomic:String,
    val telegramId:Int?,
    val phone:String,
    val dateBirth: LocalDateTime?,
    val post:PostModel
)

class Employee(id: EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<Employee>(Employees)

    val firstName by Employees.workerFirstName
    val lastName by Employees.workerLastName
    val lastPatronomic by Employees.workerLastPatronomic
    val telegramId by Employees.telegramId
    val phone by Employees.phone
    val dateBirth by Employees.dateBirth
    val post by Post referencedOn Employees.post
}

object Employees : IntIdTable("Worker","WorkerId") {

    val workerFirstName = varchar("WorkerFirstName", 512)
    val workerLastName = varchar("WorkerLastName", 512)
    val workerLastPatronomic = varchar("WorkerLastPatronomic", 512)
    val post = reference("Post_PostId", Posts)
    val dateBirth = datetime("WorkerDateofbirth")
    val phone = varchar("WorkerPhone", 12)
    var telegramId = integer("WorkerTelegramId").nullable()

    fun getAll(
        search:String?,
        positionId:Int?,
        postId:Int?
    ):List<EmployeeModel>{
        return selectAll()
            .filter {
                val item = Employee[it[Employees.id]].mapToModel()

                var filler = true

                search?.let {
                    filler = item.firstName.contains(search)
                            || item.lastName.contains(search)
                            || item.lastPatronomic.contains(search)
                }

                positionId?.let {
                    filler = item.post.position.id == positionId
                }

                postId?.let {
                    filler = item.post.id == postId
                }

                filler
            }
            .map { Employee[it[Employees.id]].mapToModel() }
    }

    fun getById(id:Int):EmployeeModel {
        return Employee[id].mapToModel()
    }

    fun checkByFIO(fio:String):Boolean{
        val result = selectAll()
            .filter {
                val item = Employee[it[Employees.id]].mapToModel()

                "${item.firstName} ${item.lastName} ${item.lastPatronomic}" == fio
            }
            .map { Employee[it[Employees.id]].mapToModel() }

        return result.isNotEmpty()
    }

    fun updateTelegramId(
        fio:String,
        telegramId:Int
    ){
        val result = selectAll()
            .filter {
                val item = Employee[it[Employees.id]].mapToModel()

                "${item.firstName} ${item.lastName} ${item.lastPatronomic}" == fio
            }
            .map { Employee[it[id]].mapToModel() }

        if (result.isEmpty())
            return

//        update({ id.eq(result[0].id) }){
//            this.telegramId = telegramId
//        }
    }
}