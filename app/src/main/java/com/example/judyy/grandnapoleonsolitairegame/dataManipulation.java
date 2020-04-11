package com.example.judyy.grandnapoleonsolitairegame;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class dataManipulation extends AppCompatActivity {

    /**
     * Method to read the data file
     *         line 0: wins
     *         line 1: losses
     *         line 2: high score
     *         line 3: lowest moves
     *         line 4: most moves
     * @param c Context in order to open the file
     * @return an array of the 5 statistics we track
     */
    public static int[] readDataFile(Context c) {
        String filename = "data";
        int[] globalStats = new int[5];

        try {
            FileInputStream fis = c.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                int lineCounter = 0;
                while (line != null) {
                    switch(lineCounter){
                        case 0:
                            globalStats[0] = Integer.parseInt(line);
                            break;
                        case 1:
                            globalStats[1] = Integer.parseInt(line);
                            break;
                        case 2:
                            globalStats[2] = Integer.parseInt(line);
                            break;
                        case 3:
                            globalStats[3] = Integer.parseInt(line);
                            break;
                        case 4:
                            globalStats[4] = Integer.parseInt(line);
                            break;
                    }
                    lineCounter++;
                    line = reader.readLine();
                }
                fis.close();
                inputStreamReader.close();
            } catch (IOException e) {
                // Error occurred when opening raw file for reading.
            } finally {
                return globalStats;

            }
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        return globalStats;

    }
}
