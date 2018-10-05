import org.vu.contest.ContestEvaluation;

import java.util.*;

public class Island {

    public List<Individual> IslandPopulation;
    public IslandParameters IslandParameters;

    private static Random rand = new Random();

    public Island(List<Individual> island, IslandParameters islandParameters)
    {
        this.IslandPopulation = island;
        this.IslandParameters = islandParameters;
    }

    public Island Evolve(ContestEvaluation eval) {
        List<Individual> children = MakeChildren(eval);

        List<Individual> pool = new ArrayList<>(IslandPopulation);
        pool.addAll(children);
        Collections.sort(pool, Individual.Comparator);

        return new Island(pool.subList(0, IslandPopulation.size()), this.IslandParameters);
    }

    private List<Individual> MakeChildren(ContestEvaluation eval) {
        List<Individual> children = new ArrayList<>();
        while(children.size() < IslandPopulation.size())
        {
            double mutationDiceRoll = rand.nextDouble();

            if(mutationDiceRoll < IslandParameters.MutationChance)
            {
                Individual singleParent = Operators.TournamentSelect(IslandPopulation, IslandParameters.TournamentSize);
                Individual child = singleParent.Mutate(IslandParameters.MutationStepSizeMultiplier, eval);
                children.add(child);
                continue;
            }


        }

        return children;
    }
}
