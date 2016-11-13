package jeneticstest;

import org.jenetics.CharacterChromosome;
import org.jenetics.CharacterGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;

import static java.lang.String.format;

public class Main
{
    private static final char[] TARGET_CHAR_ARRAY = "hello_world".toCharArray();

    private static final String POSSIBLE_CHARS = "_abcdefghijklmnopqrstuvwxyz";

    private static Integer calculateScore(Genotype<CharacterGene> geneGenotype)
    {
        final CharacterChromosome chromosome = (CharacterChromosome) geneGenotype.getChromosome();

        final char[] currentCharArray = chromosome.toArray();

        return calculatePositionScore(currentCharArray);
    }

    private static int calculatePositionScore(char[] currentCharArray)
    {
        final long targetLength = TARGET_CHAR_ARRAY.length;
        final long currentLength = currentCharArray.length;

        int positionScore = 0;
        for (int i = 0; i < currentCharArray.length; i++) {
            if (i > targetLength - 1) {
                positionScore -= (currentLength - targetLength) * 5;
                break;
            }
            char aChar = currentCharArray[i];
            char wantedChar = TARGET_CHAR_ARRAY[i];

            long charDistance = Math.abs(aChar - wantedChar);

            positionScore += Math.abs(POSSIBLE_CHARS.length() - charDistance);
        }

        return positionScore;
    }

    public static void main(String[] args)
    {
        final CharacterChromosome characterChromosome = CharacterChromosome.of("0123456789a", new CharSeq(POSSIBLE_CHARS.toCharArray()));
        Factory<Genotype<CharacterGene>> genotypeFactory = Genotype.of(characterChromosome);

        Engine<CharacterGene, Integer> engine = Engine
            .builder(Main::calculateScore, genotypeFactory)
            .build();

        Genotype<CharacterGene> result = engine.stream()
            .limit(4000)
            .collect(EvolutionResult.toBestGenotype());

        System.out.println("Found:\n" + result);
        System.out.println(format("result score: %d", calculateScore(result)));
        System.out.println(format("best possible score: %d", calculatePositionScore(TARGET_CHAR_ARRAY)));
    }
}
