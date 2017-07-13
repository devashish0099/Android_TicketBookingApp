package com.example.tonmo.bookticket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private static final String USER_MAIL_TAG="current_user_com.example.tonmo.bookticket";

    private EditText mEmail_et,mPassword_et,mRepeat_password_et;
    private Button mSignup_btn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReferenceFromUrl(BookTicket.FIREBASE_USER_URL);
        mEmail_et= (EditText) findViewById(R.id.email_et);
        mPassword_et= (EditText) findViewById(R.id.password_et);
        mRepeat_password_et= (EditText) findViewById(R.id.repeat_password_et);
        mSignup_btn= (Button) findViewById(R.id.signup_btn);
        mSignup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUp();
            }
        });

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(SignUpActivity.this, SearchBusActivity.class)
                    .putExtra(USER_MAIL_TAG,mEmail_et.getText().toString().trim()));
                }
            }
        };



    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    private void startLogIn(String e,String p){
        String email=e;
        String password=p;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this,"LogIn Problem!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void startSignUp(){
        final String email=mEmail_et.getText().toString().trim();
        final String password=mPassword_et.getText().toString().trim();
        String repeat_password=mRepeat_password_et.getText().toString().trim();

        if(checkEmail(email)){
            if(checkPassword(password,repeat_password)){
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(SignUpActivity.this,"SignUp Problem!",Toast.LENGTH_SHORT).show();
                                }else{
                                    User user=new User();
                                    user.setmEmil(email);
                                    user.setmPassword(password);
                                    userRef.child(user.getmEmil().replace(".",","))
                                            .setValue(user.getmPassword());
                                    startLogIn(email,password);
                                }
                            }
                        });
            }else{
                Toast.makeText(SignUpActivity.this,"Passoword Not Valid!",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(SignUpActivity.this,"Email Not Valid!",Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkPassword(String password, String repeat_password) {
        return !TextUtils.isEmpty(password)&& !TextUtils.isEmpty(repeat_password)
                && password.length()>=6 && password.equals(repeat_password);
    }

    private boolean checkEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
