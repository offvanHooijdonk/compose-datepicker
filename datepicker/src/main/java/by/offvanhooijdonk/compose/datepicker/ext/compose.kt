package by.offvanhooijdonk.compose.datepicker.ext

import androidx.annotation.IntegerRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ConstrainScope

internal fun ConstrainScope.parentAll() {
    top.linkTo(parent.top)
    end.linkTo(parent.end)
    bottom.linkTo(parent.bottom)
    start.linkTo(parent.start)
}

@Composable
internal fun getInt(@IntegerRes resId: Int): Int = LocalContext.current.resources.getInteger(resId)