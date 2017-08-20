package com.cos.huanhuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.Comment;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.views.CircleImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/20.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Comment> listComments;
    private Context context;
    public CommentAdapter(Context context, List<Comment> listComments) {
        this.listComments = listComments;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_comment, parent,false);//这个布局就是一个imageview用来显示图片展示数据
        CommentAdapter.MyViewHolder holder = new CommentAdapter.MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        PicassoUtils.getinstance().LoadImage(context, listComments.get(position).getPortrait(), ((CommentAdapter.MyViewHolder) holder).cim_adapter_comment, R.mipmap.public_placehold, R.mipmap.public_placehold, PicassoUtils.PICASSO_BITMAP_SHOW_NORMAL_TYPE, 0);
        ((CommentAdapter.MyViewHolder) holder).tv_comment_name.setText(listComments.get(position).getNickname());
        ((CommentAdapter.MyViewHolder) holder).tv_comment_time.setText(listComments.get(position).getAddTime());
        int goodNums = listComments.get(position).getLikeNum();
        if(goodNums > 0){
            ((CommentAdapter.MyViewHolder) holder).tv_adapter_goodNums.setText(goodNums);
        }else{
            ((CommentAdapter.MyViewHolder) holder).tv_adapter_goodNums.setText("赞");
        }
        if(listComments.get(position).getLike()){
            ((CommentAdapter.MyViewHolder) holder).iv_adapter_good.setImageResource(R.mipmap.good_red);
            ((CommentAdapter.MyViewHolder) holder).tv_adapter_goodNums.setTextColor(context.getResources().getColor(R.color.titleBarTextColor));
        }else{
            ((CommentAdapter.MyViewHolder) holder).iv_adapter_good.setImageResource(R.mipmap.good_grey);
            ((CommentAdapter.MyViewHolder) holder).tv_adapter_goodNums.setTextColor(context.getResources().getColor(R.color.forgetPassText));
        }

        //回复人id不为0并且回复文字不为空
        if(listComments.get(position).getReplyUserId() != 0 && AppStringUtils.isNotEmpty(listComments.get(position).getReplyNickname())){
            String commentDetail = "回复" + "<font color='#4083A9'>@" + listComments.get(position).getReplyNickname() + "：</font>" +listComments.get(position).getText();
            ((CommentAdapter.MyViewHolder) holder).tv_comment_detail.setText(Html.fromHtml(commentDetail));
        }else{
            ((CommentAdapter.MyViewHolder) holder).tv_comment_detail.setText(listComments.get(position).getText());
        }
    }

    @Override
    public int getItemCount() {
        return listComments.size();
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView cim_adapter_comment;
        private ImageView iv_adapter_good;
        private TextView tv_comment_name, tv_comment_detail,tv_comment_time,tv_adapter_goodNums,tv_adapter_comment;

        public MyViewHolder(View view) {
            super(view);
            cim_adapter_comment = (CircleImageView) view.findViewById(R.id.cim_adapter_comment);
            iv_adapter_good = (ImageView) view.findViewById(R.id.iv_adapter_good);
            tv_comment_name = (TextView) view.findViewById(R.id.tv_comment_name);
            tv_comment_detail= (TextView) view.findViewById(R.id.tv_comment_detail);
            tv_comment_time = (TextView) view.findViewById(R.id.tv_comment_time);
            tv_adapter_goodNums = (TextView) view.findViewById(R.id.tv_adapter_goodNums);
            tv_adapter_comment = (TextView) view.findViewById(R.id.tv_adapter_comment);
        }
    }
    public interface OnImageClick{
        void OnImageClick(View view, int position);
    }

    public interface OnUserClick{
        void OnUserClick(View view, int position);
    }
    private CoopCardGridAdapter.OnImageClick mOnItemClickListener;
    private CoopCardGridAdapter.OnUserClick mOnUserClickListener;

    public void setOnImageClick(CoopCardGridAdapter.OnImageClick onClickListener){
        this.mOnItemClickListener = onClickListener;
    }

    public void setOnUserClick(CoopCardGridAdapter.OnUserClick onClickListener) {
        this.mOnUserClickListener = onClickListener;
    }
}
