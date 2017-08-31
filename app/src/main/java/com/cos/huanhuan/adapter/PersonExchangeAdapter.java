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
import com.cos.huanhuan.model.PersonPublish;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.PicassoUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class PersonExchangeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<PersonPublish> listExchange;

    public PersonExchangeAdapter(Context context, List<PersonPublish> listExchange) {
        this.context = context;
        this.listExchange = listExchange;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_exchange, parent,false);//这个布局就是一个imageview用来显示图片展示数据
        PersonExchangeAdapter.ViewHolder holder = new PersonExchangeAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        {
            //加载数据
            PicassoUtils.getinstance().LoadImage(context, listExchange.get(position).getCover(), ((PersonExchangeAdapter.ViewHolder) holder).adapter_exchange_cover, R.mipmap.public_placehold, R.mipmap.public_placehold, PicassoUtils.PICASSO_BITMAP_SHOW_NORMAL_TYPE, 0);
            ((PersonExchangeAdapter.ViewHolder) holder).adapter_exchange_title.setText(listExchange.get(position).getTitle());
            String author = "<font color='#666666'>By </font><font color='#4083A9'>" + listExchange.get(position).getNickname() + "</font>";
            ((PersonExchangeAdapter.ViewHolder) holder).adapter_exchange_author.setText(Html.fromHtml(author));
            ((PersonExchangeAdapter.ViewHolder) holder).adapter_exchange_status.setText(listExchange.get(position).getExamine());
            ((PersonExchangeAdapter.ViewHolder) holder).adapter_exchange_time.setText(String.valueOf(listExchange.get(position).getAddTime()));

            if(listExchange.get(position).getExamine().equals("审核中")){
                listExchange.get(position).setDelete(true);
            }else if(listExchange.get(position).getExamine().contains("审核完成")){
                listExchange.get(position).setAgree(true);
                listExchange.get(position).setRefuse(true);
            }else if((!listExchange.get(position).getExamine().equals("已结束")) && AppStringUtils.isEmpty(listExchange.get(position).getLogisticCode())){
                 listExchange.get(position).setSendGoods(true);
            }
            if(listExchange.get(position).getDelete()){
                ((PersonExchangeAdapter.ViewHolder) holder).exchange_delete.setVisibility(View.VISIBLE);
            }else{
                ((PersonExchangeAdapter.ViewHolder) holder).exchange_delete.setVisibility(View.GONE);
            }
            if(listExchange.get(position).getAgree()){
                ((PersonExchangeAdapter.ViewHolder) holder).agree_exchange.setVisibility(View.VISIBLE);
            }else{
                ((PersonExchangeAdapter.ViewHolder) holder).agree_exchange.setVisibility(View.GONE);
            }
            if(listExchange.get(position).getRefuse()){
                ((PersonExchangeAdapter.ViewHolder) holder).refuse_exchange.setVisibility(View.VISIBLE);
            }else{
                ((PersonExchangeAdapter.ViewHolder) holder).refuse_exchange.setVisibility(View.GONE);
            }
            if(listExchange.get(position).getSendGoods()){
                ((PersonExchangeAdapter.ViewHolder) holder).sendGoods_exchange.setVisibility(View.VISIBLE);
            }else{
                ((PersonExchangeAdapter.ViewHolder) holder).sendGoods_exchange.setVisibility(View.GONE);
            }


            //点击事件监听
            final TextView refuse_exchange = ((PersonExchangeAdapter.ViewHolder) holder).refuse_exchange;
            ((PersonExchangeAdapter.ViewHolder) holder).refuse_exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refuseClickListener.refuseClick(refuse_exchange,position);
                }
            });
            final TextView agree_exchange = ((PersonExchangeAdapter.ViewHolder) holder).agree_exchange;
            ((PersonExchangeAdapter.ViewHolder) holder).agree_exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    agreeClickListener.agreeClick(agree_exchange,position);
                }
            });
            final TextView sendGoods_exchange = ((PersonExchangeAdapter.ViewHolder) holder).sendGoods_exchange;
            ((PersonExchangeAdapter.ViewHolder) holder).sendGoods_exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deliveryClickListener.deliverClick(sendGoods_exchange,position);
                }
            });
            final TextView address_delete = ((PersonExchangeAdapter.ViewHolder) holder).exchange_delete;
            ((PersonExchangeAdapter.ViewHolder) holder).exchange_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteClickListener.deleteClick(address_delete,position);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return listExchange.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
       public ImageView adapter_exchange_cover;
       public TextView adapter_exchange_title,adapter_exchange_author,adapter_exchange_status,adapter_exchange_time,refuse_exchange,agree_exchange,sendGoods_exchange,exchange_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            adapter_exchange_cover = (ImageView) itemView.findViewById(R.id.iv_adapter_exchange_cover);
            adapter_exchange_title = (TextView) itemView.findViewById(R.id.tv_adapter_exchange_title);
            adapter_exchange_author = (TextView) itemView.findViewById(R.id.tv_adapter_exchange_author);
            adapter_exchange_time = (TextView) itemView.findViewById(R.id.tv_adapter_exchange_time);
            adapter_exchange_status = (TextView) itemView.findViewById(R.id.tv_adapter_exchange_status);
            refuse_exchange = (TextView) itemView.findViewById(R.id.tv_refuse_exchange);
            agree_exchange = (TextView) itemView.findViewById(R.id.tv_agree_exchange);
            sendGoods_exchange = (TextView) itemView.findViewById(R.id.tv_sendGoods_exchange);
            exchange_delete = (TextView)itemView.findViewById(R.id.tv_exchange_delete);
        }
    }

    //拒绝按钮点击
    public interface RefuseClick{
        void refuseClick(View view, int position);
    }
    //同意按钮点击
    public interface AgreeClick{
        void agreeClick(View view, int position);
    }
    //删除按钮点击
    public interface DeleteClick{
        void deleteClick(View view, int position);
    }
    //发货按钮点击
    public interface DeliverClick{
        void deliverClick(View view, int position);
    }
    private RefuseClick refuseClickListener;
    private AgreeClick agreeClickListener;
    private DeleteClick deleteClickListener;
    private DeliverClick deliveryClickListener;
    public void setRefuseClick(RefuseClick listener){
        this.refuseClickListener = listener;
    }
    public void setAgreeClick(AgreeClick listener){
        this.agreeClickListener = listener;
    }
    public void setDeliverClick(DeliverClick listener){
        this.deliveryClickListener = listener;
    }
    public void setDeleteClick(DeleteClick listener){
        this.deleteClickListener = listener;
    }
}
