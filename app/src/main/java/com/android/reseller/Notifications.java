package com.android.reseller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.reseller.models.OffersDat;
import com.android.reseller.models.UserList;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Notifications extends AppCompatActivity {

    public HashMap<String,String> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

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


        final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        final FirebaseUser fbUser = fbAuth.getCurrentUser();

        DatabaseReference notificationsDB = FirebaseDatabase.getInstance().getReference("offer").child("offer");
        final ArrayList<NotificationItem> items = new ArrayList<>();
       final ListView list = (ListView) findViewById(R.id.notiflist);

        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users");
       users = new HashMap<>();

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot usersData: dataSnapshot.getChildren())
                {

                    UserList user = usersData.getValue(UserList.class);
                    users.put(usersData.getKey(),user.name);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        notificationsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    OffersDat offs = snap.getValue(OffersDat.class);
                    if (fbUser.getUid().equals(offs.offerfor)) {
                        items.add(new NotificationItem(offs.itemid, users.get(offs.offerby),offs.offerprice));
                    }
                }

                Collections.reverse(items);

                Log.e("Danish: ", items.size()+"");
                NotificationAdapter adapter = new NotificationAdapter(items);
                list.setAdapter(adapter);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    class NotificationItem
    {
        public String image,user; public long price;
        public NotificationItem(String image,String user, long price)
        {
            this.image = image;
            this.price=price;
            this.user=user;
        }
    }

    class NotificationAdapter extends BaseAdapter
    {

        ArrayList<NotificationItem> items;

        public NotificationAdapter(ArrayList<NotificationItem> items) {
            this.items = items;

        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i*8;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.notification_item,viewGroup,false);
            ImageView imageView = (ImageView) view.findViewById(R.id.postImage);
            TextView name = (TextView) view.findViewById(R.id.offerby);
            TextView price = (TextView) view.findViewById(R.id.offerprice);

            Glide.with(getApplicationContext()).load(items.get(i).image).into(imageView);
            name.setText(items.get(i).user);
            price.setText((int)items.get(i).price+"");

            Log.e("Danish: ",i+"");

            return view;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
