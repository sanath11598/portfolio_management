The portfolio manager program works for the AlphaVantageAPI ticker list. 
Daily time series closing prices will be used to evaluate each stock and the profile as a whole.
Some features are accessible through a swing GUI and some through a text based view.
All features associated with rigid/inflexible portfolios are only accessible through the text based UI.
Dollar cost averaging related features on flexible portfolios are only accessible through the Swing GUI.

1.Creating a User :
    This feature is viewable through both GUI and text based UI.
	A username is provided. If the username exists in the program's "portfolios" folder as a subdirectory,
	a message is thrown stating that the user already exists. If not, a new sub folder with the username as its alias is made under the "portfolios" directory.
	The user page is then displayed. Underscore separated string can be given as the username.

2.Logging in as an existing user:
    This feature is viewable through both GUI and text based UI.
	A username is provided. If the username exists in the program's "portfolios" folder as a subdirectory,
	the user page is displayed. If a directory with the username as an alias is not present, a message is thrown stating that the user needs to be created.
	The starting page is displayed again. Underscore separated string can be given as the username.

3. Create a new portfolio as a user:
	-Creating a rigid portfolio:
	    This feature is only visible through the text based UI.
        A new Portfolio can be created by providing a name to it in the terminal.
        Stocks may be added to this portfolio.
        After all the required stocks are added to the portfolio, the portfolio is saved under the username directory
        as an XML file with the portfolio name as an alias.
        The portfolio name must be provided as an underscore separated string.
	-Creating a flexible portfolio:
	    This feature is viewable through both GUI and text based UI.
	    A new Flexible Portfolio can be created by providing a name to it.
        Stocks may be bought into this portfolio.
        Stocks in the portfolio may also be sold.
        After all the required stocks transactions are made with the portfolio, the portfolio is written under the username directory
        as a xml file with "flexible_" followed by the portfolio name as an alias.
        The portfolio name must be provided as an underscore separated string.

4. Load a profile into the user account through a valid xml file:
    This feature is viewable through both GUI and text based UI.
	A portfolio can be placed under the user as a xml file. The xml file is placed under portfolios/$username only if the xml file
	matches the provided "xsd" file. 
	The xml file must be present in the same root directory as the jar file or the project.
	The name of the xml file is to be input with the ".xml" extension.
    If only the xml file is present in the root directory, it is assumed to being a rigid/inflexible portfolio.
    If a csv with the same name as the xml, but containing the transaction data is provided, it is treated as a flexible portfolio.

    The schema of the csv file with transactions data is as follows:
      => yyyy-MM-dd(date),Ticker_name(as per the below-mentioned file.),BUY/COMMISSION/SELL,quantity(stock quantity),total_price
         A sample transactions csv file and a portfolio xml file will be provided in the res folder for your reference.
         After every BUY or a SELL line, it is expected that there is a COMMISSION line that has the same data as the BUY/SELL with the last value being the total commission paid.

5. View the Composition of a portfolio:
    This feature is viewable through both GUI and text based UI.
	After choosing the type of portfolio, a list of portfolios for the user account is printed in the text based UI.
	In the GUI, a list of flexible portfolios is displayed with radio buttons.
	Prints the stock name, quantity purchased for each stock in a portfolio as table.
	The menu option for the portfolio to be viewed must be selected in the text UI.
	If there are no portfolios, an error message is thrown stating the same.
		
6. View the valuation of a portfolio for a given date:
    This feature is viewable through both GUI and text based UI.
	After choosing the type of portfolio, a list of portfolios for the user account is printed for the text based UI.
	In the GUI, a list of flexible portfolios are displayed with radio buttons.
	Prints the stock name, quantity purchased as a table along with their valuation for a given date.
	The menu option for the portfolio to be valued must be selected and the date of valuation must be provided in "yyyy-MM-dd" format.
	If there are no portfolios to be displayed, an error message is thrown stating the same.	
	For rigid portfolios, the portfolio valuation is performed for all stocks in the portfolio for the given valuation date.
	For flexible portfolios, the portfolio valuation is performed for stock quantities purchased before and on the provided valuation date.

7. Move back to previous pages:
    This feature is viewable through both GUI and text based UI.
	Selecting this option displays the previous page.

8. Quit the program while viewing any of the menu pages:
	Entering "q" causes the program to quit in the Text based UI.
	Pressing the "X" button on the JFrame causes the program to quit in the GUI.

9. Add a stock to the portfolio:
    This feature is viewable through text based UI.
	Stocks can be added to a given portfolio after selecting this option.
	A valid ticker name should be provided as an input, post validation of the ticker name, the quantity of the stock to be purchased is to be entered.
	Stocks can be purchased in natural numbers that are not greater than Java's max Integer value.
	The date of stock purchase must be given in "YYYY-MM-DD" format.

10. Complete Portfolio Creation:
     This feature is viewable through both GUI and text based UI.
	 Choosing to Complete portfolio creation writes a xml file under portfolios/$username which has information regarding
	 the portfolio name, creation date and a child tree with information regarding stocks.

11. Buy Stocks:
     This feature is viewable through both GUI and text based UI.
     Stocks can be purchased in flexible portfolios by providing the ticker name, stock quantity,
     date of purchase and a commission associated with the transaction.
     If the date of stock purchase is in the future, the latest stock price is used to calculate the stock value.
     If the date of stock purchase predates the IPO timestamp, its value is set to zero.

12. Sell Stocks:
     This feature is viewable through both GUI and text based UI.
     Stocks can be sold in flexible portfolios by providing the ticker name, stock quantity,
     selling date, and a commission associated with the transaction.
     If the sell date of a ticker predates the oldest buy date of a ticker, the sell is considered invalid.
     If the sell date of a ticker predates the latest sell date in a transactions file, the sell is considered invalid.
     If the sell quantity is greater than the total stocks bought on a given date, the sell is invalid.
     If the portfolio doesn't have the ticker to be sold, the transaction is considered to being invalid.

13. Cost Basis of a flexible portfolio:
     This feature is viewable through both GUI and text based UI.
     The cost basis of a flexible portfolio for a given date is calculated as the total money spent on the portfolio
     through buys and commissions(associated with buys and sells) till that date.

14. Portfolio Performance:
    This feature is viewable through both GUI and text based UI.
    For a given date range, the portfolio's performance is plotted for different timestamps within the range.
    The timestamps for when the portfolio's valuation is to be assessed is determined based on the
    size of the input date range provided.
    The plot will have a minimum of 5 dates and a maximum of 30 dates. Timestamps may vary from
    daily, weekly, monthly, and yearly scales.

    For a rigid portfolio, the entire portfolio's stocks are valued for the given range. This is available only with the text based UI.
    For flexible portfolios, the portfolio valuation is performed for stock quantities purchased before the provided valuation date
    and the performance plots the valuation of the portfolio for each generated timestamp in the date range. This is available in both
    text based and GUI.

    The GUI represents the portfolio performance as a line chart.

15. Buying Stocks by Weights:
    This feature is only available through the GUI.
    A comma separated list of ticker names, percentage weights for each ticker, a date of purchase and the total amount of money
    along with a commission is provided. The total amount is divided amongst the tickers based on the weights and the equivalent 
    stock quantities are purchased for the mentioned date.

16. Create a new Portfolio/ Edit existing portfolio with Dollar Cost Averaging(DCA):
    This feature is only available through the GUI.
    A new DCA portfolio is created by taking a portfolio name, a comma separated list of tickers, their weights by percentage, a starting date,
    an ending date(may or may not be required) along with interval in days. A transaction amount to be invested and the commission to cut after every interval
    is also mentioned. Weighted purchasing of the mentioned stocks is done for the specified date range at the date intervals.

    Adding a DCA feature to an existing portfolio may also be done in a fashion that is similar to creating a new DCA portfolio.
    Instead of providing a new portfolio name, an existing portfolio is selected and the same set of steps associated with creating a new
    DCA portfolio is to be performed.

Program Constraints -
- Date range for the stock tickers: All dates are accepted. However, dates that pre-date the ticker's IPO cause the stock value to be stored as 0.
  Dates that are in the future cause the stock price to be attributed with its latest price.

- XML file of the portfolio created externally has to be placed in the root directory and should follow the "portfolioSchema.xsd" format
  to be able to load it into the program. A transactions csv file must be in the root directory as well to load a flexible portfolio.
  It must have the same name as the XML portfolio name. For example, Portfolio file name: "abc.xml" Transactions file name: "abc.csv"
  Providing the name of the xml file should load both the xml and the csv files to the program.
  If only the XML file is provided without the csv, it will be assumed that the portfolio provided is inflexible/rigid.

- The transactions csv file to be loaded must have data in the following format:
  yyyy-MM-dd(date),Ticker_name(as per the below-mentioned file.),BUY/COMMISSION/SELL,quantity(stock quantity),total_price(total BUY/SELL/COMMISSION)
  A sample transactions csv file and a portfolio xml file will be provided in the res folder for your reference.
  After every BUY or a SELL line, it is expected that there is a COMMISSION line that has the same data as the BUY/SELL with the last value being the total commission paid.

- List of available stock tickers: All the tickers provided by AlphaVantage API.
  The ticker list may also be seen in the "supported_ticker_list.txt" file provided.
