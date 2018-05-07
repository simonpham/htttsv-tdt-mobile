package com.simonvn.tdtu.student.adapters.email;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.models.email.EmailAttachment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bichan on 9/21/17.
 */

public class EmailAttachmentAdapter extends RecyclerView.Adapter {
    public List<EmailAttachment> lists;

    public EmailAttachmentAdapter(){
        lists = new ArrayList<>();
    }

    public void setLists(ArrayList<EmailAttachment> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_email_attachment, parent, false);
        viewHolder = new EmailAttachmentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final EmailAttachment emailAttachment = lists.get(position);
        EmailAttachmentViewHolder emailAttachmentViewHolder = (EmailAttachmentViewHolder) holder;
        emailAttachmentViewHolder.tvName.setText(emailAttachment.getName());
        emailAttachmentViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClick != null)
                    onItemClick.onClick(emailAttachment, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private class EmailAttachmentViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public MaterialRippleLayout layout;

        public EmailAttachmentViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    public interface OnItemClick{
        void onClick(EmailAttachment emailAttachment, int position);
    }

    public OnItemClick onItemClick;
}
