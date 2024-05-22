package com.ucdavis.pigkeeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LastPotActivity : AppCompatActivity() {
    lateinit var globalVariable : GlobalData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_pot)


        globalVariable = GlobalData.instance
        var playerPot = globalVariable.nameToPot

        val textViewLastPotScores = findViewById<TextView>(R.id.textViewLastPotScores)
        val formattedScores = StringBuilder()
        for ((player, pot) in playerPot) {
            formattedScores.append("$player: $pot\n")
        }
        textViewLastPotScores.text = formattedScores.toString()

        val buttonBack = findViewById<Button>(R.id.buttonLastPotBack)
        buttonBack.setOnClickListener{startActivity(Intent(this@LastPotActivity, MainActivity::class.java))}
    }

    override fun onPause() {
        super.onPause()
        globalVariable.saveData()
    }
}