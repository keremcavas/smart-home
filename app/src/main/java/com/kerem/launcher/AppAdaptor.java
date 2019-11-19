package com.kerem.launcher;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AppAdaptor extends RecyclerView.Adapter<AppAdaptor.ViewHolder> {

    private ArrayList<AllApplications.AppInfo> appList;

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView nameView;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.app_icon);
            nameView = itemView.findViewById(R.id.app_name);
        }
    }

    AppAdaptor(ArrayList<AllApplications.AppInfo> a) {
        appList = a;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View appView = inflater.inflate(R.layout.app_icon, viewGroup, false);
        return new ViewHolder(appView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        AllApplications.AppInfo appInfo = appList.get(i);
        TextView textView = viewHolder.nameView;
        textView.setText(appInfo.getAppName());
        ImageView imageView = viewHolder.iconView;
        imageView.setImageDrawable(appInfo.getIcon());
        setFadeAnim(viewHolder.itemView);
    }

    private void setFadeAnim(View itemView) {
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}
