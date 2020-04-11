package com.example.judyy.grandnapoleonsolitairegame;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
//import com.opencsv.CSVReader;

public class GlobalStats extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.global_stats);

        //read values from data file
        int[] statsValues = dataManipulation.readDataFile(this);

        //set values on screen
        TextView statsWins = (TextView)findViewById(R.id.stats_wins);
        statsWins.setText(statsValues[0] + " wins");
        TextView statsLosses = (TextView)findViewById(R.id.stats_losses);
        statsLosses.setText(statsValues[1] + " losses");
        TextView statsHighScore = (TextView)findViewById(R.id.stats_highscore);
        statsHighScore.setText("Highest score: " + statsValues[2]);
        TextView statsLowestMoves = (TextView)findViewById(R.id.stats_lowestmoves);
        statsLowestMoves.setText("Lowest moves: " + statsValues[3]);
        TextView statsMostMoves = (TextView)findViewById(R.id.stats_mostmoves);
        statsMostMoves.setText("Most moves: " + statsValues[4]);
    }

    public void returnToMainMenu(View v){
        startActivity(new Intent(this, MainActivity.class));
    }


}
