package util;

import static util.Constants.PORTFOLIOS_PATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import util.stocks.model.TransactionItem;




/**
 * Utility library with helper methods to process user inputs.
 */
public class Util {

  /**
   * Checks if the date provided as a string is in yyyy-MM-dd format.
   *
   * @param dateStr Date as string to be validated.
   * @return True if the string is in yyyy-MM-dd format.
   */
  public static boolean validateDate(String dateStr) {
    try {
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.UUUU_MM_DD)
              .withResolverStyle(ResolverStyle.STRICT);
      LocalDate.parse(dateStr, dateFormatter);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }

  /**
   * Checks if a portfolio XML created outside the program is in valid format.
   *
   * @param filePath Filepath of the XMl file to be validated.
   * @return True if the XML file is in valid format needed by the program.
   */
  public static boolean validateXml(String filePath) {
    try {
      DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document document = parser.parse(new File(filePath));
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      Source schemaFile = new StreamSource(new File("portfolioSchema.xsd"));
      Schema schema = factory.newSchema(schemaFile);

      Validator validator = schema.newValidator();
      validator.validate(new DOMSource(document));
    } catch (ParserConfigurationException | SAXException | IOException e) {
      System.out.println(e);
      return false;
    }
    return true;
  }

  /**
   * Ensures that an uploaded transactions csv file has a valid date, ticker symbol, transaction
   * type, and total value of the transaction.
   *
   * @param filePath the file path having the transactions file.
   * @return true if the csv is valid, else returns false.
   */
  public static boolean validateCsv(String filePath) {
    String line = "";
    try {
      BufferedReader br = new BufferedReader(new FileReader(filePath));
      while ((line = br.readLine()) != null) {
        String[] elements = line.split(",");
        new TransactionItem(elements[0],
                elements[1],
                Constants.TransactionType.valueOf(elements[2]),
                Double.parseDouble(elements[3]), Double.parseDouble(elements[4]));
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * Helper method to load external Portfolio XML file to the user's list of portfolios.
   *
   * @param username       User to whom the portfolio belongs to.
   * @param filename       XMl file of the portfolio.
   * @param targetFileName the file to be copied.
   * @throws RuntimeException when an error occurs when copying the file to program's file storage.
   */
  public static void copyFileToSystem(String username,
                                      String filename,
                                      String targetFileName) {
    Path target = Paths.get(PORTFOLIOS_PATH, username, targetFileName);
    try {
      Files.copy(Path.of(filename), target);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Checks if the string is a valid positive integer.
   *
   * @param str String to be validated.
   * @return True if the string is a valid positive integer.
   */
  public static boolean isValidStockQuantity(String str) {
    try {
      double num = Double.parseDouble(str);
      return (num > 0.0);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Compares two dates. Provides a positive number if date1 is greater than date2,
   * a negative number if date1 is lesser than date2 and a zero if they are the same.
   *
   * @param date1 The first date to be compared.
   * @param date2 The second date to be compared.
   * @return positive integer if date1 greater than date2, negative integer if date1
   *         less than date2, a zero if date1 equals date2.
   */
  public static int compareDates(String date1, String date2) {
    try {
      SimpleDateFormat sdFormat = new SimpleDateFormat(Constants.YYYY_MM_DD);
      Date d1 = sdFormat.parse(date1);
      Date d2 = sdFormat.parse(date2);
      return d1.compareTo(d2);
    } catch (ParseException e) {
      throw new RuntimeException("Date is in invalid format.");
    }
  }

  /**
   * Reading the list of transactions lines from a given flexible portfolio.
   *
   * @param username      the username associated with the portfolio.
   * @param portfolioName the portfolio file name for which the transactions need to be fetched.
   * @return a List of TransactionItem objects that each represent a buy/sell/commission action.
   */
  public static List<TransactionItem> getPortfolioTransactions(String username,
                                                               String portfolioName) {
    List<TransactionItem> result = new ArrayList<>();
    Path path = Paths.get(PORTFOLIOS_PATH, username,
            Constants.FLEXIBLE + portfolioName + ".csv");
    if (Files.exists(path)) {
      try {
        BufferedReader csvReader = new BufferedReader(new FileReader(path.toFile()));
        String row;
        while ((row = csvReader.readLine()) != null) {
          String[] data = row.split(",");
          result.add(new TransactionItem(data[0], data[1],
                  Constants.TransactionType.valueOf(data[2]),
                  Double.parseDouble(data[3]), Double.parseDouble(data[4])));
        }
        csvReader.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return result;
  }

  /**
   * Provides a date list using the input from-date and to-date.
   * The date list is generated based on the date range.
   * The date list has a size of atleast 5 and a maximum size of 30.
   * An exception is thrown if the from-date is greater than the to date or if the range has
   * less than 5 days in it.
   *
   * @param fromDate the from-date.
   * @param toDate   the to-date.
   * @return The list of dates between the fromDate and the current time.
   */
  public static List<String> giveDateRange(String fromDate, String toDate) {

    if (fromDate.compareTo(toDate) >= 0) {
      throw new IllegalArgumentException("The from-date is greater than or equal to the to-date");
    }

    List<String> validDates = new ArrayList<>();
    DateFormat sdf = new SimpleDateFormat(Constants.YYYY_MM_DD);
    Date d1 = null;
    Date d2 = null;

    try {
      d1 = sdf.parse(fromDate);
      d2 = sdf.parse(toDate);

      LocalDate localFromDate = Instant.ofEpochMilli(d1.getTime())
              .atZone(ZoneId.systemDefault())
              .toLocalDate();

      LocalDate localToDate = Instant.ofEpochMilli(d2.getTime())
              .atZone(ZoneId.systemDefault())
              .toLocalDate();

      long diffInMillies = Math.abs(d2.getTime() - d1.getTime());
      long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

      if (diff < 5) {
        throw new IllegalArgumentException("The working date ranges are less than 5.");
      }

      if (diff <= 30) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.YYYY_MM_DD);

        while (localFromDate.compareTo(localToDate) < 0) {
          if (formatter.format(localFromDate).compareTo(toDate) > 0) {
            validDates.add(toDate);
          } else {
            validDates.add(formatter.format(localFromDate));
          }
          localFromDate = localFromDate.plusDays(1);
        }
        return validDates;
      } else if (diff / 7 < 5) {
        //If the diff/(7) < 5 - we need to print day wise by skipping a few days.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.YYYY_MM_DD);

        while (localFromDate.compareTo(localToDate) < 0) {
          if (formatter.format(localFromDate).compareTo(toDate) > 0) {
            validDates.add(toDate);
          } else {
            validDates.add(formatter.format(localFromDate));
          }
          localFromDate = localFromDate.plusDays(2);
        }
      } else if (diff / 7 >= 5 && diff / 7 <= 30) {
        //If the diff/(7) >= 5 and  <=30 - we need to print week wise.
        validDates = Util.getWeeks(fromDate, toDate);
        return validDates;
      } else if (diff / 7 > 30 && diff / 30 < 5) {
        //If the diff/7 >30 and diff/30 < 5 - we need to print week wise by skipping a few weeks.
        List<String> result;
        result = Util.getWeeks(fromDate, toDate);
        for (int i = 0; i < result.size(); i += 2) {
          if (i % 2 == 0) {
            validDates.add(result.get(i));
          }
        }
        return validDates;
      } else if (diff / 30 >= 5 && diff / 30 <= 30) {
        //If the diff/30 >=5 and <= 30 - we need to print month wise.
        validDates = Util.getMonths(fromDate, toDate);
        return validDates;

      } else if (diff / 365 < 5 && diff / 30 > 30) {
        //If the diff/365 < 5 and diff/30 >30 - we need to print monthly by skipping a few months
        List<String> result;
        result = Util.getMonths(fromDate, toDate);

        for (int i = 0; i < result.size(); i += 2) {
          if (i % 2 == 0) {
            validDates.add(result.get(i));
          }
        }
        return validDates;
      } else if (diff / 365 > 30) {
        //If the diff/365 >30, WE DON'T SUPPORT THE RANGE.
        throw new IllegalArgumentException("We dont support this date range. Please enter "
                + "dates that are under 20 years.");

        //If the diff/365 >=5 and <= 30 - we need to print year wise.
      } else {
        validDates = Util.getYears(fromDate, toDate);
        return validDates;
      }

    } catch (ParseException e) {
      throw new RuntimeException("The date range provided is not supported "
              + "or the input is invalid.");
    }
    return validDates;
  }


  private static List<String> getMonths(String fromDate, String toDate) {
    int count = 0;
    String currDate;

    List<String> validDates = new ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.UUUU_MM_DD)
            .withResolverStyle(ResolverStyle.STRICT);

    currDate = fromDate;
    LocalDate localFromDate;

    do {
      count++;
      currDate = lastDateOfMonth(currDate);
      localFromDate = LocalDate.parse(currDate, dateFormatter);
      if (currDate.compareTo(toDate) > 0) {
        validDates.add(toDate);
      } else {
        validDates.add(dateFormatter.format(localFromDate));
      }
      localFromDate = localFromDate.plusDays(1);
      currDate = localFromDate.toString();
    }
    while (currDate.compareTo(toDate) <= 0);
    return validDates;
  }

  private static String lastDateOfMonth(String date) {

    DateFormat sdf = new SimpleDateFormat(Constants.YYYY_MM_DD);
    Date today = null;
    try {
      today = sdf.parse(date);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);

    calendar.add(Calendar.MONTH, 1);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.add(Calendar.DATE, -1);

    Date lastDayOfMonth = calendar.getTime();

    return sdf.format(lastDayOfMonth);

  }

  private static List<String> getWeeks(String fromDate, String toDate) {
    int count = 0;
    String currDate;
    List<String> validDates = new ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.UUUU_MM_DD)
            .withResolverStyle(ResolverStyle.STRICT);

    currDate = fromDate;
    LocalDate.parse(currDate, dateFormatter);
    LocalDate localFromDate;

    do {
      count++;
      currDate = lastDateOfWeek(currDate);
      localFromDate = LocalDate.parse(currDate, dateFormatter);
      if (currDate.compareTo(toDate) > 0) {
        validDates.add(toDate);
      } else {
        validDates.add(dateFormatter.format(localFromDate));
      }
      localFromDate = localFromDate.plusDays(1);
      currDate = localFromDate.toString();
    }
    while (currDate.compareTo(toDate) <= 0);
    return validDates;
  }

  private static String lastDateOfWeek(String date) {
    DateFormat sdf = new SimpleDateFormat(Constants.YYYY_MM_DD);

    try {
      Date d1 = sdf.parse(date);
      LocalDate localDate = Instant.ofEpochMilli(d1.getTime())
              .atZone(ZoneId.systemDefault())
              .toLocalDate();

      LocalDate end = localDate;
      while (end.getDayOfWeek() != DayOfWeek.FRIDAY) {
        end = end.plusDays(1);
      }
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.YYYY_MM_DD);

      return formatter.format(end);

    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<String> getYears(String fromDate, String toDate) {
    int count = 0;
    String currDate;
    List<String> validDates = new ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.UUUU_MM_DD)
            .withResolverStyle(ResolverStyle.STRICT);

    currDate = fromDate;
    LocalDate localFromDate;

    do {
      count++;
      currDate = lastDateOfYear(currDate);
      localFromDate = LocalDate.parse(currDate, dateFormatter);
      if (currDate.compareTo(toDate) > 0) {
        validDates.add(toDate);
      } else {
        validDates.add(dateFormatter.format(localFromDate));
      }
      localFromDate = localFromDate.plusDays(1);
      currDate = localFromDate.toString();
    }
    while (currDate.compareTo(toDate) <= 0);
    return validDates;
  }

  private static String lastDateOfYear(String date) {
    DateFormat sdf = new SimpleDateFormat(Constants.YYYY_MM_DD);
    Date today = null;
    try {
      today = sdf.parse(date);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);

    calendar.add(Calendar.YEAR, 1);
    calendar.set(Calendar.MONTH, 0);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.add(Calendar.DAY_OF_MONTH, -1);

    Date lastDayOfMonth = calendar.getTime();

    return sdf.format(lastDayOfMonth);
  }

  /**
   * Provides a date range between a range for a provided interval in days. If the toDate provided
   * is in the future or is not provided at all, then the toDate is assumed to being the current
   * date in "yyyy-mm-dd" format. A date list is returned in accordance to this new date range.
   *
   * @param fromDate the from-date in "yyyy-mm-dd" format.
   * @param interval the number of interval days.
   * @param toDate   an optional to-date in "yyyy-mm-dd" format.
   * @return a list of dates that range from the fromDate to the toDate with an `interval` number of
   *         days inbetween consecutive dates in the list.
   */
  public static List<String> getDateListInRangeWithInterval(String fromDate, 
                                                            Integer interval, String... toDate) {

    List<String> validDates = new ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.UUUU_MM_DD)
            .withResolverStyle(ResolverStyle.STRICT);

    String currDate = fromDate;
    LocalDate localFromDate;
    LocalDate localToday = LocalDate.now();
    String today = localToday.format(dateFormatter);

    if (toDate.length > 1) {
      throw new IllegalArgumentException("More than one to-date passed as the argument.");
    }

    if (interval <= 0) {
      throw new IllegalArgumentException("The interval passed is lesser than or equal to zero.");
    }
    String endDate;
    if (toDate.length == 0 || (toDate.length == 1 && toDate[0].compareTo(today) > 0)) {
      endDate = today;
    } else {
      endDate = toDate[0];
    }

    if (fromDate.compareTo(endDate) > 0) {
      throw new IllegalArgumentException("The from date is greater than the to date.");
    }

    do {
      localFromDate = LocalDate.parse(currDate, dateFormatter);
      validDates.add(dateFormatter.format(localFromDate));
      localFromDate = localFromDate.plusDays(interval);
      currDate = localFromDate.toString();
    }
    while (currDate.compareTo(endDate) <= 0);
    return validDates;

  }

  /**
   * Validates if the weight provided is a valid double value.
   *
   * @param weight String representation of the weight.
   * @return true if the weight is within 0-100 range.
   */
  public static boolean validateWeight(String weight) {
    if (!weight.matches("\\d*\\.?\\d+")) {
      return false;
    }
    double doubleWeight = Double.parseDouble(weight);
    return !(doubleWeight < 0.0 || doubleWeight > 100);
  }

  /**
   * Rounds up the provided double value with the provided precision.
   *
   * @param value Double value to round off.
   * @param places Number of decimal places.
   * @return Decimal number after rounding-off upto the places provided.
   */
  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_EVEN);
    return bd.doubleValue();
  }

}


