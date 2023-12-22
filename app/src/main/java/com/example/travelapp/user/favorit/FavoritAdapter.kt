package com.example.travelapp.user.favorit

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.databinding.ItemTicketBinding
import java.util.concurrent.ExecutorService

class FavoritAdapter(private val listFavorit : List<Favorit>,
                     private val mFavoritDao: FavoritDao,
                     private val executorService: ExecutorService
)
    : RecyclerView.Adapter<FavoritAdapter.itemFavoritViewHolder>() {


    inner class itemFavoritViewHolder(private val binding: ItemTicketBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(data : Favorit){
            val id = data.id
            val stAsal = data.stasiun_asal
            val ktAsal = data.kota_Asal
            val stTujuan = data.stasiun_tujuan
            val ktTujuan = data.kota_tujuan
            val harga = data.harga
            var kelas = data.kelas
            val tanggal = data.tanggal

            with(binding){
                txtDate.text = tanggal
                txtPrice.text = "$$harga"
                txtStAsal.text = stAsal
                txtKtAsal.text = ktAsal
                txtStTujuan.text = stTujuan
                txtKtTujuan.text = ktTujuan
                tagClass.setImageResource(getImageResourceFromKelas(kelas))

                itemView.setOnLongClickListener {
                    showDeleteConfirmationDialog(itemView.context, id)
                    true // Return true to indicate the event is consumed
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemFavoritViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return itemFavoritViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listFavorit.size
    }
    private fun delete(id: Int) {
        executorService.execute {
            mFavoritDao.delete(id)
        }
    }

    override fun onBindViewHolder(holder: itemFavoritViewHolder, position: Int) {
        holder.bind(listFavorit[position])
    }
    private fun showDeleteConfirmationDialog(context: Context, id: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Favorit")
            .setMessage("Apa anda ingin menghapus Favorit ini?")
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