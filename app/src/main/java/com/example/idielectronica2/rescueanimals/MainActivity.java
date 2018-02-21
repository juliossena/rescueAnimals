package com.example.idielectronica2.rescueanimals;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.String;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.idielectronica2.rescueanimals.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class MainActivity extends Activity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private EditText edtTextEmail;
    private EditText edtTextPassword;
    private TextView txtViewResult;
    private Button btnSubmitEnter;
    private TextView btnRegister;
    ProgressDialog progress;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionCamera();
        permissionLocation();
        permissionReadStorage();
        permissionRecordAudio();
        permissionWriteStorage();

        this.edtTextEmail = (EditText) findViewById(R.id.edtTextEmail);
        this.edtTextPassword = (EditText) findViewById(R.id.edtTextPassword);
        this.txtViewResult = (TextView) findViewById(R.id.txtViewResult);
        this.btnSubmitEnter = (Button) findViewById(R.id.btnSubmitEnter);
        this.btnRegister = (TextView) findViewById(R.id.btnRegister);

        this.request = Volley.newRequestQueue(this);

        btnSubmitEnter.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (edtTextEmail.getText().toString().equals("") || edtTextPassword.getText().toString().equals("")) {
                    txtViewResult.setText("Rellene todos los campos");
                } else {
                    cargarWebService();
                }

            }
        });

        btnRegister.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                loadFragment();
            }
        });

    }

    private void permissionCamera() {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {
                    android.Manifest.permission.CAMERA  },1 );
        }
    }

    private void permissionLocation() {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION  },1 );
        }
    }

    private void permissionRecordAudio() {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.RECORD_AUDIO ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {
                    android.Manifest.permission.RECORD_AUDIO  },1 );
        }
    }

    private void permissionWriteStorage() {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE  },1 );
        }
    }

    private void permissionReadStorage() {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE  },1 );
        }
    }



    private void loadFragment() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progress.hide();
        this.txtViewResult.setText("Erro: " + error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        progress.hide();

        Users user = new Users();

        JSONArray json = response.optJSONArray("users");
        JSONObject jsonObject = null;

        try {
            jsonObject = json.getJSONObject(0);
            user.setEmail(jsonObject.optString("email"));
            user.setPassword(jsonObject.optString("password"));
            user.setLastName(jsonObject.optString("lastName"));
            user.setTelephone(jsonObject.optString("telephone"));

            if (user.getEmail() != null) {
                Intent intent = new Intent(this, MainLoginActivity.class);
                intent.putExtra("user", user);

                startActivity(intent);
            }
        } catch (JSONException e) {
            this.txtViewResult.setText("Usuario o contraseña no válidos");
        }
    }

    private void cargarWebService () {
        this.progress = new ProgressDialog(this);
        progress.setMessage("Carregando...");
        progress.show();

        String url = "http://denunciarap.indutel.pe/webServiceBomberos/web/?email=" + this.edtTextEmail.getText().toString()
                + "&password=" + this.edtTextPassword.getText().toString();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, this, this);
        request.add(jsonObjectRequest);
    }

}
