package com.fetel.ltdd.team9.share.recipes.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.adapter.ListTagCategoriesAdapter;
import com.fetel.ltdd.team9.share.recipes.ui.enums.ExtraKeyType;
import com.fetel.ltdd.team9.share.recipes.ui.listener.OnShowSubClickListener;
import com.fetel.ltdd.team9.share.recipes.ui.listener.TagDialogCallback;
import com.fetel.ltdd.team9.share.recipes.ui.model.TagCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SelectTagDialogFragment extends DialogFragment {

    private static final String TAG = SelectTagDialogFragment.class.getSimpleName();

    private RecyclerView rvListCategory;
    private AppCompatButton btnCancel;
    private AppCompatButton btnAdd;

    private TagDialogCallback tagDialogCallback;
    private ListTagCategoriesAdapter listTagCategoriesAdapter;
    private DatabaseReference categoryDatabase;

    private List<TagCategory> listCreateSelected;

    public SelectTagDialogFragment() {
        // Required empty public constructor
    }

    public static SelectTagDialogFragment newInstance(String categoryPath, int position) {
        SelectTagDialogFragment selectTagDialogFragment = new SelectTagDialogFragment();
        Bundle args = new Bundle();
        args.putString(ExtraKeyType.EXTRA_KEY_CATEGORY_PATH.getValue(), categoryPath);
        args.putInt(ExtraKeyType.EXTRA_KEY_CATEGORY_POSITION.getValue(), position);
        selectTagDialogFragment.setArguments(args);
        return selectTagDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(SelectTagDialogFragment.STYLE_NO_TITLE, R.style.FullScreenStyle);
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_select_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView(view);
        loadData();
    }

    private void setUpView(View root) {
        //RecyclerView
        rvListCategory = root.findViewById(R.id.rv_list_category);
        if (listTagCategoriesAdapter == null) {
            listTagCategoriesAdapter = new ListTagCategoriesAdapter(getActivity(), new OnShowSubClickListener() {
                @Override
                public <T> void onShowSubClicked(T item, int position) {
                    TagCategory tagCategory = (TagCategory) item;
                    SelectTagDialogFragment selectTagDialogFragment = SelectTagDialogFragment.newInstance("/category/" + tagCategory.getId() + "/subList", position);
                    selectTagDialogFragment.setTagDialogCallback(new TagDialogCallback() {
                        @Override
                        public void onTagSelected(List<TagCategory> tagCategories, int position) {
                            listTagCategoriesAdapter.getList().get(position).setSelectedCategories(tagCategories);
                            if (tagCategories.isEmpty()) {
                                listTagCategoriesAdapter.getList().get(position).setCheck(false);
                                listTagCategoriesAdapter.notifyItemChanged(position);
                                return;
                            }
                            listTagCategoriesAdapter.getList().get(position).setCheck(true);
                            listTagCategoriesAdapter.notifyItemChanged(position);
                        }
                    });
                    selectTagDialogFragment.setListCreateSelected(tagCategory.getSelectedCategories());
                    selectTagDialogFragment.show(getChildFragmentManager(), SelectTagDialogFragment.class.getSimpleName());
                }
            });
        }
        rvListCategory.setAdapter(listTagCategoriesAdapter);

        //Button Controller
        btnCancel = root.findViewById(R.id.btn_cancel);
        btnAdd = root.findViewById(R.id.btn_add);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (getArguments() != null) {
                    int position = getArguments().getInt(ExtraKeyType.EXTRA_KEY_CATEGORY_POSITION.getValue(), -1);
                    if (position == -1) {
                        return;
                    }
                    List<TagCategory> listSelected = listTagCategoriesAdapter.getListSelected();
                    if (tagDialogCallback != null) {
                        tagDialogCallback.onTagSelected(listSelected, position);
                    }
                }
            }
        });
    }

    private void loadData() {
        Log.w(TAG, "loadData");
        if (getArguments() != null) {
            String categoryPath = getArguments().getString(ExtraKeyType.EXTRA_KEY_CATEGORY_PATH.getValue());
            if (TextUtils.isEmpty(categoryPath)) {
                return;
            }

            categoryDatabase = FirebaseDatabase.getInstance().getReference().child(categoryPath);
            ValueEventListener categoryListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Log.d(TAG, "dataSnapshot.getValue: " + dataSnapshot.getValue());
                    listTagCategoriesAdapter.clearAllItems();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TagCategory tagCategory = snapshot.getValue(TagCategory.class);
                        if (tagCategory != null) {
                            tagCategory.setId(snapshot.getKey());
                            TagCategory tagCategoryExistsInside = isExistsInside(tagCategory.getId());
                            if (tagCategoryExistsInside != null) {
                                tagCategory.setCheck(tagCategoryExistsInside.isCheck());
                                tagCategory.setSelectedCategories(tagCategoryExistsInside.getSelectedCategories());
                            }
                            Log.d(TAG, "tagCategory: " + tagCategory.toString());
                            listTagCategoriesAdapter.addItem(tagCategory);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            categoryDatabase.addListenerForSingleValueEvent(categoryListener);
        }
    }

    private TagCategory isExistsInside(String id) {
        if (listCreateSelected == null || listCreateSelected.isEmpty()) {
            return null;
        }
        for (TagCategory tagCategory : listCreateSelected) {
            if (tagCategory.getId().equalsIgnoreCase(id)) {
                return tagCategory;
            }
        }
        return null;
    }

    public void setTagDialogCallback(TagDialogCallback tagDialogCallback) {
        this.tagDialogCallback = tagDialogCallback;
    }

    public void setListCreateSelected(List<TagCategory> listCreateSelected) {
        this.listCreateSelected = listCreateSelected;
    }
}
