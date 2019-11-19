package com.kerem.launcher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private int longClickedPosition = -1;

    private ArrayList<AllApplications.AppInfo> apps;
    private AllApplications allApplications;
    private RecyclerView appListLayout;
    private LinearLayout homeListLayout;
    private LinearLayout clockLayout;
    private HomeApps home;
    private RecyclerItemClickListener recyclerItemClickListener;
    private BottomSheetBehavior bottomSheetBehavior;
    private int lastDay = getDay();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setConstants();
        setClock();
        setAppDrawer();
    }

    private void setConstants() {
        appListLayout = findViewById(R.id.app_list_layout);
        homeListLayout = findViewById(R.id.home_apps_layout);
        home = new HomeApps();
        allApplications = new AllApplications(MainActivity.this);

        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) homeListLayout.getLayoutParams();
        params.bottomMargin = getNavigationHeight();
        homeListLayout.requestLayout();

        setBottomSheet();
    }

    private void setBottomSheet() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheet.setNestedScrollingEnabled(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int i) {
                if (i == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    Log.e("STATE", "Collapsed");
                    // getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                }
                if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Log.e("STATE", "Expanded");
                    // getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.statusBar));
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                appListLayout.setAlpha(v);
                homeListLayout.setAlpha(1 - v);
                homeListLayout.setY(-200 * v);
                clockLayout.setY(-400 * v);
                // getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                Log.e("state", "offset" + v);
                if (longClickedPosition != -1) {
                    removeLongClickedEffect(longClickedPosition);
                }
            }
        });
    }

    private void setAppDrawer() {
        apps = allApplications.getAllAppsSorted();

        AppAdaptor appAdaptor = new AppAdaptor(apps);
        appListLayout.setAdapter(appAdaptor);
        appListLayout.setLayoutManager(new LinearLayoutManager(this));
        appListLayout.addOnItemTouchListener(recyclerItemClickListener());
    }

    private RecyclerItemClickListener recyclerItemClickListener() {
        recyclerItemClickListener = new RecyclerItemClickListener(
                MainActivity.this,
                appListLayout,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PackageManager pm = MainActivity.this.getPackageManager();
                Intent i = pm.getLaunchIntentForPackage(apps.get(position).getPackageName());
                startActivity(i);
                Log.v("a", "" + position);
                home.onAppClicked(position, getHour(), true);
                checkNewDay();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                final int pixels = view.getHeight();
                ViewGroup group = (ViewGroup) view;
                View appInfoView = group.getChildAt(0);
                appInfoView.animate().x(pixels);
                group.addView(infoButton(pixels), 1);
                appListLayout.removeOnItemTouchListener(recyclerItemClickListener);
                notifyLongClicked(position);
            }
        });
        return recyclerItemClickListener;
    }

    private void checkNewDay() {
        int currentDay = getDay();
        if (lastDay != currentDay) {
            home.reSync();
            lastDay = currentDay;
        }

    }

    private void startActivityInfo(int position) {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + apps.get(position).getPackageName()));
        startActivity(intent);
    }

    private Button infoButton(int size) {
        Button info = new Button(MainActivity.this);
        info.setBackgroundResource(android.R.drawable.ic_menu_info_details);
        info.setLayoutParams(new FrameLayout.LayoutParams(size - 30, size -30));
        info.setAlpha(0);
        info.setX(15);
        info.setY(15);
        info.animate().alpha(1);
        info.setClickable(true);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityInfo(longClickedPosition);
            }
        });
        return info;
    }

    private void notifyLongClicked(int position) {
        if (longClickedPosition != -1) {
            removeLongClickedEffect(longClickedPosition);
        }
        longClickedPosition = position;

    }

    private void removeLongClickedEffect(int position) {
        ViewGroup group = (ViewGroup) Objects.requireNonNull(appListLayout.getLayoutManager()).findViewByPosition(position);
        assert group != null;
        View deleteIcon = group.getChildAt(1);
        group.removeView(deleteIcon);
        View appLine = group.getChildAt(0);
        appLine.animate().x(0);
        longClickedPosition = -1;
        appListLayout.addOnItemTouchListener(recyclerItemClickListener);
    }

    private void setClock() {
        clockLayout = findViewById(R.id.clock_layout);
        TextClock textClockHour = findViewById(R.id.text_clock_hour);
        textClockHour.setFormat24Hour("HH");
        TextClock textClockMinute = findViewById(R.id.text_clock_minute);
        textClockMinute.setFormat24Hour("mm");
/*
        textClockHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchClockIntent = getPackageManager().getLaunchIntentForPackage("com.android.deskclock");
                startActivity(launchClockIntent);
            }
        });

        textClockMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchClockIntent = getPackageManager().getLaunchIntentForPackage("com.android.deskclock");
                startActivity(launchClockIntent);
            }
        });
*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkNewApps();
        final int[] sequences = HomeApps.getHomeApps(getHour());
        homeListLayout.removeAllViews();
        for (int i=0; i<5; i++) {
            if (HomeApps.getScore(sequences[i], getHour()) == 0) {
                break;
            }
            int appID = sequences[i];
            homeListLayout.addView(setHomeAppIcon(appID, i), i);
        }
        homeListLayout.setGravity(Gravity.CENTER);
        clockLayout.setY(-400);
        clockLayout.animate().y(0);
    }

    private ImageView setHomeAppIcon(final int appID, final int i) {
        final AllApplications.AppInfo appInfo = apps.get(appID);
        ImageView appIconView = new ImageView(this);
        appIconView.setImageDrawable(appInfo.getIcon());
        appIconView.setMaxWidth(150);
        appIconView.setMaxHeight(150);
        appIconView.setPadding(25, 25, 25, 25);
        appIconView.setY(200);
        appIconView.animate().setStartDelay(10*i).setDuration(30).y(-10*i);
        appIconView.animate().setStartDelay(10*i + 30).y(0);
        appIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateHome(i);
                startActivity(
                        getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName()));
                home.onAppClicked(appID, getHour(), false);
            }
        });
        return appIconView;
    }

    private void animateHome(int i) {
        int end  = homeListLayout.getChildCount();
        int start = 0;
        int delay = 0;
        while (end != start) {
            if (start != i) {
                homeListLayout.getChildAt(start).animate().setStartDelay(delay).y(200);
                start++;
                delay += 10;
            }
            if (end != i) {
                homeListLayout.getChildAt(start).animate().setStartDelay(delay).y(200);
                end--;
                delay += 10;
            }
        }
        homeListLayout.getChildAt(i).animate().setStartDelay(delay).y(-200);
    }

    private int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    private int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    private int getNavigationHeight() {
        Resources resources = getApplicationContext().getResources();
        int resourceID = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceID > 0) {
            return resources.getDimensionPixelSize(resourceID);
        }
        return 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onBackPressed() {
        if (longClickedPosition != -1) {
            removeLongClickedEffect(longClickedPosition);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void checkNewApps() {
        if (allApplications.getPackagesCount() != apps.size()) {
            refreshAppList();
        }
    }

    private void refreshAppList() {
        setAppDrawer();
    }
}
