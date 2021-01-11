package com.example.cs223_final.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.cs223_final.MainActivity;
import com.example.cs223_final.R;
import com.example.cs223_final.ui.home.add.NewSubscription;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_fragment_layout, container, false);
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("Home");


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FloatingActionButton fab_add = v.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewSubscription.class);
                startActivity(intent);

            }
        });


        TabLayout tabLayout = v.findViewById(R.id.tabbed_layout);
        ViewPager viewPager = v.findViewById(R.id.viewPager);
        ViewPageAdapter adapter = new ViewPageAdapter(getFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        adapter.addFragment(new ServicesFragment(),"Services");
        adapter.addFragment(new UsageFragment(),"Usage");


        viewPager.setAdapter(adapter);


        tabLayout.setupWithViewPager(viewPager);


        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_entertainment);

        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_usage);

    }



///ADAPTER CLASSS
    public class ViewPageAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> titles = new ArrayList<>();

        ViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {

            return titles.size();
        }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);

            //I DON'T WANT FRAGMENTS TO HAVE A NAME JUST AN ICON
            //TO HAVE A NAME AD THIS BAK TO CONSTRUCTOR
            titles.add(title);
        }
    }
}
