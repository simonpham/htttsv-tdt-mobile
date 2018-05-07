package com.simonvn.tdtu.student.viewholder.hocphi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.simonvn.tdtu.student.R;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiMucViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView money;
    //public TextView dot;
    public HocphiMucViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.ten_text);
        money = itemView.findViewById(R.id.money_text);
        //dot = (TextView) itemView.findViewById(R.id.dot);
    }
}
