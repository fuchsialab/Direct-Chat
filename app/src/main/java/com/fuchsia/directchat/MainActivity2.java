package com.fuchsia.directchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuchsia.directchat.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {

    TextView  messege;
    EditText number;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private String bannerid;
    private String interstitialId;
    private String appid;
    Bundle bundle;
    DatabaseHelper myDB;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        number = findViewById(R.id.numbr);
        messege =findViewById(R.id.mess);
        Button button = findViewById(R.id.button);

        myDB = new DatabaseHelper(this);


        bundle= getIntent().getExtras();
        if(bundle !=null){

            Intent in = getIntent();
            String tv1= in.getExtras().getString("number");
            number.setText(tv1);


        }

        bannerAds();
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mInterstitialAd=new InterstitialAd(MainActivity2.this);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String msg = messege.getText().toString().trim();

                final String phn = number.getText().toString().trim();

                if(!phn.isEmpty()){

                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else {
                        String url= "https://api.whatsapp.com/send?phone="+ phn +"&text"+msg;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        Log.d("TAG", "The interstitial wasn't loaded yet.");

                        AddData(phn);
                        number.setText("");
                    }

                }
                else {
                    Toast.makeText(MainActivity2.this,"Enter a Valid Number.......",Toast.LENGTH_SHORT).show();
                }


                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        if(!phn.isEmpty()){

                            String url= "https://api.whatsapp.com/send?phone="+ phn +"&text"+msg;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);

                            AddData(phn);
                            number.setText("");

                        }else {
                            Toast.makeText(MainActivity2.this,"Enter a Valid Number.......",Toast.LENGTH_SHORT).show();
                        }
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }

                });

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


    public void AddData(String phn) {

        boolean insertData = myDB.addData(phn);

    }


    private void bannerAds() {
        DatabaseReference rootref=FirebaseDatabase.getInstance().getReference().child("AdUnits");
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bannerid=String.valueOf(Objects.requireNonNull(dataSnapshot.child("banner").getValue()).toString());
                interstitialId=String.valueOf(Objects.requireNonNull(dataSnapshot.child("Interstitial").getValue()).toString());
                appid=String.valueOf(Objects.requireNonNull(dataSnapshot.child("appid").getValue()).toString());
                MobileAds.initialize(MainActivity2.this,appid);
                View view= findViewById(R.id.adView);
                mAdView=new AdView(MainActivity2.this);
                ((RelativeLayout)view).addView(mAdView);
                mAdView.setAdSize(AdSize.BANNER);
                mAdView.setAdUnitId(bannerid);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

                mInterstitialAd.setAdUnitId(interstitialId);
                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
