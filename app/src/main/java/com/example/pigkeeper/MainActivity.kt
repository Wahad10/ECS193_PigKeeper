package com.example.pigkeeper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonPlay = findViewById<Button>(R.id.buttonPlay)
        buttonPlay.setOnClickListener{startActivity(Intent(this@MainActivity, NewPlayersActivity::class.java))}

        val buttonRules = findViewById<Button>(R.id.buttonRules)
        buttonRules.setOnClickListener{startActivity(Intent(this@MainActivity, RulesActivity::class.java))}

        val buttonLastPot = findViewById<Button>(R.id.buttonLastPot)
        buttonLastPot.setOnClickListener{startActivity(Intent(this@MainActivity, LastPotActivity::class.java))}
    }
}