package com.thelsien.teamworkprojectslist.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thelsien.teamworkprojectslist.R;

import org.json.JSONArray;

/**
 * Created by frodo on 2017-09-29.
 */

public class ProjectsListAdapter extends RecyclerView.Adapter<ProjectsListAdapter.ProjectsListViewHolder> {

    private JSONArray mProjects;

    public ProjectsListAdapter(JSONArray projects) {
        super();

        mProjects = projects;
    }

    @Override
    public ProjectsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView listRow = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_projects, parent, false);

        return new ProjectsListViewHolder(listRow);
    }

    @Override
    public void onBindViewHolder(ProjectsListViewHolder holder, int position) {
        holder.projectNameView.setText(mProjects.optJSONObject(position).optString("name"));
    }

    @Override
    public int getItemCount() {
        return mProjects != null ? mProjects.length() : 0;
    }

    public class ProjectsListViewHolder extends RecyclerView.ViewHolder {

        public TextView projectNameView;

        public ProjectsListViewHolder(TextView itemView) {
            super(itemView);

            projectNameView = itemView;
        }
    }
}