package com.example.travelapp.admin

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.example.travelapp.R
import com.example.travelapp.databinding.ActivityAddBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val tripCollectionRef = firestore.collection("trips")
    private lateinit var binding : ActivityAddBinding

    private var selectedKotaAsal = ""
    private var selectedStasiunAsal = ""
    private var selectedKotaTujuan = ""
    private var selectedStasiunTujuan = ""
    private var selectedKelas = ""
    private var selectedTanggal = ""
    private var paket = ""
    private var hargaPaket = 0
    private var totalHarga = 0

    private val hargaKotaAsal = mapOf("Yogyakarta" to 12, "Surakarta" to 9, "Klaten" to 8, "Semarang" to 18)
    private val hargaStasiunAsal = mapOf("Toegoe" to 28, "Loumpuyangan" to 39, "Malbor" to 18, "Ganbril" to 42)
    private val hargaKotaTujuan = mapOf("Jakarta" to 48, "Bandung" to 40, "Surabaya" to 36, "Jember" to 24)
    private val hargaStasiunTujuan = mapOf("Roeso" to 21, "Toelakagin" to 31, "Badrec" to 20, "Atangin" to 12)
    private val hargaKelas = mapOf("Executive" to 100, "Bussiness" to 75, "Economy" to 50)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityAddBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this)
        setContentView(binding.root)


        with(binding){
            val stAsal = resources.getStringArray(R.array.stAsal)
            val adapterStAsal = ArrayAdapter(this@AddActivity, R.layout.dropdown_menu, stAsal )
            edtStAsal.setAdapter(adapterStAsal)

            val ktAsal = resources.getStringArray(R.array.ktAsal)
            val adapterKtAsal = ArrayAdapter(this@AddActivity, R.layout.dropdown_menu, ktAsal )
            edtKtAsal.setAdapter(adapterKtAsal)

            val stTujuan = resources.getStringArray(R.array.stTujuan)
            val adapterStTujuan= ArrayAdapter(this@AddActivity, R.layout.dropdown_menu, stTujuan )
            edtStTujuan.setAdapter(adapterStTujuan)

            val ktTujuan = resources.getStringArray(R.array.ktTujuan)
            val adapterKtTujuan = ArrayAdapter(this@AddActivity, R.layout.dropdown_menu, ktTujuan )
            edtKtTujuan.setAdapter(adapterKtTujuan)

            val kelas = resources.getStringArray(R.array.kelas)
            val adapterKelas = ArrayAdapter(this@AddActivity, R.layout.dropdown_menu, kelas )
            edtKelas.setAdapter(adapterKelas)

            //on click listener
//            val edtKtAsal = tilKtAsal.editText as AutoCompleteTextView
//            val edtStAsal = tilStAsal.editText as AutoCompleteTextView
//            val edtKtTujuan = tilKtTujuan.editText as AutoCompleteTextView
//            val edtStTujuan = tilStTujuan.editText as AutoCompleteTextView
//            val edtKelas = tilKelas.editText as AutoCompleteTextView


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
                    this@AddActivity,
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

            paketGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->
                if (isChecked) {
                    hargaPaket += 10
                } else {
                    hargaPaket -= 10
                }

                updateTotalHarga()

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
                        if (paket.isNotEmpty()) {
                            paket += ", "
                        }
                        paket += it.text
                    } else {
                    }
                }
            }
            btnSave.setOnClickListener {
                if( selectedTanggal != "" && selectedKelas != "" &&
                    selectedKotaAsal != "" && selectedStasiunAsal != "" &&
                    selectedKotaTujuan != "" && selectedStasiunTujuan != ""){
                    val newTrip =
                        Trips(stasiun_asal = selectedStasiunAsal, kota_Asal = selectedKotaAsal,
                              stasiun_tujuan = selectedStasiunTujuan, kota_tujuan = selectedKotaTujuan,
                              tanggal = selectedTanggal, kelas = selectedKelas, paket = paket, harga = "$totalHarga")
                    addTrip(newTrip)
                    finish()
                }else{
                    Toast.makeText(this@AddActivity, "Fill all data!!",
                        Toast.LENGTH_SHORT).show()
                }
            }

            btnCancel.setOnClickListener{
                finish()
            }
        }
    }
    private fun addTrip(contact: Trips){
        tripCollectionRef.add(contact).addOnSuccessListener {
                documentReference ->
            val createBudgetId = documentReference.id
            contact.id = createBudgetId
            documentReference.set(contact).addOnFailureListener{
                Log.d("Add activity", "Error updating Trip id : ", it)
            }
        }.addOnFailureListener{
            Log.d("Add activity", "Error adding Trip id : ", it)
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
