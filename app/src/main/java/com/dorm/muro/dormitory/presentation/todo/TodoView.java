package com.dorm.muro.dormitory.presentation.todo;

import com.arellomobile.mvp.MvpView;

import java.util.List;

public interface TodoView extends MvpView {
    void setItems(List<TodoItem> items);
    void showTodoDialog(TodoItem item);
}
