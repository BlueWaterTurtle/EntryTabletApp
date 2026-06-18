package com.bluewaterturtle.entrytabletapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GuestRepositoryPersonToSeeTest {

    private val guestDao = FakeGuestDao()
    private val personDao = FakePersonToSeeDao()
    private val repository = GuestRepository(guestDao, personDao)

    @Test
    fun `add person rejects blank name`() = runBlocking {
        val result = repository.addPersonToSee("   ")

        assertEquals(AddPersonToSeeResult.EMPTY_NAME, result)
        assertTrue(personDao.people.isEmpty())
    }

    @Test
    fun `add person inserts sanitized active name`() = runBlocking {
        val result = repository.addPersonToSee("  Alice   Smith ")

        assertEquals(AddPersonToSeeResult.ADDED, result)
        assertEquals(listOf("Alice Smith"), personDao.people.map { it.displayName })
        assertEquals(listOf("Alice Smith"), repository.activePeopleToSee.value?.map { it.displayName })
    }

    @Test
    fun `add person rejects case insensitive active duplicate`() = runBlocking {
        personDao.seed(PersonToSeeEntity(id = 1, displayName = "Alice Smith", active = true))

        val result = repository.addPersonToSee("alice smith")

        assertEquals(AddPersonToSeeResult.DUPLICATE_ACTIVE, result)
        assertEquals(1, personDao.people.size)
    }

    @Test
    fun `add person reactivates inactive duplicate`() = runBlocking {
        personDao.seed(PersonToSeeEntity(id = 1, displayName = "Alice Smith", active = false))

        val result = repository.addPersonToSee("  alice smith ")

        assertEquals(AddPersonToSeeResult.REACTIVATED, result)
        assertTrue(personDao.people.single().active)
        assertEquals("alice smith", personDao.people.single().displayName)
    }

    @Test
    fun `deactivate person marks entry inactive`() = runBlocking {
        personDao.seed(PersonToSeeEntity(id = 1, displayName = "Alice Smith", active = true))

        repository.deactivatePersonToSee(1)

        assertFalse(personDao.people.single().active)
        assertTrue(repository.activePeopleToSee.value.isNullOrEmpty())
    }

    private class FakeGuestDao : GuestDao {
        private val guests = mutableListOf<GuestEntity>()
        private val allGuests = MutableLiveData<List<GuestEntity>>(emptyList())
        private val signedInGuests = MutableLiveData<List<GuestEntity>>(emptyList())

        override suspend fun insertGuest(guest: GuestEntity): Long {
            val newId = (guests.maxOfOrNull { it.id } ?: 0) + 1
            guests += guest.copy(id = newId)
            publish()
            return newId
        }

        override suspend fun updateGuest(guest: GuestEntity) {
            val index = guests.indexOfFirst { it.id == guest.id }
            if (index >= 0) {
                guests[index] = guest
                publish()
            }
        }

        override fun getAllGuests(): LiveData<List<GuestEntity>> = allGuests

        override fun getSignedInGuests(): LiveData<List<GuestEntity>> = signedInGuests

        override suspend fun getGuestById(id: Long): GuestEntity? = guests.find { it.id == id }

        override suspend fun deleteAllGuests() {
            guests.clear()
            publish()
        }

        private fun publish() {
            allGuests.value = guests.toList()
            signedInGuests.value = guests.filter { it.signOutTime == null }.sortedBy { it.name }
        }
    }

    private class FakePersonToSeeDao : PersonToSeeDao {
        val people = mutableListOf<PersonToSeeEntity>()
        private val activePeople = MutableLiveData<List<PersonToSeeEntity>>(emptyList())

        override suspend fun insertPerson(person: PersonToSeeEntity): Long {
            val newId = (people.maxOfOrNull { it.id } ?: 0) + 1
            people += person.copy(id = newId)
            publish()
            return newId
        }

        override suspend fun updatePerson(person: PersonToSeeEntity) {
            val index = people.indexOfFirst { it.id == person.id }
            if (index >= 0) {
                people[index] = person
                publish()
            }
        }

        override fun getActivePeople(): LiveData<List<PersonToSeeEntity>> = activePeople

        override suspend fun getPersonByDisplayName(displayName: String): PersonToSeeEntity? {
            return people.find { it.displayName.equals(displayName, ignoreCase = true) }
        }

        override suspend fun setPersonActive(id: Long, active: Boolean) {
            val index = people.indexOfFirst { it.id == id }
            if (index >= 0) {
                people[index] = people[index].copy(active = active)
                publish()
            }
        }

        fun seed(person: PersonToSeeEntity) {
            people += person
            publish()
        }

        private fun publish() {
            activePeople.value = people
                .filter { it.active }
                .sortedBy { it.displayName.lowercase() }
        }
    }
}
