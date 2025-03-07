/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import java.util.Random;

/**
 * @author FAkinola qqnorm(rnorm(data))
 */
public class Population implements Cloneable {
    ChromosomeSelection[] chromosomes;
    double fittest = -1000;
    int maxFit;
    int maxFitOfSecondFittest;
    int positionPointer = 0;
    int POPSIZE;

    //Initialize population
    public void initializePopulation(int popSize, int rangeMin, double rangeMax) {
        POPSIZE = popSize;
        chromosomes = new ChromosomeSelection[popSize];
        for (int i = 0; i < chromosomes.length; i++) {
            chromosomes[i] = new ChromosomeSelection(rangeMin,rangeMax);
        }
    }

    /**
     * @param index
     * @param chromosome saves a chromosome that has probably undergone change or is new
     */
    public void saveChromosomes(int index, ChromosomeSelection chromosome){
        chromosomes[index] = (ChromosomeSelection) chromosome.clone();
    }

    /**
     * @param popSize
     * @return randomly pick within the array
     */
    public ChromosomeSelection randomlyPicked(int popSize){
        return (ChromosomeSelection) chromosomes[new Random().nextInt(popSize)].clone();
    }

    /**
     * @param popSize
     * @return randomly pick within the pop and archive pop
     */
    public ChromosomeSelection randomlyPicked(int popSize, int archivePopSize, List<ChromosomeSelection> archiveChromosome) {
        int position = new Random().nextInt(popSize + archivePopSize);
        if (position >= popSize) {
            return (ChromosomeSelection) archiveChromosome.get(position - popSize).clone();
        } else {
            return (ChromosomeSelection) chromosomes[position].clone();
        }
    }

    public ChromosomeSelection getChromosome(int index){
        return (ChromosomeSelection) chromosomes[index].clone();
    }

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

    protected Object clone(Population upgradedPopulation) throws CloneNotSupportedException, NullPointerException {
        if (upgradedPopulation == null) {
            upgradedPopulation = (Population) super.clone();
        }
        upgradedPopulation.chromosomes = this.chromosomes.clone();
        for (int i = 0; i < this.chromosomes.length; i++) {
            upgradedPopulation.chromosomes[i].gene = this.chromosomes[i].gene;
            upgradedPopulation.chromosomes[i].secondFitness = this.chromosomes[i].secondFitness;
            upgradedPopulation.chromosomes[i].fitness = this.chromosomes[i].fitness;
            upgradedPopulation.chromosomes[i].partner2Chromosome = this.chromosomes[i].partner2Chromosome;
            upgradedPopulation.chromosomes[i].partnerChromosome = this.chromosomes[i].partnerChromosome;
        }
        upgradedPopulation.maxFit = this.maxFit;
        upgradedPopulation.maxFitOfSecondFittest = this.maxFitOfSecondFittest;
        upgradedPopulation.fittest = this.fittest;
        return upgradedPopulation;
    }

}
