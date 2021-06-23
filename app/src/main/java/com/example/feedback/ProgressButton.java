package com.example.feedback;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ProgressButton {
    private CardView cardView;
    private ProgressBar progressBar;
    private TextView textView;
    private ConstraintLayout constraintLayout;

    ProgressButton(Context ctx, View v){
        cardView = v.findViewById(R.id.cardview);
        progressBar = v.findViewById(R.id.progressBar);
        textView = v.findViewById(R.id.textView);
        constraintLayout = v.findViewById(R.id.constraint_layout);
    }

    void buttonActivated(){
        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Wait");
    }
    void buttonFinished(){
        progressBar.setVisibility(View.GONE);
        textView.setText("Done");
    }
    void buttonReset(String text){
        progressBar.setVisibility(View.GONE);
        textView.setText(text);

    }

}
