package dev.a2ys.conversa.main.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.a2ys.conversa.models.Chat
import dev.a2ys.conversa.models.LocalMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<Chat>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var db: AppDatabase

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resources.getIdentifier("activity_chat", "layout", packageName))

        // FIXED: Matched keys exactly with UserAdapter intents ("receiver_name" and "receiver_uid")
        val name = intent.getStringExtra("receiver_name")
        val receiverUid = intent.getStringExtra("receiver_uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().reference
        db = AppDatabase.getDatabase(this)

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        // Dynamic resource matching for reflection stability
        chatRecyclerView = findViewById(resources.getIdentifier("chatRecyclerView", "id", packageName))
        messageBox = findViewById(resources.getIdentifier("messageBox", "id", packageName))
        sendButton = findViewById(resources.getIdentifier("sendButton", "id", packageName))

        messageList = ArrayList()
        messageAdapter = ChatAdapter(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // Load offline chat cache instantly from local disk before fetching cloud updates
        if (senderUid != null && receiverUid != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val cache = db.messageDao().getChatHistory(senderUid, receiverUid)
                withContext(Dispatchers.Main) {
                    if (messageList.isEmpty() && cache.isNotEmpty()) {
                        for (localMsg in cache) {
                            messageList.add(Chat(localMsg.messageText, localMsg.senderUid))
                        }
                        messageAdapter.notifyDataSetChanged()
                        chatRecyclerView.scrollToPosition(messageList.size - 1)
                    }
                }
            }
        }

        // Real-time Cloud Stream Sync Engine
        if (senderRoom != null) {
            mDbRef.child("chats").child(senderRoom!!).child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messageList.clear()
                        for (postSnapshot in snapshot.children) {
                            val message = postSnapshot.getValue(Chat::class.java)
                            if (message != null) {
                                messageList.add(message)
                                
                                // Silently write cloud payload to local disk for offline persistence
                                if (senderUid != null && receiverUid != null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        db.messageDao().insertMessage(
                                            LocalMessage(
                                                senderUid = message.sender ?: "",
                                                receiverUid = if (message.sender == senderUid) receiverUid else senderUid,
                                                messageText = message.message ?: "",
                                                timestamp = System.currentTimeMillis()
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        messageAdapter.notifyDataSetChanged()
                        chatRecyclerView.scrollToPosition(messageList.size - 1)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Operational boundary
                    }
                })
        }

        // Secure Data Push Transmission Flow
        sendButton.setOnClickListener {
            val message = messageBox.text.toString().trim()
            if (message.isNotEmpty() && senderUid != null && receiverUid != null) {
                val messageObject = Chat(message, senderUid)

                // 1. Immediately cache the user payload locally for zero-latency execution
                CoroutineScope(Dispatchers.IO).launch {
                    db.messageDao().insertMessage(
                        LocalMessage(
                            senderUid = senderUid,
                            receiverUid = receiverUid,
                            messageText = message,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                // 2. Transmit payload directly to the network cloud graph
                if (senderRoom != null && receiverRoom != null) {
                    mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                        .setValue(messageObject).addOnSuccessListener {
                            mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                                .setValue(messageObject)
                        }
                }
                messageBox.setText("")
            }
        }
    }
}
