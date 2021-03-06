package com.example.idielectronica2.rescueanimals;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendPhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FILE = 2;

    private Button btnChangePhoto;
    private Button btnSend;
    private Button btnInsertText;
    private Button btnSearch;
    private ImageView imgPhoto;
    private StringRequest stringRequest;

    private Register register;

    ProgressDialog progress;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_photo);
        this.request = Volley.newRequestQueue(this);

        register = (Register) getIntent().getExtras().getSerializable("register");

        btnChangePhoto = (Button)findViewById(R.id.btnChangePhoto);
        btnInsertText = (Button)findViewById(R.id.btnInsertText);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSend = (Button)findViewById(R.id.btnSend);
        imgPhoto = (ImageView)findViewById(R.id.imgPhoto);

        if (register.getFile() != null) {
            byte[] imageAsBytes = Base64.decode(register.getFile().getBytes(), 0);
            imgPhoto.setImageBitmap(BitmapFactory.decodeByteArray(
                    imageAsBytes, 0, imageAsBytes.length));
        }

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
                openSendText();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntentFile();
            }
        });
    }

    protected void sendIntentFile() {
        Intent takePictureIntente = new Intent(Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntente.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(takePictureIntente, "Selecione uma imagem"), REQUEST_IMAGE_FILE);
        }
    }

    private void openSendText() {
        Intent intent = new Intent(this, SendTextActivity.class);
        intent.putExtra("register", register);
        startActivity(intent);
    }

    protected void sendPhoto() {
        if (this.register.getFile() == null) {
            Toast.makeText(this, "Por favor, tome una foto", Toast.LENGTH_SHORT).show();
        } else {
            cargarWebService();
        }
    }

    protected void sendIntent() {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {
                    android.Manifest.permission.CAMERA  },1 );
        } else {
            Intent takePictureIntente = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntente.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntente, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            this.register.setFile(converterImgString(imageBitmap, 100));

            this.register.setExtension("jpg");

            imgPhoto.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_FILE) {
            Uri imagemSelecionada = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), imagemSelecionada);


                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                String extension = mime.getExtensionFromMimeType(this.getContentResolver().getType(imagemSelecionada));
                if (extension.equals("jpg") || extension.equals("gif") || extension.equals("jpeg") || extension.equals("png")) {
                    this.register.setFile(converterImgString(imageBitmap, 40));
                    this.register.setExtension(extension);
                    imgPhoto.setImageURI(imagemSelecionada);
                } else {
                    Toast.makeText(getBaseContext(), "Colocar una imagen", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String converterImgString(Bitmap bitmap, int quality) {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality, array);
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
