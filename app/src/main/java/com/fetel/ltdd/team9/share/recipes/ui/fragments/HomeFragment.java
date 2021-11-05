package com.fetel.ltdd.team9.share.recipes.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.activities.DetailActivity;
import com.fetel.ltdd.team9.share.recipes.ui.adapter.ListCategoryAdapter;
import com.fetel.ltdd.team9.share.recipes.ui.enums.DetailType;
import com.fetel.ltdd.team9.share.recipes.ui.enums.ExtraKeyType;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnItemClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private RecyclerView rvListCategory;
    private ListCategoryAdapter listCategoryAdapter;
    private DatabaseReference categoryDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView(view);
        loadData();
    }

    private void setUpView(View root) {
        rvListCategory = root.findViewById(R.id.rv_list_category);
        if (listCategoryAdapter == null) {
            listCategoryAdapter = new ListCategoryAdapter(getActivity(), new OnItemClickListener() {
                @Override
                public <T> void onItemClicked(T item, int position) {
                    Category category = (Category) item;

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Bundle args = new Bundle();
                    if (category.isSub()) {
                        args.putString(ExtraKeyType.EXTRA_KEY_CATEGORY_ID.getValue(), category.getId());
                        intent.putExtra(ExtraKeyType.DEFAULT_EXTRA.getValue(), args);
                        DetailType.LIST_SUB.attachTo(intent);
                    } else {
                        args.putString(ExtraKeyType.EXTRA_KEY_CATEGORY_PATH.getValue(), "category/" + category.getId());
                        intent.putExtra(ExtraKeyType.DEFAULT_EXTRA.getValue(), args);
                        DetailType.LIST_POST.attachTo(intent);
                    }
                    startActivity(intent);
                }
            });
        }

        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return listCategoryAdapter.isHeader(position) ? manager.getSpanCount() : 1;
            }
        });
        rvListCategory.setLayoutManager(manager);
        rvListCategory.setAdapter(listCategoryAdapter);
    }

    private void loadData() {
        Log.w(TAG, "loadData");
        categoryDatabase = FirebaseDatabase.getInstance().getReference("category");
        ValueEventListener categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d(TAG, "dataSnapshot.getValue: " + dataSnapshot.getValue());
                listCategoryAdapter.clearAllItems();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        category.setId(snapshot.getKey());
                        Log.d(TAG, "category: " + category.toString());
                        if (!category.isTopPage()) {
                            listCategoryAdapter.addItem(category);
                        } else {
                            listCategoryAdapter.setCategoryHeader(category);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        categoryDatabase.addListenerForSingleValueEvent(categoryListener);
    }
}