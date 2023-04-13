package src.main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class ReassembleText {
    private static final String VALIDATION_ERROR = "Invalid args";
    private static final String EMPTY_VALUE = "";
    private static final String SEPARATOR = ";";
    private static final int ZERO = 0;
    private static final int ONE = 1;

    public static void main(String[] args) {
        if (args.length == ZERO || args[ZERO] == null) {
            throw new RuntimeException(VALIDATION_ERROR);
        }

        try (BufferedReader in = new BufferedReader(new FileReader(args[ZERO]))) {
            in.lines()
                    .filter(Objects::nonNull)
                    .filter(not(String::isBlank))
                    .map(ReassembleText::reassemble)
                    .forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to create a set of strings based in one single line (separator = ;)
     *
     * @param fragment string line from document
     * @return {@link List<String>}
     */
    private static List<String> splitValues(String fragment) {
        return Arrays.stream(fragment.split(SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * reassemble fragment line
     *
     * @param fragment line fragment
     * @return {@link String}
     */
    public static String reassemble(final String fragment) {
        List<String> fragments = splitValues(fragment);

        if (fragments.size() == ONE) {
            return fragment;
        }

        while (fragments.size() > ONE) {
            String concatenatedString = null;
            int numberOfOverlappingCharacters = ZERO;
            int[] selectedVariation = new int[2];

            for (int[] variation : getAllPairVariations(fragments.size())) {
                String[] concatResult = concatWithOverlappingCharacters(fragments.get(variation[ZERO]), fragments.get(variation[ONE]));
                if (Integer.parseInt(concatResult[ONE]) > numberOfOverlappingCharacters) {
                    selectedVariation = new int[]{variation[ZERO], variation[ONE]};
                    numberOfOverlappingCharacters = Integer.parseInt(concatResult[ONE]);
                    concatenatedString = concatResult[ZERO];
                }
            }

            removeFragments(fragments, selectedVariation);
            fragments.add(Objects.nonNull(concatenatedString) ? concatenatedString : "<<No Overlaping>>");
        }

        return fragments.get(ZERO);
    }

    /**
     * build all possible pair of two based on fragments size
     *
     * @param size fragments size
     * @return {@link List}
     */
    public static List<int[]> getAllPairVariations(int size) {
        List<int[]> pair = new ArrayList<>();

        int index = ZERO;

        while (index < size) {
            for (int i = ZERO; i < size; i++) {
                if (i != index) pair.add(new int[]{index, i});
            }
            index++;
        }

        return pair;
    }

    /**
     * remove two compared pairs with max overlaping
     *
     * @param fragments fragments
     * @param variation selected variation
     */
    public static void removeFragments(List<String> fragments, int[] variation) {
        List<Integer> indicesToRemove = Arrays.asList(variation[ZERO], variation[ONE]);
        indicesToRemove.sort(Collections.reverseOrder());
        indicesToRemove.forEach(index -> fragments.remove(index.intValue()));
    }

    /**
     * Overlapping chars comparing two strings
     *
     * @param firstString  first string
     * @param secondString second string
     * @return {@link String[]}
     */
    public static String[] concatWithOverlappingCharacters(final String firstString, final String secondString) {
        if (Objects.isNull(firstString) && Objects.isNull(secondString)) {
            return new String[]{EMPTY_VALUE, String.valueOf(ZERO)};
        }

        if (Objects.nonNull(firstString) && Objects.isNull(secondString)) {
            return new String[]{firstString, String.valueOf(ZERO)};
        }

        if (Objects.isNull(firstString)) {
            return new String[]{secondString, String.valueOf(ZERO)};
        }

        if (firstString.contains(secondString)) {
            return new String[]{firstString, String.valueOf(secondString.length())};
        }


        int string1Length = firstString.length() - ONE;
        int string2Length = secondString.length() - ONE;
        char lastCharAtString1 = firstString.charAt(string1Length);
        char firstCharAtString2 = secondString.charAt(ZERO);

        int index = secondString.lastIndexOf(lastCharAtString1, Math.min(string1Length, string2Length));

        while (index != -1) {
            if (firstString.charAt(string1Length - index) == firstCharAtString2) {
                int i = index;

                while ((i != -1) && (firstString.charAt(string1Length - index + i) == secondString.charAt(i))) {
                    i--;
                }

                if (i == -1) {
                    return new String[]{firstString + secondString.substring(index + ONE), String.valueOf(index + ONE)};
                }
            }
            index = secondString.lastIndexOf(lastCharAtString1, index - ONE);
        }
        return new String[]{firstString + secondString, String.valueOf(ZERO)};
    }
}


