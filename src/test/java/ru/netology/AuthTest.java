
package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;
import ru.netology.data.RegistrationDto;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class AuthTest {

    private static final String INVALID_CREDENTIALS =
            "Неверно указан логин или пароль";

    private static final String BLOCKED_USER =
            "Пользователь заблокирован";

    @BeforeAll
    static void setup() {
        Configuration.baseUrl = "http://localhost:9999";
    }

    // Тест 1. Активный пользователь с правильными данными
    @Test
    void shouldLoginWithActiveUser() {
        RegistrationDto user = DataGenerator.getActiveUser();

        login(user.getLogin(), user.getPassword());

        $(byText("Личный кабинет"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    // Тест 2. Заблокированный пользователь с правильными данными
    @Test
    void shouldNotLoginWithBlockedUser() {
        RegistrationDto user = DataGenerator.getBlockedUser();

        login(user.getLogin(), user.getPassword());

        checkLoginError(BLOCKED_USER);
    }

    // Тест 3. Активный пользователь с неправильным паролем
    @Test
    void shouldNotLoginWithWrongPassword() {
        RegistrationDto user = DataGenerator.getActiveUser();

        String wrongPassword = "wrong" + user.getPassword();

        login(user.getLogin(), wrongPassword);

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 4. Активный пользователь с неправильным логином
    @Test
    void shouldNotLoginWithWrongLogin() {
        RegistrationDto user = DataGenerator.getActiveUser();

        String wrongLogin = user.getLogin() + "wrong";

        login(wrongLogin, user.getPassword());

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 5. Незарегистрированный пользователь
    @Test
    void shouldNotLoginWithUnregisteredUser() {
        // Создаём данные, но не отправляем их через API
        RegistrationDto user = DataGenerator.generateUser("active");

        login(user.getLogin(), user.getPassword());

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 6. Заблокированный пользователь с неправильным паролем
    @Test
    void shouldNotLoginBlockedUserWithWrongPassword() {
        RegistrationDto user = DataGenerator.getBlockedUser();

        String wrongPassword = "wrong" + user.getPassword();

        login(user.getLogin(), wrongPassword);

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 7. Заблокированный пользователь с неправильным логином
    @Test
    void shouldNotLoginBlockedUserWithWrongLogin() {
        RegistrationDto user = DataGenerator.getBlockedUser();

        String wrongLogin = user.getLogin() + "wrong";

        login(wrongLogin, user.getPassword());

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 8. Активный пользователь с неверным логином и паролем
    @Test
    void shouldNotLoginWithWrongLoginAndPassword() {
        RegistrationDto user = DataGenerator.getActiveUser();

        String wrongLogin = user.getLogin() + "wrong";
        String wrongPassword = "wrong" + user.getPassword();

        login(wrongLogin, wrongPassword);

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Общий метод заполнения формы авторизации
    private void login(String login, String password) {
        open("/");

        $("[data-test-id='login'] input")
                .setValue(login);

        $("[data-test-id='password'] input")
                .setValue(password);

        $("[data-test-id='action-login']").click();
    }

    // Проверка ошибки авторизации
    private void checkLoginError(String expectedError) {

        $(".notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldNotBe(empty);

        String actualError =
                $(".notification__content").getText();

        // Выводим реальное сообщение для анализа
        System.out.println("Сообщение банка: " + actualError);

        // Проверяем ожидаемое сообщение
        $(".notification__content")
                .shouldHave(text(expectedError));

        // Убеждаемся, что личный кабинет не открылся
        $(byText("Личный кабинет"))
                .shouldNotBe(visible);
    }
}
