package com.ucdavis.pigkeeper

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.text.TextWatcher

class NewPlayersActivity : AppCompatActivity() {
    //data that needs to be passed to other functions
    lateinit var globalVariable: GlobalData
    private lateinit var textViewPlayers: TextView
    private var namesArray = ArrayList<String>()
    private val playersEntered = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_players)

        globalVariable = GlobalData.instance

        textViewPlayers = findViewById<TextView>(R.id.textViewPlayers)
        val inputName = findViewById<EditText>(R.id.inputName)

        val buttonAddPlayer = findViewById<Button>(R.id.buttonAddPlayer)
        val buttonStartRound = findViewById<Button>(R.id.buttonStartRound)
        val buttonLoadLastPlayers = findViewById<Button>(R.id.buttonLoadLastPlayers)

        // Initially disable the "Add Player" button if inputName is empty
        buttonAddPlayer.isEnabled = inputName.text.isNotEmpty()
        // Initially disable the "Start Round" button if namesArray is empty
        buttonStartRound.isEnabled = namesArray.isNotEmpty()

        buttonAddPlayer.setOnClickListener {
            val currentPlayerEntered = inputName.text.toString()
            if (currentPlayerEntered.isNotEmpty() && !namesArray.contains(currentPlayerEntered) && namesArray.size < 10) {
                namesArray.add(currentPlayerEntered)
                playersEntered.append("$currentPlayerEntered\n")
                inputName.text.clear()
                textViewPlayers.text = playersEntered.toString()
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

        // Set the initial state of the "Load Last Players" button
        buttonLoadLastPlayers.isEnabled = globalVariable.nameToPot.isNotEmpty()

        // Add a TextWatcher to listen for changes in the inputName EditText
        inputName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Enable or disable the button based on whether there is text in the input
                buttonAddPlayer.isEnabled = s?.isNotEmpty() == true
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
                    playersEntered.append("$player\n")
                }
            }
            textViewPlayers.text = playersEntered.toString()
            updateStartRoundButtonState()
        }
    }

    override fun onPause() {
        super.onPause()
        globalVariable.saveData()
    }
}
