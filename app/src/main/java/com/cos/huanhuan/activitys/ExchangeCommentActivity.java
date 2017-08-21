package com.cos.huanhuan.activitys;

import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.adapter.CommentAdapter;
import com.cos.huanhuan.model.Comment;
import com.cos.huanhuan.model.CommentDTO;
import com.cos.huanhuan.utils.AppManager;
import com.cos.huanhuan.utils.AppToastMgr;
import com.cos.huanhuan.utils.HttpRequest;
import com.cos.huanhuan.utils.JsonUtils;
import com.cos.huanhuan.utils.SoftHideKeyBoardUtil;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExchangeCommentActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager mLayoutManager;
    private EditText et_comment;
    private TextView tv_send_comment;

    private AppManager appManager;

    private List<Comment> listComment;
    private CommentAdapter commentAdapter;
    private int pageIndex = 1;
    private int pageSize = 8;
    private int userId;
    private int exchangeId;
    private int parentId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        setTitleBarColor(R.color.white);
        setLeftImageResource(R.mipmap.nav_back);
        setDividerColor(R.color.dividLineColor);
        setRightTextColor(R.color.titleBarTextColor);
        setTitleTextColor(R.color.titleBarTextColor);
        setTitle(this.getResources().getString(R.string.commentText));
        setBaseContentView(R.layout.activity_exchange_comment);
        userId = Integer.valueOf(getUserId());
        exchangeId = Integer.valueOf(getIntent().getExtras().getString("exchangeId"));
        appManager = AppManager.getAppManager();
        appManager.addActivity(this);
        SoftHideKeyBoardUtil.assistActivity(this);
        initView();
        leftButtonClick(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                appManager.finishActivity();
            }
        });
    }
    private void initView() {

        recyclerview = (RecyclerView) findViewById(R.id.grid_recycle_commentList_exchange);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_commentList_exchange);
        et_comment = (EditText) findViewById(R.id.et_comment_exchange);
        tv_send_comment = (TextView) findViewById(R.id.tv_send_comment_exchange);

        mLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        recyclerview.setLayoutManager(mLayoutManager);
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        listComment = new ArrayList<Comment>();
        commentAdapter = new CommentAdapter(this,listComment,userId,true);
        recyclerview.setAdapter(commentAdapter);

        tv_send_comment.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==commentAdapter.getItemCount()){
                    CommentDTO commentDto = new CommentDTO();
                    pageIndex  = pageIndex + 1;
                    commentDto.setPageIndex(pageIndex);
                    commentDto.setPageSize(pageSize);
                    commentDto.setUserId(userId);
                    commentDto.setExId(exchangeId);
                    HttpRequest.getExchangeCommentList(commentDto, new StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            AppToastMgr.shortToast(ExchangeCommentActivity.this,"请求失败！");
                        }

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Boolean success = jsonObject.getBoolean("success");
                                String errorMsg = jsonObject.getString("errorMsg");

                                if(success) {
                                    JSONObject obj =jsonObject.getJSONObject("data");
                                    JSONArray arr =obj.getJSONArray("data");
                                    for (int i = 0; i < arr.length(); i++) {
                                        Comment comment = JsonUtils.fromJson(arr.get(i).toString(), Comment.class);
                                        listComment.add(comment);
                                    }
                                    commentAdapter.notifyDataSetChanged();
                                }else{
                                    AppToastMgr.shortToast(ExchangeCommentActivity.this, " 请求失败！原因：" + errorMsg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();
            }
        });

        commentAdapter.setOnReplayClick(new CommentAdapter.OnReplayClick() {
            @Override
            public void OnReplayClick(View view, int position) {
                et_comment.requestFocus();
                InputMethodManager imm = (InputMethodManager) et_comment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                et_comment.setHint("回复@" + listComment.get(position).getNickname());
                parentId = listComment.get(position).getId();
            }
        });

        initData();
    }

    private void initData() {
        CommentDTO commentDto = new CommentDTO();
        pageIndex = 1;
        commentDto.setPageIndex(pageIndex);
        commentDto.setPageSize(pageSize);
        commentDto.setUserId(userId);
        commentDto.setExId(exchangeId);
        HttpRequest.getExchangeCommentList(commentDto, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                AppToastMgr.shortToast(ExchangeCommentActivity.this,"请求失败！");
            }

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if(success){
                        listComment.removeAll(listComment);
                        JSONObject obj =jsonObject.getJSONObject("data");
                        JSONArray arr =obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            Comment comment = JsonUtils.fromJson(arr.get(i).toString(), Comment.class);
                            listComment.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        AppToastMgr.shortToast(ExchangeCommentActivity.this, " 接口调用失败！原因：" + errorMsg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_send_comment_exchange:
                String commentMessage = et_comment.getText().toString();
                HttpRequest.publishExchangeComment(exchangeId, userId, commentMessage, parentId, new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        AppToastMgr.shortToast(ExchangeCommentActivity.this,"请求失败！");
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean success = jsonObject.getBoolean("success");
                            String errorMsg = jsonObject.getString("errorMsg");
                            if(success){
                                initData();
                                et_comment.setText("");
                                et_comment.setHint("我也评论一下吧");
                                parentId = -1;
                                recyclerview.smoothScrollToPosition(0);
                            }else{
                                AppToastMgr.shortToast(ExchangeCommentActivity.this, " 评论失败！原因：" + errorMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }
}
