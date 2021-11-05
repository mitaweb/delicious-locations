package com.fetel.ltdd.team9.share.recipes.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.activities.DetailActivity;
import com.fetel.ltdd.team9.share.recipes.ui.adapter.ListSubCategoryAdapter;
import com.fetel.ltdd.team9.share.recipes.ui.enums.DetailType;
import com.fetel.ltdd.team9.share.recipes.ui.enums.ExtraKeyType;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnItemClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailListSubFragment extends Fragment {

    private static final String TAG = DetailListSubFragment.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvListSubCategory;
    private ListSubCategoryAdapter listSubCategoryAdapter;
    private DatabaseReference subCategoryDatabase, categoryDatabase;

    public DetailListSubFragment() {
        // Required empty public constructor
    }

    public static DetailListSubFragment newInstance(String categoryId) {
        DetailListSubFragment fragment = new DetailListSubFragment();
        Bundle args = new Bundle();
        args.putString(ExtraKeyType.EXTRA_KEY_CATEGORY_ID.getValue(), categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_list_sub, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView(view);
        loadData();
    }

    private void setUpView(View root) {
        //RecyclerView
        rvListSubCategory = root.findViewById(R.id.rv_list_sub_category);
        if (listSubCategoryAdapter == null) {
            listSubCategoryAdapter = new ListSubCategoryAdapter(getActivity(), new OnItemClickListener() {
                @Override
                public <T> void onItemClicked(T item, int position) {
                    Category categorySub = (Category) item;

                    if (getArguments() != null) {
                        String categoryID = getArguments().getString(ExtraKeyType.EXTRA_KEY_CATEGORY_ID.getValue());
                        if (TextUtils.isEmpty(categoryID)) {
                            return;
                        }

                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        Bundle args = new Bundle();
                        args.putString(ExtraKeyType.EXTRA_KEY_CATEGORY_PATH.getValue(), "category/" + categoryID + "/subList/" + categorySub.getId());
                        intent.putExtra(ExtraKeyType.DEFAULT_EXTRA.getValue(), args);
                        DetailType.LIST_POST.attachTo(intent);
                        startActivity(intent);
                    }
                }
            });
        }
        rvListSubCategory.setAdapter(listSubCategoryAdapter);

        //Title And Back Button
        ivBack = root.findViewById(R.id.iv_back);
        tvTitle = root.findViewById(R.id.tv_title);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null && getActivity() instanceof DetailActivity) {
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private void loadData() {
        Log.w(TAG, "loadData");
        if (getArguments() != null) {
            String categoryID = getArguments().getString(ExtraKeyType.EXTRA_KEY_CATEGORY_ID.getValue());
            if (TextUtils.isEmpty(categoryID)) {
                return;
            }
            loadTitle(categoryID);
        }
    }

    private void loadTitle(String categoryID) {
        //Load Title
        categoryDatabase = FirebaseDatabase.getInstance().getReference("category")
                .child(categoryID);
        ValueEventListener categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d(TAG, "dataSnapshot.getValue: " + dataSnapshot.getValue());
                listSubCategoryAdapter.clearAllItems();
                Category category = dataSnapshot.getValue(Category.class);
                if (category != null) {
                    category.setId(dataSnapshot.getKey());
                    Log.d(TAG, "category: " + category.toString());
                    tvTitle.setText(category.getTitle(getActivity()));
                }

                loadListSub(categoryID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        categoryDatabase.addListenerForSingleValueEvent(categoryListener);
    }

    private void loadListSub(String categoryID) {
        subCategoryDatabase = FirebaseDatabase.getInstance().getReference("category")
                .child(categoryID)
                .child("subList");
        ValueEventListener subCategoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d(TAG, "dataSnapshot.getValue: " + dataSnapshot.getValue());
                listSubCategoryAdapter.clearAllItems();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        category.setId(snapshot.getKey());
                        Log.d(TAG, "category: " + category.toString());
                        listSubCategoryAdapter.addItem(category);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        subCategoryDatabase.addListenerForSingleValueEvent(subCategoryListener);
    }
}