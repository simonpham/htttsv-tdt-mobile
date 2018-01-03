package com.simonvn.tdtu.student.adapters.cnsv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.models.cnsv.ItemCnsvResult;

import java.util.ArrayList;

/**
 * Created by bichan on 9/25/17.
 */

public class CnsvResultAdapter extends RecyclerView.Adapter{
    public ArrayList<ItemCnsvResult> lists;

    public CnsvResultAdapter(){
        lists = new ArrayList<>();
    }

    public void addItem(ItemCnsvResult itemCnsvResult){
        if(itemCnsvResult == null)
            return;

        lists.add(itemCnsvResult);
        notifyDataSetChanged();
    }

    public void clear(){
        lists.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_cnsv, parent, false);
        viewHolder = new CnsvResultViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemCnsvResult item = lists.get(position);
        CnsvResultViewHolder CnsvResultViewHolder = (CnsvResultViewHolder) holder;
        CnsvResultViewHolder.tvType.setText(item.getType());
        CnsvResultViewHolder.tvHk.setText(item.getHk());
        CnsvResultViewHolder.tvDateRequest.setText(item.getDateRequest());
        CnsvResultViewHolder.tvDateResponse.setText(item.getDateResponse());
        CnsvResultViewHolder.tvStatus.setText(item.getStatus());
        CnsvResultViewHolder.tvNote.setText(item.getNote());
        CnsvResultViewHolder.tvId.setText(item.getId());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private class CnsvResultViewHolder extends RecyclerView.ViewHolder{
        public TextView tvType, tvId, tvHk, tvDateRequest, tvDateResponse, tvStatus, tvNote;

        public CnsvResultViewHolder(View itemView) {
            super(itemView);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvHk = (TextView) itemView.findViewById(R.id.tvHk);
            tvDateRequest = (TextView) itemView.findViewById(R.id.tvDateRequest);
            tvDateResponse = (TextView) itemView.findViewById(R.id.tvDateResponse);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvNote = (TextView) itemView.findViewById(R.id.tvNote);
            tvId = (TextView) itemView.findViewById(R.id.tvId);
        }
    }
}
