package com.example.travelapp.admin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.databinding.ItemTicketBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

typealias OnClickTrip = (Trips) -> Unit
class TripAdapter(
    private val listTrip: List<Trips>,
    private val onClickTrip: OnClickTrip,
    private val tripCollectionRef: CollectionReference
)
    : RecyclerView.Adapter<TripAdapter.itemContactViewHolder>() {
    inner class itemContactViewHolder(private val binding: ItemTicketBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(data : Trips){

            val id = data.id
            val stAsal = data.stasiun_asal
            val ktAsal = data.kota_Asal
            val stTujuan = data.stasiun_tujuan
            val ktTujuan = data.kota_tujuan
            val harga = data.harga
            var kelas = data.kelas
            val tanggal = data.tanggal
            val paket = data.paket
            val name = "$stAsal - $stTujuan"

            with(binding){
                txtDate.text = tanggal
                txtPrice.text = "$$harga"
                txtStAsal.text = stAsal
                txtKtAsal.text = ktAsal
                txtStTujuan.text = stTujuan
                txtKtTujuan.text = ktTujuan
                tagClass.setImageResource(getImageResourceFromKelas(kelas))

                if (kelas.isNotEmpty()) {
                    txtStartFrom.visibility = View.INVISIBLE
                }

                itemView.setOnClickListener{
                    onClickTrip(data)
                    val intent = Intent(itemView.context, EditActivity::class.java)
                    intent.putExtra("trip_id", id)
                    intent.putExtra("stasiun_asal", stAsal)
                    intent.putExtra("kota_asal", ktAsal)
                    intent.putExtra("stasiun_tujuan", stTujuan)
                    intent.putExtra("kota_tujuan", ktTujuan)
                    intent.putExtra("harga", harga)
                    intent.putExtra("kelas", kelas)
                    intent.putExtra("tanggal", tanggal)
                    intent.putExtra("paket", paket)
                    itemView.context.startActivity(intent)
                }

                itemView.setOnLongClickListener {
                    showDeleteConfirmationDialog(itemView.context, data, name)
                    true // Return true to indicate the event is consumed
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemContactViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return itemContactViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTrip.size
    }
    private fun delete(contact: Trips){
        if (contact.id.isEmpty()){
            Log.d("Main activity","error delete item: budget Id is empty!")
            return
        }
        tripCollectionRef.document(contact.id).delete().addOnFailureListener {
            Log.d("Main activity", "Error deleting budget")
        }
    }
    override fun onBindViewHolder(holder: itemContactViewHolder, position: Int) {
        holder.bind(listTrip[position])
    }
    private fun showDeleteConfirmationDialog(context: Context, id: Trips, name: String) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Ticket")
            .setMessage("Apa anda ingin menghapus perjalanan ${name}?")
            .setPositiveButton("Hapus") { _, _ ->
                delete(id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    fun getImageResourceFromKelas(kelas: String): Int {
        return when (kelas) {
            "Economy" -> R.drawable.tag_economy
            "Bussiness" -> R.drawable.tag_bussiness
            "Executive" -> R.drawable.tag_executive
            else -> 0
        }
    }
}