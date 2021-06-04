package com.fuchsia.directchat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class AllCallLogFragment extends Fragment {

    View view;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private String appid;
    private String bannerid;
    private AdView mAdView;
    DatabaseHelper myDB;
    ListView listView;
    Button button;
    TextView textView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.call_log_fragment, container, false);

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        bannerAds();

        button = view.findViewById(R.id.deletelist);
        textView= view.findViewById(R.id.textvv);

        listView = view.findViewById(R.id.listView);
        myDB = new DatabaseHelper(getActivity());

        ArrayList<String> theList = new ArrayList<>();
        Cursor data = myDB.getListContents();



        if(data.getCount() == 0){

            textView.setText("Chat History Not Available!");

        }else{
            while(data.moveToNext()){
                theList.add(data.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,theList);
                listView.setAdapter(listAdapter);

            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getContext(),MainActivity2.class);
                intent.putExtra("number",listView.getItemAtPosition(i).toString());
                startActivity(intent);

            }
        });

        deleteAllData();

        return view;

    }

    public void deleteAllData(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AlertDialog.Builder(getActivity())
                        .setTitle("Clear canvas")
                        .setMessage("Are you sure to clear all Chat History?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Integer var = myDB.deleteAllData();

                                if(var > 0){
                                    Intent intent = new Intent(getContext(),MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getActivity(), "All Contact has been deleted", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getActivity(), "There are No Contact to Delete!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });
    }



    private void bannerAds() {
        DatabaseReference rootref=FirebaseDatabase.getInstance().getReference().child("AdUnits");
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bannerid= Objects.requireNonNull(dataSnapshot.child("banner").getValue()).toString();
                appid= Objects.requireNonNull(dataSnapshot.child("appid").getValue()).toString();
                MobileAds.initialize(getActivity(),appid);
                View view= getActivity().findViewById(R.id.adView);
                mAdView=new AdView(getActivity());
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
    }


}
