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

public class UserActivity extends AppCompatActivity {

    private ListView userListView;
    ArrayList<HashMap<String, String>> userStringList;
    private ArrayList<String> userKeysList;
    ListAdapter adapter;

    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        userListView= (ListView) findViewById(R.id.user_list);
        userListView.setClickable(true);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDilog(position);
            }
        });

        userStringList=new ArrayList<>();
        userKeysList=new ArrayList<>();
        mUserRef= FirebaseDatabase.getInstance().getReferenceFromUrl(BookTicketAdmin.FIREBASE_USER_URL);
        updateUI();
    }

    private void updateUI() {
        getData();
        bindDataWithAdapter();
    }

    private void bindDataWithAdapter() {

        adapter = new SimpleAdapter(
                UserActivity.this, userStringList,
                R.layout.user_list_item, new String[]{"mEmail","mPassword"},
                new int[]{R.id.user_mail,R.id.user_password});

        userListView.setAdapter(adapter);
    }

    private void getData() {
        mUserRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userStringList.clear();
                for(DataSnapshot userData:dataSnapshot.getChildren()){
                        userKeysList.add(userData.getKey().toString());
                        HashMap<String, String> userHashMap = new HashMap<>();
                        userHashMap.put("mEmail", userData.getKey().toString().replace(",","."));
                        userHashMap.put("mPassword", userData.getValue().toString());
                        userStringList.add(userHashMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void alertDilog(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserActivity.this);
        alertDialog.setTitle("Confirm Action...");
        alertDialog.setMessage("Are you sure you want to delete this user?");
        alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                deleteUser(position);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void deleteUser(int position) {
        final String userIdText=userKeysList.get(position);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot busData:dataSnapshot.getChildren()){
                    if(busData.getKey().toString().equals(userIdText)){
                        mUserRef.child(userIdText).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Toast.makeText(UserActivity.this,"User :"+userIdText+" has been deleted",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UserActivity.this,UserActivity.class));

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

}
