package kawinpart.sorasak.projectbakery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by NekokanSama on 13/6/2559.
 */
public class ManageTABLE {

    //Explicit
    private MyOpenHelper objMyOpenHelper;
    private SQLiteDatabase writeSqLiteDatabase, readSqLiteDatabase;

    public static final String TABLE_USER = "userTABLE";
    public static final String COLUMN_id = "_id";
    public static final String COLUMN_User = "User";
    public static final String COLUMN_Password = "Password";
    public static final String COLUMN_Name = "Name";
    public static final String COLUMN_Surname = "Surname";
    public static final String COLUMN_Address = "Address";
    public static final String COLUMN_Phone = "Phone";
    public static final String COLUMN_Complacency = "Complacency";

    public static final String TABLE_BREAD = "breadTABLE";
    public static final String COLUMN_Bread = "Bread";
    public static final String COLUMN_Price = "Price";

    public static final String COLUMN_Image = "Image";
    public static final String COLUMN_Status = "Status";


    public static final String TABLE_ORDER = "orderTABLE";
    public static final String COLUMN_Date = "Date";
    public static final String COLUMN_Item = "Item";
    public static final String COLUMN_idReceive = "idReceive";

    public static final String TABLE_ORDER_FINISH = "orderTABLE_finish";

    public ManageTABLE(Context context) {

        //Create & Connected
        objMyOpenHelper = new MyOpenHelper(context);
        writeSqLiteDatabase = objMyOpenHelper.getWritableDatabase();
        readSqLiteDatabase = objMyOpenHelper.getReadableDatabase();

    }   // Constructor

    //หาชื่อขนมบังจาก breadTABLE
    public String[] searchBreadStock(String strBread) {

        try {

            String[] resultStrings = null;
            Cursor objCursor = readSqLiteDatabase.query(TABLE_BREAD,
                    new String[]{COLUMN_id, COLUMN_Bread},
                    COLUMN_Bread + "=?",
                    new String[]{String.valueOf(strBread)},
                    null, null, null, null);

            if (objCursor != null) {
                if (objCursor.moveToFirst()) {

                    resultStrings = new String[objCursor.getColumnCount()];
                    for (int i = 0; i < objCursor.getColumnCount(); i++) {
                        resultStrings[i] = objCursor.getString(i);
                    }

                }   //if2
            }   //if1
            objCursor.close();
            return resultStrings;

        } catch (Exception e) {
            return null;
        }

        // return new String[0];
    }   // searchBreadStock

    //หาชื่อขนมบังจาก orderTABLE
    public String[] searchBread(String strBread) {

        try {

            String[] resultStrings = null;
            Cursor objCursor = readSqLiteDatabase.query(TABLE_ORDER,
                    new String[]{COLUMN_id, COLUMN_Bread, COLUMN_Item},
                    COLUMN_Bread + "=?",
                    new String[]{String.valueOf(strBread)},
                    null, null, null, null);

            if (objCursor != null) {
                if (objCursor.moveToFirst()) {

                    resultStrings = new String[objCursor.getColumnCount()];
                    for (int i = 0; i < objCursor.getColumnCount(); i++) {
                        resultStrings[i] = objCursor.getString(i);
                    }

                }   //if2
            }   //if1
            objCursor.close();
            return resultStrings;

        } catch (Exception e) {
            return null;
        }

        // return new String[0];
    }

    //หาข้อมูล ที่ User login อยู่
    public String[] readAtPosition(int intID) {

        String[] resultStrings = null;
        Cursor objCursor = readSqLiteDatabase.query(TABLE_USER,
                new String[]{COLUMN_id, COLUMN_Name, COLUMN_Surname,
                        COLUMN_Address, COLUMN_Phone},
                null, null, null, null, null);

        objCursor.moveToFirst();
        resultStrings = new String[objCursor.getColumnCount()];
        objCursor.moveToPosition(intID);
        for (int i = 0; i < objCursor.getColumnCount(); i++) {
            resultStrings[i] = objCursor.getString(i);
        }   // for
        objCursor.close();

        return resultStrings;
    }


    public String[] readAllBread(int intColumn) {

        String[] resultStrings = null;
        Cursor objCursor = readSqLiteDatabase.query(TABLE_BREAD,
                new String[]{COLUMN_id,
                        COLUMN_Bread,
                        COLUMN_Price,

                        COLUMN_Image},
                null, null, null, null, null);

        objCursor.moveToFirst();
        int intRecord = objCursor.getCount();
        resultStrings = new String[intRecord];
        for (int i = 0; i < intRecord; i++) {

            switch (intColumn) {
                case 1:
                    resultStrings[i] = objCursor.getString(objCursor.getColumnIndex(COLUMN_Bread));
                    break;
                case 2:
                    resultStrings[i] = objCursor.getString(objCursor.getColumnIndex(COLUMN_Price));
                    break;

                case 4:
                    resultStrings[i] = objCursor.getString(objCursor.getColumnIndex(COLUMN_Image));
                    break;
            }   // switch

            objCursor.moveToNext();

        }   // for
        objCursor.close();
        return resultStrings;
    }


    public String[] searchUser(String strUser) {

        try {

            String[] resultStrings = null;
            Cursor objCursor = readSqLiteDatabase.query(TABLE_USER,
                    new String[]{COLUMN_id, COLUMN_User, COLUMN_Password,
                            COLUMN_Name, COLUMN_Surname, COLUMN_Address,
                            COLUMN_Phone, COLUMN_Complacency},
                    COLUMN_User + "=?",
                    new String[]{String.valueOf(strUser)},
                    null, null, null, null);

            if (objCursor != null) {
                if (objCursor.moveToFirst()) {

                    int intTimes = objCursor.getColumnCount();
                    resultStrings = new String[intTimes];
                    for (int i = 0; i < intTimes; i++) {
                        resultStrings[i] = objCursor.getString(i);
                    }   // for

                }   // if2
            }   // if1
            objCursor.close();
            return resultStrings;

        } catch (Exception e) {
            return null;
        }

        //return new String[0];
    }


    public long addNewOrderFinish(String stridReceive,
                                  String strDate,
                                  String strName,
                                  String strSurname,
                                  String strAddress,
                                  String strPhone,
                                  String strBread,
                                  String strPrice,
                                  String strItem
    ) {

        ContentValues objContentValues = new ContentValues();

        objContentValues.put(COLUMN_idReceive, stridReceive);
        objContentValues.put(COLUMN_Date, strDate);
        objContentValues.put(COLUMN_Name, strName);
        objContentValues.put(COLUMN_Surname, strSurname);
        objContentValues.put(COLUMN_Address, strAddress);
        objContentValues.put(COLUMN_Phone, strPhone);
        objContentValues.put(COLUMN_Bread, strBread);
        objContentValues.put(COLUMN_Price, strPrice);
        objContentValues.put(COLUMN_Item, strItem);

        return writeSqLiteDatabase.insert(TABLE_ORDER_FINISH, null, objContentValues);
    }


    public long addNewOrder(String strDate,
                            String strName,
                            String strSurname,
                            String strAddress,
                            String strPhone,
                            String strBread,
                            String strPrice,
                            String strItem
    ) {

        ContentValues objContentValues = new ContentValues();
        objContentValues.put(COLUMN_Date, strDate);
        objContentValues.put(COLUMN_Name, strName);
        objContentValues.put(COLUMN_Surname, strSurname);
        objContentValues.put(COLUMN_Address, strAddress);
        objContentValues.put(COLUMN_Phone, strPhone);
        objContentValues.put(COLUMN_Bread, strBread);
        objContentValues.put(COLUMN_Price, strPrice);
        objContentValues.put(COLUMN_Item, strItem);

        return writeSqLiteDatabase.insert(TABLE_ORDER, null, objContentValues);
    }

    public long addNewBread(String strBread,
                            String strPrice,

                            String strImage,
                            String strStatus) {

        ContentValues objContentValues = new ContentValues();
        objContentValues.put(COLUMN_Bread, strBread);
        objContentValues.put(COLUMN_Price, strPrice);

        objContentValues.put(COLUMN_Image, strImage);
        objContentValues.put(COLUMN_Status, strStatus);

        return writeSqLiteDatabase.insert(TABLE_BREAD, null, objContentValues);
    }

    public long addNewUser(String strID,
                           String strUser,
                           String strPassword,
                           String strName,
                           String strSurname,
                           String strAddress,
                           String strPhone,
                           String strComplacency) {

        ContentValues objContentValues = new ContentValues();
        objContentValues.put(COLUMN_id, strID);
        objContentValues.put(COLUMN_User, strUser);
        objContentValues.put(COLUMN_Password, strPassword);
        objContentValues.put(COLUMN_Name, strName);
        objContentValues.put(COLUMN_Surname, strSurname);
        objContentValues.put(COLUMN_Address, strAddress);
        objContentValues.put(COLUMN_Phone, strPhone);
        objContentValues.put(COLUMN_Complacency, strComplacency);

        return writeSqLiteDatabase.insert(TABLE_USER, null, objContentValues);
    }


}   // Main Class

