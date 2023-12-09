package com.example.travelapp.admin

data class Trips(
    var id: String = "",
    var stasiun_asal: String = "",
    var kota_Asal: String = "",
    var stasiun_tujuan: String = "",
    var kota_tujuan: String = "",
    var harga: String = "",
    var tanggal: String = "",
    var kelas: String = "",
    var paket: String = "",
)
