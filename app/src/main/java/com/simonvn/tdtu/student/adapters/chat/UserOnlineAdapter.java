package com.simonvn.tdtu.student.adapters.chat;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.models.firebase.UserOnline;
import com.simonvn.tdtu.student.utils.ColorGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by bichan on 9/8/17.
 */

public class UserOnlineAdapter extends RecyclerView.Adapter{
    public ArrayList<UserOnline> lists;
    ColorGenerator generator = ColorGenerator.TKB;

    public UserOnlineAdapter(){
        lists = new ArrayList<>();
    }

    public void addItem(UserOnline userOnline){
        if(userOnline == null)
            return;
        lists.add(userOnline);
        Collections.sort(lists, new Comparator<UserOnline>() {
            @Override
            public int compare(UserOnline t0, UserOnline t1) {
                return (int)(t1.time - t0.time);
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.item_user_online, parent, false);
        viewHolder = new UserOnlineViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserOnline userOnline = lists.get(position);
        UserOnlineViewHolder userOnlineViewHolder = (UserOnlineViewHolder) holder;
        userOnlineViewHolder.tvMSSV.setText(userOnline.mssv);
        int color = generator.getColor(userOnline.mssv);
        GradientDrawable backgroundGradient = (GradientDrawable)userOnlineViewHolder.tvMSSV.getBackground();
        backgroundGradient.setColor(color);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private class UserOnlineViewHolder extends RecyclerView.ViewHolder{
        public TextView tvMSSV;
        public UserOnlineViewHolder(View itemView) {
            super(itemView);
            tvMSSV = (TextView) itemView.findViewById(R.id.tvMSSV);
        }
    }
}
