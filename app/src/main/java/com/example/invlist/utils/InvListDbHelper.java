package com.example.invlist.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class InvListDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "InvList.db";

    public static final String TABLE_MF_EQUITY = "MF_EQUITY";
    public static final String TABLE_MF_DEBT = "MF_DEBT";
    public static final String TABLE_MF_ELSS = "MF_ELSS";
    public static final String TABLE_STOCK = "STOCK";
    public static final String TABLE_FOREX = "FOREX";
    public static final String COLUMN_NAME = "NAME";


    public InvListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_MF_EQUITY + " (" + COLUMN_NAME + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_MF_DEBT + " (" + COLUMN_NAME + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_MF_ELSS + " (" + COLUMN_NAME + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_STOCK + " (" + COLUMN_NAME + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_FOREX + " (" + COLUMN_NAME + " TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onUpgrade(db, oldVersion, newVersion);
    }

    private boolean isTableEmpty(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, table);
        return (numRows == 0) ? true : false;
    }

    public boolean isEquityEmpty() {
        return isTableEmpty(TABLE_MF_EQUITY);
    }

    public boolean isDebtEmpty() {
        return isTableEmpty(TABLE_MF_DEBT);
    }

    public boolean isElssEmpty() {
        return isTableEmpty(TABLE_MF_ELSS);
    }

    public boolean isStockEmpty() {
        return isTableEmpty(TABLE_STOCK);
    }

    public boolean isForexEmpty() {
        return isTableEmpty(TABLE_FOREX);
    }

    private void add(String table, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, value);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(table, null, values);
    }

    public void addEquity(String value) {
        add(TABLE_MF_EQUITY, value);
    }

    public void addDebt(String value) {
        add(TABLE_MF_DEBT, value);
    }

    public void addElss(String value) {
        add(TABLE_MF_ELSS, value);
    }

    public void addStock(String value) {
        add(TABLE_STOCK, value);
    }

    public void addForex(String value) {
        add(TABLE_FOREX, value);
    }

    private void remove(String table, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = db.delete(table, COLUMN_NAME + " = ?", new String[] { value });
    }

    public void removeEquity(String value) {
        remove(TABLE_MF_EQUITY, value);
    }

    public void removeDebt(String value) {
        remove(TABLE_MF_DEBT, value);
    }

    public void removeElss(String value) {
        remove(TABLE_MF_ELSS, value);
    }

    public void removeStock(String value) {
        remove(TABLE_STOCK, value);
    }

    public void removeForex(String value) {
        remove(TABLE_FOREX, value);
    }

    private void add(String table, String[] values) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (String value: values) {
            ContentValues vcs = new ContentValues();
            vcs.put(COLUMN_NAME, value);
            db.insert(table, null, vcs);
        }
    }

    public void addEquity(String[] values) {
        add(TABLE_MF_EQUITY, values);
    }

    public void addDebt(String[] values) {
        add(TABLE_MF_DEBT, values);
    }

    public void addElss(String[] values) {
        add(TABLE_MF_ELSS, values);
    }

    public void addStock(String[] values) {
        add(TABLE_STOCK, values);
    }

    public void addForex(String[] values) {
        add(TABLE_FOREX, values);
    }

    private String[] getValues(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> values = new ArrayList<String>();
        Cursor cursor =  db.rawQuery( "select * from " + table, null);

        if (cursor.moveToFirst()) {
            do {
                values.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return values.toArray(new String[0]);
    }

    public String[] getEquity() {
        return getValues(TABLE_MF_EQUITY);
    }

    public String[] getDebt() {
        return getValues(TABLE_MF_DEBT);
    }

    public String[] getElss() {
        return getValues(TABLE_MF_ELSS);
    }

    public String[] getStock() {
        return getValues(TABLE_STOCK);
    }

    public String[] getForex() {
        return getValues(TABLE_FOREX);
    }
}
