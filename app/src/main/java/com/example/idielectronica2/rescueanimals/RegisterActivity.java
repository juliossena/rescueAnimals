package com.example.idielectronica2.rescueanimals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private EditText editName;
    private EditText editLastName;
    private EditText editEmail;
    private EditText editTelephone;
    private EditText editPass;
    private EditText editPass2;
    private Button btnRegister;
    ProgressDialog progress;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.editEmail = (EditText) findViewById(R.id.editEmail);
        this.editLastName = (EditText) findViewById(R.id.editLastName);
        this.editName = (EditText) findViewById(R.id.editName);
        this.editPass = (EditText) findViewById(R.id.editPassword);
        this.editPass2 = (EditText) findViewById(R.id.editPassword2);
        this.editTelephone = (EditText) findViewById(R.id.editTelephone);
        this.btnRegister = (Button) findViewById(R.id.btnRegister);

        this.request = Volley.newRequestQueue(this);

        btnRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarWebService();
            }
        });

    }

    public void showMessage(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progress.hide();
        this.showMessage("Erro ao inserir");
    }

    @Override
    public void onResponse(JSONObject response) {

        progress.hide();

        this.showMessage("Registrado com sucesso!");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void cargarWebService () {
        if (!this.editPass.getText().toString().equals("")
                && editPass.getText().toString().equals(editPass2.getText().toString())) {
            if (!editEmail.toString().equals("")
                    && !editLastName.toString().equals("")
                    && !editName.toString().equals("")) {
                this.progress = new ProgressDialog(this);
                progress.setMessage("Carregando...");
                progress.show();

                String url = "http://denunciarap.indutel.pe/webServiceBomberos/web/?email=" + this.editEmail.getText().toString()
                        + "&password=" + this.editPass.getText().toString()
                        + "&name=" + this.editName.getText().toString()
                        + "&lastName=" + this.editLastName.getText().toString()
                        + "&telephone=" + this.editTelephone.getText().toString();
                url = url.replace(" ", "%20");


                jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, this, this);
                request.add(jsonObjectRequest);
            } else {
                showMessage("Preencha todos os campos");
            }

        } else {
            showMessage("Senhas diferentes ou em branco");
        }


    }
}
