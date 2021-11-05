package com.fetel.ltdd.team9.share.recipes.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.adapter.viewholder.ListTagCategoryViewHolder;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnShowSubClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.model.TagCategory;

import java.util.ArrayList;
import java.util.List;

public class ListTagCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ListTagCategoriesAdapter.class.getSimpleName();

    private Context context;
    private OnShowSubClickListener onShowSubClickListener;
    private List<TagCategory> tagCategories;

    public ListTagCategoriesAdapter(Context context, OnShowSubClickListener onShowSubClickListener) {
        this.context = context;
        this.onShowSubClickListener = onShowSubClickListener;
        this.tagCategories = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tag_categories, parent, false);
        return new ListTagCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mHolder, int position) {
        TagCategory tagCategory = tagCategories.get(position);
        if (tagCategory == null) {
            return;
        }
        ListTagCategoryViewHolder holder = (ListTagCategoryViewHolder) mHolder;
        if (tagCategory.isSub() && tagCategory.getSelectedCategories() != null && !tagCategory.getSelectedCategories().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(tagCategory.getTitle(context)).append("\n");
            for (int index = 0; index < tagCategory.getSelectedCategories().size(); index++) {
                TagCategory tagCategorySub = tagCategory.getSelectedCategories().get(index);
                char bulletSymbol = '\u2023';
                stringBuilder.append("\t").append(bulletSymbol).append("\t").append(tagCategorySub.getTitle(context));
                if (index < tagCategory.getSelectedCategories().size() - 1) {
                    stringBuilder.append("\n");
                }
            }
            holder.tvTitle.setText(stringBuilder.toString());
        } else {
            holder.tvTitle.setText(tagCategory.getTitle(context));
        }

        if (tagCategory.isSub() && !tagCategory.isCheck()) {
            holder.cbSelected.setVisibility(View.GONE);
            holder.ivHaveSub.setVisibility(View.VISIBLE);
        } else {
            holder.cbSelected.setClickable(!tagCategory.isSub());
            holder.cbSelected.setVisibility(View.VISIBLE);
            holder.ivHaveSub.setVisibility(View.GONE);
        }

        holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean hasCheck) {
                if (!tagCategory.isSub()) {
                    tagCategory.setCheck(hasCheck);
                }
            }
        });

        holder.cbSelected.setChecked(tagCategory.isCheck());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tagCategory.isSub()) {
                    if (onShowSubClickListener != null) {
                        onShowSubClickListener.onShowSubClicked(tagCategory, position);
                    }
                } else {
                    holder.cbSelected.setChecked(!holder.cbSelected.isChecked());
                }
            }
        });
    }

    public List<TagCategory> getListSelected() {
        List<TagCategory> listResult = new ArrayList<>();
        for (TagCategory tagCategory : tagCategories) {
            if (tagCategory.isCheck()) {
                listResult.add(tagCategory);
            }
        }
        return listResult;
    }

    @Override
    public int getItemCount() {
        if (this.tagCategories != null) {
            return this.tagCategories.size();
        }
        return 0;
    }

    public void addItem(TagCategory tagCategory) {
        this.tagCategories.add(tagCategory);
        notifyItemInserted(this.tagCategories.size() - 1);
        notifyItemChanged(this.tagCategories.size() - 1);
    }

    public void setItem(TagCategory tagCategory, int position) {
        if (position > -1 && position < tagCategories.size()) {
            tagCategories.set(position, tagCategory);
            notifyItemChanged(position);
        }
    }

    public void addAllItems(List<TagCategory> list) {
        this.tagCategories.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreData(List<TagCategory> list) {
        this.tagCategories.addAll(this.tagCategories.size(), list);
        notifyItemRangeChanged(this.tagCategories.size(), list.size());
    }

    public void removeItem(int position) {
        if (position > -1) {
            tagCategories.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    public void removeItem(TagCategory tagCategory) {
        removeItem(tagCategories.indexOf(tagCategory));
    }

    public void setList(List<TagCategory> list) {
        this.tagCategories.clear();
        addAllItems(list);
        notifyDataSetChanged();
    }

    public List<TagCategory> getList() {
        return tagCategories;
    }

    public void clearAllItems() {
        this.tagCategories.clear();
        notifyDataSetChanged();
    }

    public boolean isExistInList(TagCategory tagCategory) {
        if (tagCategories.contains(tagCategory)) {
            Log.d(TAG, "item exist");
            return true;
        } else {
            Log.d(TAG, "item not found");
            return false;
        }
    }
}
