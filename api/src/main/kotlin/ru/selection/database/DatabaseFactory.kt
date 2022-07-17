package ru.selection.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(){
        val hostname = "cfif31.ru"
        val databaseName = "ISPr24-39_BeluakovDS_Selection_2022"
        val username = "ISPr24-39_BeluakovDS"
        val password = "ISPr24-39_BeluakovDS"
        val database = Database.connect(
            "jdbc:mysql://$hostname:3306/$databaseName?serverTimezone=UTC&useSSL=false",
            password = password,
            user = username
        )

        transaction(database) {

        }
    }
}