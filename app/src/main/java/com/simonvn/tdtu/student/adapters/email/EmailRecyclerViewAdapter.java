package com.simonvn.tdtu.student.adapters.email;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.simonvn.tdtu.student.R;
import com.simonvn.tdtu.student.models.email.EmailItem;

import java.util.List;

/**
 * Created by Bichan on 7/20/2016.
 */
public class EmailRecyclerViewAdapter extends RecyclerView.Adapter<EmailRecyclerViewAdapter.EmailViewHolder>{
    private List<EmailItem> lists;
    private Context mContext;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    public EmailRecyclerViewAdapter(Context mContext, List<EmailItem> lists){
        this.lists = lists;
        this.mContext = mContext;
    }
    @Override
    public EmailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        EmailViewHolder emailViewHolder = null;
        View view = null;
        switch (viewType){
            case R.layout.item_email:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_email, parent, false);
                break;
            case R.layout.item_email_new:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_email_new, parent, false);
                break;
        }
        emailViewHolder = new EmailViewHolder(view);
        return emailViewHolder;
    }

    @Override
    public void onBindViewHolder(EmailViewHolder holder, int position) {
        EmailItem get = lists.get(position);
        holder.tvDate.setReferenceTime(get.getmSentDate());
        holder.from.setText(get.getmFrom());
        holder.personal.setText(get.getmPersonal());
        holder.subject.setText(get.getmSubject());
        if(get.getEmailAttachments().size() > 0){
            holder.imgAttachment.setVisibility(View.VISIBLE);
        }else {
            holder.imgAttachment.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(lists.get(position).isNew()){
            return R.layout.item_email_new;
        }else {
            return R.layout.item_email;
        }
    }
    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class EmailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{
        public TextView from, personal, subject;
        public MaterialRippleLayout layout;
        public ImageView imgAttachment;
        public RelativeTimeTextView tvDate;
        public EmailViewHolder(View itemView) {
            super(itemView);
            from = itemView.findViewById(R.id.from_text);
            personal = itemView.findViewById(R.id.personal_text);
            subject = itemView.findViewById(R.id.subject_text);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgAttachment = itemView.findViewById(R.id.imgAttachment);
            layout = itemView.findViewById(R.id.layout);
            layout.setOnLongClickListener(this);
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
        @Override
        public boolean onLongClick(View v) {
            if(mItemLongClickListener != null){
                mItemLongClickListener.onItemLongClick(itemView, getPosition());
            }
            return true;
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public void setOnLongItemClickListener(final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }
}
