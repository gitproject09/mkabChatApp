package org.mkab.chatapp.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.mkab.chatapp.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class DoctorsListFragment extends Fragment {
    private static final String TAG = "DoctorsListFragment";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private View rootView;

    public DoctorsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_doctors_list, container, false);

        initializeViews(rootView);

        return rootView;

    }

    private void initializeViews(View view) {
        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new AllopathicListFragment(),
                    new HomeopathicListFragment(),
                    new AurvedicListFragment()
            };
            private final String[] mFragmentNames = new String[]{
                    getString(R.string.heading_allopathic),
                    getString(R.string.heading_homeo),
                    getString(R.string.heading_aurvedic)
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = view.findViewById(R.id.doctorContainer);

        int limit = (mPagerAdapter.getCount() > 1 ? mPagerAdapter.getCount() - 1 : 1);
        mViewPager.setOffscreenPageLimit(limit);

        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tabDoctors);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}

