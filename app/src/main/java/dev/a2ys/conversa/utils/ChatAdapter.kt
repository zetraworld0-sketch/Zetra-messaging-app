package dev.a2ys.conversa.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dev.a2ys.conversa.databinding.ChatItemBinding
import dev.a2ys.conversa.models.Chat

class ChatAdapter(private val chatList: List<Chat>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val data = chatList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = chatList.size

    class ChatViewHolder(private val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(chat: Chat) {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            
            // Assign messaging payload values
            binding.textSender.text = chat.sender
            binding.textMessage.text = chat.message
            
            // Hide the circular profile icon within an individual conversation stream
            binding.nodeIcon.visibility = View.GONE

            // Get access to layout parameters to re-anchor bubble boundaries on the fly
            val senderParams = binding.textSender.layoutParams as ConstraintLayout.LayoutParams
            val messageParams = binding.textMessage.layoutParams as ConstraintLayout.LayoutParams

            if (chat.sender == currentUid) {
                // SENT MESSAGE STRUCTURE (Push completely to the right side)
                senderParams.startToEnd = ConstraintLayout.LayoutParams.UNSET
                senderParams.endToStart = ConstraintLayout.LayoutParams.UNSET
                senderParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                
                messageParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                messageParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                
                binding.textSender.text = "You"
                binding.textSender.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                binding.textMessage.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                
                // Set an accent tint for your messages to stand out
                binding.textMessage.setTextColor(android.graphics.Color.parseColor("#38BDF8"))
            } else {
                // RECEIVED MESSAGE STRUCTURE (Push completely to the left side)
                senderParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
                senderParams.endToStart = ConstraintLayout.LayoutParams.UNSET
                senderParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                
                messageParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
                messageParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                
                binding.textSender.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                binding.textMessage.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                
                // Return default system slate color for inbound text layers
                binding.textMessage.setTextColor(android.graphics.Color.parseColor("#94A3B8"))
            }

            // Apply calculated constraints cleanly back into the layout view components
            binding.textSender.layoutParams = senderParams
            binding.textMessage.layoutParams = messageParams
        }
    }
}
