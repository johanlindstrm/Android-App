package com.example.androidapp

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.DataManager.eventList
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var recyclerView: RecyclerView
    lateinit var emptyListText: TextView
    lateinit var deleteIcon: Drawable
    lateinit var mAuth: FirebaseAuth

    private var swipeBackground:ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.action_bar_layout)

        listIsEmptyText()

        val db = FirebaseFirestore.getInstance()

        val events = mutableListOf<Event>()

        val eventsRef = db.collection("events")

        eventsRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val newEvent = document.toObject(Event::class.java)
                    if (newEvent != null)
                        events.add(newEvent!!)
                    println("Johan : $newEvent")
                }
            }
        }



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


        mAuth = FirebaseAuth.getInstance()
        mAuth.currentUser

        viewAdapter = EventsRecyclerAdapter(this, eventList)
        recyclerView = findViewById(R.id.datesListView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EventsRecyclerAdapter(this, eventList)
        deleteIcon = ContextCompat.getDrawable(this,R.drawable.ic_delete_white_24dp)!!

        // Floating Action Button takes you to the Event Add activity
        val fab = findViewById<View>(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(this, AddAndCreateEventActivity::class.java)
            startActivity(intent)
        }

        // using ItemTouchHelper allows the touching of objects moving and swipe them
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            // Fungerar som den ska nu, hämtar tillbaka den senaste deleted. Gjort snackbar till en egen variable som går att customize.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {

                var position = viewHolder.adapterPosition
                var snackbar = Snackbar.make(viewHolder.itemView, "Event deleted",Snackbar.LENGTH_LONG)

                snackbar.setAction("UNDO") {
                    (viewAdapter as EventsRecyclerAdapter).restoreItem()
                    (recyclerView.adapter as EventsRecyclerAdapter).notifyItemInserted(position)
                    recyclerView.scrollToPosition(position)
                }

                snackbar.setActionTextColor(Color.parseColor("#FFB60B"))
                snackbar.show()

                (viewAdapter as EventsRecyclerAdapter).removeEvent(viewHolder)
                (recyclerView.adapter as EventsRecyclerAdapter).notifyItemRemoved(viewHolder.adapterPosition)
            }


            //Draws the red fill background on swipe motion and the delete icon
            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val backgroundCornerOffset = 20

                val iconMargin: Int = (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconTop: Int =
                    itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconBottom: Int = iconTop + deleteIcon.intrinsicHeight

                /* Saving in case I want to make an edit swipe or somethinh
                if (dX > 0) { // Swiping to the right
                    val iconLeft: Int = itemView.left + iconMargin + deleteIcon.getIntrinsicWidth()
                    val iconRight = itemView.left + iconMargin
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    swipeBackground.setBounds(
                        itemView.left, itemView.top,
                        itemView.left + dX.toInt() + backgroundCornerOffset,
                        itemView.bottom
                    ) }*/

                if (dX < 0) { // Swiping to the left
                    val iconLeft: Int = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    swipeBackground.setBounds(
                        itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top, itemView.right, itemView.bottom
                    )
                } else { // view is unSwiped
                    swipeBackground.setBounds(0, 0, 0, 0)
                }

                swipeBackground.draw(c)
                deleteIcon.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }
    //auth anonomys
    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            (currentUser)
        } else {
            mAuth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        user?.let { (it) }
                    } else {
                        Toast.makeText(this@MainActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        listIsEmptyText()
        recyclerView.adapter?.notifyDataSetChanged()

    }

    private fun listIsEmptyText() {
        emptyListText = findViewById(R.id.empty_list_text)

        if (eventList.size <= 0) {
            emptyListText.text = "List is empty, please add a new event!"
            Log.d("test", "list is empty")
        } else {
            emptyListText.text = " "
            Log.d("test", "list is not empty")

        }
    }

}
