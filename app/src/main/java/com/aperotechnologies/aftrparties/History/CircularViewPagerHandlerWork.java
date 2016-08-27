package com.aperotechnologies.aftrparties.History;

import android.support.v4.view.ViewPager;

/**
 * Created by mpatil on 19/07/16.
 */

//For never ending view pager of requestant list
public class CircularViewPagerHandlerWork implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private int         mCurrentPosition;
    private int         mScrollState;

    public CircularViewPagerHandlerWork(final ViewPager viewPager) {
        mViewPager = viewPager;
    }

    @Override
    public void onPageSelected(final int position) {
        mCurrentPosition = position;
        //mViewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public void onPageScrollStateChanged(final int state)
    {
        handleScrollState(state);
        mScrollState = state;

    }

    private void handleScrollState(final int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setNextItemIfNeeded();
        }
    }

    private void setNextItemIfNeeded() {
        if (!isScrollStateSettling()) {
            handleSetNextItem();
        }
    }

    private boolean isScrollStateSettling() {
        return mScrollState == ViewPager.SCROLL_STATE_SETTLING;

    }

    private void handleSetNextItem() {
        final int lastPosition = mViewPager.getAdapter().getCount() - 1;
        if(mCurrentPosition == 0) {
            mViewPager.setCurrentItem(lastPosition, false);
        } else if(mCurrentPosition == lastPosition) {
            //mViewPager.setPageTransformer(true, new DepthPageTransformer());
            mViewPager.setCurrentItem(0, false);

        }

    }



    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels)
    {

    }



}