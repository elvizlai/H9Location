package com.elvizlai.h9location.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.elvizlai.h9location.config.Config;
import com.elvizlai.h9location.entity.LoginEstResult;
import com.elvizlai.h9location.util.AsyncHttpUtil;
import com.elvizlai.h9location.util.JSONUtil;
import com.elvizlai.h9location.util.LogUtil;
import com.elvizlai.h9location.util.POAException;
import com.elvizlai.h9location.util.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by elvizlai on 14-8-14.
 */
public class LoadingActivity extends Activity {
    final String sign = Config.getInstance().getSign();
    private LoginEstResult mLoginEstResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.loading);

        LogUtil.d(sign);


        final JSONObject jsonObject = new JSONObject();
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            jsonObject.put("sign", sign);
            jsonObject.put("width", width);
            jsonObject.put("height", height);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        login(jsonObject);
    }

    private void login(JSONObject jsonObject) {
        AsyncHttpUtil.post("LoginEst", jsonObject, new AsyncHttpResponseHandler() {
            final int[] state = new int[1];

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                //说明帐号密码不正确
                if ((Integer) JSONUtil.JsonStr2Map(bytes).get("success") == 0) {
                    state[0] = -1;
                } else {
                    try {
                        mLoginEstResult = JSONUtil.parse(new String(bytes), LoginEstResult.class);
                    } catch (POAException e) {
                        e.printStackTrace();
                    }
                }
            }

            //网络异常
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                System.out.println("er_rcode:" + i + " " + throwable);
                state[0] = -2;
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (state[0] == -1) {
                    setResult(0x1129);
                } else if (state[0] == -2) {
                    setResult(0x0605);
                } else if (mLoginEstResult.getHasSiteControls() == 0) {
                    ToastUtil.showMsg("您的帐号暂时没有开通场控权限，请于管理员联系");
                } else {
                    //保存UserId与是否存在现场未处理标志
                    Config.getInstance().setUserId(mLoginEstResult.getUserId());
                    Config.getInstance().setIsVisited(mLoginEstResult.getIsVisited());

                    Intent intent = new Intent(getBaseContext(), SiteListActivity.class);

                    //设置标志，存在就拉倒前台，不存在就新建一个
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

}
