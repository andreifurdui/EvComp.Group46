import org.jetbrains.annotations.NotNull;
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.lang.*;

import java.util.*;

import static java.sql.Types.NULL;

public class player46 implements ContestSubmission
{
	private Random rnd_;
	private ContestEvaluation evaluation_;
	private int evals = 0;

    private int evaluations_limit_;
    private int pop_size = 100;
    private int indiv_dim = 10;
    private double[] min_max = {-5,5};
    private int fitness_index = indiv_dim;
    private double p_crossover = 0.65;
    private double p_mutation = 0.95;
    private double mutation_step_size_start = 1;
    private double mutation_tau_apos = 1/Math.sqrt(2*indiv_dim)*1.5;
    private double mutation_tau = 1/Math.sqrt(2*Math.sqrt(indiv_dim))*0;

    private double alpha = 0.5;

    private int tournament_size_parent_selection = 2;
	private int tournament_size_survival_selection = 2;
	private double weaker_offspring_survival_prop = 0.3;

	private int local_search_budget = 2;
	private double p_mutation_ls = 1;


	private double eval_search_split = 0.8;
	private int ls_in_best = 2;
	private double p_mutation_ls_end = 0.8;
	private double mutation_step_size_ls_end = 0.001;

	private ArrayList<Individual> pops = new ArrayList<>();

	private int parA;
	private int parB;

	private Individual offspringA;
	private Individual offspringB;

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

	private void tournament_selection(boolean for_parent_not_survival) {
		int tournament_size;

		if (for_parent_not_survival){
			tournament_size = tournament_size_parent_selection;
		}
		else {
			tournament_size = tournament_size_survival_selection;
		}

		int[] fighters = new int[2*tournament_size];
		boolean already_in;

		// sample x random parents out of population
		for (int battle = 0; battle < 2*tournament_size; battle++) {
			do {
				already_in = false;
				fighters[battle] = rnd_.nextInt(pops.size() - 1);
				// making sure they are in the set only once
				for (int par_in_set_a = 0; par_in_set_a < tournament_size; par_in_set_a++) {
					if (fighters[par_in_set_a] == fighters[battle] && par_in_set_a!=battle) {
						already_in = true;
					}
				}
			}while (already_in);
		}

		if (for_parent_not_survival){
			// set tournament
			int best_fighterA = fighters[0];
			int best_fighterB = fighters[tournament_size];

			// set tournament
			for (int battle = 1; battle < tournament_size; battle++) {

				if (pops.get(fighters[battle]).fitness > pops.get(best_fighterA).fitness) {
					best_fighterA = fighters[battle];
				}

				if (pops.get(fighters[battle+tournament_size]).fitness > pops.get(best_fighterB).fitness) {
					best_fighterB = fighters[battle + tournament_size];
				}
			}
			parA=best_fighterA;
			parB=best_fighterB;

		}
		else {
			// set tournament
			int worst_fighterA = fighters[0];
			int worst_fighterB = fighters[tournament_size];

			for (int battle = 1; battle < tournament_size; battle++) {

				if (pops.get(fighters[battle]).fitness < pops.get(worst_fighterA).fitness) {
					worst_fighterA=fighters[battle];
				}

				if (pops.get(fighters[battle+tournament_size]).fitness < pops.get(worst_fighterB).fitness) {
					worst_fighterB=fighters[battle+tournament_size];
				}
			}

			if(rnd_.nextDouble() <= weaker_offspring_survival_prop | pops.get(worst_fighterA).fitness<=offspringA.fitness) {
				pops.set(worst_fighterA, offspringA);
			}

			if(rnd_.nextDouble() <= weaker_offspring_survival_prop | pops.get(worst_fighterB).fitness<=offspringB.fitness) {
				pops.set(worst_fighterB, offspringB);
			}
		}
	}

	private void blend_crossover(){
		if(p_crossover >= rnd_.nextDouble()) {

			offspringA = new Individual(indiv_dim, rnd_);
			offspringB = new Individual(indiv_dim, rnd_);

			// get random crossover point
			double u = rnd_.nextDouble();

			for(int gene=0; gene<offspringA.genotype.length; gene++) {
				double gamma = (1-2*alpha)*u-alpha;
				offspringA.genotype[gene] = (1-gamma)*pops.get(parA).genotype[gene]+gamma*pops.get(parB).genotype[gene];
				offspringB.genotype[gene] = (1-gamma)*pops.get(parB).genotype[gene]+gamma*pops.get(parA).genotype[gene];

				offspringA.mutation_step_size[gene] = (1-gamma)*pops.get(parA).mutation_step_size[gene]+gamma*pops.get(parB).mutation_step_size[gene];
				offspringB.mutation_step_size[gene] = (1-gamma)*pops.get(parB).mutation_step_size[gene]+gamma*pops.get(parA).mutation_step_size[gene];
			}
		}
		else {
			offspringA = pops.get(parA);
			offspringB = pops.get(parB);
		}
	}

	private void avergae_crossover(){
		if(p_crossover >= rnd_.nextDouble()) {

			offspringA = new Individual(indiv_dim, rnd_);
			offspringB = new Individual(indiv_dim, rnd_);

			for(int gene=0; gene<offspringA.genotype.length; gene++) {
				offspringA.genotype[gene] = (1-alpha)*pops.get(parA).genotype[gene]+alpha*pops.get(parB).genotype[gene];
				offspringB.genotype[gene] = (1-alpha)*pops.get(parB).genotype[gene]+alpha*pops.get(parA).genotype[gene];

				offspringA.mutation_step_size[gene] = (1-alpha)*pops.get(parA).mutation_step_size[gene]+alpha*pops.get(parB).mutation_step_size[gene];
				offspringB.mutation_step_size[gene] = (1-alpha)*pops.get(parB).mutation_step_size[gene]+alpha*pops.get(parA).mutation_step_size[gene];
			}
		}
		else {
			offspringA = pops.get(parA);
			offspringB = pops.get(parB);
		}
	}

	private void one_point_crossover(){

		if(p_crossover >= rnd_.nextDouble()) {
			offspringA = new Individual(indiv_dim, rnd_);
			offspringB = new Individual(indiv_dim, rnd_);

			// get random crossover point
			int co_index = rnd_.nextInt(indiv_dim - 1) + 1;

			// cut the genes at crossover point
			double[] parA_genes1 = Arrays.copyOfRange(pops.get(parA).genotype, 0, co_index);
			double[] parA_genes2 = Arrays.copyOfRange(pops.get(parA).genotype, co_index, pops.get(parA).genotype.length);
			double[] parB_genes1 = Arrays.copyOfRange(pops.get(parB).genotype, 0, co_index);
			double[] parB_genes2 = Arrays.copyOfRange(pops.get(parB).genotype, co_index, pops.get(parB).genotype.length);

			// cut the mutation step sizes at crossover point
			double[] parA_sigma1 = Arrays.copyOfRange(pops.get(parA).mutation_step_size, 0, co_index);
			double[] parA_sigma2 = Arrays.copyOfRange(pops.get(parA).mutation_step_size, co_index, pops.get(parA).mutation_step_size.length);
			double[] parB_sigma1 = Arrays.copyOfRange(pops.get(parB).mutation_step_size, 0, co_index);
			double[] parB_sigma2 = Arrays.copyOfRange(pops.get(parB).mutation_step_size, co_index, pops.get(parB).mutation_step_size.length);


			// add the pieces to create new offspring
			System.arraycopy(parA_genes1, 0, offspringA.genotype, 0, parA_genes1.length);
			System.arraycopy(parB_genes2, 0, offspringA.genotype, parA_genes1.length, parB_genes2.length);
			System.arraycopy(parA_sigma1, 0, offspringA.mutation_step_size, 0, parA_sigma1.length);
			System.arraycopy(parB_sigma2, 0, offspringA.mutation_step_size, parA_sigma1.length, parB_sigma2.length);


			System.arraycopy(parB_genes1, 0, offspringB.genotype, 0, parB_genes1.length);
			System.arraycopy(parA_genes2, 0, offspringB.genotype, parB_genes1.length, parA_genes2.length);
			System.arraycopy(parB_sigma1, 0, offspringB.mutation_step_size, 0, parB_sigma1.length);
			System.arraycopy(parA_sigma2, 0, offspringB.mutation_step_size, parB_sigma1.length, parA_sigma2.length);

		}
		else {
			offspringA = pops.get(parA);
			offspringB = pops.get(parB);
		}
	}

	private void nonuniform_mutation(){
		// create delta vectors

		double[] delta_A = new double[offspringA.mutation_step_size.length];
		double[] delta_B = new double[offspringB.mutation_step_size.length];

		double const_gaussian_for_A = rnd_.nextGaussian();
		double const_gaussian_for_B = rnd_.nextGaussian();

		for (int i=0; i < offspringA.genotype.length; i++){
			if(p_mutation >= rnd_.nextDouble()) {
				// calculate new sigma
				offspringA.mutation_step_size[i] *= Math.exp(mutation_tau_apos*const_gaussian_for_A + mutation_tau*rnd_.nextGaussian());
				// get delta for A and add to offspring
				delta_A[i] = offspringA.mutation_step_size[i]*rnd_.nextGaussian();
				sum_in_range(delta_A, i, offspringA.genotype);
			}

			if(p_mutation >= rnd_.nextDouble()) {
				// calculate new sigma
				offspringB.mutation_step_size[i] *= Math.exp(mutation_tau_apos*const_gaussian_for_B + mutation_tau*rnd_.nextGaussian());
				// get delta for B and add to offspring
				delta_B[i] = offspringB.mutation_step_size[i]*rnd_.nextGaussian();
				sum_in_range(delta_B, i, offspringB.genotype);
			}
		}
	}

	private void local_search(){

		double[] delta_A = new double[offspringA.mutation_step_size.length];
		double[] delta_B = new double[offspringB.mutation_step_size.length];

		boolean local_search_A_done = false;
		int budget_A = 0;
		int budget_B = 0;
		boolean local_search_B_done = false;

		Individual offspringA_ls = new Individual(indiv_dim, rnd_);
		Individual offspringB_ls = new Individual(indiv_dim, rnd_);

		//clone mutationstepsizes offspring
		System.arraycopy(offspringA.mutation_step_size, 0, offspringA_ls.mutation_step_size, 0, offspringA.mutation_step_size.length);
		System.arraycopy(offspringB.mutation_step_size, 0, offspringB_ls.mutation_step_size, 0, offspringB.mutation_step_size.length);

		while(!local_search_A_done || !local_search_B_done) {

			if (budget_A<local_search_budget) {
				//local search for A
				local_search_A_done = greedy_ascent_local_search(delta_A, local_search_A_done, offspringA_ls, offspringA);
				budget_A++;
			}
			else {
				local_search_A_done = true;
			}

			if(budget_B<local_search_budget) {
				//local search for B
				local_search_B_done = greedy_ascent_local_search(delta_B, local_search_B_done, offspringB_ls, offspringB);
				budget_B++;
			}
			else {
				local_search_B_done=true;
			}

		}
	}

	private boolean greedy_ascent_local_search(double[] delta, boolean local_search_done, Individual offspring_ls, Individual offspring) {
		if (!local_search_done) {
			System.arraycopy(offspring.genotype, 0, offspring_ls.genotype, 0, offspring.genotype.length);
			for (int i = 0; i < offspring_ls.genotype.length; i++) {
				if (p_mutation_ls >= rnd_.nextDouble()) {
					// get delta and add to offspring
					delta[i] = offspring_ls.mutation_step_size[i] * rnd_.nextGaussian();
					sum_in_range(delta, i, offspring_ls.genotype);
				}
			}
			if (evals<evaluations_limit_) {
				// calculate fitness of offspring
				offspring_ls.fitness = (double) evaluation_.evaluate(offspring_ls.genotype);
				evals++;
			}
			else {
				local_search_done=true;
			}

			if (offspring_ls.fitness > offspring.fitness) {
				local_search_done = true;
				System.arraycopy(offspring_ls.genotype, 0, offspring.genotype, 0, offspring_ls.genotype.length);
			}
		}
		return local_search_done;
	}

	private void greedy_ascent_local_end_search(int individual_index) {

		double[] delta = new double[offspringA.mutation_step_size.length];

		Individual offspring_ls = new Individual(indiv_dim, rnd_);
		System.arraycopy(pops.get(individual_index).genotype, 0, offspring_ls.genotype, 0, pops.get(individual_index).genotype.length);

		for (int i = 0; i < offspring_ls.genotype.length; i++) {
			if (p_mutation_ls_end >= rnd_.nextDouble()) {
				// get delta and add to offspring
				delta[i] = mutation_step_size_ls_end * rnd_.nextGaussian();
				sum_in_range(delta, i, offspring_ls.genotype);
			}
		}

		// calculate fitness of offspring
		offspring_ls.fitness = (double) evaluation_.evaluate(offspring_ls.genotype);
		evals++;

		if (offspring_ls.fitness > pops.get(individual_index).fitness) {
			System.arraycopy(offspring_ls.genotype, 0, pops.get(individual_index).genotype, 0, offspring_ls.genotype.length);
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

	private static double round(double val, int places){
		if(places < 0) throw new IllegalArgumentException();

		BigDecimal bigDecimal = new BigDecimal(val);
		bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
		return bigDecimal.doubleValue();
	}

	public void run()
	{
		// Run your algorithm here

        // init population uniformly randomly within [-5,5]
		for(int individual = 0; individual < pop_size; individual ++){
			pops.add(new Individual(mutation_step_size_start,indiv_dim,rnd_, min_max));
			// calculate fitness
			pops.get(individual).fitness = (double) evaluation_.evaluate(pops.get(individual).genotype);
			evals++;
		}

		Double evolution_evals = eval_search_split*evaluations_limit_;

        while(evals< evolution_evals.intValue()){

			// select parent through method x
			tournament_selection(true);

			if(!Arrays.equals(pops.get(parA).genotype,pops.get(parB).genotype)) {
				// create offspring from selected parents by method x
				avergae_crossover();

				// mutate offspring
				nonuniform_mutation();

				// round to decimals
				for(int i=0; i< offspringA.genotype.length; i++){
					offspringA.genotype[i] = round(offspringA.genotype[i],3);
					offspringB.genotype[i] = round(offspringB.genotype[i],3);
				}

				// calculate fitness of offspring A
				offspringA.fitness = (double) evaluation_.evaluate(offspringA.genotype);
				evals++;

				// calculate fitness of offspring B
				offspringB.fitness = (double) evaluation_.evaluate(offspringB.genotype);
				evals++;

				// apply local search
				local_search();

				// select individuals to be replaced by method x
				tournament_selection(false);
			}
        }

		pops.sort(Collections.reverseOrder());

        int individual_index;

        while (evals<evaluations_limit_){
        	individual_index=rnd_.nextInt(ls_in_best-1);
			greedy_ascent_local_end_search(individual_index);
		}
	}

}
