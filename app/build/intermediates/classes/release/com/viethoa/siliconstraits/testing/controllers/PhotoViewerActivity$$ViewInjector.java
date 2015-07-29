// Generated code from Butter Knife. Do not modify!
package com.viethoa.siliconstraits.testing.controllers;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class PhotoViewerActivity$$ViewInjector<T extends com.viethoa.siliconstraits.testing.controllers.PhotoViewerActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689574, "field 'viewpager'");
    target.viewpager = finder.castView(view, 2131689574, "field 'viewpager'");
    view = finder.findRequiredView(source, 2131689575, "field 'ivBackIcon' and method 'onBackIconClicked'");
    target.ivBackIcon = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onBackIconClicked();
        }
      });
  }

  @Override public void reset(T target) {
    target.viewpager = null;
    target.ivBackIcon = null;
  }
}
