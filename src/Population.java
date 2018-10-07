import org.vu.contest.ContestEvaluation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Population {

    public List<Individual> Population;
    public List<Island> Islands;

    private static Random rand = new Random();

    public Population(List<Individual> population) {
        Collections.sort(population, Individual.Comparator);
        this.Population = population;

        this.Islands = new ArrayList<>();
    }

    public Population(List<Individual> population, List<Individual>[] islands, int islandCount) {
        this.Population = population;

        this.Islands = new ArrayList<>();
        for (int i = 0; i < islandCount; i++) {
            this.Islands.add(new Island(islands[i], IslandParameters.GetIslandParameters(islands[i].size(), i)));
        }
    }

    //Initialises each individual in pop with random genes
    public static Population InitPopulationWithFitness_Rand(ContestEvaluation evaluation_, int populationSize) {
        List<Individual> pop = new ArrayList<Individual>();

        for (int i = 0; i < populationSize; i++) {
            Individual ind = Individual.Create_Rand(evaluation_);
            pop.add(ind);
        }

        return new Population(pop);
    }

    //Divides population evenly into subpopulations for each island
    public Population WithIslandization_EqualDistribution(int island_count) {
        //Creating subpopulation: Each island gets list of individuals
        List<Individual>[] islands = new List[island_count];


        for (int i = 0; i < island_count; i++) {
            islands[i] = new ArrayList<Individual>();
        }
        //split random individuals equally to islands
        for (int i = 0; i < this.Population.size(); i++) {
            double normalizedPosition = i / (double) (this.Population.size / island_count);
            double islandNumber = rand.nextGaussian() * 1 + normalizedPosition;
            int islandBucket = islandNumber < 1 ? 0 : islandNumber < 2 ? 1 : islandNumber < 3 ? 2 : 3;

            islands[islandBucket].add(this.Population.get(i));
        }

        return new Population(this.Population, islands, island_count);
    }

    //Structure the subpopulations into pyramid scheme
    //Number of idividuals gradually increases over the islands with constant ratio
    public Population Islandization_PyramidDistribution(int island_count, double pyramid_ratio) {

        //example for 3 islands {x,y,z} and total_pop P:
        //z = P / (1+f+f²)
        //y = z * f
        //x = z * f²

        int i = 0;
        double resulting_factor = 0;
        while (i < island_count)
        {
            resulting_factor += (pyramid_ratio**i);
            i++;
        }


        int[] pop_distribution = new int[island_count];
        pop_distribution[0] = (int) Math.ceil(this.Population.size / resulting_factor); //assign smallest subpop to first island
        for(int i = 1, i<island_count, i++)
        {
            pop_distribution[i] = (int) Math.ceil(pop_distribution[i-1]*pyramid_ratio);
        }

        //Creating subpopulation: Each island gets list of individuals
        List<Individual>[] islands = new List[island_count];

        int indiv_id = 0;
        for(int i = 0; i < island_count; i++) {
            islands[i] = new ArrayList<Individual>();
            //Fill subpopulations with individuals until limit (from pop_distribution) is reached
            while(islands[i].length < pop_distribution[i]) {

                islands[i].add(this.Population.get(indiv_id));
                indiv_id++;
            }
        }

    return new Population(this.Population, islands, island_count)
    }

    //Group subpopulations based on fitness
    public Population Islandization_FitnessDistribution(int island_count)
    {
        //Sort population by fitness
        //Divide by island count
        //Assign each island a subpopulation
    }

    //Distribute population randomly over the given islands
    //Size of subpopulations specified by upper & lower limit
    public Population Islandization_RandomDistribution(int island_count, int upper_limit, int lower_limit)
    {
        //Creating subpopulation: Each island gets list of individuals
        List<Individual>[] islands = new List[island_count];

        //
        int pop;    //
        int remaining_pop = this.Population.size - island_count*lower_limit;
        int upper = upper_limit;
        if(remaining_pop<upper){
            upper = remaining_pop;
        }

        int indiv_id = 0;
        for(int i = 0; i < island_count; i++) {
            islands[i] = new ArrayList<Individual>();
            //Fill subpopulations with individuals until determined population size is reached
            pop = ThreadLocalRandom.current().nextInt(lower_limit, upper + 1); // nextInt is normally exclusive of the top value,
            while(islands[i].length < pop) {

                islands[i].add(this.Population.get(indiv_id));
                indiv_id++;
            }
            upper = remaining_pop - pop;
        }

        return new Population(this.Population, islands, island_count)
    }

    public int GetPopulationSize()
    {
        return this.Population.size();
    }

    public Individual getRandomIndividual()
    {
        return Population.get(rand.nextInt(this.Population.size));
    }
}
