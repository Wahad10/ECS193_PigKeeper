package com.example.pigkeeper

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlin.math.ceil

class TurnOrderActivity : AppCompatActivity() {
    var SitOut = false

    //Change this to get the names of the people from New Players Screen
    var Players = listOf("Jason","Sam","Peter","Luck","Avery","May","Tim","Judith","Garen","Donny")

    //Maps a players name to whether they are sitting out
    var SittingOut = mutableMapOf<String, Boolean>()

    //This is the turn order
    var Order = ArrayList<String>()
    lateinit var buttonSitOut : Button

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turn_order)

        buttonSitOut = findViewById<Button>(R.id.buttonSitOut)
        buttonSitOut.setOnClickListener{
            SitOut = !SitOut

            if(SitOut){
                buttonSitOut.text = "Set Order"
            }
            else{
                buttonSitOut.text = "Sit Out"
            }
        }

        val buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonStart.setOnClickListener{startActivity(Intent(this@TurnOrderActivity, MainActivity::class.java))}

        val unSelect = findViewById<ImageView>(R.id.unSelect)
        unSelect.setOnClickListener{
            Order.clear()
            var loop = 0
            for (i in Players){
                val button = findViewById<Button>(5+loop)
                button.text = button.tag as String
                loop++
            }
        }



        val layout = findViewById<ConstraintLayout>(R.id.activity_turn_order)

        var rows = ceil(Players.size/2.0).toInt()
        var columns = 2

        val horizontalSpacing = 16
        val verticalSpacing = 16

        val gapWidth = 300
        val gapHeight = 175

        //val buttonWidth
        //val buttonHeight

        var buttons = arrayOf<Button>()
        var id = 0
        for (r in 0..<rows){
            for (c in 0..<columns){

                if(Players.size == id){continue}

                var button = Button(this)
                button.text = Players[id]
                button.tag = Players[id]
                SittingOut.put(Players[id],false)
                button.id = id+5 //first few are already taken up
                button.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY))

                button.setOnClickListener{
                    if(SitOut){
                        setSitOut(button)
                    }
                    else {
                        setOrder(button)
                    }
                }

                layout.addView(button)
                id++

                val constraintSet = ConstraintSet()
                constraintSet.clone(layout)

                constraintSet.connect(button.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, horizontalSpacing + c * (gapWidth + horizontalSpacing)+250)
                //constraintSet.connect(button.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, horizontalSpacing + c * (buttonWidth + horizontalSpacing) + buttonWidth)
                constraintSet.connect(button.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, verticalSpacing + r * (gapHeight + verticalSpacing)+700)
                //constraintSet.connect(button.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, verticalSpacing + r * (buttonHeight + verticalSpacing) + buttonHeight)

                constraintSet.applyTo(layout)

            }
        }

    }

    //Will allow them to select the turn order
    private fun setOrder(button : Button){
        if(Order.contains(button.tag)){return}

        Order.add(button.tag as String)
        button.text = "${Order.size} ${button.tag}"

        Log.d("Order",Order.toString())
    }

    //Toggles person's sitting out status. Will also change the color of button
    //Status is found in the SittingOut Map
    private fun setSitOut(button : Button){
        if(SittingOut[button.tag]==false){
            button.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY))
            SittingOut[button.tag as String] = true
        }
        else{
            button.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY))
            SittingOut[button.tag as String] = false
        }
    }
}