/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private Population population = new Population();
    @Nullable
    private Population population2 = new Population();
    @Nullable
    private Population switchOverPopulation;
    @Nullable
    private Population switchOverPopulation2;
    @NotNull
    private Computations computations = new Computations();

    @Nullable
    private ChromosomeSelection firstinPopulation1Picked;
    @Nullable
    private ChromosomeSelection secondinPopulation1Picked;
    @Nullable
    private ChromosomeSelection firstOffSpringProducedInPopulation1;
    @Nullable
    private ChromosomeSelection secondOffSpringProducedInPopulation1;
    @Nullable
    private ChromosomeSelection thirdOffSpringProducedInPopulation1;
    @Nullable
    private ChromosomeSelection fourthOffSpringProducedInPopulation1;

    @Nullable
    private ChromosomeSelection firstinPopulation2Picked;
    @Nullable
    private ChromosomeSelection secondinPopulation2Picked;
    @Nullable
    private ChromosomeSelection firstOffSpringProducedInPopulation2;
    @Nullable
    private ChromosomeSelection secondOffSpringProducedInPopulation2;
    @Nullable
    private ChromosomeSelection thirdOffSpringProducedInPopulation2;
    @Nullable
    private ChromosomeSelection fourthOffSpringProducedInPopulation2;

    private int generationCount = 1;
    private boolean universalEval = false;
    private boolean rastrigan = false;
    private double currentHighestlevelOfFitness = -1;
    private int noOfmutations = 0;
    private int noOfComputatons = 0;
    private int noOfCrossover = 0;
    int evaluatorSize = 4;
    private boolean foundFittest = false;
    private boolean foundFittestinPop1 = false;
    //this controls if what we are computing contains integer or binary values
    private int bound = 2;

    //test variables
    int stagnantValue = 0;
    @Nullable //private int[] noOfReoccurences = {20, 50, 100, 150, 200, 300, 400};
    private int[] noOfReoccurences = {50, 100, 150, 200, 250, 300, 350, 400};
    private int noOfReoccurence = 5000;

    @NotNull
    private int[] popSizeArray = {/*2, 4, 6,*/ 10, 20, 50, 100, 150, 200, 250, 300, 350, 400};
    //end of test variables

    //this dictates the length of each individuals/chromosomes
    private int geneLength = 16;
    @NotNull
    private double[][] eval_Values = new double[evaluatorSize][2];
    @NotNull
    private List<ChromosomeSelection> paretoFrontTeam = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> population1EvalTeam = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> archivePopulation = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> tempArchivePopulation = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> evaluators = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> population2EvalTeam = new ArrayList<>();
    private int noOfEvaluation = 0;

    private int point1;
    private int point2;
    //to evaluate point of plateau
    @NotNull
    private String fittestValue = "";
    @NotNull
    private String fittestPartner = "";
    private double previousFitness = -1;

    public static void main(String[] args) throws CloneNotSupportedException {
        ArrayList<String> result = new ArrayList<>();

        GeneticAlgorithm ga = new GeneticAlgorithm();
//Get the file reference
        Path path = Paths.get("C:\\Users\\GO\\IdeaProjects\\Coev\\output.txt");
        //   for (int reOccur : ga.noOfReoccurences) {
        //ga.noOfReoccurence = reOccur;
        for (int popsiz : ga.popSizeArray) {

//Use try-with-resource to get auto-closeable writer instance
            for (int i = 0; i < 250; i++) {
                String fittestChromosome = "";
                result.add("\n" + popsiz + "\t ");
                ga.resetter();
                //Initialize population
                ga.population.initializePopulation(ga.bound, ga.geneLength, ga.rastrigan, popsiz);
                ga.population2.initializePopulation(ga.bound, ga.geneLength, ga.rastrigan, popsiz);
                String bestPartner = "";
                //While population searches for a chromosome with maximum fitness
                ga.stagnantValue = 0;
                ga.firstEvaluation();
                ga.switchOverPopulation = (Population) ga.population.clone(ga.switchOverPopulation);
                ga.switchOverPopulation2 = (Population) ga.population2.clone(ga.switchOverPopulation2);
                while ((
                        //ga.generationCount < reOccur &&
                        ga.noOfEvaluation < 51200 &&
                                ga.currentHighestlevelOfFitness < 150.0
                                && !ga.rastrigan
                        // && ga.stagnantValue < ga.noOfReoccurence
                )
                    //   || (ga.currentHighestlevelOfFitness < (ga.geneLength * 2) && !ga.rastrigan))
                ) {
                    ++ga.generationCount;
                    int beginfrom = ga.naturalSelection(new Random().nextBoolean());
                    //Do the things involved in evolution
                    for (; beginfrom < popsiz; beginfrom += 2) {
                        ga.tournamentSelection(popsiz, ga.rastrigan);
                        if (ga.foundFittest || ga.stagnantValue >= ga.noOfReoccurence) {
                            break;
                        }
                        ga.process(beginfrom);
                    }
                    // moving the new generation into the old generation space
                    ga.population = (Population) ga.switchOverPopulation.clone(ga.population);
                    ga.population2 = (Population) ga.switchOverPopulation2.clone(ga.population2);
                    ga.archivePopulation = ga.tempArchivePopulation;
                    ga.tempArchivePopulation.clear();
                    //Calculate new fitness value
                    //todo the best value all through
                    Population tempPop = null;
                    if (ga.foundFittestinPop1) {
                        tempPop = (Population) ga.population.clone(tempPop);
                    } else {
                        tempPop = (Population) ga.population2.clone(tempPop);
                    }
                    fittestChromosome = tempPop.getChromosome(tempPop.maxFit).getStringChromosome();
                    ga.currentHighestlevelOfFitness = tempPop.fittest;
                    System.out.println("The maxfit is" + tempPop.maxFit);
                    if (tempPop.getChromosome(tempPop.maxFit).fitness ==
                            tempPop.fittest) {
                        bestPartner = tempPop.getChromosome(tempPop.maxFit).partnerChromosome;

                    } else {
                        bestPartner = tempPop.getChromosome(tempPop.maxFit).partner2Chromosome;

                    }
                    if ((ga.fittestValue.equalsIgnoreCase(fittestChromosome)
                            && ga.fittestPartner.equalsIgnoreCase(bestPartner))
                            || (ga.fittestValue.equalsIgnoreCase(bestPartner)
                            && ga.fittestPartner.equalsIgnoreCase(fittestChromosome))
                            || (ga.previousFitness == ga.currentHighestlevelOfFitness)) {
                        ga.stagnantValue++;
                    } else {
                        ga.stagnantValue = 0;
                    }
                    System.out.println("Generation: " + ga.generationCount + " Fittest: " + ga.currentHighestlevelOfFitness);
                    System.out.println("The best pair are: " + fittestChromosome +
                            " and\n " + bestPartner);
                    ga.fittestValue = fittestChromosome;
                    ga.fittestPartner = bestPartner;
                    ga.previousFitness = ga.currentHighestlevelOfFitness;

                }
                //when a solution is found or 100 generations have been produced
                System.out.println("\nno of evaluations " + ga.noOfEvaluation);
                System.out.println("\nSolution found in generation " + ga.generationCount);
                System.out.println("Fitness: " + ga.currentHighestlevelOfFitness);
                System.out.println("The best pair are: " + fittestChromosome +
                        " and \n" + bestPartner);
                System.out.println("The best pair are actually: " + Integer.parseInt(fittestChromosome, 2) / Math.pow(2, ChromosomeSelection.geneLength) +
                        " and \n" + Integer.parseInt(bestPartner, 2) / Math.pow(2, ChromosomeSelection.geneLength));
                System.out.println("probability of mutation is " + (double) ga.noOfmutations / ga.noOfComputatons);
                System.out.println("probability of cross over is " + (double) ga.noOfCrossover / ga.noOfComputatons);
                result.add(ga.noOfEvaluation + "\t ");
                result.add(ga.stagnantValue + "\t ");
                result.add(ga.generationCount + "\t ");
                result.add(String.valueOf(Math.floor(ga.currentHighestlevelOfFitness * 100000 + .5) / 100000));
            }
        }
        //  }
//Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(String.valueOf(result));
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void populationEvaluationStarters(ChromosomeSelection chromSel1, ChromosomeSelection chromSel2,
                                              ChromosomeSelection chromSel3, ChromosomeSelection chromSel4) {
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
        if (rn.nextInt() % 23 >= 21) {
            ++noOfmutations;
            mutation();
        }
        evaluators.clear();
        evaluators.add((ChromosomeSelection) firstinPopulation2Picked.clone());
        evaluators.add((ChromosomeSelection) secondinPopulation2Picked.clone());
        if (generationCount > 1) {
            grandParentEvaluators(firstinPopulation2Picked, secondinPopulation2Picked);
            eval_Values = new double[evaluatorSize][evaluatorSize];
            // todo get a more effective method for this
            //  eval_Values = new double[6][evaluators.size()];
        }
        if (foundFittest || stagnantValue >= noOfReoccurence) {
            return;
        }
        evaluation(position);
    }

    private void grandParentEvaluators(@NotNull ChromosomeSelection parent1, ChromosomeSelection parent2) {
        if (parent1.fitness > parent1.secondFitness) {
            evaluators.add(new ChromosomeSelection(parent1.partner2Chromosome));
        } else {
            evaluators.add(new ChromosomeSelection(parent1.partnerChromosome));
        }
        if (parent2 != null) {
            if (parent2.fitness > parent2.secondFitness) {
                evaluators.add(new ChromosomeSelection(parent2.partner2Chromosome));
            } else {
                evaluators.add(new ChromosomeSelection(parent2.partnerChromosome));
            }
        }

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
        foundFittestinPop1 = false;
        foundFittest = false;
        firstinPopulation2Picked = null;
        secondinPopulation2Picked = null;
        firstOffSpringProducedInPopulation2 = null;
        secondOffSpringProducedInPopulation2 = null;
        thirdOffSpringProducedInPopulation2 = null;
        fourthOffSpringProducedInPopulation2 = null;
        eval_Values = new double[evaluatorSize][2];
        paretoFrontTeam.clear();
        archivePopulation.clear();
        tempArchivePopulation.clear();
        population1EvalTeam = new ArrayList<>();
        evaluators = new ArrayList<>();
        population2EvalTeam = new ArrayList<>();
        noOfEvaluation = 0;
        generationCount = 1;
        noOfmutations = 0;
        noOfComputatons = 0;
        stagnantValue = 0;
        noOfCrossover = 0;
        currentHighestlevelOfFitness = -1;
    }

    //Selection
    private int naturalSelection(boolean elitism) throws CloneNotSupportedException {
        if (generationCount > 2) {
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
        if (generationCount > 1) {
            if (popSize >= 10) {
                eval_Values = new double[10][2];
            } else {
                eval_Values = new double[popSize][2];
            }
            // List<ChromosomeSelection> populationTournamentTeam = new ArrayList<>();
            population1EvalTeam.clear();
            population2EvalTeam.clear();
            String pos = "";
            if (universalEval) {
                pos = universalValueTournamentSelection(popSize, population1EvalTeam, switchOverPopulation);
            } else {
                pos = globalMultiObjectiveTournamentSelection(popSize, population1EvalTeam, switchOverPopulation);
            }
            firstinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[0])).clone();
            secondinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[1])).clone();
            if (universalEval) {
                pos = universalValueTournamentSelection(popSize, population2EvalTeam, switchOverPopulation2);
            } else {
                pos = globalMultiObjectiveTournamentSelection(popSize, population2EvalTeam, switchOverPopulation2);
            }
            firstinPopulation2Picked = (ChromosomeSelection) population2EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[0])).clone();
            secondinPopulation2Picked = (ChromosomeSelection) population2EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[1])).clone();
            if (generationCount > 1) {
                eval_Values = new double[evaluatorSize][4];
            } else {
                eval_Values = new double[evaluatorSize][2];
            }
            population1EvalTeam.clear();
            population2EvalTeam.clear();
        } else {
            firstinPopulation1Picked = (ChromosomeSelection) switchOverPopulation.randomlyPicked(popSize).clone();
            secondinPopulation1Picked = (ChromosomeSelection) switchOverPopulation.randomlyPicked(popSize).clone();
            firstinPopulation2Picked = (ChromosomeSelection) switchOverPopulation2.randomlyPicked(popSize).clone();
            secondinPopulation2Picked = (ChromosomeSelection) switchOverPopulation2.randomlyPicked(popSize).clone();
        }
    }

    private String universalValueTournamentSelection(int popSize, @NotNull List<ChromosomeSelection> population1EvalTeam,
                                                     @NotNull Population switchOverPopulation) throws CloneNotSupportedException {
        for (int i = 0; i < eval_Values.length; i++) {
            if (archivePopulation.isEmpty()) {
                population1EvalTeam.add((ChromosomeSelection) switchOverPopulation.randomlyPicked(popSize).clone());
            } else {
                population1EvalTeam.add((ChromosomeSelection) switchOverPopulation.randomlyPicked(popSize, archivePopulation.size(), archivePopulation).clone());
            }
            eval_Values[i][0] = population1EvalTeam.get(i).fitness + population1EvalTeam.get(i).secondFitness;
        }
        int max = 0;
        int secondMax = 0;
        for (int i = 1; i < eval_Values.length; i++) {
            if (eval_Values[i][0] >= eval_Values[max][0]) {
                //if (eval_Values[i][0] <= eval_Values[max][0]) {
                secondMax = max;
                max = i;
            }
        }
        for (int i = 1; i < eval_Values.length; i++) {
            if (eval_Values[i][0] >= eval_Values[secondMax][0]) {
                //if (eval_Values[i][0] <= eval_Values[secondMax][0]) {

                if (max != i) {
                    secondMax = i;
                }
            }
        }
        String theTwoPos = max + " ";
        theTwoPos += String.valueOf(secondMax);
        return theTwoPos;
    }

    //multi-objective global eval
    @NotNull
    private String globalMultiObjectiveTournamentSelection(int popSize, @NotNull List<ChromosomeSelection> population1EvalTeam,
                                                           @NotNull Population switchOverPopulation) throws CloneNotSupportedException {
        for (int i = 0; i < 10; i++) {
            if (archivePopulation.isEmpty()) {
                population1EvalTeam.add((ChromosomeSelection) switchOverPopulation.randomlyPicked(popSize).clone());
            } else {
                population1EvalTeam.add((ChromosomeSelection) switchOverPopulation.randomlyPicked(popSize, archivePopulation.size(), archivePopulation).clone());
            }
            eval_Values[i][0] = population1EvalTeam.get(i).fitness;
            eval_Values[i][1] = population1EvalTeam.get(i).secondFitness;
        }
        int max = 0;
        int secondMax = 0;
        int max2 = 0;
        int secondMax2 = 0;
        for (int i = 1; i < eval_Values.length; i++) {
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


    private void pointSelector() {
        //Select a random crossover/mutation point
        Random rn = new Random();
        point1 = rn.nextInt(ChromosomeSelection.geneLength);
        point2 = rn.nextInt(ChromosomeSelection.geneLength);
        if (point1 > point2) {
            int temp = point2;
            point2 = point1;
            point1 = temp;
        }
    }

    //Two point crossover
    private void twoPointCrossover() throws CloneNotSupportedException {
        thirdOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
        fourthOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
        thirdOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
        fourthOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
        //Swap values among parents
        pointSelector();
        crossOver(point1, point2, thirdOffSpringProducedInPopulation1, fourthOffSpringProducedInPopulation1);
        //Swap values among parents
        pointSelector();
        crossOver(point1, point2, thirdOffSpringProducedInPopulation2, fourthOffSpringProducedInPopulation2);
        populationEvaluationStarters(thirdOffSpringProducedInPopulation1,
                fourthOffSpringProducedInPopulation1,
                thirdOffSpringProducedInPopulation2,
                fourthOffSpringProducedInPopulation2);

    }

    private void crossOver(int crossOverPoint1, int crossOverPoint2,
                           @NotNull ChromosomeSelection offSpringProduced1,
                           @NotNull ChromosomeSelection offSpringProduced2) {
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
        for (int i = 0; i < ChromosomeSelection.geneLength; i += 2) {
            int temp = firstOffSpringProducedInPopulation1.genes[i];
            firstOffSpringProducedInPopulation1.genes[i] = secondOffSpringProducedInPopulation1.genes[i];
            secondOffSpringProducedInPopulation1.genes[i] = temp;
            temp = firstOffSpringProducedInPopulation2.genes[i];
            firstOffSpringProducedInPopulation2.genes[i] = secondOffSpringProducedInPopulation2.genes[i];
            secondOffSpringProducedInPopulation2.genes[i] = temp;

        }
        populationEvaluationStarters(firstOffSpringProducedInPopulation1,
                secondOffSpringProducedInPopulation1,
                firstOffSpringProducedInPopulation2,
                secondOffSpringProducedInPopulation2);

    }

    /**
     * picking a random gene and swapping it with its allelle
     * using inversion mutation
     */
    private void mutation() {
        Random rn = new Random();
        //Flip values at the mutation point
        mutate(firstOffSpringProducedInPopulation1, secondOffSpringProducedInPopulation1);
        mutate(
                firstOffSpringProducedInPopulation2, secondOffSpringProducedInPopulation2);
        if (rn.nextInt(4) > 1) {

            //Flip values at the mutation point
            mutate(thirdOffSpringProducedInPopulation1, fourthOffSpringProducedInPopulation1);
            mutate(thirdOffSpringProducedInPopulation2, fourthOffSpringProducedInPopulation2);

        }
    }

    private void mutate(ChromosomeSelection firstOffSpringProducedInPopulation1,
                        ChromosomeSelection secondOffSpringProducedInPopulation1) {
        //Select a random mutation point
        pointSelector();
        try {
            for (int i = point1; i <= point2; i++) {
                firstOffSpringProducedInPopulation1.genes[i]
                        = computations.getRandomAllele(firstOffSpringProducedInPopulation1.genes[i], bound);
            }

            pointSelector();
            for (int i = point1; i <= point2; i++) {
                secondOffSpringProducedInPopulation1.genes[i]
                        = computations.getRandomAllele(secondOffSpringProducedInPopulation1.genes[i], bound);
            }
        } catch (NullPointerException e) {

        }
    }

    private void firstEvaluation() throws CloneNotSupportedException {
        for (int i = 0; i < population.POPSIZE; i += 2) {
            noOfEvaluation += 4;
            population2.chromosomes[i].fitness = population.chromosomes[i].calcPairedFitness(
                    population2.chromosomes[i].getStringChromosome(), 0);
            population2.chromosomes[i].partnerChromosome = population.chromosomes[i].getStringChromosome();

            population2.chromosomes[i].secondFitness = population.chromosomes[i + 1].calcPairedFitness(
                    population2.chromosomes[i].getStringChromosome(), 1);
            population2.chromosomes[i].partner2Chromosome = population.chromosomes[i + 1].getStringChromosome();

            population2.chromosomes[i + 1].secondFitness = population.chromosomes[i].calcPairedFitness(
                    population2.chromosomes[i + 1].getStringChromosome(), 1);
            population2.chromosomes[i + 1].partner2Chromosome = population.chromosomes[i].getStringChromosome();

            population2.chromosomes[i + 1].fitness = population.chromosomes[i + 1].calcPairedFitness(
                    population2.chromosomes[i + 1].getStringChromosome(), 0);
            population2.chromosomes[i + 1].partnerChromosome = population.chromosomes[i + 1].getStringChromosome();
        }
    }

    private void evaluation(int position) throws CloneNotSupportedException {
        baseEvaluation(position, population1EvalTeam, switchOverPopulation);
        // eval_Values = new double[6][evaluators.size()];
        if (foundFittest || stagnantValue >= noOfReoccurence) {
            foundFittestinPop1 = true;
            return;
        }
        if (generationCount > 1) {
//            evaluators.clear();
//            evaluators.add(population1EvalTeam.get(0));
//            evaluators.add(population1EvalTeam.get(1));
            if (evaluators.size() > 1) {
                grandParentEvaluators(evaluators.get(0), evaluators.get(1));
            } else {
                grandParentEvaluators(evaluators.get(0), null);
            }
            eval_Values = new double[evaluatorSize][evaluatorSize];
            // todo get a more effective method for this
            //  eval_Values = new double[6][evaluators.size()];
        }
        baseEvaluation(position, population2EvalTeam, switchOverPopulation2);
        evaluators.clear();
        eval_Values = new double[evaluatorSize][2];
        if (foundFittest || stagnantValue >= noOfReoccurence) {
            foundFittestinPop1 = false;
            return;
        }
    }

    private void baseEvaluation(int position, @NotNull List<ChromosomeSelection> populationEvalTeam,
                                @NotNull Population switchOverPopulation) throws CloneNotSupportedException {
        for (int i = 0; i < evaluators.size(); i++) {
            for (int j = 0; j < eval_Values.length; j++) {
                ++noOfEvaluation;
                eval_Values[j][i] = populationEvalTeam.get(j).calcPairedFitness(
                        evaluators.get(i).getStringChromosome(), i);
            }
        }
        if (evaluators.size() < eval_Values[0].length) {
            for (int i = evaluators.size(); i < eval_Values[0].length; i++) {
                for (int j = 0; j < eval_Values.length; j++) {
                    //todo always set it to the most impossible i.e if it is a global max, set it very low and vicecersa for global min
                    eval_Values[j][i] = -100000;
                }
            }
        }
        paretoFront(position, populationEvalTeam, switchOverPopulation);
        // bestSelected(position, populationEvalTeam, switchOverPopulation);

    }

    /**
     * @param evalTeam pick pareto fronts; i.e incomparable values
     */
    void paretoFront(int position, @NotNull List<ChromosomeSelection> evalTeam,
                     @NotNull Population switchOverPop) throws CloneNotSupportedException {
        paretoFrontTeam.add(evalTeam.get(0));
        for (int i = 1; i < evaluatorSize; i++) {
            int sizeOfParetoFrontTeam = paretoFrontTeam.size();
            boolean dominatedBefore = false;
            boolean nonDominatedBefore = false;
            for (int j = 0; j < sizeOfParetoFrontTeam; j++) {
                int positionInEvalTeam = evalTeam.indexOf(paretoFrontTeam.get(j));
                if (generationCount > 1) {
                    if (eval_Values[i].length == evaluatorSize) {
                        if (eval_Values[i][0] >= eval_Values[positionInEvalTeam][0] &&
                                eval_Values[i][1] >= eval_Values[positionInEvalTeam][1] &&
                                eval_Values[i][2] >= eval_Values[positionInEvalTeam][2] &&
                                eval_Values[i][3] >= eval_Values[positionInEvalTeam][3]
//                                && eval_Values[i][4] >= eval_Values[positionInEvalTeam][4]
//                                && eval_Values[i][5] >= eval_Values[positionInEvalTeam][5]
                        ) {
                            if (!dominatedBefore) {
                                paretoFrontTeam.set(j, evalTeam.get(i));
                                dominatedBefore = true;
                            }
                        } else if (eval_Values[i][0] < eval_Values[positionInEvalTeam][0] &&
                                eval_Values[i][1] < eval_Values[positionInEvalTeam][1] &&
                                eval_Values[i][2] < eval_Values[positionInEvalTeam][2] &&
                                eval_Values[i][3] < eval_Values[positionInEvalTeam][3]
//                                && eval_Values[i][4] < eval_Values[positionInEvalTeam][4]
//                                && eval_Values[i][5] < eval_Values[positionInEvalTeam][5]
                        ) {
                            break;
                        } else {
                            if (!nonDominatedBefore) {
                                paretoFrontTeam.add(evalTeam.get(i));
                                nonDominatedBefore = true;
                            }
                        }
                    } else {
                        if (eval_Values[i][0] >= eval_Values[positionInEvalTeam][0] &&
                                eval_Values[i][1] >= eval_Values[positionInEvalTeam][1] &&
                                eval_Values[i][2] >= eval_Values[positionInEvalTeam][2]) {
                            if (!dominatedBefore) {
                                paretoFrontTeam.set(j, evalTeam.get(i));
                                dominatedBefore = true;
                            }
                        } else if (eval_Values[i][0] < eval_Values[positionInEvalTeam][0] &&
                                eval_Values[i][1] < eval_Values[positionInEvalTeam][1] &&
                                eval_Values[i][2] < eval_Values[positionInEvalTeam][2]) {
                            break;
                        } else {
                            if (!nonDominatedBefore) {
                                paretoFrontTeam.add(evalTeam.get(i));
                                nonDominatedBefore = true;
                            }
                        }
                    }
                } else {
                    if (eval_Values[i][0] >= eval_Values[positionInEvalTeam][0] &&
                            eval_Values[i][1] >= eval_Values[positionInEvalTeam][1]) {
                        if (!dominatedBefore) {
                            paretoFrontTeam.set(j, evalTeam.get(i));
                            dominatedBefore = true;
                        }
                    } else if (eval_Values[i][0] < eval_Values[positionInEvalTeam][0] &&
                            eval_Values[i][1] < eval_Values[positionInEvalTeam][1]) {
                        break;
                    } else {
                        if (!nonDominatedBefore) {
                            paretoFrontTeam.add(evalTeam.get(i));
                            nonDominatedBefore = true;
                        }
                    }
                }
            }
        }
        paretoFrontBestFeetOut(position, evalTeam, switchOverPop);
    }

    /**
     * ensures that the two fitness the pareto fronts hold are the best and also launches the evaluator selectors --strongest individual or highest sum
     */
    void paretoFrontBestFeetOut(int position, @NotNull List<ChromosomeSelection> evalTeam,
                                @NotNull Population switchOverPop) throws CloneNotSupportedException {
        //set the two fitness in the pareto fronts to the best
        for (ChromosomeSelection paretofront : paretoFrontTeam) {
            int positionInEvalTeam = evalTeam.indexOf(paretofront);
            int max = 0;
            int secondMax = 0;
            for (int i = 1; i < evaluators.size(); i++) {
                if (eval_Values[positionInEvalTeam][i] > eval_Values[positionInEvalTeam][max]) {
                    max = i;
                }
            }
            for (int i = 1; i < evaluators.size(); i++) {
                if (eval_Values[positionInEvalTeam][i] > eval_Values[positionInEvalTeam][secondMax] && i != max) {
                    secondMax = i;
                }
            }
            //if the fitness and secondfitness is not max and secondmax
            if (!((max == 0 && secondMax == 1) || (max == 1 && secondMax == 0))) {
                // if max is the fitness but the secondfitness is a random value, replace the secondfitness with secondmax
                if (max == 0) {
                    paretofront.secondFitness = eval_Values[positionInEvalTeam][secondMax];
                    paretofront.partner2Chromosome = evaluators.get(secondMax).getStringChromosome();
                } // if max is the secondfitness but the fitness is a random value, replace the fitness with secondmax
                else if (max == 1) {
                    paretofront.fitness = eval_Values[positionInEvalTeam][secondMax];
                    paretofront.partnerChromosome = evaluators.get(secondMax).getStringChromosome();
                } // if secondMax is the fitness but the secondfitness is a random value, replace the secondfitness with max
                else if (secondMax == 0) {
                    paretofront.secondFitness = eval_Values[positionInEvalTeam][max];
                    paretofront.partner2Chromosome = evaluators.get(max).getStringChromosome();
                }// if secondMax is the secondfitness but the fitness is a random value, replace the fitness with max
                else if (secondMax == 1) {
                    paretofront.fitness = eval_Values[positionInEvalTeam][max];
                    paretofront.partnerChromosome = evaluators.get(max).getStringChromosome();
                } // if the fitness and secondfitness are random values, replace both
                else {
                    paretofront.secondFitness = eval_Values[positionInEvalTeam][secondMax];
                    paretofront.partner2Chromosome = evaluators.get(secondMax).getStringChromosome();
                    paretofront.fitness = eval_Values[positionInEvalTeam][max];
                    paretofront.partnerChromosome = evaluators.get(max).getStringChromosome();
                }
            }

        }
        if (new Random().nextInt() % 23 >= 1) {
            pickEvaluatorsFromParetoFrontBasedOnStrongestIndividual(position, switchOverPop);
        } else {
            pickEvaluatorsFromParetoFrontBasedOnHighestSum(position, switchOverPop);
        }
    }

    private double maxFitness = Integer.MIN_VALUE;
    private double secondMaxFitness = Integer.MIN_VALUE;
    private int positionOfMax = Integer.MIN_VALUE;
    private int positionOfSecondMax = Integer.MIN_VALUE;

    /**
     * @param position
     * @param switchOverPop
     * @throws CloneNotSupportedException
     */
    private void pickEvaluatorsFromParetoFrontBasedOnStrongestIndividual(int position, @NotNull Population switchOverPop) throws CloneNotSupportedException {
        maxFitness = Integer.MIN_VALUE;
        secondMaxFitness = Integer.MIN_VALUE;
        positionOfMax = Integer.MIN_VALUE;
        positionOfSecondMax = Integer.MIN_VALUE;
        for (int i = 0; i < paretoFrontTeam.size(); i++) {
            if (paretoFrontTeam.get(i).fitness > maxFitness) {
                maxFitness = paretoFrontTeam.get(i).fitness;
                positionOfMax = i;
            }
            if (paretoFrontTeam.get(i).secondFitness > maxFitness) {
                maxFitness = paretoFrontTeam.get(i).secondFitness;
                positionOfMax = i;
            }
        }
        if (paretoFrontTeam.size() > 1) {
            for (int i = 0; i < paretoFrontTeam.size(); i++) {
                if ((paretoFrontTeam.get(i).fitness > secondMaxFitness) && (positionOfMax != i)) {
                    secondMaxFitness = paretoFrontTeam.get(i).fitness;
                    positionOfSecondMax = i;
                }
                if ((paretoFrontTeam.get(i).secondFitness > secondMaxFitness) && (positionOfMax != i)) {
                    secondMaxFitness = paretoFrontTeam.get(i).secondFitness;
                    positionOfSecondMax = i;
                }
            }
        }
        theSwapOrTransferIntoTheInterimPop(position, switchOverPop);
    }

    /**
     * @param position
     * @param switchOverPop
     * @throws CloneNotSupportedException
     */
    private void pickEvaluatorsFromParetoFrontBasedOnHighestSum(int position, @NotNull Population switchOverPop) throws CloneNotSupportedException {
        maxFitness = Integer.MIN_VALUE;
        secondMaxFitness = Integer.MIN_VALUE;
        positionOfMax = Integer.MIN_VALUE;
        positionOfSecondMax = Integer.MIN_VALUE;
        double[] sumOfFitness = new double[paretoFrontTeam.size()];
        for (int i = 0; i < paretoFrontTeam.size(); i++) {
            sumOfFitness[i] = paretoFrontTeam.get(i).fitness + paretoFrontTeam.get(i).secondFitness;
            if (sumOfFitness[i] > maxFitness) {
                if (paretoFrontTeam.get(i).fitness < paretoFrontTeam.get(i).secondFitness) {
                    maxFitness = paretoFrontTeam.get(i).secondFitness;
                } else {
                    maxFitness = paretoFrontTeam.get(i).fitness;
                }
                positionOfMax = i;
            }
        }
        if (paretoFrontTeam.size() > 1) {
            for (int i = 0; i < sumOfFitness.length; i++) {
                if (sumOfFitness[i] > secondMaxFitness && positionOfMax != i) {
                    if (paretoFrontTeam.get(i).fitness < paretoFrontTeam.get(i).secondFitness) {
                        secondMaxFitness = paretoFrontTeam.get(i).secondFitness;
                    } else {
                        secondMaxFitness = paretoFrontTeam.get(i).fitness;
                    }
                    positionOfSecondMax = i;
                }
            }
        }
        theSwapOrTransferIntoTheInterimPop(position, switchOverPop);
    }

    /**
     * @param position
     * @param switchOverPop
     * @throws CloneNotSupportedException
     */
    private void theSwapOrTransferIntoTheInterimPop(int position, @NotNull Population switchOverPop) throws CloneNotSupportedException {
        if (secondMaxFitness >= switchOverPop.fittest) {
            switchOverPop.maxFitOfSecondFittest = position + 1;
        }
        if (maxFitness > switchOverPop.fittest) {
            switchOverPop.fittest = maxFitness;
            switchOverPop.maxFit = position;
            //todo crosscheck
            if (secondMaxFitness < switchOverPop.fittest) {
                switchOverPop.maxFitOfSecondFittest = 0;
            }
        }
        evaluators.clear();
        //empty the evaluators to store the new ones
        evaluators.add((ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
        if (positionOfSecondMax >= 0) {
            evaluators.add((ChromosomeSelection) paretoFrontTeam.get(positionOfSecondMax).clone());
        }

        // put the first two in the main population while the rest go into the archive
        switchOverPop.saveChromosomes(position, (ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
        if (positionOfSecondMax >= 0) {
            switchOverPop.saveChromosomes(position + 1, (ChromosomeSelection) paretoFrontTeam.get(positionOfSecondMax).clone());
        } else {
            if (!tempArchivePopulation.isEmpty()) {
                switchOverPop.saveChromosomes(position + 1, tempArchivePopulation.get(0));
                tempArchivePopulation.remove(0);
            } else {
                switchOverPop.saveChromosomes(position + 1, (ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
            }
        }
        for (int i = 0; i < paretoFrontTeam.size(); i++) {
            if (i != positionOfMax && i != positionOfSecondMax) {
                tempArchivePopulation.add(paretoFrontTeam.get(i));
            }
        }
        paretoFrontTeam.clear();
        if (maxFitness >= 150.0 || secondMaxFitness >= 150.0) {
//        for SMTQ
//        if (maxFitness >= 149.999999 || secondMaxFitness >= 149.999999) {
//            //todo
            foundFittest = true;
        }
    }
}