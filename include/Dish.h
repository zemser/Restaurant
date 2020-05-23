//
// Created by shlezimi@wincs.cs.bgu.ac.il on 11/7/18.
//

#ifndef ASSIGNMENT1_DISH_H
#define ASSIGNMENT1_DISH_H


#include <string>
#include <iostream>

enum DishType{
    VEG, SPC, BVG, ALC
};

class Dish{
public:
    Dish(int d_id, std::string d_name, int d_price, DishType d_type);
    int getId() const;
    std::string getName() const;
    int getPrice() const;
    DishType getType() const;
    std::string toString() const;
private:
    const int id;
    const std::string name;
    const int price;
    const DishType type;
};


#endif //ASSIGNMENT1_DISH_H

