import java.util.SortedSet;
import java.util.TreeSet;

public class Population
{
    private static final int populationSize = 100;

    public SortedSet<Individual> Population;

    public Population(SortedSet<Individual> population){
        this.Population = population;
    }

    public static Population InitializeRandomly(Random random, int min, int max)
    {
        SortedSet population = new TreeSet<Individual>();
        while(population.size() < populationSize){
            Individual individual = Individual.InitializeRandomly(random, min, max);

            population.add(individual);
        }

        return new Population(population);
    }
}