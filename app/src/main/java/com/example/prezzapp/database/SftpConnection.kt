package com.example.prezzapp.database

import com.jcraft.jsch.*
import java.io.File
import java.io.FileOutputStream

class SftpConnection {

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
     * Singleton pattern to ensure only one instance of SftpConnection is created
     */
    companion object {
        @Volatile
        private var instance: SftpConnection? = null

        fun getInstance(): SftpConnection {
            return instance ?: synchronized(this) {
                instance ?: SftpConnection().also { instance = it }
            }
        }
    }
        /**
         * Teste la connexion SSH
         */
        fun testSSHConnection(onResult: (Boolean, String) -> Unit) {
            Thread {
                try {
                    val jsch = JSch()
                    val session = jsch.getSession(username, host, port)
                    session.setPassword(password)
                    val config = java.util.Properties()
                    config["StrictHostKeyChecking"] = "no"
                    session.setConfig(config)
                    session.connect(5000)
                    session.disconnect()
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(true, "Connexion réussie !")
                    }
                } catch (e: Exception) {
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(false, "Erreur de connexion : ${e.message}")
                    }
                }
            }.start()
        }

        /**
         * Télécharge un fichier via SFTP
         */
        fun downloadFileViaSFTP(
            context: android.content.Context,
            localFileName : String,
            remoteFilePath : String,
            onResult: (Boolean, String) -> Unit
        ) {
            Thread {
                try {
                    val jsch = JSch()
                    val session = jsch.getSession(username, host, port)
                    session.setPassword(password)
                    val config = java.util.Properties()
                    config["StrictHostKeyChecking"] = "no"
                    session.setConfig(config)
                    session.connect(5000)
                    val channel = session.openChannel("sftp") as ChannelSftp
                    channel.connect()
                    val localFile = File(context.filesDir, localFileName)
                    channel.get(remoteFilePath, FileOutputStream(localFile))
                    channel.disconnect()
                    session.disconnect()
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(true, "Fichier téléchargé dans : ${localFile.absolutePath}")
                    }
                } catch (e: Exception) {
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(false, "Erreur téléchargement : ${e.message}")
                    }
                }
            }.start()
        }

        /**
         * Liste les fichiers d'un dossier distant via SFTP
         */
        fun listRemoteFilesViaSFTP(
            remoteDirectory: String,
            onResult: (List<String>) -> Unit
        ) {
            Thread {
                try {
                    val jsch = JSch()
                    val session = jsch.getSession(username, host, port)
                    session.setPassword(password)
                    val config = java.util.Properties()
                    config["StrictHostKeyChecking"] = "no"
                    session.setConfig(config)
                    session.connect(5000)
                    val channel = session.openChannel("sftp") as ChannelSftp
                    channel.connect()
                    val fileList = mutableListOf<String>()
                    val vector = channel.ls(remoteDirectory)
                    for (entry in vector) {
                        if (entry is ChannelSftp.LsEntry) {
                            val name = entry.filename
                            if (name != "." && name != "..") {
                                fileList.add(name)
                            }
                        }
                    }
                    channel.disconnect()
                    session.disconnect()
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(fileList)
                    }
                } catch (e: Exception) {
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(emptyList())
                    }
                }
            }.start()
        }


   fun uploadFileviaSftp(
       filepath: String,
       remoteDestinationPath: String,
       onResult: (Boolean, String) -> Unit
   ) {
       Thread {
           try {
               val jsch = JSch()
               session = jsch.getSession(username, host, port)
               session!!.setPassword(password)
               val config = java.util.Properties()
               config["StrictHostKeyChecking"] = "no"
               session!!.setConfig(config)
               session!!.connect(5000)
               channel = session!!.openChannel("sftp")
               channel!!.connect()
               channelSftp = channel as ChannelSftp
               channelSftp!!.put(filepath, remoteDestinationPath)
               channelSftp!!.exit()
               session!!.disconnect()
               channel!!.disconnect()
               channelSftp!!.disconnect()
               android.os.Handler(android.os.Looper.getMainLooper()).post {
                   onResult(true, "Fichier uploadé avec succès !")
               }
           } catch (e: Exception) {
               e.printStackTrace()
               isConnected = false
               android.os.Handler(android.os.Looper.getMainLooper()).post {
                   onResult(false, "Erreur upload : ${e.message}")
               }
           }
       }.start()
   }


}


