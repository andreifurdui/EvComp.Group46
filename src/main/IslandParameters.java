package main;

import java.util.ArrayList;
import java.util.List;

public class IslandParameters
{
    private static final int individual_dimension = 10;
    public static final double LearningRate_Global_PropFactor = 1 / Math.sqrt(individual_dimension);

    public int TournamentSize;
    public double MutationChance;
    public int ElitistSurvivors;
    public double CrossoverChance;
    public double LearningRate;

    public IslandParameters(int tournamentSize, double mutationChance, int elitistSurvivors, double crossoverChance, double learningRate)
    {
        this.TournamentSize = tournamentSize;
        this.MutationChance = mutationChance;
        this.CrossoverChance = crossoverChance;
        this.ElitistSurvivors = elitistSurvivors;
        this.LearningRate = learningRate * LearningRate_Global_PropFactor;
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
