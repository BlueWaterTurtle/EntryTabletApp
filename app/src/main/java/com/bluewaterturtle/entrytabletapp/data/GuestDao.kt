package com.bluewaterturtle.entrytabletapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GuestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuest(guest: GuestEntity): Long

    @Update
    suspend fun updateGuest(guest: GuestEntity)

    @Query("SELECT * FROM guests ORDER BY signInTime DESC")
    fun getAllGuests(): LiveData<List<GuestEntity>>

    @Query("SELECT * FROM guests WHERE signOutTime IS NULL ORDER BY name ASC")
    fun getSignedInGuests(): LiveData<List<GuestEntity>>

    @Query("SELECT * FROM guests WHERE id = :id")
    suspend fun getGuestById(id: Long): GuestEntity?

    @Query("DELETE FROM guests")
    suspend fun deleteAllGuests()
}
