package com.app.tts

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*


class DateTimeSchedule : AppCompatActivity() {
    private lateinit var scheduleDate : TextView
    private lateinit var scheduleTime: TextView

    private var month: Int = 0
    private var year: Int = 0
    private var day: Int = 0
    private var hour: Int = 0
    private var min: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_time_schedule)

        scheduleDate = findViewById(R.id.scheduleDate)
        scheduleTime = findViewById(R.id.scheduleTime)
    }

    @SuppressLint("SetTextI18n")
    fun DateHandler(v: View)
    {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(constraintsBuilder.build())
                .build().show(supportFragmentManager, "MATERIAL_DATE_PICKER")
    }

    @SuppressLint("SetTextI18n")
    fun TimeHandler(v:View)
    {
        val c = Calendar.getInstance()
        hour = c.get(Calendar.HOUR_OF_DAY)
        min = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { view, hourOfDay, minute -> scheduleTime.text = "$hourOfDay:$minute" },
            hour,
            min,
            false
        )
        timePickerDialog.show()
    }

    fun ScheduleNextHandler(v:View)
    {
        val ext : Bundle? = intent.extras
        val lat1 = ext?.getDouble("lat1")!!       // fetching the lat/lon passed from previous screens
        val lon1 = ext.getDouble("lon1")
        val lat2 = ext.getDouble("lat2")
        val lon2 = ext.getDouble("lon2")
        val alat1 = ext.getDouble("alat1")
        val alon1 = ext.getDouble("alon1")
        val alat2 = ext.getDouble("alat2")
        val alon2 = ext.getDouble("alon2")

        val intent = Intent(applicationContext,DetailsViewRoute::class.java)
        intent.putExtra("lon1",lon1)
        intent.putExtra("lat1",lat1)
        intent.putExtra("lon2",lon2)
        intent.putExtra("lat2",lat2)
        intent.putExtra("alat2",alat2)
        intent.putExtra("alat1",alat1)

        intent.putExtra("alon2",alon2)
        intent.putExtra("alon1",alon1)

        intent.putExtra("hour",hour)
        intent.putExtra("min",min)
        intent.putExtra("day",day)
        intent.putExtra("month",month)
        intent.putExtra("year",year)

        startActivity(intent)
    }
}