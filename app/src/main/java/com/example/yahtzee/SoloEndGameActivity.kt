package com.example.yahtzee

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.TextView

class SoloEndGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solo_end_game)


        val score = intent.getIntExtra("player1Score", 999)



        val player1ScoreTV = findViewById<TextView>(R.id.player1FinalScoreTV)

        player1ScoreTV.text = score.toString()

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


        val highScoreTextView = findViewById<TextView>(R.id.highScoreTextView)
        val settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE)
        val highScore = settings.getInt("HIGH_SCORE", 0)

        if(score > highScore){
            highScoreTextView.setText("NEW HIGH SCORE : $score")
            highScoreTextView.setTextColor(ContextCompat.getColor(this,R.color.colorOrange))


            //save
            val editor = settings.edit()
            editor.putInt("HIGH_SCORE", score)
            editor.commit()
        }else{
            highScoreTextView.setText("High Score : $highScore")
        }









    }
}
