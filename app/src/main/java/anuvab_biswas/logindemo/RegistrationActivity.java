package anuvab_biswas.logindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.LENGTH_SHORT;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userName,userPassword,userEmail,userAge;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilePic;

    String email,name,age,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth=FirebaseAuth.getInstance();
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    //upload to DB
                    String user_email=userEmail.getText().toString().trim();
                    String user_password=userPassword.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                //Toast.makeText(RegistrationActivity.this, "Registration Successful", LENGTH_SHORT).show();
                                //startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                //sendEmailVerification();//NOTE to prevent email verification for now
                                sendUserData();
                                Toast.makeText(RegistrationActivity.this,"Successfully Registered,Upload Complete",Toast.LENGTH_SHORT);
                                //firebaseAuth.signOut();
                                finish();
                                startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                            }
                            else
                                Toast.makeText(RegistrationActivity.this,"Registration Unsuccessful",LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
            }
        });
    }

    private void setupUIViews()
    {
        userName=(EditText)findViewById(R.id.etUserName);
        userPassword=(EditText)findViewById(R.id.etUserPassword);
        userEmail=(EditText)findViewById(R.id.etUserEmail);
        regButton=(Button)findViewById(R.id.btnRegister);
        userLogin=(TextView)findViewById(R.id.tvUserLogin);
        userAge=findViewById(R.id.etAge);
        userProfilePic=findViewById(R.id.ivProfile);
    }
    private boolean validate(){
        boolean result=false;
        name=userName.getText().toString();
        password=userPassword.getText().toString();
        email=userEmail.getText().toString();
        age=userAge.getText().toString();
        if(name.isEmpty()||password.isEmpty()||email.isEmpty()||age.isEmpty())
            Toast.makeText(this,"Please enter all details",LENGTH_SHORT).show();
        else
            result=true;
        return result;
    }

    private void sendEmailVerification()
    {
        final FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();//getInstance is used when old user.Not needed for new user
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        Toast.makeText(RegistrationActivity.this,"Successfully Registered,Verification Email Sent",Toast.LENGTH_SHORT);
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(RegistrationActivity.this,"Mail hasn't been sent",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myRef=firebaseDatabase.getReference(firebaseAuth.getUid());
        UserProfile userProfile=new UserProfile(age,email,name);
        myRef.setValue(userProfile);
    }


}
