//
// Created by shlezimi@wincs.cs.bgu.ac.il on 11/7/18.
//

#include "../include/Restaurant.h"
//using namespace std;
//constructor
Restaurant::Restaurant(const std::string &configFilePath):open(false),tables(),menu(),actionsLog(),numOfCustomers(0),numOfTables(0) {
    ifstream input(configFilePath);
    string line;
    vector<std::string> inputV;

    if (input.is_open()) {
        while (getline(input, line)) {
            char first;
            if(line.size()!=0 && line!="\r" && line!="\n" && line!="\t") {
                first = line.at(0);
                if ((first != '#')) {
                    inputV.push_back(line);
                }
            }
        }
        input.close();
        stringstream tmp(inputV[0]);
        tmp >> numOfTables;

        //checking the capacities of the tables
        std::istringstream iss(inputV[1]);
        std::vector<int> tableCapacity;
        std::string tmpCapacity;
        while (getline(iss, tmpCapacity, ',')) {
            tableCapacity.push_back(std::stoi(tmpCapacity));
        }

        std::vector<int>::const_iterator it;
        //creates and adds tables with the given capacities
        for (it = tableCapacity.begin(); it != tableCapacity.end(); ++it) {
            int capacity = *it.base();
            Table *t = new Table(capacity);
            tables.push_back(t);
        }

        std::vector<string>::const_iterator it1;
        int count = 0;
        string dishName;
        string dishType;
        string tmpDishPrice;
        int dishPrice;
        it1 = inputV.begin();
        ++it1;
        ++it1;
        //starts with the cells of the menu
        while (it1 != inputV.end()) {
            string str = *it1.base();
            istringstream iss2(str);
            getline(iss2, dishName, ',');
            getline(iss2, dishType, ',');
            getline(iss2, tmpDishPrice);
            dishPrice = stoi(tmpDishPrice);
            //checks what type of dish
            Dish *tmpDish;
            if (dishType == "VEG")
                tmpDish = new Dish(count, dishName, dishPrice, VEG);
            else {
                if (dishType == "SPC")
                    tmpDish = new Dish(count, dishName, dishPrice, SPC);
                else {
                    if (dishType == "ALC")
                        tmpDish = new Dish(count, dishName, dishPrice, ALC);
                    else {
                        tmpDish = new Dish(count, dishName, dishPrice, BVG);
                    }
                }
            }
            menu.push_back(*tmpDish);
            delete tmpDish;
            count++;
            ++it1;
        }
    }
    inputV.clear();
}

//copy constructor
Restaurant::Restaurant(const Restaurant &source): open(source.open),tables(),menu(),actionsLog(),numOfCustomers(source.numOfCustomers),numOfTables(source.numOfTables) {
    copy(source);
}
//copy assignment operator
Restaurant& Restaurant::operator=(const Restaurant &other) {

    if(this!=&other){
        clear();
        copy(other);
    }
    return *this;

}

Restaurant::~Restaurant() {
    clear();

}
//move constructor
Restaurant::Restaurant(Restaurant &&source): open(source.open),tables(),menu(),actionsLog(),numOfCustomers(source.numOfCustomers),numOfTables(source.numOfTables) {
    for(unsigned int  i=0; i<source.menu.size(); i++ ) {
        menu.push_back(source.menu[i]);
    }

    numOfTables=source.numOfTables;
    numOfCustomers=source.numOfCustomers;
    for(unsigned int i=0; i<source.tables.size(); i++ ){
        tables.push_back(source.tables[i]);
        source.tables[i]= nullptr;
    }
    for(unsigned int i=0; i<source.actionsLog.size(); i++ ) {
        actionsLog.push_back(source.actionsLog[i]);
        source.actionsLog[i] = nullptr;
    }
    source.tables.clear();
    source.actionsLog.clear();
    source.menu.clear();

}

//move assignment
Restaurant& Restaurant::operator=(Restaurant &&other) {
    if(this!=&other){
        clear();
        for(unsigned int i=0; i<other.menu.size(); i++ ) {
            menu.push_back(other.menu[i]);
        }
        open=other.open;
        numOfTables=other.numOfTables;
        numOfCustomers=other.numOfCustomers;
        for(unsigned int i=0; i<other.tables.size(); i++ ) {
            tables.push_back(other.tables[i]);
            other.tables[i] = nullptr;
        }
        for(unsigned int i=0; i<other.actionsLog.size(); i++ ) {
            actionsLog.push_back(other.actionsLog[i]);
            other.actionsLog[i] = nullptr;
        }
        other.menu.clear();
        other.tables.clear();
        other.actionsLog.clear();
    }
    return *this;

}


void Restaurant::start() {
    std::cout<<"Restaurant is now open!"<< std::endl ;
    open=true;
    //check the input string to determine what action to call
    std::string line="";
    std::string tmp="";
    BaseAction* bAct= nullptr;
    while(line!="closeall"){
        getline(std::cin,line);
        std::vector<Customer *> customerList;
        std::istringstream iss(line);

        getline(iss,tmp,' ');
        if(tmp=="open"){
            std::string tmpName;
            std::string tmpType;
            int tableId;
            getline(iss,tmp,' ');
            tableId=std::stoi(tmp);
            Customer* tmpCust = nullptr;
            while(!iss.eof()){
                getline(iss,tmpName,',');
                getline(iss, tmpType, ' ');
                if(tmpType=="veg") {
                    tmpCust= new VegetarianCustomer(tmpName, numOfCustomers);
                    customerList.push_back(tmpCust);
                }
                else{
                    if(tmpType=="alc"){
                        tmpCust=new AlchoholicCustomer(tmpName, numOfCustomers);
                        customerList.push_back(tmpCust);
                    }
                    else{
                        if(tmpType=="spc"){
                            tmpCust=new SpicyCustomer(tmpName, numOfCustomers);
                            customerList.push_back(tmpCust);

                        }
                        else{
                            if(tmpType=="chp"){
                                tmpCust=new CheapCustomer(tmpName, numOfCustomers);
                                customerList.push_back(tmpCust);

                            }
                        }

                    }
                }
                ++numOfCustomers;
                tmpCust= nullptr;
            }
            bAct=new OpenTable(tableId,customerList);
            bAct->act(*this);
            actionsLog.push_back(bAct->clone());
            for(unsigned int i=0;i<customerList.size();i++){
                delete customerList[i];
                customerList[i]= nullptr;
            }
            customerList.clear();
        }
        else{
            if(tmp=="order"){
                int tableNumber;
                getline(iss,tmp,' ');
                tableNumber=std::stoi(tmp);
                bAct=new Order(tableNumber);
                bAct->act(*this);
                actionsLog.push_back(bAct->clone());
            }
            else{
                if(tmp=="move"){
                    int originTable;
                    int DestinationTable;
                    int CustomerId;
                    getline(iss,tmp, ' ');
                    originTable=std::stoi(tmp);
                    getline(iss,tmp, ' ');
                    DestinationTable=std::stoi(tmp);
                    getline(iss,tmp, ' ');
                    CustomerId=std::stoi(tmp);
                    bAct=new MoveCustomer(originTable,DestinationTable,CustomerId);
                    bAct->act(*this);
                    actionsLog.push_back(bAct->clone());
                }
                else{
                    if(tmp=="close"){
                        int tableNumber;
                        getline(iss,tmp,' ');
                        tableNumber=std::stoi(tmp);
                        bAct=new Close(tableNumber);
                        bAct->act(*this);
                        actionsLog.push_back(bAct->clone());
                    }
                    else{
                        if(tmp=="menu"){
                            bAct=new PrintMenu();
                            bAct->act(*this);
                            actionsLog.push_back(bAct->clone());

                        }
                        else{
                            if(tmp=="status"){
                                int tableNumber;
                                getline(iss,tmp,' ');
                                tableNumber=std::stoi(tmp);
                                bAct=new PrintTableStatus(tableNumber);
                                bAct->act(*this);
                                actionsLog.push_back(bAct->clone());
                            }
                            else{
                                if(tmp=="log"){
                                    bAct=new PrintActionsLog();
                                    bAct->act(*this);
                                    actionsLog.push_back(bAct->clone());
                                }
                                else{
                                    if(tmp=="backup"){
                                        bAct=new BackupRestaurant();
                                        bAct->act(*this);
                                        actionsLog.push_back(bAct->clone());
                                    }
                                    else{
                                        if(tmp=="restore"){
                                            bAct=new RestoreResturant();
                                            bAct->act(*this);
                                            actionsLog.push_back(bAct->clone());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        delete bAct;
        bAct= nullptr;
    }
    bAct=new CloseAll();
    bAct->act(*this);
    delete bAct;
    bAct= nullptr;

}

int Restaurant::getNumOfTables() const {
    return numOfTables;
}

Table* Restaurant::getTable(int ind) const {
    unsigned int tmp=ind;
    if (tmp > tables.size() - 1)
        return nullptr;   // return null when there is no table with that number
    int countTables = 0;
    std::vector<Table *>::const_iterator it;
    for (it = tables.begin(); it != tables.end(); ++it) {
        if (countTables == ind)
            return *it.base();  //return the table with ind.
        countTables++;
    }
    return nullptr;
}

const std::vector<BaseAction*>& Restaurant::getActionsLog() const {
    return actionsLog;
}

std::vector<Dish>& Restaurant::getMenu() {
    return menu;
}

void Restaurant::clear() {
    for(unsigned int i=0; i<tables.size(); i++ ){
        delete tables[i];
        tables[i]= nullptr;
    }
    for(unsigned int i=0; i<actionsLog.size(); i++ ){
        delete actionsLog[i];
        actionsLog[i]= nullptr;
    }
    menu.clear();
    tables.clear();
    actionsLog.clear();
}

void Restaurant::copy(const Restaurant &other) {
    open=other.open;
    for(unsigned int i=0; i<other.menu.size(); i++ ) {
        menu.push_back(other.menu[i]);
    }
    numOfTables=other.numOfTables;
    numOfCustomers=other.numOfCustomers;
    for(unsigned int i=0; i<other.tables.size(); i++ ) {
        Table *t = other.getTable(i)->clone();
        tables.push_back(t);
    }
    for(unsigned int i=0; i<other.getActionsLog().size(); i++ ) {
        BaseAction *b = other.getActionsLog()[i]->clone();
        actionsLog.push_back(b);
    }
}

