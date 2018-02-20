package com.example.idielectronica2.rescueanimals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendAudioActivity extends AppCompatActivity  implements MediaPlayer.OnCompletionListener {

    Register register;

    ProgressDialog progress;
    RequestQueue request;
    private StringRequest stringRequest;

    MediaRecorder recorder;
    MediaPlayer player;
    File archivo;

    private TextView txtViewStatus;
    private Button btnRec;
    private Button btnPlay;
    private Button btnSave;
    private Button btnSend;
    private Button btnInsertText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_audio);

        this.request = Volley.newRequestQueue(this);

        register = (Register) getIntent().getExtras().getSerializable("register");

        this.txtViewStatus = (TextView)findViewById(R.id.txtViewStatus);
        this.btnRec = (Button)findViewById(R.id.btnRec);
        this.btnPlay = (Button)findViewById(R.id.btnPlay);
        this.btnSave = (Button)findViewById(R.id.btnSave);
        this.btnSend = (Button)findViewById(R.id.btnSend);
        this.btnInsertText = (Button)findViewById(R.id.btnInsertText);

        this.btnPlay.setEnabled(false);
        this.btnSave.setEnabled(false);
        this.btnSend.setEnabled(false);

        this.btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rec();
            }
        });

        this.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.start();
                btnRec.setEnabled(false);
                btnPlay.setEnabled(false);
                btnSave.setEnabled(false);
                txtViewStatus.setText("Reproduciendo");
            }
        });

        this.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        this.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register.setFile(convertFileForString());
                register.setExtension("3gp");
                cargarWebService();
            }
        });

        this.btnInsertText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SendTextActivity.class);
                intent.putExtra("register", register);
                startActivity(intent);
            }
        });
    }

    public void rec() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File path = new File(Environment.getExternalStorageDirectory()
                .getPath());
        try {
            archivo = File.createTempFile("temporal", ".3gp", path);
        } catch (IOException e) {
        }
        recorder.setOutputFile(archivo.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
        }
        recorder.start();
        txtViewStatus.setText("Grabando");
        btnRec.setEnabled(false);
        btnPlay.setEnabled(false);
        btnSave.setEnabled(true);
    }

    public void save() {
        recorder.stop();
        recorder.release();
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        try {
            player.setDataSource(archivo.getAbsolutePath());
        } catch (IOException e) {
        }
        try {
            player.prepare();
        } catch (IOException e) {
        }
        btnRec.setEnabled(true);
        btnSave.setEnabled(false);
        btnPlay.setEnabled(true);

        txtViewStatus.setText("Listo para reproducir");
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        btnRec.setEnabled(true);
        btnPlay.setEnabled(true);
        btnSave.setEnabled(false);
        this.btnSend.setEnabled(true);
        txtViewStatus.setText("Listo");
    }

    public String convertFileForString() {
        byte[] bytesArray = new byte[(int) this.archivo.length()];


        try {
            FileInputStream fis = new FileInputStream(this.archivo);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(bytesArray, Base64.DEFAULT);
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
                if (register.getFile() != null) {
                    parametros.put("folderFile", register.getFile());
                }
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
