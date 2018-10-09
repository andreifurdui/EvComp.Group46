package main;

import java.util.*;

public class Operators {

    private static Random rand;

    public static List<Individual> TournamentSelect(List<Individual> competitors, int tournamentSize, int championCount)
    {
        List<Individual> champions = new ArrayList<>(championCount);
        Set<Integer> selected = new HashSet<Integer>();

        while(champions.size() < championCount)
        {
            int best = competitors.size()+1;
            for (int i = 0; i < tournamentSize; i++)
            {
                int drawn;
                do
                {
                    drawn =  rand.nextInt(competitors.size());
                } while(selected.contains(drawn));
                best = drawn < best ? drawn : best;
            }

            if(!selected.contains(best))
            {
                champions.add(competitors.get(best));
                selected.add(best);
            }
        }

        return champions;
    }

    public static List<Individual> BlendXover(Individual mom, Individual dad)
    {
        double[] genotype1 = new double[10];
        double[] genotype2 = new double[10];
        double[] mutationStepSizes1 = new double[10];
        double[] mutationStepSizes2 = new double[10];
        double bias = 0.5;

        for (int i = 0; i < 10; i++) {
            double dist = Math.abs(mom.Genes.Values[i] - dad.Genes.Values[i]);

            double diceRoll = rand.nextDouble();
            genotype1[i] = diceRoll * dist + Math.min(mom.Genes.Values[i], dad.Genes.Values[i]);
            mutationStepSizes1[i] = diceRoll * dist + Math.min(mom.Genes.MutationStepSize[i], dad.Genes.MutationStepSize[i]);
            diceRoll = rand.nextDouble();
            genotype2[i] = diceRoll * dist + Math.min(mom.Genes.Values[i], dad.Genes.Values[i]);
            mutationStepSizes2[i] = diceRoll * dist + Math.min(mom.Genes.MutationStepSize[i], dad.Genes.MutationStepSize[i]);
        }

        List<Individual> children = new ArrayList<>();
        children.add(new Individual(new Genotype(genotype1, mutationStepSizes1)));
        children.add(new Individual(new Genotype(genotype2, mutationStepSizes2)));

        return children;
    }

    public static List<Individual> AritmeticalXover(Individual mom, Individual dad)
    {
        double[] genotype1 = new double[10];
        double[] genotype2 = new double[10];
        double[] mutationStepSizes1 = new double[10];
        double[] mutationStepSizes2 = new double[10];

        for (int i = 0; i < 10; i++) {
            double bias = rand.nextDouble();
            genotype1[i] = bias * mom.Genes.Values[i] + (1 - bias) * dad.Genes.Values[i];
            genotype2[i] = (1-bias) * mom.Genes.Values[i] + bias * dad.Genes.Values[i];
            mutationStepSizes1[i] = bias * mom.Genes.MutationStepSize[i] + (1 - bias) * dad.Genes.MutationStepSize[i];
            mutationStepSizes2[i] = (1-bias) * mom.Genes.MutationStepSize[i] + bias * dad.Genes.MutationStepSize[i];
        }

        List<Individual> children = new ArrayList<>();
        children.add(new Individual(new Genotype(genotype1, mutationStepSizes1)));
        children.add(new Individual(new Genotype(genotype2, mutationStepSizes2)));

        return children;
    }

    public static void SetRandom(Random rnd_)
    {
        rand = rnd_;
    }
}