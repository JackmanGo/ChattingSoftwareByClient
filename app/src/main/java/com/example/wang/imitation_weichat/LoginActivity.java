package com.example.wang.imitation_weichat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.example.wang.fragment.LoginFragment;
import com.example.wang.fragment.LogoFragment;
import com.example.wang.fragment.RegisterFragment;

/**
 * Created by wang on 16-1-23.
 */
public class LoginActivity extends BaseActivity {

    private FrameLayout logo_frameLayout;
    private FragmentManager fragmentManager;
    private LogoFragment logoFragment;
    private boolean isLogoFra = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        init();
        initFragment();
    }
    private void init(){
        logo_frameLayout = (FrameLayout) findViewById(R.id.logo_frameLayout);
        fragmentManager = getFragmentManager();
    }
    private void initFragment(){
        logoFragment = new LogoFragment();
        logoFragment.setMyClickButton(new LogoFragment.Click() {
            @Override
            public void loginClick() {
                FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.logo_frameLayout,new LoginFragment());
                isLogoFra = false;
                fragmentTransaction.commit();
            }

            @Override
            public void registerClick() {
                FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.logo_frameLayout,new RegisterFragment());
                isLogoFra = false;
                fragmentTransaction.commit();
            }
        });
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.logo_frameLayout, logoFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0&&!isLogoFra) {
            FragmentTransaction fragmentTransaction =  getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.logo_frameLayout,logoFragment);
            isLogoFra = true;
            fragmentTransaction.commit();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
