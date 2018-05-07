package com.simonvn.tdtu.student.adapters.diem;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.models.diem.DiemItem;
import com.simonvn.tdtu.student.utils.Util;

import java.util.List;

/**
 * Created by Bichan on 7/17/2016.
 */
public class DiemThonghopRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DiemItem> list;

    public DiemThonghopRecyclerViewAdapter(List<DiemItem> list){
        this.list = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.item_diem_tonghop, parent, false);
        RecyclerView.ViewHolder viewHolder = new DiemTonghopViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiemTonghopViewHolder diemTonghopViewHolder = (DiemTonghopViewHolder) holder;
        DiemItem diemItem = list.get(position);
        diemTonghopViewHolder.sTT.setText(Integer.toString(position));
        diemTonghopViewHolder.maMonHoc.setText(diemItem.getMonHocID());
        diemTonghopViewHolder.soTC.setText(diemItem.getSoTC());
        diemTonghopViewHolder.dTB.setText(diemItem.getdTB());
        diemTonghopViewHolder.monHoc.setText(diemItem.getTenMH());
        diemTonghopViewHolder.dTB.setTextColor(Color.parseColor(Util.xeLoaiDiemColor(diemItem.getdTB())));
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DiemTonghopViewHolder extends RecyclerView.ViewHolder{
        public TextView monHoc, sTT, maMonHoc, soTC, dTB;
        public DiemTonghopViewHolder(View itemView) {
            super(itemView);
            monHoc = itemView.findViewById(R.id.monhoc_text);
            sTT = itemView.findViewById(R.id.stt_text);
            maMonHoc = itemView.findViewById(R.id.mamon_text);
            soTC = itemView.findViewById(R.id.tc_text);
            dTB = itemView.findViewById(R.id.dtb_text);
        }
    }
}
