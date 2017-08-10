package com.example.leo.asynctaskchainbuilder.mvp.interfaces;

/**
 * Created by Leonardo on 10-Aug-17.
 */

public interface MainActivityMvp {

    interface IMainActivity {

    }

    interface IMainPresenter{

        void setMainActivity(IMainActivity mainActivity);
    }

    interface IProgressListener{
        void registerProgress(Integer result);
    }
}
