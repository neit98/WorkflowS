package com.example.workflow_s.ui.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.workflow_s.R;
import com.example.workflow_s.model.Checklist;

import java.util.List;

/**
 * Workflow_S
 * Created by TinhPV on 2019-06-13
 * Copyright © 2019 TinhPV. All rights reserved
 **/


public class ChecklistProgressAdapter extends RecyclerView.Adapter<ChecklistProgressAdapter.ChecklistProgressViewHolder> {

    // Constants
    private final int MAX_ITEM_NUMBER = 5;

    // DataSource for RecyclerView
    private List<Checklist> mChecklists;

    // VIEW-HOLDER
    public class ChecklistProgressViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public ChecklistProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_checklist_name);
        }
    }

    public ChecklistProgressAdapter(List<Checklist> checklists) {
        mChecklists = checklists;
    }

    @NonNull
    @Override
    public ChecklistProgressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int layoutId = R.layout.recyclerview_item_checklist_progress;
        View view = layoutInflater.inflate(layoutId, viewGroup, false);
        ChecklistProgressViewHolder viewHolder = new ChecklistProgressViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistProgressViewHolder checklistProgressViewHolder, int i) {
        // TODO - MODIFY VIEWHOLDER'S CONTENT
        checklistProgressViewHolder.mTextView.setText("ABC_XYZ");
    }

    @Override
    public int getItemCount() {
        // TODO - MODIFY SIZE LIST
        int numberOfItems = mChecklists.size();
        //return numberOfItems > MAX_ITEM_NUMBER ? MAX_ITEM_NUMBER : numberOfItems;
        return 3;
    }
}
