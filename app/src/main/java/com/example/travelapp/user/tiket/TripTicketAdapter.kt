package com.example.travelapp.user.tiket

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.admin.OnClickTrip
import com.example.travelapp.admin.Trips
import com.example.travelapp.databinding.ItemTicketBinding
import com.google.firebase.firestore.CollectionReference

class TripTicketAdapter(
    private val listTrip: List<Trips>,
    private val onClickTrip: OnClickTrip,
    private val tripCollectionRef: CollectionReference
)
    : RecyclerView.Adapter<TripTicketAdapter.itemTicketViewHolder>() {
    inner class itemTicketViewHolder(private val binding: ItemTicketBinding):
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
            val name = "$stAsal - $stTujuan"

            with(binding){
                txtDate.text = tanggal
                txtPrice.text = "$$harga"
                txtStAsal.text = stAsal
                txtKtAsal.text = ktAsal
                txtStTujuan.text = stTujuan
                txtKtTujuan.text = ktTujuan
                tagClass.setImageResource(getImageResourceFromKelas(kelas))

                itemView.setOnClickListener{
                    onClickTrip(data)
                    val intent = Intent(itemView.context, DetailTiketActivity::class.java)
                    intent.putExtra("trip_id", id)
                    intent.putExtra("stasiun_asal", stAsal)
                    intent.putExtra("kota_asal", ktAsal)
                    intent.putExtra("stasiun_tujuan", stTujuan)
                    intent.putExtra("kota_tujuan", ktTujuan)
                    intent.putExtra("harga", harga)
                    intent.putExtra("kelas", kelas)
                    intent.putExtra("tanggal", tanggal)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemTicketViewHolder {
        val binding = ItemTicketBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return itemTicketViewHolder(binding)
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
    override fun onBindViewHolder(holder: itemTicketViewHolder, position: Int) {
        holder.bind(listTrip[position])
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