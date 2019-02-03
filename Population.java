/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Arrays;
import java.util.Random;

/**
 * @author FAkinola
 */
public class Population implements Cloneable {
    ChromosomeSelection[] chromosomes;
    private double[] fitnessProb;
    double fittest = 0;
    int maxFit;
    int maxFitOfSecondFittest;

    //Initialize population
    public void initializePopulation(int bound, int geneLength, boolean rastrigin, int popSize) {
        fitnessProb = new double[popSize];
        chromosomes = new ChromosomeSelection[popSize];
        for (int i = 0; i < chromosomes.length; i++) {
            chromosomes[i] = new ChromosomeSelection(bound, geneLength, rastrigin);
        }
    }

    /**
     * @param index
     * @param chromosome saves a chromosome that has probably undergone change or is new
     */
    public void saveChromosomes(int index, ChromosomeSelection chromosome) throws CloneNotSupportedException {
        chromosomes[index] = (ChromosomeSelection) chromosome.clone();
    }

    /**
     * @param popSize
     * @return randomly pick within the array
     */
    public ChromosomeSelection randomlyPicked(int popSize) throws CloneNotSupportedException {
        return (ChromosomeSelection) chromosomes[new Random().nextInt(popSize)].clone();
    }

    public ChromosomeSelection getChromosome(int index) throws CloneNotSupportedException {
        return (ChromosomeSelection) chromosomes[index].clone();
    }

    /**
     * @param rastrigin
     * @return fittest chromosome
     */
//    public ChromosomeSelection getFittest(boolean rastrigin) {
//        if (!rastrigin) {
//            maxFit = 0;
//            for (int i = 0; i < chromosomes.length; i++) {
//                if (chromosomes[maxFit].fitness <= chromosomes[i].fitness ||
//                        chromosomes[maxFit].secondFitness <= chromosomes[i].secondFitness ||
//                        chromosomes[maxFit].fitness <= chromosomes[i].secondFitness ||
//                        chromosomes[maxFit].secondFitness <= chromosomes[i].fitness
//                ) {
//                    maxFit = i;
//                }
//            }
//        } else {
//            maxFit = 0;
//            for (int i = 0; i < chromosomes.length; i++) {
//                if (chromosomes[maxFit].fitness >= chromosomes[i].fitness && chromosomes[maxFit].fitness > 0) {
//                    maxFit = i;
//                }
//            }
//        }
//        if (chromosomes[maxFit].fitness > chromosomes[maxFit].secondFitness) {
//            fittest = chromosomes[maxFit].fitness;
//        } else {
//            fittest = chromosomes[maxFit].secondFitness;
//        }
//        return chromosomes[maxFit];
//    }

    /**
     * @return second fittest chromosome when requested for via elitism
     */
    public ChromosomeSelection getSecondFittest() {
        int maxFit2 = 0;
        for (int i = 0; i < chromosomes.length; i++) {
            if ((chromosomes[maxFit2].fitness <= chromosomes[i].fitness ||
                    chromosomes[maxFit2].secondFitness <= chromosomes[i].secondFitness ||
                    chromosomes[maxFit2].fitness <= chromosomes[i].secondFitness ||
                    chromosomes[maxFit2].secondFitness <= chromosomes[i].fitness
            ) && i != maxFit) {
                maxFit2 = i;
            }
        }
        return chromosomes[maxFit2];
    }

    /**
     * @param rastrigin
     * @return calculates the cumulative fitness of each member
     */
    public double calculateCumulativeFitness(boolean rastrigin) {
        double totalFitness = 0.0;
        for (int i = 0; i < chromosomes.length; i++) {
            if (!rastrigin) {
                totalFitness += chromosomes[i].calcFitness();
                fitnessProb[i] = totalFitness;
            } else {
                totalFitness += chromosomes[i].calcFitnessRas();
                fitnessProb[i] = totalFitness;
            }
        }
        return totalFitness;
    }

    /**
     * @param rastrigin
     * @return calculates the cdf's probability
     */
    public double[] calculateProbFitness(boolean rastrigin) {
        double totalFitness = calculateCumulativeFitness(rastrigin);
        for (int i = 0; i < chromosomes.length; i++) {
            fitnessProb[i] = fitnessProb[i] / totalFitness;

        }
        return fitnessProb;
    }

    protected Object clone(Population upgradedPopulation) throws CloneNotSupportedException, NullPointerException {
        if (upgradedPopulation==null){upgradedPopulation = (Population) super.clone();}
        upgradedPopulation.chromosomes = this.chromosomes.clone();
        for (int i = 0; i < this.chromosomes.length; i++) {
            upgradedPopulation.chromosomes[i].genes = Arrays.copyOf(this.chromosomes[i].genes, ChromosomeSelection.geneLength);
            upgradedPopulation.chromosomes[i].secondFitness = this.chromosomes[i].secondFitness;
            upgradedPopulation.chromosomes[i].fitness = this.chromosomes[i].fitness;
            upgradedPopulation.chromosomes[i].partner2Chromosome = this.chromosomes[i].partner2Chromosome;
            upgradedPopulation.chromosomes[i].partnerChromosome = this.chromosomes[i].partnerChromosome;
        }
        upgradedPopulation.fitnessProb = this.fitnessProb.clone();
        upgradedPopulation.maxFit = this.maxFit;
        upgradedPopulation.maxFitOfSecondFittest = this.maxFitOfSecondFittest;
        upgradedPopulation.fittest = this.fittest;
        return upgradedPopulation;
    }

}
