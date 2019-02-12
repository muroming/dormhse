package com.dorm.muro.dormitory.network.TodoManagement;

import android.support.annotation.NonNull;

import com.dorm.muro.dormitory.presentation.todo.TodoItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

import static com.dorm.muro.dormitory.Constants.*;

public class TodoManager implements ITodoManager {
    private DatabaseReference mDatabase;

    @Inject
    public TodoManager(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public Task<Void> uploadTodo(TodoItem item) {
        String key = mDatabase.child(TODOS_DATABASE).push().getKey();
        item.setKey(key);
        return mDatabase.child(TODOS_DATABASE).child(key).setValue(item);
    }

    public Task<Void> assignTask(TodoItem item) {
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String todoPushKey = mDatabase.child(USERS_TODOS_DATABASE).child(userKey).push().getKey();
        return mDatabase.child(USERS_TODOS_DATABASE).child(userKey).child(todoPushKey).setValue(item.getKey());
    }

    public Task<Void> unassignTask(TodoItem item) {
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return mDatabase.child(USERS_TODOS_DATABASE).child(userKey).orderByValue().equalTo(item.getKey()).getRef().removeValue();
    }

    public Task<Void> removeTodo(TodoItem item) {
        return mDatabase.child(TODOS_DATABASE).child(item.getKey()).removeValue();
    }

    public Task<Void> updateTodo(TodoItem item) {
        return mDatabase.child(TODOS_DATABASE).child(item.getKey()).setValue(item);
    }

    public PublishSubject<TodoItem> loadTodos() {
        PublishSubject<TodoItem> subject = PublishSubject.create();
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child(USERS_TODOS_DATABASE).child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> todoKeys = (HashMap<String, String>) dataSnapshot.getValue();
                if (todoKeys == null)
                    return;

                for (String todoKey : todoKeys.values()) {
                    mDatabase.child(TODOS_DATABASE).child(todoKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            subject.onNext(dataSnapshot.getValue(TodoItem.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return subject;
    }
}
