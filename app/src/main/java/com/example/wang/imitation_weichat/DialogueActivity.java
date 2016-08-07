package com.example.wang.imitation_weichat;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.wang.connecter.ConnectorManager;
import com.example.wang.dao.ChatsDao;
import com.example.wang.dao.DialoguesDao;
import com.example.wang.request.Request;
import com.example.wang.request.TextRequest;
import com.example.wang.service.CoreService;
import com.example.wang.utils.DateTools;
import com.example.wang.utils.InfoUtils;

/**
 * Created by wang on 16-1-30.
 */
public class DialogueActivity extends BaseActivity implements View.OnClickListener {

    private String chat_id;
    private ListView lv_conversation;
    private String content;
    private String time;
    private String type;
    private Button send_button;
    private EditText input_detail;
    private Cursor cursor;
    private DialogueActivityAdapter dialogueActivityAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogue);
        initData();
        initView();
        initlistener();
    }
    private void initData(){
        Intent intent = getIntent();
        chat_id = intent.getStringExtra("chat_id");
    }
    private void initView(){
        //设置所有条目为已读
        DialoguesDao.readAllMessage(chat_id);
        //获取顶部条目栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(chat_id);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.img_top_back);
        lv_conversation = (ListView) findViewById(R.id.lv_conversation_detail);
        //获取Cursor
        cursor = DialoguesDao.getDialogueCursor(chat_id);
            dialogueActivityAdapter = new DialogueActivityAdapter(this, cursor);
            lv_conversation.setAdapter(dialogueActivityAdapter);
            //显示最后一条
            lv_conversation.setSelection(cursor.getCount() - 1);
        //发送消息
        input_detail = (EditText) findViewById(R.id.et_input_conversation_detail);
        send_button = (Button) findViewById(R.id.btn_send);
    }
   private void initlistener(){
       send_button.setOnClickListener(this);
       //监听变化
       getContentResolver().registerContentObserver(Uri.parse("content://dialogues"), true, new ContentObserver(new Handler()) {
           @Override
           public void onChange(boolean selfChange) {
               super.onChange(selfChange);
               //cursorAdapter不能使用notifyDataSetChanged来实时更新，而使用cursor调用requery即可
               cursor.requery();
               lv_conversation.setSelection(cursor.getCount()-1);
           }
       });
   }
    @Override
    public void onClick(View view) {
        String messageContent = input_detail.getText().toString();
        DialoguesDao.insertDialogue(chat_id,messageContent , "request");
        ChatsDao.insertChats(chat_id,messageContent, System.currentTimeMillis() + "");
        input_detail.setText("");
        //数据发送给服务器
        Request sendMessage  = new TextRequest(InfoUtils.getUsername(),chat_id,messageContent);
        //设置监听
        ConnectorManager.getInstance().setConnectorListener(new CoreService());
        ConnectorManager.getInstance().putRequest(sendMessage);
    }

    class DialogueActivityAdapter extends CursorAdapter{

        public DialogueActivityAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = View.inflate(context,R.layout.item_dialoguedetail,null);
            DialogueViewHolder vh = new DialogueViewHolder();
            vh.tlReceive = (TableLayout) view.findViewById(R.id.tl_receive);
            vh.msgReceive = (TextView) view.findViewById(R.id.tv_msg_receive);
            vh.dateReceive = (TextView) view.findViewById(R.id.tv_date_receive);

            vh.tlSend = (TableLayout) view.findViewById(R.id.tl_send);
            vh.msgSend = (TextView) view.findViewById(R.id.tv_msg_send);
            vh.dateSend = (TextView) view.findViewById(R.id.tv_date_send);
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //先从cursor中获取出数据
            initListViewDate(cursor);
            DialogueViewHolder viewHolder = (DialogueViewHolder) view.getTag();
            //给listview设置数据 type为1表示收到的数据
            if("response".equals(type)){
                viewHolder.tlReceive.setVisibility(View.VISIBLE);
                viewHolder.tlSend.setVisibility(View.GONE);
                viewHolder.msgReceive.setText(content);
                viewHolder.dateReceive.setText(DateTools.formatDate(time));

            }else if("request".equals(type)){
                viewHolder.tlReceive.setVisibility(View.GONE);
                viewHolder.tlSend.setVisibility(View.VISIBLE);
                viewHolder.msgSend.setText(content);
                viewHolder.dateSend.setText(DateTools.formatDate(time));
            }

        }
    }
    private void initListViewDate(Cursor cursor){
        content = cursor.getString(cursor.getColumnIndex("content"));
        time = cursor.getString(cursor.getColumnIndex("time"));
        type = cursor.getString(cursor.getColumnIndex("type"));
    }
    class DialogueViewHolder{
        public TableLayout tlReceive;
        public TextView msgReceive;
        public TextView dateReceive;

        public TableLayout tlSend;
        public TextView msgSend;
        public TextView dateSend;
    }
}
