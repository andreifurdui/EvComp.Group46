package main;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Properties;

public class Player46 implements ContestSubmission
{
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

	Random rnd_;
	ContestEvaluation evaluation_;
    private int evaluations_limit_;
	private int island_count = 4;
	private int population_size = 100;
	private int epoch_length = 20;
	public Player46()
	{
		rnd_ = new Random();
	}

	public static void main(String args[]){
		Player46 player = new Player46();
		try {

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
		Population population = Population
				.InitPopulationWithFitness_Rand(evaluation_, population_size)
				.WithRandomIslandization(island_count);

		Logger islandLog = new Logger("Island Evolution" + sdf.format(new Timestamp(System.currentTimeMillis())));
		islandLog.AddRow(population.getLogHeader());

		int evals = 100;

        while(evals + 100*epoch_length <evaluations_limit_)
        {
            int epochs = 0;
            while(epochs < epoch_length)
			{
				for (int island = 0; island < island_count; island++) {
					population.Islands.get(island).Evolve(evaluation_);
				}
				islandLog.AddRows(population.GetGenerationLog());
				evals += 100;
				epochs++;
			}

			population = population.Migrate(island_count);
		}

		islandLog.WriteLog();
	}
}
