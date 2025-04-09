public class Sale {
    String dishName;
    String foodCode;
    int quantity;
    float totalPrice;
    String date;

    public Sale(String dishName, String foodCode, int quantity, float totalPrice, String date) {
        this.dishName = dishName;
        this.foodCode = foodCode;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.date = date;
    }
}