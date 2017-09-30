package com.thelsien.teamworkprojectslist.projectdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.thelsien.teamworkprojectslist.R;
import com.thelsien.teamworkprojectslist.projectdetails.background.ProjectDetailsAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ProjectDetailsFragment extends Fragment implements ProjectDetailsAsyncTask.ProjectDetailsLoaderListener {
    public static final String TAG = ProjectDetailsFragment.class.getSimpleName();

    public static final String PROJECT_ID_KEY = "project_id";
    public static final String PROJECT_KEY = "project";

    private String mProjectId;
    private JSONObject mProject;

    private TextView mDescriptionTextView;
    private TextView mCompanyNameTextView;
    private ProgressBar mLoadingProgressBar;
    private ScrollView mContentScrollView;
    private TextView mErrorTextView;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PROJECT_ID_KEY, mProjectId);
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
        mDescriptionTextView = view.findViewById(R.id.tv_description);
        mCompanyNameTextView = view.findViewById(R.id.tv_company_name);
        mLoadingProgressBar = view.findViewById(R.id.pb_loading);
        mContentScrollView = view.findViewById(R.id.sv_project_details);
        mErrorTextView = view.findViewById(R.id.tv_error);

        if (savedInstanceState == null) {
            mProjectId = getActivity().getIntent().getStringExtra(PROJECT_ID_KEY);

            new ProjectDetailsAsyncTask(this).execute(mProjectId);
        } else {
            mLoadingProgressBar.setVisibility(View.GONE);
            mContentScrollView.setVisibility(View.VISIBLE);

            mProjectId = savedInstanceState.getString(PROJECT_ID_KEY);

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
        String description = mProject.optString("description", "");

        if (description.equals("")) {
            mDescriptionTextView.setTextColor(getResources().getColor(R.color.alt_text_color));
            description = getString(R.string.project_description_empty);
        }

        mDescriptionTextView.setText(description);

        mCompanyNameTextView.setText(mProject.optJSONObject("company").optString("name"));
    }

    @Override
    public void onProjectDetailsLoaded(JSONObject project) {
        mLoadingProgressBar.setVisibility(View.GONE);
        mContentScrollView.setVisibility(View.VISIBLE);

        mProject = project;
        fillWithContent();
    }

    @Override
    public void onProjectDetailsLoadFailed(int messageId) {
        mLoadingProgressBar.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);

        mErrorTextView.setText(messageId);
    }
}
