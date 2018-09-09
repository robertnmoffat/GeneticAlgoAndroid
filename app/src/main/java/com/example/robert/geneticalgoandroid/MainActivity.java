package com.example.robert.geneticalgoandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    GenAlg genAlg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        genAlg = new GenAlg();
        for(int i=0; i<10; i++) {
            String chrom = genAlg.getChromosome(i);
            String equation = genAlg.translateChromosome(chrom);
            String validEquation = genAlg.makeEquationValid(equation);
            float solvedEq = genAlg.solveEquation(validEquation);
            Log.e("chromosome", (validEquation+"="+solvedEq));
        }
    }
}
