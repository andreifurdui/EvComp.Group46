import java.util.*;

public class Operators {

    private static Random rand = new Random();

    public static List<Individual> TournamentSelect(List<Individual> competitors, int tournamentSize, int championCount)
    {
        List<Individual> champions = new ArrayList<>(championCount);
        Set<Integer> selected = new HashSet<Integer>();

        while(champions.size() < championCount)
        {
            int best = competitors.size()+1;
            for (int i = 0; i < tournamentSize; i++)
            {
                int drawn =  rand.nextInt(competitors.size());
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

    public static Individual AritmeticalXover(Individual mom, Individual dad)
    {
        double[] genotype = new double[10];
        double bias = rand.nextDouble();

        for (int i = 0; i < 10; i++) {
            genotype[i] = bias * mom.Genes.Values[i] + (1 - bias) * dad.Genes.Values[i];
        }
        double childMutationStep = bias * mom.MutationStepSize + (1 - bias) * dad.MutationStepSize;

        return new Individual(genotype, childMutationStep);
    }

    public static Individual BlendCrossover(Individual mom, Individual dad)
    {
        double[] genotype = new double[10];
        for (int i = 0; i < 10; i++) {
            double distance = Math.abs(mom.Genes.Values[i] - dad.Genes.Values[i]);
            double directionDiceRoll = rand.nextDouble() - .5;
            genotype[i] = directionDiceRoll < 0 ?
                    mom.Genes.Values[i] - distance * .5 :
                    mom.Genes.Values[i] + distance * .5;
        }

        double mutationStepSizeDifference = Math.abs(mom.MutationStepSize - dad.MutationStepSize);
        double childMutationStep =  rand.nextDouble() - .5 < 0 ?
                mom.MutationStepSize - mutationStepSizeDifference * .5 :
                mom.MutationStepSize + mutationStepSizeDifference * .5;

        return new Individual(genotype, childMutationStep);
    }
}
