package com.asquarestudios.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.Account;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asquarestudios.todo.Model.AccountDetail;
import com.asquarestudios.todo.Model.NewItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = "MAIN ACTIVITY";
    private ImageView fab,profile;

    private AlertDialog.Builder builder,builder2;
    private AlertDialog dialog,dialog2;
    private Bundle bundle;

    private EditText date,tittle,toDo;
    private Button save_button;

    private EditText changed_name,changed_username,changed_password;
    private Button savedButton_editProfile;
    private String id;
    private TextView greetings;

    private Button high,medium,low;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    AccountDetail accountDetail = new AccountDetail();

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private List<NewItem> list = new ArrayList<>();

    private boolean highClicked=false,mediumClicked=false,lowClicked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        bundle  = getIntent().getExtras();
        id=bundle.getString("id");
        accountDetail.setUsername(bundle.getString("username"));
        accountDetail.setPassword(bundle.getString("password"));
        accountDetail.setName(bundle.getString("name"));
        greetings = findViewById(R.id.greetings_textView);
        greetings.setText(MessageFormat.format("Hello, {0}", getFirstName(accountDetail.getName())));

        fab = findViewById(R.id.fab_imageView);
        profile = findViewById(R.id.profile_imageView);
        fab.setOnClickListener(this);
        profile.setOnClickListener(this);

        recyclerView = findViewById(R.id.recycleView);


        for(NewItem item : list)
        {
            Log.d(TAG, "onCreate: "+item.getTittle());
        }
    }

    private void displayRecyclerView()
    {
        Log.d(TAG, "onStart displayRecyclerView: ");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this,list,accountDetail.getUsername());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    private void makeToast(String account_created)
    {
        Toast.makeText(MainActivity.this,account_created,Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if(id==R.id.fab_imageView)
        {
            fabButtonClicked();
        }
        else if(id==R.id.profile_imageView)
        {
            profileButtonClicked();
        }
        else if(id==R.id.save_button)
        {
            saveButtonClicked();
        }
        else if(id==R.id.savedButton_editProfile)
        {
            savedButtonEditProfileClicked();
        }
        else if(id==R.id.highPriority_button)
        {
            mediumClicked=false;
            lowClicked=false;
            highClicked=true;
            //btn_default_material
            low.setBackgroundColor(Color.TRANSPARENT);
            medium.setBackgroundColor(Color.TRANSPARENT);
            high.setBackgroundColor(getResources().getColor(R.color.green));

        }
        else if(id==R.id.MediumPriority_button)
        {
            highClicked=false;
            lowClicked=false;
            mediumClicked=true;
            low.setBackgroundColor(Color.TRANSPARENT);
            high.setBackgroundColor(Color.TRANSPARENT);
            medium.setBackgroundColor(getResources().getColor(R.color.green));
        }
        else if(id==R.id.LowPriority_button)
        {
            highClicked=false;
            mediumClicked=false;
            lowClicked=true;
            high.setBackgroundColor(Color.TRANSPARENT);
            medium.setBackgroundColor(Color.TRANSPARENT);
            low.setBackgroundColor(getResources().getColor(R.color.green));
        }

    }

    private void savedButtonEditProfileClicked()
    {

        accountDetail.setUsername(changed_username.getText().toString());
        accountDetail.setName(changed_name.getText().toString());
        accountDetail.setPassword(changed_password.getText().toString());

        Map<String, Object> map = new HashMap<>();

        map.put("username",accountDetail.getUsername());
        map.put("name",accountDetail.getName());
        map.put("password",accountDetail.getPassword());

        savedButton_editProfile.setBackgroundColor(getResources().getColor(R.color.dullGreen));
        db.collection("LOGIN DETAILS").document(id).update(map);
        makeToast("Details updated");
        savedButton_editProfile.setBackgroundColor(getResources().getColor(R.color.green));
        dialog2.dismiss();
        greetings.setText(MessageFormat.format("Hello, {0}", getFirstName(accountDetail.getName())));

    }

    private void profileButtonClicked()
    {
        builder2=new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.edit_profile,null);
        changed_name = view.findViewById(R.id.savedName_editText);
        changed_username = view.findViewById(R.id.savedUserName_editText);
        changed_password = view.findViewById(R.id.savedPassword_editText);
        savedButton_editProfile = view.findViewById(R.id.savedButton_editProfile);



        builder2.setView(view);
        dialog2=builder2.create(); //creating dialog object
        dialog2.show(); ///important step
        savedButton_editProfile.setOnClickListener(this);
        changed_name.setText(accountDetail.getName());
        changed_username.setText(accountDetail.getUsername());
        changed_password.setText(accountDetail.getPassword());

    }

    private void saveButtonClicked()
    {
        if(tittle.getText().toString().isEmpty())
        {
            makeToast("Enter tittle please");
            return;
        }
        if(!highClicked && !lowClicked && !mediumClicked)
        {
            makeToast("Select priority");
            return;
        }
        save_button.setBackgroundColor(getResources().getColor(R.color.dullGreen));
        NewItem newItem = new NewItem();
        newItem.setDate(date.getText().toString());
        newItem.setTittle(tittle.getText().toString());
        newItem.setTask(toDo.getText().toString());
        if(highClicked)
        {
            newItem.setPriority("high");
        }
        else if(mediumClicked)
        {
            newItem.setPriority("medium");
        }
        else if(lowClicked)
        {
            newItem.setPriority("low");
        }
        db.collection(accountDetail.getUsername().toLowerCase()).document(newItem.getTittle().toLowerCase()).set(newItem);
        makeToast("Added");
        save_button.setBackgroundColor(getResources().getColor(R.color.green));

        dialog.dismiss();

        list.add(newItem);
        Log.d(TAG, "onStart saveButtonClicked: "+list.size());
        recyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();

        db.collection(accountDetail.getUsername().toLowerCase()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                        {
                            NewItem newItem = snapshot.toObject(NewItem.class);
                            list.add(newItem);
                        }
                        Log.d(TAG, "onStart:  got it "+list.size());
                        displayRecyclerView();

                    }
                });
        Log.d(TAG, "onStart: 198");
        Log.d(TAG, "onStart: "+list.size());
    }

    private void fabButtonClicked()
    {
        builder=new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.add_item,null);
        date = view.findViewById(R.id.date_editText);
        tittle = view.findViewById(R.id.tittle_editText);
        toDo = view.findViewById(R.id.task_editText);
        high = view.findViewById(R.id.highPriority_button);
        medium = view.findViewById(R.id.MediumPriority_button);
        low = view.findViewById(R.id.LowPriority_button);
        builder.setView(view);
        dialog=builder.create(); //creating dialog object
        dialog.show(); ///important step
        save_button = view.findViewById(R.id.save_button);
        save_button.setOnClickListener(this);
        high.setOnClickListener(this);
        medium.setOnClickListener(this);
        low.setOnClickListener(this);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private String getFirstName(String name)
    {
        String firstName="";
        for(int x=0;x<name.length();x++)
        {
            if(name.charAt(x)!=' ')
            {
                firstName+=name.charAt(x);
            }
            else
                break;
        }
        return firstName;
    }
}