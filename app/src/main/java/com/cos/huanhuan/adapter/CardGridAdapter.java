package com.cos.huanhuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.fragments.IndexFragment;
import com.cos.huanhuan.model.CardExchange;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.PicassoUtils;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.util.List;

/**
 * Created by Administrator on 2017/8/14.
 */

public class CardGridAdapter extends Adapter<RecyclerView.ViewHolder>{

    //item类型
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_CONTENT = 1;
    private Context mContext;
    private List<CardExchange> cardList;
    private View mHeaderView;
    private String[] paths = {"http://a2.qpic.cn/psb?/V10czDLU2UlwEh/1S47MQyOiG2Qg6izfj.Ji2JgwjfI40XITjhUM5IYNfQ!/b/dG0BAAAAAAAA&ek=1&kp=1&pt=0&bo=igJoAYoCaAEDACU!&tm=1502679600&sce=0-12-12&rf=0-18",
            "http://a2.qpic.cn/psb?/V10czDLU2UlwEh/1S47MQyOiG2Qg6izfj.Ji2JgwjfI40XITjhUM5IYNfQ!/b/dG0BAAAAAAAA&ek=1&kp=1&pt=0&bo=igJoAYoCaAEDACU!&tm=1502679600&sce=0-12-12&rf=0-18",
            "http://a2.qpic.cn/psb?/V10czDLU2UlwEh/1S47MQyOiG2Qg6izfj.Ji2JgwjfI40XITjhUM5IYNfQ!/b/dG0BAAAAAAAA&ek=1&kp=1&pt=0&bo=igJoAYoCaAEDACU!&tm=1502679600&sce=0-12-12&rf=0-18",
            "http://a2.qpic.cn/psb?/V10czDLU2UlwEh/1S47MQyOiG2Qg6izfj.Ji2JgwjfI40XITjhUM5IYNfQ!/b/dG0BAAAAAAAA&ek=1&kp=1&pt=0&bo=igJoAYoCaAEDACU!&tm=1502679600&sce=0-12-12&rf=0-18",
            "http://a2.qpic.cn/psb?/V10czDLU2UlwEh/1S47MQyOiG2Qg6izfj.Ji2JgwjfI40XITjhUM5IYNfQ!/b/dG0BAAAAAAAA&ek=1&kp=1&pt=0&bo=igJoAYoCaAEDACU!&tm=1502679600&sce=0-12-12&rf=0-18"};
    //适配器初始化
    public CardGridAdapter(Context context,List<CardExchange> datas) {
        this.mContext=context;
        this.cardList=datas;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return ITEM_TYPE_CONTENT;
        if(position == 0) return ITEM_TYPE_HEADER;
        return ITEM_TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        if(mHeaderView != null && viewType == ITEM_TYPE_HEADER) return new HeaderHolder(mHeaderView);

        View view = LayoutInflater.from(mContext).inflate(R.layout.card_grid_index, parent,false);//这个布局就是一个imageview用来显示图片

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == ITEM_TYPE_HEADER) {
            //设置播放时间间隔
            ((HeaderHolder)holder).roll_view_pager.setPlayDelay(5000);
            //设置透明度
            ((HeaderHolder)holder).roll_view_pager.setAnimationDurtion(300);
            //设置适配器
            ((HeaderHolder)holder).roll_view_pager.setAdapter(new TestNormalAdapter(mContext,paths));
            ((HeaderHolder)holder).roll_view_pager.setHintView(new ColorPointHintView(mContext, mContext.getResources().getColor(R.color.titleBarTextColor), Color.WHITE));
        }else{
            final int pos = getRealPosition(holder);
            PicassoUtils.getinstance().LoadImage(mContext,cardList.get(pos).getCardImgUrl(),((MyViewHolder) holder).card_img_adapter,R.mipmap.ic_launcher,R.mipmap.ic_launcher,PicassoUtils.PICASSO_BITMAP_SHOW_ROUND_TYPE,0);
            ((MyViewHolder) holder).card_title_adapter.setText(cardList.get(pos).getCardTitle());
            ((MyViewHolder) holder).card_user_adapter.setText(cardList.get(pos).getCreateName());

            //判断是否设置了监听器
            if(mOnItemClickListener != null){
                //为ItemView设置监听器
                ((MyViewHolder) holder).card_img_adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.OnImageClick(holder.itemView,position); // 2
                    }
                });
            }
            if(mOnItemLongClickListener != null){
                ((MyViewHolder) holder).card_user_adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        mOnItemLongClickListener.OnUserClick(holder.itemView,position);
                        //返回true 表示消耗了事件 事件不会继续传递
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? cardList.size() : cardList.size() + 1;
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView card_img_adapter;
        private TextView card_title_adapter,card_user_adapter;
        public MyViewHolder(View view) {
            super(view);
            card_img_adapter = (ImageView) view.findViewById(R.id.card_img_adapter);
            card_title_adapter = (TextView) view.findViewById(R.id.card_title_adapter);
            card_user_adapter = (TextView) view.findViewById(R.id.card_user_adapter);
        }
    }
    public interface OnImageClick{
        void OnImageClick(View view,int position);
    }

    public interface OnUserClick{
        void OnUserClick(View view,int position);
    }
    private OnImageClick mOnItemClickListener;
    private OnUserClick mOnItemLongClickListener;

    public void setOnImageClick(OnImageClick onClickListener){
        this.mOnItemClickListener = onClickListener;
    }

    public void setOnUserClick(OnUserClick onClickListener) {
        this.mOnItemLongClickListener = onClickListener;
    }
    class HeaderHolder extends RecyclerView.ViewHolder {

        private RollPagerView roll_view_pager;
        private TextView tv_viewAllExchange;

        public HeaderHolder(View itemView) {
            super(itemView);
            roll_view_pager = (RollPagerView) itemView.findViewById(R.id.roll_view_pager);
            tv_viewAllExchange = (TextView) itemView.findViewById(R.id.tv_viewAllExchange);
        }
    }

    private class TestNormalAdapter extends StaticPagerAdapter {
        private Context context;
        private String[] paths;
        private int[] imgs = {
                R.mipmap.banner1,
                R.mipmap.banner2,
                R.mipmap.banner3,
                R.mipmap.banner4
        };

        public TestNormalAdapter(Context context, String[] paths) {
            this.context = context;
            this.paths = paths;
        }

        @Override
        public View getView(ViewGroup container, int position) {
            View view = View.inflate(context, R.layout.scroll_img, null);
            ImageView img = (ImageView) view.findViewById(R.id.scrollImg);
            float roundRadius = 0;
            PicassoUtils.getinstance().LoadImage(context,paths[position],img,R.mipmap.ic_launcher,R.mipmap.ic_launcher,PicassoUtils.PICASSO_BITMAP_SHOW_ROUND_TYPE,roundRadius);
            //PicassoUtils.getinstance().LoadImage(context,imgs[position],img,PicassoUtils.PICASSO_BITMAP_SHOW_ROUND_TYPE,roundRadius);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            return view;
        }


        @Override
        public int getCount() {
            return imgs.length;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == ITEM_TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }
}