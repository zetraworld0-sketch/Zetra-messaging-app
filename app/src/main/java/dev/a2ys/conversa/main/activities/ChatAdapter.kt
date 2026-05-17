package dev.a2ys.conversa.main.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.a2ys.conversa.models.Chat
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(
    private val context: Context,
    private val messageList: ArrayList<Chat>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val pkg = context.packageName
        
        return if (viewType == ITEM_SENT) {
            val layoutId = context.resources.getIdentifier("item_sent_message", "layout", pkg)
            val view = inflater.inflate(layoutId, parent, false)
            SentViewHolder(view, context)
        } else {
            val layoutId = context.resources.getIdentifier("item_received_message", "layout", pkg)
            val view = inflater.inflate(layoutId, parent, false)
            ReceiverViewHolder(view, context)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            viewHolder.sentMessage.text = currentMessage.message
        } else {
            val viewHolder = holder as ReceiverViewHolder
            viewHolder.receivedMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid == currentMessage.sender) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View, ctx: Context) : RecyclerView.ViewHolder(itemView) {
        val id = ctx.resources.getIdentifier("tvSentMessage", "id", ctx.packageName)
        val sentMessage: TextView = itemView.findViewById(id)
    }

    class ReceiverViewHolder(itemView: View, ctx: Context) : RecyclerView.ViewHolder(itemView) {
        val id = ctx.resources.getIdentifier("tvReceivedMessage", "id", ctx.packageName)
        val receivedMessage: TextView = itemView.findViewById(id)
    }
}
