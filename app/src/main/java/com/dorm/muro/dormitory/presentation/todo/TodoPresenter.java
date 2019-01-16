package com.dorm.muro.dormitory.presentation.todo;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.network.TodoManagement.TodoManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class TodoPresenter extends MvpPresenter<TodoView> {
    private Disposable loadTodos;

    void loadTodos() {
        loadTodos = TodoManager.getInstance().loadTodos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    getViewState().addItem(item);
                });
    }

    void addTodo(TodoItem item) {
        getViewState().addItem(item);
    }

    void todoClicked(TodoItem item) {
        getViewState().showTodoDialog(item);
    }

    void finishTodo(int position) {
        getViewState().removeItemAt(position);
    }

    void returnTodo(TodoItem item, int position) {
        getViewState().returnItem(item, position);
    }

    void pinTodo(TodoItem item) {
        if(item.isPinned()) {
            item.setPinned(false);
            getViewState().unpinItem(item);
        } else {
            item.setPinned(true);
            getViewState().pinItem(item);
        }
        TodoManager.getInstance().updateTodo(item);
    }

    @Override
    public void onDestroy() {
        if (loadTodos != null) {
            loadTodos.dispose();
            loadTodos = null;
        }
    }
}
