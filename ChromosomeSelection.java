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
        double SMTQHas32 = 32;
        double SMTQHas8 = 8;

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
                //genes[0] = 1;
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

        private double SMTQ(String chromosome, String partnerChromosom, boolean isSMTQ) {
            double xChromosome = Long.parseLong(chromosome, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);
            double yChromosome = Long.parseLong(partnerChromosom, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);
            double value1;
            double value2;
            if (isSMTQ) {
                value1 = H1 * (1 - ((SMTQHas32 * Math.pow((calculateTheXAndYs(xChromosome, yChromosome, X1, Y1, X1, 1) - X1), 2) +
                        SMTQHas8 * Math.pow((calculateTheXAndYs(xChromosome, yChromosome, X1, Y1, Y1, -1) - Y1), 2)) / S1));

                value2 = H2 * (1 - ((SMTQHas32 * Math.pow((calculateTheXAndYs(xChromosome, yChromosome, X2, Y2, X2, 1) - X2), 2) +
                        SMTQHas8 * Math.pow((calculateTheXAndYs(xChromosome, yChromosome, X2, Y2, Y2, -1) - Y2), 2)) / S2));
            } else {
                value1 = H1 * (1 - ((SMTQHas32 * Math.pow((xChromosome - X1), 2) +
                        SMTQHas8 * Math.pow((yChromosome - Y1), 2)) / S1));

                value2 = H2 * (1 - ((SMTQHas32 * Math.pow((xChromosome - X2), 2) +
                        SMTQHas8 * Math.pow((yChromosome - Y2), 2)) / S2));
            }
            return Math.max(value1, value2);
        }

        private double MTQ(String chromosome, String partnerChromosom) {
            SMTQHas32 = SMTQHas8 = 16;
            return SMTQ(chromosome, partnerChromosom, false);
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

        double griewank(String chromosome, String partnerChromosom) {
            Double xChromosome = Integer.parseInt(chromosome, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);
            Double yChromosome = Integer.parseInt(partnerChromosom, 2) / (Math.pow(2, ChromosomeSelection.geneLength) - 1);

            return 1 - (Math.pow(getDomainXorY(xChromosome), 2) / 4000) - (Math.pow(getDomainXorY(yChromosome), 2) / 4000)
                    + Math.cos(getDomainXorY(xChromosome)) * Math.cos(getDomainXorY(yChromosome) / Math.sqrt(2));
        }

        double eggHolderFunction(String chromosome, String partnerChromosom) {
            Double xChromosome = ((Integer.parseInt(chromosome, 2) * 2 / (Math.pow(2, ChromosomeSelection.geneLength) - 1)) - 1) * 512;
            Double yChromosome = ((Integer.parseInt(partnerChromosom, 2) * 2 / (Math.pow(2, ChromosomeSelection.geneLength) - 1)) - 1) * 512;

            return ((yChromosome + 47) * Math.sin(Math.sqrt(Math.abs(0.5 * xChromosome + yChromosome + 47)))) +
                    xChromosome * Math.sin(Math.sqrt(Math.abs(xChromosome + yChromosome + 47)));
        }

        double bohachevskyFunction(String chromosome, String partnerChromosom) {
            Double xChromosome = ((Integer.parseInt(chromosome, 2) * 2 / (Math.pow(2, ChromosomeSelection.geneLength) - 1)) - 1) * 100;
            Double yChromosome = ((Integer.parseInt(partnerChromosom, 2) * 2 / (Math.pow(2, ChromosomeSelection.geneLength) - 1)) - 1) * 100;

            return (0.3 * Math.cos(3 * xChromosome * Math.PI)) + (0.4 * Math.cos(4 * yChromosome * Math.PI))
                    - 0.7 - Math.pow(xChromosome, 2) - (2 * Math.pow(yChromosome, 2));
        }

        double schafferFunction(String chromosome, String partnerChromosom) {
            Double xChromosome = ((Integer.parseInt(chromosome, 2) * 2 / (Math.pow(2, ChromosomeSelection.geneLength) - 1)) - 1) * 100;
            Double yChromosome = ((Integer.parseInt(partnerChromosom, 2) * 2 / (Math.pow(2, ChromosomeSelection.geneLength) - 1)) - 1) * 100;

            return Math.pow((Math.pow(xChromosome, 2) + Math.pow(yChromosome, 2)), 0.25) *
                    (Math.pow(Math.sin(50*Math.pow((Math.pow(xChromosome, 2) + Math.pow(yChromosome, 2)), 0.1)), 2)+1);
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

        double calcPairedFitness(@NotNull String partner, int type) {
            if (type == 1) {
                secondFitness = 0;
                //condition is: (ga.geneLength + (3 * ga.geneLength * ga.geneLength/2)) / 2
                //            this.secondFitness = innerMatchCalcFitness(this.getStringChromosome()) + innerMatchCalcFitness(partner);
                //condition: (ga.geneLength + (ga.geneLength * ga.geneLength))
                //            this.secondFitness = matchCalcFitness(this.getStringChromosome()) + matchCalcFitness(partner);
                //condition is: (ga.geneLength *2)
                //this.secondFitness = calcFitness() + partner.replaceAll("0", "").length();
                //this.secondFitness = MTQ(this.getStringChromosome(), partner);
                this.secondFitness = rosenBrockDomain(this.getStringChromosome(), partner);
                this.partner2Chromosome = partner;
                return this.secondFitness;
            } else if (type == 0) {
                fitness = 0;
                //           this.fitness = innerMatchCalcFitness(this.getStringChromosome()) + innerMatchCalcFitness(partner);
                //            this.fitness = matchCalcFitness(this.getStringChromosome()) + matchCalcFitness(partner);
                //this.fitness = calcFitness() + partner.replaceAll("0", "").length();
                //this.fitness = MTQ(this.getStringChromosome(), partner);
                this.fitness = rosenBrockDomain(this.getStringChromosome(), partner);
                this.partnerChromosome = partner;
                return this.fitness;
            } else {
                //           return innerMatchCalcFitness(this.getStringChromosome()) + innerMatchCalcFitness(partner);
                //           return matchCalcFitness(this.getStringChromosome()) + matchCalcFitness(partner);
                //return calcFitness() + partner.replaceAll("0", "").length();
                //return MTQ(this.getStringChromosome(), partner);
                return rosenBrockDomain(this.getStringChromosome(), partner);
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