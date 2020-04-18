package org.me.gcu.mpdcoursework;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<TrafficAccident> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyAdapter(Context context, List<TrafficAccident> data, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mClickListener = itemClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapterlayout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.recyclerTitle.setText(mData.get(position).title);

        if (mData.get(position).type != 0)
            holder.recyclerIcon.setText("■");
        else
            holder.recyclerIcon.setText("");


        if (mData.get(position).time < 8)
        {
            holder.recyclerIcon.setTextColor(Color.GREEN);
        }
        else if (mData.get(position).time > 7 && mData.get(position).time < 28)
        {
            holder.recyclerIcon.setTextColor(Color.YELLOW);
        }
        else if (mData.get(position).time > 27)
            holder.recyclerIcon.setTextColor(Color.RED);


        holder.recyclerDescription.setText(Html.fromHtml(mData.get(position).description + "<br>"));

        //Log.e("Dates", mData.get(position).date);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView recyclerTitle;
        TextView recyclerDescription;
        TextView recyclerIcon;

        ViewHolder(View itemView) {
            super(itemView);
            recyclerTitle = itemView.findViewById(R.id.recyclerTitle);
            recyclerDescription = itemView.findViewById(R.id.recyclerDescription);
            recyclerIcon = itemView.findViewById(R.id.recyclerIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    TrafficAccident getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void ClearText()
    {
    this.mData.clear();
    }

}
