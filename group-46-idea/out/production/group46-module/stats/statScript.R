setwd(dir = "/home/max/git/EvComp.Group46/group-46-idea/stats")
source("Plots.R")
require(ggplot2)
require(dplyr)
raw <- read.csv("ParamTuning.csv", header = FALSE)

stats <- raw %>% group_by(Generation, IslandIndex, Epoch) %>%
  summarise(max_fitness = max(Fitness), 
            min_fitness = min(Fitness),
            avg_fitness = mean(Fitness))

p1 <- island_generational_plot(stats)
p1m <- island_mean_generational_plot(stats)
p2 <- island_growth_per_generation(stats)

plot(p1)
plot(p1m)
plot(p2)

