import java.util.List;
import java.util.Random;

public class Operators {

    private static Random rand = new Random();

    public static Individual TournamentSelect(List<Individual> competitors, int tournamentSize)
    {
        int best = competitors.size();

        for (int i = 0; i < tournamentSize; i++)
        {
            int drawn =  rand.nextInt(competitors.size());
            best = drawn < best ? drawn : best;
        }

        return competitors.get(best);
    }
}
