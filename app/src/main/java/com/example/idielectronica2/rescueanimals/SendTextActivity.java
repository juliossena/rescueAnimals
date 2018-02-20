package com.example.idielectronica2.rescueanimals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idielectronica2.rescueanimals.models.Register;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendTextActivity extends AppCompatActivity {

    private Register register;

    private EditText editMessage;
    private Button btnSend;
    private Button btnInsertPlus;

    private StringRequest stringRequest;
    ProgressDialog progress;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);

        register = (Register) getIntent().getExtras().getSerializable("register");

        editMessage = (EditText)findViewById(R.id.editMessage);
        btnSend = (Button)findViewById(R.id.btnSend);
        btnInsertPlus = (Button)findViewById(R.id.btnInsertPlus);

        if (register.getFile() != null) {
            btnInsertPlus.setEnabled(false);
        }

        if (register.getText() != null) {
            editMessage.setText(register.getText());
        }

        this.request = Volley.newRequestQueue(this);

        btnInsertPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSendProof();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText();
            }
        });
    }

    private void openSendProof() {
        register.setText(editMessage.getText().toString());
        Intent intent = new Intent(this, ProofActivity.class);
        intent.putExtra("register", register);
        startActivity(intent);
    }

    public void sendText() {
        if (editMessage.getText().toString().equals("")) {
            Toast.makeText(this, "Rellene algo en el texto", Toast.LENGTH_SHORT).show();
        } else {
            register.setText(editMessage.getText().toString());
            this.cargarWebService();
        }
    }



    private void cargarWebService () {
        this.progress = new ProgressDialog(this);
        progress.setMessage("Carregando...");
        progress.show();

        String url = "http://denunciarap.indutel.pe/webServiceBomberos/web/";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.hide();

                Toast.makeText(getBaseContext(), "Solicitud enviada con éxito!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), MainLoginActivity.class);
                intent.putExtra("user", register.getUser());

                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
                Toast.makeText(getBaseContext(), "¡Error al enviar pedido!" + register.getFile().length(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parametros = new HashMap<>();
                parametros.put("folderFile", register.getFile());
                if (register.getText() != null) {
                    parametros.put("text", register.getText());
                }

                parametros.put("emailUser", register.getUser().getEmail());
                parametros.put("longitude", "" + register.getLongitude());
                parametros.put("latitude", "" + register.getLatitude());
                parametros.put("typeAnimal", "" + register.getTypeAnimal());
                if (register.getExtension() != null) {
                    parametros.put("nameFile", register.getExtension());
                }

                return parametros;
            }
        };
        request.add(stringRequest);

    }
}
