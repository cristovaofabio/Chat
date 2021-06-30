package com.example.whatsapp.Activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView register;
    private FirebaseAuth mAuth;
    private String emailLogIn,passwordLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initializeVariables();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    public void initializeVariables(){
        email = findViewById(R.id.editLogEmail);
        password = findViewById(R.id.editLogPassword);
        register = findViewById(R.id.textViewRegister);
        mAuth = ConfigurationFirebase.getAuth();
    }

    @Override
    public void onStart() {
        super.onStart();
        //mAuth.signOut();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Start welcome page
            startWelcomePage();
        }
    }

    public void singin(View view){
        if(checkFields()){
            mAuth.signInWithEmailAndPassword(emailLogIn, passwordLogIn)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, start welcome page
                                startWelcomePage();

                            } else {
                                String error;
                                try {
                                    throw task.getException();
                                }catch (FirebaseAuthInvalidUserException e){
                                    error = "The user account corresponding to email does not exist or has been disabled";
                                }catch (FirebaseAuthInvalidCredentialsException e){
                                    error = "The password is wrong";
                                }catch (Exception e){
                                    error="Erro to login: "+e.getMessage();
                                }
                                Toast.makeText(LogInActivity.this,""+error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public boolean checkFields(){
        emailLogIn = email.getText().toString();
        passwordLogIn = password.getText().toString();
        if (!emailLogIn.isEmpty()){
            if (!passwordLogIn.isEmpty()){
                return true;
            }else{
                Toast.makeText(LogInActivity.this,"Password can't be empty",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(LogInActivity.this,"Email can't be empty",Toast.LENGTH_SHORT).show();
            }
        return false;
    }
    public void startWelcomePage(){
        Intent intent = new Intent(LogInActivity.this,WelcomeActivity.class);
        startActivity(intent);
    }
}
