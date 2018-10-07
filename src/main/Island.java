package main;

import org.vu.contest.ContestEvaluation;

import java.util.*;

public class Island {

    public int IslandIndex;
    public int Generation;
    public List<Individual> IslandPopulation;
    public IslandParameters IslandParameters;

    private static Random rand = new Random();

    public Island(int islandIndex, List<Individual> island, IslandParameters islandParameters)
    {
        this.IslandIndex = islandIndex;
        this.Generation = 1;
        this.IslandPopulation = island;
        this.IslandParameters = islandParameters;
    }

    public static List<String> getLogHeader()
    {
        List<String> header = new ArrayList<>();

        header.addAll(Individual.getLogHeader());
        header.add("IslandIndex");
        header.add("Generation");
        header.add("IslandIndex");
        header.addAll(main.IslandParameters.getHeaderLog());

        return header;
    }

    public void Evolve(ContestEvaluation eval)
    {
        List<Individual> children = MakeChildren(eval);

        List<Individual> pool = new ArrayList<>(IslandPopulation);
        pool.addAll(children);
        Collections.sort(pool, Individual.Comparator);

        List<Individual> elites = IslandParameters.ElitistSurvivors != 0 ?
                pool.subList(0, IslandParameters.ElitistSurvivors) :
                new ArrayList<>();

        List<Individual> survivors = Operators.TournamentSelect(pool.subList(IslandParameters.ElitistSurvivors, pool.size()), IslandParameters.TournamentSize, IslandPopulation.size() - elites.size());
        survivors.addAll(elites);
        Collections.sort(survivors, Individual.Comparator);

        this.IslandPopulation = survivors;
        this.Generation++;
    }

    private List<Individual> MakeChildren(ContestEvaluation eval)
    {
        List<Individual> children = new ArrayList<>();
        while(children.size() < (IslandPopulation.size() * .8))
        {
            double mutationDiceRoll = rand.nextDouble();

            if(mutationDiceRoll < IslandParameters.MutationChance)
            {
                Individual singleParent = Operators.TournamentSelect(IslandPopulation, IslandParameters.TournamentSize, 1).get(0);
                Individual child = singleParent.Mutate(IslandParameters.MutationStepSizeMultiplier);
                children.add(child);
                continue;
            }

            Individual mom = Operators.TournamentSelect(IslandPopulation, IslandParameters.TournamentSize,1).get(0);
            Individual dad;
            do {
                dad = Operators.TournamentSelect(IslandPopulation, IslandParameters.TournamentSize,1).get(0);
            } while(mom.equals(dad));

            double crossoverDiceRoll = rand.nextDouble();
            if(crossoverDiceRoll < IslandParameters.CrossoverMethodChance)
            { //do whole arithmetic xover
                Individual child = Operators.AritmeticalXover(mom, dad);
                children.add(child);
            }
            else
            { //do blend crossover
                Individual child = Operators.BlendCrossover(mom, dad);
                children.add(child);
            }
        }

        return children;
    }

    public List<String[]> Log(String epoch)
    {
        List<String[]> log = new ArrayList<>();
        List<String> metaIslandLog = GetIslandMeta();

        for (Individual ind: IslandPopulation)
        {
            List<String> indLog = ind.Log();
            indLog.addAll(metaIslandLog);
            indLog.add(epoch);

            log.add(indLog.toArray(new String[0]));
        }

        return log;
    }

    public List<String> GetIslandMeta()
    {
        List<String> islandMeta = new ArrayList<>();

        islandMeta.add(Integer.toString(IslandIndex));
        islandMeta.add(Integer.toString(Generation));
        islandMeta.addAll(IslandParameters.Log());

        return islandMeta;
    }
}
