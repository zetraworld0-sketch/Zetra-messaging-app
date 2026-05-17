package dev.a2ys.conversa.main.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.a2ys.conversa.models.User

class UserAdapter(
    private val context: Context,
    private val userList: ArrayList<User>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(context)
        val pkg = context.packageName
        val layoutId = context.resources.getIdentifier("item_user_layout", "layout", pkg)
        val view = inflater.inflate(layoutId, parent, false)
        return UserViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        
        // Display the user's username safely
        holder.tvUsername.text = currentUser.username

        // Clicking a user row routes directly to ChatActivity with intent credentials
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("receiverName", currentUser.username)
            intent.putExtra("receiverUid", currentUser.userId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View, ctx: Context) : RecyclerView.ViewHolder(itemView) {
        val id = ctx.resources.getIdentifier("tvUsername", "id", ctx.packageName)
        val tvUsername: TextView = itemView.findViewById(id)
    }
}
