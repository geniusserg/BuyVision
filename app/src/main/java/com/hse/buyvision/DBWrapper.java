package com.hse.buyvision;

import java.io.File;
import java.util.Date;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBWrapper{
    private DBHelper dbHelper;
    private long counter = -1;
    private ArrayList<ItemModel> itemsArray = new ArrayList<ItemModel>();
    public DBWrapper(DBHelper dbHelper) { this.dbHelper = dbHelper; };
    public void save(ItemModel item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseEntry.COLUMN_NAME_DATE, item.date.getTime());
        values.put(DatabaseEntry.COLUMN_NAME_TEXT, item.text);
        values.put(DatabaseEntry.COLUMN_NAME_FILE, item.photo);
        long newRowId = db.insert(DatabaseEntry.TABLE_NAME, null, values);

    }
    public void loadResults(){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(
            DatabaseEntry.TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        );
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            ItemModel item = new ItemModel();
            item.date = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseEntry.COLUMN_NAME_DATE)));
            item.text = cursor.getString(cursor.getColumnIndex(DatabaseEntry.COLUMN_NAME_TEXT));
            item.photo = cursor.getString(cursor.getColumnIndex(DatabaseEntry.COLUMN_NAME_FILE));
            itemsArray.add(item);
        }
        System.out.println("Loaded");
    }
    public Boolean hasNext(){
        return counter != itemsArray.size();
    }
    public Boolean hasPrev(){
        return counter != 0;
    }
    public ItemModel getPrev(){
        itemsArray.clear();
        loadResults();

        if (counter < 0 ){ return null;}
        counter -= 1;
        return itemsArray.get((int)counter);
    }
    public ItemModel getNext(){
        itemsArray.clear();
        loadResults();
        if (itemsArray.size() == 0){
            return null;
        }
        counter += 1;
        if (counter >= itemsArray.size()){ counter = 0; }
        return itemsArray.get((int)counter);
    }

}