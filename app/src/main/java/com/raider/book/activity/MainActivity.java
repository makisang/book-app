package com.raider.book.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.raider.book.R;
import com.raider.book.dao.LocalBook;
import com.raider.book.fragment.MainFragment;
import com.raider.book.mvp.model.MainModel;
import com.raider.book.mvp.presenter.MainPresenter;
import com.raider.book.utils.CustomViewUtils;
import com.raider.book.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_BOOK_IMPORT = 1;
    private static final String EXTRA_ADDED_BOOKS = "added_books";
    private static final int REVEAL_DURATION = 300;
    private static final int ROTATION_DURATION = 400;

    private int activity_code;
    private static final int SD_IMPORT = 1;
    private static final int BT_IMPORT = 2;

    private String[] fragmentTags = {"MainFragment"};

    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private View visualLayout;
    private boolean mShowVisualToolbar = false;
    private Toolbar visualToolbar;
    private FloatingActionButton fab;
    private MainPresenter mPresenter;
    private boolean isFabOpen;
    private FrameLayout importLayout;
    private Animator mCircularReveal;
    private FrameLayout importBG;
    private int accentColor;

    @SuppressWarnings("unchecked")
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    public static void back(Activity activity, ArrayList<LocalBook> addedBooks) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_ADDED_BOOKS, addedBooks);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        customDrawer();
        customNavigationView();

        MainFragment mFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (mFragment == null) {
            mFragment = MainFragment.newInstance();
            Utils.addFragmentToActivity(getSupportFragmentManager(), mFragment, R.id.frame_container, fragmentTags[0]);
        }

        // Create presenter.
        mPresenter = new MainPresenter(mFragment, new MainModel(this));
    }

    private void initViews() {
        accentColor = getResources().getColor(R.color.teal_accent_pink);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.my_coordinator);
        visualLayout = findViewById(R.id.visual_layout);
        visualToolbar = (Toolbar) findViewById(R.id.visual_toolbar);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        // Init import menu.
        importLayout = (FrameLayout) findViewById(R.id.import_layout);
        importLayout.setVisibility(View.GONE);
        View sdImport = findViewById(R.id.sd_import);
        View btImport = findViewById(R.id.bt_import);
        importBG = (FrameLayout) findViewById(R.id.import_bg);
        // Intercept touch event.
        importBG.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isFabOpen = false;
                        revealHideLayout(fab, importLayout);
                        handleBG(false);
                }
                return true;
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.my_fab);
        fab.setOnClickListener(this);
        importLayout.requestDisallowInterceptTouchEvent(true);
        sdImport.setOnClickListener(this);
        btImport.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_fab:
                clickFab();
                break;
            case R.id.sd_import:
                clickImportOption(SD_IMPORT);
                break;
            case R.id.bt_import:
                clickImportOption(BT_IMPORT);
                break;
        }
    }

    private void customDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (isFabOpen) closeFab();
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
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
        navigation.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.online_books:
                OnlineActivity.start(MainActivity.this);
                break;
        }
        return false;
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
                mPresenter.deleteSelected();
                return true;
            case R.id.action_select_all:
                mPresenter.selectAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BOOK_IMPORT && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<LocalBook> addedBooks = data.getParcelableArrayListExtra(EXTRA_ADDED_BOOKS);

            // Show success SnackBar.
            String hint = String.format(getResources().getString(R.string.hint_add_book_success), addedBooks.size());
            Snackbar.make(coordinatorLayout, hint, Snackbar.LENGTH_SHORT).show();

            mPresenter.addBooks(addedBooks);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (isFabOpen) {
            closeFab();
        } else {
            super.onBackPressed();
        }
    }

    private void clickFab() {
        if (!isFabOpen) {
            openFab();
        }
    }

    private void clickImportOption(int flag) {
        activity_code = flag;
        closeFab();
    }

    private void openFab() {
        isFabOpen = true;
        handleBG(true);
        if (Utils.isOrAfterLollipop()) {
            revealShowLayout(fab, importLayout);
        } else {
            fab.setVisibility(View.GONE);
            importLayout.setVisibility(View.VISIBLE);
        }
    }

    private void closeFab() {
        isFabOpen = false;
        handleBG(false);
        if (Utils.isOrAfterLollipop()) {
            revealHideLayout(fab, importLayout);
        } else {
            importLayout.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    private void revealShowLayout(final View clickedView, final FrameLayout layoutContainer) {
        prepareShowReveal(clickedView, layoutContainer);
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(clickedView, "rotation", 0, 90);
        rotationAnimator.setDuration(ROTATION_DURATION - 100);
        rotationAnimator.setInterpolator(new DecelerateInterpolator());
        rotationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                clickedView.setVisibility(View.GONE);
            }
        });

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator colorChange = ObjectAnimator.ofInt(layoutContainer, CustomViewUtils.FOREGROUND_COLOR, accentColor, Color.TRANSPARENT);
        colorChange.setEvaluator(new ArgbEvaluator());

        mCircularReveal.setStartDelay(150);
        colorChange.setStartDelay(150);
        set.play(rotationAnimator).with(mCircularReveal).with(colorChange);
        set.start();
    }

    @TargetApi(21)
    private void prepareShowReveal(View startView, final FrameLayout targetView) {
        int centerX = targetView.getWidth() - startView.getWidth() / 2;
        int centerY = targetView.getHeight() - startView.getHeight() / 2;
        float endRadius = (float) Math.hypot(centerX, centerY);

        mCircularReveal = ViewAnimationUtils.createCircularReveal(targetView, centerX, centerY, startView.getWidth() / 2, endRadius);
        mCircularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                targetView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCircularReveal.removeListener(this);
            }
        });
        mCircularReveal.setDuration(REVEAL_DURATION);
    }


    private void revealHideLayout(final View fab, final FrameLayout layoutContainer) {
        prepareHideReveal(layoutContainer, fab);

        final ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(fab, "rotation", 90, 0);
        rotationAnimator.setDuration(ROTATION_DURATION);
        rotationAnimator.setInterpolator(new FastOutSlowInInterpolator());
        rotationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Jump to SDImportActivity.
                toOtherActivity();
            }
        });

        mCircularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCircularReveal.removeListener(this);
                layoutContainer.setVisibility(View.GONE);
            }
        });

        int color = 0xBB000000 | accentColor;
        ObjectAnimator colorChange = ObjectAnimator.ofInt(importLayout, CustomViewUtils.FOREGROUND_COLOR, Color.TRANSPARENT, color);
        colorChange.setEvaluator(new ArgbEvaluator());

        AnimatorSet set = new AnimatorSet();
        rotationAnimator.setStartDelay(REVEAL_DURATION - 100);
        set.play(mCircularReveal).with(colorChange).with(rotationAnimator);
        set.start();
    }

    @TargetApi(21)
    private void prepareHideReveal(final FrameLayout startView, View targetView) {
        int centerX = startView.getWidth() - targetView.getWidth() / 2;
        int centerY = startView.getHeight() - targetView.getHeight() / 2;
        float startRadius = (float) Math.hypot(centerX, centerY);

        mCircularReveal = ViewAnimationUtils.createCircularReveal(startView, centerX, centerY, startRadius, targetView.getWidth() / 2);
        mCircularReveal.setInterpolator(new FastOutLinearInInterpolator());
        mCircularReveal.setDuration(REVEAL_DURATION);
    }

    private void toOtherActivity() {
        switch (activity_code) {
            case SD_IMPORT:
                mPresenter.toImportActivity();
                break;
            case BT_IMPORT:
                break;
        }
        activity_code = 0;
    }

    private void handleBG(boolean show) {
        ObjectAnimator alphaAnimator;
        if (show) {
            importBG.setVisibility(View.VISIBLE);
            alphaAnimator = ObjectAnimator.ofFloat(importBG, "alpha", 0f, 0.8f);
        } else {
            alphaAnimator = ObjectAnimator.ofFloat(importBG, "alpha", 0.8f, 0f);
            alphaAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    importBG.setVisibility(View.GONE);
                }
            });
        }
        alphaAnimator.start();
    }

    /**
     * Show visual ToolBar in select_mode.
     */
    public void showVisualToolBar() {
        // Lock drawer.
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        visualLayout.setVisibility(View.VISIBLE);
        CustomViewUtils.showWithRippleEffect(visualToolbar, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShowVisualToolbar = true;
                setSupportActionBar(visualToolbar);
                visualToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFab();
                        mPresenter.exitSelectMode();
                        hideVisualToolBar();
                    }
                });
            }
        });
    }

    public void hideVisualToolBar() {
        // Unlock drawer.
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        CustomViewUtils.hideWithRippleEffect(visualLayout, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                visualLayout.setVisibility(View.INVISIBLE);
                mShowVisualToolbar = false;
                setSupportActionBar(toolbar);
            }
        });
    }

    public void changeMode(boolean enterSelectMode) {
        if (enterSelectMode) {
            hideFab();
            showVisualToolBar();
        } else {
            showFab();
            hideVisualToolBar();
        }
    }

    public void enableFab() {
        fab.setEnabled(true);
    }

    public void disableFab() {
        fab.setEnabled(false);
    }

    public void showFab() {
        fab.show();
        fab.setEnabled(true);
    }

    public void hideFab() {
        fab.setEnabled(false);
        fab.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawerLayout.removeDrawerListener(drawerToggle);
        mPresenter.onDestroy();
        mPresenter = null;
    }

}
