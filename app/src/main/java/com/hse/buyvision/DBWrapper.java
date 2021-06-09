import java.io.File;
import java.sql.Date;
import java.util.ArrayList;

public class DBWrapper{
    private DBHelper dbHelper;
    private static int counter = 0;
    private ArrayList<ItemModel> itemsArray = new ArrayList<ItemModel>();
    public DBWrapper(DbHelper dbHelper) {};
    public void save(ItemModel item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseEntry.COLUMN_NAME_DATE, item.date);
        values.put(DatabaseEntry.COLUMN_NAME_TEXT, item.text);
        values.put(DatabaseEntry.COLUMN_NAME_FILE, item.photo.getAbsolutePath());
        long newRowId = db.insert(TABLE_NAME, null, values);
        counter = newRowId;
    }
    public void loadResults(){
        counter = -1;
        Coursor coursor;
        while (true){
            ItemModel item = new ItemModel();
            //////////////////////////////
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