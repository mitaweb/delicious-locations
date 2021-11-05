package com.fetel.ltdd.team9.share.recipes.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.adapter.viewholder.ListPostViewHolder;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnItemClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnItemDeleteClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ListPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ListPostAdapter.class.getSimpleName();

    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemDeleteClickListener onItemDeleteClickListener;
    private List<Post> postList;
    private boolean isDeleteEnable;

    public ListPostAdapter(Context context, OnItemClickListener onItemClickListener, boolean isDeleteEnable) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.isDeleteEnable = isDeleteEnable;
        this.postList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        return new ListPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mHolder, int position) {
        Post post = postList.get(position);
        if (post == null) {
            return;
        }
        ListPostViewHolder holder = (ListPostViewHolder) mHolder;

        holder.tvTitle.setText(post.getTitle());
        holder.tvDescription.setText(post.getDescription());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!isDeleteEnable || user == null || user.isAnonymous()) {
            holder.ivDelete.setVisibility(View.GONE);
        } else {
            holder.ivDelete.setVisibility(View.VISIBLE);
        }
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemDeleteClickListener != null) {
                    onItemDeleteClickListener.onItemClicked(post, position);
                }
            }
        });

        Glide.with(context)
                .load(post.getUrl())
                .placeholder(R.drawable.place_holder_image)
                .error(R.drawable.place_holder_image)
                .centerCrop()
                .into(holder.ivUrl);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(post, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.postList != null) {
            return this.postList.size();
        }
        return 0;
    }

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener onItemDeleteClickListener) {
        this.onItemDeleteClickListener = onItemDeleteClickListener;
    }

    public void addItem(Post post) {
        this.postList.add(post);
        notifyItemInserted(this.postList.size() - 1);
        notifyItemChanged(this.postList.size() - 1);
    }

    public void setItem(Post post, int position) {
        if (position > -1 && position < postList.size()) {
            postList.set(position, post);
            notifyItemChanged(position);
        }
    }

    public void addAllItems(List<Post> list) {
        this.postList.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreData(List<Post> list) {
        this.postList.addAll(this.postList.size(), list);
        notifyItemRangeChanged(this.postList.size(), list.size());
    }

    public void removeItem(int position) {
        if (position > -1) {
            postList.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    public void removeItem(Post post) {
        removeItem(postList.indexOf(post));
    }

    public void setList(List<Post> list) {
        this.postList.clear();
        addAllItems(list);
        notifyDataSetChanged();
    }

    public List<Post> getList() {
        return postList;
    }

    public void clearAllItems() {
        this.postList.clear();
        notifyDataSetChanged();
    }

    public boolean isExistInList(Post post) {
        if (postList.contains(post)) {
            Log.d(TAG, "item exist");
            return true;
        } else {
            Log.d(TAG, "item not found");
            return false;
        }
    }
}
