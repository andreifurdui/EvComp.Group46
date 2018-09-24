import org.vu.contest.ContestEvaluation;

import java.util.Random;

public class Individual {
    public Genotype Genes;
    public double Fitness;

    public Individual(Genotype genotype, double fitness){
        this.Genes = genotype;
        this.Fitness = fitness;
    }

    public static Individual Create_Rand(Random rand, ContestEvaluation evaluation_){
        Genotype genotype = Genotype.Create_Rand(rand);

        return new Individual(genotype, (double)evaluation_.evaluate(genotype.Values));
    }
}
