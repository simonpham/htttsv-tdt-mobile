package com.simonvn.tdtu.student.fragments.hdpt;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.actitities.OnChildSwipeRefreshListener;
import com.simonvn.tdtu.student.adapters.hdpt.HdptHoatdongAdapter;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.diem.Diem;
import com.simonvn.tdtu.student.models.hdpt.HdptHoatdongItem;
import com.simonvn.tdtu.student.models.hdpt.HdptItem;

import java.util.ArrayList;

import io.realm.Realm;


public class HdptHoatdongFragment extends Fragment {
    public static String EXTRA_IDHOCKY = "EXTRA_IDHOCKY";
    private View inflatedView = null;
    private RecyclerView recyclerView;
    private HdptHoatdongAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private ArrayList<HdptHoatdongItem> list;

    private String idHocKy;

    private Realm realm;
    private User user;
    private String userText, passText;
    private HdptItem hdptItem;

    private Diem diem;

    private SwipeRefreshLayout swipeContainer;
    private OnChildSwipeRefreshListener onChildSwipeRefreshListener;

    private void khoiTao(){
        list = new ArrayList<HdptHoatdongItem>();
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            idHocKy = bundle.getString(EXTRA_IDHOCKY);
        }

        hdptItem = realm.where(HdptItem.class)
                .equalTo("idHocKy", idHocKy)
                .findFirst();
    }
    private void anhXa(){
        khoiTao();
        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        adapter = new HdptHoatdongAdapter(list);
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        manager.setSpanCount(1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(onChildSwipeRefreshListener != null){
                    onChildSwipeRefreshListener.onChildSwipeRefreshListener();
                }
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnChildSwipeRefreshListener){
            onChildSwipeRefreshListener = (OnChildSwipeRefreshListener)context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_hdpt_hoatdong, container, false);
        anhXa();
        showHoatDong();
        return inflatedView;
    }

    private void showHoatDong(){
        if(hdptItem != null){
            for (HdptHoatdongItem e: hdptItem.getHdptHoatdongItems()){
                list.add(e);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
