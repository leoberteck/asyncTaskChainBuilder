package com.example.leo.asynctaskchainbuilder.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.databinding.library.baseAdapters.BR;
import com.example.leo.asynctaskchainbuilder.R;
import com.example.leo.asynctaskchainbuilder.mvp.impl.MainPresenter;
import com.example.leo.asynctaskchainbuilder.mvp.interfaces.MainActivityMvp;

public class MainActivity extends AppCompatActivity  {

    MainActivityMvp.IMainPresenter mainPresenter = new MainPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewDataBinding.setVariable(BR.presenter, mainPresenter);
    }
}
