package test;

import data.DataGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static data.DataGenerator.Registration.generateUser;
import static data.DataGenerator.Registration.registrationUser;

public class AuthTest {
    private static DataGenerator.UserInfo userActive = generateUser("en", "active");
    private static DataGenerator.UserInfo userBlocked = generateUser("en", "blocked");
    private static DataGenerator.UserInfo userBlockedFinally = generateUser("en", "blocked");

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @BeforeAll
    static void setRegUser() {
        registrationUser(userActive);
        registrationUser(userBlocked);
        registrationUser(userBlockedFinally);
    }

    @Test
    void shouldAuthIfUserExistAndStatusActive() {
        $("[name=login]").setValue(userActive.getLogin());
        $("[name=password]").setValue(userActive.getPassword());
        $(byText("Продолжить")).click();
        $(".heading").shouldHave(text("  Личный кабинет"));
    }

    @Test
    void shouldAuthIfUserExistStatusActivePasswordNotCorrect() {
        $("[name=login]").setValue(userActive.getLogin());
        $("[name=password]").setValue(DataGenerator.generatePassword("en"));
        $(byText("Продолжить")).click();
        $("[data-test-id] .notification__content").shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldAuthIfUserExistStatusActiveAndLoginWithMistake() {
        $("[name=login]").setValue(DataGenerator.generateLogin("en"));
        $("[name=password]").setValue(userActive.getPassword());
        $(byText("Продолжить")).click();
        $("[data-test-id] .notification__content").shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldAuthIfUserExistStatusBlocked() {
        $("[name=login]").setValue(userBlockedFinally.getLogin());
        $("[name=password]").setValue(userBlockedFinally.getPassword());
        $(byText("Продолжить")).click();
        $("[data-test-id] .notification__content").shouldHave(text("Ошибка! Пользователь заблокирован"));
    }

    @Test
    void shouldAuthIfUserBlockedAndStatusChangeToActive() {
        $("[name=login]").setValue(userBlocked.getLogin());
        $("[name=password]").setValue(userBlocked.getPassword());
        $(byText("Продолжить")).click();
        $("[data-test-id] .notification__content").shouldHave(text("Ошибка! Пользователь заблокирован"));

        registrationUser(new DataGenerator.UserInfo(userBlocked.getLogin(), userBlocked.getPassword(), "active"));

        $(byText("Продолжить")).click();
        $(".heading").shouldHave(text("  Личный кабинет"));
    }

    @Test
    void shouldAuthIfUserNotExist() {
        $("[name=login]").setValue(DataGenerator.generateLogin("en"));
        $("[name=password]").setValue(DataGenerator.generatePassword("en"));
        $(byText("Продолжить")).click();
        $("[data-test-id] .notification__content").shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldAuthIfUserBlockedAndPasswordIncorrect() {
        $("[name=login]").setValue(userBlockedFinally.getLogin());
        $("[name=password]").setValue(DataGenerator.generatePassword("en"));
        $(byText("Продолжить")).click();
        $("[data-test-id] .notification__content").shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

}
