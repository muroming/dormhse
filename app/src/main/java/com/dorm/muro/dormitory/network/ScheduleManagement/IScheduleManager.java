package com.dorm.muro.dormitory.network.ScheduleManagement;

import com.dorm.muro.dormitory.presentation.schedule.ScheduleCell;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import io.reactivex.subjects.PublishSubject;

public interface IScheduleManager {
    String createRoom(String userKey, String roomNum, String roomId);

    PublishSubject<Boolean> roomExists(String roomId);

    PublishSubject<String> joinRoom(String userKey, String roomId);

    void leaveRoom(String userKey, String roomId);

    String uploadDuty(String flatKey, int roomNumber, ScheduleCell startDate, ScheduleCell endDate);

    PublishSubject<String> checkIfUserInRoom(String userId);

    void updateDuty(String dutyKey, int roomNumber, ScheduleCell startDate, ScheduleCell endDate);

    void removeDuty(String roomKey, String dutyKey);

    DatabaseReference getFlatDuties(String flatKey);

    PublishSubject<Map<String, Object>> getDutyByKey(String dutyKey);
}