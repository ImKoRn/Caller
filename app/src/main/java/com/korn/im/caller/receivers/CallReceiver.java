package com.korn.im.caller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.korn.im.caller.services.CallInfoService;

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";

    private static boolean isServiceActive = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING) ||
                state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Log.i(TAG, "Ringin, or offhok state");
            startService(context, intent);
        } else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Log.i(TAG, "Idle state");
            stopService(context);
        } else Log.i(TAG, "Unknown call state");
    }

    private void stopService(Context context) {
        isServiceActive = false;
        Intent serviceIntent = new Intent(context, CallInfoService.class);
        context.stopService(serviceIntent);
    }

    private void startService(Context context, Intent intent) {
        if(!isServiceActive) {
            Intent serviceIntent = new Intent(context, CallInfoService.class);
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(number != null) {
                Log.i(TAG, number);
                serviceIntent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, number);
            }
            context.startService(serviceIntent);
            isServiceActive = true;
        }
    }
}
