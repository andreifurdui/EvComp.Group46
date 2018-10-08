package main;

import org.vu.contest.ContestEvaluation;

import java.util.*;
public class Population {

    public List<Individual> Population;
    public List<Island> Islands;

    public int Epoch;
    private Random rand;

    public Population(List<Individual> population, List<Individual>[] islands, int islandCount, Random rnd)
    {
        this.Population = population;

        this.Epoch = 1;
        this.Islands = new ArrayList<>();
        for (int i = 0; i < islandCount; i++) {
            Collections.sort(islands[i], Individual.Comparator);
            this.Islands.add(new Island(i, islands[i], IslandParameters.GetIslandParameters(islands[i].size(), i)));
        }

        this.rand = rnd;
    }

    public static Population Create(int population_size, int island_count, ContestEvaluation evaluation_, Random rnd_) {
        List<Individual> pop = new ArrayList<Individual>();

        List<Individual>[] islands = new List[island_count];
        for (int i = 0; i < island_count; i++) {
            islands[i] = new ArrayList<Individual>();
        }

        for (int i = 0; i < population_size; i++) {
            Individual ind = Individual.Create(evaluation_, rnd_);
            pop.add(ind);

            int diceRoll = rnd_.nextInt(4);
            while(islands[diceRoll].size() == population_size / island_count){
                diceRoll = rnd_.nextInt(4);
            }

            islands[diceRoll].add(ind);
        }

        return new Population(pop, islands, island_count, rnd_);
    }

    public List<String[]> GetGenerationLog()
    {
        List<String[]> log = new ArrayList<>();

        for (Island island: Islands)
        {
             log.addAll(island.Log(Integer.toString(Epoch)));
        }

        return log;
    }

    public String[] getLogHeader()
    { // keep same add order as in the data
        List<String> header = new ArrayList<>();

        header.addAll(Island.getLogHeader());
        header.add("Epoch");

        return header.toArray(new String[0]);
    }
}
