package com.thelsien.teamworkprojectslist.projectdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.thelsien.teamworkprojectslist.R;

public class ProjectDetailsActivity extends AppCompatActivity {

    public static final String PROJECT_NAME_KEY = "project_name";

    private String mProjectName;
    private String mProjectId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        if (savedInstanceState == null) {
            mProjectName = getIntent().getStringExtra(PROJECT_NAME_KEY);
            mProjectId = getIntent().getStringExtra(ProjectDetailsFragment.PROJECT_ID_KEY);

            addDetailsFragment();
        } else {
            mProjectName = savedInstanceState.getString(PROJECT_NAME_KEY);
            mProjectId = savedInstanceState.getString(ProjectDetailsFragment.PROJECT_ID_KEY);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(true);
        collapsingToolbarLayout.setTitle(getIntent().getStringExtra(PROJECT_NAME_KEY));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PROJECT_NAME_KEY, mProjectName);
        super.onSaveInstanceState(outState);
    }

    private void addDetailsFragment() {
        ProjectDetailsFragment detailsFragment = new ProjectDetailsFragment();
        Bundle args = new Bundle();

        args.putString(ProjectDetailsFragment.PROJECT_ID_KEY, mProjectId);
        detailsFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_wrapper_fragment_details, detailsFragment, "fragment_project_details")
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
