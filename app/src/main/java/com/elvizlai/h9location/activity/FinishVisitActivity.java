package com.elvizlai.h9location.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Elvizlai on 14-9-3.
 */
public class FinishVisitActivity extends Activity {
    //定位相关
    private final MyLocationListenner myListener = new MyLocationListenner();
    private final double[] lalo = new double[2];
    private WriteSiteNote writeSiteNote;
    private TextView location_textview, clientName_textview, beginLocation_textview, beginTime_textview, visitReason_textview;
    private EditText visitResult_edittext;
    private CheckBox isSyncDirary;
    private Button finishVisit_button;
    private LocationClient mLocationClient;
    private String siteAddress;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finishvisit);
        getActionBar().setTitle("结束访问");
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        initView();
        restoreData();

        //百度地图初始化
        SDKInitializer.initialize(getApplicationContext());
        getGpsLocation();
    }

    private void initView() {
        location_textview = (TextView) findViewById(R.id.location_textview);
        clientName_textview = (TextView) findViewById(R.id.clientName_textview);
        beginLocation_textview = (TextView) findViewById(R.id.beginLocation_textview);
        beginTime_textview = (TextView) findViewById(R.id.beginTime_textview);
        visitReason_textview = (TextView) findViewById(R.id.visitReason_textview);
        visitResult_edittext = (EditText) findViewById(R.id.visitResult_edittext);
        isSyncDirary = (CheckBox) findViewById(R.id.isSyncDirary);
        finishVisit_button = (Button) findViewById(R.id.finishVisit_button);
        finishVisit_button.setOnClickListener(new BtnClickHandler());
    }

    private void restoreData() {
        writeSiteNote = Config.getInstance().restoreWriteSiteNote();
        if (writeSiteNote == null) {
            writeSiteNote = new WriteSiteNote();
            getDataFromService();
        } else {
            clientName_textview.setText(writeSiteNote.getCompanyName());
            beginLocation_textview.setText(writeSiteNote.getSiteAddress());
            //beginTime_textview.setText(Config.getInstance().get);
            visitReason_textview.setText(writeSiteNote.getSiteNoteContent());
        }
    }

    private void getDataFromService() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sign", Config.getInstance().getSign());
            jsonObject.put("getmyCustomer", 1);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        AsyncHttpUtil.post("GetSiteNoteInfo", "", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Map map = JSONUtil.JsonStr2Map(bytes);
                writeSiteNote.setSiteNoteId((String) map.get("noteId"));

                clientName_textview.setText("" + map.get("companyName"));
                beginLocation_textview.setText("" + map.get("noteAddress"));
                beginTime_textview.setText(map.get("noteTime") + "    开始拜访");
                visitReason_textview.setText("" + map.get("noteNoteContent"));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

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

    private void writeSite() {

        writeSiteNote.setSiteNoteContent(visitResult_edittext.getText().toString());
        writeSiteNote.setSiteGPS(lalo);
        writeSiteNote.setSign(Config.getInstance().getSign());
        writeSiteNote.setIsSign(0);//????
        writeSiteNote.setSiteAddress(siteAddress);

        writeSiteNote.setUserId(Config.getInstance().getUserId());
        writeSiteNote.setSysn2Diary(isSyncDirary.isChecked());//是否同步到日记

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

                String id = (String) map.get("noteId");

                if (id.equals(writeSiteNote.getSiteNoteId())) {
                    //成功后将其置位空
                    Config.getInstance().storeWriteSiteNote(null);
                    Config.getInstance().setIsVisited(0);
                } else {
                    writeSiteNote.setSiteNoteId((String) map.get("noteId"));
                    writeSite();
                }

                setResult(0x0605);
                finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                System.out.println("失败");
            }
        });
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
            location_textview.setText(siteAddress);
            lalo[0] = location.getLongitude();
            lalo[1] = location.getLatitude();

            finishVisit_button.setEnabled(true);

            String result = "地址：" + siteAddress + " 经纬度：" + lalo[0] + "," + lalo[1];
            System.out.println(result);

            mLocationClient.stop();
        }
    }

    private class BtnClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            writeSite();
        }
    }

}
