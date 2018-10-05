import org.vu.contest.ContestEvaluation;

import java.util.Comparator;
import java.util.Random;

public class Individual {
    public Genotype Genes;
    public double Fitness;
    public double MutationStepSize;

    public static Comparator<Individual> Comparator = new SortByFitness();
    private static Random rand = new Random();

    public Individual(Genotype genotype, double fitness){
        this.Genes = genotype;
        this.Fitness = fitness;
        this.MutationStepSize = rand.nextGaussian() - .5;
    }

    public static Individual Create_Rand(ContestEvaluation evaluation_){
        Genotype genotype = Genotype.Create_Rand(rand);

        return new Individual(genotype, (double)evaluation_.evaluate(genotype.Values));
    }

    public Individual Mutate(double mutationStepSizeMultiplier, ContestEvaluation eval) {
        double[] mutatedGenes = new double[10];
        for (int i = 0; i < 10; i++) {
            mutatedGenes[i] = this.Genes.Values[i] + rand.nextGaussian() * mutationStepSizeMultiplier + MutationStepSize;
        }

        Genotype mutatedGenotype = new Genotype(mutatedGenes);
        double fitness = (double)eval.evaluate(mutatedGenes);

        return new Individual(mutatedGenotype, fitness);
    }

    static class SortByFitness implements Comparator<Individual>{

        @Override
        public int compare(Individual first, Individual second) {
            return -Double.compare(first.Fitness, second.Fitness);
        }
    }
}
