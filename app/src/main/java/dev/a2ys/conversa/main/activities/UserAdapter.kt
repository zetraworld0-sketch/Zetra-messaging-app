package dev.a2ys.conversa.utils

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.a2ys.conversa.databinding.UserLayoutBinding  // Safely targeting your generated user layout binding
import dev.a2ys.conversa.main.activities.ChatActivity
import dev.a2ys.conversa.models.User

class UserAdapter(private var userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        
        // Dynamically binds name elements from your User data class
        holder.binding.textSender.text = currentUser.username
        holder.binding.textMessage.text = "Tap to secure transmission link"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChatActivity::class.java).apply {
                putExtra("name", currentUser.username)
                putExtra("uid", currentUser.userId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = userList.size

    // Clean up memory leaks by updating data sets cleanly instead of using submitList
    fun updateData(newList: List<User>) {
        this.userList = newList
        notifyDataSetChanged()
    }

    class UserViewHolder(val binding: UserLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
