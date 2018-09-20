import java.util.TreeSet;

public class Individual implements Comparable<Individual>
{

    double fitness;

    double genotype;

    double lifespam = 0;

    TreeSet<Individual> familyTree;

    public Individual(double genotype, TreeSet<Individual> familyTree)
    {
        this.genotype = genotype;
        this.familyTree = familyTree;
    }


    @Override
    public int compareTo(Individual o)
    {
        return Double.compare(this.fitness,o.fitness);
    }
}
