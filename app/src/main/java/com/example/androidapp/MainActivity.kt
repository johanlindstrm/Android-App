package com.example.androidapp

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*


class MainActivity : AppCompatActivity() {

    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var recyclerView: RecyclerView
    private lateinit var emptyListText: TextView
    private lateinit var deleteIcon: Drawable
    private lateinit var mAuth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()
    private val reference = db.collection("events")

    val handler = Handler()
    private var swipeBackground:ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Custom actionbar display
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.action_bar_layout)

        listIsEmptyText()
        /*
        handler.post(object : Runnable {
            override fun run() {
                // Keep the postDelayed before the updateTime(), so when the event ends, the handler will stop too.
                viewAdapter.notifyDataSetChanged()

                handler.postDelayed(this, 60*1000)
            }
        })
         */

        mAuth = FirebaseAuth.getInstance()
        mAuth.currentUser

        viewAdapter = EventsRecyclerAdapter(this, DataManager.eventList)

        recyclerView = findViewById(R.id.datesListView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = EventsRecyclerAdapter(this, DataManager.eventList)


        val eventItems = DataManager.eventList
        reference.get().addOnSuccessListener { documentSnapshot ->
            for (document in documentSnapshot.documents) {
                val newEvent = document.toObject(Event::class.java)
                if (newEvent != null)
                    eventItems.add(newEvent!!)
                Log.d("test", document.id)
            }
            recyclerView.adapter?.notifyDataSetChanged()
        }

        // Floating Action Button takes you to the Event Add activity
        val fab = findViewById<View>(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(this, AddAndCreateEventActivity::class.java)
            startActivity(intent)
        }

        deleteIcon = ContextCompat.getDrawable(this,R.drawable.ic_delete_white_24dp)!!

        // using ItemTouchHelper allows the touching of objects moving/swipe them
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

                /*
                val id : String = reference.document().collection("events")
                    .document().id


                db.collection("events").document(id)
                    .delete()
                    .addOnSuccessListener{
                        Log.d("TAG","DocumentSnapshot successfully deleted!")
                    }
                    .addOnSuccessListener{
                        Log.w("TAG","Error deleting document")
                    }

                        */

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

                /* Saving in case I want to make an edit swipe or something
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


    //auth anonymous
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

        if (DataManager.eventList.size <= 0) {
            emptyListText.text = "List is empty, please add a new event!"
            Log.d("list", "list is empty")
        } else {
            emptyListText.text = " "
            Log.d("list", "list is not empty")

        }
    }

}
