package com.example.robert.geneticalgoandroid;

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
    Map<String, Character> binMap;
    int n=10;
    int mutRate = 10;//Average mutations per 100 bits
    String[] chromosomes;
    int[] fitness;
    Random rand;
    int GENE_LENGTH = 4;

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
        fitness = new int[n];

        //randomize initial values
        initChromosomeList();
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
            if(rand.nextInt()%100<mutationRate) {
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
}
