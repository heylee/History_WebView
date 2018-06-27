package com.heylee.webview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SimpleHeaderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM_VERTICAL = 1;
    public static final int VIEW_TYPE_ITEM_HORIZON = 2;
    public static final int VIEW_TYPE_ITEM_HORIZON_FULL = 3;

    public static final int VIEW_TYPE_FOOTER = 4;

    private LayoutInflater mInflater;
    ArrayList<String> mItems;
    View.OnClickListener mOnClickListener;

    public void setData(ArrayList<String> data) {
        mItems = data;
    }

    public SimpleHeaderRecyclerAdapter(Context context, ArrayList<String> items,View.OnClickListener onClickListener) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
        mOnClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        if (mItems != null) {
            return mItems.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return VIEW_TYPE_ITEM_VERTICAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new historyHolder(mInflater.inflate(R.layout.history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof historyHolder) {
            Log.d("heylee", "position " + position);
            final String url = mItems.get(position);
            ((historyHolder) viewHolder).tvPosition.setText(position +". ");
            ((historyHolder) viewHolder).tvHistoryUrl.setText(url);
        }
    }

    private class historyHolder extends RecyclerView.ViewHolder {
        TextView tvPosition;
        TextView tvHistoryUrl;

        public historyHolder(View view) {
            super(view);
            tvPosition = view.findViewById(R.id.h_position);
            tvHistoryUrl = view.findViewById(R.id.history_url);
            if(mOnClickListener != null) {
                tvHistoryUrl.setOnClickListener(mOnClickListener);
            }
        }
    }
}