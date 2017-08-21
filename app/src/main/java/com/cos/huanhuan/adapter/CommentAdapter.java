package com.cos.huanhuan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.activitys.CooperateDetailActivity;
import com.cos.huanhuan.model.Comment;
import com.cos.huanhuan.utils.AppStringUtils;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.FastBlur;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.PicassoUtils;
import com.cos.huanhuan.utils.ViewUtils;
import com.cos.huanhuan.views.CircleImageView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/20.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Comment> listComments;
    private Context context;
    private int userId;
    private Handler handler;
    private Boolean isExchange;
    public CommentAdapter(Context context, List<Comment> listComments,int userId,Boolean isExchange) {
        this.listComments = listComments;
        this.context = context;
        this.userId = userId;
        this.isExchange = isExchange;
        handler=new MyHandler();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据item类别加载不同ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_comment, parent,false);//这个布局就是一个imageview用来显示图片展示数据
        CommentAdapter.MyViewHolder holder = new CommentAdapter.MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        PicassoUtils.getinstance().LoadImage(context, listComments.get(position).getPortrait(), ((CommentAdapter.MyViewHolder) holder).cim_adapter_comment, R.mipmap.public_placehold, R.mipmap.public_placehold, PicassoUtils.PICASSO_BITMAP_SHOW_NORMAL_TYPE, 0);
        ((CommentAdapter.MyViewHolder) holder).tv_comment_name.setText(listComments.get(position).getNickname());
        ((CommentAdapter.MyViewHolder) holder).tv_comment_time.setText(listComments.get(position).getAddTime());
        int goodNums = listComments.get(position).getLikeNum();
        if(goodNums > 0){
            if(goodNums > 999){
                ((CommentAdapter.MyViewHolder) holder).tv_adapter_goodNums.setText("999+");
            }else{
                ((CommentAdapter.MyViewHolder) holder).tv_adapter_goodNums.setText(String.valueOf(goodNums));
            }
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
        ((CommentAdapter.MyViewHolder) holder).iv_adapter_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpRequest.goodComments(String.valueOf(listComments.get(position).getId()), String.valueOf(userId), new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        AppToastMgr.shortToast(context,"请求失败！");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            if (null != response.cacheResponse()) {
                                String str = response.cacheResponse().toString();
                            } else {
                                try {
                                    String str1 = response.body().string();
                                    JSONObject jsonObject = new JSONObject(str1);
                                    Boolean success = jsonObject.getBoolean("success");
                                    if(success){
                                        if(listComments.get(position).getLike()){
                                            Message message=new Message();
                                            handler.sendMessage(message);//发送message信息
                                            message.what=2;//标志是哪个线程传数据
                                        }else{
                                            listComments.get(position).setLike(true);
                                            int likeNums = listComments.get(position).getLikeNum() + 1;
                                            listComments.get(position).setLikeNum(likeNums);
                                        }
                                        Message message=new Message();
                                        handler.sendMessage(message);//发送message信息
                                        message.what=1;//标志是哪个线程传数据
                                    }else{
                                        String errorMsg = jsonObject.getString("errorMsg");
                                        AppToastMgr.shortToast(context,"修改失败！原因：" + errorMsg);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String str = response.networkResponse().toString();
                                Log.i("wangshu3", "network---" + str);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },isExchange);
            }
        });

        ((CommentAdapter.MyViewHolder) holder).tv_adapter_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnReplayClickListener.OnReplayClick(((CommentAdapter.MyViewHolder) holder).tv_adapter_comment,position);
            }
        });
        //回复人id不为0并且回复文字不为空
        if(listComments.get(position).getReplyUserId() != 0){
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
    public interface OnReplayClick{
        void OnReplayClick(View view, int position);
    }

    private CommentAdapter.OnReplayClick mOnReplayClickListener;

    public void setOnReplayClick (CommentAdapter.OnReplayClick onClickListener){
        this.mOnReplayClickListener = onClickListener;
    }

    class MyHandler extends Handler
    {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what==1)
            {
                CommentAdapter.this.notifyDataSetChanged();
            }else if(msg.what == 2){
                AppToastMgr.shortToast(context,"您已经点过赞了！" );
            }
        }
    }
}
