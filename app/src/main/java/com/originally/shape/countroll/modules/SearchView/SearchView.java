package com.originally.shape.countroll.modules.SearchView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;
import com.originally.shape.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by VietHoa on 03/05/15.
 */
public class SearchView extends RelativeLayout implements TextWatcher {

    @InjectView(R.id.et_search_view)
    EditText etSearch;
    @InjectView(R.id.iv_icon_search)
    ImageView ivIconSearch;
    @InjectView(R.id.iv_close_search)
    View vCloseSearchView;

    private boolean isClose = false;
    private SearchViewListener listener;

    public SearchView(Context context) {
        super(context);
        initialiseView(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialiseView(context);
    }

    //----------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------

    protected void initialiseView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.app_search_view, this);

        ButterKnife.inject(this);
        listener = (SearchViewListener) context;

        initialiseUI();
    }

    protected void initialiseUI() {
        etSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onRemoveSearchText(false);
            }
        });
        etSearch.addTextChangedListener(this);

        ivIconSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClose) {
                    onRemoveSearchText(true);
                }

                performSearch();
            }
        });


        etSearch.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    public void setText(String searchText) {
        etSearch.setText(searchText);
    }

    public String getSearchText() {
        if (etSearch == null)
            return null;

        return etSearch.getText().toString();
    }

    public View getBtnSearch() {
        if (ivIconSearch == null) {
            Log.d("get icon view", "null point exception");
            return this;
        }

        return ivIconSearch;
    }

    public void setEtSearchFocused() {
        if (!etSearch.isFocused())
            etSearch.requestFocus();
    }

    public EditText getEtSearch() {
        return etSearch;
    }

    public void setSearchFocused() {
        if (etSearch == null)
            return;
        etSearch.requestFocus();
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    protected void onRemoveSearchText(boolean isRemove) {

        ivIconSearch.setImageResource(R.mipmap.ic_search_blue);
        isClose = false;

        String currText = etSearch.getText().toString();
        etSearch.setText(isRemove ? "" : currText);

        int position = etSearch.length();
        Editable editable = etSearch.getText();
        Selection.setSelection(editable, position);
    }

    protected void onSearching(String searchText) {

        ivIconSearch.setImageResource(R.mipmap.ic_search_blue);
        isClose = false;

        if (listener != null && StringUtils.isNotNull(searchText) && searchText.length() > 2) {
            listener.onSearch(searchText);
        }
    }

    public void performSearch() {
        String searchText = etSearch.getText().toString();
        if (StringUtils.isNull(searchText) || searchText.length() < 3)
            return;

        ivIconSearch.setVisibility(View.VISIBLE);
        ivIconSearch.setImageResource(R.mipmap.ic_search_close);
        isClose = true;

        try {
            SearchViewActivity parent = (SearchViewActivity) getContext();
            DeviceUtils.hideKeyboard(parent);
        } catch (Exception ex) {
            throw new IllegalArgumentException("This need on Main activity");
        }

        if (listener != null) {
            listener.onSearch(searchText);
        }
    }

    @OnClick(R.id.iv_close_search)
    protected void onCloseSearchView() {
        if (listener != null) {
            listener.onCloseSearchView();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        //onSearching(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public interface SearchViewListener {
        void onSearch(String searchText);

        void onCloseSearchView();
    }
}
