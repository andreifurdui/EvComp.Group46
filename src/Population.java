import org.vu.contest.ContestEvaluation;

import java.util.*;

public class Population {
    public static final int PopulationSize = 100;
    
    public List<Individual> Population;

    public Population(List<Individual> population){
        this.Population = population;
    }

    public static Population InitPopulationWithFitness_Rand(Random rand, ContestEvaluation evaluation_) {
        List<Individual> pop = new ArrayList<Individual>();

        for (int i = 0; i < PopulationSize; i++) {
            Individual ind = Individual.Create_Rand(rand, evaluation_);
            pop.add(ind);
        }

        return new Population(pop);
    }
}
