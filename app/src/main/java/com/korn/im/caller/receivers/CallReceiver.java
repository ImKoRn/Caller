package com.korn.im.caller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.korn.im.caller.services.CallInfoService;

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";
    public static final String EXTRA_NUMBER = "number";

    private static boolean isServiceActive = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            startService(context, intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER));
        } else if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Log.i(TAG, "Ringin state");
            startService(context, intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
        } else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Log.i(TAG, "Offhok state");
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

    private void startService(Context context, String name) {
        if(!isServiceActive) {
            Intent serviceIntent = new Intent(context, CallInfoService.class);
            if(name != null) {
                Log.i(TAG, name);
                serviceIntent.putExtra(EXTRA_NUMBER, name);
            } else Log.i(TAG, "No number");
            context.startService(serviceIntent);
            isServiceActive = true;
        }
    }
}
