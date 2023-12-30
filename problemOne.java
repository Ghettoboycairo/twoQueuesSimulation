
import java.util.LinkedList;
import java.util.Queue;

class Customer {
    public String customerType;
    public int interArrivalTime; // indicates the time between this customer arrival time and the previous
                                 // customer arrival time.
    public int arrivalTime = 0; // according to the system (previous customer arrival time + this customer
    // interArrivalTime).
    public int serviceTime = 0; // fixed.
    public int remainingServiceTime = 0; // dummy for subtraction as the system time goes forward.
    public int waitingTime = 0; // to be incremented if this customer arrived and is waiting in a queue.
    public String Queue;

    Customer() { // creates a new customer following the random probaility distribution given in
                 // the problem.

        double randomNumber = Math.random();
        if (randomNumber < 0.6) { // generating random customer type.
            customerType = "Express";
        } else {
            customerType = "Regular";
        }

        randomNumber = Math.random();
        if (randomNumber < 0.16) { // generating random IAR.
            interArrivalTime = 0;
        } else if (randomNumber < 0.39) {
            interArrivalTime = 1;
        } else if (randomNumber < 0.69) {
            interArrivalTime = 2;
        } else if (randomNumber < 9.0) {
            interArrivalTime = 3;
        } else {
            interArrivalTime = 4;
        }

        randomNumber = Math.random();
        if ("Express".equals(customerType)) { // generating random service time.
            if (randomNumber < 0.3) {
                serviceTime = 1;
            } else if (randomNumber < 0.7) {
                serviceTime = 2;
            } else {
                serviceTime = 3;
            }
        } else {
            if (randomNumber < 0.2) {
                serviceTime = 3;
            } else if (randomNumber < 0.7) {
                serviceTime = 5;
            } else {
                serviceTime = 7;
            }
        }
        remainingServiceTime = serviceTime;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer Type: %s, Inter-Arrival Time: %d, Arrival Time: %d, Service Time: %d, Remaining Service Time: %d, Waiting Time: %d ,Queue: %s",
                customerType, interArrivalTime, arrivalTime, serviceTime, remainingServiceTime, waitingTime, Queue);
    }

}

class problemOne {
    int systemTime;
    int numberOfAllCustomers;
    int numberOfExpressCustomers;
    int numberOfRegularCustomers;
    Customer[] customersArray; // represents the customers sorted in their normal
                               // arrival (regardless of their type).
    int regularQueueMaxLength = 0;
    int expressQueueMaxLength = 0;
    int expressQueueIdleTime = 0;
    int regularQueueIdleTime = 0;

    problemOne(int customersNumber) {
        getCustomers(customersNumber);
        simulate(customersNumber);
        viewStats();
    }

    void getCustomers(int numberOfCustomers) { // collects a certain ammount of customers
        numberOfAllCustomers = numberOfCustomers;

        customersArray = new Customer[numberOfCustomers];

        Customer previousCustomer = null;

        for (int i = 0; i < numberOfCustomers; i++) {

            Customer currentCustomer = new Customer();

            if (i == 0) { // if this is the first customer
                currentCustomer.arrivalTime = currentCustomer.interArrivalTime;
            } else {
                currentCustomer.arrivalTime += previousCustomer.arrivalTime + currentCustomer.interArrivalTime;
            }

            if ("Express".equals(currentCustomer.customerType)) {
                numberOfExpressCustomers++;
            } else {
                numberOfRegularCustomers++;
            }

            previousCustomer = currentCustomer;
            customersArray[i] = currentCustomer; // adding the customer to the customers array (regardless of his
                                                 // type).
        }
    }

    void simulate(int numberOfCustomers) {
        Queue<Customer> expressQueue = new LinkedList<>();
        Queue<Customer> regularQueue = new LinkedList<>();

        int customersArrayIndex = 0;
        int customersDone = 0; // customers who finished their service time.

        System.out.println("number of customers: " + numberOfAllCustomers);

        for (systemTime = 0; customersDone < numberOfCustomers; systemTime++) { // each iteration represents a
                                                                                // minute of system time.
            while (customersArrayIndex < numberOfCustomers
                    && customersArray[customersArrayIndex].arrivalTime <= systemTime) { // adding each customer who
                                                                                        // arrived
                // to
                // the right queue

                if ("Express".equals(customersArray[customersArrayIndex].customerType)) {

                    if (expressQueue.size() < (regularQueue.size() * 1.5)) { // checking if the express queue size is
                                                                             // less
                                                                             // than (1.5 * regular queue size)
                        expressQueue.add(customersArray[customersArrayIndex]);
                        customersArray[customersArrayIndex].Queue = "Express";
                    } else {
                        regularQueue.add(customersArray[customersArrayIndex]);
                        customersArray[customersArrayIndex].Queue = "Regular";
                    }

                } else {
                    customersArray[customersArrayIndex].Queue = "Regular";
                    regularQueue.add(customersArray[customersArrayIndex]);
                }

                customersArrayIndex++;
            }

            for (Customer customer : regularQueue) { // after adding customers to the Q we adjust the waiting time
                if (customer != regularQueue.peek()) {
                    customer.waitingTime += 1;
                }
            }

            for (Customer customer : expressQueue) {
                if (customer != expressQueue.peek()) {
                    customer.waitingTime += 1;
                }
            }

            if (expressQueue.size() > 0) {

                if (expressQueue.size() > expressQueueMaxLength) { // updating the max queue length
                    expressQueueMaxLength = expressQueue.size();
                }

                if (expressQueue.peek().remainingServiceTime > 1) { // if the first customer in queue have a remaining
                                                                    // service time of 1 from the last iteration we
                                                                    // consider its service as done.
                    expressQueue.peek().remainingServiceTime--;
                } else {
                    expressQueue.peek().remainingServiceTime--;
                    expressQueue.poll();
                    customersDone++; // increment customres who finished by one.
                }
            } else {
                expressQueueIdleTime++;
            }

            if (regularQueue.size() > 0) {

                if (regularQueue.size() > regularQueueMaxLength) {
                    regularQueueMaxLength = regularQueue.size();
                }

                if (regularQueue.peek().remainingServiceTime > 1) {
                    regularQueue.peek().remainingServiceTime--;
                } else {
                    regularQueue.peek().remainingServiceTime--;
                    regularQueue.poll();
                    customersDone++;
                }
            } else {
                regularQueueIdleTime++;
            }
        }
    }

    void viewStats() {
        double expressAverageServiceTime = 0;
        double expressAverageWaitingTime = 0;
        double regularAverageServiceTime = 0;
        double regularAverageWaitingTime = 0;
        int waitingRegular = 0;
        int waitingExpress = 0;

        System.out.println("regular customers = " + numberOfRegularCustomers);
        System.out.println("express customers = " + numberOfExpressCustomers);
        System.out.println();

        for (Customer customer : customersArray) { // counting the number of customers who waited and each customer
                                                   // service and waiting time (for each type of customers)
            if ("Express".equals(customer.customerType)) {
                expressAverageServiceTime += customer.serviceTime;
                if (customer.waitingTime > 0) { // if the customer waited add his waiting time to the total express
                                                // customers waiting time (to calc. avg.) and increment waiting express.
                    expressAverageWaitingTime += customer.waitingTime;
                    waitingExpress++;
                }
            } else {
                regularAverageServiceTime += customer.serviceTime;
                if (customer.waitingTime > 0) {
                    regularAverageWaitingTime += customer.waitingTime;
                    waitingRegular++;
                }
            }
        }

        expressAverageServiceTime /= numberOfExpressCustomers;
        expressAverageWaitingTime /= numberOfExpressCustomers;
        regularAverageServiceTime /= numberOfRegularCustomers;
        regularAverageWaitingTime /= numberOfRegularCustomers;

        System.out.println("express customers average waiting time = " + expressAverageWaitingTime);
        System.out.println("express customers average service time = " + expressAverageServiceTime);
        System.out.println();

        System.out.println("regular customers average waiting time = " + regularAverageWaitingTime);
        System.out.println("regular customers average service time = " + regularAverageServiceTime);
        System.out.println();

        System.out.println("maximum express queue length = " + expressQueueMaxLength);
        System.out.println("maximum regular queue length = " + regularQueueMaxLength);
        System.out.println();

        System.out.println(
                "the probability that a customer wait in the express cashier queue according to express customers = "
                        + (double) waitingExpress / numberOfExpressCustomers);
        System.out.println(
                "the probability that a customer wait in the express cashier queue according to all the customers = "
                        + (double) waitingExpress / (numberOfExpressCustomers + numberOfRegularCustomers));
        System.out.println();

        System.out.println(
                "the probability that a customer wait in the regular cashier queue according to all the customers = "
                        + (double) waitingRegular / (numberOfExpressCustomers + numberOfRegularCustomers));
        System.out.println();

        System.out.println("system working time = " + systemTime);
        System.out.println();

        System.out.println("express queue idle time = " + expressQueueIdleTime);
        System.out.println();

        System.out.println("regular queue idle time = " + regularQueueIdleTime);
        System.out.println();

        for (int i = 0; i < 10; i++) { // printing the first 10 customers to monitor the system movement and make sure
                                       // its correct.
            System.out.println("customer number " + i + " :");
            System.out.println(customersArray[i]);
        }
    }

    public static void main(String[] args) throws Exception {
        new problemOne(10000);
    }
}
