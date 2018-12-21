package com.dorm.muro.dormitory.presentation.main;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dorm.muro.dormitory.R;
import com.dorm.muro.dormitory.presentation.todo.TodoFragment;
import com.dorm.muro.dormitory.presentation.options.OptionsFragment;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.dorm.muro.dormitory.presentation.schedule.ScheduleFragment;

@InjectViewState
public class MainActivityPresenter extends MvpPresenter<MainActivityView> {
    void showTodoFragment() {
        getViewState().showFragment(new TodoFragment(), R.string.fragment_todo_title);
    }

    void showScheduleFragment() {
        getViewState().showFragment(new ScheduleFragment(), R.string.fragment_schedule_title);
    }

    void showPaymentFragment() {
        getViewState().showFragment(new PaymentFragment(), R.string.fragment_payment_title);
    }

    void showSettingsFragment() {
        getViewState().showFragment(new OptionsFragment(), R.string.fragment_options_title);
    }
}
