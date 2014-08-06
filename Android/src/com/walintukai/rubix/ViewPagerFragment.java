package com.walintukai.rubix;

import com.viewpagerindicator.CirclePageIndicator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerFragment extends Fragment {
	
	private ViewPager mPager;
	private ViewPagerAdapter mAdapter;
	private ViewPagerFragmentListener mCallback;
	
	static ViewPagerFragment newInstance() {
		ViewPagerFragment fragment = new ViewPagerFragment();
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try { 
			mCallback = (ViewPagerFragmentListener) activity; 
		} 
		catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ViewPagerFragmentListener");
        }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
		mPager = (ViewPager) view.findViewById(R.id.pager);
		
		mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		
		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.page_indicator);
		circlePageIndicator.setViewPager(mPager, 1);
		circlePageIndicator.setRadius(10);
		circlePageIndicator.setStrokeWidth(3);
		circlePageIndicator.setStrokeColor(Color.parseColor("#000000"));
		circlePageIndicator.setFillColor(Color.parseColor("#000000"));
		circlePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int i) {
			}
			
			@Override
			public void onPageScrolled(int i, float v, int i2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});

		
		return view;
	}
	
	private class ViewPagerAdapter extends FragmentPagerAdapter {
		
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	switch (position) {
        	case 0:
        		return StatisticsFragment.newInstance();
        	case 1:
        		return UnlockFragment.newInstance();
        	case 2:
        		return SettingsFragment.newInstance();
    		default:
        		return null;	
        	}
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
	
	public void getCurrentFragment() {
		Fragment current = getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + 
				R.id.pager + ":" + mPager.getCurrentItem());
		mCallback.onSendCurrentFragment(current);
	}
	
	public interface ViewPagerFragmentListener {
		public void onSendCurrentFragment(Fragment fragment);
	}

}
