// Generated code from Butter Knife. Do not modify!
package com.originally.shape.countroll.modules.SearchView;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SearchView$$ViewInjector<T extends com.originally.shape.countroll.modules.SearchView.SearchView> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492960, "field 'etSearch'");
    target.etSearch = finder.castView(view, 2131492960, "field 'etSearch'");
    view = finder.findRequiredView(source, 2131492959, "field 'ivIconSearch'");
    target.ivIconSearch = finder.castView(view, 2131492959, "field 'ivIconSearch'");
    view = finder.findRequiredView(source, 2131492958, "field 'vCloseSearchView' and method 'onCloseSearchView'");
    target.vCloseSearchView = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onCloseSearchView();
        }
      });
  }

  @Override public void reset(T target) {
    target.etSearch = null;
    target.ivIconSearch = null;
    target.vCloseSearchView = null;
  }
}
