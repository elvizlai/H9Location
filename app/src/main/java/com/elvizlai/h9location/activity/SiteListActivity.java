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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elvizlai.h9location.R;
import com.elvizlai.h9location.SiteItemAdapter;
import com.elvizlai.h9location.config.Config;
import com.elvizlai.h9location.entity.MySiteNote;
import com.elvizlai.h9location.entity.SiteNote;
import com.elvizlai.h9location.entity.SiteParameter;
import com.elvizlai.h9location.util.AsyncHttpUtil;
import com.elvizlai.h9location.util.JSONUtil;
import com.elvizlai.h9location.util.POAException;
import com.elvizlai.h9location.util.TimeUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Elvizlai on 14-9-2.
 */
public class SiteListActivity extends Activity {
    String notetype = "1";
    private long mExitTime;
    private int scrolledX, scrolledY;
    private TextView footerText;
    private boolean loadingMore = false;
    private int totalSize;
    private List<SiteNote> siteNoteList = new ArrayList<SiteNote>();
    private ListView siteItems;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x1989 && resultCode == 0x8916) {
            getListFromService(true);
        }else if (requestCode==0x1989&&resultCode==0x0605){
            //完成刷新
            getListFromService(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitelist);

        getActionBar().setTitle("现场记录");

        initView();
        getListFromService(false);
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.msg_refreshable_view);
        siteItems = (ListView) findViewById(R.id.siteItems);
        footerText = new TextView(SiteListActivity.this);


        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListFromService(true);
            }
        });


    }

    private void getListFromService(final boolean isRefreshing) {


        String time = TimeUtil.getFormattedTimeStr();
        //如果是加载更多
        if (loadingMore) {
            time = siteNoteList.get(totalSize - 1).getNoteTime();
        } else

            //如果是刷新
            if (isRefreshing) {
                if (siteNoteList != null && siteNoteList.size() > 0) {
                    time = siteNoteList.get(0).getNoteTime();
                    notetype = "-1";
                }
            }

        SiteParameter siteParameter = new SiteParameter();
        siteParameter.setCount("20");
        siteParameter.setNoteTime(time);
        siteParameter.setNoteType(notetype);
        siteParameter.setSign(Config.getInstance().getSign());
        siteParameter.setUserId(Config.getInstance().getUserId());

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("siteParameter", JSONUtil.format(siteParameter));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AsyncHttpUtil.post("getMoreOrNewSiteList", jsonObject, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                loadingMore = false;
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);

                try {
                    MySiteNote mySiteNote = JSONUtil.parse(new String(bytes), MySiteNote.class);

                    int size = mySiteNote.getSiteNotes().size();

                    totalSize += size;


                    System.out.println(totalSize + " size");

                    for (int x = 0; x < size; x++) {
                        if (isRefreshing) {
                            siteNoteList.add(0, map2SiteNote((Map) mySiteNote.getSiteNotes().get(size - x - 1)));
                        } else {
                            siteNoteList.add(map2SiteNote((Map) mySiteNote.getSiteNotes().get(x)));
                        }
                    }

                    //如果数量为20的倍数，则加载显示加载更多的按钮
                    if (totalSize % 20 == 0) {
                        if (siteItems.getFooterViewsCount() == 0) {
                            footerText.setText("点击加载更多...");
                            footerText.setTextSize(22);
                            footerText.setGravity(Gravity.CENTER);
                            siteItems.addFooterView(footerText);
                        }
                    } else {
                        siteItems.removeFooterView(footerText);
                    }

                    siteItems.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            if (scrollState == SCROLL_STATE_IDLE) {
                                if (siteItems != null) {
                                    scrolledX = siteItems.getLastVisiblePosition();
                                }
                            }
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        }
                    });

                    siteItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == totalSize) {
                                loadingMore = true;
                                //加载更多
                                getListFromService(false);
                            }
                        }
                    });

                    //todo add list
                    SiteItemAdapter siteItemAdapter = new SiteItemAdapter(siteNoteList);
                    siteItems.setAdapter(siteItemAdapter);

                    if ("-1".equals(notetype)) {
                        siteItems.setSelectionFromTop(0, 0);
                    } else {
                        siteItems.setSelectionFromTop(scrolledX, scrolledY);
                    }

                } catch (POAException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                System.out.println("err:" + i);
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