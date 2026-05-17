package dev.a2ys.conversa.main.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.a2ys.conversa.models.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<Chat>
    private lateinit var database: FirebaseDatabase

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var etMessageInput: EditText
    private lateinit var btnSendMessage: ImageButton
    private lateinit var chatToolbar: Toolbar

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resources.getIdentifier("activity_chat", "layout", packageName))

        database = FirebaseDatabase.getInstance()
        messageList = ArrayList()

        // Direct layout lookups to bypass R file errors
        chatRecyclerView = findViewById(resources.getIdentifier("chatRecyclerView", "id", packageName))
        etMessageInput = findViewById(resources.getIdentifier("etMessageInput", "id", packageName))
        btnSendMessage = findViewById(resources.getIdentifier("btnSendMessage", "id", packageName))
        chatToolbar = findViewById(resources.getIdentifier("chatToolbar", "id", packageName))

        val receiverUid = intent.getStringExtra("receiverUid")
        val receiverName = intent.getStringExtra("receiverName")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        setSupportActionBar(chatToolbar)
        supportActionBar?.title = receiverName ?: "Secure Session"

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        chatAdapter = ChatAdapter(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        senderRoom?.let { room ->
            database.reference.child("chats").child(room).child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messageList.clear()
                        for (postSnapshot in snapshot.children) {
                            val message = postSnapshot.getValue(Chat::class.java)
                            if (message != null) {
                                messageList.add(message)
                            }
                        }
                        chatAdapter.notifyDataSetChanged()
                        if (messageList.isNotEmpty()) {
                            chatRecyclerView.scrollToPosition(messageList.size - 1)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Safe exit block
                    }
                })
        }

        btnSendMessage.setOnClickListener {
            val messageText = etMessageInput.text.toString().trim()
            if (messageText.isNotEmpty() && senderUid != null) {
                val messageObject = Chat(sender = senderUid, message = messageText)

                senderRoom?.let { sRoom ->
                    database.reference.child("chats").child(sRoom).child("messages").push()
                        .setValue(messageObject).addOnSuccessListener {
                            receiverRoom?.let { rRoom ->
                                database.reference.child("chats").child(rRoom).child("messages").push()
                                    .setValue(messageObject)
                            }
                        }
                }
                etMessageInput.setText("")
            }
        }
    }
}
