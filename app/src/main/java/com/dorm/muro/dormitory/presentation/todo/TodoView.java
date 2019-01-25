package com.dorm.muro.dormitory.presentation.todo;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

public interface TodoView extends MvpView {
    void setItems(List<TodoItem> items);

    void removeItemAt(int position);

    void returnItem(TodoItem item, int position);

    void addItem(TodoItem item);

    void showTodoDialog(TodoItem item);
    void pinItem(TodoItem item);
    void unpinItem(TodoItem item);
}