package com.dorm.muro.dormitory.presentation.createTodo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dorm.muro.dormitory.Constants;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.network.TodoManagement.TodoManager;
import com.dorm.muro.dormitory.presentation.todo.TodoItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dorm.muro.dormitory.Constants.*;

public class CreateTodoActivity extends AppCompatActivity {

    @BindView(R.id.et_todo_title)
    EditText mTodoTitle;

    @BindView(R.id.et_todo_description)
    EditText mTodoDescription;

    @BindView(R.id.tv_button_todo_deadline)
    TextView mTodoDeadline;

    private Date mDeadline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_todo_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_apply: {

                if (chechTodo()) {
                    String todoTitle = mTodoTitle.getText().toString(), todoDescription = mTodoDescription.getText().toString();
                    TodoItem todoItem = new TodoItem(todoTitle, todoDescription, mDeadline);
                    TodoManager.getInstance().uploadTodo(todoItem);
                    TodoManager.getInstance().assignTask(todoItem);

                    Intent todoData = new Intent();
                    todoData.putExtra(TODO_TITLE, todoTitle);
                    todoData.putExtra(TODO_DEADLINE, mDeadline.getTime());
                    todoData.putExtra(TODO_DESCRIPTION, todoDescription);
                    setResult(Constants.TODO_CREATED, todoData);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.todo_create_fields_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case android.R.id.home: {
                setResult(Constants.TODO_CANCELED);
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menu_apply).setVisible(true);
        return true;
    }

    private boolean chechTodo() {
        return !mTodoTitle.getText().toString().isEmpty()
                && !mTodoDeadline.getText().toString().equals(getString(R.string.field_not_set));
    }

    @OnClick(R.id.tv_button_todo_deadline)
    void chooseDateDeadline() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            mDeadline = new Date(year, month, dayOfMonth);
            mTodoDeadline.setText(String.format("%d.%d.%d", dayOfMonth, month, year));
        });
        datePickerDialog.show();
    }
}
