package main;

import java.util.ArrayList;
import java.util.List;

public class IslandParameters
{
    private static final double mutation_base_chance = .1;
    private static final int individual_dimension = 10;
    public static final double Mutation_Tau = 1 / Math.sqrt(2*Math.sqrt(individual_dimension));
    public static final double Mutation_Tau_Prime = 1 / Math.sqrt(2 * individual_dimension);

    public int TournamentSize;
    public double MutationChance;
    public int ElitistSurvivors;
    public double CrossoverChance;

    public IslandParameters(int tournamentSize, double mutationChance, int elitistSurvivors,double crossoverChance)
    {
        this.TournamentSize = tournamentSize;
        this.MutationChance = mutationChance;
        this.CrossoverChance = crossoverChance;
        this.ElitistSurvivors = elitistSurvivors;
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

        log.add(Double.toString(MutationChance));
        log.add(Integer.toString(TournamentSize));
        log.add(Integer.toString(ElitistSurvivors));

        return log;
    }
}
