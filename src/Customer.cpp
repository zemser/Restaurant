//
// Created by shlezimi@wincs.cs.bgu.ac.il on 11/7/18.
//

#include "../include/Customer.h"
Customer::Customer(std::string c_name, int c_id): name(c_name),id(c_id){}
std::string Customer::getName() const {
    return name;
}

int Customer::getId() const {
    return id;
}

std::vector<int> Customer::order(const std::vector<Dish> &menu) {
    std::vector<int> s;
    return s;
}

std::string Customer::toString() const {
    return name;

}
Customer* Customer::clone() {
    return nullptr;
}

Customer::~Customer() {}

//vegetarian
VegetarianCustomer::VegetarianCustomer(std::string name, int id):Customer(name,id),prevOrderIdVeg(-1),prevOrderIdBvg(-1) {}

Customer* VegetarianCustomer::clone() {
    return new VegetarianCustomer(*this);
}

std::vector<int> VegetarianCustomer::order(const std::vector<Dish> &menu) {
    std:: vector<int> food;
    //checks if the customer already ordered
    if(prevOrderIdVeg!=-1||prevOrderIdBvg!=-1){
        food.push_back(prevOrderIdVeg);
        food.push_back(prevOrderIdBvg);
        return food;
    }

    int minId=INT_MAX;
    int maxBvgPrice=-1;
    int maxBvgId=INT_MAX;
    std::vector<Dish>::const_iterator it;
    for(it = menu.begin(); it != menu.end(); ++it) {
        Dish d=*it.base();
        if(d.getType()==VEG) {
            if (d.getId() < minId)
                minId = d.getId();
        }
        else{
            if(d.getType()==BVG) {
                if (d.getPrice() > maxBvgPrice) {
                    maxBvgPrice = d.getPrice();
                    maxBvgId = d.getId();
                } else {
                    if (d.getPrice() == maxBvgPrice) {
                        if (d.getId() < maxBvgId) {
                            maxBvgPrice = d.getPrice();
                            maxBvgId = d.getId();
                        }
                    }
                }
            }
        }
    }
    if((minId==INT_MAX) || (maxBvgId==INT_MAX)){
        return food;
    }
    food.push_back(minId);
    food.push_back(maxBvgId);
    prevOrderIdVeg=minId;
    prevOrderIdBvg=maxBvgId;
    return food;
}
std::string VegetarianCustomer::toString() const {
    std::string s1=this->getName();
    return s1+","+"veg";

}


//Cheap

CheapCustomer::CheapCustomer(std::string name, int id):Customer(name,id),ordered(false) {}

Customer* CheapCustomer::clone() {
    return new CheapCustomer(*this);
}
std::vector<int> CheapCustomer::order(const std::vector<Dish> &menu) {
    std:: vector<int> food;
    int id=-1;
    int price=INT_MAX;
    std::vector<Dish>::const_iterator it;
    if(!ordered) {
        for (it = menu.begin(); it != menu.end(); ++it) {
            Dish d = *it.base();
            if (d.getPrice() < price) {
                id = d.getId();
                price = d.getPrice();
            } else { //checks if price is the same
                if (d.getPrice() == price) {
                    if (d.getId() < id) {
                        id = d.getId();
                        price = d.getPrice();
                    }
                }
            }
        }
    }
    if(id!=-1) {
        food.push_back(id);
        ordered=true;
    }
    return food;
}

std::string CheapCustomer::toString() const {
    std::string s1=this->getName();
    return s1+","+"chp";
}

//Spicy

SpicyCustomer::SpicyCustomer(std::string name, int id):Customer(name,id),ordered(false) {}

Customer* SpicyCustomer::clone() {
    return new SpicyCustomer(*this);
}

std::vector<int> SpicyCustomer::order(const std::vector<Dish> &menu) {
    std::vector<int> food;
    int id = -1;
    int price = INT_MAX;
    std::vector<Dish>::const_iterator it;
    if (ordered) { // checks if the customer already ordered
        for (it = menu.begin(); it != menu.end(); ++it) {
            Dish d = *it.base();
            if (d.getType() == BVG) {
                if (d.getPrice() < price) {
                    id = d.getId();
                    price = d.getPrice();
                }
                else {//checks if price is the same
                    if (d.getPrice() == price) {
                        if (d.getId() < id) {
                            id = d.getId();
                            price = d.getPrice();
                        }
                    }
                }
            }
        }
    }
    else{ //first order
        price=-1;
        for (it = menu.begin(); it != menu.end(); ++it) {
            Dish d = *it.base();
            if (d.getType() == SPC) {
                if (d.getPrice() > price) {
                    id = d.getId();
                    price = d.getPrice();
                }
                else {//checks if price is the same
                    if (d.getPrice() == price) {
                        if (d.getId() < id) {
                            id = d.getId();
                            price = d.getPrice();
                        }
                    }
                }
            }

        }
    }
    if(id!=-1) {
        food.push_back(id);
        ordered=true;
    }
    return food;
}

std::string SpicyCustomer::toString() const {
    std::string s1=this->getName();
    return s1+","+"spc";
}

//AlcholicCustomer

AlchoholicCustomer::AlchoholicCustomer(std::string name, int id):Customer(name, id), prevOrderId(-1), prevOrderPrice(0){
}

std::vector<int> AlchoholicCustomer::order(const std::vector<Dish> &menu) {
    std::vector<int>food;
    std::vector<Dish>::const_iterator it;
    int dist=INT_MAX;
    int tmpId=INT_MAX;
    int tmpPrice=INT_MAX;
    for(it = menu.begin() ; it != menu.end(); ++it){
        Dish d=*it.base();
        if(d.getType()==ALC){
            if(d.getPrice()>prevOrderPrice){
                if((d.getPrice()-prevOrderPrice)<dist){ //compares the difference between the checked dish and the previously ordered dish
                    dist=d.getPrice()-prevOrderPrice;
                    tmpId=d.getId();
                    tmpPrice=d.getPrice();

                }
                else{
                    if((d.getPrice()-prevOrderPrice)==dist){
                        if(d.getId()<tmpId){
                            tmpId=d.getId();
                        }
                    }
                }
            }
            else{
                if(d.getPrice()==prevOrderPrice){
                    if(d.getId()>prevOrderId){//checks if the id of "d" is bigger than the previous order and smaller than the tmp
                        if(d.getPrice()<tmpPrice || !(d.getId()>tmpId)) {
                            tmpId = d.getId();
                            tmpPrice = d.getPrice();
                        }
                    }
                }
            }
        }

    }
    if(prevOrderId!=tmpId && tmpId!=INT_MAX){
        prevOrderId=tmpId;
        prevOrderPrice=tmpPrice;
        food.push_back(tmpId);
    }
    return food;
}

std::string AlchoholicCustomer::toString() const {
    std::string s1=this->getName();
    return s1+","+"alc";
}

Customer* AlchoholicCustomer::clone() {
    return new AlchoholicCustomer(*this);
}