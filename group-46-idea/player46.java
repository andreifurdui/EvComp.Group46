import org.jetbrains.annotations.NotNull;
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.*;

import static java.sql.Types.NULL;

public class player46 implements ContestSubmission
{
	private Random rnd_;
	private ContestEvaluation evaluation_;

    private int evaluations_limit_;
    private int pop_size = 100;
    private int indiv_dim = 10;
    private int[] min_max = {-5,5};
    private int fitness_index = indiv_dim;
    private double p_crossover = 0.9;
    private double p_mutation = 0.2;
    private double mutation_step_size = 0.5;

	private double pop [][] = new double[pop_size][indiv_dim + 1];

	private double parA[] = new	double[indiv_dim + 1];
	private double parB[] = new	double[indiv_dim + 1];

	private double[] offspringA = new double[indiv_dim + 1];
	private double[] offspringB = new double[indiv_dim + 1];

	public static void main(String[] args) {
		System.out.println("Test");
	}

	public player46()
	{
		rnd_ = new Random();
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

	private void tournament_selection() {
		int tournament_size = 5;
		int[] fighters = new int[2*tournament_size];
		boolean already_in;

		// sample x random parents out of population
		for (int battle = 0; battle < 2*tournament_size; battle++) {
			do {
				already_in = false;
				fighters[battle] = rnd_.nextInt(pop.length - 1);
				// making sure they are in the set only once
				for (int par_in_set_a = 0; par_in_set_a < tournament_size; par_in_set_a++) {
					if (fighters[par_in_set_a] == fighters[battle] && par_in_set_a!=battle) {
						already_in = true;
					}
				}
			}while (already_in);
		}


		// set first fighters as candidates
		parA = Arrays.copyOf(pop[fighters[0]],pop[fighters[0]].length);
		parB = Arrays.copyOf(pop[fighters[5]],pop[fighters[5]].length);

		// set tournament
		for (int battle = 1; battle < tournament_size; battle++) {

			if (pop[fighters[battle]][fitness_index] > parA[fitness_index]) {
				parA = Arrays.copyOf(pop[fighters[battle]],pop[fighters[battle]].length);
			}

			if (pop[fighters[battle+5]][fitness_index] > parB[fitness_index]) {
				parB = Arrays.copyOf(pop[fighters[battle+5]],pop[fighters[battle+5]].length);
			}
		}


		if(Arrays.equals(parA,parB)){
			System.out.println("Equal!!!");
		}
		System.out.println("Parent A");
		System.out.println(Arrays.toString(parA));
		System.out.println("Parent B");
		System.out.print(Arrays.toString(parB));
		System.out.println("\n");

	}

	private void one_point_crossover(){

		if(p_crossover >= rnd_.nextDouble()) {
			// get random crossover point
			int co_indx = rnd_.nextInt(indiv_dim - 1) + 1;

			// cut the genes at crossover point
			double[] parA_genes1 = Arrays.copyOfRange(parA, 0, co_indx);
			double[] parA_genes2 = Arrays.copyOfRange(parA, co_indx, parA.length - 1);
			double[] parB_genes1 = Arrays.copyOfRange(parB, 0, co_indx);
			double[] parB_genes2 = Arrays.copyOfRange(parB, co_indx, parB.length - 1);

			// add the pieces to create new offspring
			System.arraycopy(parA_genes1, 0, offspringA, 0, parA_genes1.length);
			System.arraycopy(parB_genes2, 0, offspringA, parA_genes1.length, parB_genes2.length);

			System.arraycopy(parB_genes1, 0, offspringB, 0, parB_genes1.length);
			System.arraycopy(parA_genes2, 0, offspringB, parB_genes1.length, parA_genes2.length);
		}
		else {
			System.arraycopy(parA, 0, offspringA, 0, parA.length-1);
			System.arraycopy(parB, 0, offspringB, 0, parB.length-1);
		}
	}

	private void nonuniform_mutation(){
		// create delta vectors
		double[] delta_A = new double[offspringA.length - 1];
		double[] delta_B = new double[offspringB.length - 1];

		for (int i=0; i < offspringA.length-1; i++){
			if(p_mutation >= rnd_.nextDouble()) {
				// get delta for A and add to offspring
				delta_A[i] = rnd_.nextGaussian()*mutation_step_size;
				sum_in_range(delta_A, i, offspringA);
			}

			if(p_mutation >= rnd_.nextDouble()) {
				// get delta for B and add to offspring
				delta_B[i] = rnd_.nextGaussian()*mutation_step_size;
				sum_in_range(delta_B, i, offspringB);
			}
		}
	}

	private void sum_in_range(@NotNull double[] delta, int i, @NotNull double[] offspring) {
		// if in range add
		if (min_max[0] < offspring[i] + delta[i] &&  offspring[i] + delta[i] < min_max[1]){
			offspring[i] += delta[i];
		}
		// if under lower bound set to min
		else if(min_max[0] > offspring[i] + delta[i]){
			offspring[i] = min_max[0];
		}
		// if above upper bound set to max
		else if(min_max[1] < offspring[i] + delta[i]){
			offspring[i] = min_max[1];
		}
	}

	private void ranking_selection(){
		// put offspring A into sorted population
		pop[pop.length-1] = Arrays.copyOf(offspringA, offspringA.length);
		pop[pop.length-2] = Arrays.copyOf(offspringB, offspringB.length);
	}

	public void run()
	{
		// Run your algorithm here
        int evals = 0;

        // init population uniformly randomly within [-5,5]
		for(int individual = 0; individual < pop_size; individual ++){
			for(int gene = 0; gene < indiv_dim; gene++){
				// initialize
				pop[individual][gene] = min_max[0] + rnd_.nextFloat() * (min_max[1] - min_max[0]);
			}
			// calculate fitness
			double[] genotype = Arrays.copyOfRange(pop[individual], 0, pop[individual].length-1);
			pop[individual][fitness_index] = (double) evaluation_.evaluate(genotype);
			evals++;
		}

        while(evals<evaluations_limit_){
			// sort pop from highest to lowest fittest
			Arrays.sort(pop, Collections.reverseOrder(Comparator.comparingDouble(a -> a[fitness_index])));

            // select parent through method x
			tournament_selection();

			// create offspring from selected parents by method x
			one_point_crossover();

			// mutate offspring
			nonuniform_mutation();

			// calculate fitness of offspring A & B
			double[] genotypeA = Arrays.copyOfRange(offspringA, 0, offspringA.length-1);
			offspringA[fitness_index] = (double) evaluation_.evaluate(genotypeA);
			evals++;

			// calculate fitness of offspring A & B
			double[] genotypeB = Arrays.copyOfRange(offspringB, 0, offspringB.length-1);
			offspringB[fitness_index] = (double) evaluation_.evaluate(genotypeB);
			evals++;

			// select individuals to be replaced by method x
			ranking_selection();
        }
	}

}