package com.thelsien.teamworkprojectslist.projectslist.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thelsien.teamworkprojectslist.MainActivity;
import com.thelsien.teamworkprojectslist.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProjectsListAdapter extends RecyclerView.Adapter<ProjectsListAdapter.ProjectsListViewHolder> {

    private ProjectClickedListener mListener;
    private JSONArray mProjects;
    private int mSelectedPosition = -1;

    public ProjectsListAdapter(ProjectClickedListener listener, JSONArray projects) {
        super();

        mListener = listener;
        mProjects = projects;
    }

    @Override
    public ProjectsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView listRow = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_projects, parent, false);

        return new ProjectsListViewHolder(listRow);
    }

    @Override
    public void onBindViewHolder(final ProjectsListViewHolder holder, int position) {
        if (MainActivity.mIsTabledMode) {
            if (mSelectedPosition != -1 && mSelectedPosition == position) {
                holder.projectNameView.setBackgroundColor(holder.projectNameView.getResources().getColor(R.color.selected_list_item));
            } else {
                holder.projectNameView.setBackgroundColor(holder.projectNameView.getResources().getColor(android.R.color.transparent));
            }
        }

        holder.projectNameView.setText(mProjects.optJSONObject(position).optString("name"));
        holder.projectNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedPosition = holder.getAdapterPosition();

                JSONObject project = mProjects.optJSONObject(holder.getAdapterPosition());
                mListener.onProjectClicked(project.optString("id"), project.optString("name"));

                notifyDataSetChanged();
            }
        });
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

    public int getSelectedItemPosition() {
        return mSelectedPosition;
    }

    public void setSelectedItemPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public interface ProjectClickedListener {
        void onProjectClicked(String projectId, String projectName);
    }
}