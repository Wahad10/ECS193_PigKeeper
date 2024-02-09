package com.example.pigkeeper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class NewPlayersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_players)

        val namesArray = ArrayList<String>()

        val buttonStartRound = findViewById<Button>(R.id.buttonStartRound)
        buttonStartRound.setOnClickListener{
            println("here")
            for(name in namesArray){
                println(name)
            }
            startActivity(Intent(this@NewPlayersActivity, TurnOrderRollActivity::class.java))
        }

        val inputName = findViewById<EditText>(R.id.inputName)

        val buttonNextPlayer = findViewById<Button>(R.id.buttonNextPlayer)
        buttonNextPlayer.setOnClickListener {
            namesArray.add(inputName.getText().toString())
            inputName.getText().clear()
        }
    }
}