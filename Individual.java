import java.util.Random;
import org.vu.contest.ContestEvaluation;

public class Individual{
    
    private static final int geneSize = 10;

    public double[] Genotype;
    public double Fitness;

    public Individual(double[] genotype, double fitness){
        this.Genotype = genotype;
        this.Fitness = fitness;
    }

    public static Individual InitializeRandomly(Random random, int min, int max, ContestEvaluation evaluation_){
        double[] genotype = new double[geneSize];
        for (int i = 0; i < geneSize; i++) {
            genotype[i] = (random.nextDouble() - 0.5) * 10;
        }
        double fitness = evaluation_.evaluate(genotype);

        return new Individual(genotype, fitness);
    }
}