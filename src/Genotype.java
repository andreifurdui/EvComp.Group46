import java.util.Random;

public class Genotype {
    public static final int GenotypeLength = 10;

    public double[] Values;

    public Genotype(double[] values){
        this.Values = values;
    }

    //Assign random values to all genomes
    //Takes random double between 0-1 and modifies it
    //so the random generated value lies within the search space of [-5,5]
    public static Genotype Create_Rand(Random rand) {
        double[] values = new double[GenotypeLength];

        for (int i = 0; i < GenotypeLength; i++) {
            values[i] = (rand.nextDouble() - 0.5) * 10;
        }


        return new Genotype(values);
    }
}
