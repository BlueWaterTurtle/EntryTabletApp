package com.bluewaterturtle.entrytabletapp.data

object PersonToSeeInputSanitizer {
    fun sanitizeDisplayName(value: String): String {
        return value.trim().replace("\\s+".toRegex(), " ")
    }
}
