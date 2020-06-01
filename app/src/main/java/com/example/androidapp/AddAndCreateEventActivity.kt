package com.example.androidapp

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_and_create_date.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class AddAndCreateEventActivity : AppCompatActivity() {

    private lateinit var eventTextName : EditText
    private lateinit var timeLeftText : TextView
    private lateinit var selectDateEdit : EditText
    lateinit var auth : FirebaseAuth
    private val handler = Handler()


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_create_date)
        //database = Firebase.database.reference


        val db = FirebaseFirestore.getInstance()
        db.collection("events")

        // Create a new user with a first and last name
        /*
        val user: MutableMap<String, Any> = HashMap()
        user["first"] = "Ada"
        user["last"] = "Lovelace"
        user["born"] = 1815


        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference -> Log.d("test", "DocumentSnapshot added with ID: " + documentReference.id) }
            .addOnFailureListener { e -> Log.w("test", "Error adding document", e) }

         */


        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.action_bar_add_event)

        //auth = FirebaseAuth.getInstance()

        selectDateEdit = findViewById(R.id.select_date_edit)
        eventTextName = findViewById(R.id.event_name_edit)
        timeLeftText = findViewById(R.id.time_test_text)
        getDateDifference()

        /*
        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed(this, 1000)
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
    //COMPLETED
    Get the currentDate convert to timeInMillis to be able to get the diff later
    Get a futureDate from datepickerdialog/user input and convert to timeInMillis
    then get the diff
    val diff = futureMillis - currentMillis = the time diffrence bewtween the dates in milliseconds
    next step convert to days, hours, min, sec

    //NOT DONE
    final step update the countdown
     */

    private fun getDateDifference() {

        //selectDateEdit.setText(SimpleDateFormat("yyyy.MM.dd").format(System.currentTimeMillis()))
        time_test_text.text = " "

        val currentDate = Calendar.getInstance()
        val eventDate = Calendar.getInstance()
        eventDate[Calendar.SECOND] = 1

        val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            eventDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            eventDate.set(Calendar.MONTH, monthOfYear)
            eventDate.set(Calendar.YEAR, year)

            val diff = eventDate.timeInMillis - currentDate.timeInMillis
            val daysLeft = diff / (24 * 60 * 60 * 1000)
            val hoursLeft = diff / (1000 * 60 * 60) % 24
            val minutesLeft = diff / (1000 * 60) % 60
            val secondsLeft = (diff / 1000) % 60

            time_test_text.text = "$daysLeft Days $hoursLeft Hours $minutesLeft Minutes $secondsLeft Seconds"

            val myFormat = "yyyy.MM.dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
            selectDateEdit.setText(sdf.format(eventDate.time))

        }

        selectDateEdit.setOnClickListener {
           DatePickerDialog(this, dateSetListener, eventDate.get(Calendar.YEAR), eventDate.get(Calendar.MONTH), eventDate.get(Calendar.DAY_OF_MONTH)).show()
        }
        Log.d("millis", "Calendar ${currentDate.timeInMillis} futureCal ${eventDate.timeInMillis}")

    }

}
