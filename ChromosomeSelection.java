/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

/**
 * @author FAkinola
 */
public class ChromosomeSelection implements Cloneable {
    double fitness;
    double secondFitness;
    static int geneLength;
    int[] genes;
    String partnerChromosome;
    String partner2Chromosome;
    int boundd;

    public ChromosomeSelection(int bound, int geneLength, boolean rastrigin) {
        Random rn = new Random();
        boundd = bound;
        ChromosomeSelection.geneLength = geneLength;
        genes = new int[geneLength];
        //Set genes randomly for each chromosome
        if (rastrigin) {
            for (int i = 0; i < geneLength / 16; i++) {
                genes[i] = (rn.nextInt(91) - 45);
            }
        } else {
            for (int i = 0; i < genes.length; i++) {
                genes[i] = rn.nextInt(bound);
            }
        }

        fitness = 0;
    }

    public ChromosomeSelection(int bound, boolean rastrigin) {
        Random rn = new Random();
        genes = new int[geneLength];
        boundd = bound;
        //Set genes randomly for each chromosome
        if (rastrigin) {
            for (int i = 0; i < geneLength / 16; i++) {
                genes[i] = (rn.nextInt(91) - 45);
            }
        } else {
            for (int i = 0; i < genes.length; i++) {
                genes[i] = rn.nextInt(bound);
            }
        }

    }

    /**
     * @param rastrigin
     * @return converts the genes in a chromosome to a string
     */
    @NotNull
    public String getChromosome(boolean rastrigin) {
        String chromosome = "";
        if (rastrigin) {
            for (int i = 0; i < geneLength / 16; i++) {
                chromosome += (double) getGene(i) / 10;
                if (i < (geneLength / 16) - 1) {
                    chromosome += ",";
                }
            }
        } else {
            getStringChromosome();
        }
        return chromosome;
    }

    /**
     * @return converts the genes in a chromosome to a string
     */
    public String getStringChromosome() {
        String chromosome = "";
        for (int i = 0; i < geneLength; i++) {
            chromosome += getGene(i);
        }
        return chromosome;
    }


    /**
     * @return crafted from the aim of one max to have all genes as 1
     */
    public double calcFitness() {
        int individualFitness = 0;
        for (int i = 0; i < geneLength; i++) {
            if (genes[i] == boundd - 1) {
                ++individualFitness;
                //fitness= (geneLength - i) + fitness;
            }
        }
        return individualFitness;
    }

    private double matchCalcFitness(String chromosome) {
        int individualFitness = 0;
        for (int i = 0; i < chromosome.length(); i++) {
            individualFitness += (i + 1) * Integer.parseInt(String.valueOf(chromosome.charAt(i)));
        }
        return individualFitness;
    }

    private double innerMatchCalcFitness(String chromosome) {
        int individualFitness = 0;
        for (int i = chromosome.length() - 1; i >= chromosome.length() / 2; i--) {
            if (chromosome.charAt(i) == chromosome.charAt(i % (chromosome.length() / 2))) {
                individualFitness += (i + 1) * Integer.parseInt(String.valueOf(chromosome.charAt(i)));
            }
        }
        return individualFitness;
    }

    /**
     * @return grafted from the rastrigin equation
     */
    double calcFitnessRas() {

        fitness = 0;
        for (int i = 0; i < geneLength / 16; i++) {
            fitness += Math.pow(genes[i] / 10, 2) - (10 * Math.cos(2 * Math.PI * genes[i] / 10));
        }
        fitness += (10 * geneLength / 16);
        return fitness;
    }

    double calcPairedFitness(@NotNull String partner, int type) {
        if (type == 1) {
            secondFitness = 0;
            //condition is: (ga.geneLength + (3 * ga.geneLength * ga.geneLength/2)) / 2
//            this.secondFitness = innerMatchCalcFitness(this.getStringChromosome()) + innerMatchCalcFitness(partner);
            //condition: (ga.geneLength + (ga.geneLength * ga.geneLength))
//            this.secondFitness = matchCalcFitness(this.getStringChromosome()) + matchCalcFitness(partner);
            //condition is: (ga.geneLength *2)
            this.secondFitness = calcFitness() + partner.replaceAll("0", "").length();
            this.partner2Chromosome = partner;
            return this.secondFitness;
        } else {
            fitness = 0;
 //           this.fitness = innerMatchCalcFitness(this.getStringChromosome()) + innerMatchCalcFitness(partner);
//            this.fitness = matchCalcFitness(this.getStringChromosome()) + matchCalcFitness(partner);
            this.fitness = calcFitness() + partner.replaceAll("0", "").length();
            this.partnerChromosome = partner;
            return this.fitness;
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        ChromosomeSelection newChromosomeSelection = new ChromosomeSelection(2, false);
        newChromosomeSelection.partner2Chromosome = this.partner2Chromosome;
        newChromosomeSelection.secondFitness = this.secondFitness;
        newChromosomeSelection.fitness = this.fitness;
        newChromosomeSelection.partnerChromosome = this.partnerChromosome;
        newChromosomeSelection.genes = Arrays.copyOf(this.genes, ChromosomeSelection.geneLength);
        return newChromosomeSelection;
    }

    public int getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, int value) {
        genes[index] = value;
    }

}