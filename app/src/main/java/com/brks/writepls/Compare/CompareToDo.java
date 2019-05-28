package com.brks.writepls.Compare;

import com.brks.writepls.ToDoList.ToDo;

import java.util.Comparator;

public class CompareToDo implements Comparator<ToDo> {

    @Override
    public int compare(ToDo o1, ToDo o2) {

        return  -(o1.getImportance() - o2.getImportance());
    }
}
