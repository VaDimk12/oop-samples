import java.util.ArrayList;
import java.util.List;
import com.google.inject.Guice;
import com.google.inject.Injector;

// Інтерфейс для оплати
interface Payable {
    void payDeposit(double amount);
}

// Абстрактний клас Користувач
abstract class User {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
 
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract String getInfo();
}

// Клас Клієнт
class Client extends User implements Payable {
    private String contactInfo;
    private Order currentOrder;
    private ClientService clientService;

    public Client(int id, String name, String contactInfo, ClientService clientService) {
        super(id, name);
        this.contactInfo = contactInfo;
        this.clientService = clientService;
    }

    public Request createRequest(String description) {
        System.out.println(getName() + " створив(ла) заявку: " + description);
        return new Request(1, description);
    }

    public void confirmOrder(Order order) {
        System.out.println(getName() + " підтвердив(ла) замовлення №" + order.getId());
        order.confirm();
        currentOrder = order;
        clientService.saveOrder(order);
    }

    @Override
    public final void payDeposit(double amount) {
        System.out.println(getName() + " оплатив(ла) завдаток на суму " + amount + " грн");
    }

    @Override
    public String getInfo() {
        return "Клієнт: " + getName() + " (" + contactInfo + ")";
    }
}

// Клас Майстер
class Master extends User {
    private String specialization;

    public Master(int id, String name, String specialization) {
        super(id, name);
        this.specialization = specialization;
    }

    public Estimate evaluateRequest(Request request, double cost, String visitTime) {
        System.out.println(getName() + " оцінив(ла) заявку №" + request.getId());
        return new Estimate(1, cost, visitTime);
    }

    public void performWork(Order order) {
        System.out.println(getName() + " виконує роботу за замовленням №" + order.getId());
        order.updateStatus("Виконується");
    }

    public void closeOrder(Order order) {
        System.out.println(getName() + " закрив(ла) замовлення №" + order.getId());
        order.close();
    }

    @Override
    public String getInfo() {
        return "Майстер: " + getName() + " (" + specialization + ")";
    }
}

// Клас Заявка
class Request {
    private int id;
    private String description;
    private String photo;
    private String status;

    public Request(int id, String description) {
        this.id = id;
        this.description = description;
        this.status = "Чернетка";
    }

    public int getId() {
        return id;
    }

    public void attachPhoto(String photo) {
        this.photo = photo;
        System.out.println("Додано фото: " + photo);
    }

    public void send() {
        this.status = "Надіслано";
        System.out.println("Заявку надіслано на оцінку.");
    }

    public void updateStatus(String status) {
        this.status = status;
        System.out.println("Статус заявки змінено на: " + status);
    }
}

// Клас Кошторис
class Estimate {
    private int id;
    private double cost;
    private String visitTime;
    private String comment;

    public Estimate(int id, double cost, String visitTime) {
        this.id = id;
        this.cost = cost;
        this.visitTime = visitTime;
        this.comment = "";
    }

    public double calculateTotal() {
        return cost;
    }

    public void linkToRequest(Request request) {
        System.out.println("Кошторис прив’язано до заявки №" + request.getId());
    }

    public double getCost() {
        return cost;
    }

    public String getVisitTime() {
        return visitTime;
    }
}

// Клас Замовлення
class Order {
    private int id;
    private String date;
    private String status;
    private double deposit;

    public Order(int id, double deposit) {
        this.id = id;
        this.deposit = deposit;
        this.status = "Очікує підтвердження";
        this.date = "2025-11-03";
    }

    public int getId() {
        return id;
    }

    public void confirm() {
        status = "Підтверджено";
        System.out.println("Замовлення підтверджено.");
    }

    public void updateStatus(String status) {
        this.status = status;
        System.out.println("Статус замовлення: " + status);
    }

    public void close() {
        status = "Завершено";
        System.out.println("Замовлення успішно завершено.");
    }

    public double getDeposit() {
        return deposit;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}

// Клас Платіжна система
class PaymentSystem {
    private String transactionId;
    private double amount;
    private String status;

    public void processPayment(Order order) {
        this.amount = order.getDeposit();
        this.transactionId = "TX-" + System.currentTimeMillis();
        this.status = "Обробка";
        System.out.println("Обробка платежу для замовлення №" + order.getId() + "...");
    }

    public boolean verifyPayment(Order order) {
        this.status = "Підтверджено";
        System.out.println("Оплату для замовлення №" + order.getId() + " підтверджено.");
        return true;
    }
}

// Головний клас системи
public class OrderSystem {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new RenovationModule());
        ClientFactory clientFactory = injector.getInstance(ClientFactory.class);
        Client client = clientFactory.create(1, "Вадим Комиш", "vadym@example.com");
        Master master = new Master(2, "Олександр Майстер", "Сантехнік");

        Request request = client.createRequest("Полагодити кухонний кран");
        request.attachPhoto("photo_faucet.jpg");
        request.send();

        Estimate estimate = master.evaluateRequest(request, 1500.0, "2025-11-05 10:00");
        estimate.linkToRequest(request);
        System.out.println("Вартість робіт: " + estimate.getCost() + " грн, Візит: " + estimate.getVisitTime());

        Order order = new Order(1, 500.0);
        client.confirmOrder(order);
        client.payDeposit(order.getDeposit());

        PaymentSystem payment = new PaymentSystem();
        payment.processPayment(order);
        if (payment.verifyPayment(order)) {
            master.performWork(order);
            master.closeOrder(order);
        }

        System.out.println("\n--- Процес успішно завершено ---");
    }
}
