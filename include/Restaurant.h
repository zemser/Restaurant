//
// Created by shlezimi@wincs.cs.bgu.ac.il on 11/7/18.
//

#ifndef PROJECT_ONE_RESTAURANT_H
#define PROJECT_ONE_RESTAURANT_H


#include <vector>
#include <string>
#include "../include/Dish.h"
#include "../include/Table.h"
#include "../include/Action.h"
#include <fstream>
#include <sstream>
#include <iostream>
using namespace std;

class Restaurant{
public:
    //Constructor
    Restaurant();
    //Constructor
    Restaurant(const std::string &configFilePath);
    //Copy Constructor
    Restaurant(const Restaurant &source);
    //copy assignment operator
    Restaurant& operator=(const Restaurant& other);
    //destructor
    ~Restaurant();
    // Move Constructor
    Restaurant(Restaurant&& source);
    //Move Assignment
    Restaurant& operator=(Restaurant &&other);

    void start();
    int getNumOfTables() const;
    Table* getTable(int ind) const;
    const std::vector<BaseAction*>& getActionsLog() const;
    std::vector<Dish>& getMenu();
    void clear();
    void copy(const Restaurant& other);

private:
    bool open;
    std::vector<Table*> tables;
    std::vector<Dish> menu;
    std::vector<BaseAction*> actionsLog;

    int numOfCustomers;
    int numOfTables;
};

#endif