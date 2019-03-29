package com.android.reseller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.reseller.models.Offer;
import com.android.reseller.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.android.reseller.ui.activities.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class OfferPage extends AppCompatActivity {

    private EditText userOffer;
    private Button offerButton;

    public FirebaseAuth fbAuth;
    public FirebaseUser fbUser;
    public FirebaseDatabase fbDatabase;

    private Offer newOffer;
    private Post currentItem;
    private List<Offer> listOffers;

    public String itemId;
    public String sellerEmail;
    public String buyerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_page);

        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        newOffer = new Offer();
        currentItem = new Post();
        listOffers = new ArrayList<>();
        itemId = getIntent().getStringExtra("ITEM_ID");

        final TextView sellerOffer = findViewById(R.id.sellerOffer);
        sellerOffer.setText(getIntent().getStringExtra("ITEM_PRICE"));

        userOffer = findViewById(R.id.userOffer);
        final TextView itemName = findViewById(R.id.itemName);
 //       final TextView sellerPrice = findViewById(R.id.sellerOffer);

        if (fbUser != null) {
            buyerEmail = fbUser.getEmail();
        }
        offerButton = findViewById(R.id.offerButton);
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send offer to database, and send message to seller

                String sellerId = getIntent().getStringExtra("ITEM_OWNER");
                String buyerName = getIntent().getStringExtra("BUYER_NAME");

                // set values in the Offer object
                newOffer.setBuyerId(fbUser.getUid());
                newOffer.setSellerId(sellerId);
                newOffer.setOfferPrice(userOffer.getText().toString());

                // build message
                String message = buyerName + " is offering $" + newOffer.getOfferPrice() + " for your item: ";
                message += getIntent().getStringExtra("ITEM_NAME");
                newOffer.setMessage(message);

                // add this new offer to the list of offers the product already has
                listOffers.add(newOffer);

                // send offer information to firebase
                fbDatabase.getReference("posts").child(itemId).child("offers").setValue(listOffers);

                // TODO: SEND MESSAGE TO SELLER FROM BUYER
                sendMessage(message, sellerEmail, sellerId, buyerEmail, fbUser.getUid());


                // finish activity
                // finish();

            }
        });

        fbDatabase.getReference("posts").child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentItem = dataSnapshot.getValue(Post.class);
                if (currentItem != null) {
                    if (currentItem.getOffers() != null) {
                        listOffers = currentItem.getOffers();
                    }

                    itemName.setText(currentItem.getItemName());
                    sellerOffer.setText("$" + String.format("%.2f", currentItem.getPrice()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fbDatabase.getReference("users").child(getIntent().getStringExtra("ITEM_OWNER"))
                .child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    sellerEmail = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // nothing
            }
        });

    }

    public void sendMessage(final String message, final String receiver, final String receiverUid,
                            final String sender, final String senderUid) {
        // send the message
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        fbDatabase.getReference("chat_rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference messageRef;

                Long tsLong = System.currentTimeMillis();
                String timestamp = tsLong.toString();

                if (dataSnapshot.hasChild(room_type_1)) {
                    messageRef = fbDatabase.getReference("chat_rooms").child(room_type_1).child(timestamp);
                }
                else if (dataSnapshot.hasChild(room_type_2)) {
                    messageRef = fbDatabase.getReference("chat_rooms").child(room_type_2).child(timestamp);
                }
                else {
                    messageRef = fbDatabase.getReference("chat_rooms").child(room_type_1).child(timestamp);
                }

                messageRef.child("message").setValue(message);
                messageRef.child("receiver").setValue(receiver);
                messageRef.child("receiverUid").setValue(receiverUid);
                messageRef.child("sender").setValue(sender);
                messageRef.child("senderUid").setValue(senderUid);
                messageRef.child("timestamp").setValue(timestamp);

                Toast.makeText(OfferPage.this, "Offer Sent!", Toast.LENGTH_SHORT).show();

                // go to chat activity to see offer message
                fbAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                    @Override
                    public void onSuccess(GetTokenResult getTokenResult) {
                        String firebaseToken = getTokenResult.getToken();
                        ChatActivity.startActivity(OfferPage.this,
                                receiver,
                                receiverUid,
                                firebaseToken);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}