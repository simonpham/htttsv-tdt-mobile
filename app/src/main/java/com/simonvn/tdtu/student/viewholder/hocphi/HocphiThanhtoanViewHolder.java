package com.simonvn.tdtu.student.viewholder.hocphi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.simonvn.tdtu.student.R;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiThanhtoanViewHolder extends RecyclerView.ViewHolder {
    public TextView line, title, id, money;
    public HocphiThanhtoanViewHolder(View itemView) {
        super(itemView);
        id = itemView.findViewById(R.id.id_lable);
        title = itemView.findViewById(R.id.ten_text);
        money = itemView.findViewById(R.id.money_text);
    }
}
