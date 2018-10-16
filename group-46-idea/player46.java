import org.jetbrains.annotations.NotNull;
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.lang.*;
import java.io.*;

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

    private double p_crossover = 0.8;
    private double p_mutation = 0.95;
    private double mutation_step_size_start = 1;
    private double mutation_tau_apos = 1/Math.sqrt(2*indiv_dim)*0.7;
    private double mutation_tau = 1/Math.sqrt(2*Math.sqrt(indiv_dim))*0.8;

    private double alpha = 0.1;

    private int tournament_size_parent_selection = 2;
	private int tournament_size_survival_selection = 2;
	private double weaker_offspring_survival_prop = 0.1;


	private int local_search_budget = 2;
	private double p_mutation_ls = 0.1;


	private double eval_search_split =1;
	private int ls_in_best = 1;
	private double p_mutation_ls_end = 0.1;
	private double mutation_step_size_ls_end = 0.002;
	private double Mutation_step_size_ls_end_scale = 0.9;

	private ArrayList<Individual> pops = new ArrayList<>();
	private ArrayList<Individual> next_gen = new ArrayList<>();

	private int parA;
	private int parB;

	private Individual offspringA;
	private Individual offspringB;

	private static final int number_of_runs = 1000;

	// schaffers optimal 100 F0.4 Cr0.8
	// bent cigar optimal 50 F0.4 Cr0.9
	private double F;
	private double Cr;

	public player46()
	{
		rnd_ = new Random();
	}

	public static void main(String args[]) throws IOException {
		ProcessBuilder term = new ProcessBuilder("/bin/bash");
		Process p = term.start();

		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader processOutput = new BufferedReader(isr);
		BufferedWriter processInput = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

		Logger log = new Logger("Mutation_Tuning_DE");
		List<String> logHeader = new ArrayList<>();
		logHeader.add("Split");
		logHeader.add("in_best");
		logHeader.add("p_mut");
		logHeader.add("sigma");
		logHeader.add("Score");
		log.AddRow(logHeader);

		String Schaffers_function = "SchaffersEvaluation";
		String BentCigar_function = "BentCigarFunction";


		ArrayList <String> functions = new ArrayList<>();
		functions.add(Schaffers_function);
		functions.add(BentCigar_function);

		String output;

		for (double par1 = 0.7; par1 <= 0.7; par1 += 0.1) {
			for (double par2 = 1; par2 <= 1; par2 += 1)
				for (double par3 = 0.4; par3 <= 0.4; par3 += 0.1)
					for (double par4 = 0; par4 <= 1; par4 += 1)
					{
						double avgScore = 0;
						for (int run = 0; run < number_of_runs; run++) {
							String currentDir = System.getProperty("user.dir");

							String command = String.format("/usr/lib/jvm/java-1.11.0-openjdk-amd64/bin/java -Djava.library.path=%s -Dmc=%f -Dcc=%f -Dts=%f -Dlr=%f -Dfile.encoding=UTF-8 -jar %s/out/production/group46-module//testrun.jar -submission=player46 -evaluation=%s -nosec -seed=%d",
									currentDir,
									par1,
									par2,
									par3,
									par4,
									currentDir,
									functions.get((int)par4),
									run);

							processInput.write(command);
							processInput.newLine();
							processInput.flush();

							int linesRead = 0;
							double score;

							while (linesRead < 2 && (output = processOutput.readLine()) != null) {
								if (linesRead == 0) {
									score = Double.parseDouble(output.substring(6));
									avgScore += score;
								}
								linesRead++;
							}
						}

						List<String> result = new ArrayList<>();
//						result.add(Double.toString(par1));
//						result.add(Double.toString(par2));
//                      result.add(Double.toString(par3));
//						result.add(Double.toString(par4));
						result.add(functions.get((int)par4));
						result.add(Double.toString(avgScore / number_of_runs));
						log.AddRow(result);
						log.Print(result);
					}
		}

		processInput.write("exit");
		processInput.newLine();
		processInput.flush();

		processInput.close();
		processOutput.close();
		log.WriteLog();
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

		// schaffers optimal 100 F0.4 Cr0.8
		// bent cigar optimal 50 F0.4 Cr0.9
        if(isMultimodal){
			pop_size=100;
        	F=0.4;
			Cr=0.8;
        }else{
        	pop_size=50;
			F=0.4;
			Cr=0.9;
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
				for (int par_in_set_a = 0; par_in_set_a < 2* tournament_size; par_in_set_a++) {
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

			offspringA = new Individual(mutation_step_size_start,indiv_dim, rnd_);
			offspringB = new Individual(mutation_step_size_start,indiv_dim, rnd_);

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

			offspringA = new Individual(mutation_step_size_start,indiv_dim, rnd_);
			offspringB = new Individual(mutation_step_size_start,indiv_dim, rnd_);

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
			offspringA = new Individual(mutation_step_size_start, indiv_dim, rnd_);
			offspringB = new Individual(mutation_step_size_start, indiv_dim, rnd_);

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

		Individual offspringA_ls = new Individual(mutation_step_size_start, indiv_dim, rnd_);
		Individual offspringB_ls = new Individual(mutation_step_size_start, indiv_dim, rnd_);

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
				offspring.fitness = offspring_ls.fitness;
			}
		}
		return local_search_done;
	}

	private void greedy_ascent_local_end_search(int individual_index) {

		double[] delta = new double[indiv_dim];

		Individual offspring_ls = new Individual(mutation_step_size_ls_end, indiv_dim, rnd_);
		System.arraycopy(pops.get(individual_index).genotype, 0, offspring_ls.genotype, 0, pops.get(individual_index).genotype.length);
		System.arraycopy(pops.get(individual_index).mutation_step_size, 0, offspring_ls.mutation_step_size, 0, pops.get(individual_index).mutation_step_size.length);

		for (int i = 0; i < offspring_ls.genotype.length; i++) {
			if (p_mutation_ls_end >= rnd_.nextDouble()) {
				offspring_ls.mutation_step_size[i] *= Mutation_step_size_ls_end_scale;
				// get delta and add to offspring
				delta[i] = offspring_ls.mutation_step_size[i] * rnd_.nextGaussian();
				sum_in_range(delta, i, offspring_ls.genotype);
			}
		}

		// calculate fitness of offspring
		offspring_ls.fitness = (double) evaluation_.evaluate(offspring_ls.genotype);
		evals++;

		if (offspring_ls.fitness > pops.get(individual_index).fitness) {
			System.arraycopy(offspring_ls.genotype, 0, pops.get(individual_index).genotype, 0, offspring_ls.genotype.length);
			System.arraycopy(offspring_ls.mutation_step_size, 0, pops.get(individual_index).mutation_step_size, 0, offspring_ls.mutation_step_size.length);
			pops.get(individual_index).fitness = offspring_ls.fitness;
		}
	}

	private void diif_evolution_generation(){
		int indiv_in_evolution = 4;
		int[] indivs = new int[indiv_in_evolution];
		boolean already_in;

		for (int individual=0; individual<pops.size();individual++) {
			// sample x random parents out of population
			indivs[0]=individual;
			for (int battle = 1; battle < indiv_in_evolution; battle++) {
				do {
					already_in = false;
					indivs[battle] = rnd_.nextInt(pops.size() - 1);
					// making sure they are in the set only once
					for (int rnd = 0; rnd < indivs.length; rnd++) {
						if (indivs[rnd] == indivs[battle] && rnd != battle) {
							already_in = true;
						}
					}
				} while (already_in);
			}

			double donor_vector[] = new double[indiv_dim];
			double trail_vector[] = new double[indiv_dim];

			for (int gene = 0; gene < indiv_dim; gene++) {
				donor_vector[gene] = pops.get(indivs[1]).genotype[gene] + F * (pops.get(indivs[2]).genotype[gene] - pops.get(indivs[3]).genotype[gene]);
			}

			int fix_donor_gene = rnd_.nextInt(indiv_dim);

			for (int gene = 0; gene < indiv_dim; gene++) {
				if (gene == fix_donor_gene || Cr >= rnd_.nextDouble()) {
					trail_vector[gene] = donor_vector[gene];
				} else {
					trail_vector[gene] = pops.get(indivs[0]).genotype[gene];
				}
			}

			// calculate fitness of offspring
			if (evals<evaluations_limit_){
				double fitness_trail_vector = (double) evaluation_.evaluate(trail_vector);
				evals++;

				if (fitness_trail_vector > pops.get(indivs[0]).fitness) {
					Individual offspring_de = new Individual(mutation_step_size_ls_end, indiv_dim, rnd_);
					System.arraycopy(trail_vector, 0, offspring_de.genotype, 0, trail_vector.length);
					offspring_de.fitness = fitness_trail_vector;
					next_gen.add(offspring_de);
				}
				else {
					next_gen.add(pops.get(indivs[0]));
				}
			}
			else {
				break;
			}
		}
		pops.clear();
		for (int individual=0; individual<next_gen.size();individual++) {
			pops.add(next_gen.get(individual));
		}
		next_gen.clear();
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

		//eval_search_split = Double.valueOf(System.getProperty(("mc")));
		//ls_in_best = Double.valueOf(System.getProperty("cc")).intValue();
		//Mutation_step_size_ls_end_scale = Double.valueOf(System.getProperty("ts"));
		//mutation_step_size_ls_end = Double.valueOf(System.getProperty(("lr")));

		//pop_size = Double.valueOf(System.getProperty("cc")).intValue();
		//F = Double.valueOf(System.getProperty("ts"));;
		//Cr = Double.valueOf(System.getProperty("lr"));;

		// init population uniformly randomly within [-5,5]
		for(int individual = 0; individual < pop_size; individual ++){
			pops.add(new Individual(mutation_step_size_start,indiv_dim,rnd_, min_max));
			// calculate fitness
			pops.get(individual).fitness = (double) evaluation_.evaluate(pops.get(individual).genotype);
			evals++;
		}

		Double evolution_evals = eval_search_split*evaluations_limit_;

        while(evals< evolution_evals.intValue()) {

/*			// select parent through method x
			tournament_selection(true);

			if(!Arrays.equals(pops.get(parA).genotype,pops.get(parB).genotype)) {
				// create offspring from selected parents by method x
				one_point_crossover();

				// mutate offspring
				nonuniform_mutation();


				// calculate fitness of offspring A
				offspringA.fitness = (double) evaluation_.evaluate(offspringA.genotype);
				evals++;

				// calculate fitness of offspring B
				offspringB.fitness = (double) evaluation_.evaluate(offspringB.genotype);
				evals++;

				// apply local search
				//local_search();

				// select individuals to be replaced by method x
				tournament_selection(false);
			}*/
			diif_evolution_generation();

		}

		pops.sort(Collections.reverseOrder());


       /* for(int candidates=0; candidates<ls_in_best; candidates++){
        	for (int sigma= 0; sigma<pops.get(candidates).mutation_step_size.length; sigma++){
				pops.get(candidates).mutation_step_size[sigma] = mutation_step_size_ls_end;
			}
		}*/

		int individual_index;
        while (evals<evaluations_limit_){
        	individual_index=rnd_.nextInt(ls_in_best);
			greedy_ascent_local_end_search(individual_index);
		}
	}

}
