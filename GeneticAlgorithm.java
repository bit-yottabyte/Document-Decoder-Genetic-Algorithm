//Athavan Jesunesan
//COSC 3P71 Assignment #2 - Genetic Algorithms

public class GeneticAlgorithm {

    FitnessCalculator f = new FitnessCalculator();
    double bestGen;
    public GeneticAlgorithm() {

        /* Parameter settings */
        int maxGen = 10;
        int perPopulation = 50;
        char[][] arr = f.getShreddedDocument("./document1-shredded.txt");
        int[][][] population = new int[maxGen][perPopulation][arr.length];
        int[] crossovers = {100, 100, 90, 90, 80};
        int[] mutations = {0, 10, 0, 10, 20};
        /* -------------------------------- */

        System.out.println("Document 1");
        System.out.println("There will be "+ maxGen + " generations for each combination"); //Combination refers to 100% crossover and 0% mutations
        System.out.println("There are "+ perPopulation + " Individuals for each population"); 
        System.out.println();
        System.out.println("Ordered Crossover:");
        /* Odered OX */
        
        for(int i = 0; i<5; i++){ //Each of the combinations of crossovers and mutations
            System.out.println();
            System.out.println("Crossover Rate:"+crossovers[i]+ " Mutation Rate:"+mutations[i]);
            System.out.println();
            for(int j = 0; j<maxGen; j++){ //Each Generation
                if(j==0) {
                    population[j] = PopulationInitializer(perPopulation, arr.length); //2D array of all perms
                    bestGen = 2000;//Reset 
                } else {
                    population[j] = Reproduction(population[j-1], 3, arr);
                    population[j] = OrderCrossover(population[j], crossovers[i]);
                    population[j] = Mutation(population[j], mutations[i]);
                }
                int[] best = new int[arr.length];
                best = Bestpop(population[j], arr);
                System.out.println();
                System.out.println("Generation:"+(j+1));
                System.out.println("Best Fitness:"+f.fitness(arr, best));
                System.out.println("The chromosome:");
                for(int x = 0; x<arr.length; x++){
                    System.out.print(best[x]+" ");
                }
                System.out.println();
                if(f.fitness(arr, best)<bestGen) bestGen = f.fitness(arr, best);
            }
            System.out.println("Best Fitness of the Generation:"+bestGen);
            System.out.println("Average population value:" + Average(population[i], arr));
            System.out.println("-------------------------------------------------------");
            System.out.println();
        }

        /* Uniform-Ordered Crossover */

        System.out.println();
        System.out.println("Uniform-Ordered Crossover:");        
        for(int i = 0; i<5; i++){ //Each of the combinations of crossovers and mutations
            System.out.println();
            System.out.println("Crossover Rate:"+crossovers[i]+ " Mutation Rate:"+mutations[i]);
            System.out.println();
            for(int j = 0; j<maxGen; j++){ //Each Generation
                if(j==0) {
                    population[j] = PopulationInitializer(perPopulation, arr.length); //2D array of all perms
                    bestGen = 20000;//Reset 
                } else {
                    population[j] = Reproduction(population[j-1], 3, arr);
                    population[j] = UOX(population[j], crossovers[i]);
                    population[j] = Mutation(population[j], mutations[i]);
                }
                int[] best = new int[arr.length];
                best = Bestpop(population[j], arr);
                System.out.println();
                System.out.println("Generation:"+(j+1));
                System.out.println("Best Fitness:"+f.fitness(arr, best));
                System.out.println("The chromosome:");
                for(int x = 0; x<arr.length; x++){
                    System.out.print(best[x]+" ");
                }
                System.out.println();
                if(f.fitness(arr, best)<bestGen) bestGen = f.fitness(arr, best);
            }
            System.out.println("Best Fitness of the Generation:"+bestGen);
            System.out.println("Average population value:" + Average(population[i], arr));
            System.out.println("-------------------------------------------------------");
            System.out.println();
        }

        /* Ordered crossover */



    }//Constructor

    public int[][] PopulationInitializer(int popSize, int length){ //Population size, and the length of a perm/chromosome array
        int[][] pop = new int[popSize][length]; //The entire population
        int[] arr = new int[length]; //A reference array from 1 -> n-1 to shuffle popSize amount of times

        for(int i = 0; i<length; i++){
            arr[i] = i;
        }

        for(int i = 0; i<popSize; i++){
            pop[i] = arr.clone();   //Set this perm to arr
            for(int j=0; j<length; j++){ // shuffle that perm
                int index = (int)(Math.random()*(length-1)); //Get a random index
                int temp = pop[i][index];
                pop[i][index] =  pop[i][j];   //Then swap that with the current
                pop[i][j] = temp;
            }
        }
        return pop;
    }

    public int[][] Reproduction(int[][] candidates, int k, char[][] shreds){ // Candidates is the entire population
        int height = candidates.length;
        int length = candidates[0].length;
        int[][] nominees = new int[height][length];
        double best = 0;
        int index = 0;//Index of most fit individual to be added
        
        int firstBest, secondBest; // All for Elitism

            /* Elitism where n=2 */
            if(f.fitness(shreds, candidates[0])<f.fitness(shreds, candidates[1])){ //Determining which one is best of the first 2
                firstBest = 0;  
                secondBest = 1;
            }else{
                firstBest = 1;  
                secondBest = 0;
            }


            for(int i = 2; i<candidates.length; i++){ //Determining which are the top 2 out of entire population
                double current = f.fitness(shreds, candidates[i]);
                if(f.fitness(shreds, candidates[firstBest])>current){
                    int temp = firstBest;
                    firstBest = i;
                    secondBest = temp;
                } else if (f.fitness(shreds, candidates[secondBest])>current){
                    secondBest = i;
                }
            }   
            nominees[0] = candidates[firstBest];
            nominees[1] = candidates[secondBest];

        for(int i = 2; i<height; i++){//Starting at 2 due to Elitism
            for(int j = 0; j<k; j++){
                int rand = (int)(Math.random()*(height-1));
                if(j==0){                                       // The first individual will be default most fit
                    best = f.fitness(shreds, candidates[rand]);
                    index = rand;
                } else if(best>f.fitness(shreds, candidates[rand])){ // compare to others to see which is most fit 
                    best = f.fitness(shreds, candidates[rand]);
                    index = rand;
                }
            }
            nominees[i] = candidates[index];            
        }

        return nominees;

    }

    public int[][] OrderCrossover(int[][] nominees, int rate){
        int height = nominees.length;
        int length = nominees[0].length;
        int[][] offSprings = new int[height][length];

        /* Adding the first and second best from elitism to new generation */
        offSprings[0] = nominees[0];
        offSprings[1] = nominees[1];
            /* Crossover Portion */

            /* I am assuming that a crossover rate of 90% means that
             * if I generate a number from 0->100 and it lands above the rate
             * then this individual does not recieve a crossover and is placed 
             * directly into the next population. I was considering interpreting this as not including it in the next generation
             * however I decided to go with the former method 
             */

            /* There is only 1 possibility when doing the following portion
             * 1: The locusLength will land somewhere in the middle of the chromosome
             *    I will then check the remaining portion for the order of numbers and then the beginning
             */

        int locusesLength = (int)(nominees[0].length*0.4); // The consecutive locuses we use for OX will be about ~40% of the chromosome
        for(int i = 2; i<height; i++){ //Starting at 3rd postion due to the Elitism
            if(Math.random()*(100)>rate){ //If it landed in region above the rate then move on to next individual
                offSprings[i] = nominees[i];
            } else {
                int pos = (int)(Math.random()*(length-locusesLength-1)+1); //Starting position of the locuses length 
                int[] subSet = new int[locusesLength]; //Makes sure we do not have duplicates in child
                for(int j = pos; j<pos+locusesLength; j++){//Taking Parent 1's portion
                    offSprings[i][j] = nominees[i][j];
                    subSet[j-pos] = nominees[i][j];
                }
                int p = pos+locusesLength; //Reference pointer to the child
                for(int j = pos+locusesLength; j<length; j++){ //Checking elements to the right of the swath in parent 2
                    if(!InArray(subSet, nominees[i-1][j])){//if this value is not in swath of locueses then add it to child and increment p
                        offSprings[i][p] = nominees[i-1][j]; 
                        p++;
                    }
                    if(p==length){//if p hits the end of the array then put it the front 
                        p=0;
                    }
                }
                for(int j = 0; j<pos+locusesLength; j++){ //Checking remaining elements in parent 2
                    if(!InArray(subSet, nominees[i-1][j])){//if this value is not in swath of locueses then add it to child and increment p
                        offSprings[i][p] = nominees[i-1][j]; 
                        p++;
                    }
                    if(p==length){//if p hits the end of the array then put it the front
                        p=0;
                    }
                }   
            }     
        }
        return offSprings;
    }

    public int[][] UOX(int[][] nominees, int rate){
        int height = nominees.length;
        int length = nominees[0].length;
        int[][] offSprings = new int[height][length];
        int maskLength = (int)(nominees[0].length*0.4); // The amount of locuses switched will be ~60% of the length
        int rand;

        /* Elistism */
        offSprings[0] = nominees[0];
        offSprings[1] = nominees[1];

        /* Uniform-Oder Crossover */
        for(int i = 2; i<height; i++){
            if(Math.random()*(100)>rate){ //If it landed in region above the rate then move on to next individual
                offSprings[i] = nominees[i];
            } else {
                int[] mask = new int[length];
                for(int j = 0; j<maskLength; j++){ //Creating the mask
                    rand = (int)(Math.random()*(length-1));
                    mask[rand] = 1;    
                }
                for(int j = 0; j<length; j++){//Create holes
                    if(mask[j] == 1){
                        offSprings[i][j] = nominees[i][j]; //The parts of Parent 1 that stay
                    } else {
                        offSprings[i][j] = -1;
                    }
                }
                int pointer = 0; // always has index of the next hole
                for(int j = 0; j<length; j++){//Fill in holes
                    while(pointer<length && mask[pointer] == 1){ //Travels to the next hole
                        pointer++;
                    }
                    if(!InArray(offSprings[i], nominees[i-1][j])){ // If this value is not in offSprings then add it
                        offSprings[i][pointer] = nominees[i-1][j];
                        pointer++;
                    }
                }
            }
        }
        return offSprings;
    }

    public int[][] Mutation(int[][] nominees, int rate){//Single-Point Mutation
        int height = nominees.length;
        int length = nominees[0].length;

        for(int i = 2; i<height; i++){ //Starting at 3rd postion due to the Elitism
            if(Math.random()*(100)>rate){ //If it landed in region above the rate then move on to next individual
                //If this is true then do nothing
            } else {
                 int first = (int)(Math.random()*(length-1));
                 int second = (int)(Math.random()*(length-1));
                 while(second == first){//Ensures that the two points are distinct
                    second = (int)(Math.random()*(length-1));
                 }
                 int temp = nominees[i][first];
                 nominees[i][first] = nominees[i][second];
                 nominees[i][second] = temp; 
            }
        }
        return nominees;
    }

    public boolean InArray(int[] subSet, int val){ //Checks to see if a value is in the subSet
        for(int i = 0; i<subSet.length; i++){
            if(val == subSet[i]) return true;
        }
        return false;
    }

    public int[] Bestpop(int[][] check, char[][] shreds){//Finds best individual in a popualation
        int[] best = new int[check[0].length];
        best = check[0];
        for(int i = 1; i<check.length; i++){
            if(f.fitness(shreds, best)>f.fitness(shreds, check[i])){
                best = check[i];
            }
        }

        return best;
    }

    public double Average(int[][] population, char[][] shreds){ //Population average
        double average;
        int height = population.length;
        double store = 0;
        for(int i = 0; i<height; i++){
            store += f.fitness(shreds, population[i]);
        }
        average = store/height;

        return average;
    }

    public static void main(String[] args) {
        GeneticAlgorithm g = new GeneticAlgorithm();        
    }
}