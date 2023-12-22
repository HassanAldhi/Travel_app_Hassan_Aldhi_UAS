package com.example.travelapp.user.favorit
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoritDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favorit: Favorit)

    @Update
    fun update(favorit: Favorit)

    @Query("DELETE FROM favorit_table WHERE id = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM favorit_table WHERE uid = :userId ORDER BY id ASC")
    fun getFavoritsByUserId(userId: String): LiveData<List<Favorit>>
    @Query("DELETE FROM favorit_table WHERE uid = :userId AND stasiun_asal = :stasiunAsal AND stasiun_tujuan = :stasiunTujuan")
    fun deleteFavorit(userId: String, stasiunAsal: String, stasiunTujuan: String)
}
