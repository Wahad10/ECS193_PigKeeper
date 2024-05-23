package com.ucdavis.pigkeeper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class NewPlayersActivity : AppCompatActivity() {
    //data that needs to be passed to other functions
    lateinit var globalVariable : GlobalData
    private lateinit var textViewPlayers : TextView
    private var namesArray = ArrayList<String>()
    private val playersEntered = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_players)

        globalVariable = GlobalData.instance

        textViewPlayers = findViewById<TextView>(R.id.textViewPlayers)
        val inputName = findViewById<EditText>(R.id.inputName)

        val buttonAddPlayer = findViewById<Button>(R.id.buttonAddPlayer)
        buttonAddPlayer.setOnClickListener {
            val currentPlayerEntered = inputName.getText().toString()
            if(currentPlayerEntered.isNotEmpty() && !namesArray.contains(currentPlayerEntered) && namesArray.size < 10){
                namesArray.add(currentPlayerEntered)
                playersEntered.append("$currentPlayerEntered\n")
                inputName.getText().clear()
                textViewPlayers.text = playersEntered.toString()
            }
        }

        val buttonStartRound = findViewById<Button>(R.id.buttonStartRound)
        buttonStartRound.setOnClickListener{
            if(namesArray.isNotEmpty()){
                globalVariable.players = namesArray
                startActivity(Intent(this@NewPlayersActivity, TurnOrderActivity::class.java))
            }
        }

        val buttonLoadLastPlayers = findViewById<Button>(R.id.buttonLoadLastPlayers)
        buttonLoadLastPlayers.setOnClickListener{
            loadLastPlayers()
        }

        //automatically load last players if same game session just new round
        if(!globalVariable.endedGameSession){
            loadLastPlayers()
        }
    }

    private fun loadLastPlayers(){
        //DO I WANT TO BE ABLE TO LOAD LAST PLAEYRS IF CLOSE APP MID FIRST ROUND?
        //CURRENTLY CAN ONLY LOAD LAST PLAYERS IF I FINISHED AT LEAST ONE ROUND ALL THE WAY WITH THEM
        //If there was a previous game, get the players
        if(globalVariable.nameToPot.size !=0){
            //to load all last players, even the ones sitting out
            for((player, sit) in globalVariable.sittingOut){
                if(!namesArray.contains(player)) {
                    namesArray.add(player)
                    playersEntered.append("$player\n")
                }
            }
            textViewPlayers.text = playersEntered.toString()
        }
    }

    override fun onPause() {
        super.onPause()
        globalVariable.saveData()
    }
}