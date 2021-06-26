package com.shockwave.pdf_scaner.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.model.PhotoDirectory;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kotlin.jvm.Throws;

import static com.shockwave.pdf_scaner.util.ParamUtils.PDF_FOLDER;

public class FileUtils {

    public static String dayFormat = "yyyyMMdd_HHmmss";

    public static void createFolder() {
        File folder = new File(PDF_FOLDER);
        if (!folder.exists()) folder.mkdirs();
    }

    public static String createFile(Context context, String name, String folder, String extension) {
        return context.getString(R.string.file_folder, folder, name, extension);
    }

    public static String nameOffer() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    public static File createMoreFile(Context context, int format, boolean isFolder, String extension) throws IOException {
        String timeStamp = new SimpleDateFormat(dayFormat, Locale.getDefault()).format(new Date());
        String imageFileName = context.getString(format, timeStamp);
        if (!isFolder) {
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(imageFileName, extension, storageDir);
        } else {
            File storageDir = new File(PDF_FOLDER);
            if (!storageDir.exists()) storageDir.mkdirs();
            File file = File.createTempFile(imageFileName, extension, storageDir);
            if (!file.exists()) file.mkdirs();
            return file;
        }
    }

    public static File createOfferMoreFile(Context context, boolean isFolder, String extension) throws IOException {
        if (!isFolder) {
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(nameOffer(), extension, storageDir);
        } else {
            File storageDir = new File(PDF_FOLDER);
            if (!storageDir.exists()) storageDir.mkdirs();
            File file = File.createTempFile(nameOffer(), extension, storageDir);
            if (!file.exists()) file.mkdirs();
            return file;
        }
    }

    public static File outMediaFilePDF() throws IOException {
        File storageDir = new File(PDF_FOLDER);
        if (!storageDir.exists()) storageDir.mkdirs();
        File file = File.createTempFile(nameOffer(), ".pdf", storageDir);
        if (!file.exists()) file.mkdirs();
        return file;
    }


    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static long getVideoDuration(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        long duration = 0;
        try {
            retriever.setDataSource(videoPath);
            duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return duration;
    }

    public static Uri getUriFromRawResource(Context context, int resId) {
        try {
            Uri uri;
            uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void shareFile(Context context, String myFilePath, String packageName) {
        try {
            if (isPackageInstalled(packageName, context.getPackageManager()) || packageName.isEmpty()) {
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider",
                        new File(myFilePath));
                context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                if (!packageName.isEmpty()) {
                    intent.setPackage(packageName);
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, context.getString(R.string.pls_install_app_first), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        boolean found = true;
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    public static void shareFile(String myFilePath, Context context) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(myFilePath);
        if (fileWithinMyDir.exists()) {
            Uri uriShare = FileProvider.getUriForFile(context, context.getPackageName() + ".provider",
                    new File(myFilePath));
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentShareFile.setType(URLConnection.guessContentTypeFromName(fileWithinMyDir.getName()));
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uriShare);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }


    public static String getPath(Context context, Uri uri) {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static ArrayList<PhotoDirectory> queryImages(Context context) {
        ArrayList<PhotoDirectory> data = new ArrayList<>();
        Uri uri = MediaStore.Files.getContentUri("external");
        String sortOrder = MediaStore.Images.Media._ID + " DESC";

        String selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);

        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, sortOrder);

        if (cursor != null) {
            data = getPhotoDirectories(cursor);
            cursor.close();
        }
        return data;
    }

    private static ArrayList<PhotoDirectory> getPhotoDirectories(Cursor data) {
        ArrayList<PhotoDirectory> directories = new ArrayList<>();
        ArrayList<PhotoDirectory.Media> allImage = new ArrayList<>();

        while (data.moveToNext()) {

            long imageId = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID));
            String bucketId =
                    data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID));
            String name =
                    data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
            String fileName = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE));

            PhotoDirectory photoDirectory = new PhotoDirectory();
            photoDirectory.id = imageId;
            photoDirectory.bucketId = bucketId;
            photoDirectory.name = name;


            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
            allImage.add(new PhotoDirectory.Media(imageId, fileName, contentUri));

            if (!directories.contains(photoDirectory)) {
                photoDirectory.addPhoto(imageId, fileName, contentUri);
                photoDirectory.dateAdded =
                        data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED));
                directories.add(photoDirectory);
            } else {
                directories.get(directories.indexOf(photoDirectory))
                        .addPhoto(imageId, fileName, contentUri);
//                }
            }
        }
        directories.add(0, new PhotoDirectory("All", allImage));
        return directories;
    }
}
