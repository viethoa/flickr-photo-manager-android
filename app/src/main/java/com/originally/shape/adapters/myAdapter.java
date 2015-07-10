package com.originally.shape.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.originally.shape.adapters.base.MyBaseArrayListAdapter;
import com.originally.shape.models.MyModel;

import java.util.ArrayList;

/**
 * Created by VietHoa on 08/07/15.
 */
public class myAdapter extends MyBaseArrayListAdapter<MyModel> {

    public myAdapter(Context context, ArrayList<MyModel> dataArray) {
        super(context, dataArray);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
