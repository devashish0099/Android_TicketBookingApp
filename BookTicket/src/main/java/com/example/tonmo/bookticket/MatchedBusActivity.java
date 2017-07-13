package com.example.tonmo.bookticket;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchedBusActivity extends AppCompatActivity {

    private static final String USER_MAIL_TAG="current_user_com.example.tonmo.bookticket";
    private static String Current_User_Mail;
    private static final String USER_TICKET_AMOUNT_TAG="ticket_amount_com.example.tonmo.bookticket";
    private static final String BUS_LIST_TAG="bus_list_com.example.tonmo.bookticket";
    private static final String BUS_KEYS_TAG="bus_keys_com.example.tonmo.bookticket";

    private ListView busListView;
    private ArrayList<Bus> busList;
    private ArrayList<String> busKeysList;
    ArrayList<HashMap<String, String>> busStringList;
    private int ticketAmount;

    private DatabaseReference mBusRef;
    private DatabaseReference mBookingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_bus);

        busListView= (ListView) findViewById(R.id.list);
        busListView.setClickable(true);
        busListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDilog(position);
            }
        });
        busStringList=new ArrayList<>();
        getData();
        bindDataWithAdapter();

        mBusRef= FirebaseDatabase.getInstance().getReferenceFromUrl(BookTicket.FIREBASE_BUS_URL);
        mBookingRef= FirebaseDatabase.getInstance().getReferenceFromUrl(BookTicket.FIREBASE_BOOKINGS_URL);
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        intent.putExtra(USER_MAIL_TAG,Current_User_Mail);
        return intent;
    }

    private void bindDataWithAdapter() {
        ListAdapter adapter = new SimpleAdapter(
                MatchedBusActivity.this, busStringList,
                R.layout.list_item, new String[]{"mCompany", "mTime",
                "mDate","mFare"}, new int[]{R.id.company_name,
                R.id.time, R.id.date,R.id.fare});

        busListView.setAdapter(adapter);
    }

    private void getData() {
        Bundle extras=getIntent().getExtras();
        busList= (ArrayList<Bus>) extras.getSerializable(BUS_LIST_TAG);
        for(int i=0;i<busList.size();i++){
            HashMap<String, String> busHashMap = new HashMap<>();
            busHashMap.put("mCompany",busList.get(i).getmCompany());
            busHashMap.put("mTime","Journey Time : "+busList.get(i).getmTime());
            String date[]=getDate(busList.get(i).getmDate()); //day,month
            String dateText=date[0]+" "+date[1];

            busHashMap.put("mDate",dateText);
            busHashMap.put("mFare","Fare : "+busList.get(i).getmFare()+"/= per seat");

            busStringList.add(busHashMap);
        }
        busKeysList=extras.getStringArrayList(BUS_KEYS_TAG);
        Current_User_Mail=extras.getString(USER_MAIL_TAG);
        ticketAmount=Integer.valueOf(extras.getString(USER_TICKET_AMOUNT_TAG)) ;
        Toast.makeText(MatchedBusActivity.this,Current_User_Mail,Toast.LENGTH_SHORT).show();
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

    public void alertDilog(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MatchedBusActivity.this);
        alertDialog.setTitle("Confirm Booking...");
        alertDialog.setMessage("Are you sure you want book?");
        alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                bookTicket(position);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void bookTicket(int position) {
        final String busIdText=busKeysList.get(position);
        final String[] bookedSeatText = new String[1];
        mBusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot busData:dataSnapshot.getChildren()){
                    if(busData.getKey().toString().equals(busIdText)){
                        bookedSeatText[0] =busData.child("mBookedSeat").getValue().toString();
                        int bookedSeat=Integer.valueOf(bookedSeatText[0]);
                        bookedSeat=ticketAmount+bookedSeat;
                        String updatedBookedSeatForBus=String.valueOf(bookedSeat);
                        String updatedBookedSeatForUser=String.valueOf(ticketAmount);
                        mBusRef.child(busIdText).child("mBookedSeat").setValue(updatedBookedSeatForBus);
                        Booking booking=new Booking();
                        booking.setmBookedSeatAmount(updatedBookedSeatForUser);
                        booking.setmCompany(busData.child("mCompany").getValue().toString());
                        booking.setmDate(busData.child("mDate").getValue().toString());
                        booking.setmEmil(Current_User_Mail.replace(".",","));
                        booking.setmFare(busData.child("mFare").getValue().toString());
                        booking.setmFrom(busData.child("mFrom").getValue().toString());
                        booking.setmTo(busData.child("mTo").getValue().toString());
                        booking.setmTime(busData.child("mTime").getValue().toString());
                        mBookingRef.push().setValue(booking);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
