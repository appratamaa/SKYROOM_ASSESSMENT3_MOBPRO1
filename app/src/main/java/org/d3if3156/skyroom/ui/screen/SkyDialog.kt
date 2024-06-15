package org.d3if3156.skyroom.ui.screen

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.d3if3156.skyroom.R
import org.d3if3156.skyroom.ui.theme.SKYROOMTheme

@Composable
fun SkyDialog(
    bitmap: Bitmap?,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit
) {
    val poppinsregular = FontFamily(Font(R.font.poppinsregular))

    var nama_rasibintang by remember { mutableStateOf("") }
    var daerah_langitdifoto by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
                OutlinedTextField(
                    value = nama_rasibintang,
                    onValueChange = { nama_rasibintang = it },
                    label = { Text(text = stringResource(id = R.string.nama_rasibintang),
                        fontFamily = poppinsregular) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = daerah_langitdifoto,
                    onValueChange = { daerah_langitdifoto = it },
                    label = { Text(text = stringResource(id = R.string.daerah_langitdifoto),
                        fontFamily = poppinsregular) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.batal),
                            fontFamily = poppinsregular)
                    }
                    OutlinedButton(
                        onClick = { onConfirmation(nama_rasibintang, daerah_langitdifoto) },
                        enabled = nama_rasibintang.isNotEmpty() && daerah_langitdifoto.isNotEmpty(),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.simpan),
                            fontFamily = poppinsregular)
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddDialogPreview() {
    SKYROOMTheme {
        SkyDialog(
            bitmap = null,
            onDismissRequest = {},
            onConfirmation = { _, _ -> }
        )
    }
}