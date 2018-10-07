public class IslandParameters {
    //{very strict, slightly strict, slightly random, very random}
    private static final double[] tournament_size_multiplier = {.5, .25, .166, .125};
    private static final double[] aritm_over_blend_crossover_prob = {0.75, 0.63, 0.37, 0.25};
    private static final double[] mutation_step_size_multiplier = {0.5, 0.8, 1.2, 1.5};
    private static final double[] mutation_chance_multiplier = {0.5, 0.8, 1.33, 2};

    public int TournamentSize;                  //for parent selection (?)
    public double MutationStepSizeMultiplier;   //factor to influence mutation step size (sigma) over evolution (usually from exploration (big) to exploitation)
    public double CrossoverMethodChance;        //factor to influence xover rate   (not yet a probability)
    public double MutationChance;               //factor to influence mutation rate (not yet a probability)

    public IslandParameters(int tournamentSize,double aritmBlendProb, double mutationStepSizeMultplier, double mutationChance)
    {
        this.TournamentSize = tournamentSize;
        this.CrossoverMethodChance = aritmBlendProb;
        this.MutationStepSizeMultiplier = mutationStepSizeMultplier;
        this.MutationChance = mutationChance;
    }

    public static IslandParameters GetIslandParameters(int islandSize, int islandIndex)
    {
        int tSize = (int)Math.ceil(islandSize * tournament_size_multiplier[islandIndex]);
        double aritmBlendProb = aritm_over_blend_crossover_prob[islandIndex];
        double mutStepSize = mutation_step_size_multiplier[islandIndex];
        double mutationChance = 1 / (islandSize * mutation_chance_multiplier[islandIndex]);

        return new IslandParameters(tSize, aritmBlendProb, mutStepSize, mutationChance);
    }
}
