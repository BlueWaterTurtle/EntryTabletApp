package com.bluewaterturtle.entrytabletapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "people_to_see",
    indices = [Index(value = ["display_name"], unique = true)]
)
data class PersonToSeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "display_name", collate = ColumnInfo.NOCASE) val displayName: String,
    @ColumnInfo(defaultValue = "1") val active: Boolean = true
)
