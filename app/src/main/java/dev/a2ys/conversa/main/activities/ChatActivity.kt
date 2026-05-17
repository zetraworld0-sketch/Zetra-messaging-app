package dev.a2ys.conversa.main.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.a2ys.conversa.databinding.ActivityChatBinding
import dev.a2ys.conversa.models.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<Chat>
    private lateinit var database: FirebaseDatabase

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        messageList = ArrayList()

        // Extract intent transmission keys passed from the user list selection
        val receiverUid = intent.getStringExtra("receiverUid")
        val receiverName = intent.getStringExtra("receiverName")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        // Set up the toolbar header with the receiver's name identity
        setSupportActionBar(binding.chatToolbar)
        supportActionBar?.title = receiverName ?: "Secure Session"

        // Construct unique secure chat room pathways inside the database cluster
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        // Initialize the message router adapter using your custom Chat model layout definitions
        chatAdapter = ChatAdapter(this, messageList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = chatAdapter

        // Fetch real-time message updates from the specific active room pathway
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
                            binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Secure fail-safe catch logic block
                    }
                })
        }

        // Handle sending messages on interaction event trigger
        binding.btnSendMessage.setOnClickListener {
            val messageText = binding.etMessageInput.text.toString().trim()
            if (messageText.isNotEmpty() && senderUid != null) {
                val messageObject = Chat(sender = senderUid, message = messageText)

                // Simultaneously push data packets into both the sender and receiver channels
                senderRoom?.let { sRoom ->
                    database.reference.child("chats").child(sRoom).child("messages").push()
                        .setValue(messageObject).addOnSuccessListener {
                            receiverRoom?.let { rRoom ->
                                database.reference.child("chats").child(rRoom).child("messages").push()
                                    .setValue(messageObject)
                            }
                        }
                }
                binding.etMessageInput.setText("")
            }
        }
    }
}
