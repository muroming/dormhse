package com.dorm.muro.dormitory.presentation.todo;


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
import android.widget.TextView;
import android.widget.Toast;


import com.arellomobile.mvp.MvpAppCompatFragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.dagger.Injector;
import com.dorm.muro.dormitory.network.TodoManagement.TodoManager;
import com.dorm.muro.dormitory.presentation.createTodo.CreateTodoActivity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import static com.dorm.muro.dormitory.Constants.*;

public class TodoFragment extends MvpAppCompatFragment implements TodoView, TodoAdapter.OnTodoClicked, TodoItemTouchHelper.TodoItemTouchHelperListener {

    @Inject
    @InjectPresenter
    TodoPresenter presenter;

    @ProvidePresenter
    TodoPresenter providePresenter() {
        return presenter;
    }

    private TodoAdapter adapter;
    private FrameLayout rootLayout;
    private TodoItem lastDeleted;

    private final int TODO_REQUEST_CODE = 123;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Injector.getInstance().getPresenterComponent().inject(this);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        presenter.loadTodos();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo_list, container, false);
        rootLayout = v.findViewById(R.id.fl_todo_root);

        RecyclerView rv = v.findViewById(R.id.rv_todo_list);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));

        adapter = new TodoAdapter(getContext());
        rv.setAdapter(adapter);
        adapter.setListener(this);

        ItemTouchHelper.SimpleCallback itemTouchCallback = new TodoItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(rv);

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
        TextView noTodos = rootLayout.findViewById(R.id.tv_no_todos);
        if (noTodos.getVisibility() == View.INVISIBLE) {
            noTodos.setVisibility(View.VISIBLE);
        }

        adapter.removeItem(position);
    }

    @Override
    public void returnItem(TodoItem item, int position) {
        TextView noTodos = rootLayout.findViewById(R.id.tv_no_todos);
        if (noTodos.getVisibility() == View.VISIBLE) {
            noTodos.setVisibility(View.INVISIBLE);
        }

        adapter.insertItem(item, position);
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        lastDeleted = adapter.getTodoItem(position);
        presenter.finishTodo(lastDeleted, position);
        Snackbar snackbar = Snackbar.make(rootLayout, R.string.todo_done, Snackbar.LENGTH_SHORT);

        snackbar.setAction(R.string.cancel, v -> presenter.returnTodo(lastDeleted, position));
        snackbar.show();
    }

    @Override
    public void addItem(TodoItem item) {
        TextView noTodos = rootLayout.findViewById(R.id.tv_no_todos);
        if (noTodos.getVisibility() == View.VISIBLE) {
            noTodos.setVisibility(View.INVISIBLE);
        }

        adapter.addItem(item);
    }
}
