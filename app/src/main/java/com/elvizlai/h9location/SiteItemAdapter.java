package com.elvizlai.h9location;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elvizlai.h9location.entity.SiteNote;
import com.elvizlai.h9location.util.ApplictionUtil;

import java.util.List;

/**
 * Created by Elvizlai on 14-9-2.
 */
public class SiteItemAdapter extends BaseAdapter {
    private List<SiteNote> mSiteNotes;
    private LayoutInflater mLayoutInflater = LayoutInflater.from(ApplictionUtil.getContext());

    public SiteItemAdapter(List<SiteNote> siteNotes) {
        mSiteNotes = siteNotes;
    }

    @Override
    public int getCount() {
        return mSiteNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.v("ElvizLai", "getView " + position + " " + convertView);

        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.siteitem, null);
            holder = new ViewHolder();
            holder.startLoc = (TextView) convertView.findViewById(R.id.startLoc);
            holder.startTime = (TextView) convertView.findViewById(R.id.startTime);
            holder.clientName = (TextView) convertView.findViewById(R.id.clientName);
            holder.visitReason = (TextView) convertView.findViewById(R.id.visitReason);

            holder.finishTime = (TextView) convertView.findViewById(R.id.finishTime);
            holder.finishLoc = (TextView) convertView.findViewById(R.id.finishLoc);
            holder.visitResult = (TextView) convertView.findViewById(R.id.visitResult);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.startLoc.setText(mSiteNotes.get(position).getNoteAddress());
        holder.startTime.setText(mSiteNotes.get(position).getNoteTime() + "    开始拜访");
        holder.clientName.setText(mSiteNotes.get(position).getVisitCompanyName());
        holder.visitReason.setText("拜访原因：" + mSiteNotes.get(position).getNoteNoteContent());

        if ("".equals(mSiteNotes.get(position).getEndNoteTime())) {
            holder.finishTime.setText("未写拜访记录");
            holder.finishLoc.setText("");
            holder.visitResult.setText("");
        } else {
            holder.finishTime.setText(mSiteNotes.get(position).getEndNoteTime() + "    结束拜访");
            holder.finishLoc.setText(mSiteNotes.get(position).getEndNoteAddress());
            holder.visitResult.setText("拜访记录：" + mSiteNotes.get(position).getEndNoteNoteContent());
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView startLoc, startTime, clientName, visitReason, finishTime, finishLoc, visitResult;
    }
}
