package com.dorm.muro.dormitory.presentation.todo;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.arellomobile.mvp.MvpAppCompatFragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.R;

import java.util.List;

public class TodoFragment extends MvpAppCompatFragment implements TodoView, TodoAdapter.OnTodoClicked {

    @InjectPresenter
    TodoPresenter presenter;

    private TodoAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new TodoAdapter(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo_list, container, false);
        RecyclerView rv = v.findViewById(R.id.rv_todo_list);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        adapter.setListener(this);

        presenter.loadTodos();
        return v;
    }

    @Override
    public void setItems(List<TodoItem> items) {
        adapter.setItems(items);
        adapter.notifyItemRangeInserted(0, items.size());
    }

    @Override
    public void showTodoDialog(TodoItem item) {
//todo        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Toast.makeText(getContext(), "CLICKED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(TodoItem item) {
        presenter.todoClicked(item);
    }

    @Override
    public void onLongClick(TodoItem item) {
        presenter.pinTodo(item);
    }

    @Override
    public void pinItem(TodoItem item) {
        adapter.pinItem(item);
    }

    @Override
    public void unpinItem(TodoItem item) {
        adapter.unpinItem(item);
    }
}
