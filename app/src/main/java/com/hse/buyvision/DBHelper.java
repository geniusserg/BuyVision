package com.hse.buyvision;

public class DBHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
    "CREATE TABLE " + DatabaseEntry.TABLE_NAME + " (" +
    DatabaseEntry._ID + " INTEGER PRIMARY KEY," +
    DatabaseEntry.COLUMN_NAME_DATE + " DATE," +
    DatabaseEntry.COLUMN_NAME_TEXT + " TEXT," +
    DatabaseEntry.COLUMN_NAME_FILE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DatabaseEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ScansHistory.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}