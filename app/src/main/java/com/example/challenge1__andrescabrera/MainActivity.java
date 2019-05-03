package com.example.challenge1__andrescabrera;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private ImageView imageHolder;
    private EditText textFindImage;
    private final int requestCode = 69;
    private SQLiteOpenHelper camDatabaseHelper;
    private SQLiteDatabase db;
    public int imageCounter = 1;
    ByteArrayOutputStream stream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camDatabaseHelper = new CamDatabaseHelper(this);

        imageHolder = findViewById(R.id.image_display);
        Button btnTakeImage = findViewById(R.id.cam_button);
        Button btnLoadImage = findViewById(R.id.findPic);
        textFindImage = findViewById(R.id.imgID);


        btnTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (photoCaptureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(photoCaptureIntent, requestCode);
                }
            }
        });

        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImageFromDB();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResultTest", "RequestCode: " + String.valueOf(requestCode));
        //resultCode = RESULT_OK;
        Log.d("onActivityResultTest", "resultCode: " + String.valueOf(resultCode));

        if (requestCode == this.requestCode && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageHolder.setImageBitmap(bitmap);
                byte[] bytesOfImages = getBytes(bitmap);

                db = camDatabaseHelper.getWritableDatabase();
                ((CamDatabaseHelper) camDatabaseHelper).insertImageToDB(bytesOfImages, imageCounter);
                imageCounter++;
                db.close();
                Toast toast2 = Toast.makeText(this, "Picture is saved under ID: " + String.valueOf(imageCounter), Toast.LENGTH_LONG);
                toast2.show();
            }   catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Error occured taking/saving photo.", Toast.LENGTH_LONG).show();
            }
        }

    }

    // convert bitmap to bytes
    private byte[] getBytes(Bitmap bitmap) {
        try {
            stream.close();
            stream.reset();
        } catch (Exception ex) {
        }
        stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert byte array to bitmap
    private Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private void loadImageFromDB() {
        long imageID = 0;
        try {
            imageID = Long.parseLong(textFindImage.getText().toString());
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(this, "Invalid ID.", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (imageID != 0) {
            db = camDatabaseHelper.getWritableDatabase();
            Bitmap bitmap = null;

            Cursor cursor = db.rawQuery("SELECT image FROM pictures WHERE id = " + imageID, null);
            if (cursor.moveToFirst()) {
                byte[] blob = cursor.getBlob(cursor.getColumnIndex("image"));
                cursor.close();
                bitmap = getImage(blob);
            }

            if (bitmap != null) {
                imageHolder.setRotation(-270);
                imageHolder.setImageBitmap(bitmap);
            } else Toast.makeText(MainActivity.this, "Invalid ID.", Toast.LENGTH_SHORT).show();

            cursor.close();
            db.close();
        }
    }
}