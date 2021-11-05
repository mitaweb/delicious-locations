package com.fetel.ltdd.team9.share.recipes.ui.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;

public class ListPostViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView tvTitle;
    public TextView tvDescription;
    public ImageView ivUrl;
    public ImageView ivDelete;

    public ListPostViewHolder(View paramView) {
        super(paramView);
        view = paramView;
        tvTitle = view.findViewById(R.id.tv_title);
        tvDescription = view.findViewById(R.id.tv_description);
        ivUrl = view.findViewById(R.id.iv_url);
        ivDelete = view.findViewById(R.id.iv_delete);
    }
}
