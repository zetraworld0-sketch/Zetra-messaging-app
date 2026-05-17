package dev.a2ys.conversa.main.activities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.a2ys.conversa.models.LocalMessage

@Database(entities = [LocalMessage::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Secure Thread-safe Singleton block to access local storage instance
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "netscape_secure_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
