package com.bluewaterturtle.entrytabletapp.data

import androidx.lifecycle.LiveData

class GuestRepository(private val guestDao: GuestDao) {

    val allGuests: LiveData<List<GuestEntity>> = guestDao.getAllGuests()
    val signedInGuests: LiveData<List<GuestEntity>> = guestDao.getSignedInGuests()

    suspend fun insertGuest(guest: GuestEntity): Long = guestDao.insertGuest(guest)

    suspend fun signOutGuest(id: Long, signOutTime: Long) {
        val guest = guestDao.getGuestById(id) ?: return
        guestDao.updateGuest(guest.copy(signOutTime = signOutTime))
    }

    suspend fun deleteAllGuests() = guestDao.deleteAllGuests()
}
