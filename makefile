JC = javac
JFLAGS = -g
JAR = jar
JARFLAGS = cmf
JAVA = java
CONTEST = -cp contest.jar

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $(CONTEST) $*.java

CLASSES = \
	Individual.java \
	Population.java \
	Group46.java 

default: classes

classes: $(CLASSES:.java=.class)

submission: Group46.class
	$(JAR) $(JARFLAGS) MainClass.txt submission.jar Group46.class Population.class Individual.class

sphere: submission
	$(JAVA) -$(JAR) testrun.jar -submission=Group46 -evaluation=SphereEvaluation -seed=1

cigar : submission
	$(JAVA) -$(JAR) testrun.jar -submission=Group46 -evaluation=BentCigarFunction -seed=1

kat: submission
	$(JAVA) -$(JAR) testrun.jar -submission=Group46 -evaluation=KatsuuraEvaluation -seed=1

schaffers: submission
	$(JAVA) -$(JAR) testrun.jar -submission=Group46 -evaluation=SchaffersEvaluation -seed=1

tests: submission sphere cigar schaffers kat

clean:
	$(RM) *46.class submission.jar Individual.class Population.class
