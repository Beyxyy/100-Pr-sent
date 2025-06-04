package com.example.prezzapp.model
enum class Status {
    Student, Teacher, Admin
}

open class User( open val password: String, open  val status : Status, open val id: Int, open  val name: String) {

    override fun toString(): String {
        return "User(name='$name', id=$id, status=$status, password='$password')"
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (password != other.password) return false
        if (status != other.status) return false
        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }
    override fun hashCode(): Int {
        var result = password.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + id
        result = 31 * result + name.hashCode()
        return result
    }
}
