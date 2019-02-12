package com.dorm.muro.dormitory.presentation.todo;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.network.TodoManagement.ITodoManager;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class TodoPresenter extends MvpPresenter<TodoView> {
    private Disposable loadTodos;
    private ITodoManager mTodoManager;

    @Inject
    public TodoPresenter(ITodoManager mTodoManager) {
        this.mTodoManager = mTodoManager;
    }

    void loadTodos() {
        loadTodos = mTodoManager.loadTodos()
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

    void finishTodo(TodoItem item, int position) {
        getViewState().removeItemAt(position);


        mTodoManager.removeTodo(item);
        mTodoManager.unassignTask(item);
    }

    void returnTodo(TodoItem item, int position) {
        getViewState().returnItem(item, position);

        mTodoManager.uploadTodo(item);
        mTodoManager.assignTask(item);
    }

    void pinTodo(TodoItem item) {
        if(item.isPinned()) {
            item.setPinned(false);
            getViewState().unpinItem(item);
        } else {
            item.setPinned(true);
            getViewState().pinItem(item);
        }
        mTodoManager.updateTodo(item);
    }

    @Override
    public void onDestroy() {
        if (loadTodos != null) {
            loadTodos.dispose();
            loadTodos = null;
        }
    }
}
