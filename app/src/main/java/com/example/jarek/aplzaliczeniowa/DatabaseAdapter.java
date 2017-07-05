package com.example.jarek.aplzaliczeniowa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseAdapter {

    private static final String DEBUG_TAG = "SqLiteTodoManager";
    private SQLiteDatabase database;
    private Context context;
    private DatabaseHelper databaseHelper;

    private static final String DB_START = "database";

    private static final int DB_VERSION = 3;

    /**
     * Deklaracja tabeli "path":
     * - nazwa tabeli
     * - dla kazdej kolumny: nazwa, wlasciwosci, ID kolumny
     */
    private static final String PATH_TABLE_NAME = "path";

    private static final String PATH_KEY_ID = "_id";
    private static final String PATH_ID_OPTIONS = "INTEGER PRIMARY KEY";
    public static final int PATH_ID_COLUMN = 0;

    private static final String PATH_KEY_START_LATITUDE = "start_latitude";
    /**
     * REAL - typ danych w SQLite, przechowuje double/float
     * https://www.tutorialspoint.com/sqlite/sqlite_data_types.htm
     */
    private static final String PATH_START_LATITUDE_OPTIONS = "REAL NOT NULL";
    public static final int PATH_START_LATITUDE_COLUMN = 1;

    private static final String PATH_KEY_START_LONGITUDE = "start_longitude";
    private static final String PATH_START_LONGITUDE_OPTIONS = "REAL NOT NULL";
    public static final int PATH_START_LONGITUDE_COLUMN = 2;

    private static final String PATH_KEY_END_LATITUDE = "end_latitude";
    private static final String PATH_END_LATITUDE_OPTIONS = "REAL NOT NULL";
    public static final int PATH_END_LATITUDE_COLUMN = 3;

    private static final String PATH_KEY_END_LONGITUDE = "end_longitude";
    private static final String PATH_END_LONGITUDE_OPTIONS = "REAL NOT NULL";
    public static final int PATH_END_LONGITUDE_COLUMN = 4;

    private static final String PATH_KEY_TIME = "time";
    private static final String PATH_TIME_OPTIONS = "REAL NOT NULL";
    public static final int PATH_TIME_COLUMN = 5;

    /**
     * Skrypt w SQLite do tworzenia tabeli "path"
     */
    private static final String PATH_CREATE_TABLE = "CREATE TABLE " + PATH_TABLE_NAME + "( " +
            PATH_KEY_ID + " " + PATH_ID_OPTIONS + "," +
            PATH_KEY_START_LATITUDE + " " + PATH_START_LATITUDE_OPTIONS + "," +
            PATH_KEY_START_LONGITUDE + " " + PATH_START_LONGITUDE_OPTIONS + "," +
            PATH_KEY_END_LATITUDE + " " + PATH_END_LATITUDE_OPTIONS + "," +
            PATH_KEY_END_LONGITUDE + " " + PATH_END_LONGITUDE_OPTIONS + "," +
            PATH_KEY_TIME + " " + PATH_TIME_OPTIONS + ");";


    /**
     * Skrypt w SQLite do usuwania tabeli "path"
     */
    private static final String DROP_PATH_TABLE = "DROP TABLE IF EXISTS " + PATH_TABLE_NAME;

    DatabaseAdapter(Context context) {
        this.context = context;
    }

    /**
     * Otwieranie połączenia z bazą danych
     * @return Zwraca DatabaseHelper z bazą danych do zapisu lub odczytu
     */
    DatabaseAdapter open() {
        databaseHelper = new DatabaseHelper();
        try {
            database = databaseHelper.getWritableDatabase();
        }
        catch (SQLException e) {
            database = databaseHelper.getReadableDatabase();
        }
        return this;
    }

    /**
     * Zamyka połączenie z bazą danych
     */
    void close() {
        databaseHelper.close();
    }

    /**
     * Dodaje wartości do bazy danych
     * @param startLat Szerokosc geograficzna punktu poczatkowego
     * @param startLong Dlugosc geograficzna punktu poczatkowego
     * @param endLat Szerokosc geograficzna punktu koncowego
     * @param endLong Dlugosc geograficzna punktu koncowego
     * @param time Czas trasy
     * @return
     */
    long addPath(Double startLat, Double startLong, Double endLat, Double endLong, Double time) {
        ContentValues newValues = new ContentValues();
        newValues.put(PATH_KEY_START_LATITUDE, startLat);
        newValues.put(PATH_KEY_START_LONGITUDE, startLong);
        newValues.put(PATH_KEY_END_LATITUDE, endLat);
        newValues.put(PATH_KEY_END_LONGITUDE, endLong);
        newValues.put(PATH_KEY_TIME, time);
        return database.insert(PATH_TABLE_NAME,null,newValues);
    }

    /**
     *
     * @return Zwraca wszystkie dane z tabeli "path" w postaci Cursora
     */
    Cursor getAllPathData() {
        String[] columns = {PATH_KEY_ID, PATH_KEY_START_LATITUDE, PATH_KEY_START_LONGITUDE,
                PATH_KEY_END_LATITUDE, PATH_KEY_END_LONGITUDE, PATH_KEY_TIME};
        return database.query(PATH_TABLE_NAME, columns, null, null, null, null, null, null);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private SQLiteDatabase sqLiteDatabase;

        public DatabaseHelper(Context context, String title, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, title, factory, version);
        }

        DatabaseHelper() {
            super(context,DB_START,null,DB_VERSION);
        }

        /**
         * Funkcja odpala się za każdym razem gdy tworzona jest baza danych
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            this.sqLiteDatabase = sqLiteDatabase;
            Log.d(DEBUG_TAG, "Database creating...");
            sqLiteDatabase.execSQL(PATH_CREATE_TABLE);
            Log.d(DEBUG_TAG, "Table " + PATH_CREATE_TABLE + " ver." + DB_VERSION + " created");
        }

        /**
         * Funkcja odpala się, gdy Android zauwazy, ze wersja bazy danych w zainstalowanej aplikacji
         * na urzadzeniu jest mniejsza niz ta w kodzie
         */
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.d(DEBUG_TAG, "Database updating...");
            dropTables(sqLiteDatabase);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(sqLiteDatabase);
        }

        /**
         * Funkcja usuwa wszystkie tabele z bazy
         */
        private void dropTables(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DROP_PATH_TABLE);
            Log.d(DEBUG_TAG, "Table " + PATH_TABLE_NAME + " dropped.");
        }
    }
}
