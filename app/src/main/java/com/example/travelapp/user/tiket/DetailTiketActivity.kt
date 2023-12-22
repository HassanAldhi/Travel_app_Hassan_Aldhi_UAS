package com.example.travelapp.user.tiket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.travelapp.R
import com.example.travelapp.authentication.PrefManager
import com.example.travelapp.databinding.ActivityDetailTicketBinding
import com.example.travelapp.user.UserMainActivity
import com.example.travelapp.user.favorit.Favorit
import com.example.travelapp.user.favorit.FavoritDao
import com.example.travelapp.user.favorit.FavoritRoomDatabase
import com.example.travelapp.user.pesanan.Pesanan
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetailTiketActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val pesananCollectionRef = firestore.collection("pesanan")
    private lateinit var binding : ActivityDetailTicketBinding
    private lateinit var prefManager: PrefManager
    lateinit var mFavoritDao : FavoritDao
    lateinit var executorService: ExecutorService
    private val channelId = "Pesan Tiket"

    private var selectedKotaAsal = ""
    private var selectedStasiunAsal = ""
    private var selectedKotaTujuan = ""
    private var selectedStasiunTujuan = ""
    private var selectedKelas = ""
    private var selectedTanggal = ""
    private val selectedPaket = mutableSetOf<String>()
    private var paket = ""
    private var hargaPaket = 0
    private var harga = ""
    private var totalHarga = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityDetailTicketBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this)
        setContentView(binding.root)
        prefManager = PrefManager.getInstance(this)
        executorService = Executors.newSingleThreadExecutor()
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val db = FavoritRoomDatabase.getDatabase(this)
        mFavoritDao = db!!.favoritDao()!!

        var userId = prefManager.getId()
        selectedStasiunAsal = intent.getStringExtra("stasiun_asal").toString()
        selectedKotaAsal = intent.getStringExtra("kota_asal").toString()
        selectedStasiunTujuan = intent.getStringExtra("stasiun_tujuan").toString()
        selectedKotaTujuan = intent.getStringExtra("kota_tujuan").toString()
        harga = intent.getStringExtra("harga").toString()
        totalHarga = harga?.toIntOrNull() ?: 0
        selectedKelas = intent.getStringExtra("kelas").toString()
        selectedTanggal = intent.getStringExtra("tanggal").toString()

        with(binding){
            txtDate.text = selectedTanggal
            txtStAsal.text = selectedStasiunAsal
            txtKtAsal.text = selectedKotaAsal
            txtStTujuan.text = selectedStasiunTujuan
            txtKtTujuan.text = selectedKotaTujuan
            tagClass.setImageResource(getImageResourceFromKelas(selectedKelas))
            txtDestinasi.text = "$selectedKotaAsal ke $selectedKotaTujuan"
            txtPrice.setText("$$harga")

            var isFavorited = false
            btnFavorit.setOnClickListener {
                if (isFavorited) {
                    // Jika sudah difavorit sebelumnya, maka hapus data favorit
                    deleteFavorit(userId, selectedStasiunAsal, selectedStasiunTujuan)
                    btnFavorit.setImageResource(R.drawable.ic_unfavorit)
                    Toast.makeText(
                        this@DetailTiketActivity, "Dihapus dari favorit",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Jika belum difavorit, maka tambahkan data favorit
                    insert(Favorit(uid = userId, stasiun_asal = selectedStasiunAsal, stasiun_tujuan = selectedStasiunTujuan,
                        kota_Asal = selectedKotaAsal, kota_tujuan = selectedKotaTujuan, harga = harga, kelas = selectedKelas,
                        tanggal = selectedTanggal))
                    btnFavorit.setImageResource(R.drawable.ic_favorit)
                    Toast.makeText(
                        this@DetailTiketActivity, "Ditambahkan ke favorit",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // Ubah status favorit
                isFavorited = !isFavorited
            }

            paketGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->
                val selectedToggle = when (checkedId) {
                    toggleSnack.id -> toggleSnack
                    toggleMinuman.id -> toggleMinuman
                    toggleMakan.id -> toggleMakan
                    toggleKursi.id -> toggleKursi
                    toggleKasur.id -> toggleKasur
                    toggleDudukDepan.id -> toggleDudukDepan
                    toggleJendela.id -> toggleJendela
                    else -> null
                }

                selectedToggle?.let {
                    if (isChecked) {
                        // Tambahkan ke set selectedPaket
                        selectedPaket.add(it.text.toString())
                    } else {
                        // Hapus dari set selectedPaket
                        selectedPaket.remove(it.text.toString())
                    }

                    // Konversi set menjadi string untuk ditampilkan
                    paket = selectedPaket.joinToString(", ")
                }

                // Hitung ulang hargaPaket
                hargaPaket = selectedPaket.size * 10

                // Perbarui total harga
                updateTotalHarga()
            }

            btnPesan.setOnClickListener {
                val newPesanan =
                    Pesanan(stasiun_asal = selectedStasiunAsal, userId = userId, kota_Asal = selectedKotaAsal,
                        stasiun_tujuan = selectedStasiunTujuan, kota_tujuan = selectedKotaTujuan, tanggal = selectedTanggal,
                        kelas = selectedKelas, paket = paket, harga = "$totalHarga"
                    )
                addPesanan(newPesanan)
                Toast.makeText(
                    this@DetailTiketActivity, "Tiket berhasil dipesan",
                    Toast.LENGTH_SHORT
                ).show()

                val flag = if (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.M){
                    PendingIntent.FLAG_MUTABLE
                }
                else {
                    0
                }

                val intent = Intent(this@DetailTiketActivity, UserMainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    this@DetailTiketActivity, 0,
                    intent, flag
                )
                val builder = NotificationCompat.Builder(this@DetailTiketActivity,
                    channelId).setSmallIcon(R.drawable.ic_train)
                    .setContentTitle("Tiket Berhasil dipesan")
                    .setContentText("Akan berangkat tanggal $selectedTanggal " +
                            "dari $selectedStasiunAsal ke $selectedStasiunTujuan")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
//                .addAction(0, "Baca Notif", pendingIntent)

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O ){
                    val notifChannel = NotificationChannel(channelId, "Notifku",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    with(notifManager){
                        createNotificationChannel(notifChannel)
                        notify(0, builder.build())
                    }
                }
                else{
                    notifManager.notify(0, builder.build())
                }

                finish()
            }

            btnCancel.setOnClickListener{
                finish()
            }
        }
    }

    private fun insert(favorit : Favorit){
        executorService.execute{ mFavoritDao.insert(favorit) }
    }
    fun deleteFavorit(userId: String, stasiunAsal: String, stasiunTujuan: String) {
        executorService.execute {
            mFavoritDao.deleteFavorit(userId, stasiunAsal, stasiunTujuan)
        }
    }
    private fun addPesanan(pesanan: Pesanan){
        pesananCollectionRef.add(pesanan).addOnSuccessListener {
                documentReference ->
            val createBudgetId = documentReference.id
            pesanan.id = createBudgetId
            documentReference.set(pesanan).addOnFailureListener{
                Log.d("Detail Ticket activity", "Error updating pesanan id : ", it)
            }
        }.addOnFailureListener{
            Log.d("Detail Ticket activity", "Error adding pesanan id : ", it)
        }

    }

    private fun updateTotalHarga() {
        // Reset total harga menjadi 0
        totalHarga = harga?.toIntOrNull() ?: 0
        totalHarga += hargaPaket

        // Tampilkan total harga
        binding.txtPrice.text = "$$totalHarga"
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