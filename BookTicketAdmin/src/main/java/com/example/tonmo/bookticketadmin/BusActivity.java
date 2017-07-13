package com.example.tonmo.bookticketadmin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.UUID;

public class BusActivity extends AppCompatActivity {

    private Spinner mCompany,mFrom,mTo,mSeat_et,mFare_et;
    private CheckBox mIsAc,mIsSleeper;
    private Button mAddBus_btn,mDate_btn,mTime_btn;


    private String time,date;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    private DatabaseReference mBusRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        mBusRef= FirebaseDatabase.getInstance().getReferenceFromUrl(BookTicketAdmin.FIREBASE_BUS_URL);

        mCompany= (Spinner) findViewById(R.id.company_spinner);
        mFrom= (Spinner) findViewById(R.id.from_spinner);
        mTo= (Spinner) findViewById(R.id.to_spinner);
        mIsAc= (CheckBox) findViewById(R.id.is_ac_checkBox);
        mIsSleeper= (CheckBox) findViewById(R.id.is_sleeper_checkBox);
        mSeat_et= (Spinner) findViewById(R.id.seat_editText);
        mFare_et= (Spinner) findViewById(R.id.fare_editText);

        mTime_btn= (Button) findViewById(R.id.time_button);
        mTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(BusActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                mTime_btn.setText("Hour : "+ hourOfDay + "Miniute : " + minute);
                                time=hourOfDay + ":" + minute;
                            }
                        }, hour, minute, false);
                timePickerDialog.show();

            }
        });

        mDate_btn= (Button) findViewById(R.id.date_button);
        mDate_btn.setOnClickListener(new View.OnClickListener() {
            final Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year=c.get(Calendar.YEAR);
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(BusActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String monthText="";
                                switch (month){
                                    case 1:
                                        monthText="January";
                                        break;
                                    case 2:
                                        monthText="February";
                                        break;
                                    case 3:
                                        monthText="March";
                                        break;
                                    case 4:
                                        monthText="April";
                                        break;
                                    case 5:
                                        monthText="May";
                                        break;
                                    case 6:
                                        monthText="June";
                                        break;
                                    case 7:
                                        monthText="July";
                                        break;
                                    case 8:
                                        monthText="August";
                                        break;
                                    case 9:
                                        monthText="September";
                                        break;
                                    case 10:
                                        monthText="October";
                                        break;
                                    case 11:
                                        monthText="November";
                                        break;
                                    case 12:
                                        monthText="December";
                                        break;
                                }
                                mDate_btn.setText("Month : " + monthText + "Day : " + day);
                                date=month+":"+day;
                            }

                        }, year,month, day);
                datePickerDialog.show();
            }
        });

        mAddBus_btn= (Button) findViewById(R.id.add_button);
        mAddBus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBus();
            }
        });
    }

    private void AddBus(){
        if(checkInput()){
            Bus bus=new Bus();
            bus.setmCompany(mCompany.getSelectedItem().toString());
            bus.setmFrom(mFrom.getSelectedItem().toString());
            bus.setmTo(mTo.getSelectedItem().toString());
            bus.setmSeat(mSeat_et.getSelectedItem().toString());
            bus.setmFare(mFare_et.getSelectedItem().toString());
            bus.setmTime(time);
            bus.setmDate(date);
            bus.setmIsAc(mIsAc.isChecked());
            bus.setmIsSleeper(mIsSleeper.isChecked());
            bus.setmId(createId(5));
            bus.setmBookedSeat("0");
            mBusRef.push().setValue(bus);
            Toast.makeText(this,"Bus Added",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Invalid Time",Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkInput() {

        return !mDate_btn.getText().toString().equals("Select Time")
                && !mTime_btn.getText().toString().equals("Select Time")
                && !mFrom.getSelectedItem().toString().equals(mTo.getSelectedItem().toString());
    }

    private String createId( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
            return sb.toString();
    }






}


