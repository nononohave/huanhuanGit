package com.cos.huanhuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.CardCoop;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.views.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/8/14.
 */

public class CoopCardGridAdapter extends Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<CardCoop> cardList;
    //适配器初始化
    public CoopCardGridAdapter(Context context, List<CardCoop> datas) {
        this.mContext=context;
        this.cardList=datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_grid_coop, parent,false);//这个布局就是一个imageview用来显示图片展示数据
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        {
            PicassoUtils.getinstance().LoadImage(mContext, cardList.get(position).getPortrait(), ((MyViewHolder) holder).iv_coop_headImg, R.mipmap.public_placehold, R.mipmap.public_placehold, PicassoUtils.PICASSO_BITMAP_SHOW_ROUND_TYPE, 0);
            PicassoUtils.getinstance().LoadImage(mContext, cardList.get(position).getCardImgUrl(), ((MyViewHolder) holder).card_img_coop, R.mipmap.public_placehold, R.mipmap.public_placehold, PicassoUtils.PICASSO_BITMAP_SHOW_ROUND_TYPE, 0);
            ((MyViewHolder) holder).card_title_coop.setText(cardList.get(position).getCardTitle());
            ((MyViewHolder) holder).card_user_coop.setText(cardList.get(position).getCreateName());
            ((MyViewHolder) holder).tv_card_address_coop.setText(cardList.get(position).getAddress());
            ((MyViewHolder) holder).tv_card_persons_coop.setText(String.valueOf(cardList.get(position).getPersonNum()));
            //判断是否设置了监听器
            if (mOnItemClickListener != null) {
                //为ItemView设置监听器
                ((MyViewHolder) holder).card_img_coop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.OnImageClick(holder.itemView, position); // 2
                    }
                });
            }
            if (mOnUserClickListener != null) {
                ((MyViewHolder) holder).card_user_coop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        mOnUserClickListener.OnUserClick(holder.itemView, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView iv_coop_headImg;
        private ImageView card_img_coop;
        private TextView card_title_coop, card_user_coop,tv_card_address_coop,tv_card_persons_coop;

        public MyViewHolder(View view) {
            super(view);
            iv_coop_headImg = (CircleImageView) view.findViewById(R.id.iv_coop_headImg);
            card_img_coop = (ImageView) view.findViewById(R.id.card_img_coop_adapter);
            card_title_coop = (TextView) view.findViewById(R.id.card_title_coop_adapter);
            card_user_coop= (TextView) view.findViewById(R.id.card_user_coop_adapter);
            tv_card_address_coop = (TextView) view.findViewById(R.id.tv_card_address_coop_adapter);
            tv_card_persons_coop = (TextView) view.findViewById(R.id.tv_card_persons_coop_adapter);
        }
    }
    public interface OnImageClick{
        void OnImageClick(View view, int position);
    }

    public interface OnUserClick{
        void OnUserClick(View view, int position);
    }
    private OnImageClick mOnItemClickListener;
    private OnUserClick mOnUserClickListener;

    public void setOnImageClick(OnImageClick onClickListener){
        this.mOnItemClickListener = onClickListener;
    }

    public void setOnUserClick(OnUserClick onClickListener) {
        this.mOnUserClickListener = onClickListener;
    }
}
