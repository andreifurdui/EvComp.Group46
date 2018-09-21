import jdk.internal.util.xml.impl.Pair;

import java.util.Random;

public class Gene {

    public double Value;

    public Gene(double value){
        this.Value = value;
    }

    public static Gene Create_Rand(Random rand) {
        double value = (rand.nextDouble() - 0.5) * 10;

        return new Gene(value);
    }
}
