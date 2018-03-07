// MessageReceiver.aidl
package com.zhanglin.aboutprocess;
import com.zhanglin.aboutprocess.model.MessageModel;
interface MessageReceiver {
    void onMessageReceived(in MessageModel receivedMessage);
}
