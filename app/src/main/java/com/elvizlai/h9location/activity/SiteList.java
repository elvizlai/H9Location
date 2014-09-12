package com.elvizlai.h9location.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.elvizlai.h9location.R;
import com.elvizlai.h9location.SiteItemAdapter;
import com.elvizlai.h9location.config.Config;
import com.elvizlai.h9location.entity.MySiteNote;
import com.elvizlai.h9location.entity.SiteNote;
import com.elvizlai.h9location.entity.SiteParameter;
import com.elvizlai.h9location.util.AsyncHttpUtil;
import com.elvizlai.h9location.util.DateUtil;
import com.elvizlai.h9location.util.JSONUtil;
import com.elvizlai.h9location.util.POAException;
import com.elvizlai.h9location.util.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Elvizlai on 14-9-5.
 */
public class SiteList extends Activity {
    private long mExitTime;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<SiteNote> mSiteNotes = new ArrayList<SiteNote>();
    private ListView siteItems;
    private Button loadingMoreButton;
    private SiteItemAdapter mSiteItemAdapter;
    private int totalSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitelist);

        getActionBar().setTitle("现场记录列表");

        initView();

        initAdapter();

        getListFromService(false);
    }


    private void initView() {


        siteItems = (ListView) findViewById(R.id.siteItems);
        loadingMoreButton = new Button(this);

        //获取下拉刷新控件
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.msg_refreshable_view);
        //设定下拉的样式颜色
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //设定下拉后执行的内容
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListFromService(false);
            }
        });
    }

    private void initAdapter() {
        if (mSiteItemAdapter == null)
            mSiteItemAdapter = new SiteItemAdapter(mSiteNotes);


        loadingMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加载更多
                loadingMoreButton.setText("正在加载......");
                getListFromService(true);
                loadingMoreButton.setEnabled(false);
            }
        });

        siteItems.setAdapter(mSiteItemAdapter);
    }

    private void getListFromService(boolean isloadingMore) {
        String time = DateUtil.getFormattedTimeStr();
        String notetype = "1";

        if (mSwipeRefreshLayout.isRefreshing() && mSiteNotes.get(0) != null) {
            time = mSiteNotes.get(0).getNoteTime();
            notetype = "-1";
        }

        if (isloadingMore) {
            time = mSiteNotes.get(totalSize - 1).getNoteTime();
        }

        SiteParameter siteParameter = new SiteParameter();
        siteParameter.setCount("20");
        siteParameter.setNoteTime(time);//todo
        siteParameter.setNoteType(notetype);//todo
        siteParameter.setSign(Config.getInstance().getSign());
        siteParameter.setUserId(Config.getInstance().getUserId());

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("siteParameter", JSONUtil.format(siteParameter));
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.showMsg("系统异常，暂时无法提交");
            return;
        }

        AsyncHttpUtil.post("getMoreOrNewSiteList", jsonObject, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    siteItems.setSelectionFromTop(0, 0);
                    ToastUtil.showMsg("刷新完毕");
                }

                try {
                    MySiteNote mySiteNote = JSONUtil.parse(new String(bytes), MySiteNote.class);
                    int size = mySiteNote.getSiteNotes().size();
                    totalSize += size;

                    for (int x = 0; x < size; x++) {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSiteNotes.add(0, map2SiteNote((Map) mySiteNote.getSiteNotes().get(size - x - 1)));
                        } else {
                            mSiteNotes.add(map2SiteNote((Map) mySiteNote.getSiteNotes().get(x)));
                        }
                    }

                    //如果数量为20的倍数，则加载显示加载更多的按钮
                    if (totalSize != 0 && totalSize % 20 == 0) {
                        if (siteItems.getFooterViewsCount() == 0) {
                            loadingMoreButton.setText("点击加载更多...");
                            loadingMoreButton.setTextSize(22);
                            loadingMoreButton.setBackground(null);
                            loadingMoreButton.setGravity(Gravity.CENTER);
                            siteItems.addFooterView(loadingMoreButton);
                        }
                    } else {
                        siteItems.removeFooterView(loadingMoreButton);
                    }

                    //通知内容变更
                    mSiteItemAdapter.notifyDataSetChanged();
                } catch (POAException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ToastUtil.showMsg("获取内容失败");
            }

            @Override
            public void onFinish() {
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);

                if (!loadingMoreButton.isEnabled()) {
                    loadingMoreButton.setText("点击加载更多...");
                    loadingMoreButton.setEnabled(true);
                }

                super.onFinish();
            }
        });

    }

    private SiteNote map2SiteNote(Map map) {
        SiteNote siteNote = new SiteNote();
        siteNote.setNoteId((String) map.get("noteId"));

        siteNote.setVisitCompanyName((String) map.get("visitCompanyName"));//到访公司名称
        siteNote.setNoteTime((String) map.get("noteTime"));//开始时间
        siteNote.setNoteAddress((String) map.get("noteAddress"));//地点
        siteNote.setNoteNoteContent((String) map.get("noteNoteContent"));//拜访原因

        siteNote.setEndNoteTime((String) map.get("endNoteTime"));//结束时间
        siteNote.setEndNoteAddress((String) map.get("endNoteAddress"));//结束地点
        siteNote.setEndNoteNoteContent((String) map.get("endNoteNoteContent"));//结束时填写的拜访记录

        //TODO 图片类没有处理
        return siteNote;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recordingsitemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (Config.getInstance().getIsVisited() == 0) {
            intent = new Intent(this, BeginVisitActivity.class);
        } else {
            intent = new Intent(this, FinishVisitActivity.class);
        }
        startActivityForResult(intent, 0x1989);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
