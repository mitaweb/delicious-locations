package com.fetel.ltdd.team9.share.recipes.ui.activities;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.enums.DetailType;
import com.fetel.ltdd.team9.share.recipes.ui.enums.ExtraKeyType;
import com.fetel.ltdd.team9.share.recipes.ui.fragments.DetailListAllPostFragment;
import com.fetel.ltdd.team9.share.recipes.ui.fragments.DetailListPostFragment;
import com.fetel.ltdd.team9.share.recipes.ui.fragments.DetailListSubFragment;
import com.fetel.ltdd.team9.share.recipes.ui.fragments.DetailPostFragment;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        replaceFragments();
    }

    private void replaceFragments() {
        DetailType type = DetailType.detachFrom(getIntent());
        Log.d(TAG, "replaceFragments: " + type.name());

        switch (type) {
            case LIST_SUB: {
                if (getIntent().getExtras() != null) {
                    Bundle args = getIntent().getExtras().getBundle(ExtraKeyType.DEFAULT_EXTRA.getValue());
                    if (args != null) {
                        String categoryID = args.getString(ExtraKeyType.EXTRA_KEY_CATEGORY_ID.getValue(), "");
                        Log.d(TAG, "LIST_SUB: " + categoryID);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, DetailListSubFragment.newInstance(categoryID)).commit();
                    }
                }
                break;
            }
            case LIST_POST: {
                if (getIntent().getExtras() != null) {
                    Bundle args = getIntent().getExtras().getBundle(ExtraKeyType.DEFAULT_EXTRA.getValue());
                    if (args != null) {
                        String categoryPath = args.getString(ExtraKeyType.EXTRA_KEY_CATEGORY_PATH.getValue(), "");
                        Log.d(TAG, "LIST_POST: " + categoryPath);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, DetailListPostFragment.newInstance(categoryPath)).commit();
                    }
                }
                break;
            }
            case SHARE_RECIPE: {
                if (getIntent().getExtras() != null) {
                    Bundle args = getIntent().getExtras().getBundle(ExtraKeyType.DEFAULT_EXTRA.getValue());
                    if (args != null) {
                        String postId = args.getString(ExtraKeyType.EXTRA_KEY_POST_ID.getValue(), "");
                        Log.d(TAG, "SHARE_RECIPE: " + postId);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, DetailPostFragment.newInstance(postId)).commit();
                    }
                }
                break;
            }
            case SEARCH_ALL_POST: {
                if (getIntent().getExtras() != null) {
                    Bundle args = getIntent().getExtras().getBundle(ExtraKeyType.DEFAULT_EXTRA.getValue());
                    if (args != null) {
                        String searchKey = args.getString(ExtraKeyType.EXTRA_KEY_SEARCH_KEY.getValue(), "");
                        Log.d(TAG, "SEARCH_ALL_POST: " + searchKey);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, DetailListAllPostFragment.newInstance(searchKey)).commit();
                    }
                }
                break;
            }
        }
    }
}
