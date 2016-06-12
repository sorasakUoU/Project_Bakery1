package kawinpart.sorasak.projectbakery;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HubActivity extends AppCompatActivity implements View.OnClickListener {

    //Explicit
    private ImageView orderImageView, readOrderImageView;
    private String idString;    // Receive id ที่ user login อยู่


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        //Bind Widget
        bindWidget();

        //Image Controller
        imageController();

    } // onCreate

    private void imageController() {

        //Receive ID user Login
        idString = getIntent().getStringExtra("ID");
        orderImageView.setOnClickListener(this);
        readOrderImageView.setOnClickListener(this);


    }

    private void bindWidget() {
        orderImageView = (ImageView) findViewById(R.id.imageView);
        readOrderImageView = (ImageView) findViewById(R.id.imageView2);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imageView:

                //Order Bread
                Intent objIntent = new Intent(HubActivity.this, ShowMenuActivity.class);
                objIntent.putExtra("ID", idString);
                startActivity(objIntent);

                break;
            case R.id.imageView2:
                //Read Order
                clickReadOrder();

                break;
        }   // switch

    }   // onClick

    private void clickReadOrder() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);

        if (objCursor.getCount() > 0) {
            Intent objIntent = new Intent(HubActivity.this, ConfirmOrderActivity.class);
            objIntent.putExtra("Status", true);
            startActivity(objIntent);
        } else {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(HubActivity.this,  "กรุณา Order", "กรุณาสั่ง ขนมด้วยคะ");
        }
    }
}   // Main Class

