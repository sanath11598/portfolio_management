Program Requirements - 
The following items  must be placed in the root directory where the program is running. 
	    1. JAR - "Assignment6.jar"
	    2. Portfolio schema - "portfolioSchema.xsd"
    Directory Structure - 
        root_directory/
            - Assignment6.jar
            - portfolioSchema.xsd

The program can be viewed through either the text based UI or the Java Swing GUI.

-STEPS TO USE THE TEXT BASED UI
1. open cmd in the current working directory.
type `java -jar Assignment6.jar cmd`

2. Enter 1 to "Create User". 

3. Enter a username. 

4. Enter 1 to "Create Portfolio".

5. Press 2 to select "Flexible Portfolio".

6. Enter a portfolio name. 

6. Press 1 to "Buy Stocks".

7. type "goog" as the ticker name.

8. type "50" as the quantity.

9. type "2012-01-10" as the date of purchase.

10. type "20" as the commission.

follow steps 6 to 10 for the following stocks
	Ticker: "AAPL" Quantity: "1234" Date:"2017-01-26" Commission: "150"
	Ticker: "EBAY" Quantity: "2" Date: "2018-06-07" Commission: "3"

11. Press 3 to "Exit Portfolio"

12. Press 2 to "Edit Portfolio"

13. Follow steps 6 to 11 to buy more stocks into an existing portfolio.

14. Press 4 to "Examine Portfolio Composition".

15. Press 2 to select Flexible Portfolio

16. Press the appropriate option to select the required portfolio. View the composition of the portfolio.

-Similar steps from 14 to 16 can be used to view the "Total Value of Portfolio", "Cost Basis of Portfolio" and "Portfolio Performance".
-Dates are to be provided in "yyyy-MM-dd" format to view portfolio valuation and cost basis.
-A date range is to be provided in "yyyy-MM-dd" format is to be provided to view the Portfolio Performance.

-STEPS TO USE THE GUI
1. open cmd in the current working directory.
type `java -jar Assignment6.jar gui`

2. Enter a username.

3. Click on  "Create User".
- Similar steps can be used to log in for an already existing user.

4. Click "Create Portfolio".

5. Enter a portfolio name. Click on "Create"

6. type "goog" as the ticker name.

7. type "50" as the quantity.

8. type "2012-01-10" as the date of purchase.

9. type "20" as the commission.

10. Click on "Buy" to buy stocks.

follow steps 6 to 10 for the following stocks
	Ticker: "AAPL" Quantity: "1234" Date:"2017-01-26" Commission: "150"
	Ticker: "EBAY" Quantity: "2" Date: "2018-06-07" Commission: "3"

11. Click on  "Exit"

-Steps similar from 4 to 11 can be used to "Sell" Stocks as well.
-The "Buy by weights" option can also be used in a similar way by entering a COMMA SEPARATED LIST OF TICKERS, COMMA SEPARATED WEIGHTS(in %),
 a date of purchase, total investment and commission associated with each transaction.
-The "Buy by DCA" option can also be used in a similar way as the "Buy by weights" feature. In this scenario, a start date,
 an end date and the investment interval(in number of days) must be mentioned additionally.

12. Click on  "Edit Portfolio"

-Steps similar form 4 to 11 can also be used to buy/buy by weights/buy by dollar cost averaging/sell
 into a pre-existing portfolio through the "Edit Portfolio" option.

13. Click on  "View Portfolio Composition", select your portfolio and select "View Composition"

- Features like viewing the "Total Value of Portfolio", "Cost Basis of Portfolio" and "Portfolio Performance" may also be performed in a similar way as viewing the
  portfolio composition.
-Dates are to be provided in "yyyy-MM-dd" format to view portfolio valuation and cost basis.
-A date range is to be provided in "yyyy-MM-dd" format is to be provided to view the Portfolio Performance.


Program Constraints - 
- Date range for the stock tickers: All dates are accepted. However, dates that pre-date the ticker's IPO cause the stock value to be stored as 0. 
  Dates that are in the future cause the stock price to be attributed with its latest price.

- XML file of the portfolio created externally has to be placed in the root directory and should follow the "portfolioSchema.xsd" format
  to be able to load it into the program. A transactions csv file must be in the root directory as well to load a flexible portfolio.
  It must have the same name as the XML portfolio name. For example, Portfolio file name: "abc.xml" Transactions file name: "abc.csv"
  Providing the name of the xml file(MUST INCLUDE THE .xml EXTENSION IN THE INPUT) should load both the xml and the csv files to the program.
  If only the XML file is provided without the csv, it will be assumed that the portfolio provided is inflexible/rigid.
  Please refer to the "sample_portfolio.xml" and "sample_portfolio.csv" files provided in the res folder for testing the loading feature.

- The transactions csv file to be loaded must have data in the following format:
  yyyy-MM-dd(date),Ticker_name(as per the below-mentioned file.),BUY/COMMISSION/SELL,quantity(stock quantity),total_price(total BUY/SELL/COMMISSION)
  A sample transactions csv file and a portfolio xml file will be provided in the res folder for your reference.
  After every BUY or a SELL line, it is expected that there is a COMMISSION line that has the same data as the BUY/SELL with the last value being the total commission paid.

- List of available stock tickers: All the tickers provided by AlphaVantage API. 
  The ticker list may also be seen in the "supported_ticker_list.txt" file provided.
