Design Changes:
    - New Interface named Features added in the controller package.
    - GuiController, an Implementation of the Features interface added to the controller package.
    - ProgramRunner's main method takes an argument to decide if a text based UI or the swing UI is to be used while running the program.
    - The new GuiController takes the Model interface as an argument instead of a Model, View and Readable interfaces.
    - GuiView, an Interface that acts as a template to all the Swing GUI screens. Has methods to 
               add action listeners to elements of the JFrame.
    - AbstractScreen, an Implementation of GuiView with common code that all the other GuiView 
                      screens share.
    - PortfolioOperation interface added to the model package to implement the Visitor Design pattern.
    - BuyBySip and BuyByWeights classes added to extend the functionality of the model using the Visitor Design pattern.
    - This design pattern will prevent the modification of the main model interface. A new class extending the PortfolioOperation
      interface can be added for each new feature that has to be added to the program.
    - The PortfolioManagementModel Interface has an "accept" method added to it to incorporate the visitor design pattern.


Controller Interface
	Has a run method where tasks are delegated to the Model and View.

ControllerImpl
  	The controller implementation has takes the PortfolioManagementModel interface as the model argument.
	It also takes the PortfolioManagementView interface as the view argument. 
	A Readable interface is used to take inputs.
	The implementation has methods that split up the controllers work based on the different view pages.
	The controller validates inputs and passes them to Model and View methods.
	The username, portfolio name, stock quantity, transaction types(buy/sell/commission),
	 and dates are all validated here.

PortfolioManagementModel
	An interface that describes a single user.
	A single portfolio can be created at a time and written to a file storage. 
	Stocks can be added to the portfolio.
	All the portfolios of a user can be fetched and read to a Portfolio object. 
	
PortfolioManagementModelImpl
	Implements the PortfolioManagementModel interface. 
	It uses an object of the Portfolio interface to create/read a Portfolio.
	
Portfolio
	It adds stocks, constructs an XML document of the portfolio, creates a list of stock data for valuation and composition.
	Stocks can now be removed from a portfolio. A list of dates and the valuation of the portfolio for those dates can
	also be generated.

InflexiblePortfolioImpl
	Creates a portfolio with the portfolio name, date of creation and a list of Stock objects.
	The portfolio is constructed as a xml object.
	Each stock object is converted to a xml separately and is appended to the portfolio xml object.
	The portfolio composition and valuation is provided as a list of Stock attributes represented as a string.
	If redundant ticker names are given to create a stock, the stock quantities are added.

FlexiblePortfolioImpl
    Represents a flexible portfolio where Stock objects can be added and removed from the portfolio.
    The portfolio is constructed as a xml object.
    Each stock object is converted to a xml separately and is appended to the portfolio xml object.
    The portfolio composition and valuation is provided as a list of Stock attributes represented as a string.
    If redundant ticker names are given to create a stock, the stock quantities are added.

Stock
	The Stock interface represents a stock present in the portfolio.
	Each stock has a name, a quantity.
	Each stock object is written as a xml document to be used by the portfolio object.

StockImpl
	The StockImpl class implements the Stock object. 
	It takes the ticker name, quantity of stock purchased as constructor arguments.

PortfolioManagementView
	It is an interface that prints messages, errors, success messages, menu based views and table based views.

PortfolioManagementViewImpl
	It is an implementation of the PortfolioManagementView interface. 
	It's methods receive arguments as Strings or Lists of Strings. 
	Messages, errors and success messages are printed using String arguments.
	A list of Strings data represent stock attributes are used to print portfolio compositions and valuations in a tabular format.
	A List of tuples are used to represent the performance of the portfolio for a specific set of dates.

Features Interface
    It has all the methods used by the controller for the GUI, "GuiController".
    It has methods for buying and selling stock, editing, viewing, valuating and calculating the cost basis of portfolios.

GuiController
    Implements the Features interface. Interacts with the Model and the View, allows the user to
    interact with the program using the GUI.

GuiView Interface
    Interface of the Graphical User Interface for the Portfolio Management View. Adds action
    listeners to the GUI elements and sets the visibility of the screen to switch control between
    multiple screens.

Abstract Screen
    Generic implementation of GuiView interface. Adds action listeners to GUI objects in the screen.
    Allows to toggle visibility of the screen.

BuyByWeightsScreen
    Displays a GUI screen that allows user to buy multiple stocks in a single transaction based on weights.

BuySellStocksPage
    Displays a GUI screen that allows user to buy/sell stocks in the chosen portfolio, on a particular date.
	
CreatePortfolioScreen
    Displays a GUI screen that allows user to create a portfolio.

CreatePortfolioWithSipScreen
    Displays a GUI screen that allows user to create a portfolio with a basket of stocks and
    periodically invest in the basket of stocks.

DrawGraph
    Creates a Line Graph using Graphics2D for a given set of data points.

EditPortfolioPage
    Displays a GUI screen that allows user to edit a portfolio and buy/sells stocks in the chosen portfolio.

ErrorScreen
    Displays a GUI Error screen with a message to the user.

GraphScreen
   Displays the performance of a portfolio as a line chart. The x-axis has the date and the y-axis has the value of the portfolio.

HomeScreen
  Displays a GUI screen that allows user choose and interact with different features offered in the program.

InfoScreen
  Displays a GUI Info screen with a message to the user.

LoginScreen
    Displays a GUI screen that allows user to either log in or create an account in the program.

PortfolioManagementViewImpl
    An implementation of the PortfolioManagementView class that provides a text based
    view for all it's interface methods.

ShowTableScreen
    Display a JTable as a popup screen with the provided table headers and data.

ViewCompositionPage
   Displays a GUI screen that allows user to choose a portfolio and provide a date to view the
   composition of the portfolio on that specified date.

ViewCostBasisPage
  Displays a GUI screen that allows user to choose a portfolio and provide a date to view the
  cost-basis of the portfolio upto that specified date.

ViewPerformancePage
  Displays a GUI screen that allows user to choose a portfolio and provide a date range to view
  the performance of the portfolio for that specified date range.

ViewValuationPage
   Displays a GUI screen that allows user to choose a portfolio and provide a date to view the
   valuation of the portfolio on that specified date.

PortfolioOperation
    Operation that can be performed on the Portfolio object. This function extends the
    functionality of the PortfolioManagementModel interface and allows flexibility to add
    more features.

BuyBySip
    Allows the user to create a portfolio with a basket of stocks and periodically invest in the
    basket of stocks.

BuyByWeights
    Implements the PortfolioOperation interface and allows the user to bulk purchase multiple
    stocks in a single transaction on a specific date.