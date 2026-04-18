package tbcgeTest;

import basetest.BaseTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import API.Models.MoneyTransferSystem;
import API.Client.MoneyTransferClient;
import org.example.data.Constants;
import org.testng.Assert;
import org.testng.annotations.Test;
import steps.MoneyTransferSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class MoneyTransferApiTest extends BaseTest { // ვგულისხმობ რომ BaseTest გაქვს

    @Test
    public void verifyMoneyTransferSystemsApiAndUi() throws Exception {

        // --- 1. API მოთხოვნა ---
        MoneyTransferClient client = new MoneyTransferClient(request); // 'request' მოდის BaseTest-დან
        APIResponse response = client.getTransferSystems();

        // 2. სტატუს კოდის ვალიდაცია
        Assert.assertEquals(response.status(), Constants.HTTP_STATUS_OK,
                "API-მ არ დააბრუნა 200 OK!");

        // --- 3. JSON Deserialization ---
        ObjectMapper mapper = new ObjectMapper();
        List<MoneyTransferSystem> apiSystems = mapper.readValue(
                response.text(),
                new TypeReference<List<MoneyTransferSystem>>() {
                }
        );

        // 4. API მონაცემების ვალიდაცია (Business Rules & Patterns)
        Assert.assertFalse(apiSystems.isEmpty(), "სისტემების სია ცარიელია!");

        for (MoneyTransferSystem system : apiSystems) {
            // ვამოწმებთ, რომ name შეესაბამება ჩვენს Pattern-ს
            Matcher nameMatcher = Constants.SYSTEM_NAME_PATTERN.matcher(system.getName());
            Assert.assertTrue(nameMatcher.matches(),
                    "სისტემის სახელი არასწორი ფორმატისაა: " + system.getName());

            Assert.assertNotNull(system.getMtSystem(), "mtSystem ველი არ უნდა იყოს ცარიელი!");

            Matcher mysystemMatcher = Constants.SYSTEM_NAME_PATTERN.matcher(system.getMtSystem());
            Assert.assertTrue(mysystemMatcher.matches(), "mtSystem არასწორი ფორმატისაა!");

            // ვამოწმებთ სურათის URL-ის სტრუქტურას
            Matcher imageMatcher = Constants.IMAGE_URL_PATTERN.matcher(system.getImageUrl());
            Assert.assertTrue(imageMatcher.matches(),
                    "სურათის URL არ ემთხვევა მოთხოვნილ პატერნს: " + system.getImageUrl());

            // ვამოწმებთ რომ ვალუტები არსებობს
            Assert.assertFalse(system.getCurrencies().isEmpty(),
                    system.getName() + " - ვალუტები არ არის მითითებული!");
        }

// --- 5. UI შედარება ---
        // აქ ვიყენებთ შენს Page/Step მეთოდებს
        MoneyTransferSteps moneyTransferSteps = new MoneyTransferSteps(page);
        moneyTransferSteps.openPageAndHandleCookies(Constants.MONEY_TRANSFER_URL);
        page.waitForSelector(".tbcx-pw-card__logo-and-text-info");
        List<String> uiSystemNames = moneyTransferSteps.getVisibleSystemNames();

        // ვქმნით ახალ სიას და ვასუფთავებთ UI-დან წამოღებულ სახელებს
        List<String> cleanedUiNames = new ArrayList<>();
        for (String name : uiSystemNames) {
            // \\s+ შლის აბსოლუტურად ყველა სფეისს, ტაბს და ახალ ხაზს
            cleanedUiNames.add(name.replaceAll("\\s+", ""));
        }
        // ვამოწმებთ, რომ API-დან წამოღებული სისტემა ჩანს UI-ზეც
        for (MoneyTransferSystem apiSystem : apiSystems) {
            // 5.1 ვადარებთ სისტემის სახელებს
            Assert.assertTrue(cleanedUiNames.contains(apiSystem.getName()),
                    "API-ს სისტემა: " + apiSystem.getName() + " ვერ მოიძებნა UI-ზე!");
            // 5.2 ვადარებთ ვალუტებს
            // ვიძახებთ სტეპს, რომელიც კონკრეტული სისტემისთვის UI-დან ამოიღებს ვალუტების სიას
            List<String> uiCurrencies = moneyTransferSteps.getCurrenciesForSystem(apiSystem.getName());
            for (String apiCurrency : apiSystem.getCurrencies()) {
                Assert.assertTrue(uiCurrencies.contains(apiCurrency),
                        "ვალუტა " + apiCurrency + " არ ჩანს UI-ზე " + apiSystem.getName() + "-სთვის!");
            }

            for (String apiCurrency : apiSystem.getCurrencies()) {
                Assert.assertTrue(uiCurrencies.contains(apiCurrency),
                        "ვალუტა " + apiCurrency + " არ ჩანს UI-ზე " + apiSystem.getName() + "-სთვის!");
            }
        }
    }
}