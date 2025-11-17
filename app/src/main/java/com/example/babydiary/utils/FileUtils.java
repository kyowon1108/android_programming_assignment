package com.example.babydiary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    /**
     * Uri를 File로 변환
     * @param context Context
     * @param uri 변환할 Uri
     * @return 생성된 File 객체
     */
    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        // 캐시 디렉토리에 임시 파일 생성
        File tempFile = new File(context.getCacheDir(), "temp_image_" + System.currentTimeMillis() + "." + getFileExtension(context, uri));
        tempFile.createNewFile();

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {

            if (inputStream == null) {
                throw new IOException("Failed to open input stream from Uri");
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }

        return tempFile;
    }

    /**
     * Uri로부터 파일 확장자 가져오기
     * @param context Context
     * @param uri Uri
     * @return 파일 확장자 (예: "jpg", "png")
     */
    private static String getFileExtension(Context context, Uri uri) {
        String extension = null;

        // Content scheme인 경우
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            // File scheme인 경우
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        // 확장자를 찾을 수 없는 경우 기본값 설정
        if (extension == null || extension.isEmpty()) {
            extension = "jpg";
        }

        return extension;
    }

    /**
     * 임시 파일 삭제
     * @param file 삭제할 파일
     */
    public static void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}