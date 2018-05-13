package gg.example.android_qqfix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.oraycn.es.communicate.core.Configuration;
import com.oraycn.es.communicate.framework.Basic.RapidEngineFactory;
import com.oraycn.es.communicate.framework.BasicEventListener;
import com.oraycn.es.communicate.framework.GroupEventListener;
import com.oraycn.es.communicate.proto.RespLogon;

import org.apache.log4j.Level;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import gg.dragon.persondata.UserInfo;
import gg.example.utils.SerializeHelper;
import gg.example.utils.StringHelper;
import gg.handler.CustomizeHandler;
import gg.model.ContractType;
import gg.model.GGUser;
import gg.model.UserStatus;

public class MainActivity extends Activity {

    public static int currentSelectedPosition = -1;
    private static boolean isVisible = false;         //ListView是否可见
    private static boolean isIndicatorUp = false;     //指示器的方向
    TextView textFetchPassWord = null, textRegister = null;
    Button loginButton = null;
    ImageButton listIndicatorButton = null, deleteButtonOfEdit = null;
    ImageView currentUserImage = null;
    ListView loginList = null;
    EditText qqEdit = null, passwordEdit = null,ipEdit=null,portEdit=null;
    String[] from = {"userPhoto", "userQQ", "deletButton"};
    //用于记录当前选择的ListView中的QQ联系人条目的ID，如果是-1表示没有选择任何QQ账户，注意在向
    //List中添加条目或者删除条目时都要实时更新该currentSelectedPosition
    int[] to = {R.id.login_userPhoto, R.id.login_userQQ, R.id.login_deleteButton};
    ArrayList<HashMap<String, Object>> list = null;
//    private Logger log = Logger.getLogger(MainActivity.class);

    private void configLog() {
        final LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(Environment.getExternalStorageDirectory()
                + File.separator + "EsPlus.log");
        logConfigurator.setRootLevel(org.apache.log4j.Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.configure();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // configLog();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        textFetchPassWord = (TextView) findViewById(R.id.fetchPassword);
        textRegister = (TextView) findViewById(R.id.registQQ);
        loginButton = (Button) findViewById(R.id.qqLoginButton);
        listIndicatorButton = (ImageButton) findViewById(R.id.qqListIndicator);
        loginList = (ListView) findViewById(R.id.loginQQList);
        list = new ArrayList<HashMap<String, Object>>();
        currentUserImage = (ImageView) findViewById(R.id.myImage);
        qqEdit = (EditText) findViewById(R.id.qqNum);
        ipEdit=(EditText)findViewById(R.id.ip);
        portEdit=(EditText)findViewById(R.id.port);
        passwordEdit = (EditText) findViewById(R.id.qqPassword);
        deleteButtonOfEdit = (ImageButton) findViewById(R.id.delete_button_edit);


        qqEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (qqEdit.getText().toString().equals("") == false) {
                    deleteButtonOfEdit.setVisibility(View.VISIBLE);
                }
            }
        });

        deleteButtonOfEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                currentUserImage.setImageResource(R.drawable.qqmain);
                qqEdit.setText("");
                currentSelectedPosition = -1;
                deleteButtonOfEdit.setVisibility(View.GONE);

            }
        });

        UserInfo user1 = new UserInfo(R.drawable.i0, "10000", "******", R.drawable.deletebutton);
        addUser(user1);

        //设置当前显示的被选中的账户的头像
        if (currentSelectedPosition == -1) {
            currentUserImage.setImageResource(R.drawable.qqmain);
            qqEdit.setText("");
        } else {
            currentUserImage.setImageResource((Integer) list.get(currentSelectedPosition).get(from[0]));
            qqEdit.setText((String) list.get(currentSelectedPosition).get(from[1]));
        }

        MyLoginListAdapter adapter = new MyLoginListAdapter(this, list, R.layout.layout_list_item, from, to);
        loginList.setAdapter(adapter);
        loginList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                currentUserImage.setImageResource((Integer) list.get(arg2).get(from[0]));
                qqEdit.setText((String) list.get(arg2).get(from[1]));
                currentSelectedPosition = arg2;

                //相应完点击后List就消失，指示箭头反向！
                loginList.setVisibility(View.GONE);
                listIndicatorButton.setBackgroundResource(R.drawable.indicator_down);
            }
        });

        listIndicatorButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isIndicatorUp) {
                    isIndicatorUp = false;
                    isVisible = false;
                    listIndicatorButton.setBackgroundResource(R.drawable.indicator_down);
                    loginList.setVisibility(View.GONE);   //让ListView列表消失
                } else {
                    isIndicatorUp = true;
                    isVisible = true;
                    listIndicatorButton.setBackgroundResource(R.drawable.indicator_up);
                    loginList.setVisibility(View.VISIBLE);
                }
            }

        });

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//115.29.208.205
                login(qqEdit.getText().toString(), StringHelper.md5(passwordEdit.getText().toString()),
                        ipEdit.getText().toString(),Integer.parseInt(portEdit.getText().toString()));//192.168.3.50
            }
        });
    }

    //继承onTouchEvent方法，用于实现点击控件之外的部分使控件消失的功能
    private void addUser(UserInfo user) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(from[0], user.userPhoto);
        map.put(from[1], user.userQQ);
        map.put(from[2], user.deleteButtonRes);

        list.add(map);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN && isVisible) {
            int[] location = new int[2];
            //调用getLocationInWindow方法获得某一控件在窗口中左上角的横纵坐标
            loginList.getLocationInWindow(location);
            //获得在屏幕上点击的点的坐标
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x < location[0] || x > location[0] + loginList.getWidth() ||
                    y < location[1] || y > location[1] + loginList.getHeight()) {
                isIndicatorUp = false;
                isVisible = false;

                listIndicatorButton.setBackgroundResource(R.drawable.indicator_down);
                loginList.setVisibility(View.GONE);   //让ListView列表消失，并且让游标向下指！
            }
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    private void login(final String username, final String password, final String ip, final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ChatApplication app = (ChatApplication) getApplication();

                if (app.getEngine() != null) {
                    app.getEngine().Close();
                    app.clearChart();
                    app.setEngine(null);
                }
                app.setEngine(RapidEngineFactory.CreatePassiveEngine());
                Configuration config = new Configuration(ip, port);
                config.setMaxLengthOfUserId((byte) 20);
                CustomizeHandler handler = new CustomizeHandler(app);
                handler.setUserConfiguration(config);
                RespLogon resp = null;
                try {
                    resp = app.getEngine().initialize(username, password, ip, port, handler,
                            MainActivity.this.getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resp != null && resp.getLogonResult() == 0) {
                    if (resp.getLogonResult() == 0) {
                        app.showMessage("登录成功");
                    } else {
                        app.showMessage("登录失败");
                        return;
                    }
                } else {
                    if (app.getEngine().connected() == false)
                        app.showMessage("无法连接服务器");
                    app.getEngine().Close();
                    return;
                }
                app.getEngine().getBasicOutter().addBasicEventListener(new BasicEventListener() {
                    @Override
                    public void beingPushedOut() {
                        app.showMessage("账号在别处登录，下线");
                    }

                    @Override
                    public void beingKickedOut() {
                        app.showMessage("被其他用户踢出，下线");
                    }
                });

                app.getEngine().getGroupOutter().setGroupEventListener(new GroupEventListener() {
                    @Override
                    public void broadcastReceived(String s, String s1, int i, byte[] bytes) {

                    }

                    @Override
                    public void broadcastReceived2(String s, String s1, int i, byte[] bytes, String s2) {

                    }

                    @Override
                    public void groupmateConnected(String s) {
                        app.showMessage("组友" + s + "上线了!");
                    }

                    @Override
                    public void groupmateOffline(String s) {
                        app.showMessage("组友" + s + "下线了!");
                    }
                });

                byte[] userBytes = null;
                try {
                    userBytes = username.getBytes("UTF8");
                } catch (Exception ee) {

                }

                // userBytes=new byte[10];
                byte[] returnValue = app.getEngine().getCustomizeOutter().query(ContractType.GETUSERINFO.getType(), userBytes);

                GGUser ggUser = new GGUser();

                try {
                    ggUser.deserialize(SerializeHelper.wrappedBuffer(returnValue));
                } catch (Exception ee) {

                }

                app.getEngine().getCustomizeOutter().send(ContractType.CHANGESTATUS.getType(), UserStatus.GetStatusBytes(UserStatus.Online));

                //orayUser.setUserStatus(UserStatus.Online);

                app.setMyUserInfo(ggUser);

                Intent intent = new Intent(MainActivity.this, ContactsListExpandable.class);

                MainActivity.this.startActivity(intent);

                finish();
            }
        }).start();

    }

    /**
     * 为了便于在适配器中修改登录界面的Activity，这里把适配器作为
     * MainActivity的内部类，避免了使用Handler，简化代码
     */
    public class MyLoginListAdapter extends BaseAdapter {

        protected Context context;
        protected ArrayList<HashMap<String, Object>> list;
        protected int itemLayout;
        protected String[] from;
        protected int[] to;

        public MyLoginListAdapter(Context context,
                                  ArrayList<HashMap<String, Object>> list, int itemLayout,
                                  String[] from, int[] to) {
            super();
            this.context = context;
            this.list = list;
            this.itemLayout = itemLayout;
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
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            /*
            currentPosition=position;
			不能使用currentPosition，因为每绘制完一个Item就会更新currentPosition
			这样得到的currentPosition将始终是最后一个Item的position
			*/

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(itemLayout, null);
                holder = new ViewHolder();
                holder.userPhoto = (ImageView) convertView.findViewById(to[0]);
                holder.userQQ = (TextView) convertView.findViewById(to[1]);
                holder.deleteButton = (ImageButton) convertView.findViewById(to[2]);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.userPhoto.setBackgroundResource((Integer) list.get(position).get(from[0]));
            holder.userQQ.setText((String) list.get(position).get(from[1]));
            holder.deleteButton.setBackgroundResource((Integer) list.get(position).get(from[2]));
            holder.deleteButton.setOnClickListener(new ListOnClickListener(position));

            return convertView;
        }

        class ViewHolder {
            public ImageView userPhoto;
            public TextView userQQ;
            public ImageButton deleteButton;
        }

        class ListOnClickListener implements OnClickListener {

            private int position;


            public ListOnClickListener(int position) {
                super();
                this.position = position;
            }

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                list.remove(position);

                //如果删除的就是当前显示的账号，那么将主界面当前显示的头像设置回初始头像
                if (position == currentSelectedPosition) {
                    currentUserImage.setImageResource(R.drawable.qqmain);
                    qqEdit.setText("");
                    currentSelectedPosition = -1;
                } else if (position < currentSelectedPosition) {
                    currentSelectedPosition--;    //这里小于当前选择的position时需要进行减1操作
                }

                listIndicatorButton.setBackgroundResource(R.drawable.indicator_down);
                loginList.setVisibility(View.GONE);   //让ListView列表消失，并且让游标向下指！

                MyLoginListAdapter.this.notifyDataSetChanged();
            }
        }
    }

}
