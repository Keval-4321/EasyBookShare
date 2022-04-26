package com.example.easybookshare.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager
{
    Context context;
    public SharedPreferences sharedPreferencesData;
    public SharedPreferenceManager(Context context)
    {
        this.context = context;
        sharedPreferencesData = context.getSharedPreferences("DATA",Context.MODE_PRIVATE);
    }



}
