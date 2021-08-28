package by.offvanhooijdonk.compose.datepicker.ext

import androidx.constraintlayout.compose.ConstrainScope

internal fun ConstrainScope.parentAll() {
    top.linkTo(parent.top)
    end.linkTo(parent.end)
    bottom.linkTo(parent.bottom)
    start.linkTo(parent.start)
}