package com.example.tonmo.bookticketadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BookingActivity extends AppCompatActivity {

    private ListView bookingListView;
    ArrayList<HashMap<String, String>> bookingStringList;
    private ArrayList<String> bookingKeysList;

    ListAdapter adapter;

    private DatabaseReference mBookingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        bookingListView= (ListView) findViewById(R.id.booking_list);
        bookingListView.setClickable(true);
        bookingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDilog(position);
            }
        });

        bookingStringList=new ArrayList<>();
        bookingKeysList=new ArrayList<>();

        mBookingRef= FirebaseDatabase.getInstance().getReferenceFromUrl(BookTicketAdmin.FIREBASE_BOOKINGS_URL);
        updateUI();
    }

    private void updateUI() {
        getData();
        bindDataWithAdapter();
    }
    private void getData() {
        mBookingRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookingStringList.clear();
                for(DataSnapshot bookingData:dataSnapshot.getChildren()){
                        bookingKeysList.add(bookingData.getKey());
                        HashMap<String, String> bookingHashMap = new HashMap<>();
                        bookingHashMap.put("mCompany",(String) bookingData.child("mCompany").getValue());
                        bookingHashMap.put("mFrom","From: "+(String) bookingData.child("mFrom").getValue());
                        bookingHashMap.put("mTo","To: "+(String) bookingData.child("mTo").getValue());
                        String bookedSeat=(String) bookingData.child("mBookedSeatAmount").getValue();
                        int bookedSeatInt=Integer.valueOf(bookedSeat);
                        bookingHashMap.put("mBookedSeatAmount","Booked Seat : "+bookedSeat);
                        String fare=(String) bookingData.child("mFare").getValue();
                        int fareInt=bookedSeatInt*Integer.valueOf(fare);
                        bookingHashMap.put("mFare","Total Fare: "+String.valueOf(fareInt));
                        bookingHashMap.put("mTime","Time : "+(String) bookingData.child("mTime").getValue());
                        String date[]=getDate((String) bookingData.child("mDate").getValue()); //day,month
                        String dateText=date[0]+" "+date[1];

                        bookingHashMap.put("mDate","Date: "+dateText);
                        bookingStringList.add(bookingHashMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void bindDataWithAdapter() {
        adapter = new SimpleAdapter(
                BookingActivity.this, bookingStringList,
                R.layout.booking_list_item, new String[]{"mCompany","mFrom","mTo"
                ,"mBookedSeatAmount","mFare","mTime",
                "mDate",}, new int[]{R.id.company_name,R.id.from,R.id.to,R.id.booked_seat
                ,R.id.fare,R.id.time, R.id.date});

        bookingListView.setAdapter(adapter);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BookingActivity.this);
        alertDialog.setTitle("Confirm Action...");
        alertDialog.setMessage("Are you sure you want delete this booking?");
        alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                cancelTicket(position);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void cancelTicket(int position) {

        mBookingRef.child(bookingKeysList.get(position)).removeValue();
        startActivity(new Intent(BookingActivity.this,BookingActivity.class));

    }



}
