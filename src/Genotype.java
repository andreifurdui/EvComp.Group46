import java.util.Random;

public class Genotype {
    public static final int GenotypeLength = 10;

    public double[] Values;

    public Genotype(double[] values){
        this.Values = values;
    }

    public static Genotype Create_Rand(Random rand) {
        double[] values = new double[GenotypeLength];

        for (int i = 0; i < GenotypeLength; i++) {
            values[i] = (rand.nextDouble() - 0.5) * 10;
        }

        return new Genotype(values);
    }
}
