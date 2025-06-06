package com.ub.finanstics.presentation.userScreens.actionView

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ub.finanstics.R
import com.ub.finanstics.api.models.Action
import com.ub.finanstics.presentation.templates.AvatarBitmap
import com.ub.finanstics.presentation.templates.Divider
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun formatDate(inputDate: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    val date = LocalDate.parse(inputDate, inputFormatter)
    return date.format(outputFormatter)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("MagicNumber", "LongMethod", "LongParameterList")
fun ApiActionView(
    action: Action,
    category: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    color: Color,
    name: String = "",
    imageBitmap: Bitmap? = null,
    modifier: Modifier
) {
    if (isVisible) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = modifier.clickable { },
                    tonalElevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.onBackground
                ) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = action.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Divider(
                            space = 10.dp,
                            after = 0.dp,
                            stroke = 1.dp,
                            color = MaterialTheme.colorScheme.background
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.background)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleLarge,
                                color = color,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = action.value.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = formatDate(action.date),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(10.dp))

                            if (action.description != null) {
                                Text(
                                    text = action.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    maxLines = 3
                                )
                                Spacer(Modifier.height(10.dp))
                            }

                            if (name.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AvatarBitmap(
                                        image = imageBitmap,
                                        contentStr = stringResource(R.string.user_picture),
                                        modifier = Modifier.size(25.dp),
                                        resource = R.drawable.profile_placeholder
                                    )

                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                            }

                            Row(verticalAlignment = Alignment.Bottom) {
                                TextButton(
                                    onClick = { onDismiss() },
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.onBackground,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 10.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.step_back),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
