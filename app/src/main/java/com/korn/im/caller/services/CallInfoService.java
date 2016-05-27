package com.korn.im.caller.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.telephony.TelephonyManager;
import android.text.style.TtsSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.korn.im.caller.R;

import java.net.URL;

public class CallInfoService extends Service {

    private static final String PLACEHOLDER_TEXT_FOR_KNOWN_CALLER = "Caller shoes size 23, and his name ";
    private static final String PLACEHOLDER_TEXT_FOR_UNKNOWN_CALLER = "I don't now this people but his shoes size definitely 23";

    private LinearLayout layout;
    private WindowManager windowManager;
    private boolean isCollapsed = true;
    private TextView textView;
    private WindowManager.LayoutParams layoutParams;
    private ImageButton imageButton;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        layout = initLayout();

        layoutParams = initLayoutParams();

        textView = initTextView();

        imageButton = initButton();

        layout.addView(imageButton);
        layout.addView(textView);

        windowManager.addView(layout, layoutParams);
    }

    private ImageButton initButton() {
        int pxHW = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getResources().getDisplayMetrics());
        ImageButton imageButton = new ImageButton(this, null, android.R.style.Widget_Holo_Button_Borderless);
        imageButton.setImageResource(R.drawable.comment_account_outline);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCollapsed) {
                    textView.setVisibility(View.VISIBLE);
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                else {
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    textView.setVisibility(View.GONE);
                }
                isCollapsed = !isCollapsed;
                windowManager.updateViewLayout(layout, layoutParams);
            }
        });
        imageButton.setLayoutParams(new AppBarLayout.LayoutParams(pxHW, pxHW));
        return imageButton;
    }

    private TextView initTextView() {
        TextView textView = new TextView(this);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Medium);
        textView.setVisibility(View.GONE);
        textView.setLayoutParams(new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return textView;
    }

    private WindowManager.LayoutParams initLayoutParams() {
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, getResources().getDisplayMetrics());
        layout.setPadding(px, 0, px, 0);
        return layoutParams;
    }

    private LinearLayout initLayout() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        return linearLayout;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String name = getCallerName(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));

        if(textView != null)
            if(name != null)
                textView.setText(PLACEHOLDER_TEXT_FOR_KNOWN_CALLER + name);
            else textView.setText(PLACEHOLDER_TEXT_FOR_UNKNOWN_CALLER);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(layout != null)
            windowManager.removeView(layout);
    }

    public String getCallerName(String phoneNumber) {
        Uri query = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = getContentResolver().query(query, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
        return null;
    }
}
