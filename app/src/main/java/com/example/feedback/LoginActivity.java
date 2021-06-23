package com.example.feedback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedback.Interface.FeedbackServiceApi;
import com.example.feedback.Model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    //private final String baseUrl="http://10.114.43.200/IQISService/api/";
    private final String baseUrl="http://10.114.3.35:92/api/";
    TextView tvUser;
    TextView tvPassword;
    String ip;
    String dbUser;
    String dbPassword;
    String dbName;
    View view;
    ProgressButton progressButton;

    private final String station = "TCI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvUser = findViewById(R.id.userId);
        tvPassword = findViewById(R.id.password);
        view =  findViewById(R.id.btnLogin);

        ip = "10.114.3.35";
        dbUser = "sa";
        dbPassword = "Kmmb4m$1";
        dbName = "feedback";

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressButton = new ProgressButton(LoginActivity.this, v);
                progressButton.buttonActivated();
                login();
            }
        });

    }

    public void login(){

        if(tvUser.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Ingrese usuario",Toast.LENGTH_LONG).show();
            progressButton.buttonReset("Login");
            return;
        }
        if(tvPassword.getText().toString().equals("")){
            progressButton.buttonReset("Login");
            Toast.makeText(getApplicationContext(),"Ingrese password",Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedbackServiceApi feedbackServiceApi = retrofit.create(FeedbackServiceApi.class);
        User user = new User();
        user.setId(tvUser.getText().toString());
        user.setPassword(tvPassword.getText().toString());

        Call<User> call = feedbackServiceApi.GetUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userResponse = response.body();
                if(userResponse.getId() == null){
                    Toast.makeText(getApplicationContext(), "Usuario y/i password incorrecto", Toast.LENGTH_SHORT).show();
                    progressButton.buttonReset("Login");
                }
                else{
                    progressButton.buttonFinished();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userId",userResponse.getId());
                    intent.putExtra("station","TCI");
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed"+t.getMessage(),Toast.LENGTH_SHORT).show();
                progressButton.buttonReset("Login");
            }
        });

        /*
        SoapCall soapCall = new SoapCall(tvUser.getText().toString(),tvPassword.getText().toString()
                ,station);
        soapCall.execute();*/
    }
/*
    public class SoapCall extends AsyncTask<String,Object,String> {

        public static final String NAMESPACE = "http://tempuri.org/";
        public static final String METHOD_NAME = "Login";
        public static final String URL = "http://10.114.3.35:88/Service1.asmx?op=Login";
        //public static final String SOAP_ACTION = "http://tempuri.org/LoginSql";
        public static final String SOAP_ACTION = "http://tempuri.org/Login";
        public int TimeOut = 30000;
        String z="";
        Boolean isSuccess = false;

        String response;

        String user, password, station;

        public SoapCall(){}

        public SoapCall(String user,String password, String station){
            this.user = user;
            this.password = password;
            this.station = station;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progress bar here

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            //create soap object
            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
            request.addProperty("user",tvUser.getText().toString());
            request.addProperty("password",tvPassword.getText().toString());
            request.addProperty("process",station);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transportSE = new HttpTransportSE(URL,TimeOut);

            try
            {
                transportSE.call(SOAP_ACTION,envelope);
                response =  envelope.getResponse().toString();

                if(response.toString().equals("NO DATA"))
                {
                    z = "credenciales invalidas!";
                    isSuccess = false;
                }else if(response.toString().equals("ERROR"))
                {
                    z = "Algo salio mal, Llama a IT";
                    isSuccess = false;
                }
                else{
                    z = "Login Exitoso!";
                    isSuccess = true;
                }


            }catch(Exception ex){
                isSuccess = false;
                z = ex.getMessage();
            }


            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //stop progressbar

            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                progressButton.buttonFinished();
                // aqui se define a que vista se va ir dependiendo del usuario.
                Intent intent;

                intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userName",this.user);
                intent.putExtra("station",this.station);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else{
                progressButton.buttonReset("Login");
            }
        }
    }*/

}