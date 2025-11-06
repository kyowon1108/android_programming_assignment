package com.example.babydiary.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.example.babydiary.R;
import com.example.babydiary.util.Constants;
import com.example.babydiary.util.ImageUtils;
import com.example.babydiary.util.PermissionUtils;
import com.bumptech.glide.Glide;

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

    private Uri photoUri;
    private File photoFile;
    private File tempCameraFile;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diary);

        initViews();
        setupActivityResultLaunchers();
        setupListeners();
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

        if (photoFile == null) {
            Toast.makeText(this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: MultipartFormData로 서버에 업로드
        // 현재는 파일이 준비된 상태
        Toast.makeText(this, "다이어리 작성 기능은 추후 구현 예정입니다", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Ready to save diary:");
        Log.d(TAG, "Description: " + description);
        Log.d(TAG, "Photo file: " + photoFile.getAbsolutePath());
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
