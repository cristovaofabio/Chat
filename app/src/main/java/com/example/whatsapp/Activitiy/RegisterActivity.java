package com.example.whatsapp.Activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.Class.User;
import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Helper.Base64Custon;
import com.example.whatsapp.Helper.UserFirebase;
import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, email, password;
    private FirebaseAuth mAuth;
    private String nameUser, emailUser, passwordUser;
    private User user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeVariables();

    }
    public void initializeVariables(){
        name = findViewById(R.id.editNameRegister);
        email = findViewById(R.id.editEmailRegister);
        password = findViewById(R.id.editPasswordRegister);
    }
    public void registerUser(View view){

        if(checkFields()){
            barraProgresso();
            mAuth = ConfigurationFirebase.getAuth();
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String idUser = Base64Custon.encodeBase64(user.getEmail());
                                user.setId(idUser);
                                user.save();
                                progressDialog.dismiss();//Finish progress bar
                                Toast.makeText(RegisterActivity.this,
                                        "Sucess to create user",
                                        Toast.LENGTH_SHORT).show();

                                UserFirebase.updateUserName(user.getName());
                                finish();
                            } else {
                                String error = "";
                                try {
                                    throw task.getException();
                                }catch (FirebaseAuthWeakPasswordException e){
                                    error = "The password is not strong enough";
                                }catch (FirebaseAuthInvalidCredentialsException e){
                                    error = "The email address is malformed";
                                }catch (FirebaseAuthUserCollisionException e){
                                    error = "Already exists an account with the given email address";
                                }catch (Exception e){
                                    error = "Error to create the user: "+e.getMessage();
                                    e.printStackTrace();
                                }
                                Toast.makeText(RegisterActivity.this,
                                        ""+error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            progressDialog.dismiss();//Finish progress bar
        }
    }

    public boolean checkFields(){
        nameUser = name.getText().toString();
        emailUser = email.getText().toString();
        passwordUser = password.getText().toString();

        if(!nameUser.isEmpty()){
            if (!emailUser.isEmpty()){
                if (!passwordUser.isEmpty()){
                    user = new User();
                    user.setName(nameUser);
                    user.setEmail(emailUser);
                    user.setPassword(passwordUser);

                    return true;
                }else{
                    Toast.makeText(RegisterActivity.this,"Password can't be empty",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(RegisterActivity.this,"Email can't be empty",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(RegisterActivity.this,"Name can't be empty",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void barraProgresso() {
        //Show progress dialog during list image loading
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Aguarde!");
        progressDialog.setMessage("Salvando usuário...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
