package dev.a2ys.conversa.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dev.a2ys.conversa.R
import dev.a2ys.conversa.models.Chat

class ChatAdapter(
    private val context: Context, 
    private val chatList: ArrayList<Chat>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View type constants
    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_RECEIVE) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false)
            ReceiveViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false)
            SentViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = chatList[position]
        
        // Compare the message sender's UID against the locally authenticated UID
        return if (FirebaseAuth.getInstance().currentUser?.uid == currentMessage.sender) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = chatList[position]

        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            viewHolder.sentMessage.text = currentMessage.message
        } else {
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    // ViewHolder pattern for sent messages mapping your item_sent layout elements
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(resources.getIdentifier("txt_sent_message", "id", itemView.context.packageName))
    }

    // ViewHolder pattern for incoming messages mapping your item_receive layout elements
    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(resources.getIdentifier("txt_receive_message", "id", itemView.context.packageName))
    }
}
