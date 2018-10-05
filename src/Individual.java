import org.vu.contest.ContestEvaluation;

import java.util.Comparator;
import java.util.Random;

public class Individual {
    public Genotype Genes;
    public double Fitness;

    public static Comparator<Individual> Comparator = new SortByFitness();

    public Individual(Genotype genotype, double fitness){
        this.Genes = genotype;
        this.Fitness = fitness;
    }

    public static Individual Create_Rand(Random rand, ContestEvaluation evaluation_){
        Genotype genotype = Genotype.Create_Rand(rand);

        return new Individual(genotype, (double)evaluation_.evaluate(genotype.Values));
    }

    static class SortByFitness implements Comparator<Individual>{

        @Override
        public int compare(Individual first, Individual second) {
            return Double.compare(first.Fitness, second.Fitness);
        }
    }
}
