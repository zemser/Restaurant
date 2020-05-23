//
// Created by shlezimi@wincs.cs.bgu.ac.il on 11/13/18.
//

#include "../include/Action.h"
#include "../include/Table.h"
#include "../include/Restaurant.h"
extern Restaurant* backup;

BaseAction::BaseAction() :errorMsg(""),status(PENDING){}

std::string BaseAction::getErrorMsg() const {
    return errorMsg;
}
void BaseAction::complete() {
    status=COMPLETED;
}
ActionStatus BaseAction::getStatus() const {
    return status;
}

void BaseAction::error(std::string errorMsg) {
    status=ERROR;
    this->errorMsg+=errorMsg;

}

std::string BaseAction::toString() const {
    std::string s="";
    return s;
}

BaseAction::~BaseAction() {}
//OpenTable

OpenTable::OpenTable(int id, std::vector<Customer *> &customersList):BaseAction(), tableId(id),customers(){
    std::vector<Customer*>::const_iterator it;
    for(it=customersList.begin(); it!=customersList.end(); ++it) {
        Customer* tempCustomer=*it.base();
        customers.push_back(tempCustomer->clone());
    }
}

//copy constructor
OpenTable::OpenTable(const OpenTable &source):BaseAction(), tableId(source.tableId),customers() {
    for(unsigned int i=0; i<source.customers.size(); i++){
        customers.push_back(source.customers[i]->clone());
    }
}
//move constructor
OpenTable::OpenTable(OpenTable &&source): BaseAction(), tableId(source.tableId),customers(){
    for(unsigned int i=0; i<source.customers.size(); i++) {
        customers.push_back(source.customers[i]);
        source.customers[i]= nullptr;
    }
    source.customers.clear();
}

//destructor
OpenTable::~OpenTable(){
        for(unsigned int i=0; i<customers.size(); i++ ) {
            delete customers[i];
            customers[i]= nullptr;

        }
        customers.clear();
}

void OpenTable::act(Restaurant &restaurant) {
    Table *t = restaurant.getTable(tableId);
    if (t == nullptr || t->isOpen()) {
        BaseAction::error("Table does not exist or is already open");
        std:: cout<<"Error: "<<getErrorMsg()<<std::endl;
    }
    else {
        complete();
        // copy the customerlist to the table, maybe create a new table
        t->openTable();
        std::vector<Customer *>::const_iterator it;
        for (it = customers.begin(); it != customers.end(); ++it) {
            Customer *c = *it.base();
            t->addCustomer(c->clone());
        }
    }
}


std::string OpenTable::toString() const {
    std::string customerString;
    std::vector<Customer *>::const_iterator it;
    for (it = customers.begin(); it != customers.end(); ++it) {
        Customer *tempCustomer = *it.base();
        std::string tempString = tempCustomer->toString();  //tempString is string the customer we want to add to the string
        customerString = customerString + tempString + " ";
    }
    std::string s = "";
    if (this->getStatus()==ERROR) {
    s = "open " + std::to_string(tableId) + " " + customerString + "Error:" + getErrorMsg();
    }
    else {
        s = "open " + std::to_string(tableId) + " " + customerString + "Completed";
    }
    return s;

}

BaseAction* OpenTable::clone() {
    std::vector<Customer *> tempCustomers;
   for(unsigned int i=0;i<this->customers.size();i++){
       tempCustomers.push_back(customers[i]->clone());
   }
    OpenTable *t=new OpenTable(this->tableId, tempCustomers);
    for(unsigned int i=0;i<tempCustomers.size();i++){
        delete tempCustomers[i];
        tempCustomers[i]= nullptr;
    }
    tempCustomers.clear();
    if(this->getStatus()==COMPLETED)
         t->complete();
    else
        t->error(" Table does not exist or is already open");
   return t;
}

//Order

Order::Order(int id) :BaseAction(), tableId(id){
}

void Order::act(Restaurant &restaurant) {
    Table* t=restaurant.getTable(tableId);
    if(t==nullptr|| t->isOpen()== false){
        BaseAction::error("Table does not exist or is not open");
        std:: cout<<"Error: "<<getErrorMsg()<<std::endl; // not sure that it is needed
    }
    else {
        complete();
        t->order(restaurant.getMenu());
    }
}

std::string Order::toString() const {
    if(this->getStatus()==ERROR){
        return ("order " + std::to_string(tableId) + " Error: " + getErrorMsg());
    }
    else{
        return "order " + std::to_string(tableId) + " Completed";
    }
}

BaseAction* Order::clone() {
    return new Order(*this);
}


//MoveCustomer

MoveCustomer::MoveCustomer(int src, int dst, int customerId):BaseAction(), srcTable(src), dstTable(dst), id(customerId){
}

void MoveCustomer::act(Restaurant &restaurant) {
    Table* tempSourceTable=restaurant.getTable(srcTable);
    Table* tempdstTable=restaurant.getTable(dstTable);
    if(tempSourceTable==nullptr|| tempSourceTable->isOpen()==false || tempdstTable==nullptr|| tempdstTable->isOpen()==false || tempSourceTable->getCustomer(id)==
                                                                                                                   nullptr || unsigned(tempdstTable->getCapacity())<(tempdstTable->getCustomers().size()+1)){
        BaseAction::error("Cannot move customer");
        std:: cout<<"Error: "<<getErrorMsg()<<std::endl;

    }
    else{
        complete();
        tempdstTable->addCustomer(tempSourceTable->getCustomer(id));
        tempSourceTable->removeCustomer(id);
        std::vector<OrderPair> tempOrder = tempSourceTable->removeOrders(id);
        tempdstTable->addOrders(tempOrder);
        if (tempSourceTable->getCustomers().size() == 0) {// check if the source table is empty now if so close is it
            tempSourceTable->closeTable();
        }
    }
}

std::string MoveCustomer::toString() const {
    if(this->getStatus()==ERROR){
        return ("move " + std::to_string(srcTable) + " "+ std::to_string(dstTable) + " " + std::to_string(id) + " Error: "+getErrorMsg());
    }
    else{
        return ("move " + std::to_string(srcTable) + " "+ std::to_string(dstTable) + " " + std::to_string(id) +" Completed");
    }
}

BaseAction* MoveCustomer::clone() {
    return new MoveCustomer(*this);
}

//Close

Close::Close(int id):BaseAction(), tableId(id){
}

void Close::act(Restaurant &restaurant) {
    Table *t = restaurant.getTable(tableId);
    if (t == nullptr || t->isOpen()== false) {
        BaseAction::error("Table does not exist or is not open");
        std:: cout<<"Error: "<<getErrorMsg()<<std::endl;
    }
    else {
        complete();
        int bill = t->getBill();
        std::cout << "Table " << tableId << " was closed. " << "Bill " << bill << "NIS"
                  << std::endl;
        t->closeTable();
    }
}

std::string Close::toString() const {
    if(this->getStatus()==ERROR){
        return ("close "+std::to_string(tableId)+" Error: " +getErrorMsg());
    }
    else{
        return  ("close "+std::to_string(tableId)+" Completed");
    }
}
BaseAction* Close::clone() {
    return new Close(*this);
}


//CloseAll

CloseAll::CloseAll():BaseAction() {}

void CloseAll::act(Restaurant &restaurant) {
    complete();
    for(int i=0; i<restaurant.getNumOfTables(); i++ ){
        Table* t=restaurant.getTable(i);
        if(t->isOpen()){
            int bill=t->getBill();
            std::cout<<"Table " << i<< " was closed. "<<"Bill "<<bill<<"NIS" <<std::endl;
            t->closeTable();
        }

    }
}

std::string CloseAll::toString() const {
    std::string s="closeall Completed";
    return s;
}

BaseAction* CloseAll::clone() {
    return new CloseAll(*this);
}

//PrintMenu

PrintMenu::PrintMenu():BaseAction() {
}

void PrintMenu::act(Restaurant &restaurant) {
    std::vector<Dish> menu=restaurant.getMenu();
    std::vector<Dish>::const_iterator it;
    for(it=menu.begin(); it!=menu.end(); ++it) {
        Dish tmpDish = *it.base();
        std::cout << tmpDish.getName() << " ";
        if(tmpDish.getType()==VEG)
            std::cout << "VEG ";
        else{
            if(tmpDish.getType()==SPC)
                std::cout << "SPC ";
            else{
                if(tmpDish.getType()==BVG)
                    std::cout << "BVG ";
                else
                    std::cout << "ALC ";
            }
        }
        std::cout << tmpDish.getPrice()<<"NIS" << std::endl;
    }
    complete();

}
std::string PrintMenu::toString() const {
    return "menu Completed";
}

BaseAction* PrintMenu::clone() {
    return new PrintMenu(*this);
}


//PrintTableStatus

PrintTableStatus::PrintTableStatus(int id):BaseAction(),tableId(id) {}

void PrintTableStatus::act(Restaurant &restaurant) {
    Table* tmpTable=restaurant.getTable(tableId);
    if(!tmpTable->isOpen()){
        std::cout<<"Table " << tableId <<" status: " << "closed"<<std::endl;
    }
    else {
        std::cout << "Table " << tableId << " status: " << "open" << std::endl;
        std::cout << "Customers: " << std::endl;
        std::vector<Customer *>::const_iterator it;
        for (it = tmpTable->getCustomers().begin(); it != tmpTable->getCustomers().end(); ++it) {
            Customer *tmpCustomer = *it.base();
            std::cout << tmpCustomer->getId()<<" " <<tmpCustomer->getName();
            std::cout << std::endl;
        }
        std::cout << "Orders: " << std::endl;
        std::vector<OrderPair>::const_iterator it1;
        for (it1 = tmpTable->getOrders().begin(); it1 != tmpTable->getOrders().end(); ++it1) {
            OrderPair tmpOrder = *it1.base();
            std::cout << tmpOrder.second.toString() << " ";
            std::cout << tmpOrder.first;
            std::cout << std::endl;
        }
        tmpTable->getBill();
        std::cout << "Current Bill: " << tmpTable->getBill() << "NIS"<< std::endl;
    }
    complete();
}
std::string PrintTableStatus::toString() const {
    return ("status " + std::to_string(tableId) + " Completed");
}

BaseAction* PrintTableStatus::clone() {
    return new PrintTableStatus(*this);
}


//PrintActionsLog

PrintActionsLog::PrintActionsLog():BaseAction() {}

void PrintActionsLog::act(Restaurant &restaurant) {
    std::vector<BaseAction*>::const_iterator it;
    for (it = restaurant.getActionsLog().begin(); it != restaurant.getActionsLog().end(); ++it){
        BaseAction* bAct=*it.base();
        std::cout<<bAct->toString()<< std::endl;

    }
    complete();
}

std::string PrintActionsLog::toString() const {
    return "log Completed";
}

BaseAction* PrintActionsLog::clone() {
    return new PrintActionsLog(*this);
}


//BackupRestaurant

BackupRestaurant::BackupRestaurant():BaseAction() {
}

void BackupRestaurant::act(Restaurant &restaurant) {

    // backup=&restaurant;
    if(backup== nullptr){
        backup=new Restaurant(restaurant);
    }
    else{
        delete backup;
        backup=new Restaurant(restaurant);
    }
    complete();
}

std::string BackupRestaurant::toString() const {

        return "backup Completed";

}

BaseAction* BackupRestaurant::clone() {
    return new BackupRestaurant(*this);
}


//RestoreResturant

RestoreResturant::RestoreResturant():BaseAction() {

}

void RestoreResturant::act(Restaurant &restaurant) {
   if(backup==nullptr){
        BaseAction::error("No backup available");
        std:: cout<<"Error: " <<getErrorMsg()<< std::endl;

   }
    else{
        complete();
        restaurant=*backup;
    }

}
std::string RestoreResturant::toString() const {
    if(this->getStatus()==ERROR){
        return ("restore Error: " +getErrorMsg());
    }
    else{
        return "restore Completed ";
    }
}

BaseAction* RestoreResturant::clone() {
    return new RestoreResturant(*this);
}