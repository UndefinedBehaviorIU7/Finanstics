package com.ub.finanstics.presentation.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

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

