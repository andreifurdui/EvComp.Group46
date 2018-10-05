import org.vu.contest.ContestEvaluation;

import java.util.*;

public class Population {
    
    public List<Individual> Population;
    public List<Population> Islands;

    private static Random rand = new Random();

    public Population(List<Individual> population){

        Collections.sort(population, Individual.Comparator);
        this.Population = population;

        this.Islands = new ArrayList<Population>();
        this.Islands.add(this);
    }

    public Population(List<Individual> population, List<Individual>[] islands, int islandCount){
        this.Population = population;

        this.Islands = new ArrayList<Population>();
        for (int i = 0; i < islandCount; i++) {
            this.Islands.add(new Population(islands[i]));
        }
    }

    public static Population InitPopulationWithFitness_Rand(Random rand, ContestEvaluation evaluation_, int populationSize) {
        List<Individual> pop = new ArrayList<Individual>();

        for (int i = 0; i < populationSize; i++) {
            Individual ind = Individual.Create_Rand(rand, evaluation_);
            pop.add(ind);
        }

        return new Population(pop);
    }

    public Population WithIslandization(int island_count) {
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

    public int GetPopulationSize(){
        return this.Population.size();
    }

    public Individual getRandomIndividual() {
        return Population.get(rand.nextInt(100));
    }
}
