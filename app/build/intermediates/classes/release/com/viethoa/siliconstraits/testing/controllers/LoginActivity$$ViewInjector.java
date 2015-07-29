// Generated code from Butter Knife. Do not modify!
package com.viethoa.siliconstraits.testing.controllers;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class LoginActivity$$ViewInjector<T extends com.viethoa.siliconstraits.testing.controllers.LoginActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689559, "field 'btnFlickrLogin' and method 'onBtnFlickrLoginClicked'");
    target.btnFlickrLogin = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onBtnFlickrLoginClicked();
        }
      });
  }

  @Override public void reset(T target) {
    target.btnFlickrLogin = null;
  }
}
