package main;

import org.vu.contest.ContestEvaluation;

import java.util.*;

public class Individual {
    public Genotype Genes;
    public double Fitness;

    public static Comparator<Individual> Comparator = new SortByFitness();
    private static Random rand;
    private static ContestEvaluation eval;

    public Individual(Genotype genotype){
        this.Genes = genotype;
        this.Fitness = (double)eval.evaluate(genotype.Values);
    }

    public static Individual CreateRandom(){
        Genotype genotype = Genotype.CreateRandom();

        return new Individual(genotype);
    }

    public static void SetRandom(Random rnd_)
    {
        rand = rnd_;
    }

    public static void SetEvaluation(ContestEvaluation evaluation_)
    {
        eval = evaluation_;
    }

    public Individual Mutate(IslandParameters parameters)
    {
        Genotype mutatedGenotype = this.Genes.Mutate(parameters);

        return new Individual(mutatedGenotype);
    }

    public static List<String> getLogHeader()
    {
        List<String> header = new ArrayList<>();

        header.addAll(Genotype.getLogHeader());
        header.add("Fitness");
        header.add("MutationStepSize");

        return header;
    }

    public List<String> Log()
    {
        List<String> log = new ArrayList<>(Genes.Log());

        log.add(Double.toString(Fitness));

        return log;
    }

    static class SortByFitness implements Comparator<Individual>{

        @Override
        public int compare(Individual first, Individual second) {
            return -Double.compare(first.Fitness, second.Fitness);
        }
    }
}