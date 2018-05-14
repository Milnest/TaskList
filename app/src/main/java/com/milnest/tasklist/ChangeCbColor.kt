package com.milnest.tasklist

import android.content.res.ColorStateList
import android.support.v4.widget.CompoundButtonCompat
import android.widget.CheckBox
import android.widget.CompoundButton

/**
 * Created by t-yar on 26.04.2018.
 */

/**Меняет цвет галочки чекбокса
 */
object ChangeCbColor {
    private val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
    private val colors = intArrayOf(R.color.black, R.color.gray)

    fun change(compoundButton: CompoundButton) {
        CompoundButtonCompat.setButtonTintList(compoundButton, ColorStateList(states, colors))
    }
}
