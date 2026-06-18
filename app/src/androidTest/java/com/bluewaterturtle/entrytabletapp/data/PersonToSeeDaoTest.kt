package com.bluewaterturtle.entrytabletapp.data

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersonToSeeDaoTest {

    private lateinit var database: GuestDatabase
    private lateinit var dao: PersonToSeeDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, GuestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.personToSeeDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun activePeople_areSortedAndExcludeInactiveEntries() = runBlocking {
        val zoeId = dao.insertPerson(PersonToSeeEntity(displayName = "Zoe"))
        dao.insertPerson(PersonToSeeEntity(displayName = "alice"))
        dao.setPersonActive(zoeId, false)

        val activePeople = dao.getActivePeople().getOrAwaitValue()

        assertEquals(listOf("alice"), activePeople.map { it.displayName })
    }

    @Test
    fun displayName_isUniqueIgnoringCase() {
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                dao.insertPerson(PersonToSeeEntity(displayName = "Alice"))
                dao.insertPerson(PersonToSeeEntity(displayName = "alice"))
            }
        }
    }

    private fun <T> LiveData<T>.getOrAwaitValue(): T {
        val latch = CountDownLatch(1)
        var data: T? = null
        val observer = androidx.lifecycle.Observer<T> {
            data = it
            latch.countDown()
        }

        observeForever(observer)
        try {
            if (!latch.await(2, TimeUnit.SECONDS)) {
                throw AssertionError("LiveData value was never set.")
            }
        } finally {
            removeObserver(observer)
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}
