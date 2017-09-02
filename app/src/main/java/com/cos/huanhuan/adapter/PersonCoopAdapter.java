package com.cos.huanhuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.cos.huanhuan.model.PersonCoop;
import com.cos.huanhuan.model.PersonPublish;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.PicassoUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class PersonCoopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<PersonCoop> listCoop;

    public PersonCoopAdapter(Context context, List<PersonCoop> listCoop) {
        this.context = context;
        this.listCoop = listCoop;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_coop, parent,false);//这个布局就是一个imageview用来显示图片展示数据
        PersonCoopAdapter.ViewHolder holder = new PersonCoopAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        {
            //加载数据
            PicassoUtils.getinstance().LoadImage(context, listCoop.get(position).getCover(), ((PersonCoopAdapter.ViewHolder) holder).iv_adapter_coop_cover, R.mipmap.public_placehold, R.mipmap.public_placehold, PicassoUtils.PICASSO_BITMAP_SHOW_NORMAL_TYPE, 0);
            ((PersonCoopAdapter.ViewHolder) holder).tv_adapter_coop_title.setText(listCoop.get(position).getTitle());
            String author = "<font color='#666666'>By </font><font color='#4083A9'>" + listCoop.get(position).getNickname() + "</font>";
            ((PersonCoopAdapter.ViewHolder) holder).tv_adapter_coop_author.setText(Html.fromHtml(author));
            ((PersonCoopAdapter.ViewHolder) holder).tv_adapter_coop__address.setText(listCoop.get(position).getCity());
            String joinText = "<font color='#FF6FA2'>" + listCoop.get(position).getPersonNum() + "</font>" + "人参加";
            ((PersonCoopAdapter.ViewHolder) holder).tv_adapter_coop_joinNums.setText(Html.fromHtml(joinText));

            ((PersonCoopAdapter.ViewHolder) holder).tv_adapter_coop_time.setText(listCoop.get(position).getAddTime());
            final RelativeLayout itemClick = ((PersonCoopAdapter.ViewHolder) holder).rl_adapter_item;
            ((PersonCoopAdapter.ViewHolder) holder).rl_adapter_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listItemClickListener.listItemClick(itemClick,position);
                }
            });

        }
    }
    @Override
    public int getItemCount() {
        return listCoop.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public RelativeLayout rl_adapter_item;
        public ImageView iv_adapter_coop_cover;
        public TextView tv_adapter_coop_title,tv_adapter_coop_author,tv_adapter_coop__address,tv_adapter_coop_time,tv_adapter_coop_joinNums;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_adapter_item = (RelativeLayout) itemView.findViewById(R.id.rl_adapter_item) ;
            iv_adapter_coop_cover = (ImageView) itemView.findViewById(R.id.iv_adapter_coop_cover);
            tv_adapter_coop_title = (TextView) itemView.findViewById(R.id.tv_adapter_coop_title);
            tv_adapter_coop_author = (TextView) itemView.findViewById(R.id.tv_adapter_coop_author);
            tv_adapter_coop_time = (TextView) itemView.findViewById(R.id.tv_adapter_coop_time);
            tv_adapter_coop__address = (TextView) itemView.findViewById(R.id.tv_adapter_coop__address);
            tv_adapter_coop_joinNums = (TextView) itemView.findViewById(R.id.tv_adapter_coop_joinNums);
        }
    }
    public interface ListItemClick{
        void listItemClick(View view,int position);
    }
    private ListItemClick listItemClickListener;
    public void setListItemClick(ListItemClick listener){this.listItemClickListener = listener;}
}
