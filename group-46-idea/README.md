# Evolutionary Computing IntelliJ IDEA project

### Important: this project was tested on linux and mac only. Use it at your own risk.

## Steps to import the the project:
1. Install JDK
2. Install IntelliJ IDEA
3. Clone this repo
4. Open IDEA
5. In IDEA: (File &rarr;) Open &rarr; browse to the repo's folder (`ec-idea`) &rarr; OK

## Steps to see if everything is working:
1. In IDEA: Build &rarr; Build Project
2. Run &rarr; Run... &rarr; choose an option
3. Output should be something like:
```
Score: 10.0
Runtime: 3ms
```

## Steps to making the project become your own:
1. In IDEA: On the left panel rename the `player46.java` file to your own (right click on it &rarr; Refactor &rarr; Rename &rarr; `player42.java` &rarr; Refactor) - `player42.java` is an example, use your group number instead of `42`
2. Build &rarr; Build Project
3. Run &rarr; Edit configurations &rarr; JAR Application section &rarr; Choose one (e.g.: `Sphere`) &rarr; Program Arguments &rarr; rename `player46` to your own (e.g. `player42`)
4. Do the previous step for all of the configurations (Sphere, BentCigar, Katsuura, Schaffers)
5. Test if everything  still works by repeating the steps of [Steps to see if everything is working](#steps-to-see-if-everything-is-working)

## Making IDEA generate your submission.jar:
1. A submission.jar file is generated automatically with the project build. The location of this is ec-idea/out/production/assignment/submission.jar
