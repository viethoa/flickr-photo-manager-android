package com.viethoa.siliconstraits.testing.adapters.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.viethoa.siliconstraits.testing.R;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyBaseArrayListAdapter<T> extends BaseAdapter {

    protected ArrayList<T> mDataArray;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public MyBaseArrayListAdapter(Context context, ArrayList<T> dataArray) {
        this.mDataArray = dataArray != null ? dataArray : new ArrayList<T>();
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshData(ArrayList<T> dataArray) {
        this.mDataArray = dataArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (this.mDataArray == null)
            return 0;
        return this.mDataArray.size();
    }

    @Override
    public T getItem(int position) {
        if (position < 0 || position > this.mDataArray.size() - 1)
            return null;

        T model = this.mDataArray.get(position);
        return model;
    }

    @Override
    public long getItemId(int position) {
        T model = null;
        try {
            model = mDataArray.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (model == null)
            return 0;

        return model.hashCode();
    }

    public int getItemIndex(T theObject) {
        if (theObject == null)
            return -1;
        for (T obj : this.mDataArray)
            if (obj.equals(theObject))
                return mDataArray.indexOf(obj);
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(this.getClass().getSimpleName(), "Subclass must override this");
        return convertView;
    }

    protected String formatDecimal(String pattern, float value) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

    public int getResId(String drawableName) {
        if (drawableName == null || drawableName.length() <= 0)
            return 0;

        try {
            Class<R.drawable> res = R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            Log.e("CATEGORY", "Failure to get drawable id for " + drawableName, e);
        }
        return 0;
    }

    //--------------------------------------------------------------------------------
    //  Helper function
    //--------------------------------------------------------------------------------

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        MyBaseArrayListAdapter listAdapter = (MyBaseArrayListAdapter) listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}