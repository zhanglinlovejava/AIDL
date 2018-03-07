package com.zhanglin.aboutprocess;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import com.zhanglin.aboutprocess.model.MessageModel;
import com.zhanglin.aboutprocess.service.MessageService;
import com.zhanglin.aboutprocess.utils.Logger;
/*

AIDL 支持的数据类型

1，Java 编程语言中的所有基本数据类型（如 int，long，char,boolean等等）
2，String 和CharSequence
3，Parcelable：实现了Parcelable接口的对象
4，List：其中的元素需要被AIDL支持，另一端实际接收的具体类始终是ArrayList，但生成的方法使用的是List接口
5，Map：其中的元素需要被AIDL支持，包括key和value，另一端实际接收的具体类始终是HashMap，但生成的方法使用的是Map接口

其他注意事项：
1，在AIDL中传递的对象，必须实现Parcelable序列化接口
2，在AIDL中传递的对象，需要在类文件相同路径下，创建同名、但后缀为.aidl的文件。并在文件中使用Parcelable关键字声明这个类
3，跟普通接口的区别：只能声明方法，不能声明变量
4，所有非基础数据类型参数都需要标出数据走向的方向标记。可以是in out 或 inout，基础数据类型默认只能是in，不能是其他方向

********对象跨进程传输的本质就是序列化 传输 接收 反序列化这样一个过程，这也是为什么跨进恒传输的对象必须实现Parcelable接口******
 */
public class MainActivity extends AppCompatActivity {

    private MessageSender messageSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpService();
    }

    private void setUpService() {
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.e("onServiceConnected----");
            //使用asInterface 方法取得AIDL 对应的操作接口
            messageSender = MessageSender.Stub.asInterface(service);
            //生成消息实体对象
            MessageModel messageModel = new MessageModel();
            messageModel.setContent("this is message content");
            messageModel.setFrom("client user id");
            messageModel.setTo("receiver user id");


            try {
                //死亡监听
                messageSender.asBinder().linkToDeath(deathRecipient,0);
                //把接收消息的回调接口注册服务器
                messageSender.registerReceiveListener(messageReceiver);
                messageSender.sendMessage(messageModel);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.e("onServiceDisconnected----");
        }
    };
    private MessageReceiver messageReceiver = new MessageReceiver.Stub() {
        @Override
        public void onMessageReceived(MessageModel receivedMessage) throws RemoteException {
            Logger.e("onMessageReceived  " + receivedMessage.toString());
        }
    };

    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Logger.e("binderDied");
            if (messageSender != null) {
                messageSender.asBinder().unlinkToDeath(this, 0);
                messageSender = null;
            }
            //// TODO: 2017/2/28 重连服务或其他操作
            setUpService();
        }
    };

    @Override
    protected void onDestroy() {
        //接触消息监听接口
        if (messageSender != null && messageSender.asBinder().isBinderAlive()) {
            try {
                messageSender.unregisterReceiveListener(messageReceiver);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(serviceConnection);
        }
        super.onDestroy();
    }
}


