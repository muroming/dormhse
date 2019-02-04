package com.dorm.muro.dormitory.presentation.schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.main.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dorm.muro.dormitory.Constants.*;


enum ROOM_NUM {
    FIRST(0), SECOND(1), THIRD(2), FORTH(3);
    private final int value;

    ROOM_NUM(int v) {
        value = v;
    }

    public int get() {
        return value;
    }

    public static ROOM_NUM getRoomNum(int v) {
        switch (v) {
            case 0:
                return FIRST;
            case 1:
                return SECOND;
            case 2:
                return THIRD;
            case 3:
                return FORTH;
            default:
                return null;
        }
    }
}

public class ScheduleFragment extends MvpAppCompatFragment implements ScheduleFragmentView {

    private static final int DAYS_COUNT = 42;

    public enum CELL_STATE {START, MEDIUM, END, NONE}

    //Grid adapter
    private CalendarAdapter gridAdapter;

    // internal components
    @BindView(R.id.iv_schedule_prev_month)
    ImageView btnPrev;

    @BindView(R.id.iv_schedule_next_month)
    ImageView btnNext;

    @BindView(R.id.tv_schedule_date_title)
    TextView txtDate;

    @BindView(R.id.gv_schedule_grid)
    GridView grid;


    @BindViews({R.id.tv_schedule_room_1, R.id.tv_schedule_room_2, R.id.tv_schedule_room_3, R.id.tv_schedule_room_4})
    List<TextView> mRooms;

    @BindView(R.id.tv_schedule_commentary)
    TextView mRoomCommentary;

    @BindView(R.id.no_room_group)
    View mNoRoomGroup;

    @BindView(R.id.schedule_calendar_group)
    View mScheduleCalendarGroup;

    @BindView(R.id.cl_schedule_root_view)
    ConstraintLayout mRootView;

    private int currentSelectedRoom;
    private int[] menuIds;
    private Integer upButton;
    private AlertDialog dialog;

    @InjectPresenter
    ScheduleFragmentPresenter presenter;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //todo inject
        SharedPreferences preferences = getContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        presenter.setPreferences(preferences);
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);

        gridAdapter = new CalendarAdapter(getContext(), this);
        grid.setAdapter(gridAdapter);
        presenter.onRoomClicked(ROOM_NUM.FIRST);

        addListeners();

        currentSelectedRoom = 0;
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            presenter.checkUserInRoom();
        }
    }

    private void addListeners() {
        btnNext.setOnClickListener(v -> {
            presenter.onShowNextMonth();
        });

        btnPrev.setOnClickListener(v -> {
            presenter.onShowPrevMonth();
        });
    }

    @OnClick({R.id.tv_schedule_room_1, R.id.tv_schedule_room_2, R.id.tv_schedule_room_3, R.id.tv_schedule_room_4})
    public void onRoomClicked(TextView room) {
        int roomNum = mRooms.indexOf(room);
        presenter.onRoomClicked(ROOM_NUM.values()[roomNum]);
    }

    @OnClick(R.id.btn_create_room)
    public void onCreateRoomClicked(Button b) {
        showCreateRoomDialog();
    }

    @OnClick(R.id.btn_join_room)
    public void onJoinRoomClicked(Button b) {
        showJoinRoomDialog();
    }

    @Override
    public void setRoomSelected(ROOM_NUM roomNum) {
        mRooms.get(currentSelectedRoom).setBackgroundResource(0);
        mRooms.get(currentSelectedRoom).setTextColor(Color.BLACK);

        TextView mRoom = mRooms.get(roomNum.get());

        mRoom.setBackgroundResource(R.drawable.selected_room);
        GradientDrawable background = (GradientDrawable) mRoom.getBackground();

        switch (roomNum) {
            case FIRST: {
                background.setColor(ContextCompat.getColor(getContext(), R.color.first_room_selection_bg));
                mRoom.setTextColor(getResources().getColor(R.color.first_room_selection_text, null));
                break;
            }
            case SECOND: {
                background.setColor(ContextCompat.getColor(getContext(), R.color.second_room_selection_bg));
                mRoom.setTextColor(getResources().getColor(R.color.second_room_selection_text, null));
                break;
            }
            case THIRD: {
                background.setColor(ContextCompat.getColor(getContext(), R.color.third_room_selection_bg));
                mRoom.setTextColor(getResources().getColor(R.color.third_room_selection_text, null));
                break;
            }
            case FORTH: {
                background.setColor(ContextCompat.getColor(getContext(), R.color.forth_room_selection_bg));
                mRoom.setTextColor(getResources().getColor(R.color.forth_room_selection_text, null));
                break;
            }

        }

        currentSelectedRoom = roomNum.get();
        mRoomCommentary.setText("Set commentary for room" + mRoom.getText().toString());
    }

    @Override
    public void updateCalendar(int month) {
        ArrayList<ScheduleCell> cells = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, month);

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(new ScheduleCell(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        gridAdapter.setCurrentMonth(month);
        gridAdapter.setDays(cells);

        calendar.set(Calendar.MONTH, month);
        txtDate.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

        presenter.loadDuties();
    }

    @Override
    public void showDutyRange(ScheduleCell start, ScheduleCell end, ROOM_NUM roomNum) {
        gridAdapter.setDateRangeDuty(start.getDate(), end.getDate(), roomNum);
    }

    @Override
    public void updateStart(ScheduleCell newStart, ScheduleCell prevStart) {
        gridAdapter.updateStartDuty(prevStart.getDate(), newStart.getDate(), newStart.getRoomNum());
    }

    @Override
    public void updateEnd(ScheduleCell newEnd, ScheduleCell prevEnd) {
        gridAdapter.updateEndDuty(prevEnd.getDate(), newEnd.getDate(), newEnd.getRoomNum());
    }

    @Override
    public void onDateClicked(ScheduleCell date, @Nullable ScheduleCell start, @Nullable ScheduleCell end) {
        presenter.onDateClicked(date, start, end);
    }

    @Override
    public void updateDate(ScheduleCell cell, ROOM_NUM roomNum) {
        gridAdapter.updateDate(cell);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add: {
                presenter.startAddingDuty();
                return true;
            }
            case R.id.menu_apply: {
                presenter.applyRange();
                return true;
            }
            case R.id.menu_delete: {
                presenter.deleteRange();
                return true;
            }
            case android.R.id.home: {
                presenter.onEditStop();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        if (menuIds != null) {
            for (int id : menuIds) {
                menu.findItem(id).setVisible(true);
            }
        }
        if (upButton != null) {
            ((MainActivity) getActivity()).setUpButton(upButton);
        } else {
            ((MainActivity) getActivity()).hideUpButton();
        }
    }

    @Override
    public void setTitle(int titleId) {
        ((MainActivity) getActivity()).showTitle(titleId);
    }

    @Override
    public void setOptions(@Nullable Integer upButton, int... ids) {
        this.upButton = upButton;
        this.menuIds = ids;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).hideUpButton();
    }

    @Override
    public void deleteRange(ScheduleCell start, ScheduleCell end) {
        gridAdapter.deleteRange(start.getDate(), end.getDate());
    }

    @Override
    public void deleteRange(Date start, Date end) {
        gridAdapter.deleteRange(start, end);
    }

    @Override
    public void showRangeDeleteSnackbar(ScheduleCell start, ScheduleCell end) {
        Snackbar bar = Snackbar.make(getView(), getString(R.string.schedule_duty_deleted), Snackbar.LENGTH_SHORT);
        ((FrameLayout.LayoutParams) bar.getView().getLayoutParams()).setMargins(
                getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin),
                0,
                getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin),
                getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin) + getResources().getDimensionPixelSize(R.dimen.navigation_height)
        );

        bar.setAction(R.string.cancel, v -> {
            presenter.addRange(start, end);
        });

        bar.show();
    }

    public void showCreateRoomDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        EditText flatNum = new EditText(getContext());
        EditText flatId = new EditText(getContext());

        layout.setPadding(getResources().getDimensionPixelOffset(R.dimen.room_dialog_horizontal_margin), 0,
                getResources().getDimensionPixelOffset(R.dimen.room_dialog_horizontal_margin), 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        flatNum.setMaxLines(1);
        flatNum.setInputType(InputType.TYPE_CLASS_NUMBER);
        flatNum.setHint(R.string.schedule_room_number_hint);
        flatNum.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) flatNum.getLayoutParams()).bottomMargin = getResources().getDimensionPixelOffset(R.dimen.room_dialog_vertical_margin);
        ((LinearLayout.LayoutParams) flatNum.getLayoutParams()).topMargin = getResources().getDimensionPixelOffset(R.dimen.room_dialog_vertical_margin);

        flatId.setMaxLines(1);
        flatId.setInputType(InputType.TYPE_CLASS_TEXT);
        flatId.setHint(R.string.schedule_room_id_hint);
        flatId.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) flatId.getLayoutParams()).bottomMargin = getResources().getDimensionPixelOffset(R.dimen.room_dialog_vertical_margin);


        layout.addView(flatNum);
        layout.addView(flatId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(layout)
                .setTitle(R.string.title_create_room)
                .setPositiveButton(R.string.create, (dialog, which) -> {
                    presenter.onCreateRoomClicked(flatNum.getText().toString(), flatId.getText().toString());
                })
                .setNegativeButton(R.string.cancel, ((dialog, which) -> closeDialog()));

        dialog = builder.create();
        dialog.show();
    }

    public void showJoinRoomDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        EditText flatId = new EditText(getContext());

        layout.setPadding(getResources().getDimensionPixelOffset(R.dimen.room_dialog_horizontal_margin), 0,
                getResources().getDimensionPixelOffset(R.dimen.room_dialog_horizontal_margin), 0);

        flatId.setMaxLines(1);
        flatId.setInputType(InputType.TYPE_CLASS_TEXT);
        flatId.setHint(R.string.schedule_room_id_hint);
        flatId.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) flatId.getLayoutParams()).bottomMargin = getResources().getDimensionPixelOffset(R.dimen.room_dialog_vertical_margin);
        ((LinearLayout.LayoutParams) flatId.getLayoutParams()).topMargin = getResources().getDimensionPixelOffset(R.dimen.room_dialog_vertical_margin);

        layout.addView(flatId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(layout)
                .setTitle(R.string.title_join_room)
                .setPositiveButton(R.string.join, (dialog, which) -> {
                    presenter.onJoinRoomClicked(flatId.getText().toString());
                })
                .setNegativeButton(R.string.cancel, ((dialog, which) -> closeDialog()));

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void showNoRoom() {
        mNoRoomGroup.setVisibility(View.VISIBLE);
        mScheduleCalendarGroup.setVisibility(View.INVISIBLE);
        mRootView.setBackground(getResources().getDrawable(R.drawable.schedule_no_room_bg, null));
    }

    @Override
    public void showCalendar() {
        presenter.loadDuties();
        presenter.initScheduleListener();
        mNoRoomGroup.setVisibility(View.INVISIBLE);
        mScheduleCalendarGroup.setVisibility(View.VISIBLE);
        mRootView.setBackground(null);    }

    @Override
    public void updateCalendar() {
        gridAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(int text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}