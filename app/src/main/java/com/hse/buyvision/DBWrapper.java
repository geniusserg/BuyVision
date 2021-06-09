import java.io.File;
import java.sql.Date;

public class DBWrapper{
    private DBHelper dbHelper;
    private static long counter = 0;
    public DBWrapper(DbHelper dbHelper) {};
    public void write(ItemModel item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseEntry.COLUMN_NAME_DATE, item.date);
        values.put(DatabaseEntry.COLUMN_NAME_TEXT, item.text);
        values.put(DatabaseEntry.COLUMN_NAME_FILE, item.photo.getAbsolutePath());
        long newRowId = db.insert(TABLE_NAME, null, values);
        counter = newRowId;
    }

}