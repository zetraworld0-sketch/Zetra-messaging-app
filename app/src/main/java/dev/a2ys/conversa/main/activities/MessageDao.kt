package dev.a2ys.conversa.main.activities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.a2ys.conversa.models.LocalMessage

@Dao
interface MessageDao {
    
    // Inserts a new real-time message payload directly into the offline cache
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: LocalMessage)

    // Streams the localized chat history filtered specifically between two explicit user credentials
    @Query("""
        SELECT * FROM local_messages 
        WHERE (senderUid = :sender AND receiverUid = :receiver) 
        OR (senderUid = :receiver AND receiverUid = :sender) 
        ORDER BY timestamp ASC
    """)
    suspend fun getChatHistory(sender: String, receiver: String): List<LocalMessage>
}
