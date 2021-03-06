package com.example.robert.geneticalgoandroid;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by dingus on 9/8/2018.
 * Class for practicing ideas of genetic algorithms.
 * Specifically using genetic algorithms to find a suitable series of math operations to arrive at a desired value
 */

public class GenAlg {
    private Map<String, Character> binMap;
    private int n=10;
    private int mutRate = 1;//Average mutations per 100 bits
    private  String[] chromosomes;
    private String[] tempChromosomes;
    private int tempChromosomeCount = 0;
    private int crossoverRatePercent = 70;
    private  float[] fitness, solutions, roulettePercent;
    private float totalFitness =0.0f;
    private Random rand;
    private  int GENE_LENGTH = 4;
    private float target = 23f;

    /**
     * Default constructor for GenAlg.
     * Initializes chromosome translation map and list.
     */
    public GenAlg(){
        rand = new Random(System.currentTimeMillis());

        //create hashmap for translating bits to math characters
        binMap = new HashMap<String, Character>();
        binMap.put("0000", '0');
        binMap.put("0001", '1');
        binMap.put("0010", '2');
        binMap.put("0011", '3');
        binMap.put("0100", '4');
        binMap.put("0101", '5');
        binMap.put("0110", '6');
        binMap.put("0111", '7');
        binMap.put("1000", '8');
        binMap.put("1001", '9');
        binMap.put("1010", '+');
        binMap.put("1011", '-');
        binMap.put("1100", '*');
        binMap.put("1101", '/');
        binMap.put("1110", 'N');
        binMap.put("1111", 'N');

        //init arrays
        chromosomes = new String[n];
        fitness = new float[n];
        solutions = new float[n];
        roulettePercent = new float[n];
        tempChromosomes = new String[n];

        //randomize initial values
        initChromosomeList();
    }

    /**
     * Update the chromosome array with the new temp chromosome array, and then clear the temp count back to zero
     */
    public void updateChromosomesFromTemp(){
        for(int i=0; i<n; i++){
            chromosomes[i]=tempChromosomes[i];
        }
        tempChromosomeCount=0;
    }

    /**
     *Boolean of whether or not a crossover should take place based on the crossover chance percentage
     * @return boolean whether a crossover should take place
     */
    public boolean crossoverChance(){
        return (rand.nextInt(100)<=crossoverRatePercent);
    }

    /**
     * Crossover two chromosomes at a random position
     * @param index1 index of chromosome 1
     * @param index2 index of chromosome 2
     */
    public void crossoverChromosomes(int index1, int index2){
        char[] newChrom1, newChrom2;
        int length = chromosomes[0].length();
        newChrom1 = new char[length];//set to the length of a chromosome
        newChrom2 = new char[length];
        String oldChrom1 = chromosomes[index1], oldChrom2 = chromosomes[index2];
        int crossPos = rand.nextInt(n);

        if(crossoverChance()) {
            for (int i = 0; i < length; i++) {
                if (i < crossPos) {
                    newChrom1[i] = oldChrom1.charAt(i);
                    newChrom2[i] = oldChrom2.charAt(i);
                } else {
                    newChrom1[i] = oldChrom2.charAt(i);
                    newChrom2[i] = oldChrom1.charAt(i);
                }
            }
        }else{
            for (int i = 0; i < length; i++) {
                newChrom1[i] = oldChrom1.charAt(i);
                newChrom2[i] = oldChrom2.charAt(i);
            }
        }

        tempChromosomes[tempChromosomeCount]=new String(newChrom1);
        tempChromosomes[tempChromosomeCount+1]=new String(newChrom2);
        tempChromosomeCount+=2;//increment the count by two so that you know where you are in the array of new chromosomes.
    }

    public void resetTempChromosomes(){
        tempChromosomeCount = 0;
    }

    /**
     * Returns a random chromosome position with weighted chance based on fitness score
     * @return
     */
    public int rouletteSelectChromosomePosition(){
        int roll = rand.nextInt(100);
        float percentPassed = 0.0f;
        for(int i=0; i<n; i++){
            percentPassed+=roulettePercent[i];
            if(percentPassed>=roll)return i;
        }
        return 0;
    }

    /**
     * Converts an equation string into an Equation object
     * @param strEq
     * @return formatted equation object
     */
    public Equation makeEquationObject(String strEq){
        float[] numbers = new float[strEq.length()/2+1];
        char[] operators = new char[strEq.length()/2];

        for(int i=0; i<strEq.length(); i++){
            if(i%2==0){//numbers
                numbers[i/2]=Character.getNumericValue(strEq.charAt(i));
            }else{//operators
                operators[i/2]=strEq.charAt(i);
            }
        }

        return new Equation(numbers, operators);
    }

    /**
     * Converts a potentially invalid math equation to a valid one
     * @param equation potentially invalid equation
     * @return valid math equation in the form of a string
     */
    public String makeEquationValid(String equation){
        char[] newEq = new char[equation.length()];
        int eqPos = 0;
        boolean lastWasInt = false;//if last char checked of equation was an integer

        for(int i=0; i<equation.length(); i++){
            char c = equation.charAt(i);
            if(!lastWasInt&&!isInteger(c))continue;//If the character is not an integer, but should be
            if(lastWasInt&&!isMathOperation(c))continue;

            newEq[eqPos++]=equation.charAt(i);
            lastWasInt=!lastWasInt;//Alternates integer and operator, so flip each time something is added to the equation
        }

        if(isMathOperation(newEq[eqPos-1]))eqPos--;

        return new String(newEq, 0, eqPos);
    }

    /**
     * is the given char a math operation
     * @param c
     * @return
     */
    public boolean isMathOperation(char c){
        if(c==42||c==43||c==45||c==47)return true;
        return false;
    }

    /**
     * is the given char an integer
     * @param c
     * @return
     */
    public boolean isInteger(char c){
        return (c>=48&&c<=57);
    }

    /**
     * Translates a chromosome into a math equation (potentially nonsense equation)
     * @param chromosome
     * @return equation string derived from the inputted chromosome
     */
    public String translateChromosome(String chromosome){
        int equationLength = chromosome.length()/GENE_LENGTH;
        char[] equation = new char[equationLength];

        for(int i=0; i<equationLength; i++){
            String currentGene = chromosome.substring(i*GENE_LENGTH,(i*GENE_LENGTH)+GENE_LENGTH);//Each gene is GENE_LENGTH bits long

            //translate current gene in binMap
            equation[i]=binMap.get(currentGene);
        }

        return new String(equation);
    }

    /**
     * Set initial randomization for all chromosomes
     */
    public void initChromosomeList(){
        for(int i=0; i<n; i++){
            chromosomes[i] = "010101010101010101010101010101010101";
            for(int j=0; j<10; j++)
            chromosomes[i] = mutateChromosome(chromosomes[i], 50);
        }
    }

    /**
     * Mutates passed chromosome based on the current mutation rate.
     * @param chromosome chromosome to be mutated
     * @return new mutated chromosome
     */
    public String mutateChromosome(String chromosome, int mutationRate){
        char[] charArray = new char[chromosome.length()];
        char currentChar;
        for(int i=0; i<chromosome.length(); i++){
            currentChar = chromosome.charAt(i);
            if(rand.nextInt(100)<mutationRate) {
                //Log.e("chromosome", "MUTATING");
                if (currentChar == '0')
                    currentChar='1';
                else
                    currentChar='0';
            }
            charArray[i]=currentChar;
        }
        return new String(charArray);
    }

    /**
     * Returns a given chromosome
     * @param index
     * @return chromosome at given index
     */
    public String getChromosome(int index){
        return chromosomes[index];
    }

    /**
     * Sets chromosome at a given position
     * @param chromosome
     * @param index
     */
    public void setChromosome(String chromosome, int index){
        chromosomes[index] = chromosome;
    }


    public void setFitness(int index, float fitness){
        this.fitness[index] = fitness;
    }

    public float getFitness(int index){
        return fitness[index];
    }

    public float getRoulettePercent(int index) {
        return roulettePercent[index];
    }

    public void setRoulettePercent(float roulettePercent, int index) {
        this.roulettePercent[index] = roulettePercent;
    }

    public int getMutRate() {
        return mutRate;
    }

    public void setMutRate(int mutRate) {
        this.mutRate = mutRate;
    }
}
