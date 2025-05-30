package com.ub.finanstics.converters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Suppress("MagicNumber")
fun bitmapToBase64(bitmap: Bitmap?): String {
    if (bitmap == null) return ""
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

@Suppress("MagicNumber")
fun base64ToBitmap(base64Str: String): Bitmap? {
    if (base64Str.isEmpty()) return null
    val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun createMultipartBodyPart(context: Context, uri: Uri): MultipartBody.Part? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null

    val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { output ->
        inputStream.copyTo(output)
    }

    val requestFile = file
        .asRequestBody("image/jpeg".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        name = "image",
        filename = file.name,
        body = requestFile
    )
}

@Suppress("MagicNumber")
fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}
