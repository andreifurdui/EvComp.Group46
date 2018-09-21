import java.util.*;

public class Population {
    public static final int PopulationSize = 100;
    
    public List<Individual> Population;

    public Population(List<Individual> population){
        this.Population = population;
    }

    public static Population InitPopulation_Rand(Random rand) {
        List<Individual> pop = new ArrayList<Individual>();

        for (int i = 0; i < PopulationSize; i++) {
            Individual ind = Individual.Create_Rand(rand);
            pop.add(ind);
        }

        return new Population(pop);
    }
}
