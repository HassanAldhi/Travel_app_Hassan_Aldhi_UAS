package com.example.travelapp.admin

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.travelapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.example.travelapp.databinding.ActivityEditBinding
import com.google.firebase.FirebaseApp
import java.util.Calendar

class EditActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val tripCollectionRef = firestore.collection("trips")
    private var updateId : String = ""
    private lateinit var binding : ActivityEditBinding

    private var selectedKotaAsal = ""
    private var selectedStasiunAsal = ""
    private var selectedKotaTujuan = ""
    private var selectedStasiunTujuan = ""
    private var selectedKelas = ""
    private var selectedTanggal = ""
    private var hargaPaket = 0
    private var totalHarga = 0

    private val hargaKotaAsal = mapOf("Yogyakarta" to 12, "Surakarta" to 9, "Klaten" to 8, "Semarang" to 18)
    private val hargaStasiunAsal = mapOf("Toegoe" to 28, "Loumpuyangan" to 39, "Malbor" to 18, "Ganbril" to 42)
    private val hargaKotaTujuan = mapOf("Jakarta" to 48, "Bandung" to 40, "Surabaya" to 36, "Jember" to 24)
    private val hargaStasiunTujuan = mapOf("Roeso" to 21, "Toelakagin" to 31, "Badrec" to 20, "Atangin" to 12)
    private val hargaKelas = mapOf("Executive" to 100, "Bussiness" to 75, "Economy" to 50)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        FirebaseApp.initializeApp(this)

        updateId = intent.getStringExtra("trip_id") ?: ""
        selectedStasiunAsal = intent.getStringExtra("stasiun_asal").toString()
        selectedKotaAsal = intent.getStringExtra("kota_asal").toString()
        selectedStasiunTujuan = intent.getStringExtra("stasiun_tujuan").toString()
        selectedKotaTujuan = intent.getStringExtra("kota_tujuan").toString()
        val harga = intent.getStringExtra("harga")
        totalHarga = harga?.toIntOrNull() ?: 0
        selectedKelas = intent.getStringExtra("kelas").toString()
        selectedTanggal = intent.getStringExtra("tanggal").toString()


        with(binding){
            edtStAsal.setText(selectedStasiunAsal)
            edtStTujuan.setText(selectedStasiunTujuan)
            edtKtAsal.setText(selectedKotaAsal)
            edtKtTujuan.setText(selectedKotaTujuan)
            edtTanggalBerangkat.setText(selectedTanggal)
            edtKelas.setText(selectedKelas)
            txtPrice.text = "$$harga"


            val stAsal = resources.getStringArray(R.array.stAsal)
            val adapterStAsal = ArrayAdapter(this@EditActivity, R.layout.dropdown_menu, stAsal )
            edtStAsal.setAdapter(adapterStAsal)

            val ktAsal = resources.getStringArray(R.array.ktAsal)
            val adapterKtAsal = ArrayAdapter(this@EditActivity, R.layout.dropdown_menu, ktAsal )
            edtKtAsal.setAdapter(adapterKtAsal)

            val stTujuan = resources.getStringArray(R.array.stTujuan)
            val adapterStTujuan= ArrayAdapter(this@EditActivity, R.layout.dropdown_menu, stTujuan )
            edtStTujuan.setAdapter(adapterStTujuan)

            val ktTujuan = resources.getStringArray(R.array.ktTujuan)
            val adapterKtTujuan = ArrayAdapter(this@EditActivity, R.layout.dropdown_menu, ktTujuan )
            edtKtTujuan.setAdapter(adapterKtTujuan)

            val kls = resources.getStringArray(R.array.kelas)
            val adapterKelas = ArrayAdapter(this@EditActivity, R.layout.dropdown_menu, kls )
            edtKelas.setAdapter(adapterKelas)

            selectedKotaAsal = edtKtAsal.text.toString()
            selectedStasiunAsal = edtStAsal.text.toString()
            selectedKotaTujuan = edtKtTujuan.text.toString()
            selectedStasiunTujuan = edtStTujuan.text.toString()
            selectedKelas = edtKelas.text.toString()

            val edtKtAsal = tilKtAsal.editText as AutoCompleteTextView
            val edtStAsal = tilStAsal.editText as AutoCompleteTextView
            val edtKtTujuan = tilKtTujuan.editText as AutoCompleteTextView
            val edtStTujuan = tilStTujuan.editText as AutoCompleteTextView
            val edtKelas = tilKelas.editText as AutoCompleteTextView


            edtKtAsal.setOnItemClickListener { _, _, position, _ ->
                selectedKotaAsal = edtKtAsal.text.toString()
                updateTotalHarga()
            }
            edtStAsal.setOnItemClickListener { _, _, position, _ ->
                selectedStasiunAsal = edtStAsal.text.toString()
                updateTotalHarga()
            }
            edtKtTujuan.setOnItemClickListener { _, _, position, _ ->
                selectedKotaTujuan = edtKtTujuan.text.toString()
                updateTotalHarga()
            }
            edtStTujuan.setOnItemClickListener { _, _, position, _ ->
                selectedStasiunTujuan = edtStTujuan.text.toString()
                updateTotalHarga()
            }
            edtKelas.setOnItemClickListener { _, _, position, _ ->
                selectedKelas = edtKelas.text.toString()
                updateTotalHarga()
            }

            edtTanggalBerangkat.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this@EditActivity,
                    { _, year, monthOfYear, dayOfMonth ->
                        val date = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                        edtTanggalBerangkat.setText(date)
                        selectedTanggal = date
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }

            btnUpdate.setOnClickListener {
                val newTrip =
                    Trips(stasiun_asal = selectedStasiunAsal, kota_Asal = selectedKotaAsal,
                        stasiun_tujuan = selectedStasiunTujuan, kota_tujuan = selectedKotaTujuan,
                        tanggal = selectedTanggal, kelas = selectedKelas, harga = "$totalHarga")
                updateTrip(newTrip)
                updateId = ""
                finish()
            }

            btnCancel.setOnClickListener{
                finish()
            }
        }


    }
    private fun updateTrip(trips: Trips){
        trips.id = updateId
        tripCollectionRef.document(updateId).set(trips).
        addOnFailureListener {
            Log.d("Main activity", "error updating budget", it)
        }
    }

    private fun updateTotalHarga() {
        // Reset total harga menjadi 0
        totalHarga = 0

        // Tambahkan harga setiap item yang dipilih
        totalHarga += hargaKotaAsal[selectedKotaAsal] ?: 0
        totalHarga += hargaStasiunAsal[selectedStasiunAsal] ?: 0
        totalHarga += hargaKotaTujuan[selectedKotaTujuan] ?: 0
        totalHarga += hargaStasiunTujuan[selectedStasiunTujuan] ?: 0
        totalHarga += hargaKelas[selectedKelas] ?: 0
        totalHarga += hargaPaket

        // Tampilkan total harga
        binding.txtPrice.text = "$$totalHarga"
    }
}