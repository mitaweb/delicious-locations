package com.fetel.ltdd.team9.share.recipes.ui.adapter.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;

public class ListTagCategoryViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView tvTitle;
    public CheckBox cbSelected;
    public ImageView ivHaveSub;

    public ListTagCategoryViewHolder(View paramView) {
        super(paramView);
        view = paramView;
        tvTitle = view.findViewById(R.id.tv_title);
        cbSelected = view.findViewById(R.id.cb_selected);
        ivHaveSub = view.findViewById(R.id.iv_have_sub);
    }
}
