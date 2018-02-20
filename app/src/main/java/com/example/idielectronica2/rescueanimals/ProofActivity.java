package com.example.idielectronica2.rescueanimals;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.idielectronica2.rescueanimals.models.Register;

public class ProofActivity extends AppCompatActivity {

    private Register register;

    private Button btnPhoto;
    private Button btnText;
    private Button btnAudio;
    private Button btnFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proof);

        register = (Register) getIntent().getExtras().getSerializable("register");

        btnPhoto = (Button)findViewById(R.id.btnPhoto);
        btnText = (Button)findViewById(R.id.btnText);
        btnAudio = (Button)findViewById(R.id.btnAudio);
        btnFile = (Button)findViewById(R.id.btnFile);

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SendAudioActivity.class);
                intent.putExtra("register", register);
                startActivity(intent);
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSendPhoto();
            }
        });

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SendFileActivity.class);
                intent.putExtra("register", register);
                startActivity(intent);
            }
        });



        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSendText();
            }
        });
    }

    private void openSendText() {
        Intent intent = new Intent(this, SendTextActivity.class);
        intent.putExtra("register", register);
        startActivity(intent);
    }

    private void openSendFile() {
        Intent intent = new Intent(this, SendFileActivity.class);
        intent.putExtra("register", register);
        startActivity(intent);
    }

    private void openSendPhoto() {
        Intent intent = new Intent(this, SendPhotoActivity.class);
        intent.putExtra("register", register);
        startActivity(intent);
    }

}
