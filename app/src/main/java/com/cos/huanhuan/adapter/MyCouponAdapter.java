package com.cos.huanhuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.Coupon;
import com.cos.huanhuan.model.MyExchange;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.views.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class MyCouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Coupon> listCoupon;

    public MyCouponAdapter(Context context,List<Coupon> listCoupon) {
        this.context = context;
        this.listCoupon = listCoupon;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_my_coupon, parent,false);
        MyCouponAdapter.ViewHolder holder = new MyCouponAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        {
            //加载数据
            if(listCoupon.get(position).getValid()){
                ((MyCouponAdapter.ViewHolder) holder).coupon_title.setTextColor(context.getResources().getColor(R.color.titleBarTextColor));
                ((MyCouponAdapter.ViewHolder) holder).coupon_title.setText(listCoupon.get(position).getTitle());
                ((MyCouponAdapter.ViewHolder) holder).coupon_desc.setTextColor(context.getResources().getColor(R.color.grey_text_desc));
                ((MyCouponAdapter.ViewHolder) holder).coupon_desc.setText(listCoupon.get(position).getDescription());
                ((ViewHolder) holder).coupon_time.setText(listCoupon.get(position).getEndTime());
                ((MyCouponAdapter.ViewHolder) holder).coupon_disabled.setVisibility(View.GONE);
                ((MyCouponAdapter.ViewHolder) holder).couponToUse.setVisibility(View.VISIBLE);
            }else{
                ((MyCouponAdapter.ViewHolder) holder).coupon_title.setTextColor(context.getResources().getColor(R.color.forgetPassText));
                ((MyCouponAdapter.ViewHolder) holder).coupon_title.setText(listCoupon.get(position).getTitle());
                ((MyCouponAdapter.ViewHolder) holder).coupon_desc.setTextColor(context.getResources().getColor(R.color.forgetPassText));
                ((MyCouponAdapter.ViewHolder) holder).coupon_desc.setText(listCoupon.get(position).getDescription());
                ((MyCouponAdapter.ViewHolder) holder).coupon_time.setText(listCoupon.get(position).getEndTime());
                ((MyCouponAdapter.ViewHolder) holder).coupon_disabled.setVisibility(View.VISIBLE);
                ((MyCouponAdapter.ViewHolder) holder).couponToUse.setVisibility(View.GONE);
            }
            //点击事件监听
            final Button couponToUse = ((MyCouponAdapter.ViewHolder) holder).couponToUse;
            ((MyCouponAdapter.ViewHolder) holder).couponToUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    useCouponClickClickListener.useCouponClick(couponToUse,position);
                }
            });
            final LinearLayout itemLL = ((ViewHolder) holder).ll_adapter_itemClick;
            ((MyCouponAdapter.ViewHolder) holder).ll_adapter_itemClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.OnItemClick(itemLL,position);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return listCoupon.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView coupon_title,coupon_desc,coupon_time,coupon_disabled;
        private Button couponToUse;
        private LinearLayout ll_adapter_itemClick;
        public ViewHolder(View itemView) {
            super(itemView);
            coupon_title = (TextView) itemView.findViewById(R.id.tv_adapter_coupon_title);
            coupon_desc = (TextView) itemView.findViewById(R.id.tv_coupon_desc);
            coupon_time = (TextView) itemView.findViewById(R.id.tv_adapter_coupon_time);
            coupon_disabled = (TextView) itemView.findViewById(R.id.tv_adapter_coupon_disabled);
            couponToUse = (Button) itemView.findViewById(R.id.btn_adapter_couponToUse);
            ll_adapter_itemClick = (LinearLayout) itemView.findViewById(R.id.ll_adapter_itemClick);
        }
    }

    //去使用优惠券
    public interface UseCouponClick{
        void useCouponClick(View view, int position);
    }
    private UseCouponClick useCouponClickClickListener;
    public void setUseCouponClick(UseCouponClick listener){
        this.useCouponClickClickListener = listener;
    }

    private OnItemClick onItemClickListener;

    public interface OnItemClick{
        void OnItemClick(View view,int position);
    }
    public void setOnItemClick(OnItemClick onClickListener){
        this.onItemClickListener = onClickListener;
    }
}
