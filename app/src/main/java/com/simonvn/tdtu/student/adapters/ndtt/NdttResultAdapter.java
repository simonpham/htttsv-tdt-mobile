package com.simonvn.tdtu.student.adapters.ndtt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.models.ndtt.ItemNdttResult;

import java.util.ArrayList;

/**
 * Created by bichan on 9/25/17.
 */

public class NdttResultAdapter extends RecyclerView.Adapter {
    public ArrayList<ItemNdttResult> lists;

    public NdttResultAdapter(){
        lists = new ArrayList<>();
    }

    public void addItem(ItemNdttResult itemNdttResult){
        if(itemNdttResult == null)
            return;

        lists.add(itemNdttResult);
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
        View view = inflater.inflate(R.layout.item_ndtt, parent, false);
        viewHolder = new NdttResultViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemNdttResult item = lists.get(position);
        NdttResultViewHolder ndttResultViewHolder = (NdttResultViewHolder) holder;
        ndttResultViewHolder.tvType.setText(item.getType());
        ndttResultViewHolder.tvHk.setText(item.getHk());
        ndttResultViewHolder.tvDateRequest.setText(item.getDateRequest());
        ndttResultViewHolder.tvDateResponse.setText(item.getDateResponse());
        ndttResultViewHolder.tvStatus.setText(item.getStatus());
        ndttResultViewHolder.tvNote.setText(item.getNote());
        ndttResultViewHolder.tvId.setText(item.getId());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private class NdttResultViewHolder extends RecyclerView.ViewHolder{
        public TextView tvType, tvId, tvHk, tvDateRequest, tvDateResponse, tvStatus, tvNote;

        public NdttResultViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvType = itemView.findViewById(R.id.tvType);
            tvHk = itemView.findViewById(R.id.tvHk);
            tvDateRequest = itemView.findViewById(R.id.tvDateRequest);
            tvDateResponse = itemView.findViewById(R.id.tvDateResponse);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvNote = itemView.findViewById(R.id.tvNote);
        }
    }
}
