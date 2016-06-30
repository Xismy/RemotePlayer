package com.example.iye19.remoteplayer;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by iye19 on 26/04/2016.
 */
public class Volume extends Fragment implements Runnable{
    private int cont = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.volume_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setVisibility(View.INVISIBLE);
    }

    public void show() {
        super.onResume();
        Handler handler = new Handler();
        getView().setVisibility(View.VISIBLE);
        cont++;
        handler.postDelayed(this, 1000);
    }

    @Override
    public void run() {
        if(--cont==0)
            getView().setVisibility(View.INVISIBLE);
    }
}
