package com.dorm.muro.dormitory.network.UserSessionManagement;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import io.reactivex.subjects.PublishSubject;

public interface IUserSessionManager {
    Task<AuthResult> authenticate(String email, String password);

    Task<AuthResult> registerNewUser(String email, String password);

    Task<Void> updateUserEmail(String newEmail);

    PublishSubject<String[]> getUserInfo();

    Task<Void> updateUserField(Map<String, Object> newValues);

    Task<Void> sendRestoreEmail(String email);

    FirebaseUser getCurrentUser();

    Task<Void> updateUserPassword(String newPassword);


    void logout();
}
