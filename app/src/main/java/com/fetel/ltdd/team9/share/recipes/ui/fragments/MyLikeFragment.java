package com.fetel.ltdd.team9.share.recipes.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.activities.DetailActivity;
import com.fetel.ltdd.team9.share.recipes.ui.adapter.ListPostAdapter;
import com.fetel.ltdd.team9.share.recipes.ui.enums.DetailType;
import com.fetel.ltdd.team9.share.recipes.ui.enums.ExtraKeyType;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnItemClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyLikeFragment extends Fragment {

    private static final String TAG = MyLikeFragment.class.getSimpleName();

    private RecyclerView rvListMyPost;

    private ListPostAdapter listPostAdapter;
    private DatabaseReference postDatabase;
    private ValueEventListener myPostListener;
    private List<Post> listAllPost;

    public MyLikeFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_like, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myPostListener != null) {
            postDatabase.removeEventListener(myPostListener);
        }
    }

    private void setUpView(View root) {
        //RecyclerView
        rvListMyPost = root.findViewById(R.id.rv_list_my_post);
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
        rvListMyPost.setAdapter(listPostAdapter);
    }

    public void search(String query) {
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

    private void loadData() {
        Log.w(TAG, "loadData");
        if (listAllPost == null) {
            listAllPost = new ArrayList<>();
        } else {
            listAllPost.clear();
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) {
            return;
        }
        postDatabase = FirebaseDatabase.getInstance().getReference("posts");
        if (myPostListener != null) {
            postDatabase.removeEventListener(myPostListener);
        }
        myPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                listAllPost.clear();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null || user.isAnonymous()) {
                    return;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        post.setId(snapshot.getKey());
                        if (isUserLike(post.getLikes(), user.getUid())) {
                            listAllPost.add(post);
                        }
                    }
                }
                listPostAdapter.setList(listAllPost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        postDatabase.addValueEventListener(myPostListener);
    }

    private boolean isUserLike(List<String> listLike, String currentUserId) {
        if (listLike == null) {
            return false;
        }
        for (String userId : listLike) {
            if (currentUserId.equalsIgnoreCase(userId)) {
                return true;
            }
        }
        return false;
    }
}