package util;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlphaVantageDataTest {

  @Test
  public void getStockPrice() {
    StockDataUtil stockDataUtil = new AlphaVantageData();

    System.out.println( stockDataUtil.getStockPrice("AAC","2022-11-24"));
  }

  @Test
  public void getTickers() {

  }
}