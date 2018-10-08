package main;

import java.util.ArrayList;
import java.util.List;

public class IslandParameters {
    //{very strict, slightly strict, slightly random, very random}
    private static final int[] tournament_size = {4, 3, 3, 2};
    private static final double[] aritm_over_blend_crossover_prob = {0.4, 0.3, 0.2, 0.1};
    private static final double[] mutation_step_size_multiplier = {0.6, 0.8, 1.2, 1.4};
    private static final double mutation_base_chance = .1;
    private static final double[] mutation_chance_multiplier =
            {
                    0.5 * mutation_base_chance,
                    0.8 * mutation_base_chance,
                    1.33 * mutation_base_chance,
                    2 * mutation_base_chance
            };
    private static final int[] elitist_survivors = {2, 1, 1, 0};
    private static final int individual_dimension = 10;
    public static final double Mutation_Tau = 1 / Math.sqrt(2*Math.sqrt(individual_dimension));
    public static final double Mutation_Tau_Prime = 1 / Math.sqrt(2 * individual_dimension);
    public static final double CrossoverMethodChance = 1;

    public int TournamentSize;
    public double MutationStepSizeMultiplier;
    public double MutationChance;
    public int ElitistSurvivors;

    public IslandParameters(int tournamentSize,double aritmBlendProb, double mutationStepSizeMultplier, double mutationChance, int elitistSurvivors)
    {
        this.TournamentSize = tournamentSize;
        this.MutationStepSizeMultiplier = mutationStepSizeMultplier;
        this.MutationChance = mutationChance;
        this.ElitistSurvivors = elitistSurvivors;
    }

    public static IslandParameters GetIslandParameters(int islandSize, int islandIndex)
    {
        islandIndex = 2;
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
