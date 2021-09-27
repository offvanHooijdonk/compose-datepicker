package by.offvanhooijdonk.compose.datepicker.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.offvanhooijdonk.compose.datepicker.dialog.DatePickerDialog
import by.offvanhooijdonk.compose.datepicker.dialog.DatePickerSettings
import by.offvanhooijdonk.compose.datepicker.sample.ext.toDateString
import by.offvanhooijdonk.compose.datepicker.sample.ui.theme.ComposeDatePickerTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDatePickerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    SamplesScreen()
                }
            }
        }
    }
}

@Composable
fun SamplesScreen() {
    val isHeaderColorPrimary = remember { mutableStateOf(false) }
    val isDialogShow = remember { mutableStateOf(false) }
    val datePicked = remember { mutableStateOf(LocalDate.now()) }
    val textDate = remember(datePicked.value) { mutableStateOf(datePicked.value.toDateString()) }

    if (isDialogShow.value) {
        DatePickerDialog(
            initialPickedDate = datePicked.value,
            dateTo = null,
            onPick = { date ->
                datePicked.value = date
                isDialogShow.value = false
            },
            settings = DatePickerSettings.builder().run {
                if (isHeaderColorPrimary.value) headerColorPrimary() else headerColorSurface()
            }.build(),
            onDismissRequest = {
                isDialogShow.value = false
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Text(text = "Header color primary")
            Spacer(modifier = Modifier.width(16.dp))
            Switch(checked = isHeaderColorPrimary.value, onCheckedChange = {
                isHeaderColorPrimary.value = !isHeaderColorPrimary.value
            })
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { isDialogShow.value = true }) {
            Text(text = "Pick Date")
        }
        Text(modifier = Modifier.padding(top = 16.dp), text = textDate.value)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeDatePickerTheme {
        SamplesScreen()
    }
}