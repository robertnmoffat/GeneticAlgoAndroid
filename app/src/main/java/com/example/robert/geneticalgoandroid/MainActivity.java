package com.example.robert.geneticalgoandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    GenAlg genAlg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean solved = false;
                genAlg = new GenAlg();
                while(!solved) {
                    float totalFitness = 0.0f;
                    for (int i = 0; i < 10; i++) {
                        String chrom = genAlg.getChromosome(i);
                        String equation = genAlg.translateChromosome(chrom);
                        String validEquation = genAlg.makeEquationValid(equation);
                        Equation eq = genAlg.makeEquationObject(validEquation);
                        float solution = eq.solve();
                        if(solution==23){
                            Log.e("SOLUTION", "SOLUTION FOUND "+validEquation);
                            TextView tv = (TextView)findViewById(R.id.answerTextView);
                            tv.setText("SOLUTION FOUND "+validEquation);
                            solved=true;
                            break;
                        }
                        float fitness = (1 / (float)Math.abs(solution - 23f));
                        genAlg.setFitness(i, fitness);
                        totalFitness += fitness;
                        Log.e("" + i + " chromosome", (validEquation + "=" + solution + " Fitness = " + fitness));
                    }
                    for (int i = 0; i < 10; i++) {
                        float roulettePercent = (genAlg.getFitness(i) / totalFitness * 100);
                        genAlg.setRoulettePercent(roulettePercent, i);
                        Log.e("" + i + " chromosome", "Percent: " + roulettePercent);
                    }

                    for (int i = 0; i < 5; i++) {
                        int pos1, pos2;
                        pos1 = genAlg.rouletteSelectChromosomePosition();
                        pos2 = genAlg.rouletteSelectChromosomePosition();
                        Log.e("chromosome", "Selected:" + pos1 + ", " + pos2);

                        genAlg.crossoverChromosomes(pos1, pos2);
                    }

                    genAlg.updateChromosomesFromTemp();

                    for(int i=0; i<10; i++){
                        String chrom = genAlg.mutateChromosome(genAlg.getChromosome(i), genAlg.getMutRate());
                        genAlg.setChromosome(chrom, i);
                    }
                }
            }
        });


    }
}
