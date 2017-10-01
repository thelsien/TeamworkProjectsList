package com.thelsien.teamworkprojectslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thelsien.teamworkprojectslist.projectdetails.ProjectDetailsActivity;
import com.thelsien.teamworkprojectslist.projectdetails.ProjectDetailsFragment;
import com.thelsien.teamworkprojectslist.projectslist.adapters.ProjectsListAdapter;

public class MainActivity extends AppCompatActivity implements ProjectsListAdapter.ProjectClickedListener {

    private static final String PROJECT_DETAILS_FRAGMENT_TAG = "fragment_project_details";

    public static boolean mIsTabledMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fl_wrapper_fragment_projects_details) != null) {
            mIsTabledMode = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_wrapper_fragment_projects_details, new ProjectDetailsFragment(), PROJECT_DETAILS_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mIsTabledMode = false;
        }
    }

    @Override
    public void onProjectClicked(String projectId, String projectName) {
        if (mIsTabledMode) {
            ProjectDetailsFragment detailsFragment = new ProjectDetailsFragment();
            Bundle args = new Bundle();

            args.putString(ProjectDetailsFragment.PROJECT_ID_KEY, projectId);
            detailsFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_wrapper_fragment_projects_details, detailsFragment, PROJECT_DETAILS_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, ProjectDetailsActivity.class);
            intent.putExtra(ProjectDetailsFragment.PROJECT_ID_KEY, projectId);
            intent.putExtra(ProjectDetailsActivity.PROJECT_NAME_KEY, projectName);
            startActivity(intent);
        }
    }
}
