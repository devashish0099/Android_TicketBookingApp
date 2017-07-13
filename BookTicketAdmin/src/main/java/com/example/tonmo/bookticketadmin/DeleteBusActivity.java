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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteBusActivity extends AppCompatActivity {

    private static final String BUS_HASHLIST_TAG="bus_hash_list_com.example.tonmo.bookticket";
    private static final String BUS_KEYS_TAG="bus_keys_com.example.tonmo.bookticket";

    private ListView busListView;
    private ArrayList<String> busKeysList;
    ArrayList<HashMap<String, String>> busStringList;

    private DatabaseReference mBusRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_bus);

        busListView= (ListView) findViewById(R.id.list);
        busListView.setClickable(true);
        busListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDilog(position);
            }
        });

        busStringList=new ArrayList<>();
        busKeysList=new ArrayList<>();
        mBusRef= FirebaseDatabase.getInstance().getReferenceFromUrl(BookTicketAdmin.FIREBASE_BUS_URL);
        getData();
        bindDataWithAdapter();

    }



    private void bindDataWithAdapter() {
        ListAdapter adapter = new SimpleAdapter(
                DeleteBusActivity.this, busStringList,
                R.layout.list_item, new String[]{"mCompany", "mTime",
                "mDate","mFare"}, new int[]{R.id.company_name,
                R.id.time, R.id.date,R.id.fare});

        busListView.setAdapter(adapter);
    }

        private void getData() {
            Bundle extras=getIntent().getExtras();
            busKeysList=extras.getStringArrayList(BUS_KEYS_TAG);
            busStringList= (ArrayList<HashMap<String, String>>) extras.getSerializable(BUS_HASHLIST_TAG);
        }
    public void alertDilog(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeleteBusActivity.this);
        alertDialog.setTitle("Confirm Action...");
        alertDialog.setMessage("Are you sure you want to delete this bus?");
        alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                deleteBus(position);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void deleteBus(int position) {
        final String busIdText=busKeysList.get(position);
        mBusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot busData:dataSnapshot.getChildren()){
                    if(busData.getKey().toString().equals(busIdText)){
                        mBusRef.child(busIdText).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Toast.makeText(DeleteBusActivity.this,"BUS :"+busIdText+" has been deleted",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DeleteBusActivity.this,MainActivity.class));

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
