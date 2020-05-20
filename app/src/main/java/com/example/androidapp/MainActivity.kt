package com.example.androidapp

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.DataManager.eventList
import com.google.android.material.snackbar.Snackbar




class MainActivity : AppCompatActivity() {

    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var recyclerView: RecyclerView
    lateinit var emptyListText: TextView
    lateinit var deleteIcon: Drawable

    private var swipeBackground:ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //listIsEmptyText()

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

            //Delete works and undo feature now updates in the recyclerview however it does no longer bring back the CORRECT item or the position..
            // Hämtar nu tillbaka den man tog senast bort.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {

                var position = viewHolder.adapterPosition
                var snackbar = Snackbar.make(viewHolder.itemView, "Event deleted",Snackbar.LENGTH_LONG)

                snackbar.setAction("UNDO") {
                    (viewAdapter as EventsRecyclerAdapter).restoreItem(viewHolder)
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

                val iconMargin: Int = (itemView.height - deleteIcon.getIntrinsicHeight()) / 2
                val iconTop: Int =
                    itemView.top + (itemView.height - deleteIcon.getIntrinsicHeight()) / 2
                val iconBottom: Int = iconTop + deleteIcon.getIntrinsicHeight()

                /* Saving in case i want to make a edit swipe
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
                    val iconLeft: Int = itemView.right - iconMargin - deleteIcon.getIntrinsicWidth()
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


    override fun onResume() {
        super.onResume()
        recyclerView.adapter?.notifyDataSetChanged()
    }





    /*
    private fun listIsEmptyText() {
        emptyListText = findViewById(R.id.empty_list_text)

        if (recyclerView.datesListView == null) {
            emptyListText.text = "List is empty, please add a new event!"
            Log.d("test", "list is empty")
        } else {
            emptyListText.text = null
            Log.d("test", "list is not empty")

        }

    }*/

}
