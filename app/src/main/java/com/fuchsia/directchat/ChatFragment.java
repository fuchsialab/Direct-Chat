package com.fuchsia.directchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
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



public class ChatFragment extends Fragment {

    FragmentActivity activity;
    private AdView mAdView;

    @SuppressLint("StaticFieldLeak")
    private static View v;
    FragmentTransaction ft=null;
    private CountryCodePicker cpps;


    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private String bannerid;
    private  String appid;
    Button fab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.chat_fragment, container, false);
        activity = getActivity();

        final TextInputEditText number = v.findViewById(R.id.numbr);
        cpps = v.findViewById(R.id.cpp);

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();


            DatabaseReference rootref=FirebaseDatabase.getInstance().getReference().child("AdUnits");
            rootref.addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    bannerid= Objects.requireNonNull(dataSnapshot.child("banner").getValue()).toString();
                    appid= Objects.requireNonNull(dataSnapshot.child("appid").getValue()).toString();
                    MobileAds.initialize(activity,appid);
                    View view= v.findViewById(R.id.adView);
                    mAdView=new AdView(activity);
                    ((RelativeLayout)view).addView(mAdView);
                    mAdView.setAdSize(AdSize.BANNER);
                    mAdView.setAdUnitId(bannerid);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        fab = v.findViewById(R.id.button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = cpps.getSelectedCountryCode();
                final String phn = Objects.requireNonNull(number.getText()).toString().trim();
                final String number = "+"+code+phn;

                if(!phn.isEmpty()){

                    Intent intent = new Intent(getContext(),MainActivity2.class);
                    intent.putExtra ( "number", number );
                    startActivity(intent);

                }else {
                    Toast.makeText(getActivity(),"Enter a WhatsApp Number",Toast.LENGTH_SHORT).show();
                }


            }
        });

        return v;
    }





}
