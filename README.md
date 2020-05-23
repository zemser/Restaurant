# Restaurant
Object-oriented system that simulates a restaurant management system.
The program will open the restaurant, assign customers to tables, make orders,
provide bills to the tables, and other requests as described below.

The program will get a config file as an input, which includes all required information about the
restaurant opening - number of tables, number of available seats in each table, and details
about the dishes in the menu.

Each table in the restaurant has a limited amount of seats available.
The restaurant can’t connect tables together, nor accommodates more customers
than the number of seats available in a table. In this restaurant, it’s impossible to add new
customers to an open table, but it’s possible to move customers from one table to another.

A bill of a table is the total price of all dishes ordered for that table.

Program Flow: 

The program receives the path of the config file as the first command line argument. Once the
program starts, it opens the restaurant by calling the start() function, followed by printing
“Restaurant is now open!” to the screen.
Then the program waits for the user to enter an action to execute. After each executed action,
the program waits for the next action in a loop. The program ends when the user enters the
action "closeall".

Classes:

Restaurant – This class holds a list of tables, menu, and other information that is relevant to
the restaurant.

Table – This class represents a table in the restaurant. Each table has a finite number of
available seats (provided in the config file). It also holds a status flag that indicates whether the
table is open, a list of orders done in this table, and a list of customers. Table ids starts from 0.

Dish – This class represents a dish in the menu. It has an id, name, price, and a type 

Customer – This is an abstract class for the different customers classes. There are several
types of customers, and each of them has a different ordering strategy. Each customer that
arrives to the restaurant will get a number (id) that will serve as an identifier as long as he is
seating in the restaurant. This number will be a serial number of all customers that arrived so
far, starting from 0 (the first customer will get 0, second customer will get 1 , etc.). Note that
this “id” serves as a temporary identifier- if a customer leaves the restaurant and then comes
back, he will get a new id.

BaseAction – This is an abstract class for the different action classes. The class contains a
pure virtual method act(Restaurant& rest) which receives a reference to the restaurant as a
parameter and performs an action on it; A pure virtual method “toString()” which returns a 
string representation of the action; A flag which stores the current status of the action:
“Pending” for actions that weren't performed yet, “Completed” for successfully completed
actions, and “Error” for actions which couldn't be completed.
After each action is completed- if the action was completed successfully, the protected method
complete() should be called in order to change the status to “COMPLETED”. If the action
resulted in an error then the protected method error(std::string errorMsg) should be called, in
order to change the status to “ERROR” and update the error message.
When an action results in an error, the program should print to the screen:
"Error: <error_message>". 

Actions:

• Open Table – Opens a given table and assigns a list of customers to it. If the table
doesn't exist or is already open, this action should result in an error: “Table does not
exist or is already open”.
Syntax: open <table_id> <customer_1>,< customer_1_strategy> <customer_2>,<
customer_2_ strategy> ….
where the <customer_ strategy> is the 3-letter code for the ordering strategy. 

• Order – Takes an order from a given table. This function will perform an order from
each customer in the table, and each customer will order according to his strategy. After
finishing with the orders, a list of all orders should be printed. If the table doesn't exist,
or isn't open, this action should result in an error: “Table does not exist or is not open”.
Syntax: order <table_id>.

• Move customer – Moves a customer from one table to another. Also moves all orders
made by this customer from the bill of the origin table to the bill of the destination table.
If the origin table has no customers left after this move, the program will close the origin
table. If either the origin or destination table are closed or doesn't exist, or if no
customer with the received id is in the origin table, or if the destination table has no
available seats for additional customers, this action should result in an error: “Cannot
move customer”.
Syntax: move <origin_table_id> <dest_table_id> <customer_id>.

• Close – Closes a given table. Should print the bill of the table to the screen. After this
action the table should be open for new customers. If the table doesn't exist, or isn't
open, this action should result in an error: “Table does not exist or is not open”.
Syntax: close <table_id>.

• Close all – Closes all tables in the restaurant, and then closes the restaurant and exits.
The bills of all the tables that were closed by that action should be printed sorted by the
table id in an increasing order. Note that if all tables are closed in the restaurant, the
action will just close the restaurant and exit. This action never results in an error.
Syntax: closeall.

• Print menu – Prints the menu of the restaurant. This action never results in an error.
Each dish in the menu should be printed in the following manner:
<dish_name> <dish_type> <dish_price>
Syntax: menu.

• Print table status – Prints a status report of a given table. The report should include
the table status, a list of customers that are seating in the table, and a list of orders
done by each customer. If the table is closed, only the table status should be printed.
This action never results in an error.
Syntax: status <table_id>.

• Print actions log – Prints all the actions that were performed by the user (excluding
current log action), from the first action to the last action. This action never results in an
error.
Syntax: log.

• Backup restaurant – save all restaurant information (restaurant’s status, tables, orders,
menu and actions history) in a global variable called “backup”. The program can keep
only one backup: If it's called multiple times, the latest restaurant's status will be stored
and overwrite the previous one. This action never results in an error.
Syntax: backup.

• Restore restaurant – restore the backed up restaurant status and overwrite the current
restaurant status (including restaurant’s status, tables, orders, menu and actions
history). If this action is called before backup action is called (which means “backup” is
empty), then this action should result in an error: “No backup available”
Syntax: restore.
