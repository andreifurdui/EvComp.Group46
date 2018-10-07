import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.TreeSet;

public class Individual implements Comparable<Individual>
{
    public double fitness;

    public double[] genotype;

    private Random rnd_;

    public double[] mutation_step_size;

    Individual(int indiv_dim, Random rand){
        genotype = new double[indiv_dim];
        mutation_step_size = new double[indiv_dim];

        rnd_ = rand;
    }

    Individual(double mutation_step_size_start, int indiv_dim, Random rand, double[] min_max){
        genotype = new double[indiv_dim];
        mutation_step_size = new double[indiv_dim];

        rnd_ = rand;

        for(int gene = 0; gene < indiv_dim; gene++){
            // initialize genes
            genotype[gene] = min_max[0] + rnd_.nextFloat() * (min_max[1] - min_max[0]);

            genotype[gene] = round(genotype[gene],3);

            // initialize mutation step size
            mutation_step_size[gene] = mutation_step_size_start;
        }
    }

    @Override
    public int compareTo(Individual o)
    {
        return Double.compare(this.fitness,o.fitness);
    }

    public static double round(double val, int places){
        if(places < 0) throw new IllegalArgumentException();

        BigDecimal bigDecimal = new BigDecimal(val);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
