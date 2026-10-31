
package ru.netology.data;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class DataGenerator {

    private static final Faker faker =
            new Faker(new Locale("en"));

    private static final Gson gson = new Gson();

    private static final RequestSpecification requestSpec =
            new RequestSpecBuilder()
                    .setBaseUri("http://localhost")
                    .setPort(9999)
                    .setAccept(ContentType.JSON)
                    .setContentType(ContentType.JSON)
                    .build();

    private DataGenerator() {
    }

    // Создаём случайный логин
    public static String generateLogin() {
        return faker.name().username()
                + UUID.randomUUID().toString().substring(0, 8);
    }

    // Создаём случайный пароль
    public static String generatePassword() {
        return faker.internet().password(8, 16);
    }

    // Генерируем пользователя с нужным статусом
    public static RegistrationDto generateUser(String status) {
        return new RegistrationDto(
                generateLogin(),
                generatePassword(),
                status
        );
    }

    // Отправляем пользователя в банк через API
    public static void registerUser(RegistrationDto user) {
        given()
                .spec(requestSpec)
                .body(gson.toJson(user))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    // Создаём и регистрируем активного пользователя
    public static RegistrationDto getActiveUser() {
        RegistrationDto user = generateUser("active");
        registerUser(user);
        return user;
    }

    // Создаём и регистрируем заблокированного пользователя
    public static RegistrationDto getBlockedUser() {
        RegistrationDto user = generateUser("blocked");
        registerUser(user);
        return user;
    }
}
