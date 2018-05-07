package com.simonvn.tdtu.student.adapters.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.models.firebase.Chat;
import com.simonvn.tdtu.student.models.firebase.ChatDateShow;
import com.simonvn.tdtu.student.models.firebase.ChatShow;
import com.simonvn.tdtu.student.views.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by bichan on 8/29/17.
 */

public class ChatAdapter extends RecyclerView.Adapter {
    private Context mContext;

    private static final int TYPE_IN = 1;
    private static final int TYPE_IN_ADMIN = 4;
    private static final int TYPE_OUT = 2;
    private static final int TYPE_DATE = 3;

    private ArrayList<Object> lists;

    public ChatAdapter(Context mContext){
        lists = new ArrayList<>();
        this.mContext = mContext;
    }

    public void clear(){
        lists.clear();
        notifyDataSetChanged();
    }

    public void addItem(ChatShow chatShow){
        lists.add(chatShow);
        ChatDateShow chatDateShow = new ChatDateShow();
        chatDateShow.time = chatShow.time;
        lists.add(chatDateShow);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType){
            case TYPE_DATE:
                view = inflater.inflate(R.layout.item_chat_date, parent, false);
                viewHolder = new ChatDateViewHolder(view);
                break;
            case TYPE_IN:
                view = inflater.inflate(R.layout.item_chat_in, parent, false);
                viewHolder = new ChatViewHolder(view);
                break;
            case TYPE_IN_ADMIN:
                view = inflater.inflate(R.layout.item_chat_in_admin, parent, false);
                viewHolder = new ChatViewHolder(view);
                break;
            case TYPE_OUT:
                view = inflater.inflate(R.layout.item_chat_out, parent, false);
                viewHolder = new ChatViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();

        Chat preChat = position - (type == TYPE_DATE?3:2) >= 0 ? (ChatShow) lists.get(position - (type == TYPE_DATE?3:2)): null;
        Chat chat = (ChatShow) lists.get(position - (type == TYPE_DATE?1:0));
        boolean hideName = false;
        boolean hideTime = false;

        if(preChat != null && preChat.chatUser.mssv.equals(chat.chatUser.mssv)){
            hideName = true;
        }

        if(preChat != null && (chat.time - preChat.time) < 10 * 60 * 1000)
            hideTime = true;

        if(type == TYPE_DATE){
            ChatDateShow chatDateShow = (ChatDateShow) lists.get(position);
            ChatDateViewHolder chatDateViewHolder = (ChatDateViewHolder) holder;
            chatDateViewHolder.tvDate.setVisibility(hideTime?View.GONE:View.VISIBLE);
            chatDateViewHolder.tvDate.setReferenceTime(chatDateShow.time);
        }

        if(type == TYPE_IN || type == TYPE_OUT || type == TYPE_IN_ADMIN){
            ChatShow chatShow = (ChatShow) lists.get(position);
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            chatViewHolder.tvName.setText(chatShow.chatUser.name);
            chatViewHolder.tvBody.setText(chatShow.body);
            //if(type == TYPE_IN || type == TYPE_IN_ADMIN){
            chatViewHolder.imgAvatar.setVisibility(hideName?View.INVISIBLE:View.VISIBLE);
            if(!chatShow.chatUser.showAvatar){
                Picasso.with(mContext).load(R.drawable.user_empty).into(chatViewHolder.imgAvatar);
            }else{
                Picasso.with(mContext).load(chatShow.chatUser.avatar).into(chatViewHolder.imgAvatar);
            }
            if(type != TYPE_OUT){
                chatViewHolder.tvName.setVisibility(hideName?View.GONE:View.VISIBLE);
                chatViewHolder.dotOnline.setVisibility(!hideName&&chatShow.online?View.VISIBLE:View.GONE);
            }
            //}
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object o = lists.get(position);
        if(o instanceof ChatDateShow)
            return TYPE_DATE;
        if(o instanceof ChatShow){
            if(((ChatShow) o).type == 1){
                if(((ChatShow) o).chatUser.isAdmin)
                    return TYPE_IN_ADMIN;
                return TYPE_IN;
            }
            return TYPE_OUT;
        }
        return 0;
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView imgAvatar;
        public TextView tvName, tvBody;
        public View dotOnline;
        public ChatViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvBody = itemView.findViewById(R.id.tvBody);
            dotOnline = itemView.findViewById(R.id.dotOnline);
        }
    }

    private class ChatDateViewHolder extends RecyclerView.ViewHolder{
        public RelativeTimeTextView tvDate;
        public ChatDateViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
