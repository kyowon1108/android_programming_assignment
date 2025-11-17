package com.example.babydiary.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.babydiary.R;
import com.example.babydiary.data.api.AuthApi;
import com.example.babydiary.data.api.UserApi;
import com.example.babydiary.data.dto.MessageResponse;
import com.example.babydiary.data.dto.UpdateNicknameRequest;
import com.example.babydiary.data.dto.UserProfileResponse;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.FragmentProfileBinding;
import com.example.babydiary.ui.auth.LoginActivity;
import com.example.babydiary.utils.AuthUtils;
import com.example.babydiary.utils.DateUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserApi userApi;
    private AuthApi authApi;
    private UserProfileResponse currentProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // API 초기화
        userApi = ApiClient.getClient().create(UserApi.class);
        authApi = ApiClient.getClient().create(AuthApi.class);

        setupViews();
        loadProfile();
    }

    private void setupViews() {
        // SwipeRefreshLayout 설정
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProfile();
            }
        });

        // 닉네임 변경 버튼
        binding.btnUpdateNickname.setOnClickListener(v -> updateNickname());

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    private void loadProfile() {
        // 이미 로딩 중이면 스킵
        if (binding.swipeRefresh.isRefreshing()) {
            return;
        }

        // 프로그래스바가 없을 때만 SwipeRefresh 사용
        if (binding.progressBar != null && binding.progressBar.getVisibility() != View.VISIBLE) {
            binding.swipeRefresh.setRefreshing(true);
        }

        userApi.getProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (binding.progressBar != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    currentProfile = response.body();
                    displayProfile();
                } else if (response.code() == 401) {
                    AuthUtils.handleUnauthorized(requireContext());
                } else {
                    Toast.makeText(getContext(), "프로필 로딩 실패: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                if (binding.progressBar != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayProfile() {
        if (currentProfile == null || !isAdded()) return;

        // 프로필 이미지 (있으면 표시, 없으면 기본 아이콘)
        if (!TextUtils.isEmpty(currentProfile.getProfileImageUrl())) {
            Glide.with(this)
                    .load(currentProfile.getProfileImageUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(binding.ivProfileImage);
        }

        // 닉네임, 이메일
        binding.tvNickname.setText(currentProfile.getNickname());
        binding.tvEmail.setText(currentProfile.getEmail());

        // 통계 정보
        binding.tvCurrentStreak.setText(String.valueOf(currentProfile.getCurrentStreak()));
        binding.tvBestStreak.setText(String.valueOf(currentProfile.getMaxStreak()));

        // 최근 작성일
        String lastWrittenDate = currentProfile.getLastWrittenDate();
        if (!TextUtils.isEmpty(lastWrittenDate)) {
            binding.tvLastDiaryDate.setText(
                DateUtils.serverDateToDisplay(lastWrittenDate)
            );
        } else {
            binding.tvLastDiaryDate.setText("-");
        }

        // 닉네임 입력란에 현재 닉네임 표시
        binding.etNickname.setText(currentProfile.getNickname());
    }

    private void updateNickname() {
        String newNickname = binding.etNickname.getText().toString().trim();

        // 유효성 검사
        if (TextUtils.isEmpty(newNickname)) {
            binding.etNickname.setError("닉네임을 입력해주세요");
            binding.etNickname.requestFocus();
            return;
        }

        // 현재 닉네임과 동일한지 확인
        if (currentProfile != null && newNickname.equals(currentProfile.getNickname())) {
            Toast.makeText(getContext(), "현재 닉네임과 동일합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 로딩 표시
        showLoading(true);

        UpdateNicknameRequest request = new UpdateNicknameRequest(newNickname);
        userApi.updateProfile(request).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    currentProfile = response.body();
                    displayProfile();
                    Toast.makeText(getContext(), "닉네임이 변경되었습니다", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    AuthUtils.handleUnauthorized(requireContext());
                } else {
                    Toast.makeText(getContext(), "닉네임 변경 실패: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("정말로 로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performLogout();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void performLogout() {
        showLoading(true);

        // 로그아웃 API 호출
        authApi.logout().enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                showLoading(false);

                // 성공 여부와 관계없이 토큰 삭제 및 로그인 화면으로 이동
                ApiClient.clearToken();
                Toast.makeText(getContext(), "로그아웃되었습니다", Toast.LENGTH_SHORT).show();
                navigateToLogin();
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                showLoading(false);

                // 네트워크 실패해도 로컬 토큰 삭제하고 로그인으로 이동
                ApiClient.clearToken();
                navigateToLogin();
            }
        });
    }

    private void navigateToLogin() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void showLoading(boolean show) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        binding.btnUpdateNickname.setEnabled(!show);
        binding.btnLogout.setEnabled(!show);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 화면이 다시 보일 때 프로필 새로고침
        loadProfile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
