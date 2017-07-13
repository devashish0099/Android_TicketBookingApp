package com.example.tonmo.bookticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchBusActivity extends AppCompatActivity {

    private static final String USER_MAIL_TAG="current_user_com.example.tonmo.bookticket";
    private static final String USER_TICKET_AMOUNT_TAG="ticket_amount_com.example.tonmo.bookticket";
    private static String Current_User_Mail;
    private static final String BUS_LIST_TAG="bus_list_com.example.tonmo.bookticket";
    private static final String BUS_KEYS_TAG="bus_keys_com.example.tonmo.bookticket";

    private Button mLogOut_btn,mSearch_btn;
    private Spinner mCompany_spnr,mFrom_spnr, mTo_spnr, mSeat_spnr;
    private CheckBox mIsAc,mIsSleeper;
    private ArrayList<Bus> allBus=new ArrayList<>();
    private ArrayList<String> allBusKeys=new ArrayList<>();

    private boolean isAcChecked,isSleeperChecked;

    private DatabaseReference mBusRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bus);

        mBusRef= FirebaseDatabase.getInstance().getReferenceFromUrl(BookTicket.FIREBASE_BUS_URL);
        mBusRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allBus.clear();
                allBusKeys.clear();
                for(DataSnapshot busData:dataSnapshot.getChildren()){
                    allBusKeys.add(busData.getKey());
                    Bus bus=new Bus();
                    bus.setmCompany((String) busData.child("mCompany").getValue());
                    bus.setmFrom((String) busData.child("mFrom").getValue());
                    bus.setmTo((String) busData.child("mTo").getValue());
                    bus.setmSeat((String) busData.child("mSeat").getValue());
                    bus.setmBookedSeat((String) busData.child("mBookedSeat").getValue());
                    bus.setmFare((String) busData.child("mFare").getValue());
                    bus.setmTime((String) busData.child("mTime").getValue());
                    bus.setmDate((String) busData.child("mDate").getValue());
                    bus.setmIsAc((Boolean) busData.child("mIsAc").getValue());
                    bus.setmIsSleeper((Boolean) busData.child("mIsSleeper").getValue());
                    allBus.add(bus);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mCompany_spnr= (Spinner) findViewById(R.id.company_spnr);
        mFrom_spnr= (Spinner) findViewById(R.id.from_spnr);
        mTo_spnr= (Spinner) findViewById(R.id.to_spnr);
        mSeat_spnr= (Spinner) findViewById(R.id.seat_spnr);
        mIsAc= (CheckBox) findViewById(R.id.ac_service_cb);
        mIsAc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAcChecked=isChecked;
            }
        });
        mIsSleeper= (CheckBox) findViewById(R.id.sleeper_service_cb);
        mIsSleeper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSleeperChecked=isChecked;
            }
        });


        mSearch_btn= (Button) findViewById(R.id.search_btn);
        mSearch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if("None".equals(mCompany_spnr.getSelectedItem().toString())){
                   findMatch(false);
                }else{
                   findMatch(true);
               }

            }
        });

        mLogOut_btn= (Button) findViewById(R.id.logout_btn);
        mLogOut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SearchBusActivity.this,LoginActivity.class));
            }
        });
        if(savedInstanceState!=null){
            Current_User_Mail=savedInstanceState.getString(USER_MAIL_TAG);
        }else{
            Bundle extras=getIntent().getExtras();
            Current_User_Mail= (String) extras.get(USER_MAIL_TAG);
        }
        Toast.makeText(SearchBusActivity.this,Current_User_Mail,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Current_User_Mail = savedInstanceState.getString(USER_MAIL_TAG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USER_MAIL_TAG, Current_User_Mail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_searchbus_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_bookings:
                startActivity(new Intent(SearchBusActivity.this,BookingsActivity.class)
                        .putExtra(USER_MAIL_TAG,Current_User_Mail));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findMatch(boolean flag) {
        ArrayList<Bus> matchedBus=new ArrayList<>();
        ArrayList<String> matchedBusKeys=new ArrayList<>();
        if(flag){
            for(int i=0;i<allBus.size();i++){
                Bus b=allBus.get(i);
                if(b.getmCompany().equals(mCompany_spnr.getSelectedItem().toString())
                        && b.getmFrom().equals(mFrom_spnr.getSelectedItem().toString())
                        && b.getmTo().equals(mTo_spnr.getSelectedItem().toString())
                        && b.ismIsAc()==isAcChecked
                        &&b.ismIsSleeper()==isSleeperChecked){
                    matchedBus.add(b);
                    matchedBusKeys.add(allBusKeys.get(i));

                }
            }
        }else{
            for(int i=0;i<allBus.size();i++){
                Bus b=allBus.get(i);
                if(b.getmFrom().equals(mFrom_spnr.getSelectedItem().toString())
                        && b.getmTo().equals(mTo_spnr.getSelectedItem().toString())
                        && b.ismIsAc()==isAcChecked
                        &&b.ismIsSleeper()==isSleeperChecked){
                    matchedBus.add(b);
                    matchedBusKeys.add(allBusKeys.get(i));

                }
            }
        }

        if(!matchedBus.isEmpty()){
            Intent intent=new Intent(SearchBusActivity.this,MatchedBusActivity.class);
            intent.putExtra(BUS_LIST_TAG,matchedBus);
            intent.putExtra(USER_MAIL_TAG,Current_User_Mail);
            intent.putExtra(BUS_KEYS_TAG,matchedBusKeys);
            intent.putExtra(USER_TICKET_AMOUNT_TAG,mSeat_spnr.getSelectedItem().toString());
            startActivity(intent);
        }else{
            Toast.makeText(SearchBusActivity.this,"Sorry! No Bus Available",Toast.LENGTH_LONG).show();
        }

    }
}
