package com.example.pigkeeper

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class RollActivity : AppCompatActivity() {
    //Necessary data from other screens
    lateinit var globalVariable : GlobalData
    private var sittingOut = mutableMapOf<String, Boolean>()//globalVariable.sittingOut
    private var namesArray = ArrayList<String>()
    private var nameToScore = HashMap<String, Int>()
    private var nameToPot = HashMap<String, Int>()
    //NEW
    private var rulesMap = mutableMapOf<RulesActivity.SpecialRuleCase, MutableList<RulesActivity.Consequence>>()
    private lateinit var currentSpecialRuleCase: RulesActivity.SpecialRuleCase
    private lateinit var currentSpecialRuleConsequences: MutableList<RulesActivity.Consequence>
    //END NEW

    //Keep track of current roll screen
    private var selectedLeftDice: Int = 0
    private var selectedRightDice: Int = 0
    private var selectedScore: Int = 0
    private var selectedBadRoll: Boolean = false
    private var rolledOnce: Boolean = false
    private var wasFirstRoll: Boolean = false

    private var toggledBadRoll: Boolean = false
    private var mustNextRoll: Boolean = false
    private var wasMustNextRoll: Boolean = false
    private var lastRollWasForcedNextRoll: Boolean = false
    private var mustNextPlayer: Boolean = false
    private var wasMustNextPlayer: Boolean = false
    private var disableDiceSelect: Boolean = false
    private var lastRollWasDouble: Boolean = false
    private var consecutiveDoubleRolls: Int = 0
    private var previousPlayerLastRollWasDouble: Boolean = false
    private var previousPlayerConsecutiveDoubleRolls: Int = 0
    private var lastLastRollWasDouble: Boolean = false
    private var textConsequenceBuilder = StringBuilder()
    private var previousTextConsequence: String = ""
    private var previousPreviousTextConsequence: String = ""


    private var currentPlayer: String = ""
    private var currentPlayerLastTurnScore: Int = 0
    private var currentPlayerLastRollStartScore: Int = 0
    private var currentPlayerRollStartScore: Int = 0
    private var currentPlayerNewScore: Int = 0

    private var previousPlayer: String = ""
    private var previousPlayerLastScore: Int = 0

    private var endingPlayer: String = ""

    //Items from layout that need to be passed to other functions
    private lateinit var textConsequence: TextView
    private lateinit var textYourScore: TextView
    private lateinit var textTopScore: TextView
    private lateinit var leftDiceButtons: List<ImageButton>
    private lateinit var rightDiceButtons: List<ImageButton>
    private lateinit var nextRollButton: ImageButton
    private lateinit var nextPlayerButton: ImageButton
    private lateinit var undoRollButton: ImageButton
    private lateinit var badRollButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll)


        globalVariable = GlobalData.instance

        //GLOBAL PLAYERS DOES NOT ACTUALLY HOLD PLAYERS THAT WERE SITTING OUT
        //so namesArray already hold only players that are playing this round (see data above) nvm put it down here
        //REUPDATING HERE TO MAKE SURE WE HAVE FRESHEST DATA
        namesArray = globalVariable.players
        sittingOut = globalVariable.sittingOut

        //set current player to first in turn order
        currentPlayer = namesArray[0]

        //hashmap holds player names and records their scores, 0 for now
        for (name in namesArray){
            nameToScore[name] = 0
        }

        //GET RULES
        rulesMap = globalVariable.rulesMap


        //Get all items from layout
        textConsequence = findViewById<TextView>(R.id.textConsequence)
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
        badRollButton = findViewById<ImageButton>(R.id.imageButtonBadRoll)
        undoRollButton = findViewById<ImageButton>(R.id.imageButtonUndoRoll)
        nextRollButton = findViewById<ImageButton>(R.id.imageButtonNextRoll)
        nextPlayerButton = findViewById<ImageButton>(R.id.imageButtonNextPlayer)

        // Initially hide both next buttons and undo button
        hideNextButtons()
        undoRollButton.visibility = View.INVISIBLE
        // Initially show that player must roll at least once
        if(!rolledOnce){
            addTextConsequence("| MUST ROLL ONCE |")
            previousTextConsequence = textConsequenceBuilder.toString()
        }


        //THIS CODE BLOCK KEEPS DATA ALIVE
        //if we are resuming a game round restore variables
        if(globalVariable.nameToScore.size !=0){
                restoreVariables()
        //else brand new game round so nameToScore holds all 0s
        }else{
            globalVariable.nameToScore = this.nameToScore
            globalVariable.endedGameRound = false
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
                if(!disableDiceSelect) {
                    //tapped same dice
                    if (selectedLeftDice == i) {
                        //unselect and show score at rolls start
                        selectedLeftDice = 0
                        updateDiceButtonSelection(leftDiceButtons, -1)
                        showRollStartScore()

                        //rolled at least once and unselected both die, might want to end turn
                        if (rolledOnce && selectedRightDice == 0 && !wasMustNextRoll) {
                            showNextPlayerButton()
                            addTextConsequence(previousTextConsequence)
                        } else {
                            hideNextButtons()
                            addTextConsequence(previousTextConsequence)
                        }
                    } else {
                        //select this dice
                        selectedLeftDice = i
                        updateDiceButtonSelection(leftDiceButtons, i)
                        showRollStartScore()
                        hideNextButtons()
                        //Log.d("me", selectedLeftDice.toString())

                        //selected a left and a right dice
                        if (selectedRightDice > 0) {
                            updateScore()
                            showNextRollButton()
                        }
                    }
                    badRollButton.setColorFilter(null)
                    toggledBadRoll = false
                }
            }

            rightDiceButtons[i - 1].setOnClickListener {
                if(!disableDiceSelect) {
                    //tapped same dice
                    if (selectedRightDice == i) {
                        //unselect and show score at rolls start
                        selectedRightDice = 0
                        updateDiceButtonSelection(rightDiceButtons, -1)
                        showRollStartScore()

                        //rolled at least once and unselected both die, might want to end turn
                        if (rolledOnce && selectedLeftDice == 0 && !wasMustNextRoll) {
                            showNextPlayerButton()
                            addTextConsequence(previousTextConsequence)
                        } else {
                            hideNextButtons()
                            addTextConsequence(previousTextConsequence)
                        }
                    } else {
                        //select this dice
                        selectedRightDice = i
                        updateDiceButtonSelection(rightDiceButtons, i)
                        showRollStartScore()
                        hideNextButtons()

                        //selected a left and a right dice
                        if (selectedLeftDice > 0) {
                            updateScore()
                            showNextRollButton()
                        }
                    }
                    badRollButton.setColorFilter(null)
                    toggledBadRoll = false
                }
            }
        }

        //DONT NEED THIS, SET IN ROLLNEXT, losing turn, cannot roll again, can only undo or go next player
        //}else{
        //    showNextPlayerButton()
        //}

        // see toggledbadroll in data above
        //When you click bad roll it sets the flag variable
        badRollButton.setOnClickListener {
            if(!disableDiceSelect){
                if(!toggledBadRoll){
                    toggledBadRoll = true
                    selectedBadRoll = true
                    selectedLeftDice = 0
                    selectedRightDice = 0
                    updateDiceButtonSelection(leftDiceButtons, -1)
                    updateDiceButtonSelection(rightDiceButtons, -1)
                    badRollButton.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP)
                    hideTextConsequence()
                    updateScore()
                    showNextRollButton()
                }else{
                    toggledBadRoll = false
                    selectedBadRoll = false
                    badRollButton.setColorFilter(null)
                    showRollStartScore()
                    addTextConsequence(previousTextConsequence)
                }
            }
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

    private fun addTextConsequence(consequence: String){
        textConsequenceBuilder.append(consequence)
        textConsequence.setText(textConsequenceBuilder)
        textConsequence.setVisibility(View.VISIBLE);
    }

    private fun hideTextConsequence(){
        textConsequenceBuilder.setLength(0)
        textConsequence.setVisibility(View.GONE);
    }

    //when unselecting a dice, it will show the roll start score
    private fun showRollStartScore(){
        currentPlayerNewScore = currentPlayerRollStartScore
        showScoreText()
        hideTextConsequence()
    }

    //main logic function to calculate the players score based on the two dice
    private fun updateScore(){
        selectedScore = selectedLeftDice + selectedRightDice
        currentPlayerNewScore = nameToScore[currentPlayer]!! + selectedScore

        //reset rule case flags
        mustNextPlayer = false
        mustNextRoll = false
        //HERE, MOVED IF AGAIN TO ROLLNEXT
        lastRollWasDouble = false
        lastLastRollWasDouble = false
        //reset text Consequence
        //hideTextConsequence()


       //need to implement special rule cases here
        //BAD ROLL
        if(selectedBadRoll){
            //resetScoreToLastTurn()
            //loseTurn()
            currentSpecialRuleCase = RulesActivity.SpecialRuleCase.OFF_TABLE
            selectedBadRoll = false
            //YES I DO,dont really need these 3 lines here, just dont return early, no other ifs will be triggered i think
            currentSpecialRuleConsequences = rulesMap[currentSpecialRuleCase]!!
            mapRuleConsequencesToFunctions()
            showScoreText()
            return
        }
        //DOUBLES
        if(selectedLeftDice == selectedRightDice){
            //MOVED IF ABOVE, CALC IF 3 DOUBLES
            //lastRollWasDouble = true
            //consecutiveDoubleRolls += 1 UPDATING THIS TOO EARLY DO IT IN ROLLNEXT
            //if(consecutiveDoubleRolls > 1){
            //    lastLastRollWasDouble = true
            //}
            //3 DOUBLES IN A ROW
            if(consecutiveDoubleRolls == 2) {//2 consecutive doubles alr, this the third one
                //resetScoreToZero()
                //loseTurn()
                currentSpecialRuleCase = RulesActivity.SpecialRuleCase.TRIPLE_DOUBLE
                currentSpecialRuleConsequences = rulesMap[currentSpecialRuleCase]!!
                mapRuleConsequencesToFunctions()
            //SNAKE EYES
            }else if(selectedScore == 2) {
                //resetScoreToZero()
                //loseTurn()
                currentSpecialRuleCase = RulesActivity.SpecialRuleCase.SNAKE_EYES
                currentSpecialRuleConsequences = rulesMap[currentSpecialRuleCase]!!
                mapRuleConsequencesToFunctions()
            //BOX CARS
            }else if(selectedScore == 12){
                currentSpecialRuleCase = RulesActivity.SpecialRuleCase.BOX_CARS
                currentSpecialRuleConsequences = rulesMap[currentSpecialRuleCase]!!
                mapRuleConsequencesToFunctions()
            //ALL OTHER DOUBLES
            }else if (selectedScore != 0){
                //doublePoints()
                //mustRollAgain()
                currentSpecialRuleCase = RulesActivity.SpecialRuleCase.DOUBLE
                currentSpecialRuleConsequences = rulesMap[currentSpecialRuleCase]!!
                mapRuleConsequencesToFunctions()
            }
        }
        //ROLL 7
        if(selectedScore == 7){
            //resetScoreToLastTurn()
            //loseTurn()
            currentSpecialRuleCase = RulesActivity.SpecialRuleCase.ROLL_7
            currentSpecialRuleConsequences = rulesMap[currentSpecialRuleCase]!!
            mapRuleConsequencesToFunctions()
        }
        //score is exactly 100
        if(currentPlayerNewScore == 100){
            resetScoreToZero()
            loseTurn()
        }

        //cant do it here, currentSpecialRuleCase may be uninitialized
        //currentSpecialRuleConsequences = rulesMap[currentSpecialRuleCase]!!
        //mapRuleConsequencesToFunctions()
        Log.d("me",textConsequenceBuilder.toString())

        showScoreText()
    }

    private fun mapRuleConsequencesToFunctions(){
        if(currentSpecialRuleConsequences.contains(RulesActivity.Consequence.DOUBLE_POINTS)){
            doublePoints()
        }
        if(currentSpecialRuleConsequences.contains(RulesActivity.Consequence.RESET_TURN_SCORE)){
            resetScoreToLastTurn()
        }
        if(currentSpecialRuleConsequences.contains(RulesActivity.Consequence.RESET_SCORE_TO_ZERO)){
            resetScoreToZero()
        }
        if(currentSpecialRuleConsequences.contains(RulesActivity.Consequence.LOSE_TURN)){
            loseTurn()
        }
        if(currentSpecialRuleConsequences.contains(RulesActivity.Consequence.MUST_ROLL_AGAIN)){
            mustRollAgain()
        }

        //this is actually changing the rulesMap itself i think
        //currentSpecialRuleConsequences.clear()
    }

    private fun doublePoints(){
        currentPlayerNewScore = nameToScore[currentPlayer]!! + (2*selectedScore)
        addTextConsequence("| DOUBLE POINTS |")
    }

    //this not right, IS RIGHT NOW (complicated undo function)
    private fun loseTurn(){
        mustNextPlayer = true
        addTextConsequence("| LOSE TURN |")
    }

    private fun mustRollAgain(){
        mustNextRoll = true
        addTextConsequence("| MUST ROLL AGAIN |")
    }

    private fun resetScoreToLastTurn(){
        currentPlayerNewScore = currentPlayerLastTurnScore
        addTextConsequence("| RESET TURN SCORE |")
    }

    private fun resetScoreToZero(){
        currentPlayerNewScore = 0
        addTextConsequence("| RESET SCORE TO 0 |")
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

        //(showing appropriate buttons)
        //if must roll again hide all next buttons
        if(mustNextRoll){
            hideNextButtons()
            //not mustroll and rolledOnce at least so can go to next player
        }else{
            showNextPlayerButton()
        }
        badRollButton.setColorFilter(null)
        //rolledOnce at least so can go to next player
        //showNextPlayerButton()

        //can undo this action
        undoRollButton.visibility = View.VISIBLE

        wasFirstRoll = !rolledOnce
        rolledOnce = true
        lastRollWasForcedNextRoll = wasMustNextRoll
        wasMustNextRoll = mustNextRoll
        mustNextRoll = false
        if(mustNextPlayer){
            //wasMustNextPlayer = true
            disableDiceSelect = true
            //mustNextPlayer = false
        }
        toggledBadRoll = false
        //DOUBLES
        if(selectedLeftDice == selectedRightDice) {
            //MOVED IF ABOVE, CALC IF 3 DOUBLES
            lastRollWasDouble = true
            consecutiveDoubleRolls += 1
            if (consecutiveDoubleRolls > 1) {
                lastLastRollWasDouble = true
            }
        }
        if(!lastRollWasDouble){
            consecutiveDoubleRolls = 0
        }
        //lastLastRollWasDouble = lastRollWasDouble
        resetVariables()
        //get rid of roll once text
        Log.d("me",textConsequenceBuilder.toString())
        //if(wasFirstRoll){
        //    val index: Int = textConsequenceBuilder.indexOf("| MUST ROLL ONCE |")
        //    textConsequenceBuilder.delete(index, index + "| MUST ROLL ONCE |".length)
        //    textConsequence.setText(textConsequenceBuilder)
        //    textConsequence.setVisibility(View.VISIBLE)
        //}
        previousPreviousTextConsequence = previousTextConsequence
        previousTextConsequence = textConsequenceBuilder.toString()
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
            globalVariable.endedGameRound = true
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
        disableDiceSelect = false
        wasMustNextPlayer = mustNextPlayer
        mustNextPlayer = false
        previousPlayerLastRollWasDouble = lastRollWasDouble
        previousPlayerConsecutiveDoubleRolls = consecutiveDoubleRolls
        lastRollWasDouble = false
        consecutiveDoubleRolls = 0
        resetVariables()
        //previousPreviousTextConsequence = previousTextConsequence
        previousPreviousTextConsequence = textConsequenceBuilder.toString()
        hideTextConsequence()
        if(!rolledOnce){
            addTextConsequence("| MUST ROLL ONCE |")
            previousTextConsequence = textConsequenceBuilder.toString()
        }
    }

    //FOR NOW only undo once (the last action)
    //only erases current players last roll or loads previous player if current player hasnt rolled
    private fun rollUndo(){
        //What happens if I undo a winning (ending) player?  Need to deal with this case DO I? Ending player will just end game

        //cant undo twice in a row
        if (undoRollButton.visibility == View.VISIBLE) {
            //undo a bad roll, really that simple?
            toggledBadRoll = false
            if(selectedBadRoll){
                selectedBadRoll = false
            }

            showNextPlayerButton()
            //if current player hasnt rolled
            if (!rolledOnce && previousPlayer != "") {
                //go back to last player
                currentPlayer = previousPlayer
                nameToScore[currentPlayer] = previousPlayerLastScore
                currentPlayerRollStartScore = nameToScore[currentPlayer]!!
                //ADDED FOR 3 ROLLS
                lastRollWasDouble = previousPlayerLastRollWasDouble
                consecutiveDoubleRolls = previousPlayerConsecutiveDoubleRolls
                //showNextPlayerButton()
                rolledOnce = true
                if(wasMustNextPlayer){
                    mustNextPlayer = true
                    disableDiceSelect = true
                }
                //disableDiceSelect = false
                resetVariables()
                hideTextConsequence()
                previousTextConsequence = previousPreviousTextConsequence
                addTextConsequence(previousPreviousTextConsequence)

            //if current player has rolled once
            } else if(rolledOnce){
                //if we undo their first roll/must roll, they are still forced to roll
                //if(wasFirstRoll){
                    //rolledOnce = false
                    //hideNextButtons()
                //}

                if(wasFirstRoll || lastRollWasForcedNextRoll){
                    hideNextButtons()
                }
                rolledOnce = !wasFirstRoll
                //wasFirstRoll = !rolledOnce
                //wasMustNextRoll

                if(lastRollWasForcedNextRoll){
                    wasMustNextRoll = true
                    lastRollWasForcedNextRoll = false
                    //showNextPlayerButton()
                }else{
                    wasMustNextRoll = false
                }

                if(mustNextPlayer){
                    mustNextPlayer = false
                }
                //ADDED FOR 3 ROLLS
                if(lastLastRollWasDouble){
                    lastRollWasDouble = true
                    consecutiveDoubleRolls -= 1
                }else{
                    //probably unnecessary redundant
                    lastRollWasDouble = false
                    consecutiveDoubleRolls = 0
                }



                //reset score to what it was at the last roll start
                nameToScore[currentPlayer] = currentPlayerLastRollStartScore
                currentPlayerRollStartScore = nameToScore[currentPlayer]!!
                disableDiceSelect = false
                resetVariables()
                hideTextConsequence()
                previousTextConsequence = previousPreviousTextConsequence
                addTextConsequence(previousPreviousTextConsequence)
            }

            /**if(lastRollWasMustNextRoll){
                wasMustNextRoll = true
                lastRollWasMustNextRoll = false
                //showNextPlayerButton()
            }else{
                wasMustNextRoll = false
            }**/

            /**
            if(wasMustNextRoll){
                mustNextRoll = true
                wasMustNextRoll = false
                //showNextPlayerButton()
            }else{
                mustNextRoll = false
            }**/

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
        //hideTextConsequence() I dont want roll next to reset the text
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
                //GET SKUNKED
                if(nameToScore[player]!! == 0){
                    playerDebt *= 2
                }
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
        //weird case, we want the new score to reset to the roll start score
        globalVariable.currentPlayerNewScore = this.currentPlayerRollStartScore
        globalVariable.previousPlayer = this.previousPlayer
        globalVariable.previousPlayerLastScore = this.previousPlayerLastScore
        globalVariable.endingPlayer = this.endingPlayer


        globalVariable.mustNextRoll = this.mustNextRoll
        globalVariable.wasMustNextRoll = this.wasMustNextRoll
        globalVariable.lastRollWasForcedNextRoll = this.lastRollWasForcedNextRoll
        globalVariable.mustNextPlayer = this.mustNextPlayer
        globalVariable.wasMustNextPlayer = this.wasMustNextPlayer
        globalVariable.disableDiceSelect = this.disableDiceSelect
        globalVariable.lastRollWasDouble = this.lastRollWasDouble
        globalVariable.consecutiveDoubleRolls = this.consecutiveDoubleRolls
        globalVariable.previousPlayerLastRollWasDouble = this.previousPlayerLastRollWasDouble
        globalVariable.previousPlayerConsecutiveDoubleRolls = this.previousPlayerConsecutiveDoubleRolls
        globalVariable.lastLastRollWasDouble = this.lastLastRollWasDouble
        globalVariable.textConsequenceBuilder = this.textConsequenceBuilder
        //NEW
        globalVariable.currentSpecialRuleCase = this.currentSpecialRuleCase
        globalVariable.currentSpecialRuleConsequences = this.currentSpecialRuleConsequences

        //save scores to global vars
        globalVariable.nameToScore = this.nameToScore

        //save global variable state
        globalVariable.saveData()

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

        //NEW
        this.mustNextRoll = globalVariable.mustNextRoll
        this.wasMustNextRoll = globalVariable.wasMustNextRoll
        this.lastRollWasForcedNextRoll = globalVariable.lastRollWasForcedNextRoll
        this.mustNextPlayer = globalVariable.mustNextPlayer
        this.wasMustNextPlayer = globalVariable.wasMustNextPlayer
        this.disableDiceSelect = globalVariable.disableDiceSelect
        this.lastRollWasDouble = globalVariable.lastRollWasDouble
        this.consecutiveDoubleRolls = globalVariable.consecutiveDoubleRolls
        this.previousPlayerLastRollWasDouble = globalVariable.previousPlayerLastRollWasDouble
        this.previousPlayerConsecutiveDoubleRolls = globalVariable.previousPlayerConsecutiveDoubleRolls
        this.lastLastRollWasDouble = globalVariable.lastLastRollWasDouble
        this.textConsequenceBuilder = globalVariable.textConsequenceBuilder
        //NEW
        this.currentSpecialRuleCase = globalVariable.currentSpecialRuleCase
        this.currentSpecialRuleConsequences = globalVariable.currentSpecialRuleConsequences
        //restore rules from global vars
        this.rulesMap = globalVariable.rulesMap

        //restore scores from global vars
        this.nameToScore = globalVariable.nameToScore

        //update screen info
        showScoreText()
        if(rolledOnce){
            showNextPlayerButton()
        }
        if(textConsequenceBuilder.length > 0){
            Log.d("me", rolledOnce.toString())
            Log.d("me", textConsequenceBuilder.toString())
            textConsequence.setText(textConsequenceBuilder)
            textConsequence.setVisibility(View.VISIBLE);
        }
    }
}