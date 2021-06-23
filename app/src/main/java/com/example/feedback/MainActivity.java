package com.example.feedback;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.feedback.Interface.FeedbackServiceApi;
import com.example.feedback.Model.Area;
import com.example.feedback.Model.ColorCode;
import com.example.feedback.Model.Defect;
import com.example.feedback.Model.FeedbackData;
import com.example.feedback.Model.Location;
import com.example.feedback.Model.Model;
import com.example.feedback.Model.Part;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    //private final String baseUrl="http://10.114.43.200/IQISService/api/";
    private final String baseUrl="http://10.114.3.35:92/api/";
    Retrofit retrofit;
    FeedbackServiceApi feedbackServiceApi;
    ImageView imageview;
    EditText txtBodyNo;
    Spinner spinnerModel;
    Spinner spinnerDefect;
    Spinner spinnerArea;
    Spinner spinnerZone;
    Spinner spinnerLocation;
    Spinner spinnerColor;
    Spinner spinnerLine;
    CardView buttonCancel;
    CardView buttonLogout;
    View buttonSave;
    ProgressButton progressButton;
    Uri mUri;
    String mCurrentPhotoPath;
    int spnModel,spnDefect, spnZone, spnLocation, spnColor, spnArea, spnLine;
    String userId, station;

    private List<Model> modelList;
    private List<Area> areaList;
    private List<ColorCode> colorList;
    private List<Part> partList;
    private List<Defect> defectList;
    private List<Location> locationList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        station = intent.getStringExtra("station");

        spinnerModel = findViewById(R.id.spinnerModel);
        spinnerDefect = findViewById(R.id.spinnerDefect);
        spinnerZone = findViewById(R.id.spinnerZone);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        spinnerColor = findViewById(R.id.spinnerColor);
        spinnerArea = findViewById(R.id.spinnerArea);
        spinnerLine = findViewById(R.id.spinnerLine);
        imageview = findViewById(R.id.imgView);
        txtBodyNo = findViewById(R.id.bodyno);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonSave = findViewById(R.id.buttonSave);

        modelList = new ArrayList<>();
        areaList = new ArrayList<>();
        colorList = new ArrayList<>();
        defectList = new ArrayList<>();
        locationList = new ArrayList<>();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //validate camera permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            imageview.setEnabled(false);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        if(savedInstanceState!= null){
            spnModel = savedInstanceState.getInt("spinnerModel");
            spnDefect = savedInstanceState.getInt("spinnerDefect");
            spnZone = savedInstanceState.getInt("spinnerZone");
            spnLocation = savedInstanceState.getInt("spinnerLocation");
            spnColor = savedInstanceState.getInt("spinnerColor");
            spnArea = savedInstanceState.getInt("spinnerArea");
            spnLine =savedInstanceState.getInt("spinnerLine");
            txtBodyNo.setText(savedInstanceState.getString("txtBodyNo"));
            mCurrentPhotoPath = savedInstanceState.getString("currentPhotoPath");
            if(mCurrentPhotoPath != null){
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions =new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,bitmapOptions);
                imageview.setImageBitmap(bitmap);
                rotateImage();
            }

            spinnerLine.setSelection(spnLine);
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        feedbackServiceApi = retrofit.create(FeedbackServiceApi.class);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spnModel = 0;
                spnDefect = 0;
                spnZone = 0;
                spnColor = 0;
                spnArea = 0;
                spnLine = 0;
                mCurrentPhotoPath = "";
                imageview.setImageResource(R.drawable.ic_action_camera);


                txtBodyNo.setText("");
                spinnerLine.setSelection(0);

            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        progressButton = new ProgressButton(MainActivity.this, buttonSave);
        progressButton.buttonReset("Save");
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressButton.buttonActivated();
                save();
            }
        });

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map.Entry obj = (Map.Entry) spinnerArea.getSelectedItem();

                LinkedHashMap<Integer,String> tmpList  = new LinkedHashMap<Integer,String>();
                tmpList.put(0,"Zone");
                if((int)obj.getKey()!=0){
                    for (Part p : partList) {
                       // if(p.getIdArea() == (int)obj.getKey())
                            tmpList.put(p.getId(), p.getName());
                    }
                }

                LinkedHashMapAdapter<Integer, String> adapter = new LinkedHashMapAdapter<Integer, String>(MainActivity.this, android.R.layout.simple_spinner_item,tmpList);
                spinnerZone.setAdapter(adapter);
                spinnerZone.setSelection(spnZone);

                LinkedHashMap<Integer,String> tmpList2  = new LinkedHashMap<Integer,String>();

                tmpList2.put(0,"Defect");
                if((int) obj.getKey() != 0){
                    for (Defect d: defectList) {
                        if(d.getIdArea() == (int)obj.getKey()){
                            tmpList2.put(d.getId(), d.getName());
                        }
                    }
                }
                adapter = new LinkedHashMapAdapter<Integer, String>(MainActivity.this, android.R.layout.simple_spinner_item,tmpList2);
                spinnerDefect.setAdapter(adapter);
                spinnerDefect.setSelection(spnDefect);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map.Entry obj = (Map.Entry) spinnerZone.getSelectedItem();

                LinkedHashMap<Integer,String> tmpList  = new LinkedHashMap<Integer,String>();
                tmpList.put(0,"Location");

                if((int) obj.getKey() != 0){
                    for (Location l: locationList) {
                        if(l.getPartId() == (int)obj.getKey()){
                            tmpList.put(l.getId(), l.getName());
                        }
                    }
                }

                LinkedHashMapAdapter<Integer, String> adapter = new LinkedHashMapAdapter<Integer, String>(MainActivity.this, android.R.layout.simple_spinner_item,tmpList);
                spinnerLocation.setAdapter(adapter);
                spinnerLocation.setSelection(spnLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //catalogs
        getModels();
        getAreas();
        getColors();
        getParts();
        getDefects();
        getLocations();

    }

    private void getModels(){
       Call<List<Model>> call = feedbackServiceApi.GetModels();
       call.enqueue(new Callback<List<Model>>() {
           @Override
           public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
               if(response.isSuccessful()){
                   modelList = response.body();
                   LinkedHashMap<Integer,String> tmpList  = new LinkedHashMap<Integer,String>();
                   tmpList.put(0,"Model");
                   for (Model m :
                           modelList) {
                       if (m.getProcess().equals("TCI")){
                           tmpList.put(m.getId(), m.getName());
                       }
                   }
                   LinkedHashMapAdapter<Integer ,String> adapter =
                           new LinkedHashMapAdapter<Integer, String>(MainActivity.this,
                                   android.R.layout.simple_spinner_item,tmpList);
                   adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                   spinnerModel.setAdapter(adapter);
                   spinnerModel.setSelection(spnModel);
               }
           }

           @Override
           public void onFailure(Call<List<Model>> call, Throwable t) {

           }
       });
    }

    private void getAreas(){
        Call<List<Area>> call = feedbackServiceApi.GetAreas();
        call.enqueue(new Callback<List<Area>>() {
            @Override
            public void onResponse(Call<List<Area>> call, Response<List<Area>> response) {
                if(response.isSuccessful()){
                    areaList = response.body();
                    LinkedHashMap<Integer,String> tmpList  = new LinkedHashMap<Integer,String>();
                    tmpList.put(0,"Area");
                    for (Area a :
                            areaList) {
                        if (a.getProcess().equals("TCI")){
                            tmpList.put(a.getId(),a.getDescription());
                        }
                    }
                    LinkedHashMapAdapter<Integer, String> adapter = new LinkedHashMapAdapter<Integer, String>(MainActivity.this, android.R.layout.simple_spinner_item,tmpList);
                    spinnerArea.setAdapter(adapter);
                    spinnerArea.setSelection(spnArea);
                }
            }

            @Override
            public void onFailure(Call<List<Area>> call, Throwable t) {

            }
        });

    }

    private void getColors(){
        Call<List<ColorCode>> call = feedbackServiceApi.GetColors();
        call.enqueue(new Callback<List<ColorCode>>() {
            @Override
            public void onResponse(Call<List<ColorCode>> call, Response<List<ColorCode>> response) {
                if(response.isSuccessful()){
                    colorList = response.body();
                    LinkedHashMap<Integer,String> tmpList  = new LinkedHashMap<Integer,String>();
                    tmpList.put(0,"Color");
                    for (ColorCode c :
                            colorList) {
                        tmpList.put(c.getId(), c.getColor());
                    }
                    LinkedHashMapAdapter<Integer, String> adapter = new LinkedHashMapAdapter<Integer, String>(MainActivity.this, android.R.layout.simple_spinner_item,tmpList);
                    spinnerColor.setAdapter(adapter);
                    spinnerColor.setSelection(spnColor);
                }
            }

            @Override
            public void onFailure(Call<List<ColorCode>> call, Throwable t) {

            }
        });
    }

    private void getParts(){
        Call <List<Part>> call = feedbackServiceApi.GetParts();
        call.enqueue(new Callback<List<Part>>() {
            @Override
            public void onResponse(Call<List<Part>> call, Response<List<Part>> response) {
                if(response.isSuccessful()){
                    partList = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Part>> call, Throwable t) {

            }
        });
    }

    private void getDefects(){
        Call <List<Defect>> call = feedbackServiceApi.GetDefects();
        call.enqueue(new Callback<List<Defect>>() {
            @Override
            public void onResponse(Call<List<Defect>> call, Response<List<Defect>> response) {
                if(response.isSuccessful()){
                    defectList = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Defect>> call, Throwable t) {

            }
        });
    }

    private void getLocations(){
        Call<List<Location>> call = feedbackServiceApi.GetLocations();
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if(response.isSuccessful()){
                    locationList = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == 0){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                imageview.setEnabled(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("currentPhotoPath",mCurrentPhotoPath);
        outState.putInt("spinnerModel",spinnerModel.getSelectedItemPosition());
        outState.putInt("spinnerDefect",spinnerDefect.getSelectedItemPosition());
        outState.putInt("spinnerZone",spinnerZone.getSelectedItemPosition());
        outState.putInt("spinnerLocation",spinnerLocation.getSelectedItemPosition());
        outState.putInt("spinnerColor",spinnerColor.getSelectedItemPosition());
        outState.putInt("spinnerArea",spinnerArea.getSelectedItemPosition());
        outState.putInt("spinnerLine",spinnerLine.getSelectedItemPosition());
        outState.putString("txtBodyNo",txtBodyNo.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private void selectImage(){
        final CharSequence[] options = {"Tomar Foto","Galeria","Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Foto!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Tomar Foto")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f=null;
                    try {
                        f = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();
                    }
                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this, "com.example.feedback.fileprovider", f);
                    mUri = photoURI;
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                    startActivityForResult(intent,1);
                }
                else if(options[which].equals("Galeria")){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,2);
                }
                else if(options[which].equals("Cancelar")){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private File createImageFile() throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                File f = new File(mCurrentPhotoPath);

                try{
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions =new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bitmapOptions);
                    imageview.setImageBitmap(bitmap);
                    rotateImage();
                }catch(Exception ex){ex.printStackTrace();}
            }
            else if(requestCode == 2){
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage,filePath,null,null,null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = BitmapFactory.decodeFile(picturePath);
                mCurrentPhotoPath = picturePath;
                imageview.setImageBitmap(thumbnail);
                rotateImage();
            }
        }

    }

    private void rotateImage(){
        try {
            File file = new File(mCurrentPhotoPath);
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
            }
            Matrix m = new Matrix();
            m.postRotate(rotate);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=2;
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file),null,options);
            bmp = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),m,true);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,80,outStream);
            imageview.setImageBitmap(bmp);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void save(){
        Map.Entry<Integer,String> itemModel = (Map.Entry<Integer,String>) spinnerModel.getSelectedItem();
        Map.Entry<Integer,String> itemDefect = (Map.Entry<Integer,String>) spinnerDefect.getSelectedItem();
        Map.Entry<Integer,String> itemArea = (Map.Entry<Integer,String>) spinnerArea.getSelectedItem();
        Map.Entry<Integer,String> itemZone = (Map.Entry<Integer,String>) spinnerZone.getSelectedItem();
        Map.Entry<Integer,String> itemLocation = (Map.Entry<Integer,String>) spinnerLocation.getSelectedItem();
        Map.Entry<Integer,String> itemColor = (Map.Entry<Integer,String>) spinnerColor.getSelectedItem();
        int line = Integer.parseInt(spinnerLine.getSelectedItem().toString());

        if(itemModel.getKey() == 0){
            Toast.makeText(getApplicationContext(), "Selecciona Model", Toast.LENGTH_SHORT).show();
            progressButton.buttonReset("Save");
            return;
        }
        if(itemDefect.getKey() == 0){
            Toast.makeText(getApplicationContext(), "Selecciona Defecto", Toast.LENGTH_SHORT).show();
            progressButton.buttonReset("Save");
            return;
        }
        if(itemArea.getKey() == 0){
            Toast.makeText(getApplicationContext(), "Selecciona Area", Toast.LENGTH_SHORT).show();
            progressButton.buttonReset("Save");
            return;
        }
        if(itemZone.getKey() == 0){
            Toast.makeText(getApplicationContext(), "Selecciona Zona", Toast.LENGTH_SHORT).show();
            progressButton.buttonReset("Save");
            return;
        }
        if(itemLocation.getKey() == 0){
            Toast.makeText(getApplicationContext(), "Selecciona Locacion", Toast.LENGTH_SHORT).show();
            progressButton.buttonReset("Save");
            return;
        }

        if(itemColor.getKey() == 0){
            Toast.makeText(getApplicationContext(), "Selecciona Color", Toast.LENGTH_SHORT).show();
            progressButton.buttonReset("Save");
            return;
        }
        if(txtBodyNo.getText().length() == 0){
            Toast.makeText(getApplicationContext(), "Captura VIN", Toast.LENGTH_SHORT).show();
            progressButton.buttonReset("Save");
            return;
        }
        //save process
        Toast.makeText(getApplicationContext(), "Guardando, espere un momento", Toast.LENGTH_LONG).show();

        try{
            File file = new File(mCurrentPhotoPath);

            Bitmap bitmap = Helper.getCompressedImageFile(file, MainActivity.this);
            String imageString =convertBitmapToString(bitmap);


            FeedbackData feedback = new FeedbackData();
            feedback.setCAR_TYPE(itemModel.getKey());
            feedback.setBODY_NO(txtBodyNo.getText().toString());
            feedback.setAREA(itemArea.getKey());
            feedback.setPART(itemZone.getKey());
            feedback.setDEFECT(itemDefect.getKey());
            feedback.setLOCATION(itemLocation.getKey());
            feedback.setIDCOLOR(itemColor.getKey());
            feedback.setLINE(line);
            feedback.setUSERID(userId);
            feedback.setPROCESS(station);
            feedback.setImageString(imageString);

            Call<FeedbackData> call = feedbackServiceApi.PostFeedback(feedback);
            call.enqueue(new Callback<FeedbackData>() {
                @Override
                public void onResponse(Call<FeedbackData> call, Response<FeedbackData> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Finished Successful ",Toast.LENGTH_LONG).show();

                        txtBodyNo.setText("");
                        spnModel = 0;
                        spnDefect = 0;
                        spnZone = 0;
                        spnColor = 0;
                        spnArea = 0;
                        spnLine = 0;
                        mCurrentPhotoPath = "";
                        spinnerModel.setSelection(0);
                        spinnerArea.setSelection(0);
                        spinnerColor.setSelection(0);
                        spinnerLine.setSelection(0);

                        imageview.setImageResource(R.drawable.ic_action_camera);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Finished Unsuccessful ",Toast.LENGTH_LONG).show();
                    }
                    progressButton.buttonReset("Save");
                }

                @Override
                public void onFailure(Call<FeedbackData> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),"onFailure"+t.getMessage(),Toast.LENGTH_LONG).show();
                    progressButton.buttonReset("Save");
                }
            });

        }catch(Exception ex){
            ex.printStackTrace();
            progressButton.buttonReset("Save");
        }
    }

    private String convertBitmapToString(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        String result = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
        return result;
    }
}