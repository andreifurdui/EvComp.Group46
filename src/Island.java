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

    public Island Evolve(ContestEvaluation eval)
    {
        List<Individual> children = MakeChildren(eval);

        List<Individual> pool = new ArrayList<>(IslandPopulation);
        pool.addAll(children);
        Collections.sort(pool, Individual.Comparator);

        return new Island(pool.subList(0, IslandPopulation.size()), this.IslandParameters);
    }

    //creates list of "individuals" based on the given evaluation function
    //either (1) mutate an individual, (2) two-parent arithmetic xover, (3) two-parent blend xover
    private List<Individual> MakeChildren(ContestEvaluation eval)
    {
        List<Individual> children = new ArrayList<>();
        while(children.size() < IslandPopulation.size())
        {
            double mutationDiceRoll = rand.nextDouble();
            //If random value is lower than mutation rate, create a new child by
            //Selecting single parent through tournament selection and mutate that individual
            if(mutationDiceRoll < IslandParameters.MutationChance)
            {
                Individual singleParent = Operators.TournamentSelect(IslandPopulation, IslandParameters.TournamentSize);
                Individual child = singleParent.Mutate(IslandParameters.MutationStepSizeMultiplier);
                children.add(child);
                continue;
            }

            //Otherwise: select two different individuals
            //Create child according to xover operator
            //Where does mutation take place?
            Individual mom = Operators.TournamentSelect(IslandPopulation, IslandParameters.TournamentSize);
            Individual dad;
            do {
                dad = Operators.TournamentSelect(IslandPopulation, IslandParameters.TournamentSize);
            } while(mom.equals(dad));

            double crossoverDiceRoll = rand.nextDouble();
            if(crossoverDiceRoll < IslandParameters.CrossoverMethodChance)
            { //do whole arithmetic xover
                Individual child = Operators.AritmeticalXover(mom, dad);
                children.add(child);
            }
            else
            { //do blend xover
                Individual child = Operators.BlendCrossover(mom, dad);
                children.add(child);
            }
        }

        return children;
    }
}
