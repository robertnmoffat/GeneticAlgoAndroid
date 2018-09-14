package com.example.robert.geneticalgoandroid;

/**
 * Created by dingus on 9/10/2018.
 */

public class Equation {
    float[] numbers;
    char[] operators;

    /**
     * Creates a new Equation object with an array of numbers and an array of each operator between each number
     * @param numbers numbers in equation
     * @param operators operator between each number in equation
     */
    public Equation(float[] numbers, char[] operators){
        this.numbers = numbers;
        this.operators = operators;
    }

    /**
     * solves the equation
     * @return float of the solved equation
     */
    public float solve(){
        //Solve multiplication and division first for BEDMAS
        solveMultDiv();
        //Solve addition and subtraction after
        float answer = solveAddSub();
        //return the result
        return answer;
    }

    /**
     * Solves multiplication and division
     * Stores answer to consecutive multiplications in the first used number position
     */
    public void solveMultDiv(){
        for(int i=0; i<operators.length; i++){
            char c = operators[i];
            if(c=='*'||c=='/'){
                int j=i;
                if(j>0) {
                    //Move to the back of previously solved multiplications and divides
                    while (operators[--j] == '*' || operators[j] == '/') {
                        if (j == 0) break;
                    }
                }else{
                    j--;
                }
                if(c=='*'){
                    numbers[j+1]=numbers[j+1]*numbers[i+1];
                }else if(c=='/'){
                    numbers[j+1]=numbers[j+1]/numbers[i+1];
                }
            }
        }
    }

    /**
     * solves addition and subtraction
     * @return
     */
    public float solveAddSub(){
        float sum = numbers[0];

        for(int i=0; i<operators.length; i++) {
            char c = operators[i];
            if(c=='*'||c=='/')continue;
            if(c=='+')sum+=numbers[i+1];
            if(c=='-')sum-=numbers[i+1];
        }

        return sum;
    }
}
