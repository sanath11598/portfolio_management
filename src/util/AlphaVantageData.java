package util;

import static util.Constants.API_KEY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Implements StockDataUtil interface.
 * Fetches stock data from the Alpha Vantage API.
 * It has a public method for each type of API endpoint being called.
 */
public class AlphaVantageData implements StockDataUtil {


  /**
   * Fetch the price for a particular stock ticker on a particular date.
   * If the stock price is not availble for the given date, the price for the next closest
   * date is returned.
   *
   * @param ticker Stock ticker.
   * @param date   Date for which the stock price is fetched.
   * @return Stock price on a particular date.
   */
  @Override
  public Double getStockPrice(String ticker, String date) {
    String endpoint = "https://www.alphavantage.co/query?"
            + "function=TIME_SERIES_DAILY"
            + "&symbol=" + ticker
            + "&outputsize=full"
            + "&apikey=" + API_KEY
            + "&datatype=csv";
    if (!Files.exists(Paths.get("stockData"))) {
      try {
        Files.createDirectories(Paths.get("stockData"));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    //Get the current file.
    Path p = Paths.get("stockData", ticker + ".csv");
    File f = new File(p.toUri());
    String inputLine;
    String topDate;
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String today = sdf.format(new Date());


    // Check if the ticker is in StockData
    if (f.exists()) {
      Scanner sc1;
      try {
        sc1 = new Scanner(f);
        topDate = sc1.next().split(",")[0];
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
      //If date<=today and topDate < date perform update
      if (topDate.compareTo(date) <= 0) {
        RandomAccessFile file;
        try {
          Double price = null;
          InputStream in = standardApiCall(endpoint);
          String br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                  .lines().collect(Collectors.joining("\n"));
          List<String> lines = List.of(br.split("\n"));
          List<String> writeLines = new ArrayList<>();
          for (String line : lines.subList(1, lines.size())) {
            String[] cols = line.split(",");
            if (cols[0].compareTo(topDate) <= 0) {
              break;
            }
            writeLines.add(cols[0] + "," + cols[4]);
          }
          //Latest date in the csv is treated as the next working day.
          Double nextDayPrice = Double.parseDouble(lines.subList(1, lines.size())
                  .get(0).split(",")[4]);
          for (String line : lines.subList(1, lines.size())) {

            String[] cols = line.split(",");
            if ((date.compareTo(cols[0]) > 0 || date.compareTo(today) >= 0)) {
              price = nextDayPrice;
              break;
            }
            if ((date.compareTo(cols[0]) == 0 || date.compareTo(today) >= 0)) {
              price = Double.parseDouble(cols[4]);
              break;
            }
            nextDayPrice = Double.parseDouble(cols[4]);
          }

          file = new RandomAccessFile(new File(p.toUri()), "rw");
          file.seek(0); // to the beginning
          for (String line : writeLines) {
            file.write(line.getBytes());
            file.write("\n".getBytes());
          }
          file.close();

          return price;

        } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

      } else if (date.compareTo(topDate) <= 0) {
        // If date < topDate - the file exists, and we need to fetch from the file.
        try {
          sc1 = new Scanner(f);
          Double nextDayPrice = Double.parseDouble(sc1.next().split(",")[1]);
          sc1.close();
          Scanner sc = new Scanner(f);
          do {
            inputLine = sc.next();
            String[] cols = inputLine.split(",");
            if (date.compareTo(cols[0]) > 0) {
              return nextDayPrice;
            }
            if (date.compareTo(cols[0]) == 0) {
              return Double.parseDouble(cols[1]);
            }
            nextDayPrice = Double.parseDouble(cols[1]);
          }
          while (sc.hasNext());
          sc.close();
          //If the date is lesser than the lower range.

        } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
        }
      }

    } else {
      // ticker is not in the stockData folder.
      try {
        Double price = null;
        InputStream in = standardApiCall(endpoint);
        String br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        List<String> lines = List.of(br.split("\n"));
        //Latest date in the csv is treated as the next working day.
        Double nextDayPrice = Double.parseDouble(lines.subList(1, lines.size())
                .get(0).split(",")[4]);
        for (String line : lines.subList(1, lines.size())) {

          String[] cols = line.split(",");
          if ((date.compareTo(cols[0]) > 0 || date.compareTo(today) >= 0)) {
            price = nextDayPrice;
            break;
          }
          if ((date.compareTo(cols[0]) == 0 || date.compareTo(today) >= 0)) {
            price = Double.parseDouble(cols[4]);
            break;
          }
          nextDayPrice = Double.parseDouble(cols[4]);
        }
        Files.createFile(p);
        FileWriter csvWriter = new FileWriter(f);
        for (String line : lines.subList(1, lines.size())) {
          String[] cols = line.split(",");
          csvWriter.append(cols[0] + "," + cols[4]);
          csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();

        if (price == null) {
          return Double.valueOf(0);
        } else {
          return price;
        }

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return Double.valueOf(0);
  }

  /**
   * Get list of available stock tickers.
   *
   * @return List of available stock tickers.
   */
  @Override
  public List<String> getTickers() {
    List<String> tickers = new ArrayList<>();
    String endpoint = "https://www.alphavantage.co/query?function=LISTING_STATUS&apikey=" + API_KEY;
    InputStream in = standardApiCall(endpoint);

    String br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
            .lines().collect(Collectors.joining("\n"));
    List<String> lines = List.of(br.split("\n"));

    for (String line : lines) {
      tickers.add(line.split(",")[0]);
    }
    return tickers;
  }


  private InputStream standardApiCall(String endpoint) {
    URL url;
    try {
      url = new URL(endpoint);
      return url.openStream();
    } catch (MalformedURLException e) {
      throw new RuntimeException("the AlphaVantage API has either changed or "
              + "no longer works");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
