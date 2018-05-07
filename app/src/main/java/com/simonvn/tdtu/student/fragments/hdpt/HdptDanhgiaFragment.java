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
import com.simonvn.tdtu.student.adapters.hdpt.HdptDanhgiaAdapter;
import com.simonvn.tdtu.student.models.User;
import com.simonvn.tdtu.student.models.diem.Diem;
import com.simonvn.tdtu.student.models.hdpt.HdptDanhgiaDiemItem;
import com.simonvn.tdtu.student.models.hdpt.HdptDanhgiaItem;
import com.simonvn.tdtu.student.models.hdpt.HdptDanhgiaTitleItem;
import com.simonvn.tdtu.student.models.hdpt.HdptItem;
import com.simonvn.tdtu.student.models.hdpt.HdptTagDanhgiaItem;

import java.util.ArrayList;

import io.realm.Realm;


public class HdptDanhgiaFragment extends Fragment {

    public static String EXTRA_IDHOCKY = "EXTRA_IDHOCKY";
    private View inflatedView = null;
    private RecyclerView recyclerView;
    private HdptDanhgiaAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private ArrayList<Object> list;

    private String idHocKy;

    private Realm realm;
    private User user;
    private String userText, passText;
    private HdptItem hdptItem;

    private Diem diem;

    private SwipeRefreshLayout swipeContainer;
    private OnChildSwipeRefreshListener onChildSwipeRefreshListener;

    private void khoiTao(){
        list = new ArrayList<Object>();
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
        recyclerView = inflatedView.findViewById(R.id.recyclerview);
        adapter = new HdptDanhgiaAdapter(list);
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        manager.setSpanCount(1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        swipeContainer = inflatedView.findViewById(R.id.swipeContainer);
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
        this.inflatedView = inflater.inflate(R.layout.fragment_hdpt_danhgia, container, false);
        anhXa();
        showDanhgia();
        return inflatedView;
    }

    private void showDanhgia(){
        if(hdptItem != null){
            for(HdptTagDanhgiaItem e: hdptItem.getHdptTagDanhgiaItems()){
                list.add(new HdptDanhgiaTitleItem(e.getTitle()));
                for(HdptDanhgiaItem m: e.getHdptDanhgiaItems()){
                    list.add(m);
                }
            }
            list.add(new HdptDanhgiaDiemItem(hdptItem.getDiem()));
        }
    }

}
