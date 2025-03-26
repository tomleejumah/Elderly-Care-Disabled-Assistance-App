package com.example.elderCare.app.ui.adapters;


import androidx.recyclerview.widget.RecyclerView;

public interface IDragListener {

    /**
     * Called view is dragging.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}