package com.android.reseller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.reseller.models.Post;
import com.android.reseller.models.User;
import com.android.reseller.ui.activities.ChatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    private String profileUid;
    private TextView userName;
    private TextView userEmail;
    private TextView userPhone;
    private TextView userCityState;
    private FirebaseAuth fbAuth;
    private FirebaseDatabase database;
    static private User dbUser;
    private FirebaseUser user;
    @SuppressLint("StaticFieldLeak")
    static RatingBar ratingBar;
    private boolean flag;
    private Menu menu;


    public String firebaseToken;

    public ArrayList<Post> userPostList;
    public ArrayList<String> userPostIdList;

    public final int EDIT_PROFILE = 1;
    public final int CHAT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //set toolbar and enable up button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //homeAsUp back navigation
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // profileUid is the ID of the profile we are viewing, doesn't have to be the same as
        // the current logged in user
        profileUid = getIntent().getStringExtra("profileUid");
        userPostList = new ArrayList<>();
        userPostIdList = new ArrayList<>();

        //User Profile display
        userName = findViewById(R.id.userNameText);
        userEmail = findViewById(R.id.userEmailText);
        userPhone = findViewById(R.id.userPhoneText);
        userCityState = findViewById(R.id.userLocationText);

        LinearLayout rating = findViewById(R.id.rating);



        ratingBar = findViewById(R.id.ratingsbar);
        flag = true;
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    RatingBarDialog ratingBarDialog = new RatingBarDialog();
                    Bundle args = new Bundle();
                    args.putString("profileUid", profileUid);
                    ratingBarDialog.setArguments(args);
                    ratingBarDialog.show(getSupportFragmentManager(), "rating");
                }

            }
        });


        database = FirebaseDatabase.getInstance();
        fbAuth = FirebaseAuth.getInstance();
         user = fbAuth.getCurrentUser();


        dbUser = new User(user.getDisplayName(), user.getEmail());
 //       dbUser = new User();


        ImageView userImg = findViewById(R.id.userpic);

        Picasso.with(getApplicationContext())
                .load("http://via.placeholder.com/350x350")
                .transform(new RoundedTransformation(50, 10))
                .fit()
                .centerCrop().into(userImg);

        // we should receive userid from intent here in case we are viewing a profile that isn't our own
        // then we can get that user's information instead of our logged in accounts information
        if (profileUid != null) {
            database.getReference("users").child(profileUid).addListenerForSingleValueEvent(userListener);
            database.getReference("users").child(profileUid).child("photo").addValueEventListener(profilePictureListener);
            database.getReference("posts").addListenerForSingleValueEvent(postListener);
        } else {
            // bad
            // userid should be passed via intent to this activity
            // this code
            // WHICH SHOULD NOT HAPPEN IF WE WANT TO VIEW A DIFFERENT USER PROFILE
            if (user != null) {
                database.getReference("users").child(user.getUid()).addListenerForSingleValueEvent(userListener);
                database.getReference("users").child(user.getUid()).child("photo").addValueEventListener(profilePictureListener);
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        //Toast.makeText(this, "onCreateOptionsMenu", Toast.LENGTH_SHORT).show();

        if (menu != null && menu.findItem(1) == null) {
//            FirebaseUser firebaseUser = fbAuth.getCurrentUser();

    /*        if (firebaseUser != null && dbUser != null)
                if (dbUser.getEmail().equals(firebaseUser.getEmail())) {
                    // if profile email matches current logged in user email, then show edit profile button
                    flag = false;
                    MenuItem editProfile = menu.add(1, 1, 101, "Edit Profile");
                    editProfile.setIcon(R.drawable.edit);
                    editProfile.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                }  */
           if (user != null && user.getEmail().equals(dbUser.getEmail())) {
                // if profile email matches current logged in user email, then show edit profile button
                flag =false;
                MenuItem editProfile = menu.add(1, 1, 101, "Edit Profile");
                editProfile.setIcon(R.drawable.edit);
                editProfile.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
                else {
                    // if profile email does not match current logged in user email, then show chat option
                    MenuItem chat = menu.add(2, 2, 102, "Chat With This User");
                    chat.setIcon(R.drawable.ic_action_chat);
                    chat.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == EDIT_PROFILE) {
            Intent intent = new Intent(UserProfile.this, EditProfile.class);
            startActivity(intent);
        } else if (id == CHAT) {
            fbAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    firebaseToken = getTokenResult.getToken();
                    ChatActivity.startActivity(UserProfile.this,
                            dbUser.getEmail(),
                            profileUid,
                            firebaseToken);
                }
            });
        }
        return true;
    }

    static public void addRatingToUser(float newRating, String profileUid) {

        // calculate new rating within User class

        Log.e("UserProfile: ", "Added a new rating of: " + String.valueOf(newRating));

        dbUser.setRating((double) newRating);

        // log info

        Log.e("UserProfile: ", "New total rating: " + String.valueOf(dbUser.getRating()));

        // add new rating and ratingCount to firebase
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(profileUid);
        userRef.child("rating").setValue(dbUser.getRating());
        userRef.child("ratingCount").setValue(dbUser.getRatingCount());

        // set the stars of the ratingBar view
        ratingBar.setRating((float) dbUser.getRating());

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseUser user = fbAuth.getCurrentUser();
        if (user != null) {
//            database.getReference("users").child(user.getUid()).addListenerForSingleValueEvent(userListener);
            database.getReference("users").child(profileUid).addListenerForSingleValueEvent(userListener);
        }

    }

    ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            dbUser = dataSnapshot.getValue(User.class);
 //           FirebaseUser user = fbAuth.getCurrentUser();
            if (dbUser != null) {

                // get all parameters from the User (to avoid multiple function calls per each parameter)
                userName.setText(dbUser.getFullName());
    //            userName.setText(user.getDisplayName());
                userEmail.setText(dbUser.getEmail());
                String userPhoneNumber = dbUser.getPhoneNumber();
                String userCity = dbUser.getCity();
                String userState = dbUser.getState();
                ratingBar.setRating((float) dbUser.getRating());

                // set all text views to the value from user, granted that the value is not empty
                if (userPhoneNumber != null && !(userPhoneNumber.equals(""))) {
                    userPhone.setText(userPhoneNumber);
                } else {
                    userPhone.setText("No Phone Listed");
                }
                if (userCity != null && !(userCity.equals("")) && userState != null && !(userState.equals(""))) {
                    String cityAndState = userCity + ", " + userState;
                    userCityState.setText(cityAndState);
                } else {
                    userCityState.setText("No Location Listed");
                }

                // add edit profile to action bar if profile email matches logged in user
                // only check if the menu item has not yet been added to action bar
                if (menu != null && menu.findItem(1) == null) {
                    //Toast.makeText(UserProfile.this, "checking if menu is null... ", Toast.LENGTH_SHORT).show();
                    if (menu != null && menu.findItem(1) == null) {
                                         FirebaseUser firebaseUser = fbAuth.getCurrentUser();
                        if (firebaseUser.getEmail().equals(dbUser.getEmail())) {
                            // if profile email matches current logged in user email, then show edit profile button
                            flag = false;
                            MenuItem editProfile = menu.add(1, 1, 101, "Edit Profile");
                            editProfile.setIcon(R.drawable.edit);
                            editProfile.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                        }
                    }

                    // get user profile picture uri (as a string) from firebase
                    String photoString = dbUser.getPhoto();
                    if (!photoString.equals("")) {
                        // if string exists (not empty) then load image into imageview
                        Uri photoUrl = Uri.parse(photoString);
                        ImageView profilePicture = findViewById(R.id.userpic);
                        Picasso.with(UserProfile.this).load(photoUrl).into(profilePicture);
                    }
                    // if string empty, don't load anything
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    // listen for changes to "photo" field for current user in database, get profile picture
    ValueEventListener profilePictureListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // get user profile picture uri (as a string) from firebase
            String photoString = dataSnapshot.getValue(String.class);
            if (photoString != null && !photoString.equals("")) {
                // if string exists (not empty) then load image into imageview
                Uri photoUrl = Uri.parse(photoString);
                ImageView profilePicture = findViewById(R.id.userpic);
                Picasso.with(UserProfile.this).load(photoUrl).into(profilePicture);
            } else if (photoString != null && photoString.equals("")) {
                // clear photo back to placeholder
                ImageView profilePicture = findViewById(R.id.userpic);
                profilePicture.setImageResource(R.color.lightGrey);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // do nothing
        }
    };

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // loop over all posts
            // if owner matches profile we are viewing, add them to list
            for (DataSnapshot postRef : dataSnapshot.getChildren()) {
                Post post = postRef.getValue(Post.class);
                if (post != null && post.getOwner().equals(profileUid)) {
                    // keep separate list of posts and postIds because the posts themselves do not
                    // store their own ids. However these will be in the same order in both lists
                    // so that we can easily access the id of whichever post we want
                    userPostList.add(post);
                    userPostIdList.add(postRef.getKey());
                }
            }

            // set grid image adapter, pass in the list
            GridView gridViewPosts = findViewById(R.id.gridview);
            gridViewPosts.setAdapter(new ImageAdapter(UserProfile.this, userPostList));
            gridViewPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    String postKey = userPostIdList.get(position);
                    String ownerKey = dbUser.getFullName();
                    //Toast.makeText(getApplicationContext(), postKey,
                    //Toast.LENGTH_SHORT).show();

                    // TODO: Start Activity to view item <postKey>
                    // TODO: Remove Toast

                    Intent intent = new Intent(getApplicationContext(), ItemDetails.class);
                    String s[] = {postKey, ownerKey};
                    intent.putExtra("Key", s);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    class RoundedTransformation implements com.squareup.picasso.Transformation {
        private final int radius;
        private final int margin;  // dp

        // radius is corner radii in dp
        // margin is the board in dp
        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key() {
            return "rounded";
        }
    }
}

