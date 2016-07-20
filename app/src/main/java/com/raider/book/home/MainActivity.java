package com.raider.book.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.raider.book.R;
import com.raider.book.entity.BookData;
import com.raider.book.events.DeleteSelected;
import com.raider.book.events.ExitSelectMode;
import com.raider.book.events.InsertBooks;
import com.raider.book.events.SelectAll;
import com.raider.book.online.OnlineActivity;
import com.raider.book.utils.ActivityUtils;
import com.raider.book.utils.CustomAnim;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_BOOK_IMPORT = 1;
    private static final String EXTRA_ADDED_BOOKS = "added_books";

    private int activity_code;
    private static final int ONLINE = 1;

    private String[] fragmentTags = {"ShelfBooksFragment"};

    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private View visualLayout;
    private boolean mShowVisualToolbar = false;
    private Toolbar visualToolbar;

    @SuppressWarnings("unchecked")
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        ActivityCompat.startActivity(activity, intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }

    public static void back(Activity activity, ArrayList<BookData> addedBooks) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_ADDED_BOOKS, addedBooks);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finishAfterTransition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setAllowEnterTransitionOverlap(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.my_coordinator);
        visualLayout = findViewById(R.id.visual_layout);
        visualToolbar = (Toolbar) findViewById(R.id.visual_toolbar);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // set up the DrawerLayout
        customDrawer();
        // init navigation content
        customNavigationView();

        // add mFragment to activity
        ShelfBooksFragment mFragment = (ShelfBooksFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (mFragment == null) {
            mFragment = ShelfBooksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mFragment, R.id.frame_container, fragmentTags[0]);
        }

        // create presenter
        new ShelfBooksPresenter(mFragment, new ShelfBooksModel(this));
    }

    /**
     * Show visual ToolBar in select_mode.
     */
    public void showVisualToolBar() {
        // lock drawer
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        visualLayout.setVisibility(View.VISIBLE);
        CustomAnim.showWithRippleEffect(visualToolbar, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShowVisualToolbar = true;
                setSupportActionBar(visualToolbar);
                visualToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new ExitSelectMode());
                        hideVisualToolBar();
                    }
                });
            }
        });
    }

    public void hideVisualToolBar() {
        // unlock drawer
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        CustomAnim.hideWithRippleEffect(visualLayout, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                visualLayout.setVisibility(View.INVISIBLE);
                mShowVisualToolbar = false;
                setSupportActionBar(toolbar);
            }
        });
    }

    private void customDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (activity_code == 0) return;
                switch (activity_code) {
                    case ONLINE:
                        activity_code = 0;
                        OnlineActivity.start(MainActivity.this);
                        break;
                }
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }


    @SuppressWarnings("all")
    private void customNavigationView() {
        NavigationView navigation = (NavigationView) findViewById(R.id.navigation);
        navigation.inflateHeaderView(R.layout.navigation_header);
        navigation.inflateMenu(R.menu.menu_navigation);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.online_books:
                        /** notify activity jump in {@link DrawerToggle#onDrawerClosed(View)} **/
                        activity_code = ONLINE;
                        // close drawer first
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shelf, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mShowVisualToolbar) {
            getMenuInflater().inflate(R.menu.menu_select_mode, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_shelf, menu);
            // TODO need this to enable drawer open again, why?
            customDrawer();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                EventBus.getDefault().post(new DeleteSelected());
                return true;
            case R.id.action_select_all:
                EventBus.getDefault().post(new SelectAll());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BOOK_IMPORT && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<BookData> addedBooks = data.getParcelableArrayListExtra(EXTRA_ADDED_BOOKS);

            // show SnackBar
            String hint = String.format(getResources().getString(R.string.hint_add_book_success), addedBooks.size());
            Snackbar.make(coordinatorLayout, hint, Snackbar.LENGTH_SHORT).show();

            /** {@link ShelfBooksPresenter#insertInFragment(InsertBooks)}  **/
            EventBus.getDefault().post(new InsertBooks(addedBooks));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawerLayout.removeDrawerListener(drawerToggle);
    }

}
