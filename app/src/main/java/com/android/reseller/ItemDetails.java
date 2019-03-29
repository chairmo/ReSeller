package com.android.reseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.reseller.models.Post;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemDetails extends AppCompatActivity {

    public DatabaseReference mDatabase;
    public ProgressDialog progressDialog;
    public TextView name,price,condition,category,description,user;
    public ImageView photo;
    public LinearLayout makeoffer;
    public FirebaseDatabase fbDatabase;
    public FirebaseAuth fbAuth;
    public FirebaseUser fbUser;
    public String userKey,itemkey;
    public Post currentItem;
    public String buyerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        fbDatabase = FirebaseDatabase.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        currentItem = new Post();
        buyerName = "";

   //     final DatabaseReference databaseReference = fbDatabase.getReference("offer");

        final String s[] =getIntent().getStringArrayExtra("Key");
        Log.e("Danish: ",s[0]);

        name = (TextView) findViewById(R.id.itemName);
        price = (TextView) findViewById(R.id.price);
        description = (TextView) findViewById(R.id.description);
        condition = (TextView) findViewById(R.id.condition);
        category = (TextView) findViewById(R.id.category);
        user = (TextView) findViewById(R.id.postedBy);
        photo = (ImageView) findViewById(R.id.prodImg);
        makeoffer = (LinearLayout) findViewById(R.id.makeOfferLayout);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemDetails.this, UserProfile.class);
                intent.putExtra("profileUid", currentItem.getOwner());
                startActivity(intent);
            }
        });


        makeoffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(fbAuth.getCurrentUser() == null){
                Toast.makeText(getBaseContext(), "Error, user not logged in.", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(ItemDetails.this, OfferPage.class);
                intent.putExtra("ITEM_PRICE", currentItem.getPrice());
                intent.putExtra("ITEM_ID", s[0]);
                intent.putExtra("ITEM_NAME", currentItem.getItemName());
                intent.putExtra("ITEM_OWNER", currentItem.getOwner());
                intent.putExtra("BUYER_NAME", buyerName);
                startActivity(intent);
            }

            }
        });


        FirebaseDatabase.getInstance().getReference("posts").child(s[0])
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                currentItem = dataSnapshot.getValue(Post.class);
                if (currentItem != null) {
                    itemkey = currentItem.getImage();
                    userKey = currentItem.getOwner();
                    name.setText(currentItem.getItemName());
                    category.setText("Category: " + currentItem.getCategory().get(0));

                    //condition.setText("Condition: " + currentItem.getCondition());
                    switch (currentItem.getCondition()) {
                        case "1": condition.setText("Condition: New");
                            break;
                        case "2": condition.setText("Condition: Good");
                            break;
                        case "3": condition.setText("Condition: Fair");
                            break;
                        case "4": condition.setText("Condition: Not Good");
                            break;
                        case "5": condition.setText("Condition: Very Bad");
                            break;
                        default: condition.setText("Condition: " + currentItem.getCondition());
                            break;
                    }

                    price.setText("$" + String.format("%.2f", currentItem.getPrice()));
                    description.setText(currentItem.getDescription());
                    user.setText(s[1]);
                    Glide.with(getApplicationContext()).load(currentItem.getImage()).into(photo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    class Offers
    {
        public String offerby, offerfor, itemid; public long offerprice;

    }
}
