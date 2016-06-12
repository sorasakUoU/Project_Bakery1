package kawinpart.sorasak.projectbakery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowMenuActivity extends AppCompatActivity {

    //Explicit
    private String strID; // id ของ user ที่ login อยู่

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_menu);

        //Synchronize breadTABLE
        synBreadTABLE();

        //ListView Controller
        //listViewController();

    }   // onCreate

    //Create Inner Class
    public class MyConnectedBread extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://swiftcodingthai.com/mos/php_get_bread_master.php").build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("11April", "Error doInBack ==> " + e.toString());
                return null;
            }

        }   // doInBack

        @Override
        protected void onPostExecute(String strJSON) {
            super.onPostExecute(strJSON);

            Log.d("11April", "strJSON ==> " + strJSON);


            try {
                // Delete All breadTABLE
                SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                        MODE_PRIVATE, null);
                sqLiteDatabase.delete(ManageTABLE.TABLE_BREAD, null, null);

                JSONArray jsonArray = new JSONArray(strJSON);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String strBread = jsonObject.getString(ManageTABLE.COLUMN_Bread);
                    String strPrice = jsonObject.getString(ManageTABLE.COLUMN_Price);

                    String strImage = jsonObject.getString(ManageTABLE.COLUMN_Image);
                    String strStatus = jsonObject.getString(ManageTABLE.COLUMN_Status);

                    ManageTABLE manageTABLE = new ManageTABLE(ShowMenuActivity.this);
                    manageTABLE.addNewBread(strBread, strPrice,
                            strImage, strStatus);

                }   // for

                listViewController();

            } catch (Exception e) {
                Log.d("11April", "Error onPost ==> " + e.toString());
            }

        }   // onPost

    }   // MyConnectedBread Class

    @Override
    protected void onResume() {
        super.onResume();

        synBreadTABLE();

        Log.d("11April", "onResume ทำงาน ");

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        synBreadTABLE();

        Log.d("11April", "onRestart ทำงาน ");

    }

    private void synBreadTABLE() {

        MyConnectedBread myConnectedBread = new MyConnectedBread();
        myConnectedBread.execute();

    }

    public void clickConfirmOrder(View view) {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);

        if (objCursor.getCount() > 0) {

            //Have Data
            Intent objIntent = new Intent(ShowMenuActivity.this, ConfirmOrderActivity.class);
            objIntent.putExtra("idUser", strID);
            startActivity(objIntent);

        } else {

            //No Data
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(ShowMenuActivity.this, "กรุณา Order", "กรุณาสั่ง อาหารด้วยคะ");

        }


    }   // clickConfirmOrder

    private void listViewController() {

        // Setup Value
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM breadTABLE WHERE Status = '1'", null);
        cursor.moveToFirst();

        String[] iconStrings = new String[cursor.getCount()];
        final String[] breadStrings = new String[cursor.getCount()];
        final String[] priceStrings = new String[cursor.getCount()];


        for (int i = 0; i < cursor.getCount(); i++) {

            iconStrings[i] = cursor.getString(cursor.getColumnIndex(ManageTABLE.COLUMN_Image));
            breadStrings[i] = cursor.getString(cursor.getColumnIndex(ManageTABLE.COLUMN_Bread));
            priceStrings[i] = cursor.getString(cursor.getColumnIndex(ManageTABLE.COLUMN_Price));


            cursor.moveToNext();
        }   // for
        cursor.close();

        ListView menuListView = (ListView) findViewById(R.id.listView);
        MenuAdapter objMenuAdapter = new MenuAdapter(ShowMenuActivity.this,
                iconStrings, breadStrings, priceStrings);
        menuListView.setAdapter(objMenuAdapter);

        //Active When Click ListView
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chooseItem(breadStrings[i], priceStrings[i]);
            }   // event
        });

    }   // listViewController

    private void chooseItem(final String breadString,
                            final String priceString
    ) {

        CharSequence[] mySequences = {"1 ชิ้น", "2 ชิ้น", "3 ชิ้น", "4 ชิ้น", "5 ชิ้น",
                "6 ชิ้น", "7 ชิ้น", "8 ชิ้น", "9 ชิ้น", "10 ชิ้น"};


        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setTitle(breadString);
        objBuilder.setSingleChoiceItems(mySequences, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int intItem = i + 1;    // จำนวนที่สั่ง


                //update to SQLite ภาษาไทยคือ พักไว้ที่ SQLite ยังไม่ขึ้นไปที่ mySQL
                updateOrderToSQLite(breadString, priceString, intItem);

                dialogInterface.dismiss();
                synBreadTABLE();

            }   // event
        });

        objBuilder.show();

    }   // chooseItem

    private void updateOrderToSQLite(String breadString, String priceString, int intItem) {

        strID = getIntent().getStringExtra("ID");
        Log.d("hey", "ID ==> " + strID);
        int intID = Integer.parseInt(strID);
        ManageTABLE objManageTABLE = new ManageTABLE(this);
        String[] resultStrings = objManageTABLE.readAtPosition(intID - 1);

        addValueToSQLite(resultStrings[1],
                resultStrings[2],
                resultStrings[3],
                resultStrings[4],
                breadString,
                priceString,
                Integer.toString(intItem));


    }   // updateOrderToSQLite

    private void addValueToSQLite(String strName, String strSurname,
                                  String strAddress, String strPhone,
                                  String strBread, String strPrice, String strItem) {

        Log.d("hey", "Name = " + strName);
        Log.d("hey", "Surname =" + strSurname);
        Log.d("hey", "Address = " + strAddress);
        Log.d("hey", "Phone = " + strPhone);
        Log.d("hey", "Bread = " + strBread);
        Log.d("hey", "Price = " + strPrice);
        Log.d("hey", "Item = " + strItem);

        //update to SQLite
        DateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date clickDate = new Date();
        String strDate = myDateFormat.format(clickDate);

        try {
            ManageTABLE objManageTABLE = new ManageTABLE(this);
            String[] myResultStrings = objManageTABLE.searchBread(strBread);
            int oldItem = Integer.parseInt(myResultStrings[2]);
            int newItem = Integer.parseInt(strItem) + oldItem;
            String strNewItem = Integer.toString(newItem);

            SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                    MODE_PRIVATE, null);
            objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER,
                    ManageTABLE.COLUMN_id + "=" + Integer.parseInt(myResultStrings[0]), null);

            addOrderToMySQLite(strDate, strName, strSurname, strAddress,
                    strPhone, strBread, strPrice, strNewItem);


        } catch (Exception e) {
            addOrderToMySQLite(strDate, strName, strSurname, strAddress,
                    strPhone, strBread, strPrice, strItem);
        }


    }   // addValueToSQLite

    private void addOrderToMySQLite(String strDate,
                                    String strName,
                                    String strSurname,
                                    String strAddress,
                                    String strPhone,
                                    String strBread,
                                    String strPrice,
                                    String strItem) {
        ManageTABLE objManageTABLE = new ManageTABLE(this);
        objManageTABLE.addNewOrder(strDate, strName, strSurname, strAddress,
                strPhone, strBread, strPrice, strItem);

        Toast.makeText(ShowMenuActivity.this, "Add Order to SQLite Finish", Toast.LENGTH_SHORT).show();
    }


}   // Main Class
