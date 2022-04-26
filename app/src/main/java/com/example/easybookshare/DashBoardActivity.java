package com.example.easybookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.easybookshare.adapters.MyPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class DashBoardActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Buy"));
        tabLayout.addTab(tabLayout.newTab().setText("Sell"));
        //setTabBackground(R.drawable.tab_bg_left_selected,R.drawable.tab_bg_right_unselected);

        // for the default tab selection
        tabLayout.getTabAt(0).select();

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this,getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(myPagerAdapter);

        // default left tab is selected so..
        tabLayout.getTabAt(0).view.setBackground(getResources().getDrawable(R.drawable.tab_bg_left_selected));
        tabLayout.getTabAt(1).view.setBackground(getResources().getDrawable(R.drawable.tab_bg_right_unselected));

        //tab selection fragment load
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
                // changes the tab background accordingly
                if(tab.getPosition()==0)
                {
                    tab.view.setBackground(getResources().getDrawable(R.drawable.tab_bg_left_selected));
                    tabLayout.getTabAt(1).view.setBackground(getResources().getDrawable(R.drawable.tab_bg_right_unselected));
                }
                else
                {
                    tab.view.setBackground(getResources().getDrawable(R.drawable.tab_bg_right_selected));
                    tabLayout.getTabAt(0).view.setBackground(getResources().getDrawable(R.drawable.tab_bg_left_unselected));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // now on swipe also change indicator position
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_profile_wishlist,menu);

        MenuItem profile = menu.findItem(R.id.profile);
        MenuItem wishList = menu.findItem(R.id.wishlist);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        Intent intent = null;

        switch (item.getItemId())
        {
            case R.id.profile:
                intent = new Intent(this,UserProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in_profile,R.anim.stay_here);
                break;
            case R.id.wishlist:
                intent = new Intent(this,WishListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in_profile,R.anim.stay_here);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}