package com.kerem.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllApplications extends MainActivity {
    public Context context;
    private PackageManager pm;

    class AppInfo {
        private Drawable icon;
        private String appName;
        private String packageName;

        AppInfo(Drawable i, String a, String p) {
            this.icon = i;
            this.appName = a;
            this.packageName = p;
        }

        String getAppName() {
            return appName;
        }

        Drawable getIcon() {
            return icon;
        }

        String getPackageName() {
            return packageName;
        }
    }

    public AllApplications(Context contextT) {
        this.context = contextT;
        this.pm = context.getPackageManager();
    }

    public ArrayList<AppInfo> getAllAppsSorted() {
        ArrayList<AppInfo> apps = new ArrayList< >();
        List<ResolveInfo> packs = getPackages();

        for (ResolveInfo p : packs) {
            AppInfo appInfo = new AppInfo(
                    p.activityInfo.loadIcon(pm),
                    p.loadLabel(pm).toString(),
                    p.activityInfo.packageName);
            if (appInfo.packageName == null || appInfo.packageName.length() <= 0) {
                continue;
            }
            apps.add(appInfo);
        }

        Collections.sort(apps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return o1.appName.compareTo(o2.appName);
            }
        });

        return apps;
    }

    private List<ResolveInfo> getPackages() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pm.queryIntentActivities(intent, 0);
    }

    public int getPackagesCount() {
        return getPackages().size();
    }

}
