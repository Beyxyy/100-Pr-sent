package com.example.prezzapp.model

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.prezzapp.database.SftpConnection
import java.io.File
import java.io.IOException

@Database(entities = [User::class, Cours::class, Presence::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun coursDao(): CoursDao
    abstract fun presenceDao(): PresenceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DB_NAME = "prezzapp_database.db"
        private const val BACKUP_SUFFIX = "-bkp"
        private const val WAL_SUFFIX = "-wal"
        private const val SHM_SUFFIX = "-shm"
        private val DW_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        private val PATH = DW_DIR.path+DB_NAME

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )

                    .allowMainThreadQueries() // à éviter en prod, mais utile ici
                    .createFromFile(File(PATH))
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("RoomDB", "Base de données créée !")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.d("RoomDB", "Base de données ouverte.")
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


        fun restoreDatabase(context: Context, restart: Boolean = true) {
            val dbFile = context.getDatabasePath(DB_NAME)
            val walFile = File(dbFile.path + WAL_SUFFIX)
            val shmFile = File(dbFile.path + SHM_SUFFIX)
            val backupFile = File(dbFile.path + BACKUP_SUFFIX)
            val backupWalFile = File(backupFile.path + WAL_SUFFIX)
            val backupShmFile = File(backupFile.path + SHM_SUFFIX)

            if (!backupFile.exists()) {
                Log.e("RoomDB", "Fichier de sauvegarde introuvable.")
                return
            }

            checkpoint(context)

            try {
                backupFile.copyTo(dbFile, overwrite = true)
                if (backupWalFile.exists()) backupWalFile.copyTo(walFile, overwrite = true)
                if (backupShmFile.exists()) backupShmFile.copyTo(shmFile, overwrite = true)
                Log.d("RoomDB", "Restauration réussie.")
            } catch (e: IOException) {
                Log.e("RoomDB", "Erreur lors de la restauration : ${e.message}")
            }

            if (restart) {
                val i = context.packageManager.getLaunchIntentForPackage(context.packageName)
                i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
                Runtime.getRuntime().exit(0)
            }
        }

    fun exportDatabase(context: Context): Boolean {
        val sourceDb = context.getDatabasePath("prezzapp_database.db")
        Log.d("ExportDB", "Source DB path: ${sourceDb.absolutePath}")
        val db = AppDatabase.getDatabase(context)
        val userDao = db.userDao()
        val users = userDao.getAll()
        checkpoint(context)
        for (u in users) {
            Log.d("DB", "Nom: ${u.name}, Statut: ${u.status}")
        }
        val destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (destDir == null) {
            Log.e("ExportDB", "Impossible d'obtenir le dossier de destination.")
            return false
        }

        Log.d("ExportDB", "Destination dir: ${destDir.absolutePath}")

        if (!destDir.exists()) {
            val created = destDir.mkdirs()
            Log.d("ExportDB", "Dossier destination créé : $created")
        }

        val destDb = File(destDir, "prezzapp_backup.db")

        return try {
            if (!sourceDb.exists()) {
                Log.e("ExportDB", "La base de données source n'existe pas.")
                return false
            }

            sourceDb.copyTo(destDb, overwrite = true)
            Log.d("ExportDB", "Fichier exporté existe ? ${destDb.exists()}")
            Log.d("ExportDB", "Chemin complet : ${destDb.absolutePath}")
            Log.d("ExportDB", "Export réussi vers : ${destDb.absolutePath}")

            val sftp = SftpConnection.getInstance().uploadFileviaSftp(destDb.absolutePath , "/data")
            { success, message ->
                if (success) {
                    Log.d("SSH", "Connection successful: $message")
                } else {
                    Log.e("SSH", "Connection failed: $message")
                }
            }
            true
        } catch (e: IOException) {
            Log.e("ExportDB", "Erreur d'export : ${e.message}")
            false
        }


    return try {
            sourceDb.copyTo(destDb, overwrite = true)
            Log.d("ExportDB", "Export réussi vers : ${destDb.absolutePath}")
            true
        } catch (e: IOException) {
            Log.e("ExportDB", "Erreur d'export : ${e.message}")
            false
        }
    }



    private fun checkpoint(context: Context) {
            val db = getDatabase(context).openHelper.writableDatabase
            db.query("PRAGMA wal_checkpoint(FULL);")
            db.query("PRAGMA wal_checkpoint(TRUNCATE);")
        }

}

