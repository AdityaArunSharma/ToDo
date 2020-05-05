package com.asquarestudios.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asquarestudios.todo.Model.AccountDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Login_Screen extends AppCompatActivity implements View.OnClickListener
{
    private EditText username_editText,password_editText;
    private TextView newAccount;
    private Button login_button;
    public static final String TAG = "LOGIN SCREEN";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText newUserNamme_editText,newPassword_editText,confirmedPassword_editText,new_name_editText;
    private Button createNewAccount_button;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    boolean usernameAvailable=true;

    private List<AccountDetail> accountsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__screen);
        connectView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        accountsList = new ArrayList<>();
        db.collection("LOGIN DETAILS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    AccountDetail accountDetail = snapshot.toObject(AccountDetail.class);
                    accountsList.add(accountDetail);
                }
                Log.d(TAG, "onStart got it: ");


            }
        });
        Log.d(TAG, "onStart: "+accountsList.size());

    }

    private void connectView()
    {
        username_editText = findViewById(R.id.username_editText);
        password_editText = findViewById(R.id.password_editText);
        newAccount = findViewById(R.id.newAccount_textView);
        login_button = findViewById(R.id.login_button);
        newAccount.setOnClickListener(this);
        login_button.setOnClickListener(this);

    }


    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if(id==R.id.newAccount_textView)
        {
            newAccountOptionClicked();
        }
        else if(id==R.id.login_button)
        {
            loginButtonClicked();
        }
        else if(id==R.id.createNewAccount_button)
        {
            createNewAccountButtonClicked();
        }

    }

    private void loginButtonClicked()
    {
        final String username = username_editText.getText().toString();
        final String password = password_editText.getText().toString();
        login_button.setBackgroundColor(getResources().getColor(R.color.dullRed));

        if(username.isEmpty() || password.isEmpty())
        {
            makeToast("Empty fields not allowed");
            login_button.setBackgroundColor(getResources().getColor(R.color.red));
            return;
        }
        db.collection("LOGIN DETAILS").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        boolean flag=false;
                        for(QueryDocumentSnapshot snapshots : queryDocumentSnapshots)
                        {
                            AccountDetail accountDetail = snapshots.toObject(AccountDetail.class);
                            if(username.equals(accountDetail.getUsername()) && password.equals(accountDetail.getPassword()))
                            {
                                makeToast("Account found on Firestore");
                                login_button.setBackgroundColor(getResources().getColor(R.color.red));
                                accountFound(username,password,accountDetail.getName(),snapshots.getId());

                                flag=true;
                                break;
                            }
                        }

                        if(!flag)
                        {
                            login_button.setBackgroundColor(getResources().getColor(R.color.red));
                            makeToast("Account does not exist");

                        }
                        
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) 
                    {

                    }
                });


    }

    private void accountFound(String correctUsername,String correctPassword,String name,String id)
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("username",correctUsername);
        intent.putExtra("password",correctPassword);
        intent.putExtra("name",name);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    private void createNewAccountButtonClicked()
    {
        createNewAccount_button.setBackgroundColor(getResources().getColor(R.color.dullBlue));
        final String newUserName = newUserNamme_editText.getText().toString().trim();
        String newPassword = newPassword_editText.getText().toString();
        String name = new_name_editText.getText().toString();
        String confirmedPassword = confirmedPassword_editText.getText().toString();
        if(newUserName.equals("") || newPassword.equals("") || confirmedPassword.equals(""))
        {

            makeToast("Empty fields not allowed");
            createNewAccount_button.setBackgroundColor(getResources().getColor(R.color.blue));
            return;
        }

        if(!newPassword.equals(confirmedPassword))
        {
            makeToast("Passwords does not match try again");
            createNewAccount_button.setBackgroundColor(getResources().getColor(R.color.blue));
            return;
        }

        Log.d(TAG, "onStart createNewAccountButtonClicked: "+accountsList.size());
        for(AccountDetail accountDetail : accountsList)
        {
            if(accountDetail.getUsername().equals(newUserName))
            {
                makeToast("Username not available");
                createNewAccount_button.setBackgroundColor(getResources().getColor(R.color.blue));
                return;
            }
        }

        CollectionReference reference_accountdetail = db.collection("LOGIN DETAILS");
            Log.d(TAG, "createNewAccountButtonClicked: 189");
            AccountDetail accountDetail = new AccountDetail();
            accountDetail.setUsername(newUserName);
            accountDetail.setPassword(newPassword);
            accountDetail.setName(name.trim());
            reference_accountdetail.add(accountDetail)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            makeToast("Account created");
                            dialog.dismiss();

                        }
                    });

    }

    private void makeToast(String account_created)
    {
        Toast.makeText(Login_Screen.this,account_created,Toast.LENGTH_SHORT)
                .show();
    }

    private void newAccountOptionClicked()
    {

        Log.d(TAG, "onStart: "+accountsList.size());
        builder=new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.new_account,null);

        newUserNamme_editText = view.findViewById(R.id.new_username_editText);
        newPassword_editText  = view.findViewById(R.id.new_password_editText);
        createNewAccount_button = view.findViewById(R.id.createNewAccount_button);
        confirmedPassword_editText = view.findViewById(R.id.new_confirm_password_editText);
        new_name_editText = view.findViewById(R.id.new_name_editText);

        builder.setView(view);
        dialog=builder.create(); //creating dialog object
        dialog.show(); ///important step


        createNewAccount_button.setOnClickListener(this);
    }
}
