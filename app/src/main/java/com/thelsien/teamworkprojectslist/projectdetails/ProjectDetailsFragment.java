package com.thelsien.teamworkprojectslist.projectdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thelsien.teamworkprojectslist.ConnectivityBroadcastReceiver;
import com.thelsien.teamworkprojectslist.NetworkUtils;
import com.thelsien.teamworkprojectslist.R;
import com.thelsien.teamworkprojectslist.projectdetails.background.ProjectDetailsAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ProjectDetailsFragment extends Fragment implements ProjectDetailsAsyncTask.ProjectDetailsLoaderListener, ConnectivityBroadcastReceiver.NetworkListener {
    public static final String TAG = ProjectDetailsFragment.class.getSimpleName();

    public static final String PROJECT_ID_KEY = "project_id";
    public static final String PROJECT_KEY = "project";

    private String mProjectId;
    private JSONObject mProject;
    private boolean mShouldWaitForNetworkConnectivity = true;

    private TextView mDescriptionTextView;
    private TextView mCompanyNameTextView;
    private TextView mCategoryTextView;
    private TextView mStartPageTextView;
    private TextView mOverviewStartPageTextView;
    private TextView mReplyByEmailTextView;
    private TextView mPrivacyTextView;
    private TextView mStatusTextView;
    private TextView mTaskViewTextView;
    private ImageView mLogoImageView;

    private ProgressBar mLoadingProgressBar;
    private ScrollView mContentScrollView;
    private TextView mErrorTextView;
    private TextView mAnnouncementTextView;
    private TextView mAnnouncementEnabledView;

    @Override
    public void onResume() {
        super.onResume();

        ConnectivityBroadcastReceiver.registerListener(getContext(), this);
    }

    @Override
    public void onPause() {
        super.onPause();

        ConnectivityBroadcastReceiver.unRegisterListener(getContext(), this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PROJECT_ID_KEY, mProjectId);
        outState.putString(PROJECT_KEY, mProject.toString());

        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mProjectId = getArguments().getString(PROJECT_ID_KEY);
        }

        return inflater.inflate(R.layout.fragment_project_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mDescriptionTextView = view.findViewById(R.id.tv_description);
        mCompanyNameTextView = view.findViewById(R.id.tv_company_name);
        mCategoryTextView = view.findViewById(R.id.tv_category);
        mStartPageTextView = view.findViewById(R.id.tv_start_page);
        mOverviewStartPageTextView = view.findViewById(R.id.tv_overview_start_page);
        mReplyByEmailTextView = view.findViewById(R.id.tv_reply_by_email);
        mPrivacyTextView = view.findViewById(R.id.tv_privacy);
        mStatusTextView = view.findViewById(R.id.tv_status);
        mTaskViewTextView = view.findViewById(R.id.tv_task_view);
        mAnnouncementTextView = view.findViewById(R.id.tv_announcement_text);
        mAnnouncementEnabledView = view.findViewById(R.id.tv_announcement_enabled);
        mLogoImageView = view.findViewById(R.id.iv_logo);

        mLoadingProgressBar = view.findViewById(R.id.pb_loading);
        mContentScrollView = view.findViewById(R.id.sv_project_details);
        mErrorTextView = view.findViewById(R.id.tv_error);

        if (savedInstanceState == null) {
            if (mProjectId == null) {
                mShouldWaitForNetworkConnectivity = false;
                mLoadingProgressBar.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.VISIBLE);

                mErrorTextView.setText(getString(R.string.project_details_empty));
            } else {
                if (NetworkUtils.isNetworkAvailable(getContext())) {
                    mShouldWaitForNetworkConnectivity = false;
                    new ProjectDetailsAsyncTask(this).execute(mProjectId);
                } else {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    mErrorTextView.setVisibility(View.VISIBLE);

                    mErrorTextView.setText(getString(R.string.error_no_network_available));
                }
            }
        } else {
            mShouldWaitForNetworkConnectivity = false;
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
        mDescriptionTextView.setText(mProject.optString("description", getString(R.string.project_description_empty)));
        mCompanyNameTextView.setText(mProject.optJSONObject("company").optString("name", getString(R.string.project_no_company)));
        mStartPageTextView.setText(mProject.optString("start-page", getString(R.string.project_startpage_default)));
        mOverviewStartPageTextView.setText(mProject.optString("overview-start-page", getString(R.string.project_overview_startpage_default)));
        mReplyByEmailTextView.setText(mProject.optString("replyByEmailEnabled", getString(R.string.project_reply_by_email_default)));
        mPrivacyTextView.setText(mProject.optString("defaultPrivacy", getString(R.string.project_privacy_default)));
        mStatusTextView.setText(mProject.optString("status", getString(R.string.project_status_default)));
        mTaskViewTextView.setText(mProject.optString("tasks-start-page", getString(R.string.project_task_view_default)));
        mAnnouncementTextView.setText(mProject.optString("announcement", ""));
        mAnnouncementEnabledView.setText(mProject.optString("show-announcement", getString(R.string.project_announcement_enabled_default)));

        String category = mProject.optJSONObject("category").optString("name", getString(R.string.project_no_category));
        if (category.equals("")) {
            category = getString(R.string.project_no_category);
        }
        mCategoryTextView.setText(category);

        Glide.with(this)
                .load(mProject.optString("logo"))
                .apply(new RequestOptions()
                        .fallback(R.drawable.default_img)
                        .placeholder(R.drawable.default_img))
                .into(mLogoImageView);
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

    @Override
    public void onNetworkConnectivityChanged(boolean isConnected) {
        if (mShouldWaitForNetworkConnectivity && isConnected && mProject == null) {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.GONE);

            new ProjectDetailsAsyncTask(this).execute(mProjectId);
        }
    }
}
