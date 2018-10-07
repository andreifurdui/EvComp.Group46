package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IslandParameters {
    //{very strict, slightly strict, slightly random, very random}
    private static final int[] tournament_size = {8, 6, 4, 2};
    private static final double[] aritm_over_blend_crossover_prob = {0.75, 0.63, 0.37, 0.25};
    private static final double[] mutation_step_size_multiplier = {0.5, 0.8, 1.2, 1.5};
    private static final double mutation_base_chance = .04;
    private static final double[] mutation_chance_multiplier =
            {
                    0.5 * mutation_base_chance,
                    0.8 * mutation_base_chance,
                    1.33 * mutation_base_chance,
                    2 * mutation_base_chance
            };
    private static final int[] elitist_survivors = {3, 2, 1, 0};

    public int TournamentSize;
    public double MutationStepSizeMultiplier;
    public double CrossoverMethodChance;
    public double MutationChance;
    public int ElitistSurvivors;

    public IslandParameters(int tournamentSize,double aritmBlendProb, double mutationStepSizeMultplier, double mutationChance, int elitistSurvivors)
    {
        this.TournamentSize = tournamentSize;
        this.CrossoverMethodChance = aritmBlendProb;
        this.MutationStepSizeMultiplier = mutationStepSizeMultplier;
        this.MutationChance = mutationChance;
        this.ElitistSurvivors = elitistSurvivors;
    }

    public static IslandParameters GetIslandParameters(int islandSize, int islandIndex)
    {
        int tSize = tournament_size[islandIndex];
        double aritmBlendProb = aritm_over_blend_crossover_prob[islandIndex];
        double mutStepSize = mutation_step_size_multiplier[islandIndex];
        double mutationChance = mutation_chance_multiplier[islandIndex];
        int elitists = elitist_survivors[islandIndex];

        return new IslandParameters(tSize, aritmBlendProb, mutStepSize, mutationChance, elitists);
    }

    public static List<String> getHeaderLog()
    {
        List<String> header = new ArrayList<>();

        header.add("CrossoverMethodChance");
        header.add("MutationChance");
        header.add("MutationStepSizeMultiplier");
        header.add("TournamentSize");
        header.add("ElitistSurvivors");

        return header;
    }

    public List<String> Log()
    {
        List<String> log = new ArrayList<>();

        log.add(Double.toString(CrossoverMethodChance));
        log.add(Double.toString(MutationChance));
        log.add(Double.toString(MutationStepSizeMultiplier));
        log.add(Integer.toString(TournamentSize));
        log.add(Integer.toString(ElitistSurvivors));

        return log;
    }
}
