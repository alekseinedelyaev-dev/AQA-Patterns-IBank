
package ru.netology.data;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class DataGenerator {

    private static final Faker faker =
            new Faker(new Locale("en"));

    private static final RequestSpecification requestSpec =
            new RequestSpecBuilder()
                    .setBaseUri("http://localhost")
                    .setPort(9999)
                    .setAccept(ContentType.JSON)
                    .setContentType(ContentType.JSON)
                    .build();

    private DataGenerator() {
    }

    // Генерация случайного логина
    public static String generateLogin() {
        return faker.name().username()
                + UUID.randomUUID().toString().substring(0, 8);
    }

    // Генерация случайного пароля
    public static String generatePassword() {
        return faker.internet().password(8, 16);
    }

    // Генерация неправильного логина
    public static String generateWrongLogin(String correctLogin) {
        String wrongLogin;

        do {
            wrongLogin = generateLogin();
        } while (wrongLogin.equals(correctLogin));

        return wrongLogin;
    }

    // Генерация неправильного пароля
    public static String generateWrongPassword(String correctPassword) {
        String wrongPassword;

        do {
            wrongPassword = generatePassword();
        } while (wrongPassword.equals(correctPassword));

        return wrongPassword;
    }

    // Генерация пользователя с указанным статусом
    public static RegistrationDto generateUser(String status) {
        return new RegistrationDto(
                generateLogin(),
                generatePassword(),
                status
        );
    }

    // Регистрация пользователя через API банка
    public static void registerUser(RegistrationDto user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    // Создание и регистрация активного пользователя
    public static RegistrationDto getActiveUser() {
        RegistrationDto user = generateUser("active");
        registerUser(user);
        return user;
    }

    // Создание и регистрация заблокированного пользователя
    public static RegistrationDto getBlockedUser() {
        RegistrationDto user = generateUser("blocked");
        registerUser(user);
        return user;
    }

    // Генерация незарегистрированного пользователя
    public static RegistrationDto getUnregisteredUser() {
        return generateUser("active");
    }
}
