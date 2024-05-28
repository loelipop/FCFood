package fcu.app.FengChiaFood;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store_info.db";
    private static final int DATABASE_VERSION = 1;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREFS_DB_INITIALIZED = "isDatabaseInitialized";

    private Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS stores");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS favorites");
        db.execSQL("DROP TABLE IF EXISTS ratings");
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        String createStoresTable = "CREATE TABLE IF NOT EXISTS stores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "address TEXT NOT NULL, " +
                "description TEXT, " +
                "google_map_url TEXT, " +
                "photo BLOB, " +
                "rating REAL);";

        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL UNIQUE);";

        String createFavoritesTable = "CREATE TABLE IF NOT EXISTS favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "store_id INTEGER NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(store_id) REFERENCES stores(id));";

        String createRatingsTable = "CREATE TABLE IF NOT EXISTS ratings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "store_id INTEGER NOT NULL, " +
                "rating REAL NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(store_id) REFERENCES stores(id));";

        db.execSQL(createStoresTable);
        db.execSQL(createUsersTable);
        db.execSQL(createFavoritesTable);
        db.execSQL(createRatingsTable);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // 插入使用者
        String insertUserSQL = "INSERT OR IGNORE INTO users (email) VALUES ('loejunxiang@gmail.com')";
        db.execSQL(insertUserSQL);
        Log.d("DatabaseHelper", "Inserted user: loejunxiang@gmail.com");

        // 插入店家和照片
        insertStore(db, "Men Monster", "台中市西屯區慶和街32號", "在逢甲夜市旁的創意拉麵店", "https://maps.app.goo.gl/mwspEWieucSYmtrb7", R.drawable.menmonster, 4.4f);
        insertStore(db, "HUN 貳", "台中市西屯區文華路217-8號", "很好吃的一家意大利麵店", "https://maps.app.goo.gl/zLvobgYjTXiKv3sQ6", R.drawable.hun2, 4.5f);
        insertStore(db, "大王麻辣乾麵-逢甲旗艦店", "40742台中市西屯區文華路9-5號", "大名鼎鼎的大王麻辣乾麵，愛吃辣的快來吧", "https://maps.app.goo.gl/7o2B9qfGUp7eoovG8", R.drawable.dawang, 4.7f);
        insertStore(db, "大埔鐵板燒 河南逢甲店", "台中市西屯區河南路二段255-1號", "大埔鐵板燒是全台灣最多分店的鐵板燒", "https://maps.app.goo.gl/o1fzvENm4cz1MrB97", R.drawable.dapu, 3.9f);
        insertStore(db, "爭鮮迴轉壽司-逢甲店", "台中市西屯區河南路二段358號", "評價又好吃的爭鮮迴轉壽司", "https://maps.app.goo.gl/PwgGY3Xo8t7iQBTB7", R.drawable.zhengxian, 4.0f);
    }

    private void insertStore(SQLiteDatabase db, String name, String address, String description, String googleMapUrl, int imageResId, float rating) {
        try {
            // 从 drawable 中读取图像
            Bitmap image = BitmapFactory.decodeResource(myContext.getResources(), imageResId);
            byte[] imageBytes = getBytesFromBitmap(image);

            // 使用SQL语句插入数据
            String insertStoreSQL = "INSERT INTO stores (name, address, description, google_map_url, photo, rating) VALUES ('" +
                    name + "', '" + address + "', '" + description + "', '" + googleMapUrl + "', ?, " + rating + ")";
            db.execSQL(insertStoreSQL, new Object[]{imageBytes});

            Log.d("DatabaseHelper", "Inserted store: " + name);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting store: " + name, e);
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteBuffer);
        return byteBuffer.toByteArray();
    }

    public void initializeDatabase() {
        SharedPreferences prefs = myContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDatabaseInitialized = prefs.getBoolean(PREFS_DB_INITIALIZED, false);

        if (!isDatabaseInitialized) {
            SQLiteDatabase db = this.getWritableDatabase();
            createTables(db);
            insertInitialData(db);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREFS_DB_INITIALIZED, true);
            editor.apply();
        }
    }

    // 从数据库获取所有店家
    public Cursor getAllStores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("stores", null, null, null, null, null, null);
    }
}
