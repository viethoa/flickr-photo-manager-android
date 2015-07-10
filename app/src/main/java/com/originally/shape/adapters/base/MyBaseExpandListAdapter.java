package com.originally.shape.adapters.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;

import com.originally.shape.models.ExpandListModels.Child;
import com.originally.shape.models.ExpandListModels.Parent;

/**
 * Created by VietHoa on 22/04/15.
 */
public class MyBaseExpandListAdapter<T extends Parent> extends BaseExpandableListAdapter {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ArrayList<T> mListData;

    public MyBaseExpandListAdapter(Context context, ArrayList<T> listData) {
        this.mContext = context;
        this.mListData = listData;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshDataChange(ArrayList<T> newData) {
        mListData = newData;
        notifyDataSetChanged();
    }

    public ArrayList<T> getData() {
        return mListData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition < 0 || groupPosition >= mListData.size())
            return null;

        if (childPosition < 0 || childPosition >= mListData.get(groupPosition).children.size())
            return null;

        return mListData.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Child model = null;
        try {
            model = (Child) mListData.get(groupPosition).children.get(childPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (model == null)
            return 0;

        return model.hashCode();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= mListData.size())
            return 0;

        T groupItem = mListData.get(groupPosition);
        if (groupItem == null || groupItem.children == null)
            return 0;

        return mListData.get(groupPosition).children.size();
    }

    @Override
    public T getGroup(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= mListData.size())
            return null;

        return mListData.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        if (mListData == null)
            return 0;

        return mListData.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        T model = null;
        try {
            model = mListData.get(groupPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (model == null)
            return 0;

        return model.hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Log.e(this.getClass().getSimpleName(), "Subclass must override this");
        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.e(this.getClass().getSimpleName(), "Subclass must override this");
        return convertView;
    }
}
