package com.example.pigkeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView

class RollActivity : AppCompatActivity() {
    private var selectedLeftDice: Int = 0
    private var selectedRightDice: Int = 0
    private var selectedScore: Int = 0
    private var currentPlayer: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll)

        val globalVariable = GlobalData.instance
        val players = globalVariable.players
        val sittingOut = globalVariable.sittingOut

        //Idk if you need this
        val score = globalVariable.score


        val textYourScore = findViewById<TextView>(R.id.textYourScore)
        val textTopScore = findViewById<TextView>(R.id.textTopScore)


        val topCard = findViewById<CardView>(R.id.cardTop)
        topCard.setOnClickListener{startActivity(Intent(this@RollActivity, MainActivity::class.java))}


        val leftDiceButtons = listOf<ImageButton>(
            findViewById(R.id.imageButtonL1),
            findViewById(R.id.imageButtonL2),
            findViewById(R.id.imageButtonL3),
            findViewById(R.id.imageButtonL4),
            findViewById(R.id.imageButtonL5),
            findViewById(R.id.imageButtonL6)
        )


        val rightDiceButtons = listOf<ImageButton>(
            findViewById(R.id.imageButtonR1),
            findViewById(R.id.imageButtonR2),
            findViewById(R.id.imageButtonR3),
            findViewById(R.id.imageButtonR4),
            findViewById(R.id.imageButtonR5),
            findViewById(R.id.imageButtonR6)
        )


        val badRoll = findViewById<ImageButton>(R.id.imageButtonBadRoll)
        val undoRoll = findViewById<ImageButton>(R.id.imageButtonUndoRoll)
        val nextRoll = findViewById<ImageButton>(R.id.imageButtonNextRoll)


        //need to modify this to take in the list of players in the right turn order from turn order screen
        val namesArray = ArrayList<String>()
        namesArray.add("Jason")
        namesArray.add("Tim")
        namesArray.add("Sam")
        currentPlayer = namesArray[0]


        var nameToScore = HashMap<String, Int>()
        for (name in namesArray){
            nameToScore[name] = 0
        }




        for (i in 1..6) {
            leftDiceButtons[i - 1].setOnClickListener {
                print("here?")
                selectedLeftDice = i
                updateDiceButtonSelection(leftDiceButtons, i)
                updateScore()
            }


            rightDiceButtons[i - 1].setOnClickListener {
                selectedRightDice = i
                updateDiceButtonSelection(rightDiceButtons, i)
                updateScore()
            }
        }


        /**nextRoll.setOnClickListener {
        nameToScore[currentPlayer] = selectedScore


        //find next player in namesArray and save it to currentPlayer
        val currentPlayerIndex = namesArray.indexOf(currentPlayer)
        currentPlayer = if (currentPlayerIndex < namesArray.size - 1) {
        namesArray[currentPlayerIndex + 1]
        } else {
        namesArray[0]
        }
        selectedScore = 0
        updateScore()
        }**/
    }


    private fun updateDiceButtonSelection(buttons: List<ImageButton>, selectedDice: Int) {
        for (button in buttons) {
            button.isPressed = false
        }
        buttons[selectedDice - 1].isPressed = true
    }


    private fun updateScore() {
        selectedScore = selectedLeftDice + selectedRightDice
        Log.d("me", "here")
        Log.d("me", selectedScore.toString())
    }
}