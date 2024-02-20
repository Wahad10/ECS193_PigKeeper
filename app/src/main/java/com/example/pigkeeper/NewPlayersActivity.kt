package com.example.pigkeeper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class NewPlayersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_players)

        val textViewPlayers = findViewById<TextView>(R.id.textViewPlayers)
        val inputName = findViewById<EditText>(R.id.inputName)
        val namesArray = ArrayList<String>()
        val playersEntered = StringBuilder()


        val buttonAddPlayer = findViewById<Button>(R.id.buttonAddPlayer)
        buttonAddPlayer.setOnClickListener {
            val currentPlayerEntered = inputName.getText().toString()
            namesArray.add(currentPlayerEntered)
            playersEntered.append("$currentPlayerEntered\n")
            inputName.getText().clear()
            textViewPlayers.text = playersEntered.toString()
        }

        val buttonStartRound = findViewById<Button>(R.id.buttonStartRound)
        buttonStartRound.setOnClickListener{
            startActivity(Intent(this@NewPlayersActivity, TurnOrderActivity::class.java))
        }

        val buttonLoadLastPlayers = findViewById<Button>(R.id.buttonLoadLastPlayers)
        //need to get the last players names here
    }
}