package com.example.easybookshare.adapters;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.easybookshare.fragments.BuyFragment;
import com.example.easybookshare.fragments.SellFragment;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    int count;
    Context context;

    public MyPagerAdapter(Context context, FragmentManager fm, int count) {
        super(fm, count);
        this.count = count;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                BuyFragment buyFragment = new BuyFragment();
                return buyFragment;
            case 1:
                SellFragment sellFragment = new SellFragment();
                return sellFragment;
            default:
                Toast.makeText(context,"Invalid Fragment", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }
}
