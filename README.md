# compose-datepicker
Started a library to implement DatePicker widget in Jetpack Compose for Android.

Based on Google's Accompanist Pager library, see https://github.com/google/accompanist

Latest version:
```implementation 'io.github.offvanhooijdonk:compose-date-picker:0.1.0-alpha2'```

<img src="https://github.com/offvanHooijdonk/compose-datepicker/blob/main/docs/current-month.png" width="225"/><img src="https://github.com/offvanHooijdonk/compose-datepicker/blob/main/docs/years-layout.png" width="225"/><img src="https://github.com/offvanHooijdonk/compose-datepicker/blob/main/docs/other-year-month.png" width="225"/>

Note: uses  java.time API with desugaring enabled.

⚠️ 🪲 Known bugs:
* When scrolling to next month, picking new date, scrolling to previous month - previous date mark persists, [see issue](https://github.com/offvanHooijdonk/compose-datepicker/issues/9). This is due to Pager library implementation, which is expected to be fixed shortly in Accompanist Pager library - see Bug Report at https://github.com/google/accompanist/issues/706

Implemented ✅ :
* Provides: Picker Dialog, Picker Pager, Layout Picker Layout.
* Set dateTo, dateFrom - both optional - and receive picked date.
* Current MaterialTheme applies.

Upcoming 🚀 :
* Unit & tests
* UI adjustments
* Time Picker, Date Range picker, Date Input, Time Input

Feel free to fork/use/pull request/whatever.

Bug Reports are appreciated, Feature Requests are welcomed 🤝
