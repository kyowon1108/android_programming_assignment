package com.example.babydiary.ui.diary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.babydiary.data.api.DiaryApi;
import com.example.babydiary.data.api.TagApi;
import com.example.babydiary.data.dto.DiaryResponse;
import com.example.babydiary.data.dto.Tag;
import com.example.babydiary.data.network.ApiClient;
import com.example.babydiary.databinding.ActivityCreateDiaryBinding;
import com.example.babydiary.ui.main.MainActivity;
import com.example.babydiary.utils.AuthUtils;
import com.example.babydiary.utils.DateUtils;
import com.example.babydiary.utils.FileUtils;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateDiaryActivity extends AppCompatActivity {

    private ActivityCreateDiaryBinding binding;
    private DiaryApi diaryApi;
    private TagApi tagApi;

    private Uri selectedPhotoUri = null;
    private Date selectedDate = new Date(); // 기본값: 오늘
    private List<Tag> allTags = new ArrayList<>();
    private List<Integer> selectedTagIds = new ArrayList<>();

    private static final int REQUEST_PERMISSION_CODE = 100;

    // 갤러리 런처
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        selectedPhotoUri = uri;
                        displaySelectedPhoto();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateDiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // API 초기화
        diaryApi = ApiClient.getClient().create(DiaryApi.class);
        tagApi = ApiClient.getClient().create(TagApi.class);

        setupViews();
        loadTags();
        updateDateDisplay();
    }

    private void setupViews() {
        // 뒤로가기 버튼
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 날짜 선택
        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());
        binding.tvSelectedDate.setOnClickListener(v -> showDatePicker());

        // 사진 선택
        binding.llPhotoPlaceholder.setOnClickListener(v -> checkPermissionAndSelectPhoto());

        // 사진 삭제
        binding.btnRemovePhoto.setOnClickListener(v -> {
            selectedPhotoUri = null;
            binding.ivPhoto.setVisibility(View.GONE);
            binding.llPhotoPlaceholder.setVisibility(View.VISIBLE);
            binding.btnRemovePhoto.setVisibility(View.GONE);
        });

        // 저장 버튼
        binding.btnSave.setOnClickListener(v -> saveDiary());
    }

    private void loadTags() {
        tagApi.getTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allTags = response.body();
                    displayTags();
                } else if (response.code() == 401) {
                    AuthUtils.handleUnauthorized(CreateDiaryActivity.this);
                } else {
                    Toast.makeText(CreateDiaryActivity.this,
                        "태그를 불러올 수 없습니다 (" + response.code() + ")",
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                Toast.makeText(CreateDiaryActivity.this,
                    "태그를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTags() {
        binding.chipGroupTags.removeAllViews();

        for (Tag tag : allTags) {
            Chip chip = new Chip(this);
            chip.setText(tag.getTagName());
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTagIds.add(tag.getTagId());
                } else {
                    selectedTagIds.remove(Integer.valueOf(tag.getTagId()));
                }
            });
            binding.chipGroupTags.addView(chip);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = calendar.getTime();
                    updateDateDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // 미래 날짜 선택 불가
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateDateDisplay() {
        String dateStr = DateUtils.dateToServerFormat(selectedDate);
        binding.tvSelectedDate.setText(DateUtils.serverDateToDisplay(dateStr));
    }

    private void checkPermissionAndSelectPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_PERMISSION_CODE);
            } else {
                openGallery();
            }
        } else {
            // Android 12 이하
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void displaySelectedPhoto() {
        if (selectedPhotoUri != null) {
            binding.ivPhoto.setVisibility(View.VISIBLE);
            binding.llPhotoPlaceholder.setVisibility(View.GONE);
            binding.btnRemovePhoto.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(selectedPhotoUri)
                    .centerCrop()
                    .into(binding.ivPhoto);
        }
    }

    private void saveDiary() {
        String description = binding.etDescription.getText().toString().trim();

        // 유효성 검사
        if (TextUtils.isEmpty(description)) {
            binding.etDescription.setError("내용을 입력해주세요");
            binding.etDescription.requestFocus();
            return;
        }

        if (selectedPhotoUri == null) {
            Toast.makeText(this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 로딩 표시
        showLoading(true);

        try {
            // 날짜
            String dateStr = DateUtils.dateToServerFormat(selectedDate);
            RequestBody dateBody = RequestBody.create(MediaType.parse("text/plain"), dateStr);

            // 설명
            RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);

            // 태그 IDs (JSON 배열 형태)
            String tagIdsJson = new Gson().toJson(selectedTagIds);
            RequestBody tagIdsBody = RequestBody.create(MediaType.parse("text/plain"), tagIdsJson);

            // 사진 파일
            File photoFile = FileUtils.getFileFromUri(this, selectedPhotoUri);
            RequestBody photoRequestBody = RequestBody.create(
                    MediaType.parse("image/*"), photoFile);
            MultipartBody.Part photoPart = MultipartBody.Part.createFormData(
                    "photo", photoFile.getName(), photoRequestBody);

            // API 호출
            diaryApi.createDiary(dateBody, descriptionBody, tagIdsBody, photoPart)
                    .enqueue(new Callback<DiaryResponse>() {
                        @Override
                        public void onResponse(Call<DiaryResponse> call, Response<DiaryResponse> response) {
                            showLoading(false);

                            // 임시 파일 삭제
                            FileUtils.deleteTempFile(photoFile);

                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(CreateDiaryActivity.this,
                                    "다이어리가 저장되었습니다!", Toast.LENGTH_SHORT).show();

                                // MainActivity로 돌아가기
                                Intent intent = new Intent(CreateDiaryActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else if (response.code() == 401) {
                                AuthUtils.handleUnauthorized(CreateDiaryActivity.this);
                            } else {
                                Toast.makeText(CreateDiaryActivity.this,
                                    "저장 실패: " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DiaryResponse> call, Throwable t) {
                            showLoading(false);
                            FileUtils.deleteTempFile(photoFile);
                            Toast.makeText(CreateDiaryActivity.this,
                                "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception e) {
            showLoading(false);
            e.printStackTrace();
            Toast.makeText(this, "파일 처리 오류: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnSave.setEnabled(!show);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "갤러리 접근 권한이 필요합니다", Toast.LENGTH_LONG).show();
            }
        }
    }
}
