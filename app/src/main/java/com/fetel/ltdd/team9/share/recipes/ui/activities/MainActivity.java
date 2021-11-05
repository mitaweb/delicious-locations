package com.fetel.ltdd.team9.share.recipes.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.fetel.ltdd.team9.share.recipes.R;
import com.fetel.ltdd.team9.share.recipes.ui.enums.DetailType;
import com.fetel.ltdd.team9.share.recipes.ui.enums.ExtraKeyType;
import com.fetel.ltdd.team9.share.recipes.ui.fragments.HomeFragment;
import com.fetel.ltdd.team9.share.recipes.ui.fragments.MyLikeFragment;
import com.fetel.ltdd.team9.share.recipes.ui.fragments.MyPostFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int RC_SIGN_IN = 123;
    private static final String EMAIL_TO = "dungsinh11@gmail.com";

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private TextView tvUserDisplayName;
    private TextView tvUserEmail;
    private ImageView ivUserUrl;
    private Button btnSignIn;
    private SearchView menuSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        setUpDrawer();

        setUpNavHeader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        menuSearchView = (SearchView) searchItem.getActionView();
        menuSearchView.setQueryHint(getString(R.string.menu_search));
        menuSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Search onQueryTextSubmit: " + query);
                searching(query, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "Search onQueryTextSubmit: " + newText);
                searching(newText, false);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private void searching(String query, boolean isSubmit) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (fragment instanceof NavHostFragment) {
            Fragment fragmentChild = fragment.getChildFragmentManager().getFragments().get(0);
            if (isSubmit) {
                if (fragmentChild instanceof HomeFragment) {
                    Log.d(TAG, "Search In HomeFragment");

                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    Bundle args = new Bundle();
                    args.putString(ExtraKeyType.EXTRA_KEY_SEARCH_KEY.getValue(), query);
                    intent.putExtra(ExtraKeyType.DEFAULT_EXTRA.getValue(), args);
                    DetailType.SEARCH_ALL_POST.attachTo(intent);
                    startActivity(intent);
                    menuSearchView.setQuery("", false);
                    menuSearchView.clearFocus();
                }
            } else {
                if (fragmentChild instanceof MyLikeFragment) {
                    Log.d(TAG, "Search In MyLikeFragment");
                    ((MyLikeFragment) fragmentChild).search(query);
                }

                if (fragmentChild instanceof MyPostFragment) {
                    Log.d(TAG, "Search In MyPostFragment");
                    ((MyPostFragment) fragmentChild).search(query);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                showUser();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private void setUpDrawer() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_my_likes, R.id.nav_my_posts)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id == R.id.nav_home || id == R.id.nav_my_likes || id == R.id.nav_my_posts) {
                    //This is for maintaining the behavior of the Navigation view
                    NavigationUI.onNavDestinationSelected(menuItem, navController);

                    //This is for closing the drawer after acting on it
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
                if (id == R.id.nav_feedback) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_TO});
                    email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.main_feedback_subject));
                    //need this to prompts email client only
                    email.setType("message/rfc822");

                    startActivity(Intent.createChooser(email, "Choose an Email client: "));
                }
                if (id == R.id.nav_share) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.main_share_app) + getPackageName());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
                if (id == R.id.nav_sign_out) {
                    Log.d(TAG, "Sign Out FireBase");
                    AuthUI.getInstance()
                            .signOut(getApplicationContext())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    showUser();
                                }
                            });
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void setUpNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ivUserUrl = headerView.findViewById(R.id.iv_user_url);
        tvUserDisplayName = headerView.findViewById(R.id.tv_user_display_name);
        tvUserEmail = headerView.findViewById(R.id.tv_email);
        btnSignIn = headerView.findViewById(R.id.btn_sign_in);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setLogo(R.drawable.logo_share_recipe)
                                .setTheme(R.style.Theme_ShareRecipes)
                                .build(),
                        RC_SIGN_IN);
            }
        });
        showUser();
    }

    private void showUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) {
            hideItemMenuWithSignIn();
            tvUserDisplayName.setVisibility(View.GONE);
            tvUserEmail.setVisibility(View.GONE);
            ivUserUrl.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        } else {
            showItemMenuWithUser();
            tvUserDisplayName.setVisibility(View.VISIBLE);
            tvUserEmail.setVisibility(View.VISIBLE);
            ivUserUrl.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            tvUserDisplayName.setText(user.getDisplayName());
            tvUserEmail.setText(user.getEmail());
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(ivUserUrl);
        }
    }

    private void hideItemMenuWithSignIn() {
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.nav_my_likes).setVisible(false);
        navMenu.findItem(R.id.nav_my_posts).setVisible(false);
        navMenu.findItem(R.id.nav_sign_out).setVisible(false);
        NavigationUI.onNavDestinationSelected(navMenu.findItem(R.id.nav_home), navController);
    }

    private void showItemMenuWithUser() {
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.nav_my_likes).setVisible(true);
        navMenu.findItem(R.id.nav_my_posts).setVisible(true);
        navMenu.findItem(R.id.nav_sign_out).setVisible(true);
    }
}