package kawinpart.sorasak.projectbakery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    //Explicit
    private EditText userEditText, passwordEditText, nameEditText,
            surnameEditText, addressEditText, phoneEditText;
    private String userString, passwordString, nameString,
            surnameString, addressString, phoneString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Bind Widget
        bindWidget();

    }   // onCreate

    public void clickCheck(View view) {

        userString = userEditText.getText().toString().trim();
        if (userString.equals("")) {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(RegisterActivity.this, "User ว่าง", "กรุณากรอก ที่ช่อง User ด้วย");
        } else {

            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            if (checkUser()) {
                objMyAlertDialog.errorDialog(RegisterActivity.this, "ไม่สามารถใช้ชื่อนี่ได้", "เปลี่ยน User ใหม่ มีคนอื่นใช้แล้ว");
            } else {
                objMyAlertDialog.errorDialog(RegisterActivity.this, "สามารถใช้ชื่อนี่ได้", "กรอกให้ครบทุกช้องแล้ว Save เลย");
            }

        }

    }   // clickCheck

    private boolean checkUser() {

        try {
            //Have This User in my Database
            ManageTABLE objManageTABLE = new ManageTABLE(this);
            String[] resultStrings = objManageTABLE.searchUser(userString);
            Log.d("hey", "Name ==> " + resultStrings[3]);

            return true;
        } catch (Exception e) {
            //No This User in my Database
            return false;
        }

        //return false;
    }   //checkUser

    public void clickSave(View view) {

        //Check Space
        userString = userEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();
        nameString = nameEditText.getText().toString().trim();
        surnameString = surnameEditText.getText().toString().trim();
        addressString = addressEditText.getText().toString().trim();
        phoneString = phoneEditText.getText().toString().trim();

        if (userString.equals("") ||
                passwordString.equals("") ||
                nameString.equals("") ||
                surnameString.equals("") ||
                addressString.equals("") ||
                phoneString.equals("")) {

            //Have Space
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(RegisterActivity.this, "Have Space", "กรุณากรอกทุกช่อง คะ");

        } else {

            //No Space
            if (checkUser()) {
                MyAlertDialog objMyAlertDialog = new MyAlertDialog();
                objMyAlertDialog.errorDialog(RegisterActivity.this, "ไม่สามารถใช้ชื่อนี่ได้", "เปลี่ยน User ใหม่ มีคนอื่นใช้แล้ว");
            } else {
                confirmRegister();
            }

        } // if

    }   // clickSave

    private void confirmRegister() {

        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.icon_myaccount);
        objBuilder.setTitle("โปรดตรวจสอบข้อมูล");
        objBuilder.setMessage("User = " + userString + "\n" +
                "Password = " + passwordString + "\n" +
                "Name = " + nameString + "\n" +
                "Surname = " + surnameString + "\n" +
                "Address = " + addressString + "\n" +
                "Phone = " + phoneString + "\n");
        objBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                upDataMySQL();
                dialogInterface.dismiss();
            }
        });
        objBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        objBuilder.show();

    }   // confirmRegister

    private void upDataMySQL() {

        StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(myPolicy);

        try {

            String strURL = "http://swiftcodingthai.com/mos/php_add_data_master.php";

            ArrayList<NameValuePair> objNameValuePairs = new ArrayList<NameValuePair>();
            objNameValuePairs.add(new BasicNameValuePair("isAdd", "true"));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_User, userString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Password, passwordString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Name, nameString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Surname, surnameString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Address, addressString));
            objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Phone, phoneString));

            HttpClient objHttpClient = new DefaultHttpClient();
            HttpPost objHttpPost = new HttpPost(strURL);
            objHttpPost.setEntity(new UrlEncodedFormEntity(objNameValuePairs, "UTF-8"));
            objHttpClient.execute(objHttpPost);

            Toast.makeText(RegisterActivity.this, "Update Finish", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(RegisterActivity.this, "Cannot Update mySQL", Toast.LENGTH_SHORT).show();
        }

        //Intent To MainActivity
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));

    }   // upDateMySQL

    private void bindWidget() {
        userEditText = (EditText) findViewById(R.id.editText);
        passwordEditText = (EditText) findViewById(R.id.editText2);
        nameEditText = (EditText) findViewById(R.id.editText5);
        surnameEditText = (EditText) findViewById(R.id.editText6);
        addressEditText = (EditText) findViewById(R.id.editText7);
        phoneEditText = (EditText) findViewById(R.id.editText8);
    }

}   // Main Class
