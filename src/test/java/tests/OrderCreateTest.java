package tests;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.CoreMatchers.notNullValue;

import api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.TestDataGenerator;

import java.util.Arrays;
import java.util.Collection;

@Feature("Заказы")
@DisplayName("Тесты API для создания заказов")
@RunWith(Parameterized.class)
public class OrderCreateTest {

    private OrderApi orderApi;
    private Integer createdOrderTrack;
    private final String[] colors;

    public OrderCreateTest(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {new String[]{"BLACK"}},
            {new String[]{"GREY"}},
            {new String[]{"BLACK", "GREY"}},
            {new String[]{}}
        });
    }

    @Before
    public void setUp() {
        orderApi = new OrderApi();
    }

    @After
    public void tearDown() {
        if (createdOrderTrack != null) {
            try {
                orderApi.cancelOrder(createdOrderTrack);
                System.out.println("Попытка отмены заказа с треком: " + createdOrderTrack);
            } catch (Exception e) {
                System.out.println("Не удалось отменить заказ " + createdOrderTrack + ": " + e.getMessage());
            }
        }
    }

    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа")
    @Description("Проверка создания заказа с различными комбинациями цветов")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateOrder() {
        Order order = TestDataGenerator.generateOrderWithColors(colors);
        
        Response response = orderApi.createOrder(order);
        createdOrderTrack = response.then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue())
                .extract()
                .path("track");
        
        System.out.println("✅ Создан заказ с треком: " + createdOrderTrack + " и цветами: " + Arrays.toString(colors));
    }
}
