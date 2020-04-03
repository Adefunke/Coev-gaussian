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
    int evaluatorSize = 7;
    int rangeMin = 0;
    double rangeMax = 1;
    private boolean foundFittest = false;
    private boolean foundFittestinPop1 = false;
    @NotNull
    private ArrayList highestLocator = new ArrayList();
    @NotNull
    private ArrayList gapLocator = new ArrayList();
    @NotNull
    private ArrayList positionLocator = new ArrayList();

    //test variables
    int stagnantValue = 0;
    private int noOfReoccurence = 5000;

    @NotNull
    private int[] popSizeArray = {20, 32, 50, 100, 150, 200, 250, 300, 350, 400};

    @NotNull
    private double[][] eval_Values = new double[evaluatorSize][2];
    @NotNull
    private List<ChromosomeSelection> paretoFrontTeam = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> population1EvalTeam = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> archivePopulation = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> archivePopulation2 = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> tempArchivePopulation = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> tempArchivePopulation2 = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> evaluators = new ArrayList<>();
    @NotNull
    private List<ChromosomeSelection> population2EvalTeam = new ArrayList<>();
    private int noOfEvaluation = 0;
    private static double maxFitnessFromEquation = 2.0;
    private int point1;
    private int point2;
    //to evaluate point of plateau
    @NotNull
    private String fittestValue = "";
    @NotNull
    private String fittestPartner = "";
    private double previousFitness = -1;
    private static int allpath = 0;

    public static void main(String[] args) throws CloneNotSupportedException {
        ArrayList<String> result = new ArrayList<>();
    //Get the file references
        List<Path> allPaths = new ArrayList<>();
        allPaths.add(Paths.get("SMTQGlobal.txt"));
        allPaths.add(Paths.get("MTQGlobal.txt"));
        allPaths.add(Paths.get("SMTQ125Global.txt"));
        allPaths.add(Paths.get("MTQ125Global.txt"));
        allPaths.add(Paths.get("damavandiGlobal.txt"));
        allPaths.add(Paths.get("griewankGlobal.txt"));
        allPaths.add(Paths.get("eggHolderFunctionGlobal.txt"));
        allPaths.add(Paths.get("bohachevskyFunctionGlobal.txt"));
        allPaths.add(Paths.get("boothDomainGlobal.txt"));

        GeneticAlgorithm ga = new GeneticAlgorithm();
        Path path;
        for (; allpath < allPaths.size(); allpath++) {
            path = allPaths.get(allpath);

            if (allpath < 4) {
                maxFitnessFromEquation = 150.0;
            } else if (allpath == 4) {
                ga.rangeMax = 14;
                ga.rangeMin = -14;
                maxFitnessFromEquation = 0.0;
            } else if (allpath == 6) {
                maxFitnessFromEquation = 960.0;
                ga.rangeMax = 512;
                ga.rangeMin = -512;
            } else if (allpath == 7) {
                ga.rangeMax = 100;
                ga.rangeMin = -100;
                maxFitnessFromEquation = 0.0;
            } else {
                ga.rangeMax = 1;
                ga.rangeMin = 0;
                maxFitnessFromEquation = 0.0;
            }
            result.clear();
            for (int popsiz : ga.popSizeArray) {
                for (int i = 0; i < 250; i++) {
                    String fittestChromosome = "";
                    result.add("\n" + popsiz + "\t ");
                    ga.resetter();
                    //Initialize population
                    ga.population.initializePopulation(popsiz, ga.rangeMin, ga.rangeMax);
                    ga.population2.initializePopulation(popsiz, ga.rangeMin, ga.rangeMax);
                    String bestPartner = "";
                    //While population searches for a chromosome with maximum fitness
                    ga.stagnantValue = 0;
                    ga.firstEvaluation();
                    ga.switchOverPopulation = (Population) ga.population.clone(ga.switchOverPopulation);
                    ga.switchOverPopulation2 = (Population) ga.population2.clone(ga.switchOverPopulation2);
                    while ((
                            ga.noOfEvaluation < 51200 &&
                                    ga.currentHighestlevelOfFitness < maxFitnessFromEquation
                                    && !ga.rastrigan
                    ) ) {
                        ++ga.generationCount;
                        if (ga.generationCount > 2) {
                            ga.switchOverPopulation.positionPointer = 2;
                            ga.switchOverPopulation2.positionPointer = 2;
                        }
                        int beginfrom = ga.naturalSelection(new Random().nextBoolean());
                        //Do the things involved in evolution

                        while (beginfrom < popsiz) {
                            ga.tournamentSelection(popsiz, ga.rastrigan);
                            if (ga.foundFittest || ga.stagnantValue >= ga.noOfReoccurence) {
                                break;
                            }
                            ga.process(beginfrom);
                            beginfrom = ga.switchOverPopulation.positionPointer > ga.switchOverPopulation2.positionPointer
                                    ? ga.switchOverPopulation.positionPointer : ga.switchOverPopulation2.positionPointer;
                        }
                        if (ga.switchOverPopulation.positionPointer < popsiz) {
                            ga.filler(ga.switchOverPopulation, ga.population, ga.population2, ga.firstinPopulation2Picked,
                                    ga.secondinPopulation2Picked, ga.archivePopulation2, ga.tempArchivePopulation);
                        } else if (ga.switchOverPopulation2.positionPointer < popsiz) {
                            ga.filler(ga.switchOverPopulation2, ga.population2, ga.population, ga.firstinPopulation1Picked,
                                    ga.secondinPopulation1Picked, ga.archivePopulation, ga.tempArchivePopulation2);
                        }
                        // moving the new generation into the old generation space and swap
                        ga.population = (Population) ga.switchOverPopulation2.clone(ga.population);
                        ga.population2 = (Population) ga.switchOverPopulation.clone(ga.population2);
                        ga.archivePopulation = ga.tempArchivePopulation2;
                        ga.tempArchivePopulation.clear();
                        ga.archivePopulation2 = ga.tempArchivePopulation;
                        ga.tempArchivePopulation2.clear();
                        ga.switchOverPopulation = (Population) ga.population.clone(ga.switchOverPopulation);
                        ga.switchOverPopulation2 = (Population) ga.population2.clone(ga.switchOverPopulation2);

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
                        if (tempPop.getChromosome(tempPop.maxFit).fitness == tempPop.fittest) {
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
                    System.out.println("probability of mutation is " + (double) ga.noOfmutations / ga.noOfComputatons);
                    System.out.println("probability of cross over is " + (double) ga.noOfCrossover / ga.noOfComputatons);
                    result.add(ga.noOfEvaluation + "\t ");
                    result.add(ga.stagnantValue + "\t ");
                    result.add(ga.generationCount + "\t ");
                    result.add(String.valueOf(ga.currentHighestlevelOfFitness) + "\t ");
                    result.add(String.valueOf(Math.floor(ga.currentHighestlevelOfFitness * 100000 + .5) / 100000) + "\t ");

                    result.add(fittestChromosome + "\t " +
                            " and " + bestPartner + "\t ");

                }
            }
            //Use try-with-resource to get auto-closeable writer instance
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(String.valueOf(result));
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
    }

    private void populationEvaluationStarters(ChromosomeSelection chromSel1, ChromosomeSelection chromSel2,
                                              ChromosomeSelection chromSel3, ChromosomeSelection chromSel4) {
        population1EvalTeam.add(chromSel1);
        population1EvalTeam.add(chromSel2);
        population2EvalTeam.add(chromSel3);
        population2EvalTeam.add(chromSel4);
    }

    private void processFiller() throws CloneNotSupportedException {
        Random rn = new Random();
        firstinPopulation2Picked = null;
        secondinPopulation2Picked = null;
        populationEvaluationStarters(
                firstinPopulation1Picked,
                secondinPopulation1Picked,
                null, null
        );
        ++noOfComputatons;
        ++noOfmutations;
        gaussianMutation();

    }

    private void process(int position) throws CloneNotSupportedException {
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
        if (rn.nextInt() % 10 < 9) {
            ++noOfmutations;
            gaussianMutation();
        }
        evaluators.clear();
        evaluators.add((ChromosomeSelection) firstinPopulation2Picked.clone());
        evaluators.add((ChromosomeSelection) secondinPopulation2Picked.clone());
        if (generationCount > 1) {
            grandParentEvaluators(firstinPopulation1Picked, secondinPopulation1Picked);
            evaluators.add(new ChromosomeSelection(population2.getChromosome(population2.maxFit).getStringChromosome()));
            eval_Values = new double[population1EvalTeam.size()][evaluatorSize];
        }
        if (foundFittest || stagnantValue >= noOfReoccurence) {
            return;
        }
        evaluation(position);
    }

    private void grandParentEvaluators(@NotNull ChromosomeSelection parent1, ChromosomeSelection parent2) {
        evaluators.add(new ChromosomeSelection(parent1.partner2Chromosome));
        evaluators.add(new ChromosomeSelection(parent1.partnerChromosome));
        if (parent2 != null) {
            evaluators.add(new ChromosomeSelection(parent2.partner2Chromosome));
            evaluators.add(new ChromosomeSelection(parent2.partnerChromosome));
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
        archivePopulation2.clear();
        tempArchivePopulation2.clear();
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
            ChromosomeSelection fittest = (ChromosomeSelection) population.getChromosome(population.maxFit).clone();

            //Select the second most fittest chromosome
            ChromosomeSelection secondFittest = (ChromosomeSelection) population.getChromosome(population.maxFitOfSecondFittest).clone();
            firstinPopulation1Picked = fittest;
            secondinPopulation1Picked = secondFittest;
            fittest = (ChromosomeSelection) population2.getChromosome(population2.maxFit).clone();

            //Select the second most fittest chromosome
            secondFittest = (ChromosomeSelection) population2.getChromosome(population2.maxFitOfSecondFittest).clone();

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
                pos = universalValueTournamentSelection(popSize, population1EvalTeam, population, archivePopulation);
            } else {
                pos = globalMultiObjectiveTournamentSelection(popSize, population1EvalTeam, population, archivePopulation);
            }
            firstinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[0])).clone();
            secondinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                    Integer.parseInt(pos.split(" ")[1])).clone();
            if (universalEval) {
                pos = universalValueTournamentSelection(popSize, population2EvalTeam, population2, archivePopulation2);
            } else {
                pos = globalMultiObjectiveTournamentSelection(popSize, population2EvalTeam, population2, archivePopulation2);
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

            firstinPopulation1Picked = randomSelectors(population, archivePopulation);
            secondinPopulation1Picked = randomSelectors(population, archivePopulation);
            firstinPopulation2Picked = randomSelectors(population2, archivePopulation2);
            secondinPopulation2Picked = randomSelectors(population2, archivePopulation2);
        }
    }

    private ChromosomeSelection randomSelectors(Population pop, List<ChromosomeSelection> archivePop) throws CloneNotSupportedException {
        if (archivePop.isEmpty()) {
            return (ChromosomeSelection) pop.randomlyPicked(pop.POPSIZE).clone();
        } else {
            return (ChromosomeSelection) pop.randomlyPicked(pop.POPSIZE, archivePop.size(), archivePop).clone();
        }
    }

    private void tournamentSelectionForFillers(Population switchPopFiller, List<ChromosomeSelection> archivePop) throws CloneNotSupportedException {
        if (switchPopFiller.POPSIZE >= 10) {
            eval_Values = new double[10][2];
        } else {
            eval_Values = new double[switchPopFiller.POPSIZE][2];
        }
        population1EvalTeam.clear();
        String pos = "";
        if (universalEval) {
            pos = universalValueTournamentSelection(switchPopFiller.POPSIZE, population1EvalTeam, switchPopFiller, archivePop);
        } else {
            pos = globalMultiObjectiveTournamentSelection(switchPopFiller.POPSIZE, population1EvalTeam, switchPopFiller, archivePop);
        }
        firstinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                Integer.parseInt(pos.split(" ")[0])).clone();
        secondinPopulation1Picked = (ChromosomeSelection) population1EvalTeam.get(
                Integer.parseInt(pos.split(" ")[1])).clone();

        eval_Values = new double[evaluatorSize][2];
        population1EvalTeam.clear();
    }

    private String universalValueTournamentSelection(int popSize, @NotNull List<ChromosomeSelection> population1EvalTeam,
                                                     @NotNull Population pop, List<ChromosomeSelection> archivePop) throws CloneNotSupportedException {
        for (int i = 0; i < eval_Values.length; i++) {
            if (archivePop.isEmpty()) {
                population1EvalTeam.add((ChromosomeSelection) pop.randomlyPicked(popSize).clone());
            } else {
                population1EvalTeam.add((ChromosomeSelection) pop.randomlyPicked(popSize, archivePop.size(), archivePop).clone());
            }
            eval_Values[i][0] = population1EvalTeam.get(i).fitness + population1EvalTeam.get(i).secondFitness;
        }
        int max = 0;
        int secondMax = 1;
        for (int i = 1; i < eval_Values.length; i++) {
            if (eval_Values[i][0] > eval_Values[max][0]) {
                //if (eval_Values[i][0] <= eval_Values[max][0]) {
                secondMax = max;
                max = i;
            }
        }//goal is to ensure that secondMax starts off as any number but max.
        //if it starts of as max, it can't be overridden
        secondMax = (max + secondMax) % evaluators.size();
        for (int i = 0; i < eval_Values.length; i++) {
            if (eval_Values[i][0] >= eval_Values[secondMax][0] && max != i) {
                //if (eval_Values[i][0] <= eval_Values[secondMax][0]) {
                secondMax = i;
            }
        }
        String theTwoPos = max + " ";
        theTwoPos += String.valueOf(secondMax);
        return theTwoPos;
    }

    //multi-objective global eval
    @NotNull
    private String globalMultiObjectiveTournamentSelection(int popSize, @NotNull List<ChromosomeSelection> population1EvalTeam,
                                                           @NotNull Population pop, List<ChromosomeSelection> archivePop) throws CloneNotSupportedException {
        for (int i = 0; i < 10; i++) {
            if (archivePop.isEmpty()) {
                population1EvalTeam.add((ChromosomeSelection) pop.randomlyPicked(popSize).clone());
            } else {
                population1EvalTeam.add((ChromosomeSelection) pop.randomlyPicked(popSize, archivePop.size(), archivePop).clone());
            }
            eval_Values[i][0] = population1EvalTeam.get(i).fitness;
            eval_Values[i][1] = population1EvalTeam.get(i).secondFitness;
        }
        int max = 0;
        int secondMax = 0;
        int max2 = 0;
        int secondMax2 = 0;
        for (int i = 1; i < eval_Values.length; i++) {
            if (eval_Values[i][0] > eval_Values[max][0]) {
                max = i;
            }
            if (eval_Values[i][1] > eval_Values[max2][1]) {
                max2 = i;
            }//goal is to ensure that secondMax starts off as any number but max.
            //if it starts of as max, it can't be overridden
            secondMax = (max + 1) % eval_Values.length;
            secondMax2 = (max2 + 1) % eval_Values.length;
            if (eval_Values[i][0] > eval_Values[secondMax][0] && i != max) {
                secondMax = i;
            }
            if (eval_Values[i][1] > eval_Values[secondMax2][1] && i != max2) {
                secondMax2 = i;
            }
        }
        String theTwoPos = max + " ";
        if (max != max2) {
            theTwoPos += String.valueOf(max2);
        } else {
            if (eval_Values[secondMax][0] + eval_Values[secondMax][1] >
                    eval_Values[secondMax2][0] + eval_Values[secondMax2][1]) {
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
        } else if (point2 == point1) {
            if (point2 > 5 * ChromosomeSelection.geneLength / 6) {
                point1 = rn.nextInt(point2);
            } else {
                point2 = rn.nextInt(ChromosomeSelection.geneLength - point1) + point1;
            }
        }

    }

    /**
     * picking a random gene and swapping it with its allelle
     * using inversion mutation
     */
    private void gaussianMutation() {
        //Flip values at the mutation point
        if (firstinPopulation1Picked != null) {
            firstOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
            secondOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
            thirdOffSpringProducedInPopulation1 = (ChromosomeSelection) firstinPopulation1Picked.clone();
            fourthOffSpringProducedInPopulation1 = (ChromosomeSelection) secondinPopulation1Picked.clone();
            firstOffSpringProducedInPopulation1.gene = mutate(firstOffSpringProducedInPopulation1);
            secondOffSpringProducedInPopulation1.gene = mutate(secondOffSpringProducedInPopulation1);
            thirdOffSpringProducedInPopulation1.gene = mutate(thirdOffSpringProducedInPopulation1);
            fourthOffSpringProducedInPopulation1.gene = mutate(fourthOffSpringProducedInPopulation1);
        }
        if (firstinPopulation2Picked != null) {
            firstOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
            secondOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
            thirdOffSpringProducedInPopulation2 = (ChromosomeSelection) firstinPopulation2Picked.clone();
            fourthOffSpringProducedInPopulation2 = (ChromosomeSelection) secondinPopulation2Picked.clone();
            firstOffSpringProducedInPopulation2.gene = mutate(firstOffSpringProducedInPopulation2);
            secondOffSpringProducedInPopulation2.gene = mutate(secondOffSpringProducedInPopulation2);
            thirdOffSpringProducedInPopulation2.gene = mutate(thirdOffSpringProducedInPopulation2);
            fourthOffSpringProducedInPopulation2.gene = mutate(fourthOffSpringProducedInPopulation2);
        }
        populationEvaluationStarters(
                firstOffSpringProducedInPopulation1,
                secondOffSpringProducedInPopulation1,
                firstOffSpringProducedInPopulation2,
                secondOffSpringProducedInPopulation2
        );
        populationEvaluationStarters(
                thirdOffSpringProducedInPopulation1,
                fourthOffSpringProducedInPopulation1,
                thirdOffSpringProducedInPopulation2,
                fourthOffSpringProducedInPopulation2
        );
    }

    private double mutate(ChromosomeSelection offSpring) {
        //Select a random mutation point
        boolean passedThroughGaussian = false;
        double value = 0;
        while (!passedThroughGaussian || (offSpring.getGene() > rangeMax && offSpring.getGene() > 1)) {
            offSpring.gene -= value;
            passedThroughGaussian = true;
            if (rangeMax > 1) {
                value = gaus() * rangeMax;
            } else {
                value = gaus();
            }
            offSpring.gene += value;
        }
        return offSpring.getGene();
    }

    private double randomNumberInRange() {
        return -1 + (rangeMax + 1) * new Random().nextDouble();
    }

    private double gaus() {
        return 0.01 * new Random().nextGaussian();
    }

    private void firstEvaluation() {
        for (int i = 0; i < population.POPSIZE; i += 2) {
            noOfEvaluation += 4;
            population2.chromosomes[i].fitness = population.chromosomes[i].calcPairedFitness(
                    population2.chromosomes[i].getStringChromosome(), 0, allpath);
            population2.chromosomes[i].partnerChromosome = population.chromosomes[i].getStringChromosome();

            population2.chromosomes[i].secondFitness = population.chromosomes[i + 1].calcPairedFitness(
                    population2.chromosomes[i].getStringChromosome(), 1, allpath);
            population2.chromosomes[i].partner2Chromosome = population.chromosomes[i + 1].getStringChromosome();

            population2.chromosomes[i + 1].secondFitness = population.chromosomes[i].calcPairedFitness(
                    population2.chromosomes[i + 1].getStringChromosome(), 1, allpath);
            population2.chromosomes[i + 1].partner2Chromosome = population.chromosomes[i].getStringChromosome();

            population2.chromosomes[i + 1].fitness = population.chromosomes[i + 1].calcPairedFitness(
                    population2.chromosomes[i + 1].getStringChromosome(), 0, allpath);
            population2.chromosomes[i + 1].partnerChromosome = population.chromosomes[i + 1].getStringChromosome();
        }
    }

    private void evaluation(int position) throws CloneNotSupportedException {
        baseEvaluation(position, population1EvalTeam, switchOverPopulation, tempArchivePopulation);
        if (foundFittest || stagnantValue >= noOfReoccurence) {
            foundFittestinPop1 = true;
            return;
        }
        if (generationCount > 1) {
            grandParentEvaluators(firstinPopulation2Picked, secondinPopulation2Picked);
            if (population.fittest > -1000 && generationCount % 2 == 0) {
                evaluators.add(new ChromosomeSelection(population.getChromosome(population.maxFit).getStringChromosome()));
            }
            eval_Values = new double[population2EvalTeam.size()][evaluatorSize];
        }
        baseEvaluation(position, population2EvalTeam, switchOverPopulation2, tempArchivePopulation2);
        evaluators.clear();
        eval_Values = new double[population2EvalTeam.size()][2];
        if (foundFittest || stagnantValue >= noOfReoccurence) {
            foundFittestinPop1 = false;
            return;
        }
    }

    private void baseEvaluation(int position, @NotNull List<ChromosomeSelection> populationEvalTeam,
                                @NotNull Population switchOverPopulation,
                                List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        for (int i = 0; i < evaluators.size(); i++) {
            for (int j = 0; j < populationEvalTeam.size(); j++) {
                ++noOfEvaluation;
                eval_Values[j][i] = populationEvalTeam.get(j).calcPairedFitness(
                        evaluators.get(i).getStringChromosome(), i, allpath
                );
            }
        }
        noOfEvaluation -= 4;
        if (evaluators.size() < eval_Values[0].length) {
            for (int i = evaluators.size(); i < eval_Values[0].length; i++) {
                for (int j = 0; j < eval_Values.length; j++) {
                    //todo always set it to the most impossible i.e if it is a global max, set it very low and vicecersa for global min
                    eval_Values[j][i] = -100000;
                }
            }
        }
        paretoFront(position, populationEvalTeam, switchOverPopulation, tempArchivePop);
        // bestSelected(position, populationEvalTeam, switchOverPopulation);

    }

    /**
     * @param evalTeam pick pareto fronts; i.e incomparable values
     */
    void paretoFront(int position, @NotNull List<ChromosomeSelection> evalTeam,
                     @NotNull Population switchOverPop,
                     List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        paretoFrontTeam.add(evalTeam.get(0));
        for (int i = 1; i < evalTeam.size(); i++) {
            int sizeOfParetoFrontTeam = paretoFrontTeam.size();
            boolean dominatedBefore = false;
            boolean nonDominatedBefore = false;
            for (int j = 0; j < sizeOfParetoFrontTeam; j++) {
                int positionInEvalTeam = evalTeam.indexOf(paretoFrontTeam.get(j));
                boolean better = false;
                boolean same = false;
                for (int ev = 0; ev < evaluators.size(); ev++) {
                    if (eval_Values[i][ev] == eval_Values[positionInEvalTeam][ev]) {
                        same = true;

                    } else if (eval_Values[i][ev] > eval_Values[positionInEvalTeam][ev]) {
                        better = true;
                    } else {
                        better = false;
                        break;
                    }
                }
                if (better) {
                    if (!dominatedBefore) {
                        paretoFrontTeam.set(j, evalTeam.get(i));
                        dominatedBefore = true;
                    } else {
                        paretoFrontTeam.remove(j);
                        sizeOfParetoFrontTeam--;
                    }
                } else {
                    boolean worse = false;
                    for (int ev = 0; ev < evaluators.size(); ev++) {
                        if (eval_Values[i][ev] <= eval_Values[positionInEvalTeam][ev]) {
                            worse = true;
                        } else {
                            worse = false;
                            break;
                        }
                    }
                    if (worse) {
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
        paretoFrontBestFeetOut(position, evalTeam, switchOverPop, tempArchivePop);
    }

    void strengthLocator(@NotNull List<ChromosomeSelection> evalTeam) throws CloneNotSupportedException {
        double highest = -10000;
        int point = 0;
        for (int i = 0; i < evaluators.size(); i++) {
            for (int j = 0; j < paretoFrontTeam.size(); j++) {
                if (eval_Values[evalTeam.indexOf(paretoFrontTeam.get(j))][i] > highest) {
                    highest = eval_Values[evalTeam.indexOf(paretoFrontTeam.get(j))][i];
                    point = evalTeam.indexOf(paretoFrontTeam.get(j));
                }
            }
            positionLocator.add(point);
            highestLocator.add(highest);
            highest = -10000;
            point = 0;
        }

        for (int i = 0; i < evaluators.size(); i++) {
            for (int j = 0; j < paretoFrontTeam.size(); j++) {
                if (eval_Values[evalTeam.indexOf(paretoFrontTeam.get(j))][i] > highest
                        && eval_Values[evalTeam.indexOf(paretoFrontTeam.get(j))][i] != Double.parseDouble(highestLocator.get(j).toString())) {
                    highest = eval_Values[evalTeam.indexOf(paretoFrontTeam.get(j))][i];
                }
            }
            if (highest > -1000) {
                gapLocator.add(Double.parseDouble(highestLocator.get(gapLocator.size()).toString()) - highest);
            } else {
                gapLocator.add(0);
            }
            highest = -10000;
        }
    }

    /**
     * ensures that the two fitness the pareto fronts hold are the best and also launches the evaluator selectors --strongest individual or highest sum
     */
    void paretoFrontBestFeetOut(int position, @NotNull List<ChromosomeSelection> evalTeam,
                                @NotNull Population switchOverPop,
                                List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        if (paretoFrontTeam.size() > 1) {
            //strengthLocator(evalTeam);
        }
        //set the two fitness in the pareto fronts to the best
        for (ChromosomeSelection paretofront : paretoFrontTeam) {
            int positionInEvalTeam = evalTeam.indexOf(paretofront);
            int max = 0;
            int secondMax = 1;
            for (int i = 1; i < evaluators.size(); i++) {
                if (eval_Values[positionInEvalTeam][i] > eval_Values[positionInEvalTeam][max]) {
                    max = i;
                }
            }
            //goal is to ensure that secondMax starts off as any number but max.
            //if it starts of as max, it can't be overridden
            secondMax = (max + secondMax) % evaluators.size();
            for (int i = 0; i < evaluators.size(); i++) {
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
        if (new Random().nextInt() % 23 >= 10) {
            pickEvaluatorsFromParetoFrontBasedOnStrongestIndividual(position, switchOverPop, tempArchivePop);
        } else {
            pickEvaluatorsFromParetoFrontBasedOnHighestSum(position, switchOverPop, tempArchivePop);
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
    private void pickEvaluatorsFromParetoFrontBasedOnStrongestIndividual(int position, @NotNull Population switchOverPop,
                                                                         List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
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
        theSwapOrTransferIntoTheInterimPop(position, switchOverPop, tempArchivePop);
    }

    /**
     * @param position
     * @param switchOverPop
     * @throws CloneNotSupportedException
     */
    private void pickEvaluatorsFromParetoFrontBasedOnHighestSum(int position, @NotNull Population switchOverPop,
                                                                List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
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
        theSwapOrTransferIntoTheInterimPop(position, switchOverPop, tempArchivePop);
    }

    /**
     * @param position
     * @param switchOverPop
     * @throws CloneNotSupportedException
     */
    private void theSwapOrTransferIntoTheInterimPop(int position, @NotNull Population switchOverPop,
                                                    List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        if (secondMaxFitness >= switchOverPop.fittest && switchOverPop.positionPointer < switchOverPop.POPSIZE - 1) {
            switchOverPop.maxFitOfSecondFittest = switchOverPop.positionPointer + 1;
        }
        if (maxFitness > switchOverPop.fittest) {
            switchOverPop.fittest = maxFitness;
            switchOverPop.maxFit = switchOverPop.positionPointer;
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
        switchOverPop.saveChromosomes(switchOverPop.positionPointer, (ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
        if (switchOverPop.positionPointer < switchOverPop.POPSIZE - 1) {
            if (positionOfSecondMax >= 0) {
                switchOverPop.saveChromosomes(switchOverPop.positionPointer + 1, (ChromosomeSelection) paretoFrontTeam.get(positionOfSecondMax).clone());
                switchOverPop.positionPointer += 2;
            } else {
                if (!tempArchivePop.isEmpty()) {
                    switchOverPop.saveChromosomes(switchOverPop.positionPointer + 1, tempArchivePop.get(0));
                    tempArchivePop.remove(0);
                    switchOverPop.positionPointer += 2;
                } else {
                    switchOverPop.positionPointer += 1;
                    //switchOverPop.saveChromosomes(position + 1, (ChromosomeSelection) paretoFrontTeam.get(positionOfMax).clone());
                }
            }
        } else {
            switchOverPop.positionPointer += 1;
        }
        for (int i = 0; i < paretoFrontTeam.size(); i++) {
            if (i != positionOfMax && i != positionOfSecondMax) {
                tempArchivePop.add(paretoFrontTeam.get(i));
            }
        }
        paretoFrontTeam.clear();
        if (maxFitness >= maxFitnessFromEquation || secondMaxFitness >= maxFitnessFromEquation) {
            foundFittest = true;
        }
    }

    private void filler(Population switchpop, Population realPop, Population otherPop, ChromosomeSelection chrome1, ChromosomeSelection chrome2, List<ChromosomeSelection> archivePop,
                        List<ChromosomeSelection> tempArchivePop) throws CloneNotSupportedException {
        while (switchpop.positionPointer < switchpop.POPSIZE) {
            tournamentSelectionForFillers(realPop, archivePop);
            evaluators.clear();
            evaluators.add(otherPop.chromosomes[otherPop.maxFit]);
            evaluators.add(otherPop.chromosomes[otherPop.maxFitOfSecondFittest]);
            grandParentEvaluators(firstinPopulation1Picked, secondinPopulation1Picked);
            processFiller();
            eval_Values = new double[population1EvalTeam.size()][evaluatorSize];
            baseEvaluation(0, population1EvalTeam, switchpop, tempArchivePop);

        }
    }
}
