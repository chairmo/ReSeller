package com.android.reseller;

// NOT IN USE ANYMORE

/*
public class Welcome extends AppCompatActivity implements RecyclerViewAdapter.ItemListener {

    public FirebaseAuth fbAuth;
    public FirebaseUser fbUser;
    private Menu menu;

    RecyclerView recyclerView;
    ArrayList<DataModel> arrayList;

    static final int AUTH_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();
        arrayList.add(new DataModel("Item 1", R.drawable.battle, "#09A9FF"));
        arrayList.add(new DataModel("Item 2", R.drawable.beer, "#3E51B1"));
        arrayList.add(new DataModel("Item 3", R.drawable.ferrari, "#673BB7"));
        arrayList.add(new DataModel("Item 4", R.drawable.jetpack_joyride, "#4BAA50"));
        arrayList.add(new DataModel("Item 5", R.drawable.three_d, "#F94336"));
        arrayList.add(new DataModel("Item 6", R.drawable.terraria, "#0A9B88"));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, arrayList, this);
        recyclerView.setAdapter(adapter);

        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);
        recyclerView.setLayoutManager(layoutManager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTH_REQUEST && resultCode == RESULT_OK) {
            if(data.getBooleanExtra("loginStatus", true)) {
                fbUser = fbAuth.getCurrentUser();
                menu.findItem(R.id.action_profile).setVisible(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // set global menu variable pointing to current menu
        this.menu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        if (fbUser != null) {
            // if user is logged in, hide the "log in" option in menu
            menu.findItem(R.id.action_authentication).setVisible(false);
            menu.findItem(R.id.action_registration).setVisible(false);
        }
        else {
            // if user is not logged in, hide the "log out" and "profile" options in menu
            menu.findItem(R.id.action_logout).setVisible(false);
            menu.findItem(R.id.action_profile).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // function called every time someone opens the menu
        if (fbUser != null) {
            // if user is logged in, hide the "log in" option in menu
            menu.findItem(R.id.action_authentication).setVisible(false);
            menu.findItem(R.id.action_registration).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(true);
            menu.findItem(R.id.action_profile).setVisible(true);
        }
        else {
            // if user is not logged in, hide the "log out" and "profile" options in menu
            menu.findItem(R.id.action_logout).setVisible(false);
            menu.findItem(R.id.action_profile).setVisible(false);
            menu.findItem(R.id.action_authentication).setVisible(true);
            menu.findItem(R.id.action_registration).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_authentication) {
            Intent intent = new Intent(this, Authentication.class);
            startActivityForResult(intent, AUTH_REQUEST);
            return true;
        }
        else if (id == R.id.action_registration) {
            Intent intent = new Intent (this, Registration.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.action_profile) {
            Intent intent = new Intent(this, UserProfile.class);
            intent.putExtra("profileUid", fbUser.getUid());
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_logout) {
            // sign out, give notice to user
            fbAuth.signOut();
            fbUser = null;
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();

            // After logged out, change menu to show log in button and hide log out button
            menu.findItem(R.id.action_logout).setVisible(false);
            menu.findItem(R.id.action_profile).setVisible(false);
            menu.findItem(R.id.action_authentication).setVisible(true);
            menu.findItem(R.id.action_registration).setVisible(true);

            return true;
        }
//        else if (id == R.id.action_googleIn) {
//            Intent intent = new Intent (this, GoogleInActivity.class);
//            startActivity(intent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    //@Override
    public void onItemClick(DataModel item) {
        // launch activity to view specific item
        Toast.makeText(getApplicationContext(), item.text + " is clicked", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fbUser = fbAuth.getCurrentUser();
    }
}
*/