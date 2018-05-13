package gg.example.android_qqfix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import gg.dragon.persondata.ContactsInfo;
import gg.example.utils.SerializeHelper;
import gg.model.ChatContentContract;
import gg.model.ContractType;
import gg.model.GGUser;


public class ContactsListExpandable extends Activity implements ChatApplication.ChatMessageListener {


    int[] photoRes = {R.drawable.contact_0, R.drawable.contact_1, R.drawable.contact_2, R.drawable.contact_3};
    String[] groupFrom = {"groupImage", "groupName", "childCount"};
    int[] groupTo = {R.id.groupImage, R.id.groupName, R.id.childCount};
    String[] childFrom = {"userImage", "userName", "userSign","unReadMessage"};
    int[] childTo = {R.id.ct_photo, R.id.ct_name, R.id.ct_sign,R.id.unReadMessage};
    ArrayList<HashMap<String, Object>> groupData = null;
    ArrayList<ArrayList<HashMap<String, Object>>> childData = null;
    ArrayList<HashMap<String, Object>> groupItenformation = null;  //用于存放groupItem的相对位置
    int[] groupIndicator = {R.drawable.toright, R.drawable.todown};
    ExpandableListView exListView = null;
    ListView listViewTop = null, listViewDown = null;
    ScrollView qqScroll = null;
    LinearLayout titleGroupView = null;
    ImageView titleImage = null;
    TextView titleGroupName = null;
    TextView titleChildCount = null;
    MyExpandableListViewAdapter adapter = null;
    private ChatApplication app;
    private int height = 0;
    private int GROUP_HEIGHT = 0;
    private int CHILD_HEIGHT = 52;
    private int VISIBILITY_GONE = 0;
    private int VISIBILITY_VISIBLE = 1;
    /**
     * 实现这个Activity的最后一个效果，即滑动到某一个Group那么改GroupItem就在屏幕顶端悬停的效果
     * 方法：需要使用onTouchEvent方法，这个比用onScrollView方法靠谱
     */
    private boolean isChecking = false;

    @Override
    public void ChatMessageReceived(String sourceUserID, ChatContentContract chatContent) {
        if(app.getChatContentListenerCount(sourceUserID)==1) {
            edithandler.sendMessage(new Message());
        }
    }
    private Handler edithandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //((CustomizeHandler) app.getEngine().getCustomizeHandler()).removeInformationListener(ContractType.CHAT.getType(), this);
//        if (TalkingUserID != null) {
//            app.RemoveChatMessageListener(TalkingUserID,this);
//        }
//        app.RemoveFriendStatusChangedListener(this);

        for(ArrayList<HashMap<String, Object>> entry:childData)
        {
            app.RemoveChatMessageListener(entry.get(0).get("userID").toString(), this);
        }

    }

    private void AddChatEvent(String userID) {
        app.AddChatMessageListener(userID, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_qqlist_expandable);
        groupData = new ArrayList<HashMap<String, Object>>();
        childData = new ArrayList<ArrayList<HashMap<String, Object>>>();
        app = (ChatApplication) getApplication();
        GetUserInfo();

        exListView = (ExpandableListView) findViewById(R.id.qq_list);
        adapter = new MyExpandableListViewAdapter(this, groupData, R.layout.layout_qqlist_group, groupFrom, groupTo, childData, R.layout.layout_qqlist_child, childFrom, childTo);
        exListView.setAdapter(adapter);
        exListView.setGroupIndicator(null);
        exListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(ContactsListExpandable.this, ChatActivity.class);
                String talkingUserID=childData.get(0).get((int) id).get("userID").toString();
                String talkingUserName=childData.get(0).get((int) id).get("userName").toString();
                intent.putExtra("TalkingUserID", talkingUserID);
                intent.putExtra("TalkingUserName", talkingUserName);
                app.setHaveNewChatContent(talkingUserID, false);
                ContactsListExpandable.this.startActivity(intent);
                edithandler.sendMessage(new Message());
                return false;
            }
        });


        qqScroll = (ScrollView) findViewById(R.id.qqlist_scroll);

        View exGroupListItem = exListView.getExpandableListAdapter().getGroupView(0, false, null, exListView);
        exGroupListItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        exGroupListItem.measure(0, 0);
        GROUP_HEIGHT = exGroupListItem.getMeasuredHeight();

        View exChildListItem = exListView.getExpandableListAdapter().getChildView(0, 0, false, null, exListView);
        exChildListItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        exChildListItem.measure(0, 0);
        CHILD_HEIGHT = exChildListItem.getMeasuredHeight();

        ViewGroup.LayoutParams params = exListView.getLayoutParams();
        height = groupData.size() * GROUP_HEIGHT - 2;
        params.height = height;
        exListView.setLayoutParams(params);

        for (int i = 0; i < groupData.size(); i++) {
            groupData.get(i).put("location", i * GROUP_HEIGHT);
        }


        titleGroupView = (LinearLayout) findViewById(R.id.titleGroupView);
        titleGroupName = (TextView) findViewById(R.id.title_groupName);
        titleChildCount = (TextView) findViewById(R.id.title_childCount);

        titleGroupView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int groupPosition = (Integer) titleGroupView.getTag();

                exListView.collapseGroup(groupPosition);

                new LocationCheckThread().start();


            }

        });

        qqScroll.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_MOVE && isChecking == false) {
                    new LocationCheckThread().start();
                }
                return false;
            }
        });
    }

    protected void addUser(ContactsInfo user) {
        int i;
        for (i = 0; i < groupData.size(); i++) {
            if (groupData.get(i).get("groupName").toString().equals(user.groupInfo)) {
                break;
            }
        }
        if (i >= groupData.size()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("groupImage", groupIndicator);
            map.put("groupName", user.groupInfo);
            map.put("childCount", 0);
            groupData.add(map);

            ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            childData.add(list);
        }

        HashMap<String, Object> userData = new HashMap<String, Object>();
        userData.put("userImage", user.userImage);
        userData.put("userName", user.userName);
        userData.put("userSign", user.userSign);
        userData.put("userID", user.userID);
        userData.put("unReadMessage", user.UnReadMessage);
        childData.get(i).add(userData);
        Integer count = (Integer) groupData.get(i).get("childCount") + 1;
        groupData.get(i).put("childCount", count);
        groupData.get(i).put("expanded", false);

        this.AddChatEvent(user.userID);
    }

    private void GetUserInfo() {
        ChatApplication app = (ChatApplication) getApplication();
        byte[] userBytes = null;
        try {
            userBytes = app.getMyUserInfo().getUserID().getBytes("UTF8");
        } catch (Exception ee) {

        }
        byte[] returnValue =app.getEngine().getCustomizeOutter().query(ContractType.GETALLCONTRACTS.getType(), userBytes);

        List<GGUser> userList = null;
        try {
            userList = GGUser.deserializeUserList(SerializeHelper.wrappedBuffer(returnValue));
        } catch (Exception ee) {

        }

        app.setMyFriendUserInfo(userList);

        for(int i=0;i<userList.size();i++)
        {
            ContactsInfo user=new ContactsInfo(userList.get(i));
            addUser(user);
        }
    }

    private class LocationCheckThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            isChecking = true;
            int[] location = new int[2];
            int beforeY = 0;
            int i;
            ExpandableHandler mHandler = new ExpandableHandler(Looper.getMainLooper());
            while (true) {
                //exListView.getLocationOnScreen(location);

                exListView.getLocationOnScreen(location);
                if (beforeY == location[1]) {                    //控件停止运动，线程关闭
                    isChecking = false;
                    return;
                } else {
                    beforeY = location[1];
                    for (i = groupData.size() - 1; i >= 0; i--) {
                        if ((Boolean) groupData.get(i).get("expanded") && (Integer) groupData.get(i).get("location") + location[1] <= 24) {


                            Message msg = new Message();
                            msg.arg1 = childData.get(i).size();
                            msg.arg2 = i;
                            msg.obj = groupData.get(i).get("groupName");
                            msg.what = VISIBILITY_VISIBLE;
                            mHandler.sendMessage(msg);
                            break;
                        }
                    }
                    if (i < 0) {
                        Message msg = new Message();
                        msg.what = VISIBILITY_GONE;
                        msg.obj = "";                  //必须加这一步，否则会有空指针异常
                        mHandler.sendMessage(msg);
                    }

                }

                try {
                    this.sleep(80);         //sleep的时间不能过短！！
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }

    }

    public class ExpandableHandler extends Handler {

        public ExpandableHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);


            int listNum = msg.arg1;
            int visibility = msg.what;
            int groupPos = msg.arg2;
            String groupName = msg.obj.toString();

            if (visibility == VISIBILITY_GONE) {
                titleGroupView.setVisibility(View.GONE);

                return;
            } else {

                titleGroupView.setVisibility(View.VISIBLE);
                titleGroupName.setText(groupName);
                titleChildCount.setText("" + listNum);

                titleGroupView.setTag(groupPos);        //给这个View控件设置一个标签属性，用于存放groupPosition
                /**
                 * setText()中的参数不能使int型！！
                 */
            }

        }

    }

    /**
     * ExpandableListView对应的适配器
     *
     * @author DragonGN
     */
    public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        int groupLayout = 0;
        int childLayout = 0;
        private Context context = null;
        private ArrayList<HashMap<String, Object>> groupData = null;
        private String[] groupFrom = null;
        private int[] groupTo = null;
        private ArrayList<ArrayList<HashMap<String, Object>>> childData = null;
        private String[] childFrom = null;
        private int[] childTo = null;


        public MyExpandableListViewAdapter(Context context, ArrayList<HashMap<String, Object>> groupData,
                                           int groupLayout, String[] groupFrom, int[] groupTo,
                                           ArrayList<ArrayList<HashMap<String, Object>>> childData, int childLayout,
                                           String[] childFrom, int[] childTo) {
            super();
            this.context = context;
            this.groupData = groupData;
            this.groupLayout = groupLayout;
            this.groupFrom = groupFrom;
            this.groupTo = groupTo;
            this.childData = childData;
            this.childLayout = childLayout;
            this.childFrom = childFrom;
            this.childTo = childTo;

        }

        @Override
        public Object getChild(int arg0, int arg1) {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * position与id一样，都是从0开始计数的，
         * 这里返回的id也是从0开始计数的
         */
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            long id = 0;
            for (int i = 0; i < groupPosition; i++) {
                id += childData.size();
            }
            id += childPosition;
            return id;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            /**
             * 这里isLastChild目前没用到，如果出现异常再说
             */
            ChildViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(childLayout, null);
                //感觉这里需要把root设置成ViewGroup 对象
                /**
                 * ERROR!!这里不能把null换成parent，否则会出现异常退出，原因不太确定，可能是inflate方法获得的这个item的View
                 * 并不属于某个控件组，所以使用默认值null即可
                 */
                holder = new ChildViewHolder();
                holder.userImage = (ImageButton) convertView.findViewById(childTo[0]);
                holder.userName = (TextView) convertView.findViewById(childTo[1]);
                holder.userSign = (TextView) convertView.findViewById(childTo[2]);
                holder.bezierView = (BezierView) convertView.findViewById(childTo[3]);

                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            holder.userImage.setBackgroundResource((Integer) (childData.get(groupPosition).get(childPosition).get(childFrom[0])));
            holder.userName.setText(childData.get(groupPosition).get(childPosition).get(childFrom[1]).toString());
            holder.userSign.setText(childData.get(groupPosition).get(childPosition).get(childFrom[2]).toString());
            holder.userImage.setOnClickListener(new ImageClickListener(holder));

            //if(!app.IsHaveNewChatContent(childData.get(groupPosition).get(childPosition).get("userID").toString()))
            if(!app.IsHaveNewChatContent(childData.get(groupPosition).get(childPosition).get("userID").toString()))
            {
                holder.bezierView.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.bezierView.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return childData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return groupData.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            GroupViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(groupLayout, null);
                holder = new GroupViewHolder();
                holder.image = (ImageView) convertView.findViewById(groupTo[0]);
                holder.groupName = (TextView) convertView.findViewById(groupTo[1]);
                holder.childCount = (TextView) convertView.findViewById(groupTo[2]);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            int[] groupIndicator = (int[]) groupData.get(groupPosition).get(groupFrom[0]);
            holder.image.setBackgroundResource(groupIndicator[isExpanded ? 1 : 0]);
            holder.groupName.setText(groupData.get(groupPosition).get(groupFrom[1]).toString());
            holder.childCount.setText(groupData.get(groupPosition).get(groupFrom[2]).toString());


            if (groupPosition == groupData.size() - 1) {
                convertView.setBackgroundResource(R.drawable.list_lastitem_border);
            } else {
                convertView.setBackgroundResource(R.drawable.list_item_border);
            }
            //else的情况也要考虑，否则在绘制时出现错位现象

            /**
             * 将刚刚创建的groupItem的相对坐标计算出来放在groupLocation中，这个是初始相对坐标
             * 当点击打开一级菜单和关闭一级菜单时重新更新每一个group的相对坐标
             */

            return convertView;
            /**
             * 不要在适配器中调用适配器的内部方法，不然会出现奇怪的异常
             *
             */
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

        /**
         * 在设置ExpandableListView的宽度的时候，要注意每次点击展开或者关闭时，各个Group和所要显示的Item都会重绘
         * 因此在每次绘制完毕之后都需要对height进行更新
         */

        @Override
        public void onGroupExpanded(int groupPosition) {
            // TODO Auto-generated method stub
            super.onGroupExpanded(groupPosition);


            /**
             * 更新groupItem的相对坐标
             */
            groupData.get(groupPosition).put("expanded", true);
            for (int i = groupPosition + 1; i < groupData.size(); i++) {
                groupData.get(i).put("location", (Integer) groupData.get(i).get("location") + childData.get(groupPosition).size() * CHILD_HEIGHT);
            }

            float deviationFix = 0;         //对ExpandableListView高度的误差修正
            for (int i = 0; i < groupData.size(); i++) {
                deviationFix += (Integer) groupData.get(i).get("location") / CHILD_HEIGHT * 0.5;
            }

            height += childData.get(groupPosition).size() * CHILD_HEIGHT;
            ViewGroup.LayoutParams params = exListView.getLayoutParams();
            params.height = height - floatToInt(deviationFix);
            exListView.setLayoutParams(params);


        }

        @Override
        public void onGroupCollapsed(int groupPosition) {
            // TODO Auto-generated method stub
            super.onGroupCollapsed(groupPosition);
            height = height - childData.get(groupPosition).size() * CHILD_HEIGHT;
            ViewGroup.LayoutParams params = exListView.getLayoutParams();
            params.height = height;
            exListView.setLayoutParams(params);

            /**
             * 更新groupItem的相对坐标
             */
            groupData.get(groupPosition).put("expanded", false);
            for (int i = groupPosition + 1; i < groupData.size(); i++) {
                groupData.get(i).put("location", (Integer) groupData.get(i).get("location") - childData.get(groupPosition).size() * CHILD_HEIGHT);
            }
        }

        private int floatToInt(double f) {
            if (f > 0) {
                return (int) (f + 0.5);
            } else {
                return (int) (f - 0.5);
            }

        }

        /**
         * ChildViewHolder内部类
         **/
        class ChildViewHolder {
            ImageButton userImage = null;
            TextView userName = null;
            TextView userSign = null;
            BezierView bezierView=null;
        }

        /**
         * 头像点击事件监听类
         **/
        class ImageClickListener implements OnClickListener {

            ChildViewHolder holder = null;

            public ImageClickListener(ChildViewHolder holder) {
                this.holder = holder;
            }

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, holder.userName.getText() + " is clicked", Toast.LENGTH_SHORT).show();

            }

        }

        class GroupViewHolder {
            ImageView image = null;
            TextView groupName = null;
            TextView childCount = null;
        }

    }

    /**
     * 这个是ListView的适配器
     *
     * @author DragonGN
     */
    public class MyListViewAdapter extends BaseAdapter {

        Context context = null;
        ArrayList<HashMap<String, Object>> list = null;
        int layout;
        String[] from = null;
        int[] to;


        public MyListViewAdapter(Context context,
                                 ArrayList<HashMap<String, Object>> list, int layout,
                                 String[] from, int[] to) {
            super();
            this.context = context;
            this.list = list;
            this.layout = layout;
            this.from = from;
            this.to = to;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View listItemView = LayoutInflater.from(context).inflate(layout, null);
            ImageView image = (ImageView) listItemView.findViewById(to[0]);
            TextView text = (TextView) listItemView.findViewById(to[1]);
            image.setImageResource((Integer) list.get(position).get(from[0]));
            text.setText((String) list.get(position).get(from[1]));
            return listItemView;
        }
    }
}
