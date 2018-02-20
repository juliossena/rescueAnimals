package com.example.idielectronica2.rescueanimals;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idielectronica2.rescueanimals.models.Register;
import com.example.idielectronica2.rescueanimals.models.Users;



public class MainLoginActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    private Users user;
    private Register register;
    private LocationManager locationManager;
    private Button btnLocation;
    private Button btnNext;
    private TextView latitude;
    private TextView longitude;

    private ImageButton btnBird;
    private ImageButton btnCat;
    private ImageButton btnDog;
    private ImageButton btnOther;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        user = (Users) getIntent().getExtras().getSerializable("user");
        this.register = new Register();
        register.setUser(user);

        this.locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        btnNext = (Button)findViewById(R.id.btnNext);
        btnLocation = (Button)findViewById(R.id.btnLocation);
        btnBird = (ImageButton)findViewById(R.id.btnBird);
        btnCat = (ImageButton)findViewById(R.id.btnCat);
        btnDog = (ImageButton)findViewById(R.id.btnDog);
        btnOther = (ImageButton)findViewById(R.id.btnOther);

        btnBird.setAlpha((float) 0.5);
        btnDog.setAlpha((float) 0.5);
        btnCat.setAlpha((float) 0.5);
        btnOther.setAlpha((float) 0.5);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                openMaps();
            }
        });

        btnBird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBird();
            }
        });

        btnCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCat();
            }
        });

        btnDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDog();
            }
        });

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOther();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                sendProof();
            }
        });
    }

    private void sendProof() {
        if (this.register.getTypeAnimal() == 0) {
            Toast.makeText(this, "Por favor, seleccione un animal", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, ProofActivity.class);
            intent.putExtra("register", register);
            startActivity(intent);
        }
    }

    private void openMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("register", register);
        startActivity(intent);
    }

    private void selectBird() {
        register.setTypeAnimal(1);

        btnBird.setAlpha((float) 1);
        btnDog.setAlpha((float) 0.5);
        btnCat.setAlpha((float) 0.5);
        btnOther.setAlpha((float) 0.5);
    }

    private void selectCat() {
        register.setTypeAnimal(2);

        btnBird.setAlpha((float) 0.5);
        btnDog.setAlpha((float) 0.5);
        btnCat.setAlpha((float) 1);
        btnOther.setAlpha((float) 0.5);
    }

    private void selectDog() {
        register.setTypeAnimal(3);

        btnBird.setAlpha((float) 0.5);
        btnDog.setAlpha((float) 1);
        btnCat.setAlpha((float) 0.5);
        btnOther.setAlpha((float) 0.5);
    }

    private void selectOther() {
        register.setTypeAnimal(4);

        btnBird.setAlpha((float) 0.5);
        btnDog.setAlpha((float) 0.5);
        btnCat.setAlpha((float) 0.5);
        btnOther.setAlpha((float) 1);
    }

    private void getLocation () {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = locationManager.getLastKnownLocation((LocationManager.NETWORK_PROVIDER));
            if (location != null) {
                this.register.setLatitude(location.getLatitude());
                this.register.setLongitude(location.getLongitude());
            }
        }
    }

}
