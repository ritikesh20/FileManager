package com.example.filemanager.apps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter<AppsAdapter> itemAdapterApps;
    private FastAdapter<AppsAdapter> fastAdapterApps;
    private List<AppsAdapter> listApps = new ArrayList<>();
    private ProgressBar progressBarApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        Toolbar toolbar = findViewById(R.id.toolbarApps);

        recyclerView = findViewById(R.id.recyclerViewApps);
        itemAdapterApps = new ItemAdapter<>();
        fastAdapterApps = FastAdapter.with(itemAdapterApps);
        progressBarApps = findViewById(R.id.progressbarApps);

        progressBarApps.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(fastAdapterApps);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Apps");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadingApp();
    }

    private void loadingAppx() {
//
//        PackageManager packageManager = getPackageManager();
//        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
//
//        for (ApplicationInfo app : apps) {
//            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
//                continue;
//            }
//
//            Drawable icon = app.loadIcon(packageManager);
//            String name = app.loadLabel(packageManager).toString();
//            long size = new File(app.sourceDir).length();
//            String sizeFormatted = Formatter.formatFileSize(this, size);
//
//            PackageInfo pi = packageManager.getPackageArchiveInfo(app.packageName, 0);
//
//            String data = new SimpleDateFormat("dd MMM",
//                    Locale.getDefault())
//                    .format(new Date(pi.firstInstallTime));
//
//
//            listApps.add(new AppsAdapter(icon, name, sizeFormatted, data));
//
//
//        }
//
//        itemAdapterApps.setNewList(listApps);


    }


    private void loadingApp() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                PackageManager packageManager = getPackageManager();

                List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

                for (ApplicationInfo app : apps) {
                    // Skip system apps
//            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
//                continue;
//            }

                    Drawable icon = app.loadIcon(packageManager);
                    String name = app.loadLabel(packageManager).toString();
                    long size = new File(app.sourceDir).length();
                    String sizeFormatted = Formatter.formatFileSize(AppsActivity.this, size);

                    try {
                        PackageInfo pi = packageManager.getPackageInfo(app.packageName, 0);
                        String date = new SimpleDateFormat("dd MMM", Locale.getDefault())
                                .format(new Date(pi.firstInstallTime));

                        listApps.add(new AppsAdapter(icon, name, sizeFormatted, date));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(() -> itemAdapterApps.setNewList(listApps));
                        progressBarApps.setVisibility(View.GONE);
                    }
                });
            }
        });
    }




}