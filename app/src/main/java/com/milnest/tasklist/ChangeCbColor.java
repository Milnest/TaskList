package com.milnest.tasklist;

import android.content.res.ColorStateList;
import android.support.v4.widget.CompoundButtonCompat;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by t-yar on 26.04.2018.
 */

public class ChangeCbColor {
    private static int states[][] = {{android.R.attr.state_checked}, {}};
    private static int colors[] = {R.color.black, R.color.gray};

    public static void change(CompoundButton compoundButton){
        CompoundButtonCompat.setButtonTintList(compoundButton, new ColorStateList(states, colors));
    }
}
