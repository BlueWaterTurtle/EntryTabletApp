package com.bluewaterturtle.entrytabletapp.data

import androidx.lifecycle.LiveData

class GuestRepository(
    private val guestDao: GuestDao,
    private val personToSeeDao: PersonToSeeDao
) {

    val allGuests: LiveData<List<GuestEntity>> = guestDao.getAllGuests()
    val signedInGuests: LiveData<List<GuestEntity>> = guestDao.getSignedInGuests()
    val activePeopleToSee: LiveData<List<PersonToSeeEntity>> = personToSeeDao.getActivePeople()

    suspend fun insertGuest(guest: GuestEntity): Long = guestDao.insertGuest(guest)

    suspend fun signOutGuest(id: Long, signOutTime: Long) {
        val guest = guestDao.getGuestById(id) ?: return
        guestDao.updateGuest(guest.copy(signOutTime = signOutTime))
    }

    suspend fun addPersonToSee(displayName: String): AddPersonToSeeResult {
        val sanitizedName = PersonToSeeInputSanitizer.sanitizeDisplayName(displayName)
        if (sanitizedName.isEmpty()) {
            return AddPersonToSeeResult.EMPTY_NAME
        }

        val existingPerson = personToSeeDao.getPersonByDisplayName(sanitizedName)
        return when {
            existingPerson == null -> {
                personToSeeDao.insertPerson(PersonToSeeEntity(displayName = sanitizedName))
                AddPersonToSeeResult.ADDED
            }

            existingPerson.active -> AddPersonToSeeResult.DUPLICATE_ACTIVE

            else -> {
                personToSeeDao.updatePerson(
                    existingPerson.copy(
                        displayName = sanitizedName,
                        active = true
                    )
                )
                AddPersonToSeeResult.REACTIVATED
            }
        }
    }

    suspend fun deactivatePersonToSee(id: Long) {
        personToSeeDao.setPersonActive(id, false)
    }

    suspend fun deleteAllGuests() = guestDao.deleteAllGuests()
}
