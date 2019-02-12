package com.dorm.muro.dormitory.network.TodoManagement;

import com.dorm.muro.dormitory.presentation.todo.TodoItem;
import com.google.android.gms.tasks.Task;

import io.reactivex.subjects.PublishSubject;

public interface ITodoManager {
    Task<Void> uploadTodo(TodoItem item);

    Task<Void> assignTask(TodoItem item);

    Task<Void> unassignTask(TodoItem item);

    Task<Void> removeTodo(TodoItem item);

    Task<Void> updateTodo(TodoItem item);

    PublishSubject<TodoItem> loadTodos();
}
