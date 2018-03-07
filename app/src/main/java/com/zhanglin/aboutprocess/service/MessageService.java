package com.zhanglin.aboutprocess.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.zhanglin.aboutprocess.MessageReceiver;
import com.zhanglin.aboutprocess.MessageSender;
import com.zhanglin.aboutprocess.model.MessageModel;
import com.zhanglin.aboutprocess.utils.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhanglin on 2018/3/7.
 */

public class MessageService extends Service {

    private AtomicBoolean serviceStop = new AtomicBoolean(false);
    private RemoteCallbackList<MessageReceiver> listenerList = new RemoteCallbackList<>();

    private IBinder messageSender = new MessageSender.Stub() {
        @Override
        public void sendMessage(MessageModel messageModel) throws RemoteException {
            Logger.e("messageModel  " + messageModel.toString());
        }

        @Override
        public void registerReceiveListener(MessageReceiver messageReceiver) throws RemoteException {
            listenerList.register(messageReceiver);
        }

        @Override
        public void unregisterReceiveListener(MessageReceiver messageReceiver) throws RemoteException {
            listenerList.unregister(messageReceiver);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new FakeTCPTask()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messageSender;
    }

    //模拟长连接 通知 客户端有新消息到达
    private class FakeTCPTask implements Runnable {
        @Override
        public void run() {
            while (!serviceStop.get()) {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MessageModel messageModel = new MessageModel();
                messageModel.setFrom("service");
                messageModel.setTo("Client");
                messageModel.setContent(String.valueOf(System.currentTimeMillis()));
                final int listenerCount = listenerList.beginBroadcast();
                Logger.e("listenerCount == " + listenerCount);
                for (int i = 0; i < listenerCount; i++) {
                    MessageReceiver messageReceiver = listenerList.getBroadcastItem(i);
                    if (messageReceiver != null) {
                        try {
                            messageReceiver.onMessageReceived(messageModel);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                }
                listenerList.finishBroadcast();
            }
        }
    }

    @Override
    public void onDestroy() {
        serviceStop.set(true);
        super.onDestroy();
    }
}
