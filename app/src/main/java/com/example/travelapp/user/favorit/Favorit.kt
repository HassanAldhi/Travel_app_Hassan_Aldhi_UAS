package com.example.travelapp.user.favorit
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorit_table")
data class Favorit(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int = 0,

    @ColumnInfo(name = "uid")
    val uid: String,

    @ColumnInfo(name = "stasiun_asal")
    val stasiun_asal: String,

    @ColumnInfo(name = "kota_Asal")
    val kota_Asal: String,

    @ColumnInfo(name = "stasiun_tujuan")
    val stasiun_tujuan: String,

    @ColumnInfo(name = "kota_tujuan")
    val kota_tujuan: String,

    @ColumnInfo(name = "harga")
    val harga: String,

    @ColumnInfo(name = "tanggal")
    val tanggal: String,

    @ColumnInfo(name = "kelas")
    val kelas: String,
)
