package com.cos.huanhuan.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cos.huanhuan.R;
import com.cos.huanhuan.model.AddressDTO;
import com.cos.huanhuan.model.AddressVO;

import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class AddressAdapter extends BaseAdapter {

    private Context context;
    private List<AddressVO> listAddress;

    public AddressAdapter(Context context, List<AddressVO> listAddress) {
        this.context = context;
        this.listAddress = listAddress;
    }

    @Override
    public int getCount() {
        return listAddress.size();
    }

    @Override
    public Object getItem(int i) {
        return listAddress.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if(convertView ==null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_address, null);
                viewHolder = new ViewHolder();
                viewHolder.address_person= (TextView) convertView.findViewById(R.id.tv_adapter_address_person_name);
                viewHolder.address_phone= (TextView) convertView.findViewById(R.id.tv_adapter_address_person_phone);
                viewHolder.address_detail= (TextView) convertView.findViewById(R.id.tv_address_detail);
                viewHolder.adapter_setDefault_text= (TextView) convertView.findViewById(R.id.tv_adapter_setDefault);
                viewHolder.address_edit= (TextView) convertView.findViewById(R.id.tv_address_edit);
                viewHolder.tv_address_delete= (TextView) convertView.findViewById(R.id.tv_address_delete);
                viewHolder.adapter_edit= (RelativeLayout) convertView.findViewById(R.id.rl_adapter_edit);
                viewHolder.adapter_setDefault= (ImageButton) convertView.findViewById(R.id.ib_adapter_address);
                viewHolder.adapter_address_line = (View) convertView.findViewById(R.id.adapter_address_line);
                convertView.setTag(viewHolder);//绑定ViewHolder对象
            }else{
                viewHolder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
            }
            viewHolder.address_person.setText(listAddress.get(position).getName());
            viewHolder.address_phone.setText(listAddress.get(position).getPhone());
            String address = listAddress.get(position).getProvince() + listAddress.get(position).getCity() + listAddress.get(position).getCounty() + listAddress.get(position).getAddress();
            if(listAddress.get(position).getDefault()){
                String commentDetail = "收货地址：" + address + "<font color='#FF6FA2'>【默认地址】</font>";
                viewHolder.address_detail.setText(Html.fromHtml(commentDetail));
                viewHolder.adapter_setDefault.setImageResource(R.mipmap.circle_ok);
                viewHolder.adapter_setDefault_text.setText("默认地址");
                viewHolder.adapter_setDefault_text.setTextColor(context.getResources().getColor(R.color.titleBarTextColor));
            }else{
                viewHolder.address_detail.setText("收货地址：" + address);
                viewHolder.adapter_setDefault.setImageResource(R.mipmap.circle);
                viewHolder.adapter_setDefault_text.setText("设为默认");
                viewHolder.adapter_setDefault_text.setTextColor(context.getResources().getColor(R.color.grey_text_desc));
            }
            if(listAddress.get(position).getManage()){
                viewHolder.adapter_edit.setVisibility(View.VISIBLE);
                viewHolder.adapter_address_line.setVisibility(View.VISIBLE);
            }else{
                viewHolder.adapter_edit.setVisibility(View.GONE);
                viewHolder.adapter_address_line.setVisibility(View.GONE);
            }
            final ImageButton imageButton = viewHolder.adapter_setDefault;
            viewHolder.adapter_setDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageButtonClickListener.imageButtonClick(imageButton,position);
                }
            });
            final TextView address_edit = viewHolder.address_edit;
            viewHolder.address_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editClickListener.editClick(address_edit,position);
                }
            });
            final TextView address_delete = viewHolder.tv_address_delete;
            viewHolder.tv_address_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteClickListener.deleteClick(address_delete,position);
                }
            });
            return convertView;
        }
        class ViewHolder
        {
            public TextView address_person;
            public TextView address_phone;
            public TextView address_detail;
            public TextView address_edit;
            public TextView tv_address_delete;
            public TextView adapter_setDefault_text;
            public RelativeLayout adapter_edit;
            public ImageButton adapter_setDefault;
            public View adapter_address_line;
        }

        public interface ImageButtonClick{
            void imageButtonClick(View view, int position);
        }
        public interface EditClick{
            void editClick(View view, int position);
        }
        public interface DeleteClick{
            void deleteClick(View view, int position);
        }
        public interface RlClickVisiable{
            void rlClickVisiable(View view, int position);
        }
        private ImageButtonClick imageButtonClickListener;
        private EditClick editClickListener;
        private DeleteClick deleteClickListener;
        private RlClickVisiable rlClickVisiableListener;
        public void setRlClickVisiableClick(RlClickVisiable listener){
            this.rlClickVisiableListener = listener;
        }
        public void setImageButtonClick(ImageButtonClick listener){
            this.imageButtonClickListener = listener;
        }
        public void setEditClick(EditClick listener){
            this.editClickListener = listener;
        }
        public void setDeleteClick(DeleteClick listener){
            this.deleteClickListener = listener;
        }
}
