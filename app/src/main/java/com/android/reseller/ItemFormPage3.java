package com.android.reseller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ItemFormPage3 extends AppCompatActivity {

    private Bundle b;
    private EditText itemPrice;
    private EditText itemCond;
    private Spinner spinner;

    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_form_page3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getExtras() == null){
            b = new Bundle();
        }
        else{
            b = getIntent().getExtras();
        }
        itemPrice = findViewById(R.id.itemPrice);
        itemCond = findViewById(R.id.itemCond);
        spinner = findViewById(R.id.spinner);

        //fbAuth = FirebaseAuth.getInstance();
        //FirebaseUser user = fbAuth.getCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (b.getString("PRICE") != null) {
            itemPrice.setText(b.getString("PRICE"));
        }
        if (b.getString("ITEM_COND") != null) {
            itemCond.setText(b.getString("ITEM_COND"));
        }
    }


    public void toPageOne(View view) {
        String price = itemPrice.getText().toString();
        if(price != null && price.length() > 0) {
            b.putFloat("ITEM_PRICE", Float.valueOf(price));
        }
        b.putString("ITEM_COND", itemCond.getText().toString());

        Intent intent = new Intent(this, ItemFormPage1.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void toPageTwo(View view) {
        String price = itemPrice.getText().toString();
        if(price != null && price.length() > 0) {
            b.putFloat("ITEM_PRICE", Float.valueOf(price));
        }
        b.putString("ITEM_COND", itemCond.getText().toString());
        Intent intent = new Intent(this, ItemFormPage2.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void toPageFour(View view) {
        if(itemPrice.getText().length() == 0) {
            Toast.makeText(this, "Please add a price.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(itemCond.getText().length() == 0) {
            Toast.makeText(this, "Please add item condition.", Toast.LENGTH_SHORT).show();
            return;
        }

        String price = itemPrice.getText().toString();
        if(price != null && price.length() > 0) {
            b.putFloat("ITEM_PRICE", Float.valueOf(price));
        }
        b.putString("ITEM_COND", itemCond.getText().toString());
        String tempCat = spinner.getSelectedItem().toString();
        b.putString("CATEGORY", tempCat);
        Intent intent = new Intent(this, ItemFormPage4.class);
        intent.putExtras(b);
        startActivity(intent);
    }
}
