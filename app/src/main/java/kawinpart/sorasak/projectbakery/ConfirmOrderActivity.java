package kawinpart.sorasak.projectbakery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ConfirmOrderActivity extends AppCompatActivity {

    //Explicit
    private TextView dateTextView, nameTextView, addressTextView,
            phoneTextView, totalTextView, idReceiveTextView;
    private String dateString, nameString, surnameString, addressString,
            phoneString, totalString;
    private ListView orderListView;
    private int totalAnInt = 0;
    private String strCurrentIDReceive;
    private Button moreButton, finishButton;
    private boolean visibleStatus = false;
    private String strDate;
    private String strIDuser;
    private String strOrderNo;
    private int orderDetailAnInt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        strIDuser = getIntent().getStringExtra("idUser");

        //Bind Widget
        bindWidget();

        //Check Visible Button
        checkVisible();

        //Read ALL Data
        readAllData();

        //Find ID receive
        findIDreceive();

        //Show View
        showView();

        //Find Last OrderNo
        findLastOrderNo();

    }   // Main Method

    public class ConnectedOrderDetail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://swiftcodingthai.com/mos/php_get_last_orderdetail.php").build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("12April", "doInBack ==> " + e.toString());
                return null;
            }

        }   // doInBack

        @Override
        protected void onPostExecute(String strJSON) {
            super.onPostExecute(strJSON);

            try {

                JSONArray jsonArray = new JSONArray(strJSON);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                strOrderNo = jsonObject.getString("OrderNo");
                Log.d("12April", "ค่าของ OrderNo ล่าสุดที่อ่านได้ ==> " + strOrderNo);

            } catch (Exception e) {
                Log.d("12April", "onPost ==> " + e.toString());
            }

        }   // onPost

    }   // Connected Class

    private void findLastOrderNo() {

        ConnectedOrderDetail connectedOrderDetail = new ConnectedOrderDetail();
        connectedOrderDetail.execute();


    }

    private void checkVisible() {

        try {

            boolean myStatus = getIntent().getBooleanExtra("Status", false);

            if (myStatus) {

                moreButton.setVisibility(View.INVISIBLE);
                finishButton.setVisibility(View.INVISIBLE);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }   // checkVisible

    private void findIDreceive() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER_FINISH, null);
        objCursor.moveToFirst();
        objCursor.moveToLast();

        String strIDreceive = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_idReceive));
        Log.d("Receive", "Receive Last = " + strIDreceive);

        String[] idReceiveStrings = strIDreceive.split("#");
        strCurrentIDReceive = idReceiveStrings[0] + "#" + Integer.toString((Integer.parseInt(idReceiveStrings[1]) + 1));
        idReceiveTextView.setText(strCurrentIDReceive);
        Log.d("Receive", "Receive current = " + strCurrentIDReceive);


        objCursor.close();


    }   // findIDreceive

    public void clickFinish(View view) {


        //Read All orderTABLE
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);
        objCursor.moveToFirst();

        //****************************************************************************************
        // Update Stock
        //****************************************************************************************

        for (int i = 0; i < objCursor.getCount(); i++) {

            strDate = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Date));
            String strName = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Name));
            String strSurname = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Surname));
            String strAddress = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Address));
            String strPhone = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Phone));
            String strBread = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread));
            String strPrice = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Price));
            String strItem = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Item));

            //Update to mySQL
            StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy
                    .Builder().permitAll().build();
            StrictMode.setThreadPolicy(myPolicy);


            // Update orderTABLE_mos
            try {

                ArrayList<NameValuePair> objNameValuePairs = new ArrayList<NameValuePair>();
                objNameValuePairs.add(new BasicNameValuePair("isAdd", "true"));
                objNameValuePairs.add(new BasicNameValuePair("idReceive", strCurrentIDReceive));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Date, strDate));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Name, strName));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Surname, strSurname));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Address, strAddress));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Phone, strPhone));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Bread, strBread));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Price, strPrice));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Item, strItem));

                HttpClient objHttpClient = new DefaultHttpClient();
                HttpPost objHttpPost = new HttpPost("http://swiftcodingthai.com/mos/php_add_order_master.php");
                objHttpPost.setEntity(new UrlEncodedFormEntity(objNameValuePairs, "UTF-8"));
                objHttpClient.execute(objHttpPost);

                if (i == (objCursor.getCount() - 1)) {
                    Toast.makeText(ConfirmOrderActivity.this, "Update Order Finish",
                            Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                Log.d("hey", "Error Cannot Update to mySQL ==> " + e.toString());
            }   // end of TryCase 1


            try {

                //Find Id Bread
                ManageTABLE objManageTABLE = new ManageTABLE(this);
                String[] resultStrings = objManageTABLE.searchBread(strBread);
                Log.d("11April", "id bread ที่สั่งได้" + strBread + " " + resultStrings[0]);


            } catch (Exception e) {
                Log.d("16Feb", "Cannot Delete Stock");
            }

            objCursor.moveToNext();


            //****************************************************************************************
            // Update tborderdetail
            //****************************************************************************************


            //Update to tborderdetail on Server
            Log.d("12April", "clickFinish OrderNo ล่าสุดที่อ่านได้ ==> " + strOrderNo);
            int intOrderNo = Integer.parseInt(strOrderNo) + 1;
            String strNextOrderNo = Integer.toString(intOrderNo);

            orderDetailAnInt += 1;
            Log.d("12April", "OrderDetailID(" + (i + 1) + ")" + orderDetailAnInt);
            String strOrderDetail = Integer.toString(orderDetailAnInt);

            String strProductID = findProductID(strBread);
            Log.d("12April", strBread + " มี id = " + strProductID);


            int intAmount = Integer.parseInt(strItem);
            int intPrice = Integer.parseInt(strPrice);
            int PriceTotal = intAmount * intPrice;
            String strPriceTotal = Integer.toString(PriceTotal);

            Log.d("12April", "Amount * Price = " + intAmount + " x " + intPrice + " =  " + PriceTotal);

            updateTotborderdetail(strNextOrderNo,
                    strOrderDetail,
                    strProductID,
                    strItem,
                    strPrice,
                    strPriceTotal);

        }   // for
        objCursor.close();

        //****************************************************************************************
        // จุดเปลี่ยน
        //****************************************************************************************


        //Update to tborder on Server
        updateTotborder(strDate,
                strIDuser,
                Integer.toString(totalAnInt),
                "รอการชำระ");


        // Intent HubActivity
        Intent objIntent = new Intent(ConfirmOrderActivity.this, HubActivity.class);
        String strID = getIntent().getStringExtra("idUser");
        objIntent.putExtra("ID", strID);

        Log.d("19Feb", "ID ที่ได้ ==> " + strID);

        startActivity(objIntent);

        //Delete orderTABLE
        objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER, null, null);


    }   // clickFinish

    private void updateTotborderdetail(String strOrderNo,
                                       String strOrderDetail_ID,
                                       String strProductID,
                                       String strAmount,
                                       String strPrice,
                                       String strpriceTotal) {

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("isAdd", "true")
                .add("OrderNo", strOrderNo)
                .add("OrderDetail_ID", strOrderDetail_ID)
                .add("Product_ID", strProductID)
                .add("Amount", strAmount)
                .add("Price", strPrice)
                .add("PriceTotal", strpriceTotal)
                .build();

        Request.Builder builder = new Request.Builder();
        final Request request = builder.url("http://swiftcodingthai.com/mos/php_add_tborderdetail.php")
                .post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("12April", "Fail to Upload");
            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {
                    Log.d("12April", "Finish to Upload" + response.body().string());
                } catch (Exception e) {
                    Log.d("12April", "Error upload ==> " + e.toString());
                }

            }
        });

    }   // updateTo

    private String findProductID(String strBread) {

        String strProductID = null;

        try {

            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                    MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM breadTABLE WHERE Bread = " + "'" + strBread + "'", null);
            cursor.moveToFirst();
            strProductID = cursor.getString(0);
            return strProductID;

        } catch (Exception e) {
            return null;
        }


    }

    private void updateTotborder(String strDate,
                                 String strIDuser,
                                 String strSumtotal,
                                 String strStatus) {

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        try {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("isAdd", "true"));
            nameValuePairs.add(new BasicNameValuePair("OrderDate", strDate));
            nameValuePairs.add(new BasicNameValuePair("CustomerID", strIDuser));
            nameValuePairs.add(new BasicNameValuePair("GrandTotal", strSumtotal));
            nameValuePairs.add(new BasicNameValuePair("Status", strStatus));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://swiftcodingthai.com/mos/php_add_tborder_master.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            httpClient.execute(httpPost);

            Log.i("11April", "Update Finish");

        } catch (Exception e) {
            Log.i("11April", "ไม่สามารถอัพไปที่ tborder ได้ จาก " + e.toString());
        }


    }   // updateTotborder

    private void updateBreadStock(String strBread, String strItem) {

        String tag = "updateBreadStock";
        int intCurrentStock;
        String strCurrentStock;
        String strID;

        //หา ID ของ Bread
        try {

            ManageTABLE manageTABLE = new ManageTABLE(this);
            String[] resultBread = manageTABLE.searchBreadStock(strBread);

            strID = resultBread[0];
            Log.d(tag, "ID bread ==> " + strID);

            intCurrentStock = Integer.parseInt(resultBread[2]) - Integer.parseInt(strItem);
            strCurrentStock = Integer.toString(intCurrentStock);

            Log.d(tag, "Current Storck ==> " + strCurrentStock);

            //Edit Value on breadTABLE
            editValueOnBreadTABLE(strID, strCurrentStock);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }   // updateBreadStock

    private void editValueOnBreadTABLE(String strID, String strCurrentStock) {

        String tag = "updateBreadStock";

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        try {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("isAdd", "true"));
            nameValuePairs.add(new BasicNameValuePair("id", strID));
            nameValuePairs.add(new BasicNameValuePair("Amount", strCurrentStock));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://swiftcodingthai.com/mos/php_edit_stock_mos.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            httpClient.execute(httpPost);

            Log.d(tag, "Edit Finish");

        } catch (Exception e) {
            Log.d(tag, "ไม่สามารถ Edit ได้ " + e.toString());
        }


    }   // editValueOnBreadTABLE

    public void clickMore(View view) {
        finish();
    }

    private void showView() {
        dateTextView.setText("วันที่ " + dateString);
        nameTextView.setText(nameString + " " + surnameString);
        addressTextView.setText("ที่อยู่ " + addressString);
        phoneTextView.setText("Phone = " + phoneString);
        totalTextView.setText(Integer.toString(totalAnInt));
    }

    private void readAllData() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM orderTABLE", null);
        objCursor.moveToFirst();
        dateString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Date));
        nameString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Name));
        surnameString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Surname));
        addressString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Address));
        phoneString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Phone));

        String[] nameOrderStrings = new String[objCursor.getCount()];
        String[] priceStrings = new String[objCursor.getCount()];
        String[] itemStrings = new String[objCursor.getCount()];
        String[] noStrings = new String[objCursor.getCount()];
        String[] amountStrings = new String[objCursor.getCount()];


        for (int i = 0; i < objCursor.getCount(); i++) {

            nameOrderStrings[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread));
            priceStrings[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Price));
            itemStrings[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Item));
            noStrings[i] = Integer.toString(i + 1);
            amountStrings[i] = Integer.toString(Integer.parseInt(itemStrings[i]) * Integer.parseInt(priceStrings[i]));

            objCursor.moveToNext();

            totalAnInt = totalAnInt + Integer.parseInt(amountStrings[i]);

        }   // for

        objCursor.close();

        //Create Listview
        MyOrderAdapter objMyOrderAdapter = new MyOrderAdapter(ConfirmOrderActivity.this,
                noStrings, nameOrderStrings, itemStrings, priceStrings, amountStrings);
        orderListView.setAdapter(objMyOrderAdapter);

        //Delete Order
        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                myDeleteOrder(i);

            }   // event
        });

    }   // readAllData

    private void myDeleteOrder(int position) {


        final SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);
        objCursor.moveToFirst();
        objCursor.moveToPosition(position);
        String strBread = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread));
        String strItem = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Item));
        final String strID = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_id));
        Log.d("Hay", "ID ==> " + strID);


        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.icon_myaccount);
        objBuilder.setTitle("Are You Sure ?");
        objBuilder.setMessage("Delete Order " + strBread + " " + strItem + " ชิ้น");
        objBuilder.setCancelable(false);
        objBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int intID = Integer.parseInt(strID);
                objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER,
                        ManageTABLE.COLUMN_id + "=" + intID, null);
                totalAnInt = 0;
                readAllData();
                totalTextView.setText(Integer.toString(totalAnInt));
                dialogInterface.dismiss();
            }
        });
        objBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        objBuilder.show();

        objCursor.close();

    }   // myDeleteOrder

    private void bindWidget() {

        dateTextView = (TextView) findViewById(R.id.textView18);
        nameTextView = (TextView) findViewById(R.id.textView19);
        addressTextView = (TextView) findViewById(R.id.textView20);
        phoneTextView = (TextView) findViewById(R.id.textView21);
        totalTextView = (TextView) findViewById(R.id.textView22);
        orderListView = (ListView) findViewById(R.id.listView2);
        idReceiveTextView = (TextView) findViewById(R.id.textView15);
        moreButton = (Button) findViewById(R.id.button8);
        finishButton = (Button) findViewById(R.id.button9);

    }   // bindWidget

}   // Main Class
