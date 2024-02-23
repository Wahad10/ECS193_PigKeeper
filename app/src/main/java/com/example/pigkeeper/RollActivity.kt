package com.example.pigkeeper

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView

class RollActivity : AppCompatActivity() {
    //Necessary data from other screens
    val globalVariable = GlobalData.instance
    private val namesArray = ArrayList<String>()
    private var nameToScore = HashMap<String, Int>()

    //Keep track of current roll screen
    private var selectedLeftDice: Int = 0
    private var selectedRightDice: Int = 0
    private var selectedBadRoll: Boolean = false
    private var rolledOnce: Boolean = false
    private var currentPlayer: String = ""
    private var currentPlayerNewScore: Int = 0
    private var lastPlayer: String = ""
    private var lastPlayerLastScore: Int = 0
    private var endingPlayer: String = ""

    //Items from layout that need to be passed to other functions
    private lateinit var textYourScore: TextView
    private lateinit var textTopScore: TextView
    private lateinit var leftDiceButtons: List<ImageButton>
    private lateinit var rightDiceButtons: List<ImageButton>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll)


//       //GLOBAL PLAYERS DOES NOT ACTUALLY HOLD PLAYERS THAT WERE SITTING OUT
        //only get the players that are in this round
        val players = globalVariable.players
        val sittingOut = globalVariable.sittingOut
        for(player in players){
            if(sittingOut[player] == false){
                namesArray.add(player)
            }
        }
        currentPlayer = namesArray[0]

        //hashmap holds player names and records their scores
        for (name in namesArray){
            //may not want to reset all scores to 0 if we are resuming a game
            nameToScore[name] = 0
        }
        globalVariable.nameToScore = this.nameToScore


        //Get all items from layout
        textYourScore = findViewById<TextView>(R.id.textYourScore)
        textTopScore = findViewById<TextView>(R.id.textTopScore)

        val topCard = findViewById<CardView>(R.id.cardTop)

        leftDiceButtons = listOf<ImageButton>(
            findViewById(R.id.imageButtonL1),
            findViewById(R.id.imageButtonL2),
            findViewById(R.id.imageButtonL3),
            findViewById(R.id.imageButtonL4),
            findViewById(R.id.imageButtonL5),
            findViewById(R.id.imageButtonL6)
        )

        rightDiceButtons = listOf<ImageButton>(
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


        //When you click on the scores card, It will take you to All Scores screen
        topCard.setOnClickListener{startActivity(Intent(this@RollActivity, AllScoresActivity::class.java))}

        //When you click on the dice, it sets the selected dice, updates the dice button and current player score
        for (i in 1..6) {
            leftDiceButtons[i - 1].setOnClickListener {
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

        //When you click bad roll it sets the flag variable
        badRoll.setOnClickListener {
            selectedBadRoll = true
        }

        //When you click next roll it saves the current player's score and loads next player
        nextRoll.setOnClickListener {
            if(selectedLeftDice > 0 && selectedRightDice > 0
                && rolledOnce){
                rollNext()
            }
        }

        //When you click undo roll it loads last player and erases their last roll
        undoRoll.setOnClickListener {
            rollUndo()
        }
    }


    //   //need to make this function change the dice buttons somehow, maybe highlight them?
    //right now just changing color of whole image to yellow
    private fun updateDiceButtonSelection(buttons: List<ImageButton>, selectedDice: Int) {
        /**for (button in buttons) {
        button.isPressed = false
        }
        buttons[selectedDice - 1].isPressed = true*/

        for (button in buttons) {
            button.setColorFilter(null) // Reset color filter for all buttons
        }

        if(selectedDice > 0) {
            buttons[selectedDice - 1].setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP) // Change color of selected button
        }
    }


    //main logic function to calculate the players score based on the two dice
    private fun updateScore() {
        var selectedScore = selectedLeftDice + selectedRightDice
        currentPlayerNewScore = nameToScore[currentPlayer]!! + selectedScore


//       //need to implement special rule cases here


        textYourScore.text = "YOUR SCORE:\n$currentPlayer: $currentPlayerNewScore"

        //update top score
        val topScore = nameToScore.values.maxOrNull()!!
        val topPlayer = nameToScore.entries.firstOrNull { it.value == topScore }?.key
        textTopScore.text = "TOP SCORE:\n$topPlayer: $topScore"
    }


    //saves the current player's info and loads next player
    private fun rollNext(){
        //update global scores
        globalVariable.nameToScore = this.nameToScore

        //check if the game has ended and go to the final pot screen
        if(currentPlayer == endingPlayer){
            startActivity(Intent(this@RollActivity, WinScreenActivity::class.java))
        }

        //save current player's info
        lastPlayer = currentPlayer
        lastPlayerLastScore = nameToScore[currentPlayer]!!

        //update current player's score
        nameToScore[currentPlayer] = currentPlayerNewScore

        //find next player in namesArray and save it to currentPlayer
        val currentPlayerIndex = namesArray.indexOf(currentPlayer)
        currentPlayer = if (currentPlayerIndex < namesArray.size - 1) {
            namesArray[currentPlayerIndex + 1]
        } else {
            namesArray[0]
        }

//       //NOT WORKING CORRECTLY NEED TO MODIFY
        //check if the player surpassed 100 points, we will do one more turn and end the game at this player
        if(currentPlayerNewScore > 100){
            endingPlayer = currentPlayer
        }

        currentPlayerNewScore = 0
        selectedLeftDice = 0
        selectedRightDice = 0
        selectedBadRoll = false
        rolledOnce = true

        updateScore()
        updateDiceButtonSelection(leftDiceButtons, -1)
        updateDiceButtonSelection(rightDiceButtons, -1)
    }


    //loads last player and erases their last roll
    private fun rollUndo(){
        //What happens if I undo a winning (ending) player?  Need to deal with this case

        currentPlayer = lastPlayer
        nameToScore[currentPlayer] = lastPlayerLastScore

        currentPlayerNewScore = 0
        selectedLeftDice = 0
        selectedRightDice = 0
        selectedBadRoll = false

        updateScore()
        updateDiceButtonSelection(leftDiceButtons, -1)
        updateDiceButtonSelection(rightDiceButtons, -1)
    }
}