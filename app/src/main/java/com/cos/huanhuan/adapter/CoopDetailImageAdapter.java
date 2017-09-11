package com.cos.huanhuan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.Image;
import com.cos.huanhuan.utils.AppBigDecimal;
import com.cos.huanhuan.utils.AppScreenMgr;
import com.cos.huanhuan.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */

public class CoopDetailImageAdapter extends BaseAdapter {
    private int screenWidth;
    private int imgHeight;
    private List<Image> listImgs;
    private Context context;
    public CoopDetailImageAdapter(Context context, List<Image> data) {
        this.listImgs = data;
        this.context = context;
        screenWidth = AppScreenMgr.getScreenWidth(context);
    }

    @Override
    public int getCount() {
        return listImgs.size();
    }

    @Override
    public Image getItem(int position) {
        return listImgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        convertView = LayoutInflater.from(context).inflate(R.layout.coop_detail_image, null);
        imageView = (ImageView) convertView.findViewById(R.id.imageView);
        convertView.setTag(imageView);
        if(getItem(position).getWidth() != 0 && getItem(position).getHeight() != 0) {
            imgHeight = new Double(AppBigDecimal.multiply(AppBigDecimal.divide(Double.valueOf(screenWidth), Double.valueOf(getItem(position).getWidth())), Double.valueOf(getItem(position).getHeight()), 0)).intValue();
            Picasso.with(context).load(getItem(position).getImgPath()).placeholder(R.mipmap.public_placehold).resize(screenWidth, imgHeight).into(imageView);
        }else{
            Picasso.with(context).load(getItem(position).getImgPath()).placeholder(R.mipmap.public_placehold).into(imageView);
        }
        return convertView;
    }
}

