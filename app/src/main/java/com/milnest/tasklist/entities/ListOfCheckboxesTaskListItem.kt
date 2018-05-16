package com.milnest.tasklist.entities

/**
 * Created by t-yar on 24.04.2018.
 */

class ListOfCheckboxesTaskListItem(id: Int, name: String, type: Int, var cbList: List<CheckboxTaskListItem>?) : TaskListItem(id, name, type)
