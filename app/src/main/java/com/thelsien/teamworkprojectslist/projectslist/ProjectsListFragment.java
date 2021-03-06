package com.thelsien.teamworkprojectslist.projectslist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thelsien.teamworkprojectslist.ConnectivityBroadcastReceiver;
import com.thelsien.teamworkprojectslist.NetworkUtils;
import com.thelsien.teamworkprojectslist.R;
import com.thelsien.teamworkprojectslist.projectslist.adapters.ProjectsListAdapter;
import com.thelsien.teamworkprojectslist.projectslist.background.ProjectsListAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

public class ProjectsListFragment extends Fragment implements ProjectsListAsyncTask.ProjectsListListener, ConnectivityBroadcastReceiver.NetworkListener {

    public static final String TAG = ProjectsListFragment.class.getSimpleName();

    private static final String PROJECTS_LIST_KEY = "projects_list";
    private static final String ADAPTER_SELECTED_POSITION_KEY = "selected_adapter_position";

    private JSONArray mProjectsList;
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private ProgressBar mLoadingProgressBar;

    private ProjectsListAdapter mAdapter;
    private int mSelectedAdapterPosition = -1;

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
        outState.putString(PROJECTS_LIST_KEY, mProjectsList.toString());
        outState.putInt(ADAPTER_SELECTED_POSITION_KEY, mAdapter.getSelectedItemPosition());

        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_projects_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.lv_projects);
        mLoadingProgressBar = view.findViewById(R.id.pb_loading);
        mErrorTextView = view.findViewById(R.id.tv_error);

        if (savedInstanceState == null) {
            if (NetworkUtils.isNetworkAvailable(getContext())) {
                new ProjectsListAsyncTask(this).execute();
            } else {
                mLoadingProgressBar.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.VISIBLE);

                mErrorTextView.setText(getString(R.string.error_no_network_available));
            }
        } else {
            try {
                mProjectsList = new JSONArray(savedInstanceState.getString(PROJECTS_LIST_KEY));
                mSelectedAdapterPosition = savedInstanceState.getInt(ADAPTER_SELECTED_POSITION_KEY, -1);

                populateList();
            } catch (JSONException e) {
                mLoadingProgressBar.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.VISIBLE);

                mErrorTextView.setText(getString(R.string.error_cannot_parse_saved_data));
                e.printStackTrace();
            }
        }
    }

    private void populateList() {
        mLoadingProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mProjectsList.length() != 0) {
            mAdapter = new ProjectsListAdapter((ProjectsListAdapter.ProjectClickedListener) getActivity(), mProjectsList);
            mAdapter.setSelectedItemPosition(mSelectedAdapterPosition);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.VISIBLE);

            mErrorTextView.setText(R.string.empty_list);
        }
    }

    @Override
    public void onProjectsLoadSuccess(JSONArray projects) {
        mProjectsList = projects;

        Log.d(TAG, mProjectsList.toString());

        populateList();
    }

    @Override
    public void onProjectsLoadFailed(@StringRes int messageId) {
        mLoadingProgressBar.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);

        mErrorTextView.setText(getString(messageId));

        Toast.makeText(getContext(), R.string.error_general, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNetworkConnectivityChanged(boolean isConnected) {
        if (isConnected && mProjectsList == null) {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            mErrorTextView.setVisibility(View.GONE);

            new ProjectsListAsyncTask(this).execute();
        }
    }
}
