package com.hse.buyvision;

import java.io.File;
import java.util.Date;
import java.util.ArrayList;
import android.database.Cursor;

public class DBWrapper{
    private DBHelper dbHelper;
    private static int counter = 0;
    private ArrayList<ItemModel> itemsArray = new ArrayList<ItemModel>();
    public DBWrapper(DbHelper dbHelper) {};
    public void save(ItemModel item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseEntry.COLUMN_NAME_DATE, item.date.getTime());
        values.put(DatabaseEntry.COLUMN_NAME_TEXT, item.text);
        values.put(DatabaseEntry.COLUMN_NAME_FILE, item.photo.getAbsolutePath());
        long newRowId = db.insert(TABLE_NAME, null, values);
        counter = newRowId;
    }
    public void loadResults(){
        counter = -1;
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
    }
    public Boolean hasNext(){
        return counter != itemsArray.size();
    }
    public Boolean hasPrev(){
        return counter != 0;
    }
    public ItemModel getPrev(){
        if (counter < 0 ){ return null;}
        counter -= 1;
        return itemsArray.get(counter);
    }
    public ItemModel getNext(){
        if (counter > itemsArray.size() ){ return null;}
        counter += 1;
        return itemsArray.get(counter);
    }

}