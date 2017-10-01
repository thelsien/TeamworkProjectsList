package com.thelsien.teamworkprojectslist.background;

import android.os.AsyncTask;
import android.support.annotation.StringRes;

import com.thelsien.teamworkprojectslist.Base64Coder;
import com.thelsien.teamworkprojectslist.BuildConfig;
import com.thelsien.teamworkprojectslist.Config;
import com.thelsien.teamworkprojectslist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProjectsListAsyncTask extends AsyncTask<Void, Void, Void> {

    public static final String TAG = ProjectsListAsyncTask.class.getSimpleName();

    private ProjectsListListener mListener;
    private JSONObject mData;
    private boolean mIsError = false;
    private @StringRes int mErrorMessageId;

    public ProjectsListAsyncTask(ProjectsListListener listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + Base64Coder.encodeString(BuildConfig.TeamworkAPIKey+":"))
                .url(Config.BASE_URL + Config.PROJECTS)
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
            mListener.onProjectsLoadFailed(mErrorMessageId);
            return;
        }

        if (mData != null && mData.optString("STATUS").equals("OK")) {
            mListener.onProjectsLoadSuccess(mData.optJSONArray("projects"));
            return;
        }

        mListener.onProjectsLoadFailed(R.string.error_general);
    }

    public interface ProjectsListListener {
        void onProjectsLoadSuccess(JSONArray projects);

        void onProjectsLoadFailed(@StringRes int messageId);
    }
}
