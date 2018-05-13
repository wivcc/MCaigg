package gg.example.android_qqfix;

import android.app.Application;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.oraycn.es.communicate.framework.EngineEventListener;
import com.oraycn.es.communicate.framework.IRapidPassiveEngine;
import com.oraycn.es.communicate.proto.RespLogon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gg.model.ChatContentContract;
import gg.model.ContractType;
import gg.model.GGUser;
import gg.model.UserStatus;

/**
 * Created by ZN on 2015/9/1.
 */
public class ChatApplication extends Application implements EngineEventListener {

    private Map<String, ChatContentBag> chatMap = new ConcurrentHashMap<String, ChatContentBag>();
    private Map<String, ArrayList<ChatMessageListener>> messageListenerMap = new ConcurrentHashMap<String, ArrayList<ChatMessageListener>>();
    private List<FriendStatusChangedListener> friendStatusChangeds = new ArrayList<FriendStatusChangedListener>();
    private GGUser MyUserInfo;
    private List<GGUser> MyFriendUserInfo;
    private IRapidPassiveEngine engine;
    private Handler handler = new Handler();

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void connectionInterrupted() {

    }

    @Override
    public void connectionRebuildStart() {

    }

    @Override
    public void messageReceived(String sourceUserID, int informationType, byte[] info, String tag) {
        if (informationType == ContractType.CHAT.getType()) {
            ChatContentContract content = new ChatContentContract();
            try {
                content.deserialize(info);

                this.insertChatInfo(tag, content);

                if (this.messageListenerMap.containsKey(tag)) {
                    for (ChatMessageListener listener : this.messageListenerMap.get(tag))
                        listener.ChatMessageReceived(tag, content);
                }
            } catch (Exception ee) {
                Log.d(ee.getMessage(),ee.getStackTrace().toString());
            }
        }
    }

    @Override
    public void relogonCompleted(RespLogon respLogon) {

    }

    public List<GGUser> getMyFriendUserInfo() {
        return MyFriendUserInfo;
    }

    public void setMyFriendUserInfo(List<GGUser> myFriendUserInfo) {
        MyFriendUserInfo = myFriendUserInfo;
    }

    public GGUser getMyUserInfo() {
        return MyUserInfo;
    }

    public void setMyUserInfo(GGUser myUserInfo) {
        MyUserInfo = myUserInfo;
    }

    public IRapidPassiveEngine getEngine() {
        return engine;
    }

    public void insertChatInfo(String targetUserID, ChatContentContract chatInfo) {
        ChatContentBag bag = chatMap.get(targetUserID);
        if (bag == null) {
            bag = new ChatContentBag();
            chatMap.put(targetUserID, bag);
        }
        bag.setHaveNewChat(true);
        bag.getAllChatContent().add(chatInfo);
    }

    public  void  setHaveNewChatContent(String userID,boolean flag)
    {
        ChatContentBag bag = chatMap.get(userID);
        if (bag != null)
        {
            bag.setHaveNewChat(flag);
        }
    }

    public List<ChatContentContract> getAllChatInfoOfUser(String targetUserID) {
        if (chatMap.containsKey(targetUserID)) {
            return chatMap.get(targetUserID).getAllChatContent();
        } else {
            return null;
        }
    }

    public ChatContentContract getLastChatInfoOfUser(String targetUserID) {
        ChatContentBag bag = chatMap.get(targetUserID);
        if (bag != null && bag.getAllChatContent().size() > 0) {
            return bag.getAllChatContent().get(bag.getAllChatContent().size() - 1);
        }
        return null;
    }

    public GGUser getMyFriendByID(String userID) {
        GGUser user = null;
        for (GGUser ou : this.MyFriendUserInfo) {
            if (ou.getUserID().equals(userID)) {
                user = ou;
                break;
            }
        }
        return user;
    }

    public void clearChart() {
        chatMap.clear();
    }

    public void changeMyStatus(UserStatus status) {
        if (this.MyUserInfo != null) {
            this.MyUserInfo.setUserStatus(status);
        }
    }

    public void changMyFriendStatus(String userID, UserStatus status) {
        for (GGUser ou : this.MyFriendUserInfo) {
            if (ou.getUserID().equals(userID)) {
                ou.setUserStatus(status);

                synchronized (friendStatusChangeds) {
                    for (FriendStatusChangedListener listener : friendStatusChangeds) {
                        listener.FriendStatusChanged(userID, status);
                    }
                }
                break;
            }
        }
    }

    //private void AddChatEvent() {
    //    this.getEngine().addMessageReceivedListener(this);
    //}
    public  void  setEngine(IRapidPassiveEngine _engine)
    {
        engine=_engine;
        engine.setEngineEventListener(this);
    }

    public void AddChatMessageListener(String messageSender, ChatMessageListener listener) {
        if (!this.messageListenerMap.containsKey(messageSender)) {
            this.messageListenerMap.put(messageSender, new ArrayList<ChatMessageListener>());
        }
        this.messageListenerMap.get(messageSender).add(listener);
    }

    public void RemoveChatMessageListener(String messageSender, ChatMessageListener listener) {
        if (!this.messageListenerMap.containsKey(messageSender)) {
            return;
        }
        this.messageListenerMap.get(messageSender).remove(listener);
    }

    public void AddFriendStatusChangedListener(FriendStatusChangedListener listener) {
        synchronized (friendStatusChangeds) {
            friendStatusChangeds.add(listener);
        }
    }

    public void RemoveFriendStatusChangedListener(FriendStatusChangedListener listener) {
        synchronized (friendStatusChangeds) {
            friendStatusChangeds.remove(listener);
        }
    }

    public int getChatContentListenerCount(String userID) {
        if (!this.messageListenerMap.containsKey(userID)) {
            return 0;
        }
        return this.messageListenerMap.get(userID).size();
    }

    public boolean IsHaveNewChatContent(String userID) {
        ChatContentBag bag = chatMap.get(userID);
        if (bag == null) {
            return false;
        } else {
            return bag.isHaveNewChat();
        }
    }

    public interface ChatMessageListener {
        void ChatMessageReceived(String sourceUserID, ChatContentContract chatContent);
    }
    public void showMessage(String message) {
        handler.post(new UIRunnable(message));
    }

    private class UIRunnable implements Runnable {

        private String text;

        public UIRunnable(String info) {
            this.text = info;
        }

        public void run() {
            Toast.makeText(ChatApplication.this, text,Toast.LENGTH_LONG).show();
        }
    }

    public interface FriendStatusChangedListener {
        void FriendStatusChanged(String sourceUserID, UserStatus status);
    }

    class ChatContentBag {
        private boolean haveNewChat = false;
        private List<ChatContentContract> allChatContent = new ArrayList<ChatContentContract>();

        public boolean isHaveNewChat() {
            return haveNewChat;
        }

        public void setHaveNewChat(boolean haveNewChat) {
            this.haveNewChat = haveNewChat;
        }

        public List<ChatContentContract> getAllChatContent() {
            return allChatContent;
        }

        public void setAllChatContent(List<ChatContentContract> allChatContent) {
            this.allChatContent = allChatContent;
        }
    }
}