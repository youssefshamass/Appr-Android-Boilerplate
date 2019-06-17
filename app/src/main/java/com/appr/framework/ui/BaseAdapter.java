package com.appr.framework.ui;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    //region Variables

    protected List mDataSource;
    protected ViewHolderClickListener mViewHolderClickListener;

    //endregion

    //region RecyclerView.Adapter members

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bindObject(mDataSource.get(position));

        if (mViewHolderClickListener != null)
            holder.itemView.setOnClickListener(v -> mViewHolderClickListener.onClick(holder.getDataSource()));
    }

    @Override
    public int getItemCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }

    //endregion

    //region Public members

    public void setDataSource(List dataSource) {
        mDataSource = dataSource;
        notifyDataSetChanged();
    }

    public void append(List dataSource) {
        int startPosition = mDataSource.size();

        mDataSource.addAll(dataSource);
        notifyItemRangeChanged(startPosition, dataSource.size());
    }

    public List getDataSource() {
        return mDataSource;
    }

    public void setViewHolderClickListener(ViewHolderClickListener viewHolderClickListener) {
        mViewHolderClickListener = viewHolderClickListener;
    }

    public void removeAt(int position) {
        mDataSource.remove(position);
        notifyItemRemoved(position);
    }

    public void updateAt(int position, Object object) {
        mDataSource.set(position, object);
        notifyItemChanged(position);
    }

    public void update(Object object) {
        if (mDataSource.contains(object)) {
            int index = mDataSource.indexOf(object);
            mDataSource.set(index, object);

            notifyItemChanged(index);
        }
    }

    //endregion

    //region ViewHolderClickListeners

    public interface ViewHolderClickListener {
        void onClick(Object datasource);
    }

    //endregion
}
