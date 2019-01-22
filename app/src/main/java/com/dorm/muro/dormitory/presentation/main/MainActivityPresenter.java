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
    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showPage(0, R.string.fragment_todo_title);
    }

    void showTodoFragment() {
        getViewState().showPage(0, R.string.fragment_todo_title);
    }

    void showScheduleFragment() {
        getViewState().showPage(1, R.string.fragment_schedule_title);
    }

    void showPaymentFragment() {
        getViewState().showPage(2, R.string.fragment_payment_title);
    }

    void showSettingsFragment() {
        getViewState().showPage(3, R.string.fragment_options_title);
    }
}
