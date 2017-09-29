package com.thelsien.teamworkprojectslist.projectdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.thelsien.teamworkprojectslist.R;
import com.thelsien.teamworkprojectslist.projectdetails.background.ProjectDetailsAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ProjectDetailsFragment extends Fragment implements ProjectDetailsAsyncTask.ProjectDetailsLoaderListener {
    public static final String TAG = ProjectDetailsFragment.class.getSimpleName();

    public static final String PROJECT_ID_KEY = "project_id";
    public static final String PROJECT_KEY = "project";
    public static final String PROJECT_NAME_KEY = "project_name";

    private String mProjectId;
    private String mProjectName;
    private JSONObject mProject;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PROJECT_ID_KEY, mProjectId);
        outState.putString(PROJECT_NAME_KEY, mProjectName);
        outState.putString(PROJECT_KEY, mProject.toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mProjectId = getActivity().getIntent().getStringExtra(PROJECT_ID_KEY);
            mProjectName = getActivity().getIntent().getStringExtra(PROJECT_NAME_KEY);

            new ProjectDetailsAsyncTask(this).execute(mProjectId);
        } else {
            mProjectId = savedInstanceState.getString(PROJECT_ID_KEY);
            mProjectName = savedInstanceState.getString(PROJECT_NAME_KEY);

            try {
                mProject = new JSONObject(savedInstanceState.getString(PROJECT_KEY));

                fillWithContent();
            } catch (JSONException e) {
                //TODO handle error
                e.printStackTrace();
            }
        }
    }

    private void fillWithContent() {

    }

    @Override
    public void onProjectDetailsLoaded(JSONObject project) {
        mProject = project;

        fillWithContent();
    }

    @Override
    public void onProjectDetailsLoadFailed(int messageId) {

    }
}
