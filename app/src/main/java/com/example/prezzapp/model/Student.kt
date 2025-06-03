package com.example.prezzapp.model

data class Student (override val name: String, override val id : Int, override val status : Status, override val password : String, val tp: String, val td : String, val year : String, val spe : String )
    : User(password, status, id, name)
{
    fun getAbsences() {
        // Logic to get absence information
    }

    fun getAsbencesByDate(date: String) {
        // Logic to get absence information by date
    }

    fun getAbsencesPeriode(dateStart: String, dateEnd: String) {
        // Logic to get absence information by type
    }

}
