package com.thelsien.teamworkprojectslist.projectdetails.background;

import android.os.AsyncTask;
import android.support.annotation.StringRes;
import android.util.Log;

import com.thelsien.teamworkprojectslist.Base64Coder;
import com.thelsien.teamworkprojectslist.BuildConfig;
import com.thelsien.teamworkprojectslist.Config;
import com.thelsien.teamworkprojectslist.R;
import com.thelsien.teamworkprojectslist.projectdetails.ProjectDetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by frodo on 2017-09-29.
 */

public class ProjectDetailsAsyncTask extends AsyncTask<String, Void, Void> {
    public static final String TAG = ProjectDetailsAsyncTask.class.getSimpleName();

    private ProjectDetailsLoaderListener mListener;
    private JSONObject mData;
    private boolean mIsError = false;
    private @StringRes int mErrorMessageId;

    public ProjectDetailsAsyncTask(ProjectDetailsLoaderListener listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.d(TAG, String.format(Config.PROJECT_DETAIL, params[0]));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + Base64Coder.encodeString(BuildConfig.TeamworkAPIKey+":"))
                .url(Config.BASE_URL + String.format(Config.PROJECT_DETAIL, params[0]))
                .build();

        try {
            Response response = client.newCall(request).execute();
            mData = new JSONObject(response.body().string());
        } catch (IOException e) {
            //executing network call failed
            e.printStackTrace();
            mIsError = true;
            mErrorMessageId = R.string.error_projects_list_server_query;
        } catch (NullPointerException e) {
            //response body was empty, string() call threw this
            e.printStackTrace();
            mIsError = true;
            mErrorMessageId = R.string.error_projects_list_server_message_empty;
        } catch (JSONException e) {
            //parsing data failed
            e.printStackTrace();
            mIsError = true;
            mErrorMessageId = R.string.error_projects_list_parsing_data;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mIsError) {
            mListener.onProjectDetailsLoadFailed(mErrorMessageId);
            return;
        }

        Log.d(TAG, mData.toString());

        if (mData != null && mData.optString("STATUS").equals("OK")) {
            mListener.onProjectDetailsLoaded(mData.optJSONObject("project"));
            return;
        }

        mListener.onProjectDetailsLoadFailed(R.string.error_general);
    }

    public interface ProjectDetailsLoaderListener {
        void onProjectDetailsLoaded(JSONObject project);
        void onProjectDetailsLoadFailed(@StringRes int messageId);
    }
}
