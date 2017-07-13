package com.example.tonmo.bookticketadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String BUS_HASHLIST_TAG="bus_hash_list_com.example.tonmo.bookticket";
    private static final String BUS_KEYS_TAG="bus_keys_com.example.tonmo.bookticket";


    private Button mAddBus,mUsers,mDeleteBus,mBooking,mLogout;

    private ArrayList<String> busKeysList;
    ArrayList<HashMap<String, String>> busStringList;
    private DatabaseReference mBusRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddBus= (Button) findViewById(R.id.add_bus_btn);
        mAddBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BusActivity.class));
            }
        });
        mUsers= (Button) findViewById(R.id.users_btn);
        mUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserActivity.class));
            }
        });

        mDeleteBus= (Button) findViewById(R.id.delete_bus_btn);
        mDeleteBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DeleteBusActivity.class);
                intent.putExtra(BUS_HASHLIST_TAG,busStringList);
                intent.putExtra(BUS_KEYS_TAG,busKeysList);
                startActivity(intent);
            }
        });

        mLogout= (Button) findViewById(R.id.logout_btn);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });

        busKeysList=new ArrayList<>();
        busStringList=new ArrayList<>();
        mBusRef= FirebaseDatabase.getInstance().getReferenceFromUrl(BookTicketAdmin.FIREBASE_BUS_URL);
        mBusRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                busKeysList.clear();
                busStringList.clear();
                for (DataSnapshot busData : dataSnapshot.getChildren()) {
                    busKeysList.add(busData.getKey());
                    HashMap<String, String> busHashMap = new HashMap<>();
                    busHashMap.put("mCompany", (String) busData.child("mCompany").getValue());
                    busHashMap.put("mFare", "Fare : " + (String) busData.child("mFare").getValue() + "/= per seat");
                    busHashMap.put("mTime", "Journey Time : " + (String) busData.child("mTime").getValue());
                    String date[] = getDate((String) busData.child("mDate").getValue());
                    String dateText = date[0] + " " + date[1];
                    busHashMap.put("mDate", dateText);
                    busStringList.add(busHashMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBooking= (Button) findViewById(R.id.booking_btn);
        mBooking.setClickable(true);
        mBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,BookingActivity.class));
            }
        });

    }

    private String[] getDate(String s) {

        String date[] = s.split("\\:");
        String month = date[0];
        String day = date[1];
        switch (month) {
            case "1":
                month = "January";
                break;
            case "2":
                month = "February";
            case "3":
                month = "March";
            case "4":
                month = "April";
                break;
            case "5":
                month = "May";
            case "6":
                month = "June";
            case "7":
                month = "July";
                break;
            case "8":
                month = "August";
            case "9":
                month = "September";
            case "10":
                month = "October";
                break;
            case "11":
                month = "November";
            case "12":
                month = "December";
        }
        return new String[] {day,month};
    }
}
