package com.cos.huanhuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.MyExchange;
import com.cos.huanhuan.model.PersonPublish;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.PicassoUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class MyExchangeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MyExchange> listMyExchange;

    public MyExchangeAdapter(Context context, List<MyExchange> listMyExchange) {
        this.context = context;
        this.listMyExchange = listMyExchange;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_my_exchange, parent,false);//这个布局就是一个imageview用来显示图片展示数据
        MyExchangeAdapter.ViewHolder holder = new MyExchangeAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        {
            //加载数据
            PicassoUtils.getinstance().LoadImage(context, listMyExchange.get(position).getCover(), ((MyExchangeAdapter.ViewHolder) holder).iv_adapter_my_exchange_cover, R.mipmap.public_placehold, R.mipmap.public_placehold, PicassoUtils.PICASSO_BITMAP_SHOW_NORMAL_TYPE, 0);
            ((MyExchangeAdapter.ViewHolder) holder).tv_adapter_exchangeNo.setText("兑换编号：" + listMyExchange.get(position).getSerialNum());
            ((MyExchangeAdapter.ViewHolder) holder).tv_adapter_exchangeStatus.setText(listMyExchange.get(position).getState());
            String author = "<font color='#666666'>By </font><font color='#4083A9'>" + listMyExchange.get(position).getNickname() + "</font>";
            ((MyExchangeAdapter.ViewHolder) holder).tv_adapter_my_exchange_author.setText(Html.fromHtml(author));
            ((MyExchangeAdapter.ViewHolder) holder).tv_adapter_my_exchange_time.setText(listMyExchange.get(position).getAddTime());

            //点击事件监听
            final TextView viewTrackingTv = ((MyExchangeAdapter.ViewHolder) holder).tv_adapter_view_exchange;
            ((MyExchangeAdapter.ViewHolder) holder).tv_adapter_view_exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewTrackingClickListener.viewTrackingClick(viewTrackingTv,position);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return listMyExchange.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
       public ImageView iv_adapter_my_exchange_cover;
       public TextView tv_adapter_exchangeNo,tv_adapter_exchangeStatus,tv_adapter_my_exchange_title,tv_adapter_my_exchange_author,tv_adapter_my_exchange_value,tv_adapter_my_exchange_time,tv_adapter_view_exchange;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_adapter_my_exchange_cover = (ImageView) itemView.findViewById(R.id.iv_adapter_my_exchange_cover);
            tv_adapter_exchangeNo = (TextView) itemView.findViewById(R.id.tv_adapter_exchangeNo);
            tv_adapter_exchangeStatus = (TextView) itemView.findViewById(R.id.tv_adapter_exchangeStatus);
            tv_adapter_my_exchange_author = (TextView) itemView.findViewById(R.id.tv_adapter_my_exchange_author);
            tv_adapter_my_exchange_title = (TextView) itemView.findViewById(R.id.tv_adapter_my_exchange_title);
            tv_adapter_my_exchange_value = (TextView) itemView.findViewById(R.id.tv_adapter_my_exchange_value);
            tv_adapter_my_exchange_time = (TextView) itemView.findViewById(R.id.tv_adapter_my_exchange_time);
            tv_adapter_view_exchange = (TextView)itemView.findViewById(R.id.tv_adapter_view_exchange);
        }
    }

    //查看物流信息
    public interface ViewTrackingClick{
        void viewTrackingClick(View view, int position);
    }
    private ViewTrackingClick viewTrackingClickListener;
    public void setViewTracking(ViewTrackingClick listener){
        this.viewTrackingClickListener = listener;
    }
}
