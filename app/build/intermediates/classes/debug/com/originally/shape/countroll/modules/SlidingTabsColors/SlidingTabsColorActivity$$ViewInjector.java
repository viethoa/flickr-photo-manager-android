// Generated code from Butter Knife. Do not modify!
package com.originally.shape.countroll.modules.SlidingTabsColors;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SlidingTabsColorActivity$$ViewInjector<T extends com.originally.shape.countroll.modules.SlidingTabsColors.SlidingTabsColorActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492955, "field 'mSlidingTabLayout'");
    target.mSlidingTabLayout = finder.castView(view, 2131492955, "field 'mSlidingTabLayout'");
    view = finder.findRequiredView(source, 2131492956, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131492956, "field 'mViewPager'");
  }

  @Override public void reset(T target) {
    target.mSlidingTabLayout = null;
    target.mViewPager = null;
  }
}
