package com.tuananh2.filesbrowsers;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ListActivity {
        private String path;
        public static final String TAG = "anhlt2";
        public static final int MY_PERMISSIONS = 1;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            checkAndRequestPermissions();
            // Use the current directory as title
            path = "/";
            if (getIntent().hasExtra("path")) {
                path = getIntent().getStringExtra("path");
            }
            setTitle(path);

            // Read all files sorted into the values-array
            List values = new ArrayList();
            File dir = new File(path);
            if (!dir.canRead()) {
                setTitle(getTitle() + " (inaccessible)");
            }
            String[] list = dir.list();
            if (list != null) {
                for (String file : list) {
                    if (!file.startsWith(".")) {
                        values.add(file);
                    }
                }
            }
            Collections.sort(values);

            // Put the data into the list
            ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_2, android.R.id.text1, values);
            setListAdapter(adapter);
        }

        @Override
        protected void onListItemClick(ListView l, View v, int position, long id) {
            String filename = (String) getListAdapter().getItem(position);
            if (path.endsWith(File.separator)) {
                filename = path + filename;
            } else {
                filename = path + File.separator + filename;
            }
            if (new File(filename).isDirectory()) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("path", filename);
                startActivity(intent);
            } else {
                Toast.makeText(this, filename + " is not a directory", Toast.LENGTH_LONG).show();
            }
        }

    //get list of available storage and save them in share preference
    private void getListStorages()
    {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!Environment.isExternalStorageRemovable())
        {
            File [] listStorages = getExternalFilesDirs(null);
            Log.d(TAG, "getListStorages: number of storage= "+listStorages.length);
            if(listStorages!= null)
            {
                for(File i : listStorages)
                {
                    Log.d(TAG, "getListStorages: storage: "+ i.toString()+ " available mem= "+i.getFreeSpace());
                }
            }
        }
    }

    public void checkAndRequestPermissions()
    {
        if(Build.VERSION.SDK_INT>= 23)
        {
            if((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
                    || (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED))
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS);
            }
        }
    }

}
