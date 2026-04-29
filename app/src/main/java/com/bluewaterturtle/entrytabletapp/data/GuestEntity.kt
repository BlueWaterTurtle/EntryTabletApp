package com.bluewaterturtle.entrytabletapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guests")
data class GuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val personToSee: String,
    val signInTime: Long,
    val signOutTime: Long? = null
)
