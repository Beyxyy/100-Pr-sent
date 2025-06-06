package com.example.prezzapp.model

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class, Cours::class, Presence::class], version = 2)
@TypeConverters(Converters::class)  // ← Ajoute cette ligne ici
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun coursDao(): CoursDao
    abstract fun presenceDao(): PresenceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prezzapp_database.db"
                )
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
                    .fallbackToDestructiveMigration()  // <-- Ici, on ajoute la gestion de migration destructive
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
