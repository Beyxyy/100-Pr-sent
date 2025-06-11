package com.example.prezzapp.database

import android.content.Context
import com.jcraft.jsch.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SftpConnection {

    companion object {
        private const val host: String = "10.74.251.68"
        private const val port: Int = 22
        private const val username: String = "groupe1"
        private const val password: String = "Euler314"
        private const val path = "/home/"

        @Volatile
        private var instance: SftpConnection? = null

        fun getInstance(): SftpConnection {
            return instance ?: synchronized(this) {
                instance ?: SftpConnection().also { instance = it }
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
            context: Context,
            localFileName: String,
            remoteFilePath: String,
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

        /**
         * Upload un fichier via SFTP
         */
        fun uploadFileViaSFTP(
            filepath: String,
            remoteDestinationPath: String,
            onResult: (Boolean, String) -> Unit
        ) {
            Thread {
                try {
                    // Vérifie que le fichier local existe
                    val file = File(filepath)
                    if (!file.exists()) {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onResult(false, "Fichier introuvable : $filepath")
                        }
                        return@Thread
                    }

                    // Connexion SSH via JSch
                    val jsch = JSch()
                    val session = jsch.getSession(username, host, port)
                    session.setPassword(password)

                    val config = java.util.Properties()
                    config["StrictHostKeyChecking"] = "no"
                    session.setConfig(config)

                    session.connect(10000) // 10 secondes de timeout

                    if (!session.isConnected) {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onResult(false, "Session SSH non connectée.")
                        }
                        return@Thread
                    }

                    // Ouverture du canal SFTP
                    val channel = session.openChannel("sftp") as? ChannelSftp
                    if (channel == null) {
                        session.disconnect()
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onResult(false, "Échec de l'ouverture du canal SFTP.")
                        }
                        return@Thread
                    }

                    channel.connect(5000)

                    if (!channel.isConnected) {
                        session.disconnect()
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onResult(false, "Canal SFTP non connecté.")
                        }
                        return@Thread
                    }

                    // Upload du fichier
                    channel.put(filepath, remoteDestinationPath)

                    // Fermeture
                    channel.disconnect()
                    session.disconnect()

                    // Succès
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(true, "Fichier uploadé avec succès vers : $remoteDestinationPath")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(false, "Erreur upload : ${e.message}")
                    }
                }
            }.start()
        }

        fun uploadFileViaSFTPwithFile(
            file : File,
            remoteDestinationPath: String,
            onResult: (Boolean, String) -> Unit
        ) {
            Thread {
                try {
                    // Vérifie que le fichier local existe
                    val file = file
                    if (!file.exists()) {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onResult(false, "Fichier introuvable : ${file.absolutePath}")
                        }
                        return@Thread
                    }

                    // Connexion SSH via JSch
                    val jsch = JSch()
                    val session = jsch.getSession(username, host, port)
                    session.setPassword(password)

                    val config = java.util.Properties()
                    config["StrictHostKeyChecking"] = "no"
                    session.setConfig(config)

                    session.connect(10000) // 10 secondes de timeout

                    if (!session.isConnected) {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onResult(false, "Session SSH non connectée.")
                        }
                        return@Thread
                    }

                    // Ouverture du canal SFTP
                    val channel = session.openChannel("sftp") as? ChannelSftp
                    if (channel == null) {
                        session.disconnect()
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onResult(false, "Échec de l'ouverture du canal SFTP.")
                        }
                        return@Thread
                    }

                    channel.connect(5000)

                    if (!channel.isConnected) {
                        session.disconnect()
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onResult(false, "Canal SFTP non connecté.")
                        }
                        return@Thread
                    }

                    // Upload du fichier
                    channel.put(file.absolutePath, remoteDestinationPath)

                    // Fermeture
                    channel.disconnect()
                    session.disconnect()

                    // Succès
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(true, "Fichier uploadé avec succès vers : $remoteDestinationPath")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onResult(false, "Erreur upload tara : ${e.message}")
                    }
                }
            }.start()
        }

    }
}
