package com.example.leo.asynctaskchainbuilder.builder.impl;

import android.os.AsyncTask;

import com.example.leo.asynctaskchainbuilder.builder.interfaces.IAsyncTaskChainBuilder;
import com.example.leo.asynctaskchainbuilder.builder.interfaces.ITaskFinishListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by leo on 09/08/17.
 */

public class AsyncTaskChainBuilder implements IAsyncTaskChainBuilder {

    private List<AsyncTaskNode> nodeList = new ArrayList<>();

    private AsyncTaskNode buidingTask;

    public AsyncTaskChainBuilder withParameters(IArgumentProvider provider){
        buidingTask.setProvider(provider);
        return this;
    }

    public AsyncTaskChainBuilder withExecutor(Executor executor){
        buidingTask.setExecutor(executor);
        return this;
    }

    public AsyncTaskChainBuilder startWith(ChainAsyncTask<Object, ?, ?> nodeTask){
        addLink(nodeTask);
        return this;
    }

    public AsyncTaskChainBuilder thenExecute(ChainAsyncTask<Object, ?, ?> nodeTask){
        addLink(nodeTask);
        return this;
    }

    public AsyncTaskChainBuilder finishWith(ITaskFinishListener listener){
        buidingTask.setOnFinishListener(listener);
        nodeList.add(buidingTask);
        return this;
    }

    public void executeChain(){
        if(nodeList.size() > 0){
            nodeList.get(0).execute();
        }
    }

    private void addLink(ChainAsyncTask<Object, ?, ?> nodeTask){
        AsyncTaskNode nextNode = new AsyncTaskNode();
        nextNode.setNodeTask(nodeTask);
        if(buidingTask != null){
            buidingTask.setNext(nextNode);
            nodeList.add(buidingTask);
        }
        buidingTask = nextNode;
    }

    private class AsyncTaskNode implements ITaskFinishListener {
        private ChainAsyncTask<Object, ?, ?> nodeTask;
        private AsyncTaskNode next;
        private IArgumentProvider provider;
        private Executor executor = AsyncTask.SERIAL_EXECUTOR;

        private ITaskFinishListener onFinishListener;

        public ChainAsyncTask<Object, ?, ?> getNodeTask() {
            return nodeTask;
        }

        private void setNodeTask(ChainAsyncTask<Object, ?, ?> nodeTask) {
            nodeTask.setAsyncTaskFinishListener(this);
            this.nodeTask = nodeTask;
        }

        private void setNext(AsyncTaskNode next) {
            this.next = next;
        }

        private void setProvider(IArgumentProvider provider) {
            this.provider = provider;
        }

        private void setExecutor(Executor executor) {
            this.executor = executor;
        }

        private void setOnFinishListener(ITaskFinishListener onFinishListener) {
            this.onFinishListener = onFinishListener;
        }

        private void execute(){
            nodeTask.executeOnExecutor(executor, provider != null ? provider.provide() : null);
        }

        @Override
        public void onTaskFinish() {
            nodeTask.setAsyncTaskFinishListener(null);
            if(next != null){
                next.execute();
            } else if(onFinishListener != null){
                onFinishListener.onTaskFinish();
            }
        }
    }

    public interface IArgumentProvider {
        Object[] provide();
    }
}
