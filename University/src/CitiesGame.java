import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class CitiesGame {

    private static Scanner scanner = new Scanner(System.in);

    private static int minPlayerNameLength = 2;
    private static int maxPlayerNameLength = 12;
    private static int minCityNameLength = 2;
    private static int maxCityNameLength = 15;

    private static String player1;
    private static String player2;

    private static int player1CityFormatErrors = 0;
    private static int player1GameRuleErrors = 0;
    private static int player2CityFormatErrors = 0;
    private static int player2GameRuleErrors = 0;

    private static boolean isPlayer1Turn;
    private static String nextFirstLetter = null;

    private static Set<String> listOfCities = new HashSet<>();

    public static void main(String[] args) {
        readProperties();

        System.out.println("Please enter Player 1 name:");
        player1 = readPlayerName();
        System.out.println("Please enter Player 2 name:");
        player2 = readPlayerName();

        startGameLoop();
    }

    private static void startGameLoop() {
        isPlayer1Turn = new Random().nextBoolean();
        System.out.println("The game has started!");
        while (true) {
            printGamePrompt(isPlayer1Turn ? player1 : player2);
            processTurn();
            if (shouldGameBeStopped()) {
                break;
            }
        }
        endGame();
    }

    private static void printGamePrompt(String playerName) {
        if (nextFirstLetter == null) {
            System.out.println(playerName + ", please enter city name:");
        } else {
            System.out.println(playerName + ", please enter city name starting with '" + nextFirstLetter + "':");
        }
    }

    private static void processTurn() {
        String input = scanner.nextLine();
        boolean isCityNameValid = validateCityName(input);
        if (isCityNameValid) {
            listOfCities.add(input);
            nextFirstLetter = Character.toString(input.charAt(input.length() - 1));
            isPlayer1Turn = !isPlayer1Turn;
        }
    }

    private static String readPlayerName() {
        while (true) {
            String input = scanner.nextLine();
            boolean isPlayerNameValid = validatePlayerName(input);
            if (isPlayerNameValid) {
                return input;
            }
        }
    }

    private static boolean validatePlayerName(String input) {
        if (input.length() < minPlayerNameLength) {
            System.out.println("Player name should not be shorter than " + minPlayerNameLength);
            return false;
        }

        if (input.length() > maxPlayerNameLength) {
            System.out.println("Player name should not be longer than " + maxPlayerNameLength);
            return false;
        }

        if (!input.matches("[a-zA-Z]+")) {
            System.out.println("Player name should contain only letters. No numbers or special characters.");
            return false;
        }
        return true;
    }

    private static boolean validateCityName(String input) {
        if (input.length() < minCityNameLength) {
            System.out.println("City name should not be shorter than " + minPlayerNameLength);
            saveCityFormatError();
            return false;
        }

        if (input.length() > maxCityNameLength) {
            System.out.println("City name should not be longer than " + maxPlayerNameLength);
            saveCityFormatError();
            return false;
        }

        if (!input.matches("[a-zA-Z]+")) {
            System.out.println("City name should contain only letters. No numbers or special characters.");
            saveCityFormatError();
            return false;
        }

        if (nextFirstLetter != null) {
            String inputFirstLetter = Character.toString(input.charAt(0));
            if (!nextFirstLetter.equalsIgnoreCase(inputFirstLetter)) {
                System.out.println("City name should start with '" + nextFirstLetter + "'");
                saveGameRuleError();
                return false;
            }
        }
        if (listOfCities.contains(input)) {
            System.out.println("City names should not repeat!");
            saveGameRuleError();
            return false;
        }
        System.out.println("Correct!");
        return true;
    }

    private static void saveCityFormatError() {
        if (isPlayer1Turn) {
            player1CityFormatErrors++;
        } else {
            player2CityFormatErrors++;
        }
    }

    private static void saveGameRuleError() {
        if (isPlayer1Turn) {
            player1GameRuleErrors++;
        } else {
            player2GameRuleErrors++;
        }
    }

    private static boolean shouldGameBeStopped() {
        if (player1CityFormatErrors > 4) {
            System.out.println(player1 + " has made city format errors 5 times. Game over. Winner is " + player2);
            return true;
        }
        if (player2CityFormatErrors > 4) {
            System.out.println(player2 + " has made city format errors 5 times. Game over. Winner is " + player1);
            return true;
        }
        if (player1GameRuleErrors > 2) {
            System.out.println(player1 + " has made city format errors 3 times. Game over. Winner is " + player2);
            return true;
        }
        if (player2GameRuleErrors > 2) {
            System.out.println(player2 + " has made city format errors 3 times. Game over. Winner is " + player1);
            return true;
        }
        return false;
    }

    private static void endGame() {
        // TODO print result to file
    }

    private static void readProperties() {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream("game.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("Failed to open properties file: " + e.getMessage());
            System.out.println("Using default values.");
        }

        try {
            minPlayerNameLength = Integer.parseInt(properties.getProperty("minPlayerNameLength"));
            maxPlayerNameLength = Integer.parseInt(properties.getProperty("maxPlayerNameLength"));
            minCityNameLength = Integer.parseInt(properties.getProperty("minCityNameLength"));
            maxCityNameLength = Integer.parseInt(properties.getProperty("maxCityNameLength"));
        } catch (NumberFormatException e) {
            System.out.println("Failed to read properties file: " + e.getMessage());
            System.out.println("Using default values.");
        }
    }
}
