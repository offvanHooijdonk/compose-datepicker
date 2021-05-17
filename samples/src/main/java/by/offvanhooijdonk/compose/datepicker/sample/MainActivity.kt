package by.offvanhooijdonk.compose.datepicker.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import by.offvanhooijdonk.compose.datepicker.DatePickerDialog
import by.offvanhooijdonk.compose.datepicker.sample.theme.ComposedatepickerTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposedatepickerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    DatePickerSamplesScreen()
                }
            }
        }
    }
}

@Composable
fun DatePickerSamplesScreen() {
    val isDialogShow = remember { mutableStateOf(true) }
    val textDate = remember { mutableStateOf("-") }

    if (isDialogShow.value) {
        DatePickerDialog(
            initialPickedDate = Date(),
            dateFrom = null,
            dateTo = null,
            onPick = { day, month, year ->
                textDate.value = "$day $month $year"
                isDialogShow.value = false
            },
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
        TextButton(onClick = { isDialogShow.value = true }) {
            Text(text = "Pick Date")
        }
        Text(modifier = Modifier.padding(top = 16.dp), text = textDate.value)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposedatepickerTheme {
        DatePickerSamplesScreen()
    }
}