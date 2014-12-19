package com.sanshisoft.findfunny.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanshisoft.findfunny.R;
import com.sanshisoft.findfunny.ui.MainActivity;

public class AboutFragment extends Fragment {

    private MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MainActivity)getActivity();
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        int i = getArguments().getInt(MainActivity.ARG_DRAWER_NUMBER);
        String title = getResources().getStringArray(R.array.drawer_array)[i];
        mActivity.setTitle(title);
        return rootView;
    }

}
