/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author FAkinola
 */
public class GeneticAlgorithm {
    /**
     * @param args the command line arguments
     */
    private Population population = new Population();
    private Population population2 = new Population();
    private Population switchOverPopulation;
    private Population switchOverPopulation2;
    @NotNull
    private Computations computations = new Computations();

    private ChromosomeSelection firstinPopulation1Picked;
    private ChromosomeSelection secondinPopulation1Picked;
    private ChromosomeSelection firstOffSpringProducedInPopulation1;
    private ChromosomeSelection secondOffSpringProducedInPopulation1;
    private ChromosomeSelection thirdOffSpringProducedInPopulation1;
    private ChromosomeSelection fourthOffSpringProducedInPopulation1;

    private ChromosomeSelection firstinPopulation2Picked;
    private ChromosomeSelection secondinPopulation2Picked;
    private ChromosomeSelection firstOffSpringProducedInPopulation2;
    private ChromosomeSelection secondOffSpringProducedInPopulation2;
    private ChromosomeSelection thirdOffSpringProducedInPopulation2;
    private ChromosomeSelection fourthOffSpringProducedInPopulation2;

    private int generationCount = 1;
    private boolean fps = false;
    private double[] fitnessProb;
    private double[] fitnessProbPop2;
    private boolean rastrigan = false;
    private double currentHighestlevelOfFitness;
    private int noOfmutations = 0;
    private int noOfComputatons = 0;
    private int noOfCrossover = 0;
    private boolean foundFittest = false;
    //this controls if what we are computing contains integer or binary values
    private int bound = 2;
    private int[] popSizeArray = {2, 4, 6, 10, 20, 50, 100, 150, 200, 250, 300, 350, 400};
    private int popSize = 2;
    //this dictates the length of each individuals/chromosomes
    private int geneLength = 64;
    @NotNull
    private double[][] eval_Values = new double[6][2];
    @NotNull
    private List<ChromosomeSelection> population1EvalTeam = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> evaluators = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> population2EvalTeam = new ArrayList<>();
    private int noOfEvaluation = 0;

    public static void main(String[] args) throws CloneNotSupportedException {
        ArrayList<String> result = new ArrayList<>();

        GeneticAlgorithm ga = new GeneticAlgorithm();
//Get the file reference
        Path path = Paths.get("C:\\Users\\GO\\IdeaProjects\\Coev\\output.txt");

        for (int popsiz : ga.popSizeArray) {

//Use try-with-resource to get auto-closeable writer instance
            for (int i = 0; i < 20; i++) {
                String fittestChromosome = "";
                result.add("\n" + popsiz + "\t ");
                ga.resetter();
                //Initialize population
                ga.population.initializePopulation(ga.bound, ga.geneLength, ga.rastrigan, popsiz);
                ga.population2.initializePopulation(ga.bound, ga.geneLength, ga.rastrigan, popsiz);
                String bestPartner = "";
                ga.switchOverPopulation = (Population) ga.population.clone(ga.switchOverPopulation);
                ga.switchOverPopulation2 = (Population) ga.population2.clone(ga.switchOverPopulation2);
                //While population searches for a chromosome with maximum fitness
                while (((ga.currentHighestlevelOfFitness > 0 && ga.rastrigan)
                        || (ga.currentHighestlevelOfFitness < (ga.geneLength * 2) && !ga.rastrigan))
                ) {
                    ++ga.generationCount;
                    if (ga.fps) {
                        ga.fitnessProb = ga.population.calculateProbFitness(ga.rastrigan);
                        ga.fitnessProbPop2 = ga.population2.calculateProbFitness(ga.rastrigan);
                    }
                    int beginfrom = ga.naturalSelection(new Random().nextBoolean());
                    //Do the things involved in evolution
                    for (; beginfrom < popsiz; beginfrom += 2) {
                        if (ga.fps) {
                            ga.fPSelection(ga.rastrigan);
                        } else {
                            ga.tournamentSelection(popsiz, ga.rastrigan);
                        }
                        ga.process(beginfrom);
                    }
                    // moving the new generation into the old generation space
                    ga.population = (Population) ga.switchOverPopulation.clone(ga.population);
                    ga.population2 = (Population) ga.switchOverPopulation2.clone(ga.population);

                    //Calculate new fitness value
                    //todo the best value all through
                    fittestChromosome = ga.population2.getChromosome(ga.population2.maxFit).getStringChromosome();
                    ga.currentHighestlevelOfFitness = ga.population2.fittest;
                    System.out.println("The maxfit is" + ga.population2.maxFit);
                    if (ga.population2.getChromosome(ga.population2.maxFit).fitness ==
                            ga.population2.fittest) {
                        bestPartner = ga.population2.getChromosome(ga.population2.maxFit).partnerChromosome;

                    } else {
                        bestPartner = ga.population2.getChromosome(ga.population2.maxFit).partner2Chromosome;

                    }
                    System.out.println("Generation: " + ga.generationCount + " Fittest: " + ga.currentHighestlevelOfFitness);
                    System.out.println("The best pair are: " + fittestChromosome +
                            " and\n " + bestPartner);

                }
                //when a solution is found or 100 generations have been produced
                System.out.println("\nno of evaluations " + ga.noOfEvaluation);
                System.out.println("\nSolution found in generation " + ga.generationCount);
                System.out.println("Fitness: " + ga.currentHighestlevelOfFitness);
                System.out.println("The best pair are: " + fittestChromosome +
                        " and \n" + bestPartner);
                System.out.println("probability of mutation is " + (double) ga.noOfmutations / ga.noOfComputatons);
                System.out.println("probability of cross over is " + (double) ga.noOfCrossover / ga.noOfComputatons);
                result.add(String.valueOf(ga.noOfEvaluation));

            }
        }
//Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(String.valueOf(result));
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void populationEvaluationStarters(ChromosomeSelection chromSel1,
                                              ChromosomeSelection chromSel2, ChromosomeSelection chromSel3,
                                              ChromosomeSelection chromSel4) {
        population1EvalTeam.add(chromSel1);
        population1EvalTeam.add(chromSel2);
        population2EvalTeam.add(chromSel3);
        population2EvalTeam.add(chromSel4);
    }

    private void process(int position) throws CloneNotSupportedException {
        Random rn = new Random();
        population1EvalTeam.clear();
        population2EvalTeam.clear();
        populationEvaluationStarters(
                firstinPopulation1Picked,
                secondinPopulation1Picked,
                firstinPopulation2Picked,
                secondinPopulation2Picked
        );
        ++noOfComputatons;
        //crossover with a random and quite high probability
        if (rn.nextInt() % 5 < 4) {
            ++noOfCrossover;
            //onePointCrossover();
            uniformCrossover();
            twoPointCrossover();
        } else {
            populationEvaluationStarters(
                    firstinPopulation1Picked,
                    secondinPopulation1Picked,
                    firstinPopulation2Picked,
                    secondinPopulation2Picked);
            //  ++ga.noOfCrossover;
            twoPointCrossover();
        }

        //mutate with a random and quite low probability
        if (rn.nextInt() % 23 >= 18) {
            ++noOfmutations;
            mutation();
        }
        evaluators.clear();
        evaluators.add((ChromosomeSelection) firstinPopulation2Picked.clone());
        evaluators.add((ChromosomeSelection) secondinPopulation2Picked.clone());
        evaluation(position);
    }

    private void resetter() {
        population = new Population();
        population2 = new Population();
        switchOverPopulation = null;
        switchOverPopulation2 = null;
        firstinPopulation1Picked = null;
        secondinPopulation1Picked = null;
        firstOffSpringProducedInPopulation1 = null;
        secondOffSpringProducedInPopulation1 = null;
        thirdOffSpringProducedInPopulation1 = null;
        fourthOffSpringProducedInPopulation1 = null;

        firstinPopulation2Picked = null;
        secondinPopulation2Picked = null;
        firstOffSpringProducedInPopulation2 = null;
        secondOffSpringProducedInPopulation2 = null;
        thirdOffSpringProducedInPopulation2 = null;
        fourthOffSpringProducedInPopulation2 = null;
        eval_Values = new double[6][2];
        population1EvalTeam = new ArrayList<>();
        evaluators = new ArrayList<>();
        population2EvalTeam = new ArrayList<>();
        noOfEvaluation = 0;
        generationCount = 1;
        noOfmutations = 0;
        noOfComputatons = 0;
        noOfCrossover = 0;
        currentHighestlevelOfFitness = 0;
    }

    //Selection
    private int naturalSelection(boolean elitism) throws CloneNotSupportedException {
        if (generationCount > 1) {
            //Select the most fittest chromosome
            ChromosomeSelection fittest = (ChromosomeSelection) switchOverPopulation.getChromosome(population.maxFit).clone();

            //Select the second most fittest chromosome
            ChromosomeSelection secondFittest = (ChromosomeSelection) switchOverPopulation.getChromosome(population.maxFitOfSecondFittest).clone();
            firstinPopulation1Picked = fittest;
            secondinPopulation1Picked = secondFittest;
            fittest = (ChromosomeSelection) switchOverPopulation2.getChromosome(switchOverPopulation2.maxFit).clone();

            //Select the second most fittest chromosome
            secondFittest = (ChromosomeSelection) switchOverPopulation2.getChromosome(switchOverPopulation2.maxFitOfSecondFittest).clone();

            firstinPopulation2Picked = (ChromosomeSelection) fittest.clone();
            secondinPopulation2Picked = (ChromosomeSelection) secondFittest.clone();
            process(0);
            return 2;
        }
        return 0;
    }

    /**
     * @param popSize
     * @param rastrigan this picks two chromosomes randomly. In tournament selection, the norm is to randomly pick k numbers of chromosomes,
     *                  then select the best and return it to the population so as to increase the chance of picking global optimum.
     *                  k can be between 1 and n; Here, I'm picking one random chromosome each then the reproduction process.
     */
    private void tournamentSelection(int popSize, boolean rastrigan) throws CloneNotSupportedException {
        if (generationCount > 2) {
            eval_Values = new double[10][2];
            // List<ChromosomeSelection> populationTournamentTeam = new ArrayList<>();
            population1EvalTeam.clear();
            population2EvalTeam.clear();
            String pos = tournamentSelection(popSize, population1EvalTeam, switchOverPopulation);
            firstinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[0])).clone();
            secondinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[1])).clone();
            tournamentSelection(popSize, population2EvalTeam, switchOverPopulation2);
            firstinPopulation2Picked = (ChromosomeSelection) population2EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[0])).clone();
            secondinPopulation2Picked = (ChromosomeSelection) population2EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[1])).clone();
            eval_Values = new double[6][2];
            population1EvalTeam.clear();
            population2EvalTeam.clear();
        } else {
            firstinPopulation1Picked = (ChromosomeSelection) switchOverPopulation.randomlyPicked(popSize).clone();
            secondinPopulation1Picked = (ChromosomeSelection) switchOverPopulation.randomlyPicked(popSize).clone();
            firstinPopulation2Picked = (ChromosomeSelection) switchOverPopulation2.randomlyPicked(popSize).clone();
            secondinPopulation2Picked = (ChromosomeSelection) switchOverPopulation2.randomlyPicked(popSize).clone();
        }
    }

    private String tournamentSelection(int popSize, List<ChromosomeSelection> population1EvalTeam,
                                       Population switchOverPopulation) throws CloneNotSupportedException {
        for (int i = 0; i < 10; i++) {
            population1EvalTeam.add((ChromosomeSelection) switchOverPopulation.randomlyPicked(popSize).clone());
            eval_Values[i][0] = population1EvalTeam.get(i).fitness;
            eval_Values[i][1] = population1EvalTeam.get(i).secondFitness;
        }
        int max = 0;
        int secondMax = 0;
        int max2 = 0;
        int secondMax2 = 0;
        for (int i = 0; i < eval_Values.length; i++) {
            if (eval_Values[i][0] >= eval_Values[max][0]) {
                secondMax = max;
                max = i;
            }
            if (eval_Values[i][1] >= eval_Values[max2][1]) {
                secondMax2 = max2;
                max2 = i;
            }
        }
        String theTwoPos = max + " ";
        if (max != max2) {
            theTwoPos += String.valueOf(max2);
        } else {
            if (eval_Values[secondMax][0] > eval_Values[secondMax2][1]) {
                theTwoPos += String.valueOf(secondMax);
            } else {
                theTwoPos += String.valueOf(secondMax2);
            }
        }
        return theTwoPos;
    }


    private void fPSelection(boolean rastrigan) throws CloneNotSupportedException {
        double rand = new Random().nextDouble();
        firstinPopulation1Picked = (ChromosomeSelection) computations.binaryToGray(population.
                getChromosome(positionOfChromosome(rand, fitnessProb)), bound, rastrigan).clone();
        rand = new Random().nextDouble();
        secondinPopulation1Picked = (ChromosomeSelection) computations.binaryToGray(population.
                getChromosome(positionOfChromosome(rand, fitnessProb)), bound, rastrigan).clone();
        rand = new Random().nextDouble();
        firstinPopulation2Picked = (ChromosomeSelection) computations.binaryToGray(population2.
                getChromosome(positionOfChromosome(rand, fitnessProbPop2)), bound, rastrigan).clone();
        rand = new Random().nextDouble();
        secondinPopulation2Picked = (ChromosomeSelection) computations.binaryToGray(population2.
                getChromosome(positionOfChromosome(rand, fitnessProbPop2)), bound, rastrigan).clone();
    }

    private int positionOfChromosome(double rand, double[] fitnessProbability) {
        if (rand > 0.6) {
            for (int i = popSize - 1; i > 0; i--) {
                if (rand > fitnessProbability[i]) {
                    return i + 1;
                }
            }

        } else {
            for (int i = 0; i < popSize; i++) {
                if (rand < fitnessProbability[i]) {
                    return i;
                }
            }
        }
        return 0;
    }

    //Two point crossover
    private void twoPointCrossover() throws CloneNotSupportedException {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint1 = rn.nextInt(ChromosomeSelection.geneLength);
        int crossOverPoint2 = rn.nextInt(ChromosomeSelection.geneLength);
        if (crossOverPoint1 > crossOverPoint2) {
            int temp = crossOverPoint2;
            crossOverPoint2 = crossOverPoint1;
            crossOverPoint1 = temp;
        }
        thirdOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
        fourthOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
        thirdOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
        fourthOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
        //Swap values among parents
        crossOver(crossOverPoint1, crossOverPoint2, thirdOffSpringProducedInPopulation1, fourthOffSpringProducedInPopulation1);
        crossOverPoint1 = rn.nextInt(ChromosomeSelection.geneLength);
        crossOverPoint2 = rn.nextInt(ChromosomeSelection.geneLength);
        if (crossOverPoint1 > crossOverPoint2) {
            int temp = crossOverPoint2;
            crossOverPoint2 = crossOverPoint1;
            crossOverPoint1 = temp;
        }
        //Swap values among parents
        crossOver(crossOverPoint1, crossOverPoint2, thirdOffSpringProducedInPopulation2, fourthOffSpringProducedInPopulation2);
        populationEvaluationStarters(thirdOffSpringProducedInPopulation1,
                fourthOffSpringProducedInPopulation1,
                thirdOffSpringProducedInPopulation2,
                fourthOffSpringProducedInPopulation2);

    }

    private void crossOver(int crossOverPoint1, int crossOverPoint2,
                           ChromosomeSelection offSpringProduced1,
                           ChromosomeSelection offSpringProduced2) {
        for (int i = crossOverPoint1; i < crossOverPoint2; i++) {
            int temp = offSpringProduced1.genes[i];
            offSpringProduced1.genes[i] = offSpringProduced2.genes[i];
            offSpringProduced2.genes[i] = temp;

        }
    }

    //One point crossover
    private void onePointCrossover() throws CloneNotSupportedException {
        Random rn = new Random();

        //Select a random crossover point
        int crossOverPoint = rn.nextInt(ChromosomeSelection.geneLength);
        firstOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
        secondOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
        firstOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
        secondOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
        //Swap values among parents
        crossOver(0, crossOverPoint, firstOffSpringProducedInPopulation1, secondOffSpringProducedInPopulation1);
        crossOverPoint = rn.nextInt(ChromosomeSelection.geneLength);
        crossOver(0, crossOverPoint, firstOffSpringProducedInPopulation2, secondOffSpringProducedInPopulation2);

        populationEvaluationStarters(firstOffSpringProducedInPopulation1,
                secondOffSpringProducedInPopulation1,
                firstOffSpringProducedInPopulation2,
                secondOffSpringProducedInPopulation2);

    }

    //Uniform crossover
    private void uniformCrossover() throws CloneNotSupportedException {
        firstOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
        secondOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
        firstOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
        secondOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();

        //Select a random crossover point
        //int crossOverPoint = rn.nextInt(ChromosomeSelection.geneLength);

        //Swap values uniformly among parents
        for (int i = 0; i < ChromosomeSelection.geneLength; i++) {
            int temp = firstOffSpringProducedInPopulation1.genes[i];
            firstOffSpringProducedInPopulation1.genes[i] = secondOffSpringProducedInPopulation1.genes[i];
            secondOffSpringProducedInPopulation1.genes[i] = temp;
            temp = firstOffSpringProducedInPopulation2.genes[i];
            firstOffSpringProducedInPopulation2.genes[i] = secondOffSpringProducedInPopulation2.genes[i];
            secondOffSpringProducedInPopulation2.genes[i] = temp;
            i++;
        }
        populationEvaluationStarters(firstOffSpringProducedInPopulation1,
                secondOffSpringProducedInPopulation1,
                firstOffSpringProducedInPopulation2,
                secondOffSpringProducedInPopulation2);

    }

    /**
     * picking a random gene and swapping it with its allelle
     */
    private void mutation() {
        Random rn = new Random();
        //Flip values at the mutation point
        mutate(rn, firstOffSpringProducedInPopulation1, secondOffSpringProducedInPopulation1);
        mutate(rn,
                firstOffSpringProducedInPopulation2, secondOffSpringProducedInPopulation2);
        if (rn.nextInt(4) > 1) {

            //Flip values at the mutation point
            mutate(rn, thirdOffSpringProducedInPopulation1, fourthOffSpringProducedInPopulation1);
            mutate(rn, thirdOffSpringProducedInPopulation2, fourthOffSpringProducedInPopulation2);

        }
    }

    private void mutate(Random rn, ChromosomeSelection firstOffSpringProducedInPopulation1,
                        ChromosomeSelection secondOffSpringProducedInPopulation1) {
        //Select a random mutation point
        int mutationPoint = rn.nextInt(ChromosomeSelection.geneLength);
        try {
            firstOffSpringProducedInPopulation1.genes[mutationPoint]
                    = computations.getRandomAllele(firstOffSpringProducedInPopulation1.genes[mutationPoint], bound);

            mutationPoint = rn.nextInt(ChromosomeSelection.geneLength);
            secondOffSpringProducedInPopulation1.genes[mutationPoint]
                    = computations.getRandomAllele(secondOffSpringProducedInPopulation1.genes[mutationPoint], bound);
        } catch (NullPointerException e) {

        }
    }

    private void evaluation(int position) throws CloneNotSupportedException {
        baseEvaluation(position, population1EvalTeam, switchOverPopulation);
        eval_Values = new double[6][2];
        baseEvaluation(position, population2EvalTeam, switchOverPopulation2);
        evaluators.clear();
        eval_Values = new double[6][2];

    }

    private void baseEvaluation(int position, List<ChromosomeSelection> populationEvalTeam,
                                Population switchOverPopulation) throws CloneNotSupportedException {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < eval_Values.length; j++) {
                ++noOfEvaluation;
                eval_Values[j][i] = populationEvalTeam.get(j).calcPairedFitness(
                        evaluators.get(i).getStringChromosome(), i);
            }
        }
        bestSelected(position, populationEvalTeam, switchOverPopulation);

    }

    private void bestSelected(int position, @NotNull List<ChromosomeSelection> team,
                              @NotNull Population switchOverPop)
            throws CloneNotSupportedException {
        int best1stPart = 0;
        int best2ndPart = 0;
        int nextBest1stPart = 0;
        int nextBest2ndPart = 0;
        for (int i = 1; i < eval_Values.length; i++) {
            if (eval_Values[i][0] > eval_Values[best1stPart][0]) {
                nextBest1stPart = best1stPart;
                best1stPart = i;
            } else if (eval_Values[i][0] == eval_Values[best1stPart][0] || eval_Values[i][0] >= eval_Values[nextBest1stPart][0]) {
                nextBest1stPart = i;
            }
            if (eval_Values[i][1] > eval_Values[best2ndPart][1]) {
                nextBest2ndPart = best2ndPart;
                best2ndPart = i;
            } else if (eval_Values[i][1] == eval_Values[best2ndPart][1] || eval_Values[i][1] >= eval_Values[nextBest2ndPart][1]) {
                nextBest2ndPart = i;
            }
        }
        switchOverPop.saveChromosomes(position, team.get(best1stPart));

        if (best1stPart == best2ndPart) {
            if (eval_Values[nextBest1stPart][0] > eval_Values[nextBest2ndPart][1]) {
                switchOverPop.saveChromosomes(position + 1, team.get(nextBest1stPart));

            } else if (eval_Values[nextBest1stPart][0] < eval_Values[nextBest2ndPart][1]) {
                switchOverPop.saveChromosomes(position + 1, (ChromosomeSelection) team.get(nextBest2ndPart).clone());


            } else {
                //todo put the overall best in and gamble between the other two or pick the one with the highest sum
                if (new Random().nextBoolean()) {
                    switchOverPop.saveChromosomes(position + 1, team.get(nextBest1stPart));
                } else {
                    switchOverPop.saveChromosomes(position + 1, team.get(nextBest2ndPart));

                }
            }
        } else {
            switchOverPop.saveChromosomes(position + 1, team.get(best2ndPart));
        }
        evaluators.clear();
        if (eval_Values[best1stPart][0] > switchOverPop.fittest && eval_Values[best1stPart][0] >= eval_Values[best2ndPart][1]) {
            switchOverPop.fittest = eval_Values[best1stPart][0];
            if (eval_Values[best2ndPart][1] > switchOverPop.fittest) {
                switchOverPop.maxFitOfSecondFittest = position + 1;
            } else {
                switchOverPop.maxFitOfSecondFittest = switchOverPop.maxFit;
            }
            switchOverPop.maxFit = position;
        } else if (eval_Values[best2ndPart][1] > eval_Values[best1stPart][0]) {
            if (eval_Values[best2ndPart][1] > switchOverPop.fittest) {

                switchOverPop.fittest = eval_Values[best2ndPart][1];
                if (eval_Values[best1stPart][0] > switchOverPop.fittest) {
                    switchOverPop.maxFitOfSecondFittest = position + 1;
                } else {
                    switchOverPop.maxFitOfSecondFittest = switchOverPop.maxFit;
                }
                switchOverPop.maxFit = position + 1;
            } else if (eval_Values[best1stPart][0] > switchOverPop.getChromosome(switchOverPop.maxFitOfSecondFittest).fitness
                    || eval_Values[best1stPart][0] > switchOverPop.getChromosome(switchOverPop.maxFitOfSecondFittest).secondFitness) {
                switchOverPop.maxFitOfSecondFittest = position;
            }

        }
        evaluators.add((ChromosomeSelection) team.get(best1stPart).clone());
        evaluators.add((ChromosomeSelection) switchOverPop.getChromosome(position + 1).clone());
        if (eval_Values[best1stPart][0] == geneLength * 2 || eval_Values[best2ndPart][1] == geneLength * 2) {
            //todo
            foundFittest=true;

            //System.out.println("Here");
        }
    }
}