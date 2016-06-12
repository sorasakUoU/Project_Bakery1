package kawinpart.sorasak.projectbakery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by NekokanSama on 13/6/2559.
 */
public class MyOpenHelper {
    //Explicit
    public static final String DATABASE_NAME = "Bakery.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_USER = "create table userTABLE (" +
            "_id integer primary key, " +
            "User text, " +
            "Password text, " +
            "Name text, " +
            "Surname text, " +
            "Address text, " +
            "Phone text, " +
            "Complacency text);";

    private static final String CREATE_TABLE_BREAD = "create table breadTABLE (" +
            "_id integer primary key, " +
            "Bread text, " +
            "Price text, " +

            "Image text, " +
            "Status text);";

    private static final String CREATE_TABLE_ORDER = "create table orderTABLE (" +
            "_id integer primary key, " +
            "Date text, " +
            "Name text, " +
            "Surname text, " +
            "Address text, " +
            "Phone text, " +
            "Bread text, " +
            "Price text, " +
            "Item text);";

    private static final String CREATE_TABLE_ORDER_finish = "create table orderTABLE_finish (" +
            "_id integer primary key, " +
            "idReceive text, "+
            "Date text, " +
            "Name text, " +
            "Surname text, " +
            "Address text, " +
            "Phone text, " +
            "Bread text, " +
            "Price text, " +
            "Item text);";

    public MyOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }   // Constructor

    //@Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
        sqLiteDatabase.execSQL(CREATE_TABLE_BREAD);
        sqLiteDatabase.execSQL(CREATE_TABLE_ORDER);
        sqLiteDatabase.execSQL(CREATE_TABLE_ORDER_finish);
    }

    //@Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}   // Main Class

