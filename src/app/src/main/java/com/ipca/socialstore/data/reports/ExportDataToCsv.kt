package com.ipca.socialstore.data.reports

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

fun exportDataToCsv(
    context: Context,
    fileName: String,
    header: String,
    data: List<String>
) {
    try {
        val file = File(context.cacheDir, "$fileName.csv")
        file.printWriter().use { out ->
            out.println(header)
            data.forEach { line ->
                out.println(line)
            }
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Partilhar Relatório"))

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
