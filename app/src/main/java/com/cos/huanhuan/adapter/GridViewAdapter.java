package com.cos.huanhuan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.RechargeMoney;

import java.util.List;

/**
 * Created by Administrator on 2017/8/29.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private List<RechargeMoney> listRecharge;

    public GridViewAdapter(Context context, List<RechargeMoney> listRecharge) {
        this.context = context;
        this.listRecharge = listRecharge;
    }

    @Override
    public int getCount() {
        return listRecharge.size();
    }

    @Override
    public Object getItem(int i) {
        return listRecharge.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView ==null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_recharge, null);
            viewHolder = new ViewHolder();
            viewHolder.ll_adapter_recharge = (LinearLayout) convertView.findViewById(R.id.ll_adapter_recharge);
            viewHolder.tv_adapter_recharge = (TextView) convertView.findViewById(R.id.tv_adapter_recharge);
            convertView.setTag(viewHolder);//绑定ViewHolder对象
        }else{
            viewHolder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
        if(listRecharge.get(position).getClick()){
            viewHolder.ll_adapter_recharge.setBackground(context.getResources().getDrawable(R.drawable.btn_money_selected));
        }else{
            viewHolder.ll_adapter_recharge.setBackground(context.getResources().getDrawable(R.drawable.btn_money));
        }
        viewHolder.tv_adapter_recharge.setText(String.valueOf(listRecharge.get(position).getMoney()));
        return convertView;
    }
    class ViewHolder{
        private LinearLayout ll_adapter_recharge;
        private TextView tv_adapter_recharge;
    }
}
