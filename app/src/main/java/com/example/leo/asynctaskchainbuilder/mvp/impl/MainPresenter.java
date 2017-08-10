package com.example.leo.asynctaskchainbuilder.mvp.impl;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.os.AsyncTask;

import com.example.leo.asynctaskchainbuilder.builder.impl.AsyncTaskChainBuilder;
import com.example.leo.asynctaskchainbuilder.builder.impl.ChainAsyncTask;
import com.example.leo.asynctaskchainbuilder.builder.interfaces.ITaskFinishListener;
import com.example.leo.asynctaskchainbuilder.mvp.interfaces.MainActivityMvp;

import java.lang.ref.WeakReference;
import java.util.Random;

import static com.example.leo.asynctaskchainbuilder.mvp.interfaces.MainActivityMvp.*;

/**
 * Created by Leonardo on 10-Aug-17.
 */

public class MainPresenter extends BaseObservable implements IMainPresenter, IProgressListener {

    private final Integer MIN = 1;
    private final Integer MAX = 15;
    Random rand = new Random();

    private WeakReference<MainActivityMvp.IMainActivity> mainActivityWeakReference;

    public ObservableArrayList<Integer> listNumbers = new ObservableArrayList<>();
    public ObservableBoolean isButtonEnabled = new ObservableBoolean(true);

    @Override
    public void setMainActivity(IMainActivity mainActivity){
        mainActivityWeakReference = new WeakReference<MainActivityMvp.IMainActivity>(mainActivity);
    }

    public void generateNumbers(){
        listNumbers.clear();
        isButtonEnabled.set(false);
        new AsyncTaskChainBuilder()
            .startWith(new RandomCounterAsyncTask(this))
                .withParameters(generateRandomSeed)
                .withExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            .thenExecute(new RandomCounterAsyncTask(this))
                .withParameters(generateRandomSeed)
                .withExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            .thenExecute(new RandomCounterAsyncTask(this))
                .withParameters(generateRandomSeed)
                .withExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            .thenExecute(new RandomCounterAsyncTask(this))
                .withParameters(generateRandomSeed)
                .withExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            .finishWith(new ITaskFinishListener() {
                @Override
                public void onTaskFinish() {
                    isButtonEnabled.set(true);
                }
            })
            .executeChain();
    }

    private AsyncTaskChainBuilder.IArgumentProvider generateRandomSeed = new AsyncTaskChainBuilder.IArgumentProvider() {
        @Override
        public Object[] provide() {
            return new Object[]{
                rand.nextInt((MAX - MIN) + 1) + MIN
                , rand.nextInt((MAX - MIN) + 1) + MIN
                , rand.nextInt((MAX - MIN) + 1) + MIN
                , rand.nextInt((MAX - MIN) + 1) + MIN
                , rand.nextInt((MAX - MIN) + 1) + MIN
            };
        }
    };

    @Override
    public void registerProgress(Integer result) {
        listNumbers.add(result);
    }

    private class RandomCounterAsyncTask extends ChainAsyncTask<Object, Integer, Void> {

        private IProgressListener listeningActivity;

        public RandomCounterAsyncTask(IProgressListener listeningActivity) {
            this.listeningActivity = listeningActivity;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            Integer int1 = (Integer) objects[0];
            Integer int2 = (Integer) objects[1];
            Integer int3 = (Integer) objects[2];
            Integer int4 = (Integer) objects[3];
            Integer int5 = (Integer) objects[4];

            for (int x = 0; x < 5; x++) {
                Integer div = rand.nextInt((MAX - MIN) + 1) + MIN;
                registerProgress((int1 + int2 + int3 + int4 + int5) / div);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            listeningActivity.registerProgress(values[0]);
        }
    }
}
