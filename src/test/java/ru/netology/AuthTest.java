
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

    // Тест 1. Успешная авторизация активного пользователя
    @Test
    void shouldLoginWithActiveUser() {
        RegistrationDto user = DataGenerator.getActiveUser();

        login(user.getLogin(), user.getPassword());

        $(byText("Личный кабинет"))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    // Тест 2. Заблокированный пользователь
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

        login(
                user.getLogin(),
                DataGenerator.generateWrongPassword(user.getPassword())
        );

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 4. Активный пользователь с неправильным логином
    @Test
    void shouldNotLoginWithWrongLogin() {
        RegistrationDto user = DataGenerator.getActiveUser();

        login(
                DataGenerator.generateWrongLogin(user.getLogin()),
                user.getPassword()
        );

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 5. Незарегистрированный пользователь
    @Test
    void shouldNotLoginWithUnregisteredUser() {
        RegistrationDto user = DataGenerator.getUnregisteredUser();

        login(user.getLogin(), user.getPassword());

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 6. Заблокированный пользователь с неправильным паролем
    @Test
    void shouldNotLoginBlockedUserWithWrongPassword() {
        RegistrationDto user = DataGenerator.getBlockedUser();

        login(
                user.getLogin(),
                DataGenerator.generateWrongPassword(user.getPassword())
        );

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 7. Заблокированный пользователь с неправильным логином
    @Test
    void shouldNotLoginBlockedUserWithWrongLogin() {
        RegistrationDto user = DataGenerator.getBlockedUser();

        login(
                DataGenerator.generateWrongLogin(user.getLogin()),
                user.getPassword()
        );

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Тест 8. Активный пользователь с неправильными
    // логином и паролем
    @Test
    void shouldNotLoginWithWrongLoginAndPassword() {
        RegistrationDto user = DataGenerator.getActiveUser();

        login(
                DataGenerator.generateWrongLogin(user.getLogin()),
                DataGenerator.generateWrongPassword(user.getPassword())
        );

        checkLoginError(INVALID_CREDENTIALS);
    }

    // Общий метод авторизации
    private void login(String login, String password) {
        open("/");

        $("[data-test-id='login'] input")
                .setValue(login);

        $("[data-test-id='password'] input")
                .setValue(password);

        $("[data-test-id='action-login']").click();
    }

    // Проверка сообщения об ошибке авторизации
    private void checkLoginError(String expectedError) {
        $(".notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text(expectedError));

        $(byText("Личный кабинет"))
                .shouldNotBe(visible);
    }
}
