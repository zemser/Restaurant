package bgu.spl.mics.application;
import bgu.spl.mics.Pair;
import bgu.spl.mics.ThreadCounter;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.io.*;
import java.util.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner implements java.io.Serializable{
    public static void main(String[] args) {
        Gson gson = new Gson();
        try {
            FileReader fr = new FileReader(args[0]);
            HashMap settings = gson.fromJson(fr, HashMap.class);

            //get the Inventory data from the file
            Inventory inventory = Inventory.getInstance();
            ArrayList<LinkedTreeMap> arrInventory = (ArrayList) settings.getOrDefault("initialInventory", null);
            BookInventoryInfo[] books = new BookInventoryInfo[arrInventory.size()];
            for (int i = 0; i < arrInventory.size(); i++) {
                String bookTitle = (String) arrInventory.get(i).get("bookTitle");
                int amount = (int) (double) arrInventory.get(i).get("amount");
                int price = (int) (double) arrInventory.get(i).get("price");
                BookInventoryInfo book = new BookInventoryInfo(bookTitle, amount, price);
                books[i] = book;
            }
            inventory.load(books);


            //get the resource data from the file
            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            ArrayList<LinkedTreeMap> arrResources = (ArrayList) settings.getOrDefault("initialResources", null);
            ArrayList<LinkedTreeMap> vehiclesFromJson = (ArrayList<LinkedTreeMap>) arrResources.get(0).get("vehicles");
            DeliveryVehicle[] vehiclesArray = new DeliveryVehicle[vehiclesFromJson.size()];
            for (int i = 0; i < vehiclesFromJson.size(); i++) {
                LinkedTreeMap<String, Object> line = vehiclesFromJson.get(i);
                int license = (int) (double) line.get("license");
                int speed = (int) (double) line.get("speed");
                vehiclesArray[i] = new DeliveryVehicle(license, speed);
            }
            resourcesHolder.load(vehiclesArray);


            //get the services data from the file
            LinkedTreeMap arrServices = (LinkedTreeMap) settings.getOrDefault("services", null);
            List<Thread> threads = new LinkedList<>();

            //Time Service
            LinkedTreeMap<String, Object> time = (LinkedTreeMap<String, Object>) arrServices.get("time");
            int speed = (int) (double) time.get("speed");
            int duration = (int) (double) time.get("duration");
            TimeService timeService = new TimeService(speed, duration);  ///wants a string in the consturctor

            //selling services
            int sellingServicesCounter = (int) (double) arrServices.get("selling");
            for (int i = 1; i <= sellingServicesCounter; i++) {
                SellingService sell = new SellingService(i);   //// how to change the name each time
                Thread sellingThread = new Thread(sell);
                threads.add(sellingThread);
                sellingThread.start();
            }

            //Inventory Services
            int inventoryServicesCounter = (int) (double) arrServices.get("inventoryService");
            for (int i = 1; i <= inventoryServicesCounter; i++) {
                InventoryService inventoryServ = new InventoryService(i);
                Thread inventoryThread = new Thread(inventoryServ);
                threads.add(inventoryThread);
                inventoryThread.start();
            }
            //Logistics service
            int logisticsServicesCounter = (int) (double) arrServices.get("logistics");
            for (int i = 1; i <= logisticsServicesCounter; i++) {
                LogisticsService logistics = new LogisticsService(i);   //// how to change the name each time
                Thread logisticsThread = new Thread(logistics);
                threads.add(logisticsThread);
                logisticsThread.start();
            }

            //resourcesService
            int resourcesServiceCounter = (int) (double) arrServices.get("resourcesService");
            for (int i = 1; i <= resourcesServiceCounter; i++) {
                ResourceService resources = new ResourceService(i);   //// how to change the name each time
                Thread resourceThread = new Thread(resources);
                threads.add(resourceThread);
                resourceThread.start();
            }

            //Customers
            ArrayList<LinkedTreeMap> arrCustomers = (ArrayList) arrServices.get("customers");
            HashMap<Integer, Customer> customersMap = new HashMap<>(); // for the customer output print
            for (int i = 0; i < arrCustomers.size(); i++) {  //go through each customer's info and the read the info
                LinkedTreeMap<String, Object> spesificCustomer = arrCustomers.get(i);
                int id = (int) (double) spesificCustomer.get("id");
                String name = (String) spesificCustomer.get("name");
                String address = (String) spesificCustomer.get("address");
                int distance = (int) (double) spesificCustomer.get("distance");
                LinkedTreeMap<String, Object> VisaDetails = (LinkedTreeMap<String, Object>) spesificCustomer.get("creditCard");
                int creditNumber = (int) (double) VisaDetails.get("number");
                int creditAmount = (int) (double) VisaDetails.get("amount");
                //orderschedule
                ArrayList<LinkedTreeMap> arrOrderschedule = (ArrayList) spesificCustomer.get("orderSchedule");
                Pair<String, Integer>[] customerOrders = new Pair[arrOrderschedule.size()]; // pair array for the orders schedule- pair contains the book name and the tick order
                for (int j = 0; j < arrOrderschedule.size(); j++) {  //go through the schedule info and create the pairs and insert them into the array
                    LinkedTreeMap<String, Object> order = arrOrderschedule.get(j);
                    String bookTitle = (String) order.get("bookTitle");
                    int orderTick = (int) (double) order.get("tick");
                    Pair<String, Integer> p = new Pair<>(bookTitle, orderTick);
                    customerOrders[j] = p;
                }
                Customer c = new Customer(name, id, address, distance, creditAmount, creditNumber, customerOrders);
                customersMap.put(id, c); // adds the created customer to the print map
                APIService api = new APIService(c, i + 1);   //// how to change the name each time
                Thread APIThread = new Thread(api);
                threads.add(APIThread);
                APIThread.start();
            }

            while (ThreadCounter.getInstance().getThreadCounter().get() != threads.size()) {
            }
            Thread TimeThread = new Thread(timeService);
            threads.add(TimeThread);
            TimeThread.start();

            Iterator<Thread> it = threads.iterator();
            while (it.hasNext()) {
                Thread threadFromTheList = it.next();
                try {
                    if (!threadFromTheList.isInterrupted()) {
                        threadFromTheList.join();
                    }
                } catch (InterruptedException e) {
                }
            }

            //print output files  Prints to a file a serialized object Hashmap of the customers
            try {
                FileOutputStream file = new FileOutputStream(args[1]);
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(customersMap);
                out.close();
                file.close();

            } catch (IOException e) {
            }
            //print output files  Prints to a file a serialized object Hashmap of the inventory
            inventory.printInventoryToFile(args[2]);
            ////print output files  Prints to a file a serialized object Order Reeciets
            MoneyRegister.getInstance().printOrderReceipts(args[3]);
            ////print output files  Prints to a file a serialized object Money Register
           try{
               FileOutputStream file = new FileOutputStream(args[4]);
               ObjectOutputStream out = new ObjectOutputStream(file);
               out.writeObject(MoneyRegister.getInstance());
               out.close();
               file.close();
           }catch (IOException e){

           }


        } catch (FileNotFoundException e) {
        }

    }
}
