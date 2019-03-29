package com.android.reseller;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemFormPage1 extends AppCompatActivity {

    private static final String CONTENT_PROVIDER_AUTHORITY = "com.android.reseller.fileprovider";

    private Bundle bundle;
    private EditText itemName;
    private String image;
    String cameraFilepath; // uri for temp imagefile for use when taking photo with camera
    File cameraFile;
    Bitmap newPhoto;

    private FirebaseAuth fbAuth;

    public ImageView imageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_form_page1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fbAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fbAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }

        Button cameraButton = findViewById(R.id.cameraButton);
        Button galleryButton = findViewById(R.id.galleryButton);
        itemName = findViewById(R.id.itemName);
        imageView = findViewById(R.id.imageView);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    cameraFilepath = "";
                    cameraFile = null;
                    try {
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK)
                                .format(new Date());
                        String imageFilename = "JPEG_" + timestamp + "_";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        cameraFile = File.createTempFile(imageFilename, ".jpg", storageDir);
                        cameraFilepath = cameraFile.getAbsolutePath();
                    }
                    catch(IOException e) {
                        notifyUser("Error creating temp file!");
                        return;
                    }
                    if (cameraFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(ItemFormPage1.this,
                                CONTENT_PROVIDER_AUTHORITY, cameraFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getPictureIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getPictureIntent.setType("image/*");
                if (getPictureIntent.resolveActivity(getPackageManager()) != null) {

                    startActivityForResult(getPictureIntent, REQUEST_IMAGE_GALLERY);
                }
            }
        });

        bundle = new Bundle();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        bundle = new Bundle();

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bitmap imageBitmap = (Bitmap)data.getExtras().get("data");
//            imageView.setImageBitmap(imageBitmap);
//
//            bundle.putBundle("IMAGE", data.getExtras());

            File photoFile = new File(cameraFilepath);
 //           Uri photoUri = Uri.fromFile(photoFile);

           Uri photoUri =  getPhotoUri(photoFile);

            this.getContentResolver().notifyChange(photoUri, null);
            ContentResolver cr = this.getContentResolver();
            try
            {
                newPhoto = android.provider.MediaStore.Images.Media.getBitmap(cr, photoUri);

                int photoWidth = newPhoto.getWidth();
                int photoHeight = newPhoto.getHeight();
                while (photoWidth > 1000 || photoHeight > 1000) {
                    photoWidth = photoWidth / 2;
                    photoHeight = photoHeight / 2;
                    newPhoto = Bitmap.createScaledBitmap(newPhoto, photoWidth, photoHeight, true);
                }

                // fix image rotation
                ExifInterface exifInterface = new ExifInterface(cameraFilepath);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                Matrix matrix = new Matrix();
                float rotateValue = 0;
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotateValue = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotateValue = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotateValue = 270;
                    default:
                        break;
                }
                if (rotateValue != 0) {
                    matrix.postRotate(rotateValue);
                    newPhoto = Bitmap.createBitmap(newPhoto, 0, 0, photoWidth, photoHeight, matrix, true);
                }


                imageView.setImageBitmap(newPhoto);

                bundle.putString("IMAGE", cameraFilepath);

            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.d("Result of Camera: ", "Failed to load", e);
            }
        }
        else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(imageBitmap);

                bundle.putString("IMAGE_URI", data.getData().toString());
            } catch(FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong in creating input stream.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void toPageTwo(View view) {

        if(bundle.getString("IMAGE") == null && bundle.getString("IMAGE_URI") == null) {
            Toast.makeText(this, "Please add image.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(itemName.getText().length() == 0) {
            Toast.makeText(this, "Please add item name.", Toast.LENGTH_SHORT).show();
            return;
        }
        bundle.putString("ITEM_NAME", itemName.getText().toString());
        Intent intent = new Intent(this, ItemFormPage2.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void notifyUser(String message) {
        Toast.makeText(ItemFormPage1.this, message, Toast.LENGTH_SHORT).show();
    }

    /**Method for handling file **/
    public static Uri getPhotoUri(File file) {
        Uri outputUri = Uri.fromFile(file);
        Uri.Builder builder = new Uri.Builder()
                .authority(CONTENT_PROVIDER_AUTHORITY)
                .scheme("file")
                .path(outputUri.getPath())
                .query(outputUri.getQuery())
                .fragment(outputUri.getFragment());

        return builder.build();
    }
}