//
// Created by shlezimi@wincs.cs.bgu.ac.il on 11/7/18.
//
#ifndef PROJECT_ONE_TABLE_H
#define PROJECT_ONE_TABLE_H
#include <vector>
#include "../include/Customer.h"
#include "../include/Dish.h"

typedef std::pair<int, Dish> OrderPair;

class Table{
public:
    //constructor
    Table(int t_capacity);
    //copy constructor
    Table(const Table &source);
    //copy assignment operator
    Table& operator=(const Table& other);
    //destructor
    ~Table();
    // Move Constructor
    Table(Table&& source);
    //Move Assignment
    Table& operator=(Table &&other);

    int getCapacity() const;
    void addCustomer(Customer* customer);
    void removeCustomer(int id);
    Customer* getCustomer(int id);
    std::vector<Customer*>& getCustomers();
    std::vector<OrderPair>& getOrders();
    void order(const std::vector<Dish> &menu);
    void openTable();
    void closeTable();
    int getBill();
    bool isOpen();
    void printOrders();
    void addOrders(std::vector<OrderPair> &p);
    std::vector<OrderPair> removeOrders(int id);
    Table* clone();

    Dish findDishById(const std::vector<Dish> &menu, int id);
    void clear();
    void copy(const Table& other);
private:
    int capacity;
    bool open;
    std::vector<Customer*> customersList;
    std::vector<OrderPair> orderList; //A list of pairs for each order in a table - (customer_id, Dish)
};

#endif