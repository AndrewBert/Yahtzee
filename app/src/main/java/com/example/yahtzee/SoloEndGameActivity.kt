package com.example.yahtzee

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.TextView

class SoloEndGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solo_end_game)


        val player1Score = intent.getIntExtra("player1Score", 999)


        var winningPlayerNumber = 0

        /**UI ELEMENTS**/
        val player1ScoreTV = findViewById<TextView>(R.id.player1FinalScoreTV)

        player1ScoreTV.text = player1Score.toString()

        val newGameButton = findViewById<Button>(R.id.versusButton)

        newGameButton.setOnClickListener {
            val intent = Intent(this, SoloActivity::class.java)
            startActivity(intent)
        }

        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)

        mainMenuButton.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }



        /**UI ELEMENTS END**/









    }
}
