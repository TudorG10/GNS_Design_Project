package com.example.judyy.grandnapoleonsolitairegame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VictoryScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_stats);


        String info = FileHelper.ReadFile(this);
        System.out.println(info);
        TextView points = (TextView)findViewById(R.id.total_points);
        points.setText(GameActivity.totalPoints + " points!");
        TextView moves = (TextView)findViewById(R.id.total_moves);
        moves.setText(GameActivity.totalMoves + " moves!");


    }

    public void returnToMainMenu(View v){
        startActivity(new Intent(this, MainActivity.class));
    }

}
