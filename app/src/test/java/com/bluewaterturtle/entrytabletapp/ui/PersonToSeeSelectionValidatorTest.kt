package com.bluewaterturtle.entrytabletapp.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonToSeeSelectionValidatorTest {

    @Test
    fun `listed person selection returns selected name`() {
        val result = PersonToSeeSelectionValidator.validate(
            selectedOption = "Alice",
            customName = "",
            validNames = setOf("Alice", "Bob"),
            otherLabel = "Other"
        )

        assertEquals(PersonToSeeSelectionResult.Success("Alice"), result)
    }

    @Test
    fun `other selection requires non blank custom name`() {
        val result = PersonToSeeSelectionValidator.validate(
            selectedOption = "Other",
            customName = "   ",
            validNames = setOf("Alice", "Bob"),
            otherLabel = "Other"
        )

        assertTrue(result is PersonToSeeSelectionResult.MissingCustomName)
    }

    @Test
    fun `other selection trims custom name`() {
        val result = PersonToSeeSelectionValidator.validate(
            selectedOption = "Other",
            customName = "  Front Desk  ",
            validNames = setOf("Alice", "Bob"),
            otherLabel = "Other"
        )

        assertEquals(PersonToSeeSelectionResult.Success("Front Desk"), result)
    }

    @Test
    fun `invalid typed selection is rejected`() {
        val result = PersonToSeeSelectionValidator.validate(
            selectedOption = "Unknown",
            customName = "",
            validNames = setOf("Alice", "Bob"),
            otherLabel = "Other"
        )

        assertTrue(result is PersonToSeeSelectionResult.InvalidSelection)
    }
}
