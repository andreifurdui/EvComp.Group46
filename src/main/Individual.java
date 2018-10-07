package main;

import org.vu.contest.ContestEvaluation;

import java.util.*;

public class Individual {
    public Genotype Genes;
    public double Fitness;
    public double MutationStepSize;

    public static Comparator<Individual> Comparator = new SortByFitness();
    private static Random rand = new Random();
    private static ContestEvaluation eval = null;

    public Individual(Genotype genotype, double fitness){
        this.Genes = genotype;
        this.Fitness = fitness;
        this.MutationStepSize = rand.nextGaussian() - .5;
    }

    public Individual(double[] geneValues, double mutationStepSize)
    {
        this.Genes = new Genotype(geneValues);
        this.Fitness = (double)eval.evaluate(Genes.Values);
        this.MutationStepSize = mutationStepSize;
    }

    public static Individual Create_Rand(ContestEvaluation evaluation_){
        if(eval == null)
            eval = evaluation_;

        Genotype genotype = Genotype.Create_Rand(rand);

        return new Individual(genotype, (double)evaluation_.evaluate(genotype.Values));
    }

    public static List<String> getLogHeader()
    {
        List<String> header = new ArrayList<>();

        header.addAll(Genotype.getLogHeader());
        header.add("Fitness");
        header.add("MutationStepSize");

        return header;
    }

    public Individual Mutate(double mutationStepSizeMultiplier) {
        double[] mutatedGenes = new double[10];
        for (int i = 0; i < 10; i++) {
            mutatedGenes[i] = this.Genes.Values[i] + rand.nextGaussian() * mutationStepSizeMultiplier + MutationStepSize;
        }

        return new Individual(mutatedGenes, this.MutationStepSize);
    }

    public List<String> Log()
    {
        List<String> log = new ArrayList<>(Genes.Log());

        log.add(Double.toString(Fitness));
        log.add(Double.toString(MutationStepSize));

        return log;
    }

    static class SortByFitness implements Comparator<Individual>{

        @Override
        public int compare(Individual first, Individual second) {
            return -Double.compare(first.Fitness, second.Fitness);
        }
    }
}
