// MessageSender.aidl
package com.zhanglin.aboutprocess;
import com.zhanglin.aboutprocess.model.MessageModel;
import com.zhanglin.aboutprocess.MessageReceiver;
interface MessageSender {
       void sendMessage(in MessageModel messageModel);

       void registerReceiveListener(MessageReceiver messageReceiver);

       void unregisterReceiveListener(MessageReceiver messageReceiver);
}
