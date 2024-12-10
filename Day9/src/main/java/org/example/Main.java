package org.example;

import com.google.common.base.Strings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String input = loadInput();
        String decompressedDiscMap = decompressFormat(input);
        String movedFile = moveFiles(decompressedDiscMap);
        System.out.println("decompressedMap: " + decompressedDiscMap);
        System.out.println("move: " + movedFile);
        //List<String> decompressedDiscMaps = singleDiscMaps.stream().map(Main::decompressFormat).toList();
        //List<String> rearrangedFiles = decompressedDiscMaps.stream().map(Main::moveFiles).toList();
        /*List<Integer> checkSumOfEachFile = rearrangedFiles.stream()
                .map(file -> file.substring(0, file.indexOf('.'))).toList().stream()
                .map(Main::calculateCheckSum).toList();
*/
        System.out.println(calculateCheckSum(movedFile.substring(0, movedFile.indexOf('.'))));
        //System.out.println((Integer) checkSumOfEachFile.stream().mapToInt(Integer::intValue).sum());
    }

    private static String loadInput() {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource("input.txt");
            Path path = Paths.get(url.toURI());
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            return lines.getFirst();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String decompressFormat(String discMap) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFileDigit = true;
        int id = 0;
        for (char digit : discMap.toCharArray()) {
            if (isFileDigit) {
                stringBuilder.append(Strings.repeat(String.valueOf(id), Character.getNumericValue(digit)));
                id++;
                isFileDigit = false;
            } else {
                stringBuilder.repeat('.', Character.getNumericValue(digit));
                isFileDigit = true;
            }
        }
        return stringBuilder.toString();
    }

    //have 2 lists
    //build a new one where i copy every digit i find which is not a point to the new one
    //if i find a point i replace it with a number from behind and the number from behind with a point
    private static String moveFiles(String decompressedMap) {
        StringBuilder transformedDisc = new StringBuilder();
        CharacterIterator it = new StringCharacterIterator(decompressedMap);
        while (it.current() != CharacterIterator.DONE) {
            if (it.current() != '.') {
                transformedDisc.append(it.current());
            } else {
                transformedDisc.append(decompressedMap.charAt(lastIndexOfFileNumber(decompressedMap)));
                StringBuilder temporary = new StringBuilder(decompressedMap);
                temporary.setCharAt(lastIndexOfFileNumber(decompressedMap), '.');
                decompressedMap = temporary.toString();
            }
            it.next();
            if (onlyFreeSpaceLeft(it.getIndex(), decompressedMap)) {
                transformedDisc.repeat('.', decompressedMap.length() - it.getIndex());
                return transformedDisc.toString();
            }
        }
        return transformedDisc.toString();
    }

    public static int lastIndexOfFileNumber(String discMap) {
        for (int i = discMap.length() - 1; i > 0; --i) {
            if (discMap.charAt(i) != '.') {
                return i;
            }
        }
        return 0;
    }

    public static boolean onlyFreeSpaceLeft(int position, String toCheck) {
        if (position == toCheck.length() - 1) {
            return true;
        }
        return toCheck.substring(position).matches("^[.]*$");
    }

    private static int calculateCheckSum(String withoutFreeSpacePoints) {
        int checkSum = 0;
        for (int i = 1; i < withoutFreeSpacePoints.length(); ++i) {
            checkSum += Character.getNumericValue(withoutFreeSpacePoints.charAt(i)) * i;
        }
        return checkSum;
    }
}