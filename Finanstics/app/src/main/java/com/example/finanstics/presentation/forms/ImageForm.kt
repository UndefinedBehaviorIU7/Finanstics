package com.example.finanstics.presentation.forms

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finanstics.R

@Composable
fun ImageForm(imageUri: Uri?, lambda: () -> Unit, text: String) {
    Text(
        text,
        fontSize = 20.sp,
        color = Color.DarkGray,
        modifier = Modifier.padding(bottom = 10.dp)
    )

    Box (contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        if (imageUri != null) {
            Image(
                painter = painterResource(id = R.drawable.placeholder),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
            TODO("Add image loading")
        } else {
            Image(
                painter = painterResource(id = R.drawable.placeholder),
                contentDescription = "Image Placeholder",
                alignment = Alignment.Center,
                modifier = Modifier.size(150.dp)
                    .clickable(onClick = lambda)
            )
        }
    }
}
