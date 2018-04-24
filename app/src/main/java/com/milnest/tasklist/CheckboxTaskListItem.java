package com.milnest.tasklist;

import java.util.List;

/**
 * Created by t-yar on 24.04.2018.
 */

public class CheckboxTaskListItem /*extends TaskListItem*/ {
    private boolean mCbState;
    private String mCbText;

    public CheckboxTaskListItem(String cbText, boolean cbState) {
        mCbState = cbState;
        mCbText = cbText;
    }

    public boolean isCbState() {
        return mCbState;
    }

    public void setCbState(boolean cbState) {
        mCbState = cbState;
    }

    public String getCbText() {
        return mCbText;
    }

    public void setCbText(String cbText) {
        mCbText = cbText;
    }
}
