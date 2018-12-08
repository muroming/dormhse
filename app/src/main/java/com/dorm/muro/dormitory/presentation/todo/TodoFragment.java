package com.dorm.muro.dormitory.presentation.todo;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.arellomobile.mvp.MvpAppCompatFragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.R;

import java.util.List;

public class TodoFragment extends MvpAppCompatFragment implements TodoView, TodoAdapter.OnTodoClciked {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    }

    @Override
    public void OnClick(TodoItem item) {
        presenter.todoClicked(item);
    }
}
