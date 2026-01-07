package tests;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.hamcrest.CoreMatchers.equalTo;

import api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.TestDataGenerator;

import java.util.HashMap;
import java.util.Map;

@Feature("Курьеры")
@DisplayName("Тесты API для создания курьеров")
public class CourierTest {

    private CourierApi courierApi;
    private final Map<String, String> createdCouriers = new HashMap<>();

    @Before
    public void setUp() {
        courierApi = new CourierApi();
    }

    @After
    public void tearDown() {
        for (Map.Entry<String, String> entry : createdCouriers.entrySet()) {
            try {
                CourierCredentials credentials = new CourierCredentials(entry.getKey(), entry.getValue());
                Response loginResponse = courierApi.loginCourier(credentials);
                
                if (loginResponse.getStatusCode() == SC_OK) {
                    Integer courierId = loginResponse.then()
                            .extract()
                            .path("id");
                    
                    Response deleteResponse = courierApi.deleteCourier(courierId);
                    deleteResponse.then().statusCode(SC_OK);
                }
            } catch (Exception e) {
                System.out.println("Не удалось удалить курьера " + entry.getKey() + ": " + e.getMessage());
            }
        }
        createdCouriers.clear();
    }

    @Test
    @Story("Создание курьера")
    @DisplayName("Успешное создание курьера")
    @Description("Проверка успешного создания нового курьера")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateCourierSuccess() {
        String login = TestDataGenerator.generateRandomLogin();
        String password = TestDataGenerator.generateRandomPassword();
        Courier courier = new Courier(login, password, TestDataGenerator.generateRandomFirstName());

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        createdCouriers.put(login, password);
    }

    @Test
    @Story("Создание курьера")
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("Проверка что нельзя создать двух курьеров с одинаковым логином")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateDuplicateCourier() {
        String login = TestDataGenerator.generateRandomLogin();
        String password = TestDataGenerator.generateRandomPassword();
        Courier courier = new Courier(login, password, TestDataGenerator.generateRandomFirstName());

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CREATED);

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        createdCouriers.put(login, password);
    }

    @Test
    @Story("Создание курьера")
    @DisplayName("Создание курьера без логина")
    @Description("Проверка создания курьера без указания логина")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateCourierWithoutLogin() {
        Courier courier = new Courier(
                "",
                TestDataGenerator.generateRandomPassword(),
                TestDataGenerator.generateRandomFirstName()
        );

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @Story("Создание курьера")
    @DisplayName("Создание курьера без пароля")
    @Description("Проверка создания курьера без указания пароля")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateCourierWithoutPassword() {
        String login = TestDataGenerator.generateRandomLogin();
        Courier courier = new Courier(login, "", TestDataGenerator.generateRandomFirstName());

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @Story("Создание курьера")
    @DisplayName("Успешное создание курьера (без имени)")
    @Description("Проверка успешного создания курьера без указания имени")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateCourierWithoutFirstName() {
        String login = TestDataGenerator.generateRandomLogin();
        String password = TestDataGenerator.generateRandomPassword();
        Courier courier = new Courier(login, password, "");

        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        createdCouriers.put(login, password);
    }
}
