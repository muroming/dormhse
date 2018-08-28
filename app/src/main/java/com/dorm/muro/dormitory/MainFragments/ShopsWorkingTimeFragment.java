package com.dorm.muro.dormitory.MainFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dorm.muro.dormitory.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopsWorkingTimeFragment extends Fragment {


    public ShopsWorkingTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shops_working_time, container, false);
    }

}
