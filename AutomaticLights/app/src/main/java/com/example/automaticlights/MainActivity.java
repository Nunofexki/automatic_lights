package com.example.automaticlights;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.automaticlights.ui.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Switch s;
    ImageView img;
    public String status_write;
    public String status_read;
    public String luminosity_write;
    public String luminosity_read;
    public String users_write;
    public String users_read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s = findViewById(R.id.switch1);
        img = findViewById(R.id.imageView);
        TextView lumi_txt = findViewById(R.id.textView5);
        TextView users_txt = findViewById(R.id.textView8);
        TextView error = findViewById(R.id.textView9);
        img.setImageDrawable(getResources().getDrawable(R.drawable.off));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String status_read = snapshot.child("Status").getValue().toString();
                final String users_read = snapshot.child("nPessoas").getValue().toString();
                final String luminosity_read = snapshot.child("Luminosity").getValue().toString();

                //System.out.println("status read " + status_read);
                //System.out.println("users read " + users_read);
                //System.out.println("luminosity read " + luminosity_read);

                lumi_txt.setText(luminosity_read);
                users_txt.setText(users_read);

                //System.out.println(status_read.equals("1"));

                if (status_read.equals("1")){
                    img.setImageDrawable(getResources().getDrawable(R.drawable.on));
                    s.setChecked(true);
                }
                else {
                    img.setImageDrawable(getResources().getDrawable(R.drawable.off));
                    s.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String status_write = snapshot.child("Status").getValue().toString();
                        final String users_write = snapshot.child("nPessoas").getValue().toString();
                        final String luminosity_write = snapshot.child("Luminosity").getValue().toString();

                        //System.out.println("status write " + status_write);
                        //System.out.println("users write " + users_write);
                        //System.out.println("luminosity write " + luminosity_write);

                        Integer lumi = Integer.valueOf(luminosity_write);
                        Integer users = Integer.valueOf(users_write);

                        System.out.println("lumi " + lumi);
                        System.out.println("user " + users);

                        if (lumi <= 800 && users == 0) {
                            if (s.isChecked()) {
                                img.setImageDrawable(getResources().getDrawable(R.drawable.on));
                                myRef.child("Status").setValue(1);
                                error.setText("");
                            }
                            else {
                                img.setImageDrawable(getResources().getDrawable(R.drawable.off));
                                myRef.child("Status").setValue(0);
                                error.setText("");
                            }
                        }
                        else {
                            if (lumi > 800) {
                                if (s.isChecked()) {
                                    s.setChecked(false);
                                    error.setText("Está de dia pah !!");
                                }
                            }
                            else {
                                if (s.isChecked()) {
                                    s.setChecked(false);
                                    error.setText("Tens gente no quarto pah !!");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        Intent intent = getIntent();
        String user = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);

        Toast.makeText(getApplicationContext(), "Welcome " + user, Toast.LENGTH_LONG).show();

        TextView  text = findViewById(R.id.textView2);
        text.setText(user);
    }
}