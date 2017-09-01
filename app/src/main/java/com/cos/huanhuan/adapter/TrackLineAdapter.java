package com.cos.huanhuan.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.AddressVO;
import com.cos.huanhuan.model.TrackLine;
import com.foamtrace.photopicker.Image;

import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class TrackLineAdapter extends BaseAdapter {

    private Context context;
    private List<TrackLine> listTimeLine;

    public TrackLineAdapter(Context context, List<TrackLine> listTimeLine) {
        this.context = context;
        this.listTimeLine = listTimeLine;
    }

    @Override
    public int getCount() {
        return listTimeLine.size();
    }

    @Override
    public Object getItem(int i) {
        return listTimeLine.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if(convertView ==null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_track_line, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_adapter_trackingDesc= (TextView) convertView.findViewById(R.id.tv_adapter_trackingDesc);
                viewHolder.tv_adapter_trackingTime= (TextView) convertView.findViewById(R.id.tv_adapter_trackingTime);
                viewHolder.iv_adapter_track = (ImageView) convertView.findViewById(R.id.iv_adapter_track);
                convertView.setTag(viewHolder);//绑定ViewHolder对象
            }else{
                viewHolder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
            }
            if(position == 0){
                viewHolder.tv_adapter_trackingDesc.setTextColor(context.getResources().getColor(R.color.trackText));
                viewHolder.tv_adapter_trackingTime.setTextColor(context.getResources().getColor(R.color.trackText));
                viewHolder.iv_adapter_track.setImageResource(R.mipmap.my_logistics);
            }else{
                viewHolder.tv_adapter_trackingDesc.setTextColor(context.getResources().getColor(R.color.grey_text_desc));
                viewHolder.tv_adapter_trackingTime.setTextColor(context.getResources().getColor(R.color.grey_text_desc));
                viewHolder.iv_adapter_track.setImageResource(R.drawable.time_cycle);
            }
            viewHolder.tv_adapter_trackingDesc.setText(listTimeLine.get(position).getValue());
            viewHolder.tv_adapter_trackingTime.setText(listTimeLine.get(position).getKey());
            return convertView;
        }
        class ViewHolder
        {
            public TextView tv_adapter_trackingDesc;
            public TextView tv_adapter_trackingTime;
            public ImageView iv_adapter_track;
        }
}
