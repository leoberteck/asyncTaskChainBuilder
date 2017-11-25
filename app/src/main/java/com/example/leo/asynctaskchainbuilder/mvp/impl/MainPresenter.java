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

public class MainPresenter extends BaseObservable implements IMainPresenter, IProgressListener {

    private final static int MIN = 1;
    private final static int MAX = 15;
    private final static Random rand = new Random();

    private WeakReference<MainActivityMvp.IMainActivity> mainActivityWeakReference;

    public ObservableArrayList<Integer> listNumbers = new ObservableArrayList<>();
    public ObservableBoolean isButtonEnabled = new ObservableBoolean(true);

    @Override
    public void setMainActivity(IMainActivity mainActivity){
        mainActivityWeakReference = new WeakReference<>(mainActivity);
    }

    public void generateNumbers(){
        listNumbers.clear();
        isButtonEnabled.set(false);
        new AsyncTaskChainBuilder()
            //Primeira linha
            .startWith(new RandomCounterAsyncTask(this))
                .withParameters(generateRandomSeed)
            //Segunda linha
            .thenExecute(new RandomCounterAsyncTask(this))
                .withParameters(generateRandomSeed)
            //Terceira linha
            .thenExecute(new RandomCounterAsyncTask(this))
                .withParameters(generateRandomSeed)
            //Quarta linha
            .thenExecute(new RandomCounterAsyncTask(this))
                .withParameters(generateRandomSeed)
            //Fim do processo
            .finishWith(new ITaskFinishListener() {
                @Override
                public void onTaskFinish() {
                    isButtonEnabled.set(true);
                }
            })
            .executeChain();
    }

    //Prove os parametros da AsyncTask no momento da sua execucao
    private AsyncTaskChainBuilder.IArgumentProvider generateRandomSeed = new AsyncTaskChainBuilder.IArgumentProvider() {
        @Override
        public Object[] provide() {
            return new Object[]{
                getRandomNumber()
                , getRandomNumber()
                , getRandomNumber()
                , getRandomNumber()
                , getRandomNumber()
            };
        }
    };

    @Override
    public void registerProgress(Integer result) {
        listNumbers.add(result);
    }

    private static class RandomCounterAsyncTask extends ChainAsyncTask<Object, Integer, Void> {

        private IProgressListener listeningActivity;

        RandomCounterAsyncTask(IProgressListener listeningActivity) {
            this.listeningActivity = listeningActivity;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            //Le os argumentos passados pelo argument provider
            Integer int1 = (Integer) objects[0];
            Integer int2 = (Integer) objects[1];
            Integer int3 = (Integer) objects[2];
            Integer int4 = (Integer) objects[3];
            Integer int5 = (Integer) objects[4];

            //Gera mais 5 numeros randomicos atraves da soma dos parametros divida por outro numero randomico
            for (int x = 0; x < 5; x++) {
                Integer div = getRandomNumber();
                //Registra o progresso na thread da view
                onProgressUpdate((int1 + int2 + int3 + int4 + int5) / div);
                //Coloquei um delay aqui porque se nao fica muito rapido hahahahha
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

    private static int getRandomNumber(){
        return rand.nextInt((MAX - MIN) + 1) + MIN;
    }
}
