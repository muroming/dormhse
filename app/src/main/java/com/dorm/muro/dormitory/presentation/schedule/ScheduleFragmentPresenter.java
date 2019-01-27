package com.dorm.muro.dormitory.presentation.schedule;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.network.ScheduleManagement.ScheduleManagement;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.dorm.muro.dormitory.Constants.*;

@InjectViewState
public class ScheduleFragmentPresenter extends MvpPresenter<ScheduleFragmentView> implements ChildEventListener {

    // Calendar State
    private ScheduleCell rangeStartDate;
    private ScheduleCell rangeEndDate;
    private ROOM_NUM currentRoom;

    private CompositeDisposable disposable = new CompositeDisposable();


    // current displayed month
    private Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    private boolean isEditing = false, isAdding = false;
    private String dutyKey;

    //todo inject
    private SharedPreferences preferences;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        if (!preferences.getBoolean(SIGNED_IN_ROOM, false)) {
            String userKey = UserSessionManager.getInstance().getCurrentUser().getUid();
            disposable.add(ScheduleManagement.getInstance().checkIfUserInRoom(userKey)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(roomKey -> {
                        if (roomKey != null && !roomKey.isEmpty()) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean(SIGNED_IN_ROOM, true);
                            editor.putString(ROOM_KEY, roomKey);
                            editor.apply();


                            showCalendar();
                        } else {
                            getViewState().showNoRoom();
                        }
                    }));
        } else {
            showCalendar();
        }
    }

    void showCalendar() {
        getViewState().updateCalendar(currentDate.get(Calendar.MONTH));
        getViewState().setOptions(null, R.id.menu_add);
        getViewState().showCalendar();
    }

    void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    void onCreateRoomClicked(String flatNum, String flatId) {
        //todo move all managers to dagger injection
        disposable.add(ScheduleManagement.getInstance().roomExists(flatId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(exists -> {
                    if (!exists) {
                        String userKey = UserSessionManager.getInstance().getCurrentUser().getUid();
                        String roomKey = ScheduleManagement.getInstance().createRoom(userKey, flatNum, flatId);

                        preferences.edit().putBoolean(SIGNED_IN_ROOM, true).apply();
                        preferences.edit().putString(ROOM_KEY, roomKey).apply();

                        getViewState().closeDialog();
                        showCalendar();
                    } else {
                        getViewState().showToast(R.string.schedule_room_id_exists);
                    }
                }));
    }

    void onJoinRoomClicked(String flatId) {
        String userKey = UserSessionManager.getInstance().getCurrentUser().getUid();
        disposable.add(ScheduleManagement.getInstance().joinRoom(userKey, flatId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    if (!res.isEmpty()) {
                        showCalendar();
                        preferences.edit().putBoolean(SIGNED_IN_ROOM, true).apply();
                        preferences.edit().putString(ROOM_KEY, res).apply();
                    } else {
                        getViewState().showToast(R.string.schedule_room_not_found);
                    }
                }));
    }

    void onRoomClicked(ROOM_NUM roomNum) {
        if (!isEditing) {
            getViewState().setRoomSelected(roomNum);
            currentRoom = roomNum;
        }
    }

    void onShowNextMonth() {
        currentDate.add(Calendar.MONTH, 1);
        getViewState().updateCalendar(currentDate.get(Calendar.MONTH));
    }

    void onShowPrevMonth() {
        currentDate.add(Calendar.MONTH, -1);
        getViewState().updateCalendar(currentDate.get(Calendar.MONTH));
    }

    void onDateClicked(ScheduleCell date, @Nullable ScheduleCell start, @Nullable ScheduleCell end) {
        if (isEditing) { // If clicked while adding a duty
            if (date.getRoomNum() != currentRoom && date.getState() != ScheduleFragment.CELL_STATE.NONE) {
                return;
            }
            if (rangeStartDate != null) {  // If first date is selected
                if (!rangeStartDate.equals(date)) { // If we selected different from first date
                    if (rangeEndDate != null) { // If end date is selected
                        if (!rangeEndDate.equals(date)) { // And different from end date update range bounds
                            showSelectedRange(date);
                        }
                    } else {  // Else set date as end
                        showSelectedRange(date);
                    }
                }
            } else { // Else set date as first
                rangeStartDate = date;
                date.setState(ScheduleFragment.CELL_STATE.START);
                date.setRoomNum(currentRoom);
                getViewState().updateDate(date, currentRoom);
            }
        } else {
            if (date.getState() != ScheduleFragment.CELL_STATE.NONE) {  //Clicked on date
                dutyKey = preferences.getString(String.valueOf(start.getDate().getTime()), "");

                isEditing = true;
                currentRoom = date.getRoomNum();
                rangeStartDate = start;
                rangeEndDate = end;
                getViewState().setTitle(R.string.schedule_edit_duty);
                getViewState().setOptions(R.drawable.ic_close, R.id.menu_delete, R.id.menu_apply);
            }
        }
    }

    private void showSelectedRange(ScheduleCell date) {
        if (rangeEndDate == null) {  // Create range
            rangeEndDate = date;
            if (rangeStartDate.getDate().getTime() < rangeEndDate.getDate().getTime()) { // Manage date order
                rangeEndDate.setState(ScheduleFragment.CELL_STATE.END);
                getViewState().showDutyRange(rangeStartDate, rangeEndDate, currentRoom);
            } else {
                ScheduleCell temp = rangeEndDate;
                rangeEndDate = rangeStartDate;
                rangeStartDate = temp;

                rangeStartDate.setState(ScheduleFragment.CELL_STATE.START);
                rangeEndDate.setState(ScheduleFragment.CELL_STATE.END);

                getViewState().showDutyRange(rangeStartDate, rangeEndDate, currentRoom);
            }
        } else {  // Update range
            long startDelta = Math.abs(date.getDate().getTime() - rangeStartDate.getDate().getTime()),
                    endDelta = Math.abs(date.getDate().getTime() - rangeEndDate.getDate().getTime());

            date.setRoomNum(currentRoom);

            if (endDelta < startDelta) { // Update end date
                date.setState(ScheduleFragment.CELL_STATE.END);
                getViewState().updateEnd(date, rangeEndDate);
                rangeEndDate = date;
            } else {  //Update start date
                date.setState(ScheduleFragment.CELL_STATE.START);
                getViewState().updateStart(date, rangeStartDate);
                rangeStartDate = date;
            }
        }
    }

    void applyRange() {
        if (isAdding) {
            saveDuty();
        } else {
            updateDuty();
        }

        stopEditing();
    }

    void addRange(ScheduleCell start, ScheduleCell end) {
        getViewState().showDutyRange(start, end, start.getRoomNum());

        rangeStartDate = start;
        rangeEndDate = end;
        saveDuty();
        stopEditing();
    }

    void deleteRange() {
        getViewState().deleteRange(rangeStartDate, rangeEndDate);
        getViewState().showRangeDeleteSnackbar(rangeStartDate, rangeEndDate);

        removeDuty();
        stopEditing();
    }

    void saveDuty() {
        String flatKey = preferences.getString(ROOM_KEY, "");
        String pushKey = ScheduleManagement.getInstance().uploadDuty(flatKey, currentRoom.get(), rangeStartDate, rangeEndDate);

        writeDutyToPrefs(pushKey, rangeStartDate, rangeEndDate);
    }

    void updateDuty() {
        ScheduleManagement.getInstance().updateDuty(dutyKey, currentRoom.get(), rangeStartDate, rangeEndDate);
        preferences.edit().remove(dutyKey).apply();
        writeDutyToPrefs(dutyKey, rangeStartDate, rangeEndDate);
    }

    void removeDuty() {
        String dutyKey = preferences.getString(String.valueOf(rangeStartDate.getDate().getTime()), "");
        String roomKey = preferences.getString(ROOM_KEY, "");

        ScheduleManagement.getInstance().removeDuty(roomKey, dutyKey);
        preferences.edit().remove(String.valueOf(rangeStartDate.getDate().getTime())).apply();
    }

    private void writeDutyToPrefs(String dutyKey, ScheduleCell rangeStartDate, ScheduleCell rangeEndDate) {
        preferences.edit().putString(String.valueOf(rangeStartDate.getDate().getTime()), dutyKey).apply();
        preferences.edit().putString(dutyKey, String.valueOf(rangeStartDate.getDate().getTime()) + " " + String.valueOf(rangeEndDate.getDate().getTime())).apply();
    }

    private void stopEditing() {
        isEditing = false;
        isAdding = false;
        rangeStartDate = null;
        rangeEndDate = null;
        dutyKey = "";

        getViewState().setOptions(null, R.id.menu_add);
        getViewState().setTitle(R.string.fragment_schedule_title);
    }

    void startAddingDuty() {
        isEditing = true;
        isAdding = true;
        getViewState().setOptions(R.drawable.ic_close, R.id.menu_apply);
        getViewState().setTitle(R.string.schedule_add_duty);
    }

    void onEditStop() {  // when user presses X while editing just stop w/o saving to DB
        if (isAdding) {
            if (rangeStartDate != null) {
                if (rangeEndDate == null) {
                    rangeStartDate.setState(ScheduleFragment.CELL_STATE.NONE);
                    getViewState().updateDate(rangeStartDate, currentRoom);
                } else {
                    getViewState().deleteRange(rangeStartDate, rangeEndDate);
                }
            }
        }

        stopEditing();
    }

    void loadDuties() {
        String roomKey = preferences.getString(ROOM_KEY, "");
        ScheduleManagement.getInstance().getFlatDuties(roomKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
                };
                Map<String, String> duties = dataSnapshot.getValue(t);

                if (duties == null) {
                    return;
                }

                for (String dutyKey : duties.values()) {
                    disposable.add(ScheduleManagement.getInstance().getDutyByKey(dutyKey)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(duty -> {
                                if (duty != null) {
                                    ROOM_NUM roomNum = ROOM_NUM.getRoomNum(Math.toIntExact((Long) duty.get(DUTY_ROOM)));
                                    ScheduleCell start = (ScheduleCell) duty.get(DUTY_START);
                                    ScheduleCell end = (ScheduleCell) duty.get(DUTY_END);


                                    getViewState().showDutyRange(start, end, roomNum);
                                }
                            }));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void initScheduleListener() {
        String flatKey = preferences.getString(ROOM_KEY, "");
        ScheduleManagement.getInstance().getFlatDuties(flatKey).addChildEventListener(this);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        String dutyKey = dataSnapshot.getValue(String.class);

        if (dutyKey == null) {
            return;
        }

        disposable.add(ScheduleManagement.getInstance().getDutyByKey(dutyKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(duty -> {
                    if (duty != null) {
                        ROOM_NUM roomNum = ROOM_NUM.getRoomNum(Math.toIntExact((Long) duty.get(DUTY_ROOM)));
                        ScheduleCell start = (ScheduleCell) duty.get(DUTY_START);
                        ScheduleCell end = (ScheduleCell) duty.get(DUTY_END);

                        writeDutyToPrefs(dutyKey, start, end);
                        getViewState().showDutyRange(start, end, roomNum);
                    }
                }));
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        loadDuties();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        String dutyKey = dataSnapshot.getValue(String.class);
        String times = preferences.getString(dutyKey, "");
        if (times == null || times.isEmpty())
            return;

        long startTime = Long.parseLong(times.split(" ")[0]), endTime = Long.parseLong(times.split(" ")[1]);
        getViewState().deleteRange(new Date(startTime), new Date(endTime));
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
    }

    @Override
    public void onDestroy() {
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
