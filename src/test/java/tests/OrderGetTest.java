package tests;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.notNullValue;

import api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

@Feature("Заказы")
@DisplayName("Тесты API для получения заказов")
public class OrderGetTest {

    private OrderApi orderApi;

    @Before
    public void setUp() {
        orderApi = new OrderApi();
    }

    @Test
    @Story("Получение заказов")
    @DisplayName("Получение списка заказов")
    @Description("Проверка получения списка заказов")
    @Severity(SeverityLevel.NORMAL)
    public void testGetOrdersList() {
        Response response = orderApi.getOrders();
        response.then()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
        System.out.println("✅ Получен список заказов");
    }
}
