package com.example.babydiary.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.babydiary.R;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.ActivityMainBinding;
import com.example.babydiary.ui.auth.LoginActivity;
import com.example.babydiary.ui.diary.CreateDiaryActivity;
import com.example.babydiary.ui.diary.DiaryListFragment;
import com.example.babydiary.ui.profile.ProfileFragment;
import com.example.babydiary.ui.search.SearchFragment;
import com.example.babydiary.ui.weekly.WeeklyDiaryListFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment activeFragment;

    // Fragment 태그
    private static final String TAG_DIARY_LIST = "diary_list";
    private static final String TAG_WEEKLY = "weekly";
    private static final String TAG_SEARCH = "search";
    private static final String TAG_PROFILE = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // 토큰 확인
            if (!ApiClient.hasToken()) {
                navigateToLogin();
                return;
            }

            setupViews();

            // 기본 Fragment 표시 (홈)
            if (savedInstanceState == null) {
                loadFragment(new DiaryListFragment(), TAG_DIARY_LIST);
                binding.bottomNavigation.getMenu()
                        .findItem(R.id.navigation_home)
                        .setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 에러 발생 시 로그인 화면으로
            navigateToLogin();
        }
    }

    private void setupViews() {
        // Toolbar 설정
        setSupportActionBar(binding.toolbar);

        // FAB 클릭 - 다이어리 작성
        binding.fabCreateDiary.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateDiaryActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation 설정
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String tag = null;

                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    fragment = new DiaryListFragment();
                    tag = TAG_DIARY_LIST;
                } else if (itemId == R.id.navigation_weekly) {
                    fragment = new WeeklyDiaryListFragment();
                    tag = TAG_WEEKLY;
                } else if (itemId == R.id.navigation_search) {
                    fragment = new SearchFragment();
                    tag = TAG_SEARCH;
                } else if (itemId == R.id.navigation_profile) {
                    fragment = new ProfileFragment();
                    tag = TAG_PROFILE;
                }

                if (fragment != null) {
                    loadFragment(fragment, tag);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        // 이미 같은 Fragment가 표시되어 있으면 스킵
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (existingFragment != null && existingFragment.isVisible()) {
            updateTitle(tag);
            updateFabForTag(tag);
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 애니메이션 추가
        transaction.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        );

        transaction.replace(R.id.fragmentContainer, fragment, tag);
        transaction.commit();

        activeFragment = fragment;

        // 타이틀 업데이트
        updateTitle(tag);
        updateFabForTag(tag);
    }

    private void updateTitle(String tag) {
        String title = "Baby Diary";
        switch (tag) {
            case TAG_DIARY_LIST:
                title = "일기";
                break;
            case TAG_WEEKLY:
                title = "주간 요약";
                break;
            case TAG_SEARCH:
                title = "검색";
                break;
            case TAG_PROFILE:
                title = "프로필";
                break;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void updateFabForTag(String tag) {
        if (TAG_DIARY_LIST.equals(tag)) {
            binding.fabCreateDiary.setVisibility(View.VISIBLE);
        } else {
            binding.fabCreateDiary.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 나중에 필요한 경우 메뉴 추가
        return true;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void refreshDiaryList() {
        // DiaryListFragment를 새로고침하는 메서드
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_DIARY_LIST);
        if (fragment instanceof DiaryListFragment) {
            ((DiaryListFragment) fragment).refreshData();
        }
    }
}
