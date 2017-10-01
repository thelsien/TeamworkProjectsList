package com.thelsien.teamworkprojectslist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import java.util.ArrayList;
import java.util.List;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {

    private static ConnectivityBroadcastReceiver sInstance;
    private static List<NetworkListener> mListeners = new ArrayList<>();

    public static void registerListener(Context context, NetworkListener listener) {
        if (sInstance == null) {
            sInstance = new ConnectivityBroadcastReceiver();

            IntentFilter receiveIntentFilter = new IntentFilter();
            receiveIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

            context.getApplicationContext().registerReceiver(sInstance, receiveIntentFilter);
        }

        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public static void unRegisterListener(Context context, NetworkListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
            if (mListeners.isEmpty()) {
                context.getApplicationContext().unregisterReceiver(sInstance);
                sInstance = null;
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            return;
        }

        boolean isConnected = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

        for (NetworkListener listener : mListeners) {
            listener.onNetworkConnectivityChanged(isConnected);
        }
    }

    public interface NetworkListener {
        void onNetworkConnectivityChanged(boolean isConnected);
    }
}
