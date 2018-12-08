package com.dorm.muro.dormitory.presentation.todo;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@InjectViewState
public class TodoPresenter extends MvpPresenter<TodoView> {
    void loadTodos() {//todo make this w/ dagger
        List<TodoItem> items = new ArrayList<TodoItem>(3);
        items.add(new TodoItem("title1", "commentary1", new Date()));
        items.add(new TodoItem("title2", "commentary2", new Date()));
        items.add(new TodoItem("title3", "reallylongcommentaryreallylongcommentaryreallylongcommen" +
                "taryreallylongcommentaryreallylongcommentaryreallylongcomme", new Date()));
        getViewState().setItems(items);
    }

    void todoClicked(TodoItem item) {
        getViewState().showTodoDialog(item);
    }
}
