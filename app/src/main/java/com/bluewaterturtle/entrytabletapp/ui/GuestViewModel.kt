package com.bluewaterturtle.entrytabletapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bluewaterturtle.entrytabletapp.data.GuestDatabase
import com.bluewaterturtle.entrytabletapp.data.GuestEntity
import com.bluewaterturtle.entrytabletapp.data.GuestRepository
import kotlinx.coroutines.launch

class GuestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GuestRepository

    val allGuests: LiveData<List<GuestEntity>>
    val signedInGuests: LiveData<List<GuestEntity>>

    init {
        val dao = GuestDatabase.getDatabase(application).guestDao()
        repository = GuestRepository(dao)
        allGuests = repository.allGuests
        signedInGuests = repository.signedInGuests
    }

    fun signIn(name: String, personToSee: String, signInTime: Long) {
        viewModelScope.launch {
            val guest = GuestEntity(
                name = name,
                personToSee = personToSee,
                signInTime = signInTime
            )
            repository.insertGuest(guest)
        }
    }

    fun signOut(id: Long, signOutTime: Long) {
        viewModelScope.launch {
            repository.signOutGuest(id, signOutTime)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.deleteAllGuests()
        }
    }
}
