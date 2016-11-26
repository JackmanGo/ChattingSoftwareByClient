package com.example.wang.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import java.net.URLEncoder;


/**
 * Created by wang on 16-1-23.
 */
public class RegisterFragment extends BaseFragment {

    private FragmentManager fragmentManager;
    private EditText account;
    private EditText pwd;
    private ProgressBar register_pb;
    private Button register_button;
    private String username;
    private String password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fra,null);
        return  view;
    }
    //处理左上角的点击返回
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //执行注册
        register_button = (Button) getActivity().findViewById(R.id.btn_register);
        register_pb = (ProgressBar) getActivity().findViewById(R.id.register_pb);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    register(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        TopBar login_topbar = (TopBar) getActivity().findViewById(R.id.register_topbar);
        ImageView imageView = (ImageView) login_topbar.findViewById(R.id.back_button);
        fragmentManager = getActivity().getFragmentManager();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoFragment logoFragment = new LogoFragment();
                logoFragment.setMyClickButton(new LogoFragment.Click() {
                    @Override
                    public void loginClick() {
                        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.logo_frameLayout,new LoginFragment());
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void registerClick() {
                        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.logo_frameLayout,new RegisterFragment());
                        fragmentTransaction.commit();
                    }
                });
                getActivity().getFragmentManager().beginTransaction().replace(R.id.logo_frameLayout, logoFragment).commit();
            }
        });
    }
    private void register(Activity activity) throws Exception{
        OkHttpClient  mOkHttpClient = UiUtils.getOkHttpClient();
        account = (EditText) activity.findViewById(R.id.et_register_account);
        pwd = (EditText)activity.findViewById(R.id.et_register_pwd);
        username = account.getEditableText().toString();
        password = pwd.getEditableText().toString();
        if(TextUtils.isEmpty(username)){
            ToastUtils.showToast("用户名不能为空");
            UiUtils.cancelKeyBoard(account);
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtils.showToast("密码不能为空");
            UiUtils.cancelKeyBoard(account);
            return;
        }
        UiUtils.cancelKeyBoard(account);
        //请求网络。禁用button
        register_button.setClickable(false);
        register_button.setFocusable(false);
        //显示progressBar
        register_pb.setVisibility(View.VISIBLE);
        //执行网络请求
        //创建请求参数
        RequestBody formBody = new FormEncodingBuilder()
                .add("userId",URLEncoder.encode(username,"UTF-8"))
                .add("userPassword",URLEncoder.encode(password,"UTF-8"))
                .build();
//创建一个Request
        final Request request = new Request.Builder()
                .url(InfoUtils.REGISTER_URL).post(formBody)
                .build();
//new call
        Call call = mOkHttpClient.newCall(request);
//请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e)
            {
                UiUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        register_pb.setVisibility(View.GONE);
                        ToastUtils.showToast("网络异常!!!");
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException
            {
                  String response_json =  response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(response_json);
                    final  String result = jsonObject.getString("result");
                    System.out.println(result);
                    if("注册成功".equals(result)){
                        //将信息序列号到本地
                        InfoUtils.saveOrupdateLoginInfo(username);
                        UiUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.cancelKeyBoard(account);
                                ToastUtils.showToast(result);
                                register_pb.setVisibility(View.GONE);
                                register_button.setClickable(true);
                                register_button.setFocusable(true);
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                    } else if("用户名已存在".equals(result)){
                        UiUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.cancelKeyBoard(account);
                                ToastUtils.showToast(result);
                                register_pb.setVisibility(View.GONE);
                                register_button.setClickable(true);
                                register_button.setFocusable(true);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
