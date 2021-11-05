package com.fetel.ltdd.team9.share.recipes.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;

import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.listener.AddOrEditDialogCallback;
import com.fetel.ltdd.team9.share.recipes.ui.listener.TagDialogCallback;
import com.fetel.ltdd.team9.share.recipes.ui.model.Post;
import com.fetel.ltdd.team9.share.recipes.ui.model.TagCategory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddOrEditPostDialogFragment extends DialogFragment {

    private static final String TAG = AddOrEditPostDialogFragment.class.getSimpleName();

    private AppCompatEditText etPostTitle;
    private AppCompatEditText etPostDescription;
    private AppCompatEditText etPoster;
    private AppCompatEditText etFoodIngredients;
    private AppCompatEditText etCookingRecipe;
    private ImageView ivSelectTagCategories;

    private ChipGroup chipGroup;

    private AppCompatButton btnCancel;
    private AppCompatButton btnAdd;

    private List<TagCategory> listCreateSelected;
    private Post postEdit;
    private AddOrEditDialogCallback addOrEditDialogCallback;

    private DatabaseReference postDatabase;

    public AddOrEditPostDialogFragment() {
        // Required empty public constructor
    }

    public static AddOrEditPostDialogFragment newInstance() {
        AddOrEditPostDialogFragment addPostDialogFragment = new AddOrEditPostDialogFragment();
        Bundle args = new Bundle();
        addPostDialogFragment.setArguments(args);
        return addPostDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AddOrEditPostDialogFragment.STYLE_NO_TITLE, R.style.FullScreenStyle);
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
        return inflater.inflate(R.layout.dialog_add_or_edit_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView(view);
    }

    public void setPostEdit(Post postEdit) {
        this.postEdit = postEdit;
    }

    public void setAddOrEditDialogCallback(AddOrEditDialogCallback addOrEditDialogCallback) {
        this.addOrEditDialogCallback = addOrEditDialogCallback;
    }

    private void setUpView(View root) {
        // Data
        etPostTitle = root.findViewById(R.id.et_post_title);
        etPostDescription = root.findViewById(R.id.et_post_description);
        etPoster = root.findViewById(R.id.et_poster);
        etFoodIngredients = root.findViewById(R.id.et_food_ingredients);
        etCookingRecipe = root.findViewById(R.id.et_cooking_recipe);
        ivSelectTagCategories = root.findViewById(R.id.iv_select_tag_categories);

        ivSelectTagCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTagCategory();
            }
        });

        //Tag Category
        chipGroup = root.findViewById(R.id.chip_group);

        //Button Controller
        btnCancel = root.findViewById(R.id.btn_cancel);
        btnAdd = root.findViewById(R.id.btn_add);

        //Check Edit or Add
        if (postEdit == null) {
            btnAdd.setText(getString(R.string.dialog_add_post_add));
        } else {
            etPostTitle.setText(postEdit.getTitle());
            etPostDescription.setText(postEdit.getDescription());
            etPoster.setText(postEdit.getUrl());
            etFoodIngredients.setText(postEdit.getFoodIngredients());
            etCookingRecipe.setText(postEdit.getCookingRecipe());
            btnAdd.setText(getString(R.string.dialog_add_post_update));
            updateListTagCategorySelected();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndAddNewPost();
            }
        });
    }

    private void updateListTagCategorySelected() {
        DatabaseReference categoryDatabase = FirebaseDatabase.getInstance().getReference().child("category");
        ValueEventListener categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (listCreateSelected == null) {
                    listCreateSelected = new ArrayList<>();
                } else {
                    listCreateSelected.clear();
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TagCategory tagCategory = snapshot.getValue(TagCategory.class);
                    if (tagCategory != null) {
                        tagCategory.setId(snapshot.getKey());
                        if (tagCategory.isSub()) {
                            FirebaseDatabase.getInstance().getReference().child("category").child(tagCategory.getId()).child("subList")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            boolean isCheck = false;
                                            List<TagCategory> tagCategoriesSelected = new ArrayList<>();
                                            for (DataSnapshot tagSubCategorySnapshot : snapshot.getChildren()) {
                                                TagCategory tagSubCategory = tagSubCategorySnapshot.getValue(TagCategory.class);
                                                if (tagSubCategory != null) {
                                                    tagSubCategory.setId(tagSubCategorySnapshot.getKey());
                                                    if (isExistsInside(tagSubCategory.getId())) {
                                                        isCheck = true;
                                                        tagSubCategory.setCheck(true);
                                                        tagCategoriesSelected.add(tagSubCategory);
                                                    }
                                                }
                                            }
                                            if (isCheck) {
                                                tagCategory.setCheck(true);
                                                tagCategory.setSelectedCategories(tagCategoriesSelected);
                                                listCreateSelected.add(tagCategory);
                                                showListTagCategories();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                        } else {
                            if (isExistsInside(tagCategory.getId())) {
                                tagCategory.setCheck(true);
                                listCreateSelected.add(tagCategory);
                            }
                        }
                    }
                }
                showListTagCategories();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        categoryDatabase.addListenerForSingleValueEvent(categoryListener);
    }

    private boolean isExistsInside(String id) {
        if (postEdit == null || postEdit.getTagCategories() == null || postEdit.getTagCategories().isEmpty()) {
            return false;
        }
        for (String idInsidePostEdit : postEdit.getTagCategories()) {
            if (idInsidePostEdit.equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private void selectTagCategory() {
        SelectTagDialogFragment selectTagDialogFragment = SelectTagDialogFragment.newInstance("/category", 0);
        selectTagDialogFragment.setTagDialogCallback(new TagDialogCallback() {
            @Override
            public void onTagSelected(List<TagCategory> tagCategories, int position) {
                if (listCreateSelected == null) {
                    listCreateSelected = tagCategories;
                } else {
                    listCreateSelected.clear();
                    listCreateSelected.addAll(tagCategories);
                }
                showListTagCategories();
            }
        });
        selectTagDialogFragment.setListCreateSelected(listCreateSelected);
        selectTagDialogFragment.show(getChildFragmentManager(), SelectTagDialogFragment.class.getSimpleName());
    }

    private void showListTagCategories() {
        chipGroup.removeAllViews();

        if (listCreateSelected == null || listCreateSelected.isEmpty()) {
            return;
        }

        for (TagCategory tagCategory : listCreateSelected) {
            if (tagCategory.isCheck()) {
                if (tagCategory.isSub() && tagCategory.getSelectedCategories() != null && !tagCategory.getSelectedCategories().isEmpty()) {
                    for (TagCategory tagSubCategory : tagCategory.getSelectedCategories()) {
                        if (tagSubCategory.isCheck()) {
                            addNewChipTag(tagSubCategory);
                        }
                    }
                } else {
                    addNewChipTag(tagCategory);
                }
            }
        }
    }

    private void addNewChipTag(TagCategory tagCategory) {
        Chip chip = new Chip(getActivity());
        chip.setText(tagCategory.getTitle(getActivity()));
        chipGroup.addView(chip);
    }

    private void showDialogErrorListTag() {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_add_post_tag_categories_error)
                    .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void checkAndAddNewPost() {
        if (TextUtils.isEmpty(etPostTitle.getText())) {
            etPostTitle.setError(getString(R.string.dialog_add_post_title_error));
            return;
        }
        if (TextUtils.isEmpty(etPostDescription.getText())) {
            etPostDescription.setError(getString(R.string.dialog_add_post_description_error));
            return;
        }
        if (TextUtils.isEmpty(etPoster.getText())) {
            etPoster.setError(getString(R.string.dialog_add_post_poster_error));
            return;
        }
        if (TextUtils.isEmpty(etFoodIngredients.getText())) {
            etFoodIngredients.setError(getString(R.string.dialog_add_post_food_ingredients_error));
            return;
        }
        if (TextUtils.isEmpty(etCookingRecipe.getText())) {
            etCookingRecipe.setError(getString(R.string.dialog_add_post_cooking_recipe_error));
            return;
        }
        if (listCreateSelected == null || listCreateSelected.isEmpty()) {
            showDialogErrorListTag();
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) {
            return;
        }

        List<String> listTagId = new ArrayList<>();

        for (TagCategory tagCategory : listCreateSelected) {
            if (tagCategory.isCheck()) {
                if (tagCategory.isSub() && tagCategory.getSelectedCategories() != null && !tagCategory.getSelectedCategories().isEmpty()) {
                    for (TagCategory tagSubCategory : tagCategory.getSelectedCategories()) {
                        if (tagSubCategory.isCheck()) {
                            listTagId.add(tagSubCategory.getId());
                        }
                    }
                } else {
                    listTagId.add(tagCategory.getId());
                }
            }
        }
        if (listTagId.isEmpty()) {
            showDialogErrorListTag();
            return;
        }

        Post post = new Post(etPostTitle.getText().toString(),
                etPostDescription.getText().toString(),
                etPoster.getText().toString(),
                etFoodIngredients.getText().toString(),
                etCookingRecipe.getText().toString(),
                listTagId,
                user.getUid());
        Map<String, Object> postValues = post.toMap();

        postDatabase = FirebaseDatabase.getInstance().getReference("posts");

        if (postEdit == null) {
            postDatabase.push().setValue(postValues);
        } else {
            postDatabase.child(postEdit.getId()).setValue(postValues);
        }
        dismiss();
    }
}
