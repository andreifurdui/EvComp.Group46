package main;

import org.vu.contest.ContestEvaluation;

import java.util.*;

public class Individual {
    public Genotype Genes;
    public double Fitness;

    public static Comparator<Individual> Comparator = new SortByFitness();
    private static Random rand;
    private static ContestEvaluation eval = null;

    public Individual(Genotype genotype){
        this.Genes = genotype;
        this.Fitness = (double)eval.evaluate(genotype.Values);
    }

    public static Individual Create(ContestEvaluation evaluation_, Random rnd_){
        if(eval == null)
            eval = evaluation_;
        if(rand == null)
            rand = rnd_;

        Genotype genotype = Genotype.Create(rnd_);

        return new Individual(genotype);
    }

    public Individual Mutate(IslandParameters parameters)
    {
        Genotype mutatedGenotype = this.Genes.Mutate(parameters, rand);

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
