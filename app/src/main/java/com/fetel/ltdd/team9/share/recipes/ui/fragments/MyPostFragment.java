package com.fetel.ltdd.team9.share.recipes.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.adapter.ListPostAdapter;
import com.fetel.ltdd.team9.share.recipes.ui.dialogs.AddOrEditPostDialogFragment;
import com.fetel.ltdd.team9.share.recipes.ui.listener.AddOrEditDialogCallback;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnItemClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnItemDeleteClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPostFragment extends Fragment {

    private static final String TAG = MyPostFragment.class.getSimpleName();

    private RecyclerView rvListMyPost;
    private FloatingActionButton fabAddPost;

    private ListPostAdapter listPostAdapter;
    private DatabaseReference postDatabase;
    private ValueEventListener myPostListener;
    private List<Post> listAllPost;

    public MyPostFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_post, container, false);
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
                    AddOrEditPostDialogFragment editPostDialogFragment = AddOrEditPostDialogFragment.newInstance();
                    editPostDialogFragment.setPostEdit(post);
                    editPostDialogFragment.setAddOrEditDialogCallback(new AddOrEditDialogCallback() {
                        @Override
                        public void onDone() {
                            loadData();
                        }
                    });
                    editPostDialogFragment.show(getChildFragmentManager(), AddOrEditPostDialogFragment.class.getSimpleName());
                }
            }, true);
            listPostAdapter.setOnItemDeleteClickListener(new OnItemDeleteClickListener() {
                @Override
                public <T> void onItemClicked(T item, int position) {
                    if (getActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.my_post_delete_title)
                                .setMessage(R.string.my_post_delete_message)
                                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Post post = (Post) item;
                                        postDatabase.child(post.getId()).removeValue();
                                    }
                                })
                                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            });
        }
        rvListMyPost.setAdapter(listPostAdapter);

        //FloatingActionButton
        fabAddPost = root.findViewById(R.id.fab_add_post);
        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddOrEditPostDialogFragment addPostDialogFragment = AddOrEditPostDialogFragment.newInstance();
                addPostDialogFragment.setPostEdit(null);
                addPostDialogFragment.setAddOrEditDialogCallback(new AddOrEditDialogCallback() {
                    @Override
                    public void onDone() {
                        loadData();
                    }
                });
                addPostDialogFragment.show(getChildFragmentManager(), AddOrEditPostDialogFragment.class.getSimpleName());
            }
        });
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
        Query query = postDatabase.orderByChild("userIdCreated").equalTo(user.getUid());
        myPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        query.addValueEventListener(myPostListener);
    }
}