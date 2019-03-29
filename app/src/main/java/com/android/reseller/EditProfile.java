package com.android.reseller;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.reseller.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfile extends AppCompatActivity {

    private FirebaseDatabase fbDatabase;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private FirebaseStorage fbStorage;
    private User currentUser;

    private EditText editFirstName, editLastName;
    private EditText editPhone;
    private EditText editCity;
    private EditText editState;
    private ImageView profilePicture;
    private Button newPhotoButton;
    private Button clearPhotoButton;



    public final int SAVE_CHANGES_CODE = 1;
    public final int REVERT_CHANGES_CODE = 2;
    public final int EXIT_CODE = 3;

    public final int REQUEST_IMAGE_CAPTURE = 4;
    public final int REQUEST_IMAGE_GALLERY = 5;

    // these ints are used when save button is clicked
    // we will check image state and upload or delete photo to/from firebase depending on "imageState"
    public final int NEW_FROM_CAMERA = 6;
    public final int NEW_FROM_GALLERY = 7;
    public final int CLEAR_PHOTO = 8;
    public final int NO_CHANGE = 9;
    public int imageState = NO_CHANGE;
    public Bitmap newPhoto = null;
    public Bitmap oldPhoto = null;
    String cameraFilepath; // uri for temp imagefile for use when taking photo with camera
    File cameraFile;
    // need variables here to store bitmap (from camera) or uri (from gallery)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //set toolbar and enable up button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //homeAsUp back navigation
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(EditProfile.this, UserProfile.class));
            }
        });


//        android.support.v7.app.ActionBar bar = getSupportActionBar();
 //       bar.setHomeButtonEnabled(true);


        editFirstName = findViewById(R.id.editProfileEditFirstName);
        editLastName = findViewById(R.id.editProfileLastName);
        editPhone = findViewById(R.id.editProfileEditPhone);
        editCity = findViewById(R.id.editProfileEditCity);
        editState = findViewById(R.id.editProfileEditState);
        profilePicture = findViewById(R.id.viewProfilePicture);
        newPhotoButton = findViewById(R.id.newPhotoButton);
        clearPhotoButton = findViewById(R.id.clearPhotoButton);

        fbDatabase = FirebaseDatabase.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbStorage = FirebaseStorage.getInstance();

        currentUser = new User();

        if (fbUser == null) {
            // no user logged in, send them to login screen
            notifyUser("No user logged in");
            Intent intent = new Intent(this, Authentication.class);
            startActivity(intent);
        }
        else {
            fbDatabase.getReference("users").child(fbUser.getUid()).addListenerForSingleValueEvent(userListener);
        }

        newPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // use dialog to let user pick between camera or gallery
                // save corresponding variable in onActionResult function
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(EditProfile.this, android.R.style.Theme_Material_Dialog_Alert);
                }
                else {
                    builder = new AlertDialog.Builder(EditProfile.this);
                }
                builder.setTitle("Upload a Profile Picture");
                builder.setMessage("Where would you like to upload the image from?");
                // neutral, negative, and positive buttons are mixed up just to give proper button order
                builder.setNeutralButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // open camera activity
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            cameraFilepath = "";
                            cameraFile = null;
                            try {
                                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
                                Uri photoUri = FileProvider.getUriForFile(EditProfile.this,
                                        "com.android.reseller.fileprovider", cameraFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
//                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                });
                builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // open gallery activity
                        Intent getPictureIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        getPictureIntent.setType("image/*");
                        if (getPictureIntent.resolveActivity(getPackageManager()) != null) {

                            startActivityForResult(getPictureIntent, REQUEST_IMAGE_GALLERY);
                        }
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing, it will close on its own
                    }
                });
                builder.setIcon(R.drawable.ic_menu_camera);
                builder.show();
            }
        });

        clearPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // let save handler know that we should delete user image
                imageState = CLEAR_PHOTO;
                profilePicture.setImageResource(R.color.lightGrey);
                Toast.makeText(EditProfile.this, "Profile picture cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            currentUser = dataSnapshot.getValue(User.class);
            if (currentUser != null) {
                // set input values to current User data, so they only change what they want
                editFirstName.setText(currentUser.getFirstName());
                editLastName.setText(currentUser.getLastName());
                editPhone.setText(currentUser.getPhoneNumber());
                editCity.setText(currentUser.getCity());
                editState.setText(currentUser.getState());

                // get user profile picture uri (as a string) from firebase
                if (currentUser.getPhoto() != null && !(currentUser.getPhoto().equals(""))) {
                    StorageReference photoRef = fbStorage.getReferenceFromUrl(currentUser.getPhoto());
                    final long TWO_MEGABYTES = 1024 * 2048;
                    photoRef.getBytes(TWO_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            oldPhoto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            profilePicture.setImageBitmap(oldPhoto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            notifyUser("Error loading profile picture from database.");
                            // handle errors
                        }
                    });
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // do nothing
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // handle image from camera
            imageState = NEW_FROM_CAMERA;

            File photoFile = new File(cameraFilepath);
            Uri photoUri = Uri.fromFile(photoFile);

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
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
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


                profilePicture.setImageBitmap(newPhoto);
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.d("Result of Camera: ", "Failed to load", e);
            }

        }
        else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            // handle image from gallery
            imageState = NEW_FROM_GALLERY;

            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                newPhoto = BitmapFactory.decodeStream(imageStream);

                int photoWidth = newPhoto.getWidth();
                int photoHeight = newPhoto.getHeight();
                while (photoWidth > 1000 || photoHeight > 1000) {
                    photoWidth = photoWidth / 2;
                    photoHeight = photoHeight / 2;
                    newPhoto = Bitmap.createScaledBitmap(newPhoto, photoWidth, photoHeight, true);
                }

                try {
                    // fix image rotation
                    ExifInterface exifInterface = new ExifInterface(imageUri.toString());
                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
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
                }
                catch (IOException e) {
                    //notifyUser("Failed to get EXIF data from gallery image");
                    // rotate 90 degrees and hope for the best
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    newPhoto = Bitmap.createBitmap(newPhoto, 0, 0, photoWidth, photoHeight, matrix, true);
                }


                profilePicture.setImageBitmap(newPhoto);

            } catch(FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong in creating input stream.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add options to save and revert changes to action bar
        MenuItem saveMenuItem = menu.add(1, SAVE_CHANGES_CODE, 101, "Save Changes");
        saveMenuItem.setIcon(R.drawable.ic_save);
        saveMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        MenuItem revertMenuItem = menu.add(1, REVERT_CHANGES_CODE, 102, "Revert Changes");
        revertMenuItem.setIcon(R.drawable.ic_refresh);
        revertMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        MenuItem exitMenuItem = menu.add(1, EXIT_CODE, 103, "Exit");
        exitMenuItem.setIcon(R.drawable.ic_exit);
        exitMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == SAVE_CHANGES_CODE) {
            currentUser.setFirstName(editFirstName.getText().toString());
            currentUser.setLastName(editLastName.getText().toString());
            currentUser.setPhoneNumber(editPhone.getText().toString());
            currentUser.setCity(editCity.getText().toString());
            currentUser.setState(editState.getText().toString());

            if (imageState != NO_CHANGE) {
                // this means there was a change in imageState

                if (imageState == CLEAR_PHOTO) {
                    // delete photo from firebase
                    StorageReference deleteFile = fbStorage.getReferenceFromUrl(currentUser.getPhoto());
                    deleteFile.delete().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            notifyUser("Failed to delete photo. Please try again.");
                        }
                    });

                    // set user photo to empty string
                    currentUser.setPhoto("");

                }
                else if (imageState == NEW_FROM_GALLERY || imageState == NEW_FROM_CAMERA) {
                    // upload photo to firebase, set new photo url

                    // resize image if its too large, as the displayed images in app are small
                    int photoWidth = newPhoto.getWidth();
                    int photoHeight = newPhoto.getHeight();
                    while (photoWidth > 1000 || photoHeight > 1000) {
                        photoWidth = photoWidth / 2;
                        photoHeight = photoHeight / 2;
                        newPhoto = Bitmap.createScaledBitmap(newPhoto, photoWidth, photoHeight, true);
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    newPhoto.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    final StorageReference imageRef = fbStorage.getReference().child("profileimages/"+fbUser.getUid()+".png");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            //Continue with the task to get the download URL
                            return imageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                fbDatabase.getReference("users").child(fbUser.getUid())
                                        .child("photo").setValue(downloadUri);
                            } else {
                                notifyUser("Image upload failed. Please try again.");
                                currentUser.setPhoto("");
                                Log.e("EditProfile", "Image upload failed. Exception: ");
                            }
                        }
                    });
            /*
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            notifyUser("Image upload failed. Please try again.");
                            currentUser.setPhoto("");
                            Log.e("EditProfile", "Image upload failed. Exception: " + e.getMessage());
                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String photoUrl = taskSnapshot.getDownloadUrl().toString();
                            fbDatabase.getReference("users").child(fbUser.getUid()).child("photo").setValue(photoUrl);
                        }
                    });  */

                }
            }


            fbDatabase.getReference("users").child(fbUser.getUid()).setValue(currentUser);

            // Go to User Profile when changes are saved.
            notifyUser("Changes saved.");
            finish();
        }
        else if (id == REVERT_CHANGES_CODE) {
            editFirstName.setText(currentUser.getFirstName());
            editLastName.setText(currentUser.getLastName());
            editPhone.setText(currentUser.getPhoneNumber());
            editCity.setText(currentUser.getCity());
            editState.setText(currentUser.getState());

            // reload image from firebase storage
            // should store image as bitmap or similar in memory to avoid another download
            imageState = NO_CHANGE;
            notifyUser("All changes reverted.");
            profilePicture.setImageBitmap(oldPhoto);
        }
        else if (id == EXIT_CODE) {
            notifyUser("Changes not saved.");
            finish();
        }
        return true;
    }

    private void notifyUser(String message) {
        Toast.makeText(EditProfile.this, message, Toast.LENGTH_SHORT).show();
    }

}
