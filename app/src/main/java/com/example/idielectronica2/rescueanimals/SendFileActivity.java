package com.example.idielectronica2.rescueanimals;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idielectronica2.rescueanimals.models.Register;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendFileActivity extends AppCompatActivity{

    static final int REQUEST_FILE = 1;
    private Button btnChangePhoto;
    private Button btnSend;
    private Button btnInsertText;
    private TextView txtNameFile;
    private Register register;
    private StringRequest stringRequest;

    ProgressDialog progress;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

        btnSend = (Button)findViewById(R.id.btnSend);
        btnInsertText = (Button)findViewById(R.id.btnInsertText);
        btnChangePhoto = (Button)findViewById(R.id.btnChangePhoto);
        register = (Register) getIntent().getExtras().getSerializable("register");
        txtNameFile = (TextView)findViewById(R.id.txtNameFile);

        this.request = Volley.newRequestQueue(this);

        sendIntent();

        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntent();
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPhoto();
            }
        });

        btnInsertText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SendTextActivity.class);
                intent.putExtra("register", register);
                startActivity(intent);
            }
        });
    }



    protected void sendPhoto() {
        if (this.register.getFile() == null) {
            Toast.makeText(this, "Por favor, tome una foto", Toast.LENGTH_SHORT).show();
        } else {
            cargarWebService();
        }
    }

    protected void sendIntent() {
        Intent takePictureIntente = new Intent(Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntente.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(takePictureIntente, "Selecione uma imagem"), REQUEST_FILE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            File auxFile = new File(uri.getPath());
            this.register.setFile(converterFileString(auxFile));

            String[] split = uri.toString().split("/");
            String nameFile = split[split.length - 1];

            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            String extension = mime.getExtensionFromMimeType(this.getContentResolver().getType(uri));

            this.txtNameFile.setText(uri.toString() + extension);

            this.register.setExtension(extension);
        }
    }

    private String converterFileString (File file) {
        byte[] bytesArray = new byte[(int) file.length()];


        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(bytesArray, Base64.DEFAULT);
    }

    private String converterImgString(Bitmap bitmap) {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, array);
        byte[] imagenByte = array.toByteArray();
        String imagemString = Base64.encodeToString(imagenByte, Base64.DEFAULT);

        return imagemString;
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
