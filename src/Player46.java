import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Properties;

public class Player46 implements ContestSubmission
{
	Random rnd_;
	ContestEvaluation evaluation_;
    private int evaluations_limit_;
	private int island_count = 4;
	private int population_size = 100;
	private int epoch_length = 100;
	public Player46()
	{
		rnd_ = new Random();
	}

	public static void main(String args[]){
		Player46 player = new Player46();
		try {
			player.setEvaluation(new SphereEvaluation());
			player.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		rnd_.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
		// Set evaluation problem used in the run
		evaluation_ = evaluation;

		// Get evaluation properties
		Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }
    
	public void run()
	{
		// Generate a new population with the "Population" class based on
		// (1) evaluation function, (2) size, (3) no. islands
		Population population = Population
				.InitPopulationWithFitness_Rand(evaluation_, population_size)
				.WithIslandization_EqualDistribution(island_count);
		// Set no. evaluations -> why start with 100?
		int evals = 100;

        while(evals<evaluations_limit_){

        	// Set the no. epochs to zero
			// Counter "epoch" will be incremented within the island evolution

        	int epochs = 0;
			for (int island = 0; island < island_count; island++) {
				population.Islands.get(island).Evolve(evaluation_);
			}


            double child[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
            // Check fitness of unknown fuction
            Double fitness = (double) evaluation_.evaluate(population.getRandomIndividual());
            evals++;
            // Select survivors
        }

	}
}
