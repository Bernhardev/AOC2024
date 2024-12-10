package org.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String input = loadInput();
        processPartOne(input);
        processPartTwo(input);
    }

    private static void processPartOne(String input) {
        List<FileSpace> decompressedDiscMap = decompressFormat(input);
        List<FileSpace> defragmentedDiscMap = defragDiskMap(decompressedDiscMap);
        double result = calculateCheckSum(defragmentedDiscMap.stream().filter(fileSpace -> fileSpace instanceof File).map(fileSpace -> (File) fileSpace).toList());
        System.out.printf("Result: %.0f\n", result);
    }

    private static void processPartTwo(String input) {
        List<FileSpace> decompressedDiscMap = decompressFormat(input);
        List<FileSpace> defragmentedDiscMap = defragPartTwo(decompressedDiscMap);
        double result = calculateCheckSumForPartTwo(defragmentedDiscMap);
        System.out.printf("Result Part Two: %.0f\n", result);
    }

    private static List<FileSpace> defragPartTwo(List<FileSpace> decompressedMap) {
        for (int currentPosition = 0; currentPosition < decompressedMap.size(); ++currentPosition) {
            if (decompressedMap.get(currentPosition) instanceof FreeSpace) {
                moveFilesPartTwo(currentPosition, decompressedMap);
            }
        }
        return decompressedMap;
    }

    private static void moveFilesPartTwo(int position, List<FileSpace> decompressedMap) {
        FileSpace freeSpace = decompressedMap.get(position);
        File fileToMove = findFittingFile(position, freeSpace.space, decompressedMap);
        if(fileToMove == null) {
            return;
        }
        if (fileToMove.space == freeSpace.space) {
            decompressedMap.set(position, new File(fileToMove.id, fileToMove.space));
            decompressedMap.set(decompressedMap.indexOf(fileToMove), new FreeSpace(fileToMove.space));
        } else {
            int difference = freeSpace.space - fileToMove.space;
            decompressedMap.set(position, fileToMove);
            decompressedMap.add(position + 1, new FreeSpace(difference));
            decompressedMap.set(decompressedMap.lastIndexOf(fileToMove), new FreeSpace(fileToMove.space));
        }
    }

    private static double calculateCheckSumForPartTwo(List<FileSpace> defragmentedDiscMap) {
        double checkSum = 0;
        int index = 0;
        for (FileSpace fileSpace : defragmentedDiscMap) {
            for (int i = 0; i < fileSpace.space; i++) {
                if(fileSpace instanceof File file) {
                    checkSum += index * file.id;
                }
                index++;
            }
        }
        return checkSum;
    }

    private static File findFittingFile(int position, int freeSpace, List<FileSpace> discMap) {
        for (int i = discMap.size() - 1; i > position; --i) {
            if (discMap.get(i) instanceof File file && file.space <= freeSpace) {
                return file;
            }
        }
        return null;
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

    private static List<FileSpace> decompressFormat(String discMap) {
        List<FileSpace> decompressedFormat = new ArrayList<>();
        boolean isFileDigit = true;
        int id = 0;
        for (char digit : discMap.toCharArray()) {
            if (isFileDigit) {
                decompressedFormat.add(new File(id, Character.getNumericValue(digit)));
                id++;
                isFileDigit = false;
            } else {
                decompressedFormat.add(new FreeSpace(Character.getNumericValue(digit)));
                isFileDigit = true;
            }
        }
        return decompressedFormat;
    }

    private static List<FileSpace> defragDiskMap(List<FileSpace> decompressedMap) {
        for (int currentPosition = 0; currentPosition < decompressedMap.size(); ++currentPosition) {
            if (noMoreFilesLeft(currentPosition, decompressedMap)) {
                return decompressedMap;
            }
            if (decompressedMap.get(currentPosition) instanceof FreeSpace) {
                moveFile(currentPosition, decompressedMap);
            }
        }
        return decompressedMap;
    }

    private static void moveFile(int position, List<FileSpace> decompressedMap) {
        File fileToMove = getLastFileInDiskMapping(decompressedMap);
        FileSpace freeSpace = decompressedMap.get(position);
        if (fileToMove.space == freeSpace.space) {
            decompressedMap.set(position, new File(fileToMove.id, fileToMove.space));
            decompressedMap.set(decompressedMap.indexOf(fileToMove), new FreeSpace(fileToMove.space));
        } else if (fileToMove.space > freeSpace.space) {
            fileToMove.space = fileToMove.space - freeSpace.space;
            decompressedMap.set(position, new File(fileToMove.id, freeSpace.space));
            decompressedMap.addLast(new FreeSpace(fileToMove.space));
        } else {
            int difference = freeSpace.space - fileToMove.space;
            decompressedMap.set(position, fileToMove);
            decompressedMap.add(position + 1, new FreeSpace(difference));
            decompressedMap.set(decompressedMap.lastIndexOf(fileToMove), new FreeSpace(fileToMove.space));
        }
    }

    private static File getLastFileInDiskMapping(List<FileSpace> discMap) {
        for (int i = discMap.size() - 1; i > 0; --i) {
            if (discMap.get(i) instanceof File file && file.space > 0) {
                return file;
            }
        }
        return null;
    }

    private static boolean noMoreFilesLeft(int position, List<FileSpace> toCheck) {
        if (position == toCheck.size() - 1) {
            return true;
        }
        return toCheck.subList(position, toCheck.size()).stream().allMatch(file -> file instanceof FreeSpace);
    }

    private static double calculateCheckSum(List<File> onlyFiles) {
        double checkSum = 0;
        int index = 0;
        for (File onlyFile : onlyFiles) {
            for (int i = 0; i < onlyFile.space; i++) {
                checkSum += index * onlyFile.id;
                index++;
            }
        }
        return checkSum;
    }
}