package com.simonvn.tdtu.student.fragments.trangchu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.actitities.cnsv.CnsvActivity;
import com.simonvn.tdtu.student.actitities.diem.DiemActivity;
import com.simonvn.tdtu.student.actitities.email.EmailActivity;
import com.simonvn.tdtu.student.actitities.email.EmailNewActivity;
import com.simonvn.tdtu.student.actitities.hdpt.HdptActivity;
import com.simonvn.tdtu.student.actitities.hocphi.HocphiActivity;
import com.simonvn.tdtu.student.actitities.lichthi.LichThiActivity;
import com.simonvn.tdtu.student.actitities.ndtt.NdttActivity;
import com.simonvn.tdtu.student.actitities.sakai.SakaiActivity;
import com.simonvn.tdtu.student.actitities.thongbao.ThongbaoActivity;
import com.simonvn.tdtu.student.actitities.tkb.TkbActivity;
import com.simonvn.tdtu.student.adapters.trangchu.TrangchuMenuRecyclerViewAdapter;
import com.simonvn.tdtu.student.models.trangchu.TrangchuMenuItem;
import com.simonvn.tdtu.student.utils.Tag;

import java.util.ArrayList;


public class TrangchuMenuFragment extends Fragment{
    private View inflatedView = null;
    private RecyclerView recyclerView;
    private TrangchuMenuRecyclerViewAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private ArrayList<TrangchuMenuItem> lists;
    private int column;
    public void khoiTao(){
        lists = new ArrayList<TrangchuMenuItem>();
    }
    public void anhXa(){
        khoiTao();

        if(getActivity().getResources().getConfiguration().orientation == 2){
            column = 3;
        }else{
            column = 3;
        }

        recyclerView = inflatedView.findViewById(R.id.recyclerview);
        adapter = new TrangchuMenuRecyclerViewAdapter(inflatedView.getContext(), lists);
        manager = new StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL);
        manager.setSpanCount(column);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }
    public TrangchuMenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView =  inflater.inflate(R.layout.fragment_trangchu_menu, container, false);
        anhXa();
        addMenu();
        adapter.setOnItemClickListener(new TrangchuMenuRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (lists.get(position).getTag()){
                    case Tag.TAG_MENU_THONGBAO:
                        Intent thongBao = new Intent(getActivity(), ThongbaoActivity.class);
                        startActivity(thongBao);
                        break;
                    case Tag.TAG_MENU_DIEM:
                        Intent diem = new Intent(getActivity(), DiemActivity.class);
                        startActivity(diem);
                        break;
                    case Tag.TAG_MENU_HOCPHI:
                        Intent hocPhi = new Intent(getActivity(), HocphiActivity.class);
                        startActivity(hocPhi);
                        break;
                    case Tag.TAG_MENU_TKB:
                        Intent tkb = new Intent(getActivity(), TkbActivity.class);
                        startActivity(tkb);
                        break;
                    case Tag.TAG_MENU_EMAIL:
                        Intent email = new Intent(getActivity(), EmailActivity.class);
                        startActivity(email);
                        break;
                    case Tag.TAG_MENU_HDPT:
                        Intent hdpt = new Intent(getActivity(), HdptActivity.class);
                        startActivity(hdpt);
                        break;
                    case Tag.TAG_MENU_LICHTHI:
                        Intent lichThi = new Intent(getActivity(), LichThiActivity.class);
                        startActivity(lichThi);
                        break;
                    case Tag.TAG_MENU_NDTT:
                        Intent ndtt = new Intent(getActivity(), NdttActivity.class);
                        startActivity(ndtt);
                        break;
                    case Tag.TAG_MENU_CNSV:
                        Intent cnsv = new Intent(getActivity(), CnsvActivity.class);
                        startActivity(cnsv);
                        break;
                    case Tag.TAG_MENU_SAKAI:
                        Intent sakai = new Intent(getActivity(), SakaiActivity.class);
                        startActivity(sakai);
                        break;
                    case Tag.TAG_MENU_BUG:
                        Intent emailBug = new Intent(getActivity(), EmailNewActivity.class);
                        emailBug.putExtra(EmailNewActivity.EXTRA_BUG, true);
                        emailBug.putExtra(EmailNewActivity.EXTRA_TO, "51702071@student.tdt.edu.vn");
                        emailBug.putExtra(EmailNewActivity.EXTRA_SUBJECT, "Báo lỗi");
                        startActivity(emailBug);
                        break;
                }
            }
        });

        return inflatedView;
    }

    private void addMenu(){
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_THONGBAO,getString(R.string.m_news),getString(R.string.m_news_summary), R.drawable.icon_menu_0, "#689F39"));
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_EMAIL,getString(R.string.m_email),getString(R.string.m_email_summary), R.drawable.icon_menu_1, "#8CC34B"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_TKB,getString(R.string.m_schedule),getString(R.string.m_schedule_summary), R.drawable.icon_menu_2, "#F44337"));
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_LICHTHI,getString(R.string.m_examschedule),getString(R.string.m_examschedule_summary), R.drawable.icon_menu_3, "#FF9801"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_DIEM,getString(R.string.m_grades),getString(R.string.m_grades_summary), R.drawable.icon_menu_4, "#03A9F4"));
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_HDPT,getString(R.string.m_eaa),getString(R.string.m_eaa_summary), R.drawable.icon_menu_5, "#3F51B5"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_HOCPHI,getString(R.string.m_tuitionfee),getString(R.string.m_tuitionfee_summary), R.drawable.icon_menu_7, "#9C37CB"));
        //lists.add(new TrangchuMenuItem(Tag.TAG_MENU_THONGTIN,getString(R.string.m_sinfo),"Thông tin hồ sơ Sinh viên của bạn", R.drawable.icon_menu_6, "#AA2E85"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_SAKAI,getString(R.string.m_sakai),getString(R.string.m_sakai_summary), R.drawable.icon_menu_11, "#9C37CB"));


        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_CNSV,getString(R.string.m_cs),"chứng nhận sinh viên", R.drawable.icon_menu_8, "#9C37CB"));
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_NDTT,getString(R.string.m_onlines),"nộp đơn trực tuyến", R.drawable.icon_menu_9, "#9C37CB"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_BUG,getString(R.string.m_bugreport),getString(R.string.m_bugreport_summary), R.drawable.icon_menu_12, "#9C37CB"));

        adapter.notifyDataSetChanged();
    }

}
