package com.example.wang.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wang.dao.MyFriendsDao;
import com.example.wang.dao.VerifyDao;
import com.example.wang.imitation_weichat.AddFriend;
import com.example.wang.imitation_weichat.DisposeVerify;
import com.example.wang.imitation_weichat.FriendDetail;
import com.example.wang.imitation_weichat.HomeActivity;
import com.example.wang.imitation_weichat.R;
import com.example.wang.utils.InfoUtils;
import com.example.wang.utils.ToastUtils;
import com.example.wang.utils.UiUtils;
import com.example.wang.view.TopBar;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by wang on 16-1-20.
 */
public class ContactsFragment extends BaseFragment {

    private LinearLayout linearLayout;
    private TopBar contacts_topbar;
    private TextView no_read_verify;
    private int allUnReadMessage;
    private ListView contacts_list;
    private OkHttpClient okHttpClient = UiUtils.getOkHttpClient();
    private JSONObject jsonObject;
    private Cursor myFriendCursor;
    private List<String> friends;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_fra,null);
        initView(view);
        initData();
        initListener();
        return  view;
    }
    private void initView(View view){
        contacts_topbar = (TopBar) view.findViewById(R.id.contacts_topbar);
        linearLayout = (LinearLayout) view.findViewById(R.id.contacts_addFriend);
        no_read_verify = (TextView) view.findViewById(R.id.no_read_verify);
        contacts_list = (ListView) view.findViewById(R.id.contacts_list);
    }
    private void initData(){
        // okHttpClient = UiUtils.getOkHttpClient();
        //访问api获取好友列表
         friends = MyFriendsDao.getAllMyFriendsFromServer();
        if(friends.size()==0){
            //网络链接失败，获取本地数据库
            myFriendCursor = MyFriendsDao.getAllMyFriendsCursor();
            FriendsCursorAdapter friendsCursorAdapter = new FriendsCursorAdapter(UiUtils.getContext(), myFriendCursor);
            contacts_list.setAdapter(friendsCursorAdapter);
        }else{
            FriendsListAdapter friendsListAdapterListAdapter = new FriendsListAdapter();
            contacts_list.setAdapter(friendsListAdapterListAdapter);
        }

    }
    private void initListener(){
        //监听变化
        UiUtils.getContext().getContentResolver() .registerContentObserver(Uri.parse("content://myFriends"), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                //cursorAdapter不能使用notifyDataSetChanged来实时更新，而使用cursor调用requery即可
                //messageAdapter.notifyDataSetChanged();
                myFriendCursor.requery();
            }
        });
        //跳转到添加好友页面
        if(contacts_topbar.actionView!=null){
            contacts_topbar.actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoAddFriendActivity();
                }
            });
        }
        //处理好友请求
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查看所有好友请求并处理
                if(allUnReadMessage!=0) {
                    gotoDisposeVerify();
                }

            }
        });
        //显示待处理的好友请求数
        UiUtils.getContext().getContentResolver() .registerContentObserver(Uri.parse("content://waitForVerify"), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                allUnReadMessage = VerifyDao.selectAllVerifyNum();
               //等待请求的数目显示在UI上
                if(allUnReadMessage==0){
                    no_read_verify.setVisibility(View.INVISIBLE);
                }else{
                    no_read_verify.setVisibility(View.VISIBLE);
                    no_read_verify.setText(allUnReadMessage+"");
                }
            }
        });
        openDialogue();
    }
    private void  gotoAddFriendActivity(){
        Intent gotoAddFriend = new Intent(UiUtils.getContext(),AddFriend.class);
        gotoAddFriend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gotoAddFriend);
    }
    private void gotoDisposeVerify(){
        Intent intent = new Intent(UiUtils.getContext(),DisposeVerify.class);
        startActivity(intent);
    }

    private void openDialogue(){
        //listview点击处理,打开对话框
        contacts_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv_friend_id = (TextView) view.findViewById(R.id.friend_id);
                final String friend_id = tv_friend_id.getText().toString();
                //执行网络请求
                //创建请求参数
                RequestBody formBody = null;
                try {
                    formBody = new FormEncodingBuilder()
                            .add("user_id", URLEncoder.encode(friend_id, "UTF-8"))
                            .build();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
//创建一个Request
                final Request request = new Request.Builder()
                        .url(InfoUtils.SEARCH_URL).post(formBody)
                        .build();
//new call
                Call call = okHttpClient.newCall(request);
//异步请求,加入调度
                call.enqueue(new Callback() {

                    @Override
                    public void onFailure(Request request, IOException e) {
                        UiUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast("网络请求超时");
                            }
                        });
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        String result_json = response.body().string();

                        try {
                            jsonObject = new JSONObject(result_json);
                            final boolean result = jsonObject.getBoolean("result");
                            UiUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!result){
                                        ToastUtils.showToast("查询不到该用户");
                                    }else{
                                        Intent intent = new Intent(UiUtils.getContext(),FriendDetail.class);
                                        try {
                                            intent.putExtra("friend_id",jsonObject.getString("user_id"));
                                            intent.putExtra("friend_name",jsonObject.getString("user_name"));
                                            intent.putExtra("friend_signature",jsonObject.getString("user_signature"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    class FriendsCursorAdapter extends CursorAdapter{

        public FriendsCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view  =  View.inflate(context, R.layout.item_myfriends, null);
            FriendsAdapterHolder friendsAdapterHolder = new FriendsAdapterHolder();
            friendsAdapterHolder.friends_head = (ImageView) view.findViewById(R.id.friend_head);
            friendsAdapterHolder.friends_id = (TextView) view.findViewById(R.id.friend_id);
            view.setTag(friendsAdapterHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            FriendsAdapterHolder friendsAdapterHolder = (FriendsAdapterHolder) view.getTag();
            friendsAdapterHolder.friends_id.setText(cursor.getString(cursor.getColumnIndex("friend_id")));
        }
    }

    class FriendsListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return friends.size();
        }

        @Override
        public Object getItem(int position) {
            return friends.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FriendsAdapterHolder friendsAdapterHolder = null;
            if(convertView==null) {
                convertView= View.inflate(UiUtils.getContext(), R.layout.item_myfriends, null);
                friendsAdapterHolder = new FriendsAdapterHolder();
                friendsAdapterHolder.friends_head = (ImageView) convertView.findViewById(R.id.friend_head);
                friendsAdapterHolder.friends_id = (TextView) convertView.findViewById(R.id.friend_id);
                convertView.setTag(friendsAdapterHolder);
            }else {
                friendsAdapterHolder = (FriendsAdapterHolder) convertView.getTag();
            }
            friendsAdapterHolder.friends_id.setText(friends.get(position));
            return convertView;
        }
    }
    static class FriendsAdapterHolder{
        ImageView friends_head;
        TextView friends_id;
    }

}
