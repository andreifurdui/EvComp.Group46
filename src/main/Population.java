package main;

import org.vu.contest.ContestEvaluation;

import java.util.*;
public class Population {

    public List<Individual> Population;
    public List<Island> Islands;

    public int Epoch;

    private static Random rand = new Random();

    public Population(List<Individual> population)
    {
        Collections.sort(population, Individual.Comparator);
        this.Population = population;

        this.Epoch = 1;
        this.Islands = new ArrayList<>();
    }

    public Population(List<Individual> population, List<Individual>[] islands, int islandCount)
    {
        this.Population = population;
        this.Islands = new ArrayList<>();
        for (int i = 0; i < islandCount; i++) {
            this.Islands.add(new Island(i, islands[i], IslandParameters.GetIslandParameters(islands[i].size(), i)));
        }
    }

    public static Population InitPopulationWithFitness_Rand(ContestEvaluation evaluation_, int populationSize) {
        List<Individual> pop = new ArrayList<Individual>();

        for (int i = 0; i < populationSize; i++) {
            Individual ind = Individual.Create_Rand(evaluation_);
            pop.add(ind);
        }

        return new Population(pop);
    }

    public Population ReshuffleIslands(int islandCount) {
        List<Individual> everyone = new ArrayList<>(Islands.get(0).IslandPopulation);
        for (int i = 1; i < Islands.size(); i++) {
            everyone.addAll(Islands.get(i).IslandPopulation);
        }

        Population newPopulation = new Population(everyone)
                .WithRandomIslandization(islandCount);

        return newPopulation;
    }

    public Population Migrate(int island_count)
    {
        List<Individual> island0to1 = Operators.TournamentSelect(Islands.get(0).IslandPopulation.subList(13, 25), 1, 3);
        Islands.get(0).IslandPopulation.removeAll(island0to1);

        List<Individual> island1to2 = Operators.TournamentSelect(Islands.get(1).IslandPopulation.subList(13, 25), 1, 3);
        Islands.get(1).IslandPopulation.removeAll(island1to2);

        List<Individual> island1to0 = Operators.TournamentSelect(Islands.get(1).IslandPopulation.subList(0, 13), 1, 3);
        Islands.get(1).IslandPopulation.removeAll(island1to0);

        List<Individual> island2to3 = Operators.TournamentSelect(Islands.get(2).IslandPopulation.subList(13, 25), 1, 3);
        Islands.get(2).IslandPopulation.removeAll(island2to3);

        List<Individual> island2to1 = Operators.TournamentSelect(Islands.get(2).IslandPopulation.subList(0, 13), 1, 3);
        Islands.get(2).IslandPopulation.removeAll(island2to1);

        List<Individual> island3to2 = Operators.TournamentSelect(Islands.get(3).IslandPopulation.subList(0, 13), 1, 3);
        Islands.get(3).IslandPopulation.removeAll(island3to2);

        Islands.get(0).IslandPopulation.addAll(island1to0);
        Islands.get(1).IslandPopulation.addAll(island0to1);
        Islands.get(1).IslandPopulation.addAll(island2to1);
        Islands.get(2).IslandPopulation.addAll(island1to2);
        Islands.get(2).IslandPopulation.addAll(island3to2);
        Islands.get(3).IslandPopulation.addAll(island2to3);

        for (int i = 0; i < island_count; i++) {
            Collections.sort(Islands.get(i).IslandPopulation, Individual.Comparator);
        }

        this.Epoch++;
        return this;
    }

    public Population WithIslandization(int island_count)
    {
        List<Individual>[] islands = new List[island_count];

        for (int i = 0; i < island_count; i++) {
            islands[i] = new ArrayList<Individual>();
        }

        for (int i = 0; i < this.Population.size(); i++) {
            double normalizedPosition = i / (double)25;
            double islandNumber = rand.nextGaussian()*1+normalizedPosition;
            int islandBucket = islandNumber < 1 ? 0 : islandNumber < 2 ? 1 : islandNumber < 3 ? 2 : 3;

            islands[islandBucket].add(this.Population.get(i));
        }

        return new Population(this.Population, islands, island_count);
    }

    public Population WithRandomIslandization(int island_count)
    {
        List<Individual>[] islands = new List[island_count];

        for (int i = 0; i < island_count; i++) {
            islands[i] = new ArrayList<Individual>();
        }

        for (int i = 0; i < this.Population.size(); i++) {
            int diceRoll = rand.nextInt(4);
            while(islands[diceRoll].size() == 25){
                diceRoll = rand.nextInt(4);
            }

            islands[diceRoll].add(this.Population.get(i));
        }

        return new Population(this.Population, islands, island_count);
    }

    public int GetPopulationSize()
    {
        return this.Population.size();
    }

    public Individual getRandomIndividual()
    {
        return Population.get(rand.nextInt(100));
    }

    public List<String[]> GetGenerationLog()
    {
        List<String[]> log = new ArrayList<>();

        if(this.Islands.size() > 1)
        {
            for (Island island: Islands)
            {
                 log.addAll(island.Log(Integer.toString(Epoch)));
            }
        }
        else
        {
            log.addAll(GetPopulationLog());
        }

        return log;
    }

    private List<String[]> GetPopulationLog()
    {
        List<String[]> log = new ArrayList<>();
        List<String> populationMeta = GetPopulationMeta();

        for (Individual ind: Population)
        {
            List<String> indLog = ind.Log();
            indLog.addAll(populationMeta);

            log.add(indLog.toArray(new String[0]));
        }

        return log;
    }

    private List<String> GetPopulationMeta()
    { // add your main.Population stats
        return null;
    }

    public String[] getLogHeader()
    { // keep same add order as in the data
        List<String> header = new ArrayList<>();

        if(this.Islands.size() > 1)
        {
            header.addAll(Island.getLogHeader());
            header.add("Epoch");
        }
        else
        {
            header.addAll(main.Population.getPopulationLogHeader());
        }

        return header.toArray(new String[0]);
    }

    public static List<String> getPopulationLogHeader()
    {
        List<String> header = new ArrayList<>();

        //add population data headers (variable names)

        return header;
    }
}
