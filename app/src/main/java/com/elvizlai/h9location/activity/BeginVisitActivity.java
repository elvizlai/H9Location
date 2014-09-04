package com.elvizlai.h9location.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.elvizlai.h9location.R;
import com.elvizlai.h9location.config.Config;
import com.elvizlai.h9location.entity.WriteSiteNote;
import com.elvizlai.h9location.util.AsyncHttpUtil;
import com.elvizlai.h9location.util.JSONUtil;
import com.elvizlai.h9location.util.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Elvizlai on 14-9-3.
 */
public class BeginVisitActivity extends Activity {
    //定位相关
    private final MyLocationListenner myListener = new MyLocationListenner();
    private final double[] lalo = new double[2];
    WriteSiteNote writeSiteNote;
    private LocationClient mLocationClient;
    private String siteAddress;

    //UI
    private TextView beginvisit_location;
    private EditText beginvisit_clientName, beginvisit_visitReason;
    private Button beginVisit_button;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginvisit);
        getActionBar().setTitle("开始访问");
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        initView();

        //百度地图初始化
        SDKInitializer.initialize(getApplicationContext());
        getGpsLocation();
    }

    private void initView() {
        beginvisit_location = (TextView) findViewById(R.id.beginvisit_location);
        beginvisit_clientName = (EditText) findViewById(R.id.beginvisit_clientName);
        beginvisit_visitReason = (EditText) findViewById(R.id.beginvisit_visitReason);
        beginVisit_button = (Button) findViewById(R.id.beginVisit_button);
        beginVisit_button.setOnClickListener(new BtnClickHandler());
    }

    private boolean isInputValid() {
        if ("".equals(beginvisit_clientName.getText().toString().trim())) {
            ToastUtil.showMsg("客户名称不能为空");
            return false;
        } else if ("".equals(beginvisit_visitReason.getText().toString().trim())) {
            ToastUtil.showMsg("拜访原因不能为空");
            return false;
        }
        return true;
    }

    private void writeSite() {
        if (!isInputValid())
            return;

        writeSiteNote = new WriteSiteNote();
        writeSiteNote.setCompanyName(beginvisit_clientName.getText().toString());//设置公司名称
        writeSiteNote.setSiteGPS(lalo);
        writeSiteNote.setSign(Config.getInstance().getSign());
        writeSiteNote.setIsSign(0);//????
        writeSiteNote.setSiteAddress(siteAddress);
        writeSiteNote.setSiteNoteContent(beginvisit_visitReason.getText().toString());
        writeSiteNote.setUserId(Config.getInstance().getUserId());
        writeSiteNote.setSysn2Diary(false);//是否同步到日记
        writeSiteNote.setSiteNoteId("");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("writeSiteNote", JSONUtil.format(writeSiteNote));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        AsyncHttpUtil.post("WriteSiteNote", jsonObject, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Map map = JSONUtil.JsonStr2Map(bytes);
                writeSiteNote.setSiteNoteId((String) map.get("noteId"));
                Config.getInstance().storeWriteSiteNote(writeSiteNote);
                Config.getInstance().setIsVisited(1);

                ToastUtil.showMsg("记录成功");
                setResult(0x8916);
                finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.showMsg("失败");
            }
        });
    }

    private void getGpsLocation() {
        mLocationClient = new LocationClient(this);     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式--高精度
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(1000);//设置发起定位请求的间隔时间为1000ms
        option.setProdName("ElvizLai");
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //返回，不需要刷新
        finish();
        return true;
    }

    private class BtnClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            writeSite();
        }
    }

    /**
     * 定位SDK监听函数
     */
    private class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            siteAddress = location.getAddrStr();
            beginvisit_location.setText(siteAddress);
            lalo[0] = location.getLongitude();
            lalo[1] = location.getLatitude();

            beginVisit_button.setEnabled(true);

            String result = "地址：" + siteAddress + " 经纬度：" + lalo[0] + "," + lalo[1];
            System.out.println(result);

            mLocationClient.stop();
        }
    }

}
