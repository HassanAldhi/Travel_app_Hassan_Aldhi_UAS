package com.example.travelapp.user.pesanan

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.admin.OnClickTrip
import com.example.travelapp.admin.Trips
import com.example.travelapp.databinding.ItemTicketPesananBinding
import com.google.firebase.firestore.CollectionReference

class PesananTiketAdapter(
    private val listTrip: List<Pesanan>,
    private val tripCollectionRef: CollectionReference
)
    : RecyclerView.Adapter<PesananTiketAdapter.itemTiketPesananViewHolder>() {
    inner class itemTiketPesananViewHolder(private val binding: ItemTicketPesananBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(data : Pesanan){

            val id = data.id
            val stAsal = data.stasiun_asal
            val ktAsal = data.kota_Asal
            val stTujuan = data.stasiun_tujuan
            val ktTujuan = data.kota_tujuan
            val harga = data.harga
            var kelas = data.kelas
            val tanggal = data.tanggal
            val paket = data.paket

            with(binding){
                txtDate.text = tanggal
                txtPrice.text = "$$harga"
                txtStAsal.text = stAsal
                txtKtAsal.text = ktAsal
                txtStTujuan.text = stTujuan
                txtKtTujuan.text = ktTujuan
                tagClass.setImageResource(getImageResourceFromKelas(kelas))
                txtPaket.text = paket

                itemView.setOnLongClickListener {
                    showDeleteConfirmationDialog(itemView.context, data)
                    true // Return true to indicate the event is consumed
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemTiketPesananViewHolder {
        val binding = ItemTicketPesananBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return itemTiketPesananViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTrip.size
    }
    private fun delete(contact: Trips){
        if (contact.id.isEmpty()){
            Log.d("Main activity", "error delete item: budget Id is empty!")
            return
        }
        tripCollectionRef.document(contact.id).delete().addOnFailureListener {
            Log.d("Main activity", "Error deleting budget")
        }
    }
    override fun onBindViewHolder(holder: itemTiketPesananViewHolder, position: Int) {
        holder.bind(listTrip[position])
    }
    private fun delete(pesanan: Pesanan){
        if (pesanan.id.isEmpty()){
            Log.d("Main activity","error delete item: budget Id is empty!")
            return
        }
        tripCollectionRef.document(pesanan.id).delete().addOnFailureListener {
            Log.d("Main activity", "Error deleting budget")
        }
    }
    private fun showDeleteConfirmationDialog(context: Context, id: Pesanan) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Ticket")
            .setMessage("Yakin menghapus riwayat pesanan ini ?")
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