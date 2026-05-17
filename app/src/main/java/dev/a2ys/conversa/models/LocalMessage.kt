package dev.a2ys.conversa.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_messages")
data class LocalMessage(
    @PrimaryKey(autoGenerate = true) 
    val localId: Long = 0,
    val senderUid: String,
    val receiverUid: String,
    val messageText: String,
    val timestamp: Long
)
