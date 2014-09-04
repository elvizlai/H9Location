package com.elvizlai.h9location.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.elvizlai.h9location.R;
import com.elvizlai.h9location.config.Config;
import com.elvizlai.h9location.util.CryptUtil;
import com.elvizlai.h9location.util.LogUtil;
import com.elvizlai.h9location.util.ToastUtil;


public class LoginActivity extends Activity {
    //用于记录上一次按返回按钮的时间，用于连按两次返回退出
    private long mExitTime;


    private EditText user_ed, psw_ed;
    private CheckBox isRemPsw_cb, isAutoLogin_cb;
    private Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        restoreData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x1989 && resultCode == 0x1129) {
            ToastUtil.showMsg("用户名或密码错误！");
        } else if (requestCode == 0x1989 && resultCode == 0x0605) {
            ToastUtil.showMsg("无法连接到服务器！");
        }
    }

    /**
     * 初始化视图
     */
    private void init() {
        user_ed = (EditText) findViewById(R.id.user_ed);
        psw_ed = (EditText) findViewById(R.id.psw_ed);

        isRemPsw_cb = (CheckBox) findViewById(R.id.isRemPsw_cb);
        isAutoLogin_cb = (CheckBox) findViewById(R.id.isAutoLogin_cb);

        login_btn = (Button) findViewById(R.id.login_btn);

        View.OnClickListener btnClickedHandler = new btnClickedHandler();

        login_btn.setOnClickListener(btnClickedHandler);
    }


    /**
     * 配置文件恢复
     */
    private void restoreData() {
        user_ed.setText(Config.getInstance().getAccount());

        boolean isRemPsw = Config.getInstance().getIsRemPsw();

        if (isRemPsw) {
            isRemPsw_cb.setChecked(isRemPsw);
            psw_ed.setText(CryptUtil.decryptPsw(Config.getInstance().getPassword()));
        }

        isAutoLogin_cb.setChecked(Config.getInstance().getIsAutoLogin());
    }

    private void storeData() {
        String user = user_ed.getText().toString();
        String psw = psw_ed.getText().toString();

        if (user.equals("") || psw.equals("")) {
            Toast.makeText(getBaseContext(), "输入不合法", Toast.LENGTH_SHORT).show();
            return;
        }

        LogUtil.d(user + " " + psw);
        Config.getInstance().setAccount(user);
        Config.getInstance().setPassword(CryptUtil.encryptPsw(psw));
        Config.getInstance().setIsRemPsw(isRemPsw_cb.isChecked());
        Config.getInstance().setIsAutoLogin(isAutoLogin_cb.isChecked());

    }

    /**
     * 屏蔽返回按钮，按一次返回按钮提示“再按一次退出程序”
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showMsg("再次点击退出程序");
                mExitTime = System.currentTimeMillis();

            } else {
                //该方法对2.2版本后有效
                finish();
                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                manager.killBackgroundProcesses(getPackageName());
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击登录、menu响应
     */
    private class btnClickedHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.login_btn:
                    storeData();
                    Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                    startActivityForResult(intent, 0x1989);
                    break;
                default:
                    break;
            }

        }
    }
}
