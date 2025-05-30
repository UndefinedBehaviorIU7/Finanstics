package com.ub.finanstics.presentation.templates

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ub.finanstics.R

@Suppress("MagicNumber")
@Composable
fun LoadingContent() {
    BoxWithConstraints {
        val width = maxWidth
        Loader(
            modifier = Modifier
                .fillMaxSize()
                .padding(width / 3)
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun ErrorContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.connection_error),
            contentDescription = stringResource(R.string.connection_error),
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = stringResource(R.string.no_internet),
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.retry), fontSize = 22.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AvatarBitmap(
    image: Bitmap?,
    contentStr: String,
    modifier: Modifier,
    resource: Int
) {
    val painter = image?.asImageBitmap()?.let { BitmapPainter(it) }
        ?: painterResource(resource)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painter,
            contentDescription = contentStr,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun Divider(
    space: Dp,
    stroke: Dp,
    after: Dp = 10.dp,
    color: Color = MaterialTheme.colorScheme.secondary
) {
    Spacer(modifier = Modifier.height(space))
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = stroke,
        color = color
    )
    Spacer(modifier = Modifier.height(after))
}

@Composable
fun Loader(modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colorScheme.tertiary
    )
}
