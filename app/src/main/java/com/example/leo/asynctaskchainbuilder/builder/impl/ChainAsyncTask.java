package com.example.leo.asynctaskchainbuilder.builder.impl;

import android.os.AsyncTask;

import com.example.leo.asynctaskchainbuilder.builder.interfaces.ITaskFinishListener;
import com.example.leo.asynctaskchainbuilder.builder.interfaces.IChainAsyncTask;

/**
 * Created by leo on 09/08/17.
 */

public abstract class ChainAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> implements IChainAsyncTask {

    private ITaskFinishListener onFinishListener;

    @Override
    public void setAsyncTaskFinishListener(ITaskFinishListener listener) {
        onFinishListener = listener;
    }
}
