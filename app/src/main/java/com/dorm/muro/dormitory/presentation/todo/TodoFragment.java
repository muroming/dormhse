package com.dorm.muro.dormitory.presentation.todo;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.arellomobile.mvp.MvpAppCompatFragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.network.TodoManagement.TodoManager;
import com.dorm.muro.dormitory.presentation.createTodo.CreateTodoActivity;

import java.util.Date;
import java.util.List;

import static com.dorm.muro.dormitory.Constants.*;

public class TodoFragment extends MvpAppCompatFragment implements TodoView, TodoAdapter.OnTodoClicked, TodoItemTouchHelper.TodoItemTouchHelperListener {

    @InjectPresenter
    TodoPresenter presenter;

    private TodoAdapter adapter;
    private FrameLayout rootLayout;
    private TodoItem lastDeleted;

    private final int TODO_REQUEST_CODE = 123;

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
        rootLayout = v.findViewById(R.id.fl_todo_root);

        RecyclerView rv = v.findViewById(R.id.rv_todo_list);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        adapter.setListener(this);
        rv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        ItemTouchHelper.SimpleCallback itemTouchCallback = new TodoItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(rv);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TODO_REQUEST_CODE && resultCode == TODO_CREATED) {
            TodoItem item = (TodoItem) data.getSerializableExtra(TODO_SERIALIZED);
            presenter.addTodo(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.menu_add).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add: {
                Intent intent = new Intent(getContext(), CreateTodoActivity.class);
                startActivityForResult(intent, TODO_REQUEST_CODE);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void removeItemAt(int position) {
        adapter.removeItem(position);
    }

    @Override
    public void returnItem(TodoItem item, int position) {
        adapter.insertItem(item, position);
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        lastDeleted = adapter.getTodoItem(position);
        presenter.finishTodo(position);
        Snackbar snackbar = Snackbar.make(rootLayout, R.string.todo_done, Snackbar.LENGTH_SHORT);

        TodoManager.getInstance().removeTodo(lastDeleted);
        TodoManager.getInstance().unassignTask(lastDeleted);

        snackbar.setAction(R.string.cancel, v -> {
            presenter.returnTodo(lastDeleted, position);
            TodoManager.getInstance().uploadTodo(lastDeleted);
            TodoManager.getInstance().assignTask(lastDeleted);
        });
        snackbar.show();
    }

    @Override
    public void addItem(TodoItem item) {
        adapter.addItem(item);
    }
}
