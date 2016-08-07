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
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wang.dao.ChatsDao;
import com.example.wang.dao.DialoguesDao;
import com.example.wang.imitation_weichat.DialogueActivity;
import com.example.wang.imitation_weichat.R;
import com.example.wang.utils.DateTools;
import com.example.wang.utils.ToastUtils;
import com.example.wang.utils.UiUtils;

/**
 * Created by wang on 16-1-20.
 */
public class MessageFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView message_list;
    private MessageAdapter messageAdapter;
    private Cursor cursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(UiUtils.getContext(),R.layout.message_fra,null);
        message_list = (ListView) view.findViewById(R.id.message_list);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        cursor = ChatsDao.getChatCursor();
        //为listview设置adapter
        messageAdapter = new MessageAdapter(getActivity(), cursor);
        message_list.setAdapter(messageAdapter);
        initListener();
    }
    private void initListener(){
        message_list.setOnItemClickListener(this);
        //监听变化
        UiUtils.getContext().getContentResolver() .registerContentObserver(Uri.parse("content://chats"), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                //cursorAdapter不能使用notifyDataSetChanged来实时更新，而使用cursor调用requery即可
                //messageAdapter.notifyDataSetChanged();
                cursor.requery();
            }
        });
        UiUtils.getContext().getContentResolver().registerContentObserver(Uri.parse("content://dialogues"), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                //cursorAdapter不能使用notifyDataSetChanged来实时更新，而使用cursor调用requery即可
                //messageAdapter.notifyDataSetChanged();
                cursor.requery();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(UiUtils.getContext(), DialogueActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MessageViewHolder messageViewHolder  = (MessageViewHolder) view.getTag();
            intent.putExtra("chat_id",messageViewHolder.sender_name.getText().toString());
            startActivity(intent);
    }

    class MessageAdapter extends CursorAdapter{

        public MessageAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = View.inflate(context,R.layout.item_message_fra,null);
            MessageViewHolder messageViewHolder = new MessageViewHolder();
            messageViewHolder.head_view  = (ImageView) view.findViewById(R.id.item_head_view);
            messageViewHolder.no_read_message  = (TextView) view.findViewById(R.id.item_no_read_message);
            messageViewHolder.sender_name  = (TextView) view.findViewById(R.id.item_sender_name);
            messageViewHolder.last_message  = (TextView) view.findViewById(R.id.item_last_message);
            messageViewHolder.last_message_time  = (TextView) view.findViewById(R.id.item_last_message_time);
            view.setTag(messageViewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            MessageViewHolder messageViewHolder = (MessageViewHolder) view.getTag();
            //messageViewHolder.head_view.
            messageViewHolder.sender_name.setText(cursor.getString(cursor.getColumnIndex("_id")));
            int no_read_message = DialoguesDao.unreadsum(messageViewHolder.sender_name.getText().toString());
            if(no_read_message!=0){
                messageViewHolder.no_read_message.setText(no_read_message+"");
            }else {
                messageViewHolder.no_read_message.setVisibility(View.GONE);
            }
            messageViewHolder.last_message.setText(cursor.getString(cursor.getColumnIndex("content")));
            messageViewHolder.last_message_time.setText(DateTools.formatDate(cursor.getString(cursor.getColumnIndex("time"))));
        }
    }
    static class MessageViewHolder{
        ImageView head_view;
        TextView no_read_message;
        TextView sender_name;
        TextView last_message;
        TextView last_message_time;
    }
}
