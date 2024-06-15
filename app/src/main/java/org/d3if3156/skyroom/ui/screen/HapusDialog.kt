package org.d3if3156.skyroom.ui.screen

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import org.d3if3156.skyroom.R
import org.d3if3156.skyroom.ui.theme.SKYROOMTheme

@Composable
fun HapusDialog(
    openDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    val poppinsregular = FontFamily(Font(R.font.poppinsregular))

    if (openDialog) {
        AlertDialog(
            text = { Text(text = stringResource(R.string.hapus_data),
                fontFamily = poppinsregular) },
            confirmButton = {
                TextButton(onClick = { onConfirmation() }) {
                    Text(text = stringResource(R.string.hapus),
                        fontFamily = poppinsregular)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = stringResource(R.string.batal),
                        fontFamily = poppinsregular)
                }
            },
            onDismissRequest = { onDismissRequest() }
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HapusPreview() {
    SKYROOMTheme {
        HapusDialog(
            openDialog = true,
            onDismissRequest = {},
            onConfirmation = {}
        )
    }
}