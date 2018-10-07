package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Genotype {
    public static final int GenotypeLength = 10;

    public double[] Values;

    public Genotype(double[] values){
        this.Values = Bounded(values);
    }

    public static List<String> getLogHeader()
    {
        List<String> header = new ArrayList<>();

        for (int i = 0; i < GenotypeLength; i++) {
            header.add("X_" + i);
        }

        return header;
    }

    private double[] Bounded(double[] values)
    {
        for (int i = 0; i < 10; i++) {
            values[i] = Bounded(values[i]);
        }

        return values;
    }

    private double Bounded(double value)
    {
        return value < -5 ?
                -5 :
                value > 5 ?
                        5 :
                        value;
    }

    public static Genotype Create_Rand(Random rand) {
        double[] values = new double[GenotypeLength];

        for (int i = 0; i < GenotypeLength; i++) {
            values[i] = (rand.nextDouble() - 0.5) * 10;
        }

        return new Genotype(values);
    }

    public List<String> Log()
    {
        List<String> log = new ArrayList<>();

        for (int i = 0; i < GenotypeLength; i++) {
            log.add(Double.toString(Values[i]));
        }

        return log;
    }
}
