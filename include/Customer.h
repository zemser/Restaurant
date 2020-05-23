//
// Created by shlezimi@wincs.cs.bgu.ac.il on 11/7/18.
//

#ifndef PROJECT_ONE_CUSTOMER_H
#define PROJECT_ONE_CUSTOMER_H


#include <vector>
#include <string>
#include "../include/Dish.h"
#include <iterator>
#include <limits.h>

class Customer{
public:
    Customer(std::string c_name, int c_id);
    virtual std::vector<int> order(const std::vector<Dish> &menu)=0;
    virtual std::string toString() const = 0;
    std::string getName() const;
    int getId() const;
    virtual Customer* clone()=0;
    virtual ~Customer() = 0;
private:
    const std::string name;
    const int id;
};


class VegetarianCustomer : public Customer {
public:
    VegetarianCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
    Customer* clone();
private:
    int prevOrderIdVeg;
    int prevOrderIdBvg;
};


class CheapCustomer : public Customer {
public:
    CheapCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
    Customer* clone();
private:
    bool ordered;
};


class SpicyCustomer : public Customer {
public:
    SpicyCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
    Customer* clone();
private:
    bool ordered;
};




class AlchoholicCustomer : public Customer {
public:
    AlchoholicCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
    Customer* clone();
private:
    int prevOrderId;
    int prevOrderPrice;

};

#endif //ASSIGNMENT1_CUSTOMER_H