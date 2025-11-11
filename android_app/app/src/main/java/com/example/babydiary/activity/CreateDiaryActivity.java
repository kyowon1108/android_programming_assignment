package com.example.babydiary.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.babydiary.R;
import com.example.babydiary.adapter.TagAdapter;
import com.example.babydiary.dialog.LoadingDialog;
import com.example.babydiary.listener.OnApiResponseListener;
import com.example.babydiary.model.Diary;
import com.example.babydiary.model.Tag;
import com.example.babydiary.service.DiaryService;
import com.example.babydiary.service.TagService;
import com.example.babydiary.util.Constants;
import com.example.babydiary.util.ImageUtils;
import com.example.babydiary.util.PermissionUtils;

import java.io.File;
import java.io.IOException;

/**
 * 다이어리 작성 화면
 */
public class CreateDiaryActivity extends AppCompatActivity {
    private static final String TAG = "CreateDiaryActivity";

    private ImageView ivPhoto;
    private EditText etDescription;
    private Button btnSelectPhoto;
    private Button btnSave;
    private View progressBar;
    private RecyclerView rvTags;

    private Uri photoUri;
    private File photoFile;
    private File tempCameraFile;

    private DiaryService diaryService;
    private TagService tagService;
    private TagAdapter tagAdapter;
    private java.util.List<Tag> tags;
    private java.util.List<Integer> selectedTagIds;
    private LoadingDialog loadingDialog;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diary);

        diaryService = new DiaryService();
        tagService = new TagService();
        tags = new java.util.ArrayList<>();
        selectedTagIds = new java.util.ArrayList<>();

        initViews();
        setupActivityResultLaunchers();
        setupListeners();
        setupTags();
        loadTags();
    }

    /**
     * 뷰 초기화
     */
    private void initViews() {
        ivPhoto = findViewById(R.id.iv_photo);
        etDescription = findViewById(R.id.et_description);
        btnSelectPhoto = findViewById(R.id.btn_select_photo);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
        rvTags = findViewById(R.id.rv_tags);
    }

    /**
     * 태그 RecyclerView 설정
     */
    private void setupTags() {
        tagAdapter = new TagAdapter(this, tags, new TagAdapter.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag) {
                // 토글 방식으로 선택/해제
                if (selectedTagIds.contains(tag.getTagId())) {
                    selectedTagIds.remove(Integer.valueOf(tag.getTagId()));
                    Toast.makeText(CreateDiaryActivity.this, tag.getName() + " 태그 해제", Toast.LENGTH_SHORT).show();
                } else {
                    selectedTagIds.add(tag.getTagId());
                    Toast.makeText(CreateDiaryActivity.this, tag.getName() + " 태그 선택", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "Selected tags: " + selectedTagIds.size());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTags.setLayoutManager(layoutManager);
        rvTags.setAdapter(tagAdapter);
    }

    /**
     * 태그 목록 로드
     */
    private void loadTags() {
        tagService.getAllTags(this, new OnApiResponseListener<java.util.List<Tag>>() {
            @Override
            public void onSuccess(java.util.List<Tag> tagList) {
                tags.clear();
                tags.addAll(tagList);
                tagAdapter.notifyDataSetChanged();
                Log.d(TAG, "Tags loaded: " + tags.size());
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Load tags error: " + error);
                // 태그 로드 실패해도 다이어리 작성은 가능하도록 함
            }
        });
    }

    /**
     * ActivityResultLauncher 설정
     */
    private void setupActivityResultLaunchers() {
        // 카메라
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (tempCameraFile != null && tempCameraFile.exists()) {
                            handleImageSelected(Uri.fromFile(tempCameraFile));
                        }
                    }
                }
        );

        // 갤러리
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            handleImageSelected(selectedImageUri);
                        }
                    }
                }
        );

        // 카메라 권한
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 저장소 읽기 권한
        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "저장소 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * 리스너 설정
     */
    private void setupListeners() {
        btnSelectPhoto.setOnClickListener(v -> showImagePickerDialog());
        btnSave.setOnClickListener(v -> handleSave());
    }

    /**
     * 이미지 선택 다이얼로그 표시
     */
    private void showImagePickerDialog() {
        String[] options = {"카메라로 촬영", "갤러리에서 선택"};

        new AlertDialog.Builder(this)
                .setTitle("사진 선택")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndOpen();
                    } else {
                        checkStoragePermissionAndOpen();
                    }
                })
                .show();
    }

    /**
     * 카메라 권한 확인 후 열기
     */
    private void checkCameraPermissionAndOpen() {
        if (PermissionUtils.checkCameraPermission(this)) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * 저장소 권한 확인 후 갤러리 열기
     */
    private void checkStoragePermissionAndOpen() {
        if (PermissionUtils.checkStorageReadPermission(this)) {
            openGallery();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    /**
     * 카메라 열기
     */
    private void openCamera() {
        try {
            tempCameraFile = ImageUtils.createImageFile(this);
            Uri photoURI = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    tempCameraFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create image file: " + e.getMessage());
            Toast.makeText(this, "카메라 실행 실패", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 갤러리 열기
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    /**
     * 이미지 선택 처리
     * @param imageUri 이미지 URI
     */
    private void handleImageSelected(Uri imageUri) {
        try {
            // 이미지 압축
            photoFile = ImageUtils.compressImage(this, imageUri,
                    Constants.MAX_IMAGE_WIDTH,
                    Constants.MAX_IMAGE_HEIGHT,
                    Constants.IMAGE_QUALITY);

            photoUri = Uri.fromFile(photoFile);

            // 이미지 표시
            Glide.with(this)
                    .load(photoUri)
                    .centerCrop()
                    .into(ivPhoto);

            btnSelectPhoto.setText("사진 변경");

            Log.d(TAG, "Image selected and compressed: " + photoFile.getAbsolutePath());
            Log.d(TAG, "File size: " + ImageUtils.getFileSize(photoFile) + " bytes");

        } catch (IOException e) {
            Log.e(TAG, "Image compression failed: " + e.getMessage());
            Toast.makeText(this, "이미지 처리 실패", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 저장 처리
     */
    private void handleSave() {
        String description = etDescription.getText().toString().trim();

        // 유효성 검사
        if (description.isEmpty()) {
            etDescription.setError("내용을 입력해주세요");
            etDescription.requestFocus();
            return;
        }

        if (photoFile == null || !photoFile.exists()) {
            Toast.makeText(this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // LoadingDialog 표시
        loadingDialog = LoadingDialog.show(this, "다이어리 생성 중...\n(사진 분석 및 AI 동화 생성)");

        // 버튼 비활성화
        btnSave.setEnabled(false);
        btnSelectPhoto.setEnabled(false);
        etDescription.setEnabled(false);

        // 다이어리 생성 API 호출 (선택된 태그 ID 포함)
        diaryService.createDiary(this, description, photoFile, selectedTagIds, new OnApiResponseListener<Diary>() {
            @Override
            public void onSuccess(Diary diary) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                Log.d(TAG, "Diary created successfully with " + selectedTagIds.size() + " tags: " + diary.getDiaryId());
                Toast.makeText(CreateDiaryActivity.this, "다이어리가 작성되었습니다!", Toast.LENGTH_SHORT).show();

                // 생성된 다이어리의 상세 화면으로 이동
                Intent intent = new Intent(CreateDiaryActivity.this, DiaryDetailActivity.class);
                intent.putExtra("diary_id", diary.getDiaryId());
                startActivity(intent);

                // 현재 Activity 종료
                finish();
            }

            @Override
            public void onError(String error) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                // 버튼 다시 활성화
                btnSave.setEnabled(true);
                btnSelectPhoto.setEnabled(true);
                etDescription.setEnabled(true);

                Log.e(TAG, "Create diary error: " + error);
                Toast.makeText(CreateDiaryActivity.this, "저장 실패: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 로딩 상태 표시
     * @param show 표시 여부
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
        btnSelectPhoto.setEnabled(!show);
        etDescription.setEnabled(!show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 임시 파일 정리
        if (tempCameraFile != null && tempCameraFile.exists()) {
            tempCameraFile.delete();
        }
    }
}
