package com.fetel.ltdd.team9.share.recipes.ui.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;

public class ListCategoryHeaderViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView tvTitle;
    public ImageView ivCover;

    public ListCategoryHeaderViewHolder(View paramView) {
        super(paramView);
        view = paramView;
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.INVISIBLE);
        ivCover = view.findViewById(R.id.iv_cover);
    }
}
