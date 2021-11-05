package com.fetel.ltdd.team9.share.recipes.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.activities.DetailActivity;
import com.fetel.ltdd.team9.share.recipes.ui.activities.MainActivity;
import com.fetel.ltdd.team9.share.recipes.ui.enums.ExtraKeyType;
import com.fetel.ltdd.team9.share.recipes.ui.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailPostFragment extends Fragment {

    private static final String TAG = DetailPostFragment.class.getSimpleName();

    private ImageView ivBack;
    private TextView tvTitle;
    private ImageView ivHome;
    private LinearLayout llShare;
    private LinearLayout llLike;
    private ImageView ivLike;
    private TextView tvPostTitle;
    private TextView tvTotalRating;
    private TextView tvPostDescription;
    private ImageView ivPoster;
    private TextView tvFoodIngredients;
    private TextView tvCookingRecipe;
    private AppCompatRatingBar ratingPost;

    private DatabaseReference postDatabase;
    private ValueEventListener postListener;

    private DatabaseReference postRatingDatabase;
    private ValueEventListener postRatingListener;
    private Post currentPost;

    private boolean isLike;

    public DetailPostFragment() {
        // Required empty public constructor
    }

    public static DetailPostFragment newInstance(String postId) {
        DetailPostFragment fragment = new DetailPostFragment();
        Bundle args = new Bundle();
        args.putString(ExtraKeyType.EXTRA_KEY_POST_ID.getValue(), postId);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_post, container, false);
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
        if (postListener != null) {
            postDatabase.removeEventListener(postListener);
        }
        if (postRatingListener != null) {
            postRatingDatabase.removeEventListener(postRatingListener);
        }
    }

    private void setUpView(View root) {
        //Title And Back Button, Home button
        ivBack = root.findViewById(R.id.iv_back);
        tvTitle = root.findViewById(R.id.tv_title);
        ivHome = root.findViewById(R.id.iv_home);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null && getActivity() instanceof DetailActivity) {
                    getActivity().onBackPressed();
                }
            }
        });
        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // Post Controller
        llShare = root.findViewById(R.id.ll_post_share);
        llLike = root.findViewById(R.id.ll_post_like);
        ivLike = root.findViewById(R.id.iv_post_like);
        ratingPost = root.findViewById(R.id.rating_post);
        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPost == null) {
                    return;
                }
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getString(R.string.detail_post_share_title_post)).append(currentPost.getTitle()).append("\n")
                        .append(getString(R.string.detail_post_share_title_download)).append(getActivity().getPackageName());
                sendIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postDatabase == null) {
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null || user.isAnonymous()) {
                    return;
                }
                HashMap<String, Object> updateLike = new HashMap<>();
                if (currentPost.getLikes() == null) {
                    currentPost.setLikes(new ArrayList<>());
                }
                if (isLike) {
                    currentPost.getLikes().remove(user.getUid());
                } else {
                    currentPost.getLikes().add(user.getUid());
                }
                updateLike.put("likes", currentPost.getLikes());
                postDatabase.updateChildren(updateLike);
            }
        });
        ratingPost.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null || user.isAnonymous()) {
                    return;
                }
                if (fromUser) {
                    postRatingDatabase.child(user.getUid()).setValue(rating);
                }
            }
        });

        //Post Info
        tvPostTitle = root.findViewById(R.id.tv_post_title);
        tvTotalRating = root.findViewById(R.id.tv_post_total_rating);
        tvPostDescription = root.findViewById(R.id.tv_post_description);
        ivPoster = root.findViewById(R.id.iv_poster);
        tvFoodIngredients = root.findViewById(R.id.tv_food_ingredients);
        tvCookingRecipe = root.findViewById(R.id.tv_cooking_recipe);
    }

    private void loadData() {
        Log.w(TAG, "loadData");
        if (getArguments() != null) {
            String postId = getArguments().getString(ExtraKeyType.EXTRA_KEY_POST_ID.getValue());
            if (TextUtils.isEmpty(postId)) {
                return;
            }
            loadPost(postId);
        }
    }

    private void loadPost(String postId) {
        postDatabase = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        if (postListener != null) {
            postDatabase.removeEventListener(postListener);
        }
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                if (post != null) {
                    post.setId(dataSnapshot.getKey());
                    updateView(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        postDatabase.addValueEventListener(postListener);

        //Get List Rating
        postRatingDatabase = FirebaseDatabase.getInstance().getReference("ratings").child(postId);
        if (postRatingListener != null) {
            postRatingDatabase.removeEventListener(postRatingListener);
        }
        postRatingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                float totalRating = 0;
                float TotalRating = 0;
                int numberRating = 0;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue() != null) {
                        float rating = Float.parseFloat(snapshot.getValue().toString());
                        numberRating++;
                        TotalRating += rating;
                        totalRating = TotalRating/numberRating;
                        if (user != null && !user.isAnonymous() && user.getUid().equalsIgnoreCase(snapshot.getKey())) {
                            ratingPost.setRating(rating);
                        }
                    }
                }

                tvTotalRating.setText(String.format(numberRating == 1 ? getString(R.string.detail_post_1_rating_post) : getString(R.string.detail_post_rating_post), totalRating, numberRating));

                Log.d(TAG, "Rating - totalRating: " + totalRating + " - numberRating: " + numberRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        postRatingDatabase.addValueEventListener(postRatingListener);
    }

    private void updateView(Post post) {
        if (post == null) {
            return;
        }
        currentPost = post;
        tvTitle.setText(post.getTitle());
        tvPostTitle.setText(post.getTitle());
        tvPostDescription.setText(post.getDescription());

        Glide.with(this)
                .load(post.getUrl())
                .placeholder(R.drawable.place_holder_image)
                .error(R.drawable.place_holder_image)
                .centerCrop()
                .into(ivPoster);

        tvFoodIngredients.setText(post.getFoodIngredients());
        tvCookingRecipe.setText(post.getCookingRecipe());
        updateLikeAndRatingButton();
    }

    private void updateLikeAndRatingButton() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.isAnonymous()) {
            if (isUserLike(user.getUid())) {
                Glide.with(this)
                        .load(R.drawable.ic_likes)
                        .into(ivLike);
                isLike = true;
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_not_likes)
                        .into(ivLike);
                isLike = false;
            }
            ratingPost.setVisibility(View.VISIBLE);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_not_likes)
                    .into(ivLike);
            isLike = false;
            ratingPost.setVisibility(View.GONE);
        }
    }

    private boolean isUserLike(String currentUserId) {
        if (currentPost.getLikes() == null) {
            return false;
        }
        for (String userId : currentPost.getLikes()) {
            if (currentUserId.equalsIgnoreCase(userId)) {
                return true;
            }
        }
        return false;
    }
}