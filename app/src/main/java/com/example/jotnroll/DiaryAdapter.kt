package com.example.jotnroll

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class DiaryEntry(
    val id: String = "",  // use `doc.id` when retrieving
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val content: String = "",
    val date: Timestamp? = null // or use `String`, just be consistent
)


class DiaryAdapter(
    private val context: Context,
    private val entryList: MutableList<DiaryEntry>
) : RecyclerView.Adapter<DiaryAdapter.EntryViewHolder>() {

    inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.tvUserName)
        val titleText: TextView = itemView.findViewById(R.id.tvDiaryTitle)
        val dateText: TextView = itemView.findViewById(R.id.tvDate)
        val contentText: TextView = itemView.findViewById(R.id.tvDiaryContent)
        val viewBtn: Button = itemView.findViewById(R.id.btnView)
        val deleteBtn: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary_entry, parent, false)

        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entryList[position]

        holder.nameText.text = entry.userName
        holder.titleText.text = entry.title
        holder.dateText.text = entry.date

        // Preview only the first 100 characters of the content
        holder.contentText.text = if (entry.content.length > 100)
            entry.content.take(100) + "..."
        else
            entry.content

        holder.viewBtn.setOnClickListener {
            val intent = Intent(context, ViewEntry::class.java).apply {
                putExtra("userName", entry.userName)
                putExtra("title", entry.title)
                putExtra("date", entry.date)
                putExtra("content", entry.content)
            }
            context.startActivity(intent)
        }

        holder.deleteBtn.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null && user.email == entry.email) {
                FirebaseFirestore.getInstance().collection("entries").document(entry.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Entry deleted", Toast.LENGTH_SHORT).show()
                        entryList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, entryList.size)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "You can only delete your own entries", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = entryList.size
}
