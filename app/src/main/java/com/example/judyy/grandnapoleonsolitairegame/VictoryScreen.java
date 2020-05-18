package com.example.judyy.grandnapoleonsolitairegame;

import android.annotation.TargetApi;
import android.content.Context;
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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class VictoryScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_stats);


        //display current game stats
        int actualPoints = (GameActivity.totalPoints  - (5 * GameActivity.totalHintsUsed) - (10 * GameActivity.totalUndosUsed));
        TextView points = (TextView)findViewById(R.id.total_points);
        points.setText(actualPoints + " points!");
        TextView moves = (TextView)findViewById(R.id.total_moves);
        moves.setText(GameActivity.totalMoves + " moves");
        TextView hints = (TextView)findViewById(R.id.total_hints);
        hints.setText(GameActivity.totalHintsUsed + " hints -> - 5 points");
        TextView undos = (TextView)findViewById(R.id.total_undos);
        undos.setText(GameActivity.totalUndosUsed + " undos -> -10 points");

        String filename = "data";
        //read data file
        int[] statsValues = dataManipulation.readDataFile(this);
        /*
        line 0: wins
        line 1: losses
        line 2: high score
        line 3: lowest moves
        line 4: most moves
        */

        String fileContents =
                (statsValues[0] + 1) + "\n" +
                        statsValues[1] + "\n" +
                        (actualPoints > statsValues[2] ? actualPoints : statsValues[2]) + "\n" +
                        (GameActivity.totalMoves < statsValues[3] && statsValues[3] != 0  ? GameActivity.totalMoves : statsValues[3]) + "\n" +
                        (GameActivity.totalMoves > statsValues[4] ? GameActivity.totalMoves : statsValues[4]) + "\n"
                ;

        try (FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        }
        catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void returnToMainMenu(View v){
        startActivity(new Intent(this, MainActivity.class));
    }

}
