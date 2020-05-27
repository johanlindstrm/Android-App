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
import java.time.Duration
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.*

class AddAndCreateEventActivity : AppCompatActivity() {

    private lateinit var eventTextName : EditText
    private lateinit var timeLeftText : TextView
    private lateinit var selectDateEdit : EditText

    private val handler = Handler()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_create_date)
        selectDateEdit = findViewById(R.id.select_date_edit)
        eventTextName = findViewById(R.id.event_name_edit)
        timeLeftText = findViewById(R.id.time_test_text)

        updateTime()

        // Update Time TextView every second, needed in main activity for all cardViews
        /*
        handler.post(object : Runnable {
            override fun run() {
                // Keep the postDelayed before the updateTime(), so when the event ends, the handler will stop too.
                handler.postDelayed(this, 1000)
                updateTime()
            }
        })
         */

        add_button.setOnClickListener {
            // Run the addNewDate function on button click
            addNewDate()

        }

    }
    // Adds a new Event Title, Selected Date, "Time Diff" to the Event List and then finish() returns to the recyclerview
    private fun addNewDate() {

        val selectedEvent = eventTextName.text.toString()
        val selectedDate = selectDateEdit.text.toString()
        val timeLeft = timeLeftText.text.toString()

        val newDate = Event(selectedDate, selectedEvent,timeLeft)

        DataManager.eventList.add(newDate)

        finish()
    }

    /* TODO
    Get the currentDate convert to timeInMillis to be able to get the diff later

    Get a futureDate from datepickerdialog/user input and convert to timeInMillis

    then get the diff
    val diff = futureMillis - currentMillis = the time diffrence bewtween the dates in milliseconds

    next step convert to days, hours, min, sec

    final step update the countdown





     */


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun updateTime() {
        /*
        // Get the current date/time IRL
        val currentDate = Calendar.getInstance()
        val currentInMills = currentDate.timeInMillis
        //currentDate.timeZone = TimeZone.getTimeZone("GMT")
        Log.d("calendar", "$currentInMills Time in millis")

        val futureDate = Calendar.getInstance()
        futureDate.add(Calendar.DATE,5)
        val futureInMillis = futureDate.timeInMillis
        Log.d("calendar", "$futureInMillis Time in Millis")

        // Cal the diff between the two Calenders, Need to get the hours, min, secs + update the time in main activity somehow
        val diff = futureInMillis - currentInMills

        // Change the milliseconds to days, hours, minutes and seconds
        val daysLeft = diff / (24 * 60 * 60 * 1000)
        val hoursLeft = diff / (1000 * 60 * 60) % 24
        val minutesLeft = diff / (1000 * 60) % 60
        val secondsLeft = (diff / 1000) % 60

        Log.d("calendar", "$daysLeft Days $hoursLeft Hours $minutesLeft Minutes $secondsLeft Seconds")
         */

        //Set a future date, this is currently selected date
        val c1 = Calendar.getInstance()
        val year = c1.get(Calendar.YEAR)
        val month = c1.get(Calendar.MONTH)
        val day = c1.get(Calendar.DAY_OF_MONTH)
        /*
        c1[Calendar.HOUR] = 0
        c1[Calendar.MINUTE] = 0
        c1[Calendar.SECOND] = 0
         */

        c1.timeZone = TimeZone.getTimeZone("GMT+2")

        select_date_edit.setOnClickListener {
            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view: DatePicker, mYear: Int, mMonth: Int, mDay: Int ->

                    selectDateEdit.setText("" + mYear +  "/" + (mMonth+1) + "/" + mDay)
                    Toast.makeText(this, "Date selected:" + mYear + "/" + (mMonth+1) + "/" + mDay, Toast.LENGTH_SHORT).show()
                },
                year,
                month,
                day
            )
            dpd.show()

        }
        /*

        val diffBetween = Duration.between(c1.toInstant(),currentDate.toInstant())

        Log.d("test", "$diffBetween")

        // Cal the diff between the two Calenders, Need to get the hours, min, secs + update the time in main activity somehow
        val diff = currentDate.timeInMillis - c1.timeInMillis

        // Change the milliseconds to days, hours, minutes and seconds
        val daysLeft = diff / (24 * 60 * 60 * 1000)
        val hoursLeft = diff / (1000 * 60 * 60) % 24
        val minutesLeft = diff / (1000 * 60) % 60
        val secondsLeft = (diff / 1000) % 60

        timeLeftText.text = "$daysLeft Days $hoursLeft Hours $minutesLeft Minutes $secondsLeft Seconds"

        Log.d("tag", "$daysLeft Days $hoursLeft Hours $minutesLeft Minutes $secondsLeft Seconds")

 */

    }


}
