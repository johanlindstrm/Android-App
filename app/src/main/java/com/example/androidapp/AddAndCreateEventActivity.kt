package com.example.androidapp

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_and_create_date.*
import java.text.SimpleDateFormat
import java.util.*


class AddAndCreateEventActivity : AppCompatActivity() {

    private lateinit var eventTextName : EditText
    private lateinit var dateDiffText : TextView
    private lateinit var selectDateEdit : EditText
   // private val handler = Handler()


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_create_date)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.action_bar_add_event)


        selectDateEdit = findViewById(R.id.select_date_edit)
        eventTextName = findViewById(R.id.event_name_edit)
        dateDiffText = findViewById(R.id.time_left_text)

        getDateDifference()

        add_button.setOnClickListener {
            // Run the addNewDate function on button click
            addNewEvent()

        }

    }


    // Adds a new Event Title, Selected Date, "Time Diff" to the Event List and then finish() returns to the recyclerview
    private fun addNewEvent() {

        val selectedEvent = eventTextName.text.toString()
        val selectedDate = selectDateEdit.text.toString()
        val timeLeft = dateDiffText.text.toString()

        val newDate = Event(selectedDate, selectedEvent, timeLeft)
        DataManager.eventList.add(newDate)

        val db = FirebaseFirestore.getInstance()
        val events = Event(selectedDate,selectedEvent,timeLeft)

        db.collection("events")
            .add(events)
            .addOnSuccessListener { documentReference ->
                Log.d("log", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("log", "Error adding document", e)
            }

        finish()
    }

    /* TODO
    //COMPLETED
    Get the currentDate convert to timeInMillis to be able to get the diff later
    Get a futureDate from datepickerdialog/user input and convert to timeInMillis
    then get the diff
    val diff = futureMillis - currentMillis = the time diffrence bewtween the dates in milliseconds
    next step convert to days, hours, min, sec

    //NOT DONE
    final step have the diff count down the time!
     */

     private fun getDateDifference() {

        time_left_text.text = " "

        val currentDate = Calendar.getInstance()
        val eventDate = Calendar.getInstance()
        eventDate[Calendar.SECOND] = 1

        val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            eventDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            eventDate.set(Calendar.MONTH, monthOfYear)
            eventDate.set(Calendar.YEAR, year)

            var diff = eventDate.timeInMillis - currentDate.timeInMillis

            /*
            val daysLeft = diff / (24 * 60 * 60 * 1000)
            val hoursLeft = diff / (1000 * 60 * 60) % 24
            val minutesLeft = diff / (1000 * 60) % 60
            val secondsLeft = (diff / 1000) % 60
             */
            //val milliseconds = eventDate.timeInMillis

            var countDownTimer = object : CountDownTimer(diff, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    var difference = millisUntilFinished
                    val secondsInMilli : Long = 1000
                    val minutesInMilli = secondsInMilli * 60
                    val hoursInMilli = minutesInMilli * 60
                    val daysInMilli = hoursInMilli * 24

                    val elapsedDays = diff / daysInMilli
                    difference %= daysInMilli
                    val elapsedHours = difference / hoursInMilli
                    difference %= hoursInMilli
                    val elapsedMinutes = difference / minutesInMilli
                    difference %= minutesInMilli
                    val elapsedSeconds = difference / secondsInMilli

                    time_left_text.text = "$elapsedDays Days $elapsedHours Hours $elapsedMinutes Minutes $elapsedSeconds Seconds"
                }

                override fun onFinish() {
                    time_left_text.text = "Done"
                }
            }

            countDownTimer.start()


            //time_test_text.text = "$daysLeft Days $hoursLeft Hours $minutesLeft Minutes $secondsLeft Seconds"

            val myFormat = "yyyy.MM.dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
            selectDateEdit.setText(sdf.format(eventDate.time))

        }



        selectDateEdit.setOnClickListener {
           DatePickerDialog(this, dateSetListener, eventDate.get(Calendar.YEAR), eventDate.get(Calendar.MONTH), eventDate.get(Calendar.DAY_OF_MONTH)).show()
        }
        //Log.d("millis", "Calendar ${currentDate.timeInMillis} futureCal ${eventDate.timeInMillis}")

    }

}
