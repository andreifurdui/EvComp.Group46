import java.util.Random;

public class Individual {
    public static final int GenotypeLength = 10;

    public Gene[] Genotype;

    public Individual(Gene[] genotype){
        this.Genotype = genotype;
    }

    public static Individual Create_Rand(Random rand){
        Gene[] genotype = new Gene[GenotypeLength];

        for (int i = 0; i < GenotypeLength; i++) {
            genotype[i] = Gene.Create_Rand(rand);
        }

        return new Individual(genotype);
    }
}
