package com.nexters.boolti.presentation.extension

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

fun Uri.getFullPath(context: Context): String {
    var fullPath = ""
    context.contentResolver.query(this, null, null, null, null)?.let { cursor ->
        cursor.moveToFirst()
        cursor.getStringOrNull(0)?.let { documentId ->
            val docId = documentId.substring(documentId.lastIndexOf(':') + 1)
            cursor.close()
            val projection = arrayOf("_data")
            try {
                context.contentResolver.query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Media._ID + "=?",
                    arrayOf(docId),
                    null,
                )?.let { cursor ->
                    cursor.moveToFirst()
                    fullPath = cursor.getString(cursor.getColumnIndexOrThrow("_data"))
                }
            } finally {

            }
        } ?: run {
            repeat(cursor.columnCount) {
                if ("_data".contentEquals(cursor.getColumnName(it))) {
                    fullPath = cursor.getString(it)
                    return@repeat
                }
            }
        }
    }
    return fullPath
}

fun Uri.toRequestBody(context: Context): RequestBody {
    val contentResolver = context.contentResolver
    var fileName = ""
    var size = -1L

    contentResolver.query(
        this,
        arrayOf(MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME),
        null,
        null,
        null,
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
            fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
        }
    }

    return object : RequestBody() {
        override fun contentType(): MediaType? = contentResolver.getType(this@toRequestBody)?.toMediaType()

        override fun writeTo(sink: BufferedSink) {
            contentResolver.openInputStream(this@toRequestBody)?.source()?.use { source ->
                sink.writeAll(source)
            }
        }

        override fun contentLength(): Long = size
    }
}
