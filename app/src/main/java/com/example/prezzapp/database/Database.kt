package com.example.prezzapp.database


class Database private constructor() {
    private val host: String = "10.74.252.198"
    private val port: Int = 22
    private val username: String = "groupe1"
    private val password: String = "Euler314"
    private var path = "/home/"


    fun getPath(): String {
        return path
    }

    fun setPath(newPath: String) {
        path = newPath
    }

    companion object {
        private var instance: Database? = null

        /**
         * Singleton pattern to ensure only one instance of Database exists
         */
        fun getInstance(): Database {
            if (instance == null) {
                instance = Database()
            }
            return instance as Database
        }
    }

    fun fileExists(fileName: String): Boolean {

        return true
    }
}