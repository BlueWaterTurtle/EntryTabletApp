package com.bluewaterturtle.entrytabletapp.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.bluewaterturtle.entrytabletapp.data.GuestEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    private val displayFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())

    /**
     * Sanitize a CSV cell value to prevent CSV injection in Excel/Google Sheets.
     * If a value starts with =, +, -, or @, prefix with a single quote.
     */
    private fun sanitize(value: String): String {
        return if (value.isNotEmpty() && value[0] in listOf('=', '+', '-', '@')) {
            "'$value"
        } else {
            value
        }
    }

    private fun formatTimestamp(epochMillis: Long?): String {
        if (epochMillis == null) return ""
        return displayFormat.format(Date(epochMillis))
    }

    fun exportAndShare(context: Context, guests: List<GuestEntity>) {
        val sb = StringBuilder()
        sb.appendLine("Name,Person to See,Sign In Time,Sign Out Time")
        for (guest in guests) {
            val name = sanitize(guest.name)
            val personToSee = sanitize(guest.personToSee)
            val signIn = sanitize(formatTimestamp(guest.signInTime))
            val signOut = sanitize(formatTimestamp(guest.signOutTime))
            sb.appendLine("\"$name\",\"$personToSee\",\"$signIn\",\"$signOut\"")
        }

        val fileName = "guest_log_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        FileWriter(file).use { it.write(sb.toString()) }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Export Guest Log"))
    }
}
