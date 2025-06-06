package com.example.prezzapp.database

import com.jcraft.jsch.*

class Database private constructor() {
    private val host: String = "10.74.252.198"
    private val port: Int = 22
    private val username: String = "groupe1"
    private val password: String = "Euler314"
    private var path = "/home/"

    private var session: Session? = null
    private var channel: Channel? = null
    private var channelExec: ChannelExec? = null
    private var channelSftp: ChannelSftp? = null
    private var sftp: SftpATTRS? = null
    private var isConnected: Boolean = false

/**
     * Connects to the database using JSch library
     */
    fun connect() {
        if (!isConnected) {
            try {
                val jsch = JSch()
                session = jsch.getSession(username, host, port)
                session?.setPassword(password)
                session?.setConfig("StrictHostKeyChecking", "no")
                session?.connect()

                channel = session?.openChannel("sftp")
                channel?.connect()
                channelSftp = channel as ChannelSftp
                isConnected = true
            } catch (e: JSchException) {
                e.printStackTrace()
            }
        }
    }
    /**
     * Disconnects from the database
     */
    fun disconnect() {
        if (isConnected) {
            try {
                channelSftp?.disconnect()
                channel?.disconnect()
                session?.disconnect()
                isConnected = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    /**
     * Executes a command on the remote server
     */
    fun executeCommand(command: String): String {
        if (!isConnected) {
            connect()
        }
        return try {
            channelExec = session?.openChannel("exec") as ChannelExec
            channelExec?.setCommand(command)
            channelExec?.inputStream = null
            channelExec?.outputStream = System.out
            channelExec?.connect()
            val output = StringBuilder()
            val inputStream = channelExec?.inputStream
            inputStream?.bufferedReader()?.use { reader ->
                reader.forEachLine { line ->
                    output.append(line).append("\n")
                }
            }
            output.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            channelExec?.disconnect()
        }
    }

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