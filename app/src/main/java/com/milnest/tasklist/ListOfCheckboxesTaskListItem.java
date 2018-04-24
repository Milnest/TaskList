package com.milnest.tasklist;

import java.util.List;

/**
 * Created by t-yar on 24.04.2018.
 */

public class ListOfCheckboxesTaskListItem extends TaskListItem {
    private List<CheckboxTaskListItem> mCbList;

    public ListOfCheckboxesTaskListItem(int id, String name, int type, List<CheckboxTaskListItem> cbList) {
        super(id, name, type);
        mCbList = cbList;
    }

    public List<CheckboxTaskListItem> getCbList() {
        return mCbList;
    }

    public void setCbList(List<CheckboxTaskListItem> cbList) {
        mCbList = cbList;
    }
}
