import POM.LoginPage;
import POM.MainPage;
import POM.RegistrationPage;
import entity.User;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import restClients.UserRestClient;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.assertTrue;

@Story("Тесты на регистрацию")
public class RegistrationTestsInChrome {

    public String nameForRegistration;
    public String emailForRegistration;
    public String passwordForRegistration;
    private MainPage mainPage;
    private RegistrationPage registrationPage;
    private LoginPage loginPage;
    private User user;

    @Before
    @DisplayName("Создание рандомных данных, вход на сайт")
    public void setUp() {
        nameForRegistration = RandomStringUtils.randomAlphabetic(10);
        emailForRegistration = String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(6), RandomStringUtils.randomAlphabetic(6)).toLowerCase(Locale.ROOT);
        passwordForRegistration = RandomStringUtils.randomAlphabetic(7);

        user = User.builder()
                .email(emailForRegistration)
                .name(nameForRegistration)
                .password(passwordForRegistration)
                .build();
        mainPage = open("https://stellarburgers.nomoreparties.site/", MainPage.class);
        mainPage.clickToTheEnterButton();
    }

    @After
    @DisplayName("Очищение кеша")
    public void tearDown() {
        UserRestClient.deleteUser(user.getToken());

        clearBrowserCookies();
        clearBrowserLocalStorage();
        closeWebDriver();
    }

    @Test
    @DisplayName("Тест успешной регистрации пользователя")
    public void successfulRegistrationTest() {
        loginPage = open("https://stellarburgers.nomoreparties.site/login", LoginPage.class);
        loginPage.clickToTheRegistrationButton();
        registrationPage = open("https://stellarburgers.nomoreparties.site/register", RegistrationPage.class);
        registrationPage.registrationNewUser(nameForRegistration, emailForRegistration, passwordForRegistration);

        loginPage = open("https://stellarburgers.nomoreparties.site/login", LoginPage.class);

        user.setToken(UserRestClient.authorizationUser(user).extract().jsonPath().get("accessToken"));
        assertTrue(loginPage.getTitleFromTheLoginPage());
    }

    @Test
    @DisplayName("Тест провальной регистрации (из-за пароля < 6 символов)")
    public void unSuccessfulRegistrationTest() {
        loginPage = open("https://stellarburgers.nomoreparties.site/login", LoginPage.class);
        loginPage.clickToTheRegistrationButton();
        registrationPage = open("https://stellarburgers.nomoreparties.site/register", RegistrationPage.class);
        registrationPage.registrationNewUser(nameForRegistration, emailForRegistration, passwordForRegistration);

        assertTrue(registrationPage.showRegistrationErrorText());
    }

}
