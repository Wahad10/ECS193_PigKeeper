package com.ucdavis.pigkeeper

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TurnOrderActivity : AppCompatActivity() {
    var sitOut = false
    lateinit var players: ArrayList<String>

    // Maps a player's name to whether they are sitting out
    var sittingOut = mutableMapOf<String, Boolean>()
    var sittingOutSize = 0

    // This is the turn order
    var order = ArrayList<String>()
    lateinit var buttonSitOut: Button

    lateinit var globalVariable: GlobalData
    lateinit var message1: TextView
    lateinit var message2: TextView
    lateinit var message3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        globalVariable = GlobalData.instance

        players = globalVariable.players

        setContentView(R.layout.activity_turn_order)

        buttonSitOut = findViewById<Button>(R.id.buttonSitOut)
        buttonSitOut.setOnClickListener {
            sitOut = !sitOut
            buttonSitOut.text = if (sitOut) "Set Order" else "Sit Out"
        }

        message1 = findViewById<TextView>(R.id.Message1)
        message2 = findViewById<TextView>(R.id.Message2)
        message3 = findViewById<TextView>(R.id.Message3)

        val buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonStart.setOnClickListener {
            if (order.size + sittingOutSize != players.size) {
                message3.visibility = View.VISIBLE
            } else {
                globalVariable.players = order
                globalVariable.sittingOut = sittingOut
                startActivity(Intent(this@TurnOrderActivity, RollActivity::class.java))
            }
        }

        val unSelect = findViewById<ImageView>(R.id.unSelect)
        unSelect.setOnClickListener {
            order.clear()
            for (i in 0 until players.size) {
                val button = findViewById<Button>(resources.getIdentifier("person$i", "id", packageName))
                button.text = button.tag as String
            }
            updateStartButtonState()
        }

        setupPlayerButtons()
    }

    private fun setupPlayerButtons() {
        val maxPlayers = 10 // Assuming there are 10 buttons (person0 to person9)
        for (i in 0 until maxPlayers) {
            val buttonId = resources.getIdentifier("person$i", "id", packageName)
            val button = findViewById<Button>(buttonId)
            button.visibility = View.INVISIBLE
        }

        for (i in players.indices) {
            val buttonId = resources.getIdentifier("person$i", "id", packageName)
            val button = findViewById<Button>(buttonId)
            button.visibility = View.VISIBLE
            button.text = players[i]
            button.tag = players[i]
            sittingOut[players[i]] = false

            // Automatically set last round winning player as 1st in turn order if applicable
            if (!globalVariable.endedGameSession && globalVariable.endedGameRound && globalVariable.endingPlayer == players[i]) {
                setOrder(button)
            }

            button.setOnClickListener {
                if (sitOut) {
                    setSitOut(button)
                } else {
                    setOrder(button)
                }
                updateStartButtonState()
            }
        }
        updateStartButtonState()
    }

    private fun setOrder(button: Button) {
        // Return if they already have an order OR they are sitting out
        if (order.contains(button.tag)) return
        if (sittingOut[button.tag] == true) {
            message1.visibility = View.VISIBLE
            return
        }

        message1.visibility = View.INVISIBLE
        message2.visibility = View.INVISIBLE
        message3.visibility = View.INVISIBLE
        order.add(button.tag as String)
        button.text = "${order.size} ${button.tag}"
    }

    private fun setSitOut(button: Button) {
        if (order.contains(button.tag)) {
            message2.visibility = View.VISIBLE
            return
        }

        message1.visibility = View.INVISIBLE
        message2.visibility = View.INVISIBLE
        message3.visibility = View.INVISIBLE
        if (sittingOut[button.tag] == false) {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY))
            sittingOut[button.tag as String] = true
            sittingOutSize += 1
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3b444b")))
            sittingOut[button.tag as String] = false
            sittingOutSize -= 1
        }
    }

    private fun updateStartButtonState() {
        val buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonStart.isEnabled = order.size + sittingOutSize == players.size
    }

    override fun onPause() {
        super.onPause()
        globalVariable.saveData()
    }
}
