/**
 * Created by Andrew Bertino
 * Started 2/23/19
 * CS490
 */
package com.example.yahtzee

import android.annotation.TargetApi
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


data class Player(val name: String, val playerScoreSheet: List<ScoreBox>,
                  var upperTotalScore: Int, var totalScore: Int, var upperScoreBonus: Boolean = false)
data class Dice(val button: Button, var isSelected: Boolean = false, var value: Int)
data class ScoreBox(val button: Button, var value: Int = 0, var isSelected: Boolean = false, var isSaved: Boolean = false, var isCalculated: Boolean = false)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val player1ScoreTextView = findViewById<TextView>(R.id.player1ScoreTextView)
        val player2ScoreTextView = findViewById<TextView>(R.id.player2ScoreTextView)

        //initializing the roll button and play button
        val rollButton = findViewById<Button>(R.id.rollButton)
        val playButton = findViewById<Button>(R.id.playButton)
        val nextTurnButton = findViewById<Button>(R.id.nextTurnButton)
        //initializing dice buttons
        val dice1Button = findViewById<Button>(R.id.dice1Button)
        val dice2Button = findViewById<Button>(R.id.dice2Button)
        val dice3Button = findViewById<Button>(R.id.dice3Button)
        val dice4Button = findViewById<Button>(R.id.dice4Button)
        val dice5Button = findViewById<Button>(R.id.dice5Button)

        //initializing the Dice items
        val dice1 = Dice(dice1Button, false, 0)
        val dice2 = Dice(dice2Button, false, 0)
        val dice3 = Dice(dice3Button, false, 0)
        val dice4 = Dice(dice4Button, false, 0)
        val dice5 = Dice(dice5Button, false, 0)
        //slapping the dice items into this list of type Dice
        val diceList = listOf(dice1, dice2, dice3, dice4, dice5)

        val numPlayers = 2
        val playerList = mutableListOf<Player>()
        var thisPlayersTurn = 0

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
                                numPlayers,
                                thisPlayersTurn,
                                this@MainActivity
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
                if (turnCount > 1 && itemIsSelected) {
                    playerList[thisPlayersTurn].playerScoreSheet.forEachIndexed { index, scoreBox ->
                        if (scoreBox.isSelected) {
                            scoreBox.isSaved = true
                        }
                        if (scoreBox.isSaved) {
                            if (!scoreBox.isCalculated) {
                                //add to total score
                                playerList[thisPlayersTurn].totalScore += scoreBox.value
                                scoreBox.isCalculated = true
                            }


                            //if its in the upper section
                            if (index <= 5) {
                                playerList[thisPlayersTurn].upperTotalScore += scoreBox.value
                            }

                            scoreBox.button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

                        } else {
                            scoreBox.button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
                        }
                    }

                    player1ScoreTextView.text = "Player 1's Score: ${playerList[0].totalScore}"
                    player2ScoreTextView.text = "Player 2's Score: ${playerList[1].totalScore}"


                    playButton.visibility = (View.INVISIBLE)
                    playButton.isClickable = false

                    nextTurnButton.visibility = (View.VISIBLE)
                    nextTurnButton.isClickable = true

                }
            }
            nextTurnButton.setOnClickListener {
                when {
                    //goes to next players turn
                    (thisPlayersTurn + 1) < numPlayers -> {
                        thisPlayersTurn++
                        turnCount = 1
                        nextTurn(playerList, thisPlayersTurn, diceList)

                    }
                    //goes to the first players turn
                    (thisPlayersTurn + 1) == numPlayers -> {
                        thisPlayersTurn = 0
                        turnCount = 1
                        nextTurn(playerList, thisPlayersTurn, diceList)
                        roundCount++
                        Toast.makeText(this, "Round Count: $roundCount", Toast.LENGTH_SHORT).show()
                    }
                }
                rollButton.text = "ROLL #1"

                player1ScoreTextView.text = "Player 1's Score: ${playerList[0].totalScore}"
                player2ScoreTextView.text = "Player 2's Score: ${playerList[1].totalScore}"

                nextTurnButton.visibility = (View.INVISIBLE)
                nextTurnButton.isClickable = false

                playButton.isClickable = true
                playButton.visibility = (View.VISIBLE)
            }
    }


    //activates dice buttons
    private fun startNewRole(diceList: List<Dice>, playersScoreSheet: MutableList<Player>, turnCount: Int, numPlayers: Int, thisPlayersTurn: Int, context: Context) {
        Toast.makeText(this,"Player ${thisPlayersTurn+1} Turn #$turnCount",Toast.LENGTH_SHORT).show()

        if(turnCount==1)
        {
            diceList.forEach { n->
                n.isSelected = false
                n.value = 0
                n.button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))}
        }
        for(n in 0..4) {
            //create a random number
            val rng = (1..6).random()
            //set the button text to the random number
            if(!diceList[n].isSelected) {
                diceList[n].button.text = rng.toString()
                diceList[n].value = rng
            }
        }
        //set click listeners for all of the dice buttons in an efficient loop
        diceList.forEach { n-> n.button.setOnClickListener{
            if(!n.isSelected)
            {
                n.isSelected=true
                n.button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSavedButton))
            }
            else
            {
                n.isSelected=false
                n.button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))

            }
        }}

        //set click listeners for all of the upper score buttons in an efficient loop
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
                            n.button.setBackgroundColor(ContextCompat.getColor(context,R.color.colorWhite))
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
                    n.button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSavedButton))
                }
                else {
                    n.button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
            }


            }
        }}
        calcScore(diceList,playersScoreSheet[thisPlayersTurn].playerScoreSheet, playersScoreSheet[thisPlayersTurn].upperTotalScore ,playersScoreSheet[thisPlayersTurn].upperScoreBonus)

    }

    @TargetApi(24)
    private fun calcScore(diceList: List<Dice>, playerScoreSheet: List<ScoreBox>, playerUpperScore: Int, playerUpperScoreBonus: Boolean){
        var playerUpperScoreBonus = false
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
        playerScoreSheet[12].value = sumOfAllDice

        //checks and calculates 3x, 4x, full house,  and yahtzee
        frequenciesOfNumbers.forEach{ number, frequency ->
            //three Of A Kind check
            if(frequency >= 3){
                playerScoreSheet[6].value = sumOfAllDice

                //full house check
                if(frequenciesOfNumbers.size == 2 && frequency==3) {
                    playerScoreSheet[8].value = 25
                }
            }

            //four Of A Kind check
            if(frequency >= 4){
                playerScoreSheet[7].value = sumOfAllDice
            }

            //yahtzee check
            if(frequency == 5){
                playerScoreSheet[11].value = 50
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
                    playerScoreSheet[9].value = 30
                    playerScoreSheet[10].value = 40
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

                    playerScoreSheet[9].value = 30
                }
            }
            //left off here, what to do with upper score bonus and how to add the bonus only once
            if(playerUpperScore >= 63){
                playerUpperScoreBonus = true
            }



        }


        //displays the value on the upper score buttons
        for(n in 0 until playerScoreSheet.size) {
            //stops the number from updating if you have selected to save it, might be a bad implementation bc
            //the numbers still change in the background
            //if(!playerScoreSheet[n].isSaved) {}
                playerScoreSheet[n].button.text = playerScoreSheet[n].value.toString()

        }


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

                n.button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            else
                n.button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
        }

        diceList.forEach { n->
            n.button.isClickable = false
            n.button.text = "0"
            n.button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
        }

        playerList[thisPlayersTurn].playerScoreSheet.forEach { n->
            //shows appropriate saved score-boxes
            if(n.isSaved) { n.button.text = n.value.toString()}
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
}



