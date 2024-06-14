package com.ucdavis.pigkeeper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton

class NewPlayersActivity : AppCompatActivity() {
    //data that needs to be passed to other functions
    lateinit var globalVariable: GlobalData
    private lateinit var textViewPlayers: List<TextView>
    private var namesArray = ArrayList<String>()
    private val playersEntered = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_players)

        globalVariable = GlobalData.instance

        var editingPlayer = -1

        //textViewPlayers = findViewById<TextView>(R.id.textViewPlayers)
        textViewPlayers = listOf<TextView>(
            findViewById(R.id.textViewPlayer1),
            findViewById(R.id.textViewPlayer2),
            findViewById(R.id.textViewPlayer3),
            findViewById(R.id.textViewPlayer4),
            findViewById(R.id.textViewPlayer5),
            findViewById(R.id.textViewPlayer6),
            findViewById(R.id.textViewPlayer7),
            findViewById(R.id.textViewPlayer8),
            findViewById(R.id.textViewPlayer9),
            findViewById(R.id.textViewPlayer10),
        )
        val inputName = findViewById<EditText>(R.id.inputName)
        val buttonAddPlayer = findViewById<Button>(R.id.buttonAddPlayer)

        //ADD Listeners to 10 textviews to edit entered player names
        for (i in 1..10) {
            textViewPlayers[i - 1].setOnClickListener {
                val currentPlayerEntered = textViewPlayers[i - 1].text.toString()

                /**if(currentPlayerEntered.isNotBlank()){
                    inputName.setText(currentPlayerEntered)
                    inputName.performClick()
                    editingPlayer = i
                }**/
                if (currentPlayerEntered.isNotBlank() && currentPlayerEntered != "No Players Added") {
                    buttonAddPlayer.isEnabled = true
                    buttonAddPlayer.setText("Edit")
                    //buttonAddPlayer.setText("Remove")
                    editingPlayer = i

                    inputName.setText(currentPlayerEntered)
                    inputName.requestFocus()
                    inputName.setSelection(currentPlayerEntered.length)

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(inputName, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }




        val buttonStartRound = findViewById<Button>(R.id.buttonStartRound)
        val buttonLoadLastPlayers = findViewById<Button>(R.id.buttonLoadLastPlayers)

        // Initially disable the "Add Player" button if inputName is empty
        buttonAddPlayer.isEnabled = inputName.text.isNotEmpty()
        // Initially disable the "Start Round" button if namesArray is empty
        buttonStartRound.isEnabled = namesArray.isNotEmpty()
        // Set the initial state of the "Load Last Players" button
        buttonLoadLastPlayers.isEnabled = globalVariable.nameToPot.isNotEmpty()

        buttonAddPlayer.setOnClickListener {
            val currentPlayerEntered = inputName.text.toString()

            if(buttonAddPlayer.text == "Remove"){
                namesArray.removeAt(editingPlayer-1)

                for(i in editingPlayer-1..<namesArray.size){
                    textViewPlayers[i].text = textViewPlayers[i+1].text
                }
                textViewPlayers[namesArray.size].text = ""


                buttonAddPlayer.setText("Add Player")
                buttonAddPlayer.isEnabled = false
                editingPlayer = -1
            }
            else if(editingPlayer != -1 && currentPlayerEntered.isNotEmpty() && currentPlayerEntered.length < 20){
                namesArray[editingPlayer-1] = currentPlayerEntered
                textViewPlayers[editingPlayer-1].text = currentPlayerEntered

                inputName.text.clear()
                updateStartRoundButtonState()

                buttonAddPlayer.setText("Add Player")
                buttonAddPlayer.isEnabled = false
                editingPlayer = -1
            }
            else if (currentPlayerEntered.isNotEmpty() && !namesArray.contains(currentPlayerEntered) && namesArray.size < 10 && currentPlayerEntered.length < 20) {
                namesArray.add(currentPlayerEntered)

                val playerIndex = namesArray.size
                textViewPlayers[playerIndex-1].text = currentPlayerEntered
                //playersEntered.append("$currentPlayerEntered\n")
                //textViewPlayers.text = playersEntered.toString()

                inputName.text.clear()
                updateStartRoundButtonState()
            }
        }

        buttonStartRound.setOnClickListener {
            if (namesArray.isNotEmpty()) {
                globalVariable.players = namesArray
                startActivity(Intent(this@NewPlayersActivity, TurnOrderActivity::class.java))
            }
        }

        buttonLoadLastPlayers.setOnClickListener {
            loadLastPlayers()
        }

        // Add a TextWatcher to listen for changes in the inputName EditText
        inputName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable or disable the button based on whether there is text in the input
                buttonAddPlayer.isEnabled = s?.isNotEmpty() == true

                if(editingPlayer != -1){
                    buttonAddPlayer.isEnabled = true

                    if(s?.isEmpty() == true){
                        buttonAddPlayer.setText("Remove")
                    }else{
                        buttonAddPlayer.setText("Edit")
                    }
                }

                if(namesArray.size == 10 && editingPlayer == -1){
                    buttonAddPlayer.isEnabled = false
                }

                if (s != null && namesArray.contains(s.toString())) {
                    buttonAddPlayer.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed during text changes
            }
        })

        // Automatically load last players if same game session just new round
        if (!globalVariable.endedGameSession) {
            loadLastPlayers()
        }
    }

    private fun updateStartRoundButtonState() {
        val buttonStartRound = findViewById<Button>(R.id.buttonStartRound)
        buttonStartRound.isEnabled = namesArray.isNotEmpty()
    }

    private fun loadLastPlayers() {
        // If there was a previous game, get the players
        if (globalVariable.nameToPot.isNotEmpty()) {
            // Load all last players, even the ones sitting out
            for ((player, sit) in globalVariable.sittingOut) {
                if (!namesArray.contains(player)) {
                    namesArray.add(player)
                    //playersEntered.append("$player\n")
                    val playerIndex = namesArray.size
                    textViewPlayers[playerIndex-1].text = player
                }
            }
            //textViewPlayers.text = playersEntered.toString()
            updateStartRoundButtonState()
            val buttonLoadLastPlayers = findViewById<Button>(R.id.buttonLoadLastPlayers)
            buttonLoadLastPlayers.isEnabled = false
        }
    }

    override fun onPause() {
        super.onPause()
        globalVariable.saveData()
    }
}
