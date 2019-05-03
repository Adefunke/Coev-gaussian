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
        static double X1 = 0.75;
        static double Y1 = 0.75;
        static double X2 = 0.25;
        static double Y2 = 0.25;
        static double S1 = 1.6;
        static double S2 = 1.0 / 32;
        static double H1 = 50;
        static double H2 = 150;

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

        public ChromosomeSelection(String genesString) {
            Random rn = new Random();
            genes = new int[geneLength];

            for (int i = 0; i < genes.length; i++) {
                genes[i] = Integer.parseInt(genesString.substring(i, i + 1));
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

        private double calculateTheXAndYs(double x, double y, double independentVariableX,
                                          double independentVariableY, double independentVariableXorY, int sign) {
            return ((x - independentVariableX) * Math.cos(Math.PI / 4))
                    + sign * ((y - independentVariableY) * Math.sin(Math.PI / 4))
                    + independentVariableXorY;
        }

        private double SMTQ(String chromosome, String partnerChromosom) {
            double xChromosome = Integer.parseInt(chromosome, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);
            double yChromosome = Integer.parseInt(partnerChromosom, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);
            double value1 = H1 * (1 - ((32 * Math.pow((calculateTheXAndYs(xChromosome, yChromosome, X1, Y1, X1, 1) - X1), 2) +
                    8 * Math.pow((calculateTheXAndYs(xChromosome, yChromosome, X1, Y1, Y1, -1) - Y1), 2)) / S1));

            double value2 = H2 * (1 - ((32 * Math.pow((calculateTheXAndYs(xChromosome, yChromosome, X2, Y2, X2, 1) - X2), 2) +
                    8 * Math.pow((calculateTheXAndYs(xChromosome, yChromosome, X2, Y2, Y2, -1) - Y2), 2)) / S2));
            if (value1 > value2) {
                return value1;
            } else {
                return value2;
            }
        }

        private double getDomainXorY(Double initialXorY) {
            return (10.24 * initialXorY) - 5.12;
        }

        double rosenBrockDomain(String chromosome, String partnerChromosom) {
            Double xChromosome = Integer.parseInt(chromosome, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);
            Double yChromosome = Integer.parseInt(partnerChromosom, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);

            return -1 * (100 * (Math.pow(Math.pow(getDomainXorY(xChromosome), 2) - getDomainXorY(yChromosome), 2))
                    + Math.pow((1 - getDomainXorY(xChromosome)), 2));
        }

        double boothDomain(String chromosome, String partnerChromosom) {
            Double xChromosome = Integer.parseInt(chromosome, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);
            Double yChromosome = Integer.parseInt(partnerChromosom, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);

            return -1 * (Math.pow((getDomainXorY(xChromosome) + 2 * (getDomainXorY(yChromosome)) - 7), 2)
                    + Math.pow((getDomainXorY(yChromosome) + 2 * (getDomainXorY(xChromosome)) - 5), 2));
        }

        double oneRidgeFunction(String chromosome, String partnerChromosom) {
            Double xChromosome = Integer.parseInt(chromosome, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);
            Double yChromosome = Integer.parseInt(partnerChromosom, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);

            return 1 + 2 * Math.min(xChromosome, yChromosome) - Math.max(xChromosome, yChromosome);
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
                fitness += Math.pow(genes[i] / 10.0, 2) - (10 * Math.cos(2 * Math.PI * genes[i] / 10));
            }
            fitness += (10 * geneLength / 16.0);
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
                //this.secondFitness = calcFitness() + partner.replaceAll("0", "").length();
                this.secondFitness = oneRidgeFunction(this.getStringChromosome(), partner);
                //this.secondFitness = boothDomain(this.getStringChromosome(), partner);
                this.partner2Chromosome = partner;
                return this.secondFitness;
            } else if (type == 0) {
                fitness = 0;
                //           this.fitness = innerMatchCalcFitness(this.getStringChromosome()) + innerMatchCalcFitness(partner);
                //            this.fitness = matchCalcFitness(this.getStringChromosome()) + matchCalcFitness(partner);
                //this.fitness = calcFitness() + partner.replaceAll("0", "").length();
                this.fitness = oneRidgeFunction(this.getStringChromosome(), partner);
                //this.fitness =boothDomain(this.getStringChromosome(), partner);
                this.partnerChromosome = partner;
                return this.fitness;
            } else {
                //           return innerMatchCalcFitness(this.getStringChromosome()) + innerMatchCalcFitness(partner);
                //           return matchCalcFitness(this.getStringChromosome()) + matchCalcFitness(partner);
                //return calcFitness() + partner.replaceAll("0", "").length();
                return oneRidgeFunction(this.getStringChromosome(), partner);
                //return boothDomain(this.getStringChromosome(), partner);
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