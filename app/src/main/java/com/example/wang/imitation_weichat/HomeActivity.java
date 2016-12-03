package com.example.wang.imitation_weichat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v13.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TabHost;

import com.example.wang.broadcastReceiver.NetWorkBroadcastReceiver;
import com.example.wang.broadcastReceiver.PushDataBroadcastReceiver;
import com.example.wang.dao.ChatsDao;
import com.example.wang.dao.DialoguesDao;
import com.example.wang.dao.MyFriendsDao;
import com.example.wang.dao.VerifyDao;
import com.example.wang.fragment.ContactsFragment;
import com.example.wang.fragment.DiscoverFragment;
import com.example.wang.fragment.MeFragment;
import com.example.wang.fragment.MessageFragment;
import com.example.wang.javaBean.ResponseInfos;
import com.example.wang.service.CoreService;
import com.example.wang.service.ServiceMethod;
import com.example.wang.utils.NetWorkInfos;
import com.example.wang.utils.UiUtils;
import com.example.wang.view.ButtomRadioButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {
    private static final String MESSAGE = "btn_message";
    private static final String CONTACTS = "btn_contacts";
    private static final String DISCOVER = "btn_discover";
    private static final String ME = "btn_me";
    private FragmentTabHost fragmentTabHost;


    private String tabSpecs[] = {MESSAGE,CONTACTS,DISCOVER,ME};
    private Class fragmentArray[] = {MessageFragment.class,ContactsFragment.class,DiscoverFragment.class,MeFragment.class};
    private int backgrounds[] = {R.drawable.btn_message_selector,R.drawable.btn_contacts_selector,R.drawable.btn_discover_selector,R.drawable.btn_me_selector};
    private String names[] = {"消息","联系人","发现","我"};
    private List<ButtomRadioButton> buttomRadioButtons = null;
    private String text;

    private  ServiceMethod coreServiceMethod;
    private ServiceConnection monitorServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initTabHost();
        initListener();
        initBroadcastReceiver();
        authInfos();
    }
    private void initData(){
        //初始化四个组合布局
        buttomRadioButtons = new ArrayList<>();
        for(int i=0;i<4;i++){
            ButtomRadioButton buttomRadioButton = new ButtomRadioButton(this);
            buttomRadioButton.setImageBackgroundResource(backgrounds[i]);
            buttomRadioButton.setText(names[i]);
            buttomRadioButtons.add(buttomRadioButton);
        }
        MyFriendsDao.getAllMyFriendsFromServer();
    }
    private void initTabHost(){
        //生成共三步.
        /*
         *1.初始化Tabhost
         *2.新建TabSpec
         *3.添加TabSpec到TabHost
         */
        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this,getFragmentManager(),R.id.main_frameLayout);
        //通过循环添加
        for(int i=0;i<4;i++){
            //newTabSpec的参数表示唯一表示符。用来确定那个被点击
         TabHost.TabSpec tabSpec =  fragmentTabHost.newTabSpec(tabSpecs[i]).setIndicator(buttomRadioButtons.get(i));
         fragmentTabHost.addTab(tabSpec,fragmentArray[i],null);
        }
        //取消每个条目之间的分割线
        fragmentTabHost.getTabWidget().setDividerDrawable(android.R.color.white);
        // 初始化 第一个条目被选中 通过唯一标识符确定
        fragmentTabHost.setCurrentTabByTag(tabSpecs[0]);
    }
    private void initListener(){
        UiUtils.getContext().getContentResolver().registerContentObserver(Uri.parse("content://dialogues"), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                //cursorAdapter不能使用notifyDataSetChanged来实时更新，而使用cursor调用requery即可
                //messageAdapter.notifyDataSetChanged();
                setChatUnReadNum(ChatsDao.getAllUnRead());
            }
        });
        //监听变化
        UiUtils.getContext().getContentResolver() .registerContentObserver(Uri.parse("content://waitForVerify"), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                int allUnReadMessage = VerifyDao.selectAllVerifyNum();
                setWaitUnReadNum(allUnReadMessage);
            }
        });

    }
    private void initBroadcastReceiver(){
        //注册pushData的广播
        IntentFilter pushData_filter = new IntentFilter();
        pushData_filter.addAction(PushDataBroadcastReceiver.ACTION);
        registerReceiver(pushDataBroadcastReceiver, pushData_filter);
        //注册监听网络变化的广播
        IntentFilter network_filter = new IntentFilter();
        network_filter.addAction(NetWorkBroadcastReceiver.CONNECTIVITY_CHANGE_ACTION);
        registerReceiver(netWorkBroadcastReceiver,network_filter);
    }
    //向服务器发送认证信息
    private void authInfos(){
        Log.d("socketLog","开始启动服务。。。。。");
        Intent serviceIntent = new Intent(this,CoreService.class);
        startService(serviceIntent);
        //监听网络的变化，用于重新链接socket
        monitorServiceConnection = new MonitorServiceConnection();
        bindService(serviceIntent,monitorServiceConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pushDataBroadcastReceiver);
        unregisterReceiver(netWorkBroadcastReceiver);
        unbindService(monitorServiceConnection);
    }
    //设置未读数
     public void setChatUnReadNum(String unReadNum){
         buttomRadioButtons.get(0).setNoReadNum(Integer.parseInt(unReadNum));
     }
    //设置未读数
    public void setWaitUnReadNum(int unReadNum){
        buttomRadioButtons.get(1).setNoReadNum(unReadNum);
    }
    //广播接受者
    private PushDataBroadcastReceiver pushDataBroadcastReceiver = new PushDataBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (PushDataBroadcastReceiver.ACTION.equals(action)) {
                text = intent.getStringExtra(PushDataBroadcastReceiver.DATA_KEY);
                //处理接收到的消息
                Gson gson = new Gson();
                ResponseInfos responseInfos = gson.fromJson(text, ResponseInfos.class);
                if (!"ask_for" .equals(responseInfos.getAction())) {
                    //获取到服务器的反馈数据。这个数据有可能是用户的认证也有可能是接受的消息
                    //先添加dialogues后添加chats
                    DialoguesDao.insertDialogue(responseInfos.getSender(),responseInfos.getContent(),responseInfos.getType());
                    ChatsDao.insertChats(responseInfos.getSender(),responseInfos.getContent(), responseInfos.getTime());
                }else{
                    VerifyDao.insertVerify(responseInfos.getSender(), responseInfos.getContent());
                }
            }
        }
    };
    private NetWorkBroadcastReceiver  netWorkBroadcastReceiver = new NetWorkBroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("TAG", "PfDataTransReceiver receive action " + action);
            if(TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)){//网络变化的时候会发送通知
                Log.i("TAG", "网络变化了");
                //判断此时的网络情况
                ConnectivityManager mConnMgr = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                //如果mConnMgr为null说明无网络
                if (mConnMgr != null){
                    NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo(); // 获取活动网络连接信息
                    if(aActiveInfo!=null) {
                        Log.i("TAG", "网络OK");
                        if(NetWorkInfos.NOW_NETWORK_STATIC!=null&&NetWorkInfos.NOW_NETWORK_STATIC!=NetWorkInfos.NETWORKISFAILE){
                            HomeActivity.this.reConnection();
                        }
                        //网络通畅，向服务器发送请求保持连接
                        NetWorkInfos.NOW_NETWORK_STATIC = NetWorkInfos.NETWORKISOK;
                    }else{
                        Log.i("TAG", "网络Failed");
                        NetWorkInfos.NOW_NETWORK_STATIC=NetWorkInfos.NETWORKISFAILE;
                    }
                }
                return;
            }
        }
    };
    //
    public  void reConnection(){
       coreServiceMethod.keepSocketWithService(false);
    }


    class MonitorServiceConnection implements android.content.ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
             coreServiceMethod = (ServiceMethod) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}
