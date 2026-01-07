package tests;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

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

@Feature("Курьеры")
@DisplayName("Тесты API для работы с курьерами (с использованием CourierCredentials)")
public class CourierCredentialsTest {

    private CourierApi courierApi;
    private String login;
    private String password;
    private Integer courierId;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        
        // Создаем курьера перед каждым тестом
        login = TestDataGenerator.generateRandomLogin();
        password = TestDataGenerator.generateRandomPassword();
        
        Courier courier = new Courier(
                login,
                password,
                TestDataGenerator.generateRandomFirstName()
        );
        
        // Регистрируем курьера
        courierApi.createCourier(courier)
                .then()
                .statusCode(SC_CREATED);
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            try {
                Response deleteResponse = courierApi.deleteCourier(courierId);
                deleteResponse.then().statusCode(SC_OK);
            } catch (Exception e) {
                System.out.println("Не удалось удалить курьера: " + e.getMessage());
            }
        }
    }

    @Test
    @Story("Авторизация курьера")
    @DisplayName("Успешная авторизация с использованием CourierCredentials")
    @Description("Проверка что CourierCredentials корректно работает для логина")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithCourierCredentials() {
        // Логинимся с CourierCredentials (данные созданы в setUp)
        CourierCredentials credentials = new CourierCredentials(login, password);
        Response loginResponse = courierApi.loginCourier(credentials);
        
        courierId = loginResponse.then()
                .statusCode(SC_OK)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Test
    @Story("Авторизация курьера")
    @DisplayName("Авторизация с неверным паролем")
    @Description("Проверка ошибки при авторизации с неверным паролем")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithWrongPassword() {
        // Пытаемся залогиниться с неверным паролем
        CourierCredentials wrongCredentials = new CourierCredentials(login, "wrong_password");
        courierApi.loginCourier(wrongCredentials)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));

        // Получаем ID для удаления (используем правильные данные)
        CourierCredentials correctCredentials = new CourierCredentials(login, password);
        Response loginResponse = courierApi.loginCourier(correctCredentials);
        courierId = loginResponse.then()
                .statusCode(SC_OK)
                .extract()
                .path("id");
    }

    @Test
    @Story("Авторизация курьера")
    @DisplayName("Авторизация без логина")
    @Description("Проверка ошибки при авторизации без логина")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithoutLogin() {
        CourierCredentials credentials = new CourierCredentials("", "password");
        courierApi.loginCourier(credentials)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
        
        // Получаем ID для удаления
        CourierCredentials correctCredentials = new CourierCredentials(login, password);
        Response loginResponse = courierApi.loginCourier(correctCredentials);
        courierId = loginResponse.then()
                .statusCode(SC_OK)
                .extract()
                .path("id");
    }

    @Test
    @Story("Авторизация курьера")
    @DisplayName("Авторизация несуществующего курьера")
    @Description("Проверка ошибки при авторизации несуществующего курьера")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginNonExistentCourier() {
        CourierCredentials credentials = new CourierCredentials(
                "nonexistent_" + System.currentTimeMillis(),
                "password"
        );
        courierApi.loginCourier(credentials)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
        
        // Получаем ID для удаления
        CourierCredentials correctCredentials = new CourierCredentials(login, password);
        Response loginResponse = courierApi.loginCourier(correctCredentials);
        courierId = loginResponse.then()
                .statusCode(SC_OK)
                .extract()
                .path("id");
    }
}
