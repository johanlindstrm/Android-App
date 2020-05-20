package com.example.androidapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_add_and_create_date.*
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class AddAndCreateEventActivity : AppCompatActivity() {

    private lateinit var eventTextName : EditText
    //private lateinit var dateTextName : TextView
    private lateinit var timeLeftText : TextView
    private lateinit var selectDateEdit : EditText
    //private val handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_create_date)
        selectDateEdit = findViewById(R.id.select_date_edit)
        eventTextName = findViewById(R.id.event_name_edit)
        //dateTextName = findViewById(R.id.display_date_text)
        timeLeftText = findViewById(R.id.time_test_text)

        updateTime()

        // Update Time TextView every second, needed in main activity for all cardViews
        /* USESLESS AT THE MOMENT
        handler.post(object : Runnable {
            override fun run() {
                // Keep the postDelayed before the updateTime(), so when the event ends, the handler will stop too.
                handler.postDelayed(this, 1000)
                updateTime()
            }
        })*/

        add_button.setOnClickListener {

            addNewDate()
        }

    }

    private fun addNewDate() {

        val selectedEvent = eventTextName.text.toString()
        val selectedDate = selectDateEdit.text.toString()
        //val selectedDate = dateTextName.text.toString()
        val timeLeft = timeLeftText.text.toString()

        val newDate = Event(selectedDate, selectedEvent,timeLeft)

        DataManager.eventList.add(newDate)

        finish()
    }

    // Research Google Calendar/Calendar Contracts for setting events and start/end dates!
    private fun updateTime() {
        // Get the current date/time IRL
        val currentDate = Calendar.getInstance()
        //currentDate.timeZone = TimeZone.getTimeZone("GMT")
        /*
        val builder = MaterialDatePicker.Builder.dateRangePicker()

        val picker = builder.build()

        picker.show(supportFragmentManager, picker.toString())

        picker.addOnCancelListener {
            Log.d("DatePicker Activity", "Dialog was cancelled")
        }

        picker.addOnNegativeButtonClickListener {
            Log.d("DatePicker Activity", "Dialog Negative Button was clicked")
        }

        picker.addOnPositiveButtonClickListener {
            Log.d("DatePicker Activity", "Date String = ${picker.headerText}:: Date epoch value = ${it}")
        }

        picker.addOnPositiveButtonClickListener {
            Log.d("DatePicker Activity", "Date String = ${picker.headerText}::  Date epoch values::${it.first}:: to :: ${it.second}")
        }*/
        //Set a future date
        val c1 = Calendar.getInstance()
        val year = c1.get(Calendar.YEAR)
        val month = c1.get(Calendar.MONTH)
        val day = c1.get(Calendar.DAY_OF_MONTH)
        /*
        val hour = c1.get(Calendar.HOUR)
        val minute = c1.get(Calendar.MINUTE)
        val seconds = c1.get(Calendar.SECOND)*/
        c1[Calendar.HOUR] = 0
        c1[Calendar.MINUTE] = 0
        c1[Calendar.SECOND] = 0
        //c1.timeZone = TimeZone.getTimeZone("GMT")

        // How to get hours, minutes, seconds from dpd??
        select_date_edit.setOnClickListener {
            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view: DatePicker, mYear: Int, mMonth: Int, mDay: Int ->

                    selectDateEdit.setText("" + mYear +  "/" + (mMonth+1) + "/" + mDay)
                    Toast.makeText(this, "Date selected:" + mYear + "/" + (mMonth+1) + "/" + mDay, Toast.LENGTH_SHORT).show()
                    Log.d("test", "${c1.timeInMillis}")
                    Log.d("test", "${currentDate.timeInMillis}")

                },
                year,
                month,
                day
            )
            dpd.show()

        }

        // Cal the diff between the two Calenders, Need to get the hours, min, secs + update the time in main activity somehow
        val diff = currentDate.timeInMillis - c1.timeInMillis

        // Change the milliseconds to days, hours, minutes and seconds
        val daysLeft = diff / (24 * 60 * 60 * 1000)
        val hoursLeft = diff / (1000 * 60 * 60) % 24
        val minutesLeft = diff / (1000 * 60) % 60
        val secondsLeft = (diff / 1000) % 60

        timeLeftText.text = "${daysLeft} Days ${hoursLeft} Hours ${minutesLeft} Minutes ${secondsLeft} Seconds"

        Log.d("tag", "${daysLeft} Days ${hoursLeft} Hours ${minutesLeft }Minutes ${secondsLeft} Seconds")

    }


}
