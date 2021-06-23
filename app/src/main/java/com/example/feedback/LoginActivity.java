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
    
    private final String baseUrl="";
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

       
    }


}