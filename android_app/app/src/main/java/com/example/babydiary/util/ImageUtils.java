package com.example.babydiary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 이미지 처리 유틸리티
 */
public class ImageUtils {

    /**
     * 임시 이미지 파일 생성 (카메라 촬영용)
     * @param context Context
     * @return 생성된 파일
     * @throws IOException 파일 생성 실패
     */
    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "DIARY_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    /**
     * 이미지 압축 및 리사이징
     * @param context Context
     * @param imageUri 원본 이미지 URI
     * @param maxWidth 최대 너비
     * @param maxHeight 최대 높이
     * @param quality 압축 품질 (0-100)
     * @return 압축된 이미지 파일
     * @throws IOException 처리 실패
     */
    public static File compressImage(Context context, Uri imageUri, int maxWidth, int maxHeight, int quality) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
        if (inputStream != null) {
            inputStream.close();
        }

        if (originalBitmap == null) {
            throw new IOException("Failed to decode bitmap");
        }

        // 회전 정보 확인 (EXIF)
        Bitmap rotatedBitmap = rotateImageIfRequired(context, originalBitmap, imageUri);

        // 리사이징
        Bitmap resizedBitmap = resizeBitmap(rotatedBitmap, maxWidth, maxHeight);

        // 압축 후 저장
        File compressedFile = createImageFile(context);
        FileOutputStream fos = new FileOutputStream(compressedFile);
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
        fos.flush();
        fos.close();

        // 메모리 해제
        if (rotatedBitmap != originalBitmap) {
            originalBitmap.recycle();
        }
        resizedBitmap.recycle();

        return compressedFile;
    }

    /**
     * 비트맵 리사이징 (비율 유지)
     * @param bitmap 원본 비트맵
     * @param maxWidth 최대 너비
     * @param maxHeight 최대 높이
     * @return 리사이즈된 비트맵
     */
    private static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }

    /**
     * EXIF 정보에 따라 이미지 회전
     * @param context Context
     * @param bitmap 원본 비트맵
     * @param imageUri 이미지 URI
     * @return 회전된 비트맵
     * @throws IOException EXIF 읽기 실패
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap bitmap, Uri imageUri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(imageUri);
        if (input == null) {
            return bitmap;
        }

        ExifInterface exif = new ExifInterface(input);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        input.close();

        int rotationAngle = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationAngle = 270;
                break;
        }

        if (rotationAngle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationAngle);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }

    /**
     * 파일 크기 확인
     * @param file 파일
     * @return 파일 크기 (bytes)
     */
    public static long getFileSize(File file) {
        if (file != null && file.exists()) {
            return file.length();
        }
        return 0;
    }

    /**
     * 파일 크기가 제한을 초과하는지 확인
     * @param file 파일
     * @param maxSize 최대 크기 (bytes)
     * @return 초과 여부
     */
    public static boolean isFileSizeExceeded(File file, long maxSize) {
        return getFileSize(file) > maxSize;
    }
}
