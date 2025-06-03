package com.example.prezzapp.model

data class Teacher (override val name: String, override val id : Int, override val status : Status, override val password : String, val tp: String, val td : String, val year : String, val spe : String )
    : User(password, status, id, name) {


    fun getNextClass() {
        // Logic to get next class information
    }

    fun getAbscenceByPromo(year : String, spe: String) {
        // Logic to get absence information by promotion
    }

    fun getAbscenceByDate(date: String) {
        // Logic to get absence information by date
    }

    fun getAbscenceByClass(type: String) {
        // Logic to get absence information by type
    }

    fun getAbscenceByModule(module: String) {
        // Logic to get absence information by module
    }
}