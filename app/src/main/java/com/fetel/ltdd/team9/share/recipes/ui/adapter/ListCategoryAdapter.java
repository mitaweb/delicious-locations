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
import com.fetel.ltdd.team9.share.recipes.ui.adapter.viewholder.ListCategoryHeaderViewHolder;
import com.fetel.ltdd.team9.share.recipes.ui.adapter.viewholder.ListCategoryViewHolder;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnItemClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ListCategoryAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Category> categoryList;
    private Category categoryHeader;

    public ListCategoryAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.categoryList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
            return new ListCategoryHeaderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new ListCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mHolder, int position) {
        if (mHolder instanceof ListCategoryHeaderViewHolder) {
            if (categoryHeader == null) {
                return;
            }
            ListCategoryHeaderViewHolder holder = (ListCategoryHeaderViewHolder) mHolder;
            holder.tvTitle.setText(categoryHeader.getTitle(context));
            holder.tvTitle.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(categoryHeader.getUrl())
                    .placeholder(R.drawable.place_holder_image)
                    .error(R.drawable.place_holder_image)
                    .centerCrop()
                    .into(holder.ivCover);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClicked(categoryHeader, 0);
                    }
                }
            });
        } else if (mHolder instanceof ListCategoryViewHolder) {
            Category category = categoryList.get(position - 1);
            if (category == null) {
                return;
            }
            ListCategoryViewHolder holder = (ListCategoryViewHolder) mHolder;
            holder.tvTitle.setText(category.getTitle(context));
            Glide.with(context)
                    .load(category.getUrl())
                    .placeholder(R.drawable.place_holder_image)
                    .error(R.drawable.place_holder_image)
                    .centerCrop()
                    .into(holder.ivCover);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClicked(category, position - 1);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (this.categoryList != null) {
            return this.categoryList.size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    public void setCategoryHeader(Category categoryHeader) {
        this.categoryHeader = categoryHeader;
    }

    public void addItem(Category category) {
        this.categoryList.add(category);
        notifyItemInserted(this.categoryList.size() - 1);
        notifyItemChanged(this.categoryList.size() - 1);
    }

    public void setItem(Category category, int position) {
        if (position > -1 && position < categoryList.size()) {
            categoryList.set(position, category);
            notifyItemChanged(position);
        }
    }

    public void addAllItems(List<Category> list) {
        this.categoryList.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreData(List<Category> list) {
        this.categoryList.addAll(this.categoryList.size(), list);
        notifyItemRangeChanged(this.categoryList.size(), list.size());
    }

    public void removeItem(int position) {
        if (position > -1) {
            categoryList.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    public void removeItem(Category category) {
        removeItem(categoryList.indexOf(category));
    }

    public void setList(List<Category> list) {
        this.categoryList.clear();
        addAllItems(list);
        notifyDataSetChanged();
    }

    public List<Category> getList() {
        return categoryList;
    }

    public void clearAllItems() {
        this.categoryList.clear();
        notifyDataSetChanged();
    }

    public boolean isExistInList(Category category) {
        if (categoryList.contains(category)) {
            Log.d(TAG, "item exist");
            return true;
        } else {
            Log.d(TAG, "item not found");
            return false;
        }
    }
}
