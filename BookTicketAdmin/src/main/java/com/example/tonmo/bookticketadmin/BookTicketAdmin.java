package com.example.tonmo.bookticketadmin;


import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class BookTicketAdmin extends Application{

    public static final String FIREBASE_URL="https://bookticket-5da81.firebaseio.com/";
    public static final String FIREBASE_BUS_URL="https://bookticket-5da81.firebaseio.com/bus";
    public static final String FIREBASE_ROUTE_URL="https://bookticket-5da81.firebaseio.com/route";
    public static final String FIREBASE_USER_URL="https://bookticket-5da81.firebaseio.com/users";
    public static final String FIREBASE_BOOKINGS_URL="https://bookticket-5da81.firebaseio.com/booking";
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
