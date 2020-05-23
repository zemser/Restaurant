//
// Created by shlezimi@wincs.cs.bgu.ac.il on 11/7/18.
//

#include "../include/Table.h"
//constructor
Table::Table(int t_capacity): capacity(t_capacity), open(false),customersList(),orderList() {
}

//copy constructor
Table::Table(const Table &source): capacity(source.capacity), open(source.open),customersList(),orderList()  {
    copy(source);
}

//copy assignment operator
Table& Table::operator=(const Table &other) {
    if(this!=&other){
        clear();
        copy(other);
    }
    return *this;

}
//destructor
Table::~Table() {
    clear();
}

//move constructor
Table::Table(Table &&source) :capacity(source.capacity), open(source.open),customersList(),orderList() {

    int orderListSize=(int)source.orderList.size();
    for(int i=0; i<orderListSize; i++ ) {
        orderList.push_back(source.orderList[i]);
    }
    int customersListSize=(int)source.customersList.size();
    for(int i=0; i<customersListSize; i++ ) {
        customersList.push_back(source.customersList[i]);
        source.customersList[i]=nullptr;
    }
    source.orderList.clear();
    source.customersList.clear();
}

//move assignment
Table& Table::operator=(Table &&other) {
    if(this!=&other){
        clear();
        capacity=other.capacity;
        open=other.open;
        int orderListSize=(int)other.orderList.size();
        for(int i=0; i<orderListSize; i++ ) {
            orderList.push_back(other.orderList[i]);
        }
        int customersListSize=(int)other.customersList.size();
        for(int i=0; i<customersListSize; i++ ) {
            customersList.push_back(other.customersList[i]);
            other.customersList[i]=nullptr;
        }
        other.orderList.clear();
        other.customersList.clear();
    }
    return *this;
}


int Table::getCapacity() const {
    return capacity;
}

bool Table::isOpen() {
    return open;
}

void Table::addCustomer(Customer *customer) {
    customersList.push_back(customer);
}

Customer* Table::getCustomer(int id) {
    int temp=-1;
    std::vector<Customer*>::const_iterator it;
    it = customersList.begin();
    while(it!=customersList.end()){
        Customer* c=*it.base();
        temp=c->getId();
        if(temp==id)
            return c;
        ++it;
    }
    return nullptr;
}
std::vector<Customer*>& Table::getCustomers() {
    return customersList;
}

std::vector<OrderPair>& Table::getOrders() {
    return orderList;
}

void Table::order(const std::vector<Dish> &menu) {
    std::vector<Customer*>::const_iterator it;
    //run on the customers and take order for each one
    for(it=customersList.begin(); it!=customersList.end(); ++it) {
        Customer *tempCustomer = *it.base();
        std::vector<int> tmp;
        tmp = tempCustomer->order(menu); //get the order vector
        if (tmp.size() != 0) {
            //for each customer get an order pair
            std::vector<int>::const_iterator it2;
            for (it2 = tmp.begin(); it2 != tmp.end(); ++it2) {
                int tempId = *it2.base();
                Dish tempDish = findDishById(menu, tempId); //find dish by id from the menu
                orderList.push_back(OrderPair(tempCustomer->getId(), tempDish));
                std::cout<<tempCustomer->getName() <<" ordered "<<tempDish.getName()<<std::endl;
            }
        }
    }
}
//find dishes by id from the menu
Dish Table::findDishById(const std::vector<Dish> &menu, int id) {
    std::vector<Dish>::const_iterator it;
    for (it=menu.begin(); it!=menu.end(); ++it) {
        Dish d=*it.base();
        if(d.getId()==id)
            return d;
    }
    std::cout<< "dish by id not found"<<id;
    return menu[0];

}

void Table::removeCustomer(int id) {
    std::vector<Customer*>::const_iterator it;
    int count=0;
    bool check=false;
    for(it=customersList.begin(); it!=customersList.end()&&!check; ++it){
        Customer* tempCustomer=*it.base();
        if(tempCustomer->getId()==id){
            check=true;
        }
        else
          ++count;
    }
    customersList.erase(customersList.begin()+count);
}

void Table::openTable() {
    open=true;
}
void Table::closeTable() {
    open=false;
    clear();
}

int Table::getBill() {
    int sum=0;
    std::vector<OrderPair>::const_iterator it;
    for(it=orderList.begin(); it!=orderList.end(); ++it){
        sum=sum+it.base()->second.getPrice();
    }
    return sum;
}

void Table::addOrders(std::vector<OrderPair> &p) {
    std::vector<OrderPair>::const_iterator it;
    for(it=p.begin(); it!=p.end(); ++it){
        orderList.push_back(*it.base());
    }
}

std::vector<OrderPair> Table::removeOrders(int id) {
    std::vector<OrderPair> removedPairs;
    std::vector<OrderPair> tempOrders;
    std::vector<OrderPair>::const_iterator it;
    //go through the order list, if the dish belongs to the id add it to the removed orders else add it to the temp orders
    for(it=orderList.begin(); it!=orderList.end(); ++it){
        if(it.base()->first==id){
            removedPairs.push_back(*it.base());
        }
        else{
            tempOrders.push_back(*it.base());
        }
    }
    // delete everything from orderList and then copy to it the orders from temp
    orderList.clear();
    std::vector<OrderPair>::const_iterator it2;
    for(it2=tempOrders.begin(); it2!=tempOrders.end(); ++it2){
        orderList.push_back(*it2.base());
    }
    tempOrders.clear();
    return removedPairs;

}

Table* Table::clone() {
    Table *t=new Table(capacity);
    t->open=this->isOpen();
    for(unsigned int i=0;i<customersList.size();i++){
        t->customersList.push_back(customersList[i]->clone());
    }
    for(unsigned int i=0;i<orderList.size();i++){
        t->orderList.push_back(orderList[i]);
    }
    return t;
}

void Table::clear() {
    int customersListSize=(int)customersList.size();
    for(int i=0; i<customersListSize; i++ ) {
        delete customersList[i];
        customersList[i]= nullptr;
    }
    orderList.clear();
    customersList.clear();
}
void Table::copy(const Table &other) {
    capacity=other.capacity;
    open=other.open;
    int otherOrderListSize = (int)other.orderList.size();
    for(int i=0; i<otherOrderListSize; i++ ) {
        orderList.push_back(other.orderList[i]);
    }


    int customersListSize=(int)other.customersList.size();
    for(int i=0; i<customersListSize; i++ ) {
        Customer *t = other.customersList[i]->clone();
        customersList.push_back(t);
    }
}