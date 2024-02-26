package com.example.pigkeeper

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView

class RollActivity : AppCompatActivity() {
    //Necessary data from other screens
    val globalVariable = GlobalData.instance
    private val sittingOut = globalVariable.sittingOut
    private val namesArray = globalVariable.players
    private var nameToScore = HashMap<String, Int>()
    private var nameToPot = HashMap<String, Int>()

    //Keep track of current roll screen
    private var selectedLeftDice: Int = 0
    private var selectedRightDice: Int = 0
    private var selectedScore: Int = 0
    private var selectedBadRoll: Boolean = false
    private var rolledOnce: Boolean = false
    private var wasFirstRoll: Boolean = false

    private var currentPlayer: String = ""
    private var currentPlayerLastTurnScore: Int = 0
    private var currentPlayerLastRollStartScore: Int = 0
    private var currentPlayerRollStartScore: Int = 0
    private var currentPlayerNewScore: Int = 0

    private var previousPlayer: String = ""
    private var previousPlayerLastScore: Int = 0

    private var endingPlayer: String = ""

    //Items from layout that need to be passed to other functions
    private lateinit var textYourScore: TextView
    private lateinit var textTopScore: TextView
    private lateinit var leftDiceButtons: List<ImageButton>
    private lateinit var rightDiceButtons: List<ImageButton>
    private lateinit var nextRollButton: ImageButton
    private lateinit var nextPlayerButton: ImageButton
    private lateinit var undoRollButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll)

        //GLOBAL PLAYERS DOES NOT ACTUALLY HOLD PLAYERS THAT WERE SITTING OUT
        //so namesArray already hold only players that are playing this round (see data above)

        //set current player to first in turn order
        currentPlayer = namesArray[0]

        //hashmap holds player names and records their scores, 0 for now
        for (name in namesArray){
            nameToScore[name] = 0
        }


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
        val badRollButton = findViewById<ImageButton>(R.id.imageButtonBadRoll)
        undoRollButton = findViewById<ImageButton>(R.id.imageButtonUndoRoll)
        nextRollButton = findViewById<ImageButton>(R.id.imageButtonNextRoll)
        nextPlayerButton = findViewById<ImageButton>(R.id.imageButtonNextPlayer)

        // Initially hide both next buttons and undo button
        hideNextButtons()
        undoRollButton.visibility = View.INVISIBLE


        //THIS CODE BLOCK KEEPS DATA ALIVE
        //if we are resuming a game round restore variables
        if(globalVariable.nameToScore.size !=0){
                restoreVariables()
        //else brand new game round so nameToScore holds all 0s
        }else{
            globalVariable.nameToScore = this.nameToScore
            //if not just a new round but a whole new session, reset pot
            if(globalVariable.endedGameSession){
                globalVariable.nameToPot.clear()
                globalVariable.endedGameSession = false
            }
        }


        //When you click on the scores card, It will take you to All Scores screen
        topCard.setOnClickListener{startActivity(Intent(this@RollActivity, AllScoresActivity::class.java))}

        //When you click on the dice, it sets the selected dice, updates the dice button and current player score
        for (i in 1..6) {
            leftDiceButtons[i - 1].setOnClickListener {
                //tapped same dice
                if (selectedLeftDice == i) {
                    //unselect and show score at rolls start
                    selectedLeftDice = 0
                    updateDiceButtonSelection(leftDiceButtons, -1)
                    showRollStartScore()

                    //rolled at least once and unselected both die, might want to end turn
                    if (rolledOnce && selectedRightDice == 0) {
                        showNextPlayerButton()
                    } else {
                        hideNextButtons()
                    }
                } else {
                    //select this dice
                    selectedLeftDice = i
                    updateDiceButtonSelection(leftDiceButtons, i)

                    //selected a left and a right dice
                    if (selectedRightDice > 0) {
                        updateScore()
                        showNextRollButton()
                    }
                }
            }

            rightDiceButtons[i - 1].setOnClickListener {
                //tapped same dice
                if (selectedRightDice == i) {
                    //unselect and show score at rolls start
                    selectedRightDice = 0
                    updateDiceButtonSelection(rightDiceButtons, -1)
                    showRollStartScore()

                    //rolled at least once and unselected both die, might want to end turn
                    if (rolledOnce && selectedLeftDice == 0) {
                        showNextPlayerButton()
                    } else {
                        hideNextButtons()
                    }
                } else {
                    //select this dice
                    selectedRightDice = i
                    updateDiceButtonSelection(rightDiceButtons, i)

                    //selected a left and a right dice
                    if (selectedLeftDice > 0) {
                        updateScore()
                        showNextRollButton()
                    }
                }
            }
        }


        //When you click bad roll it sets the flag variable
        badRollButton.setOnClickListener {
            selectedBadRoll = true
        }

        //When you click next roll it it updates the current player's score and saves their last score
        nextRollButton.setOnClickListener {
            rollNext()
        }

        //When you click next player it updates the current player's score and loads next player
        nextPlayerButton.setOnClickListener {
            playerNext()
        }

        //When you click undo roll it loads last player and erases their last roll
        undoRollButton.setOnClickListener {
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

    private fun showNextRollButton(){
        nextRollButton.visibility = View.VISIBLE
        nextPlayerButton.visibility = View.GONE
    }

    private fun showNextPlayerButton(){
        nextRollButton.visibility = View.GONE
        nextPlayerButton.visibility = View.VISIBLE
    }

    private fun hideNextButtons(){
        nextRollButton.visibility = View.GONE
        nextPlayerButton.visibility = View.GONE
    }

    //when unselecting a dice, it will show the roll start score
    private fun showRollStartScore(){
        currentPlayerNewScore = currentPlayerRollStartScore
        showScoreText()
    }

    //main logic function to calculate the players score based on the two dice
    private fun updateScore(){
        selectedScore = selectedLeftDice + selectedRightDice
        currentPlayerNewScore = nameToScore[currentPlayer]!! + selectedScore


//       //need to implement special rule cases here


        showScoreText()
    }

    private fun showScoreText(){
        textYourScore.text = "YOUR SCORE:\n$currentPlayer: $currentPlayerNewScore"

        //update top score
        val topScore = nameToScore.values.maxOrNull()!!
        val topPlayer = nameToScore.entries.firstOrNull { it.value == topScore }?.key
        textTopScore.text = "TOP SCORE:\n$topPlayer: $topScore"
    }

    //save current roll and do next roll
    private fun rollNext(){
        //save current player's last roll start score (for the undo)
        currentPlayerLastRollStartScore = currentPlayerRollStartScore

        //save current player's score to global array
        nameToScore[currentPlayer] = currentPlayerNewScore

        //update current player's roll start score for the next roll
        currentPlayerRollStartScore = nameToScore[currentPlayer]!!

        //showing appropriate buttons
        //rolledOnce at least so can go to next player
        showNextPlayerButton()
        //can undo this action
        undoRollButton.visibility = View.VISIBLE

        wasFirstRoll = !rolledOnce
        rolledOnce = true
        resetVariables()
    }

    //saves the current player's info and loads next player
    private fun playerNext(){
        //save current player's info in previous player in case of undo
        previousPlayer = currentPlayer
        previousPlayerLastScore = nameToScore[currentPlayer]!!

        //save current player's score to global array
        nameToScore[currentPlayer] = currentPlayerNewScore

        //check if the player surpassed 100 points or previous ending player's score
        //we will do one more turn and end the game at this player
        if(endingPlayer == "" && currentPlayerNewScore > 100 ||
           endingPlayer != "" && currentPlayerNewScore > nameToScore[endingPlayer]!!) {
            endingPlayer = currentPlayer
        }

        //find next player in namesArray and save it to currentPlayer
        val currentPlayerIndex = namesArray.indexOf(currentPlayer)
        currentPlayer = if (currentPlayerIndex < namesArray.size - 1) {
            namesArray[currentPlayerIndex + 1]
        } else {
            namesArray[0]
        }

        //check if the game has ended and go to the final pot screen
        if(currentPlayer == endingPlayer){
            //update global scores/pot and go to win screen
            globalVariable.nameToScore = this.nameToScore
            updatePot()
            startActivity(Intent(this@RollActivity, WinScreenActivity::class.java))
        }

        //(showing appropriate buttons) next player must roll at least once, and can undo this action
        hideNextButtons()
        undoRollButton.visibility = View.VISIBLE

        //how i get the last roll start score? there is none he hasnt rolled yet
        currentPlayerLastRollStartScore = 0
        //get the next players last score and save it in roll start and turn start
        currentPlayerRollStartScore = nameToScore[currentPlayer]!!
        currentPlayerLastTurnScore = nameToScore[currentPlayer]!!

        rolledOnce = false
        resetVariables()
    }

    //FOR NOW only undo once (the last action)
    //only erases current players last roll or loads previous player if current player hasnt rolled
    private fun rollUndo(){
        //What happens if I undo a winning (ending) player?  Need to deal with this case DO I? Ending player will just end game

        //cant undo twice in a row
        if (undoRollButton.visibility == View.VISIBLE) {
            //if current player hasnt rolled
            if (!rolledOnce && previousPlayer != "") {
                //go back to last player
                currentPlayer = previousPlayer
                nameToScore[currentPlayer] = previousPlayerLastScore
                currentPlayerRollStartScore = nameToScore[currentPlayer]!!

                showNextPlayerButton()
                rolledOnce = true
                resetVariables()

            //if current player has rolled once
            } else if(rolledOnce){
                //if we undo their first roll they are still forced to roll
                if(wasFirstRoll){
                    rolledOnce = false
                    hideNextButtons()
                }
                //reset score to what it was at the last roll start
                nameToScore[currentPlayer] = currentPlayerLastRollStartScore
                currentPlayerRollStartScore = nameToScore[currentPlayer]!!
                resetVariables()
            }

            undoRollButton.visibility = View.INVISIBLE
        }
    }

    private fun resetVariables(){
        currentPlayerNewScore = nameToScore[currentPlayer]!!
        selectedLeftDice = 0
        selectedRightDice = 0
        selectedBadRoll = false

        showScoreText()
        updateDiceButtonSelection(leftDiceButtons, -1)
        updateDiceButtonSelection(rightDiceButtons, -1)
    }

    private fun updatePot(){
        //if we are middle of a game session get the global pot
        if(globalVariable.nameToPot.size !=0){
            this.nameToPot = globalVariable.nameToPot

            //add in any new players that were added this round with 0 in their pot
            for (name in sittingOut.keys){
                if(!this.nameToPot.contains(name)){
                    this.nameToPot[name] = 0
                }
            }
        //else brand new game session so nameToPot holds all 0s
        }else{
            //make sure we are putting all players in the Pot (even ones that sat out)
            for (name in sittingOut.keys){
                nameToPot[name] = 0
            }
        }

        var totalDebt = 0
        var winningScore = nameToScore[endingPlayer]!!
        //update pot for all losing players
        for (player in namesArray){
            if(player != endingPlayer){
                var playerDebt = winningScore - nameToScore[player]!!
                totalDebt += playerDebt
                nameToPot[player] = nameToPot[player]!! - playerDebt
            }
        }
        //update pot for winning player
        nameToPot[endingPlayer] = nameToPot[endingPlayer]!! + totalDebt

        globalVariable.nameToPot = this.nameToPot
    }

    //when clicking away from activity, save all current game data
    override fun onPause() {
        super.onPause()
        globalVariable.rolledOnce = this.rolledOnce
        globalVariable.wasFirstRoll = this.wasFirstRoll
        globalVariable.currentPlayer = this.currentPlayer
        globalVariable.currentPlayerLastTurnScore = this.currentPlayerLastTurnScore
        globalVariable.currentPlayerLastRollStartScore = this.currentPlayerLastRollStartScore
        globalVariable.currentPlayerRollStartScore = this.currentPlayerRollStartScore
        globalVariable.currentPlayerNewScore = this.currentPlayerNewScore
        globalVariable.previousPlayer = this.previousPlayer
        globalVariable.previousPlayerLastScore = this.previousPlayerLastScore
        globalVariable.endingPlayer = this.endingPlayer

        //save scores to global vars
        globalVariable.nameToScore = this.nameToScore

        //cant show undo anymore if move away from screen
    }

    private fun restoreVariables(){
        this.rolledOnce = globalVariable.rolledOnce
        this.wasFirstRoll = globalVariable.wasFirstRoll
        this.currentPlayer = globalVariable.currentPlayer
        this.currentPlayerLastTurnScore = globalVariable.currentPlayerLastTurnScore
        this.currentPlayerLastRollStartScore = globalVariable.currentPlayerLastRollStartScore
        this.currentPlayerRollStartScore = globalVariable.currentPlayerRollStartScore
        this.currentPlayerNewScore = globalVariable.currentPlayerNewScore
        this.previousPlayer = globalVariable.previousPlayer
        this.previousPlayerLastScore = globalVariable.previousPlayerLastScore
        this.endingPlayer = globalVariable.endingPlayer

        //restore scores from global vars
        this.nameToScore = globalVariable.nameToScore

        //update screen info
        showScoreText()
        if(rolledOnce){
            showNextPlayerButton()
        }
    }
}