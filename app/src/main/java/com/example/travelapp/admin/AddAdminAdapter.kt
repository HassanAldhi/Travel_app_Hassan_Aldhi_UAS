package com.example.travelapp.admin

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.authentication.Users
import com.example.travelapp.databinding.ItemUserBinding
import com.google.firebase.firestore.CollectionReference

class AddAdminAdapter(
    private val listUser: List<Users>,
    private val UserCollectionRef: CollectionReference
)
    : RecyclerView.Adapter<AddAdminAdapter.itemAddAdminViewHolder>() {
    inner class itemAddAdminViewHolder(private val binding: ItemUserBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(data : Users){

            val id = data.id
            val username = data.username
            val email = data.email
            val role = data.role

            with(binding){
                txtUsername.setText(username)
                txtEmail.setText(email)

                if(role == "admin"){
                    checkboxAdmin.setChecked(true)
                }

                checkboxAdmin.setOnCheckedChangeListener { _, isChecked ->
                    // Update user role based on checkbox state
                    if (isChecked) {
                        updateRole(data, "admin")
                    } else {
                        updateRole(data, "user")
                    }
                }

                itemView.setOnLongClickListener {
                    showDeleteConfirmationDialog(itemView.context, data, username)
                    true // Return true to indicate the event is consumed
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemAddAdminViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return itemAddAdminViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }
    private fun delete(users: Users){
        if (users.id.isEmpty()){
            Log.d("Add Admin activity","error delete item: user Id is empty!")
            return
        }
        UserCollectionRef.document(users.id).delete().addOnFailureListener {
            Log.d("Add Admin activity", "Error deleting budget")
        }
    }

    private fun updateRole(users: Users, role: String) {
        if (users.id != null) {
            UserCollectionRef.document(users.id)
                .update("role", role)
                .addOnSuccessListener {
                    // Update successful
                }
                .addOnFailureListener { e ->
                    // Handle the failure
                }
        }
    }
    override fun onBindViewHolder(holder: itemAddAdminViewHolder, position: Int) {
        holder.bind(listUser[position])
    }
    private fun showDeleteConfirmationDialog(context: Context, id: Users, name: String) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Pengguna")
            .setMessage("Apa anda ingin menghapus  ${name}?")
            .setPositiveButton("Hapus") { _, _ ->
                delete(id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}