package com.example.pigkeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        var globalVariable = GlobalData.instance
        //Save rules to globalData
    }
}