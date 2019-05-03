package com.example.yahtzee

import android.widget.ImageButton

data class Dice(val button: ImageButton, var isSelected: Boolean = false, var value: Int = 0)