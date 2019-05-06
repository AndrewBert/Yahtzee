/**
 * Created by Andrew Bertino
 * Started 2/23/19
 * CS490
 */
package com.example.yahtzee

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_solo.*

class SoloActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solo)

        val player1ScoreTextView = findViewById<TextView>(R.id.player1ScoreTextView)


        //initializing the roll button and play button
        val rollButton = findViewById<Button>(R.id.versusButton)
        val playButton = findViewById<Button>(R.id.playButton)
        val nextTurnButton = findViewById<Button>(R.id.nextTurnButton)


        //initializing dice buttons
        val dice1Button = findViewById<ImageButton>(R.id.dice1Button)
        val dice2Button = findViewById<ImageButton>(R.id.dice2Button)
        val dice3Button = findViewById<ImageButton>(R.id.dice3Button)
        val dice4Button = findViewById<ImageButton>(R.id.dice4Button)
        val dice5Button = findViewById<ImageButton>(R.id.dice5Button)


        //initializing the Dice items
        val dice1 = Dice(dice1Button)
        val dice2 = Dice(dice2Button)
        val dice3 = Dice(dice3Button)
        val dice4 = Dice(dice4Button)
        val dice5 = Dice(dice5Button)
        //slapping the dice items into this list of type Dice
        val diceList = listOf(dice1, dice2, dice3, dice4, dice5)

        val numPlayers = 1
        val playerList = mutableListOf<Player>()
        val thisPlayersTurn = 0
        val maxNumberOfRounds = 14

        for (n in 0 until numPlayers) {
            playerList.add(Player("Player ${n + 1}", createScoreSheet(), 0, 0, false))
        }

        var turnCount = 1
        var roundCount = 1

        //when you click the ROLL button
        rollButton.setOnClickListener {



            //make the score boxes clickable again unless they are saved already
            playerList[thisPlayersTurn].playerScoreSheet.forEach { n ->
                if (!n.isSaved) {
                    n.button.isClickable = true
                }
            }


            //player can only roll 3 times
            when {
                turnCount <= 3 -> {
                    rollButton.text = "Roll #${turnCount + 1}"
                    startNewRole(
                        diceList,
                        playerList,
                        turnCount,
                        thisPlayersTurn,
                        this@SoloActivity
                    )
                    if (turnCount == 3) {
                        rollButton.text = "No more rolls!"
                    }
                    turnCount++

                }
            }
        }




        playButton.setOnClickListener {
            var itemIsSelected = false
            playerList[thisPlayersTurn].playerScoreSheet.forEach { n ->
                if (n.isSelected && !n.isSaved) {
                    itemIsSelected = true
                }
            }
            //makes sure a roll has happened and there is actually something selected
            val context: Context = this
            if (turnCount > 1 && itemIsSelected) {
                playerList[thisPlayersTurn].playerScoreSheet.forEachIndexed { index, scoreBox ->
                    if (scoreBox.isSelected) {
                        scoreBox.isSaved = true
                    }
                    if (scoreBox.isSaved) {
                        if (!scoreBox.isCalculated) {
                            //add to total score
                            playerList[thisPlayersTurn].totalScore += scoreBox.value
                            //if its in the upper section
                            if (index <= 5) {
                                playerList[thisPlayersTurn].upperTotalScore += scoreBox.value
                            }
                            scoreBox.isCalculated = true

                        }


                        scoreBox.button.setBackgroundResource(R.drawable.button_background_orange)
                        scoreBox.button.setTextColor(ContextCompat.getColor(context,R.color.colorWhite))

                    } else {
                        scoreBox.button.setBackgroundResource(R.drawable.button_background_white)
                        scoreBox.button.setTextColor(ContextCompat.getColor(context,R.color.colorOrange))
                    }
                }

                player1ScoreTextView.text = "${playerList[0].totalScore}"

                activateUpperBonus(playerList, thisPlayersTurn)
                updateSectionBonusScore(playerList, thisPlayersTurn)

                playButton.visibility = (View.INVISIBLE)
                playButton.isClickable = false

                nextTurnButton.visibility = (View.VISIBLE)
                nextTurnButton.isClickable = true

                rollButton.isClickable = false

            }
        }



        nextTurnButton.setOnClickListener {

            turnCount = 1
            roundCount++
            nextTurn(playerList, thisPlayersTurn, diceList)


            //Displays end game button
            if(roundCount >= maxNumberOfRounds) {
                endGameMessage(playerList)

            }

            rollButton.text = "ROLL #1"

            player1ScoreTextView.text = "${playerList[0].totalScore}"


            nextTurnButton.visibility = (View.INVISIBLE)
            nextTurnButton.isClickable = false

            playButton.isClickable = true
            playButton.visibility = (View.VISIBLE)

            rollButton.isClickable = true
        }

        backToHomeButton.setOnClickListener {
            onBackPressed()

        }


    }

    override fun onBackPressed(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to quit?")
        .setCancelable(true)
        .setNegativeButton("No") { dialog, which ->
                dialog.cancel()

        }

        .setPositiveButton("Yes"){dialog, which ->
                finish()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun diceRollAnimation(dice: Dice){
        val anim = AnimationUtils.loadAnimation(this, R.anim.shake)
        val value = dice.value


        val animationListener: Animation.AnimationListener = object : Animation.AnimationListener{

            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                val res = resources.getIdentifier("dice_$value", "drawable", "com.example.yahtzee")

                dice.button.setBackgroundResource(res)

            }

            override fun onAnimationRepeat(animation: Animation?) {}
        }

        anim.setAnimationListener(animationListener)
        dice.button.startAnimation(anim)

    }

    private fun endGameMessage(playerList: MutableList<Player>){

        val intent = Intent(this,SoloEndGameActivity::class.java)
        intent.putExtra("player1Score",playerList[0].totalScore)
        startActivity(intent)

    }

    @SuppressLint("SetTextI18n")
    private fun updateSectionBonusScore(playersScoreSheet: MutableList<Player>, thisPlayersTurn: Int){
        val upperTotalScore = playersScoreSheet[thisPlayersTurn].upperTotalScore
        sectionBonusTextView.text = "BONUS : $upperTotalScore/63"

        if(upperTotalScore>=63){
            sectionBonusTextView.text = "BONUS +35!"
        }
    }

    private fun activateUpperBonus(playersScoreSheet: MutableList<Player>, thisPlayersTurn: Int)
    {
        if(playersScoreSheet[thisPlayersTurn].upperTotalScore >= 63){
            playersScoreSheet[thisPlayersTurn].upperScoreBonus = true
            if(playersScoreSheet[thisPlayersTurn].upperScoreBonus&&!playersScoreSheet[thisPlayersTurn].upperScoreBonusActivated){
                playersScoreSheet[thisPlayersTurn].totalScore += 35
            }
            if(playersScoreSheet[thisPlayersTurn].upperScoreBonus){

                playersScoreSheet[thisPlayersTurn].upperScoreBonusActivated = true
            }
        }
    }

    //activates dice buttons
    private fun startNewRole(diceList: List<Dice>, playersScoreSheet: MutableList<Player>, turnCount: Int, thisPlayersTurn: Int, context: Context) {
        Toast.makeText(this,"Player ${thisPlayersTurn+1} Turn #$turnCount",Toast.LENGTH_SHORT).show()

        if(turnCount==1)
        {
            diceList.forEach { n->
                n.isSelected = false
                n.value = 0
            }
        }


        for(n in 0..4) {
            //create a random number
            val rng = (1..6).random()
            //set the button text to the random number
            if(!diceList[n].isSelected) {
                //diceList[n].button.text = rng.toString()
                diceList[n].value = rng
            }
        }


        for(dice in diceList){
            if(!dice.isSelected) {
                diceRollAnimation(dice)
            }
        }


        //set click listeners for all of the dice buttons in an efficient loop
        diceList.forEach { n-> n.button.setOnClickListener{
            if(!n.isSelected) {
                n.isSelected=true
                val res = resources.getIdentifier("dice_selected_${n.value}", "drawable", "com.example.yahtzee")
                n.button.setBackgroundResource(res)

            }
            else{
                n.isSelected=false
                val res = resources.getIdentifier("dice_${n.value}", "drawable", "com.example.yahtzee")
                n.button.setBackgroundResource(res)

            }
        }}

        //set click listeners for all of the score buttons in an efficient loop
        playersScoreSheet[thisPlayersTurn].playerScoreSheet.forEach { n -> n.button.setOnClickListener{
            if(!n.isSaved) {
                if (!n.isSelected) {
                    //makes sure you can only select one button at a time
                    playersScoreSheet[thisPlayersTurn].playerScoreSheet.forEach { n ->
                        /*if(!n.isSaved) {
                            n.isSelected = false
                            n.button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
                        }*/
                        n.isSelected = false
                        if(!n.isSaved) {
                            //n.button.setBackgroundColor(ContextCompat.getColor(context,R.color.colorWhite))
                            n.button.setBackgroundResource(R.drawable.button_background_white)
                            n.button.setTextColor(ContextCompat.getColor(context,R.color.colorOrange))
                        }
                    }
                    n.isSelected = true
                    //n.button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSavedButton))
                } else {
                    n.isSelected = false
                    //n.button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
                }
                if(n.isSelected)
                {
                    //n.button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSavedButton))
                    n.button.setBackgroundResource(R.drawable.button_background_orange_selected)
                    n.button.setTextColor(ContextCompat.getColor(context,R.color.colorWhite))

                }
                else {
                    //n.button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                    n.button.setBackgroundResource(R.drawable.button_background_white)
                    n.button.setTextColor(ContextCompat.getColor(context,R.color.colorOrange))
                }


            }
        }}
        calcScore(diceList,playersScoreSheet[thisPlayersTurn].playerScoreSheet)

        //calculates and returns total score
        playersScoreSheet[thisPlayersTurn].totalScore = calcPlayerTotalScore(playersScoreSheet, thisPlayersTurn)
    }

    @TargetApi(24)
    private fun calcScore(diceList: List<Dice>, playerScoreSheet: List<ScoreBox>){
        val frequenciesOfNumbers = diceList.groupingBy { it.value }.eachCount()
        var sumOfAllDice = 0


        //makes sure the values reset to 0 if they are not saved
        playerScoreSheet.forEach { n ->
            if(!n.isSaved) {
                n.value = 0
            }
        }
        //determines the points in the upper section
        for(n in 0 until diceList.size){
            when(diceList[n].value){
                1 -> {if(!playerScoreSheet[0].isSaved)
                {playerScoreSheet[0].value+=1}
                    sumOfAllDice+=1}
                2 -> {if(!playerScoreSheet[1].isSaved)
                {playerScoreSheet[1].value+=2}
                    sumOfAllDice+=2}
                3 -> {if(!playerScoreSheet[2].isSaved)
                {playerScoreSheet[2].value+=3}
                    sumOfAllDice+=3}
                4 -> {if(!playerScoreSheet[3].isSaved)
                {playerScoreSheet[3].value+=4}
                    sumOfAllDice+=4}
                5 -> {if(!playerScoreSheet[4].isSaved)
                {playerScoreSheet[4].value+=5}
                    sumOfAllDice+=5}
                6 -> {if(!playerScoreSheet[5].isSaved)
                {playerScoreSheet[5].value+=6}
                    sumOfAllDice+=6}
            }
        }
        //chance
        if(!playerScoreSheet[12].isSaved){
            playerScoreSheet[12].value = sumOfAllDice
        }

        //checks and calculates 3x, 4x, full house,  and yahtzee
        frequenciesOfNumbers.forEach{ (number, frequency) ->
            //three Of A Kind check
            if(frequency >= 3){
                if(!playerScoreSheet[6].isSaved){
                    playerScoreSheet[6].value = sumOfAllDice}

                //full house check

                if(frequenciesOfNumbers.size == 2 && frequency==3){
                    if(!playerScoreSheet[8].isSaved){
                        playerScoreSheet[8].value = 25}
                }
            }

            //four Of A Kind check
            if(frequency >= 4){
                if(!playerScoreSheet[7].isSaved){
                    playerScoreSheet[7].value = sumOfAllDice
                }
            }

            //yahtzee check
            if(frequency == 5){
                if(!playerScoreSheet[11].isSaved){
                    playerScoreSheet[11].value = 50
                }
            }

            //large straight check
            if(frequenciesOfNumbers.size == 5)
            {
                //dice values
                val listOfValues  = mutableListOf<Int>()

                for(n in 0 until diceList.size) {
                    listOfValues.add(diceList[n].value)
                }
                //sorted list of dice values
                val sortedListOfValues = listOfValues.sorted()

                if(sortedListOfValues == mutableListOf(1,2,3,4,5) || sortedListOfValues == mutableListOf(2,3,4,5,6)) {
                    if(!playerScoreSheet[9].isSaved){
                        playerScoreSheet[9].value = 30
                    }
                    if(!playerScoreSheet[10].isSaved){
                        playerScoreSheet[10].value = 40
                    }
                }
            }

            //small straight check
            if(frequenciesOfNumbers.size >= 4)
            {
                //dice values
                val listOfValues  = mutableListOf<Int>()

                for(n in 0 until diceList.size) {

                    listOfValues.add(diceList[n].value)
                }

                //sorted list of distinct dice values
                val sortedDistinctListOfValues = listOfValues.sorted().distinct()

                //all possible small straight combos
                if(sortedDistinctListOfValues == mutableListOf(1,2,3,4) || sortedDistinctListOfValues == mutableListOf(1,2,3,4,5) ||
                    sortedDistinctListOfValues == mutableListOf(1,2,3,4,6) || sortedDistinctListOfValues == mutableListOf(2,3,4,5) ||
                    sortedDistinctListOfValues == mutableListOf(1,3,4,5,6) || sortedDistinctListOfValues == mutableListOf(3,4,5,6)) {
                    if(!playerScoreSheet[9].isSaved){playerScoreSheet[9].value = 30}
                }
            }
        }



        //displays the value on the score buttons
        for(n in 0 until playerScoreSheet.size) {
            //stops the number from updating if you have selected to save it, might be a bad implementation bc
            //the numbers still change in the background
            playerScoreSheet[n].button.text = playerScoreSheet[n].value.toString()
        }

    }

    private fun calcPlayerTotalScore(playersScoreSheet: MutableList<Player>, thisPlayersTurn: Int): Int{

        var totalScore = playersScoreSheet[thisPlayersTurn].totalScore
        for(scoreBox in playersScoreSheet[thisPlayersTurn].playerScoreSheet) {
            if(scoreBox.isSaved&&!scoreBox.isCalculated) {
                scoreBox.isCalculated = true
                totalScore += scoreBox.value

            }
        }

        return totalScore

    }



    private fun createScoreSheet(): List<ScoreBox>{
        val onesButton = findViewById<Button>(R.id.onesButton)
        val twosButton = findViewById<Button>(R.id.twosButton)
        val threesButton = findViewById<Button>(R.id.threesButton)
        val foursButton = findViewById<Button>(R.id.foursButton)
        val fivesButton = findViewById<Button>(R.id.fivesButton)
        val sixesButton = findViewById<Button>(R.id.sixesButton)
        val threeOfAKindButton = findViewById<Button>(R.id.threeOfAKindButton)
        val fourOfAKindButton = findViewById<Button>(R.id.fourOfAKindButton)
        val fullHouseButton = findViewById<Button>(R.id.fullHouseButton)
        val smallStraightButton = findViewById<Button>(R.id.smallStraightButton)
        val largeStraightButton = findViewById<Button>(R.id.largeStraightButton)
        val yahtzeeButton = findViewById<Button>(R.id.yahtzeeButton)
        val chanceButton = findViewById<Button>(R.id.chanceButton)

        //initializing score boxes
        val upperScoreBox1 = ScoreBox(onesButton)
        val upperScoreBox2 = ScoreBox(twosButton)
        val upperScoreBox3 = ScoreBox(threesButton)
        val upperScoreBox4 = ScoreBox(foursButton)
        val upperScoreBox5 = ScoreBox(fivesButton)
        val upperScoreBox6 = ScoreBox(sixesButton)
        //initializing lower score boxes
        val lowerScoreBox1 = ScoreBox(threeOfAKindButton)
        val lowerScoreBox2 = ScoreBox(fourOfAKindButton)
        val lowerScoreBox3 = ScoreBox(fullHouseButton)
        val lowerScoreBox4 = ScoreBox(smallStraightButton)
        val lowerScoreBox5 = ScoreBox(largeStraightButton)
        val lowerScoreBox6 = ScoreBox(yahtzeeButton)
        val lowerScoreBox7 = ScoreBox(chanceButton)

        //returns score-sheet to player
        return listOf(upperScoreBox1,upperScoreBox2,upperScoreBox3,
            upperScoreBox4,upperScoreBox5,upperScoreBox6, lowerScoreBox1,
            lowerScoreBox2, lowerScoreBox3, lowerScoreBox4, lowerScoreBox5,
            lowerScoreBox6, lowerScoreBox7)
    }

    private fun nextTurn(playerList: MutableList<Player>, thisPlayersTurn: Int,diceList: List<Dice>){

        //supposed to make all saved boxes green and non saved ones white
        playerList[thisPlayersTurn].playerScoreSheet.forEach { n ->
            if(n.isSaved)
            {
                n.button.setBackgroundResource(R.drawable.button_background_orange)
                n.button.setTextColor(ContextCompat.getColor(this,R.color.colorWhite))
            }
            else
                n.button.setBackgroundResource(R.drawable.button_background_white)
            n.button.setTextColor(ContextCompat.getColor(this,R.color.colorOrange))
        }

        diceList.forEach { n->
            n.button.isClickable = false
        }

        playerList[thisPlayersTurn].playerScoreSheet.forEach { n->
            //shows appropriate saved score-boxes
            if(n.isSaved) {
                n.button.text = n.value.toString()
                n.button.setTextColor(ContextCompat.getColor(this,R.color.colorWhite))
            }
            else{
                n.button.text = "0"
            }
            n.button.isClickable = false
        }
    }

//todo change random algorithm because its pseudo-random??
    /**
     * add end game screen
     * add home menu
     * add data persistence for highest scores
     * add option to play against a bot
     *
     */

    /**
     * LOGS
     * Version: 0.1 Development
     *
     *
     */
}