package com.simonvn.tdtu.student.adapters.hdpt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.models.hdpt.HdptHoatdongItem;
import com.simonvn.tdtu.student.utils.ColorGenerator;
import com.simonvn.tdtu.student.views.widget.RoundedLetterView;

import java.util.List;

/**
 * Created by Bichan on 7/27/2016.
 */
public class HdptHoatdongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<HdptHoatdongItem> lists;
    ColorGenerator generator = ColorGenerator.TKB;
    public HdptHoatdongAdapter(List<HdptHoatdongItem> lists){
        this.lists = lists;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.item_hdpt_hoatdong, parent, false);
        RecyclerView.ViewHolder viewHolder = new HdptHoatdongViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HdptHoatdongViewHolder hdptHoatdongViewHolder = (HdptHoatdongViewHolder) holder;
        HdptHoatdongItem hdptHoatdongItem = lists.get(position);
        hdptHoatdongViewHolder.noiDung.setText(hdptHoatdongItem.getTenSuKien());
        hdptHoatdongViewHolder.thoiGian.setText(hdptHoatdongItem.getThoiGian());
        int color = generator.getColor(hdptHoatdongItem.getDiemRL());
        hdptHoatdongViewHolder.diem.setTitleText(hdptHoatdongItem.getDiemRL());
        hdptHoatdongViewHolder.diem.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class HdptHoatdongViewHolder extends RecyclerView.ViewHolder{
        public TextView  noiDung, thoiGian;
        public RoundedLetterView diem;
        public HdptHoatdongViewHolder(View itemView) {
            super(itemView);
            noiDung = itemView.findViewById(R.id.noidung_text);
            thoiGian = itemView.findViewById(R.id.thoigian_text);
            diem = itemView.findViewById(R.id.diem_text);
        }
    }
}
