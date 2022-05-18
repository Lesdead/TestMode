package data;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import java.util.Locale;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class DataGenerator {

    public DataGenerator() {
    }

    @Value
    public static class UserInfo {
        String login;
        String password;
        String status;
    }

    public static String generateLogin(String locale) {
        String login = new Faker(new Locale(locale)).name().username();
        return login;
    }

    public static String generatePassword(String locale) {
        String password = new Faker(new Locale(locale)).internet().password();
        return password;
    }

    public static String generateStatus(String locale) {
        String[] status = {"active", "blocked"};
        String star = status[new Random().nextInt(status.length)];
        return star;
    }

    public static class Registration {
        static RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        public Registration() {

        }

        public static UserInfo generateUser(String locale, String status) {
            var user = new UserInfo(DataGenerator.generateLogin(locale), DataGenerator.generatePassword(locale), status);
            return user;

        }

        public static void registrationUser(UserInfo user) {
            given()
                    .spec(requestSpec)
                    .body(new Gson().toJson(user))
                    .when()
                    .post("/api/system/users")
                    .then()
                    .statusCode(200);
        }
    }
}
