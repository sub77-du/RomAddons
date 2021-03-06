package com.sub77.romaddon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class ApkPublicDataSource {

    private static final String LOG_TAG = ApkPublicDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ApkPublicDbHelper dbHelper;

    private String[] columns = {
            ApkPublicDbHelper.COLUMN_ID,
            ApkPublicDbHelper.COLUMN_APK,
            ApkPublicDbHelper.COLUMN_URL,
            ApkPublicDbHelper.COLUMN_CHECKED
    };

    public ApkPublicDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ApkPublicDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    public ApkPublic createApkPublic(String apk, String url) {
        ContentValues values = new ContentValues();
        values.put(ApkPublicDbHelper.COLUMN_APK, apk);
        values.put(ApkPublicDbHelper.COLUMN_URL, url);

        long insertId = database.insert(ApkPublicDbHelper.TABLE_PUBLICAPK_LIST, null, values);

        Cursor cursor = database.query(ApkPublicDbHelper.TABLE_PUBLICAPK_LIST,
                columns, ApkPublicDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        ApkPublic apkPublic = cursorToApkPublic(cursor);
        cursor.close();

        return apkPublic;
    }

    public void deleteApkPublic(ApkPublic apkPublic) {
        long id = apkPublic.getId();

        database.delete(ApkPublicDbHelper.TABLE_PUBLICAPK_LIST,
                ApkPublicDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + apkPublic.toString());
    }

    public ApkPublic updateApkPublic(long id, String newApk, String newUrl, boolean newChecked) {
        int intValueChecked = (newChecked)? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(ApkPublicDbHelper.COLUMN_APK, newApk);
        values.put(ApkPublicDbHelper.COLUMN_URL, newUrl);
        values.put(ApkPublicDbHelper.COLUMN_CHECKED, intValueChecked);

        database.update(ApkPublicDbHelper.TABLE_PUBLICAPK_LIST,
                values,
                ApkPublicDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(ApkPublicDbHelper.TABLE_PUBLICAPK_LIST,
                columns, ApkPublicDbHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        ApkPublic apkPublic = cursorToApkPublic(cursor);
        cursor.close();

        return apkPublic;
    }

    private ApkPublic cursorToApkPublic(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ApkPublicDbHelper.COLUMN_ID);
        int idApk = cursor.getColumnIndex(ApkPublicDbHelper.COLUMN_APK);
        int idUrl = cursor.getColumnIndex(ApkPublicDbHelper.COLUMN_URL);
        int idChecked = cursor.getColumnIndex(ApkPublicDbHelper.COLUMN_CHECKED);

        String apk = cursor.getString(idApk);
        String url = cursor.getString(idUrl);
        long id = cursor.getLong(idIndex);
        int intValueChecked = cursor.getInt(idChecked);

        boolean isChecked = (intValueChecked != 0);

        ApkPublic apkPublic = new ApkPublic(apk, url, id, isChecked);

        return apkPublic;
    }

    public List<ApkPublic> getAllApkPublics() {
        List<ApkPublic> apkPublicList = new ArrayList<>();

        Cursor cursor = database.query(ApkPublicDbHelper.TABLE_PUBLICAPK_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        ApkPublic apkPublic;

        while(!cursor.isAfterLast()) {
            apkPublic = cursorToApkPublic(cursor);
            apkPublicList.add(apkPublic);
            Log.d(LOG_TAG, "ID: " + apkPublic.getId() + ", Inhalt: " + apkPublic.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return apkPublicList;
    }
}