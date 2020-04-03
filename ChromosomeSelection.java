    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */

    import org.jetbrains.annotations.NotNull;
    import java.util.Random;

    /**
     * @author FAkinola
     */
    public class ChromosomeSelection implements Cloneable {
        double fitness;
        double secondFitness;
        static int geneLength;
        double gene;
        String partnerChromosome;
        String partner2Chromosome;
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

        public ChromosomeSelection() {
            //Set genes randomly for each chromosome
            gene = Math.random();
            fitness = 0;
        }

        public ChromosomeSelection(int rangeMin, double rangeMax) {
            Random r = new Random();
            //Set genes randomly for each chromosome
            gene = rangeMin + (0.5 - rangeMin) * r.nextDouble();
            fitness = 0;
        }

        public ChromosomeSelection(String genesString) {
            gene = Double.parseDouble(genesString);
        }

        /**
         * @return converts the genes in a chromosome to a string
         */
        public String getStringChromosome() {
            return String.valueOf(getGene());
        }

        private double calculateTheXAndYs(double x, double y, double independentVariableX,
                                          double independentVariableY, double independentVariableXorY, int sign) {
            return ((x - independentVariableX) * Math.cos(Math.PI / 4))
                    + sign * ((y - independentVariableY) * Math.sin(Math.PI / 4))
                    + independentVariableXorY;
        }

        private double SMTQ(double chromosome, double partnerChromosom, boolean isSMTQ) {
            double value1;
            double value2;
            if (isSMTQ) {
                value1 = H1 * (1 - ((SMTQHas32 * Math.pow((calculateTheXAndYs(chromosome, partnerChromosom, X1, Y1, X1, 1) - X1), 2) +
                        SMTQHas8 * Math.pow((calculateTheXAndYs(chromosome, partnerChromosom, X1, Y1, Y1, -1) - Y1), 2)) / S1));

                value2 = H2 * (1 - ((SMTQHas32 * Math.pow((calculateTheXAndYs(chromosome, partnerChromosom, X2, Y2, X2, 1) - X2), 2) +
                        SMTQHas8 * Math.pow((calculateTheXAndYs(chromosome, partnerChromosom, X2, Y2, Y2, -1) - Y2), 2)) / S2));
            } else {
                value1 = H1 * (1 - ((SMTQHas32 * Math.pow((chromosome - X1), 2) +
                        SMTQHas8 * Math.pow((partnerChromosom - Y1), 2)) / S1));

                value2 = H2 * (1 - ((SMTQHas32 * Math.pow((chromosome - X2), 2) +
                        SMTQHas8 * Math.pow((partnerChromosom - Y2), 2)) / S2));
            }
            return Math.max(value1, value2);
        }

        private double MTQ(double chromosome, double partnerChromosom) {
            SMTQHas32 = SMTQHas8 = 16;
            return SMTQ(chromosome, partnerChromosom, false);
        }

        private double getDomainXorY(Double initialXorY) {
            return (10.24 * initialXorY) - 5.12;
        }

        private double damavandi(double chromosome, double partnerChromosom) {
            //-14 to 14
            double absPart = Math.sin(Math.PI * (chromosome - 2)) * Math.sin(Math.PI * (partnerChromosom - 2))
                    / (Math.pow(Math.PI, 2) * (chromosome - 2) * (partnerChromosom - 2));
            double firstPart = 1 - Math.pow(Math.abs(absPart), 5);
            double otherPart = 2 + Math.pow(chromosome - 7, 2) + (2 * Math.pow(partnerChromosom - 7, 2));

            return firstPart * otherPart * -1;
        }

        double griewank(double chromosome, double partnerChromosom) {
            return -1 - (Math.pow(getDomainXorY(chromosome), 2) / 4000) - (Math.pow(getDomainXorY(partnerChromosom), 2) / 4000)
                    + (Math.cos(getDomainXorY(chromosome)) * Math.cos(getDomainXorY(partnerChromosom) / Math.sqrt(2)));
        }

        double eggHolderFunction(double chromosome, double partnerChromosom) {
            //-512 to 512 for global and more
            return ((partnerChromosom + 47) * Math.sin(Math.sqrt(Math.abs((0.5 * chromosome) + partnerChromosom + 47)))) +
                    chromosome * Math.sin(Math.sqrt(Math.abs(chromosome - partnerChromosom - 47)));
        }

        double bohachevskyFunction(double chromosome, double partnerChromosom) {

            return (0.3 * Math.cos(3 * chromosome * Math.PI)) + (0.4 * Math.cos(4 * partnerChromosom * Math.PI))
                    - 0.7 - Math.pow(chromosome, 2) - (2 * Math.pow(partnerChromosom, 2));
        }

        double boothDomain(double chromosome, double partnerChromosom) {

            return -1 * (Math.pow((getDomainXorY(chromosome) + 2 * (getDomainXorY(partnerChromosom)) - 7), 2)
                    + Math.pow((getDomainXorY(partnerChromosom) + 2 * (getDomainXorY(chromosome)) - 5), 2));
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

        double calcPairedFitness(@NotNull String partner, int type, int functionNumber) {
            if (type == 1) {
                secondFitness = 0;
                this.secondFitness = conditioner(functionNumber, partner);
                this.partner2Chromosome = partner;
                return this.secondFitness;
            } else if (type == 0) {
                fitness = 0;
                this.fitness = conditioner(functionNumber, partner);
                this.partnerChromosome = partner;
                return this.fitness;
            } else {
                return conditioner(functionNumber, partner);
            }
        }

        double conditioner(int functionNumber, String partner) {
            double answer = 0;
            if (functionNumber == 0) {
                answer = SMTQ(this.getGene(), Double.parseDouble(partner), true);
            } else if (functionNumber == 1) {
                answer = MTQ(this.getGene(), Double.parseDouble(partner));
            } else if (functionNumber == 2) {
                H1 = 125;
                answer = SMTQ(this.getGene(), Double.parseDouble(partner), true);
            } else if (functionNumber == 3) {
                H1 = 125;
                answer = MTQ(this.getGene(), Double.parseDouble(partner));
            } else if (functionNumber == 4) {
                answer = damavandi(this.getGene(), Double.parseDouble(partner));
            } else if (functionNumber == 5) {
                answer = griewank(this.getGene(), Double.parseDouble(partner));
            } else if (functionNumber == 6) {
                answer = eggHolderFunction(this.getGene(), Double.parseDouble(partner));

            } else if (functionNumber == 7) {
                answer = bohachevskyFunction(this.getGene(), Double.parseDouble(partner));

            } else if (functionNumber == 8) {
                answer = boothDomain(this.getGene(), Double.parseDouble(partner));
            }
            return answer;
        }

        protected Object clone() {
            ChromosomeSelection newChromosomeSelection = new ChromosomeSelection();
            newChromosomeSelection.partner2Chromosome = this.partner2Chromosome;
            newChromosomeSelection.secondFitness = this.secondFitness;
            newChromosomeSelection.fitness = this.fitness;
            newChromosomeSelection.partnerChromosome = this.partnerChromosome;
            newChromosomeSelection.gene = this.gene;
            return newChromosomeSelection;
        }

        public double getGene() {
            return gene;
        }

    }
