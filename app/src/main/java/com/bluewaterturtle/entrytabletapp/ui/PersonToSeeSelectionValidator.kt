package com.bluewaterturtle.entrytabletapp.ui

sealed interface PersonToSeeSelectionResult {
    data class Success(val personToSee: String) : PersonToSeeSelectionResult
    data object MissingSelection : PersonToSeeSelectionResult
    data object InvalidSelection : PersonToSeeSelectionResult
    data object MissingCustomName : PersonToSeeSelectionResult
}

object PersonToSeeSelectionValidator {
    fun validate(
        selectedOption: String,
        customName: String,
        validNames: Set<String>,
        otherLabel: String
    ): PersonToSeeSelectionResult {
        val trimmedSelection = selectedOption.trim()
        if (trimmedSelection.isEmpty()) {
            return PersonToSeeSelectionResult.MissingSelection
        }

        if (trimmedSelection == otherLabel) {
            val trimmedCustomName = customName.trim()
            return if (trimmedCustomName.isEmpty()) {
                PersonToSeeSelectionResult.MissingCustomName
            } else {
                PersonToSeeSelectionResult.Success(trimmedCustomName)
            }
        }

        return if (validNames.contains(trimmedSelection)) {
            PersonToSeeSelectionResult.Success(trimmedSelection)
        } else {
            PersonToSeeSelectionResult.InvalidSelection
        }
    }
}
