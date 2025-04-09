import java.io.*;
import java.util.*;

public class RestaurantSystem {
    private final String ADMIN_EMAIL = "admin.com";
    private final String ADMIN_PASSWORD = "admin123";

    private ArrayList<Dish> dishes = new ArrayList<>();
    private ArrayList<Sale> sales = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void run() {
        loadData();
        if (login()) {
            adminMenu();
        }
        saveData();
    }

    private boolean login() {
        Utils.printLine();
        System.out.println("       Daffodil Green Garden       ");
        Utils.printLine();
        System.out.println("          LOGIN          ");
        Utils.printLine();

        System.out.print("Enter Email: ");
        String inputEmail = scanner.nextLine();
        System.out.print("Enter Password: ");
        String inputPassword = scanner.nextLine();

        if (inputEmail.equals(ADMIN_EMAIL) && inputPassword.equals(ADMIN_PASSWORD)) {
            System.out.println("Login successful!");
            Utils.clearScreen();
            return true;
        } else {
            System.out.println("Invalid email or password. Please try again.");
            return false;
        }
    }

    private void adminMenu() {
        int choice;
        do {
            System.out.println("          Admin Menu          ");
            Utils.printLine();
            System.out.println("1. Add Dish");
            System.out.println("2. Update Dish");
            System.out.println("3. Delete Dish");
            System.out.println("4. View Stock");
            System.out.println("5. Process Order");
            System.out.println("6. View Total Sales");
            System.out.println("0. Logout");
            Utils.printLine();
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1: addDish(); break;
                case 2: updateDish(); break;
                case 3: deleteDish(); break;
                case 4: viewStock(); break;
                case 5: processOrder(); break;
                case 6: viewTotalSales(); break;
                case 0: System.out.println("Logging out..."); break;
                default: System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 0) Utils.printLine();
        } while (choice != 0);
    }

    private void addDish() {
        viewStock();
        Utils.printLine();
        System.out.println("          Add Dish          ");
        Utils.printLine();

        System.out.print("Enter the name of the new dish: ");
        String name = scanner.nextLine();
        System.out.print("Enter the food code for the new dish: ");
        String code = scanner.nextLine();
        System.out.print("Enter the price: ");
        float price = Float.parseFloat(scanner.nextLine());
        System.out.print("Enter the stock quantity: ");
        int stock = Integer.parseInt(scanner.nextLine());

        dishes.add(new Dish(name, code, price, stock));
        System.out.println("Dish '" + name + "' (Food Code: " + code + ") added successfully!");
    }

    private void updateDish() {
        viewStock();
        Utils.printLine();
        System.out.println("          Update Dish          ");
        Utils.printLine();

        System.out.print("Enter the food code to update: ");
        String code = scanner.nextLine();
        Dish dish = findDishByCode(code);

        if (dish == null) {
            System.out.println("Dish not found.");
            return;
        }

        System.out.print("Enter new price: ");
        dish.price = Float.parseFloat(scanner.nextLine());
        System.out.print("Enter new stock quantity: ");
        dish.stock = Integer.parseInt(scanner.nextLine());
        System.out.println("Dish updated successfully!");
    }

    private void deleteDish() {
        viewStock();
        Utils.printLine();
        System.out.println("          Delete Dish          ");
        Utils.printLine();

        System.out.print("Enter the food code to delete: ");
        String code = scanner.nextLine();
        Dish dish = findDishByCode(code);

        if (dish == null) {
            System.out.println("Dish not found.");
            return;
        }

        dishes.remove(dish);
        System.out.println("Dish deleted successfully!");
    }

    private void viewStock() {
        Utils.clearScreen();
        System.out.println("       Daffodil Green Garden       ");
        Utils.printLine();
        System.out.println("          Current Stock          ");
        Utils.printLine();

        System.out.printf("%-30s | %-12s | %-10s | %-6s%n", "Dish Name", "Food Code", "Price", "Stock");
        System.out.println("-------------------------------------------------------------");
        for (Dish d : dishes) {
            System.out.printf("%-30s | %-12s | %-10.2f | %-6d%n", d.name, d.foodCode, d.price, d.stock);
        }
    }

    private Dish findDishByCode(String code) {
        for (Dish d : dishes) {
            if (d.foodCode.equals(code)) {
                return d;
            }
        }
        return null;
    }

    private void processOrder() {
        Utils.clearScreen();
        viewStock();
        Utils.printLine();
        System.out.println("          Process Order          ");
        Utils.printLine();

        System.out.print("Enter the date of the order (DD/MM/YYYY): ");
        String date = scanner.nextLine();
        System.out.print("Enter the customer's name: ");
        String customerName = scanner.nextLine();
        String fileName = "order_" + customerName.replaceAll("\s+", "_") + ".txt";

        try (PrintWriter billFile = new PrintWriter(fileName)) {
            float totalBill = 0;
            int orderCount = 1;

            billFile.println("--------------------------------------------------------------");
            billFile.println("                    Daffodil Green Garden");
            billFile.println("                     Customer: " + customerName);
            billFile.println("                      Date: " + date);
            billFile.println("--------------------------------------------------------------");
            billFile.printf("%-30s | %-8s | %-8s | %-8s%n", "Dish Name", "Quantity", "Price", "Total");
            billFile.println("--------------------------------------------------------------");

            boolean moreOrders = true;
            while (moreOrders) {
                System.out.println("Order #" + orderCount++);
                System.out.print("Enter food code to order: ");
                String code = scanner.nextLine();
                Dish dish = findDishByCode(code);

                if (dish == null) {
                    System.out.println("Dish not found.");
                    continue;
                }

                System.out.print("Enter quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine());

                if (quantity > dish.stock) {
                    System.out.println("Insufficient stock. Only " + dish.stock + " available.");
                    continue;
                }

                dish.stock -= quantity;
                float totalPrice = dish.price * quantity;
                totalBill += totalPrice;

                billFile.printf("%-30s | %-8d | %-8.2f | %-8.2f%n", dish.name, quantity, dish.price, totalPrice);
                sales.add(new Sale(dish.name, dish.foodCode, quantity, totalPrice, date));

                System.out.print("Do you want to add more orders? (yes/no): ");
                moreOrders = scanner.nextLine().equalsIgnoreCase("yes");
            }

            System.out.println("Total bill before discount: " + totalBill);
            System.out.print("Enter discount percentage (0 for no discount): ");
            float discount = Float.parseFloat(scanner.nextLine());
            float discountAmount = (discount / 100) * totalBill;
            float finalBill = totalBill - discountAmount;

            System.out.println("Discount applied: " + discountAmount);
            System.out.println("Total bill after discount: " + finalBill);

            billFile.println("--------------------------------------------------------------");
            billFile.printf("%-30s   %-8s   %-8s   %-8.2f%n", "Total Bill", "", "", totalBill);
            billFile.printf("%-30s   %-8s   %-8s   %-8.2f%n", "Discount", "", "", discountAmount);
            billFile.printf("%-30s   %-8s   %-8s   %-8.2f%n", "Total After Discount", "", "", finalBill);
            billFile.println("--------------------------------------------------------------");
            billFile.println("                 Thank you for your order!");
            billFile.println("--------------------------------------------------------------");

            System.out.println("The bill has been saved to '" + fileName + "'.");

        } catch (IOException e) {
            System.out.println("Error writing bill file.");
        }
    }

    private void viewTotalSales() {
        Utils.printLine();
        System.out.println("          Total Sales          ");
        Utils.printLine();

        float total = 0;
        String lastDate = "";

        for (Sale s : sales) {
            if (!s.date.equals(lastDate)) {
                if (!lastDate.isEmpty()) System.out.println();
                System.out.println("--------------------------------------------------------------");
                System.out.println("                   Sales for Date: " + s.date);
                System.out.println("--------------------------------------------------------------");
                System.out.printf("%-30s | %-8s | %-8s | %-8s%n", "Dish Name", "Quantity", "Price", "Total");
                System.out.println("--------------------------------------------------------------");
                lastDate = s.date;
            }
            System.out.printf("%-30s | %-8d | %-8.2f | %-8.2f%n", s.dishName, s.quantity, s.totalPrice / s.quantity, s.totalPrice);
            total += s.totalPrice;
        }

        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-30s | %-8s | %-8s | %-8.2f%n", "Total Sales", "", "", total);
        System.out.println("--------------------------------------------------------------");
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("dishes.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    dishes.add(new Dish(parts[0], parts[1], Float.parseFloat(parts[2]), Integer.parseInt(parts[3])));
                }
            }
        } catch (IOException e) {}

        try (BufferedReader reader = new BufferedReader(new FileReader("sales.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    sales.add(new Sale(parts[0], parts[1], Integer.parseInt(parts[2]), Float.parseFloat(parts[3]), parts[4]));
                }
            }
        } catch (IOException e) {}
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter("dishes.txt")) {
            for (Dish d : dishes) {
                writer.printf("%s,%s,%.2f,%d%n", d.name, d.foodCode, d.price, d.stock);
            }
        } catch (IOException e) {
            System.out.println("Error saving dishes.");
        }

        try (PrintWriter writer = new PrintWriter("sales.txt")) {
            for (Sale s : sales) {
                writer.printf("%s,%s,%d,%.2f,%s%n", s.dishName, s.foodCode, s.quantity, s.totalPrice, s.date);
            }
        } catch (IOException e) {
            System.out.println("Error saving sales.");
        }
    }
}
