package com.example.travelapp.user.pesanan

data class Pesanan(
    var id: String = "",
    var userId: String = "",
    var stasiun_asal: String = "",
    var kota_Asal: String = "",
    var stasiun_tujuan: String = "",
    var kota_tujuan: String = "",
    var harga: String = "",
    var tanggal: String = "",
    var kelas: String = "",
    var paket: String = "",
)
