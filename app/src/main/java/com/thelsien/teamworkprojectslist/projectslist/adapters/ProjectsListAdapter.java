package com.thelsien.teamworkprojectslist.projectslist.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thelsien.teamworkprojectslist.R;
import com.thelsien.teamworkprojectslist.projectdetails.ProjectDetailsActivity;
import com.thelsien.teamworkprojectslist.projectdetails.ProjectDetailsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public void onBindViewHolder(final ProjectsListViewHolder holder, int position) {
        holder.projectNameView.setText(mProjects.optJSONObject(position).optString("name"));
        holder.projectNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject project = mProjects.optJSONObject(holder.getAdapterPosition());
                Intent intent = new Intent(view.getContext(), ProjectDetailsActivity.class);
                intent.putExtra(ProjectDetailsFragment.PROJECT_ID_KEY, project.optString("id"));
                intent.putExtra(ProjectDetailsActivity.PROJECT_NAME_KEY, project.optString("name"));
                view.getContext().startActivity(intent);
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
}