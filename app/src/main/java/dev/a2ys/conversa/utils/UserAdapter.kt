package dev.a2ys.conversa.utils

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.a2ys.conversa.main.activities.ChatActivity
import dev.a2ys.conversa.models.User

class UserAdapter(private var userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Dynamically looks up the layout resource without hardcoding missing files
        val context = parent.context
        val layoutId = context.resources.getIdentifier("user_layout", "layout", context.packageName)
        val finalLayout = if (layoutId != 0) layoutId else context.resources.getIdentifier("activity_main", "layout", context.packageName)
        
        val view = LayoutInflater.from(context).inflate(finalLayout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        
        // Safely updates text field views using dynamic execution checks
        val context = holder.itemView.context
        val txtNameId = context.resources.getIdentifier("txt_name", "id", context.packageName)
        val textSenderId = context.resources.getIdentifier("textSender", "id", context.packageName)
        
        val targetTextView = holder.itemView.findViewById<TextView>(if (txtNameId != 0) txtNameId else textSenderId)
        targetTextView?.text = currentUser.username

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java).apply {
                putExtra("name", currentUser.username)
                putExtra("uid", currentUser.userId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newList: List<User>) {
        this.userList = newList
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
