package com.bluewaterturtle.entrytabletapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PersonToSeeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPerson(person: PersonToSeeEntity): Long

    @Update
    suspend fun updatePerson(person: PersonToSeeEntity)

    @Query("SELECT * FROM people_to_see WHERE active = 1 ORDER BY display_name COLLATE NOCASE ASC")
    fun getActivePeople(): LiveData<List<PersonToSeeEntity>>

    @Query("SELECT * FROM people_to_see WHERE display_name = :displayName COLLATE NOCASE LIMIT 1")
    suspend fun getPersonByDisplayName(displayName: String): PersonToSeeEntity?

    @Query("UPDATE people_to_see SET active = :active WHERE id = :id")
    suspend fun setPersonActive(id: Long, active: Boolean)
}
