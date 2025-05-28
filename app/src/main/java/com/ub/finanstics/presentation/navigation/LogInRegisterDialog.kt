package com.ub.finanstics.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ub.finanstics.R
import com.ub.finanstics.ui.theme.icons.GroupsIcon

@Composable
@Suppress("MagicNumber", "LongMethod")
fun LogInRegisterDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onLoginPressed: () -> Unit,
    onRegisterPressed: () -> Unit,
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
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = modifier,
                    tonalElevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.onBackground
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = GroupsIcon,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(12.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.need_auth),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.authorisation_warning),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(12.dp))

                        Row {
                            TextButton(
                                onClick = {
                                    onLoginPressed()
                                },
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.log_in),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(Modifier.width(20.dp))

                            TextButton(
                                onClick = { onRegisterPressed() },
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.register),
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
