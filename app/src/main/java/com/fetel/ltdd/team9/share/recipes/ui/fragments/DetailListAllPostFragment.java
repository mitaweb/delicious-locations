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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.activities.DetailActivity;
import com.fetel.ltdd.team9.share.recipes.ui.adapter.ListPostAdapter;
import com.fetel.ltdd.team9.share.recipes.ui.enums.DetailType;
import com.fetel.ltdd.team9.share.recipes.ui.enums.ExtraKeyType;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnItemClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailListAllPostFragment extends Fragment {

    private static final String TAG = DetailListAllPostFragment.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvListPost;
    private SearchView svPost;
    private ListPostAdapter listPostAdapter;
    private DatabaseReference postDatabase;
    private List<Post> listAllPost;
    private boolean isFirst;

    public DetailListAllPostFragment() {
        // Required empty public constructor
    }

    public static DetailListAllPostFragment newInstance(String searchKey) {
        DetailListAllPostFragment fragment = new DetailListAllPostFragment();
        Bundle args = new Bundle();
        args.putString(ExtraKeyType.EXTRA_KEY_SEARCH_KEY.getValue(), searchKey);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_list_post, container, false);
        isFirst = true;
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
        rvListPost = root.findViewById(R.id.rv_list_post);
        if (listPostAdapter == null) {
            listPostAdapter = new ListPostAdapter(getActivity(), new OnItemClickListener() {
                @Override
                public <T> void onItemClicked(T item, int position) {
                    Post post = (Post) item;

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Bundle args = new Bundle();
                    args.putString(ExtraKeyType.EXTRA_KEY_POST_ID.getValue(), post.getId());
                    intent.putExtra(ExtraKeyType.DEFAULT_EXTRA.getValue(), args);
                    DetailType.SHARE_RECIPE.attachTo(intent);
                    startActivity(intent);
                }
            }, false);
        }
        rvListPost.setAdapter(listPostAdapter);

        //Title And Back Button
        ivBack = root.findViewById(R.id.iv_back);
        tvTitle = root.findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.app_name));

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null && getActivity() instanceof DetailActivity) {
                    getActivity().onBackPressed();
                }
            }
        });

        //Search
        svPost = root.findViewById(R.id.sv_post);
        svPost.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
    }

    private void loadData() {
        Log.w(TAG, "loadData");
        if (listAllPost == null) {
            listAllPost = new ArrayList<>();
        } else {
            listAllPost.clear();
        }
        loadListPost();
    }

    private void loadListPost() {
        postDatabase = FirebaseDatabase.getInstance().getReference("posts");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                listAllPost.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        post.setId(snapshot.getKey());
                        listAllPost.add(post);
                    }
                }
                listPostAdapter.setList(listAllPost);
                if (isFirst && getArguments() != null) {
                    isFirst = false;
                    svPost.setQuery(getArguments().getString(ExtraKeyType.EXTRA_KEY_SEARCH_KEY.getValue(), ""), false);
                    svPost.requestFocus();
                    svPost.requestFocusFromTouch();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        postDatabase.addListenerForSingleValueEvent(postListener);
    }

    private void search(String query) {
        if (listPostAdapter == null) {
            return;
        }
        if (TextUtils.isEmpty(query)) {
            listPostAdapter.setList(listAllPost);
            return;
        }
        List<Post> filteredList = new ArrayList<>();
        for (Post post : listAllPost) {
            // name match condition. this might differ depending on your requirement
            // here we are looking for name or phone number match
            if (post.getTitle().toLowerCase().contains(query.toLowerCase())
                    || post.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(post);
            }
        }
        listPostAdapter.setList(filteredList);
    }
}