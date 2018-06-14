package com.example.weioule.imagecachcedemo;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * author weioule
 * Create on 2018/6/14.
 */
public class RecyAdapter extends RecyclerView.Adapter<RecyAdapter.RecylerViewHolder> {

    private List<String> mDatas;

    public RecyAdapter(List<String> datas) {
        this.mDatas = datas;
    }

    @Override
    public RecylerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecylerViewHolder holder = null;
        if (null == holder) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false);
            holder = new RecylerViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecylerViewHolder holder, int position) {
        ((AsyncImageView) holder.getView(R.id.list_imag)).downloadCache2memory(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public class RecylerViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> viewList;

        public RecylerViewHolder(View itemView) {
            super(itemView);
            viewList = new SparseArray<>();
        }

        public View getView(int viewId) {
            View view = viewList.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                viewList.put(viewId, view);
            }
            return view;
        }
    }
}
