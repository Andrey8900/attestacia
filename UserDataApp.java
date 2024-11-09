import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;

public class UserDataApp {

  private static final String FILE_EXTENSION = ".txt";
  private static final String DATE_FORMAT = "dd.MM.yyyy";

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println(
        "Введите фамилию, имя, отчество, дату рождения (формат dd.mm.yyyy), номер телефона и пол (м/ж или m/f): ");
    String input = scanner.nextLine();

    try {
      processUserInput(input);
    } catch (IllegalArgumentException | DateTimeParseException | IOException e) {
      System.err.println(e.getMessage());
    }

    scanner.close();
  }

  private static void processUserInput(String input)
      throws IllegalArgumentException, DateTimeParseException, IOException {
    String[] parts = input.split("\\s+", 6);
    if (parts.length != 6) {
      throw new IllegalArgumentException("Необходимо ввести ровно 6 элементов!");
    }

    String surname = validateAndNormalize(parts[0], "Фамилия");
    String name = validateAndNormalize(parts[1], "Имя");
    String patronymic = validateAndNormalize(parts[2], "Отчество");
    LocalDate birthdate = parseBirthdate(parts[3]);
    long phoneNumber = parsePhoneNumber(parts[4]);
    char gender = parseGender(parts[5].toLowerCase(Locale.ENGLISH));

    saveToFile(surname, name, patronymic, birthdate, phoneNumber, gender);
  }

  private static String validateAndNormalize(String value, String fieldName) throws IllegalArgumentException {
    if (value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " не может быть пустым.");
    }
    return value.trim();
  }

  private static LocalDate parseBirthdate(String dateString) throws DateTimeParseException {
    return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT));
  }

  private static long parsePhoneNumber(String numberString) throws NumberFormatException {
    return Long.parseLong(numberString);
  }

  private static char parseGender(String genderString) throws IllegalArgumentException {
    switch (genderString.toLowerCase()) {
      case "м":
      case "мужской":
      case "male":
      case "m":
        return 'м';
      case "ж":
      case "женский":
      case "female":
      case "f":
        return 'ж';
      default:
        throw new IllegalArgumentException("Пол должен быть либо 'м'/'ж' или 'm'/'f'");
    }
  }

  private static void saveToFile(String surname, String name, String patronymic, LocalDate birthdate, long phoneNumber,
      char gender)
      throws IOException {
    String fileName = surname + FILE_EXTENSION;
    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(fileName, true), StandardCharsets.UTF_8))) {
      String dataLine = String.format("%s %s %s %s %d %c%n",
          surname, name, patronymic, birthdate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)), phoneNumber, gender);
      writer.write(dataLine);
    }
  }
}
