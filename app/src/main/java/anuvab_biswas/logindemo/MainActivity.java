package anuvab_biswas.logindemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText Name,Password;
    private TextView Info;
    private Button Login;
    private int counter=5;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotPassword;
    //TODO:
    //Add data through user-login and store in Firebase Database
    //Display the data inserted
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Name=(EditText)findViewById(R.id.etName);
        Password=(EditText)findViewById(R.id.etPassword);
        Info=(TextView)findViewById(R.id.tvInfo);
        Login=(Button)findViewById(R.id.btn);
        userRegistration=(TextView)findViewById(R.id.tvRegister);
        forgotPassword=findViewById(R.id.tvForgotPassword);

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        progressDialog=new ProgressDialog(this);

        if(user!=null)
        {
            finish();
            startActivity(new Intent(MainActivity.this,SecondActivity.class));
        }
        Info.setText("No. of attempts remaining: 5");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(),Password.getText().toString());
            }
        });
        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegistrationActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PasswordActivity.class));
            }
        });
    }

    private void validate(String userName,String userPassword)
    {
        progressDialog.setMessage("You can check out more videos while being verified");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    //Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(MainActivity.this,SecondActivity.class));
                    checkEmailVerification();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    counter--;
                    Info.setText("No. of attempts remaining: "+counter);
                    if(counter==0) {

                        Login.setEnabled(false);
                    }
                }
            }
        });
    }

    private void checkEmailVerification()
    {
        FirebaseUser firebaseUser=firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag=firebaseUser.isEmailVerified();

        if(emailflag)
        {
            finish();
            startActivity(new Intent(MainActivity.this,SecondActivity.class));

        }
        else
        {
            Toast.makeText(this,"Verify your email",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}
