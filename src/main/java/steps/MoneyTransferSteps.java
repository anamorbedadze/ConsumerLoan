package steps;

import com.microsoft.playwright.Page;
import pages.MoneyTransferPage;


import java.util.List;

public class MoneyTransferSteps {
    private final MoneyTransferPage moneyTransferPage;
//API-ის ტესტირებისთვის შემომაქვს სთეფში ეს ინფო, რათა გაპარსვა სწორად მოხდეს ვალუტის და დასახელების
    public MoneyTransferSteps(Page page) {
        this.moneyTransferPage = new MoneyTransferPage(page);
    }
    public List<String> getVisibleSystemNames () {
        return moneyTransferPage.getAllSystemNames();
    }
    // ვალუტების გასუფთავება და სიად (List) გადაქცევა - მაგ მანიგრამის დასახელება ბექში სხვა არის და ფრონტში სხვა
    public List<String> getCurrenciesForSystem(String systemName) {
        // 1. მოგვაქვს დაუმუშავებელი ტექსტი (მაგ: "currency - EUR/GBP/GEL/USD")
        String rawText = moneyTransferPage.getCurrenciesRawTextForSystem(systemName);

        // 2. ვაშორებთ ზედმეტ სიტყვას და სფეისებს
        // rawText.replace აკეთებს იმას, რომ "currency - "-ს ანაცვლებს სიცარიელით ""
        String cleanText = rawText.replace("currency -", "").trim();
        // ახლა cleanText არის უბრალოდ "EUR/GBP/GEL/USD"

        // 3. ტექსტს ვყოფთ სლეშების ("/") მიხედვით და ვაქცევთ მასივად
        String[] currenciesArray = cleanText.split("/");

        // 4. მასივს ვაქცევთ List-ად, რომ ტესტმა მარტივად შეძლოს შემოწმება
        return List.of(currenciesArray);
    }

    public void openPageAndHandleCookies(String url) {
        moneyTransferPage.navigateTo(url);
        moneyTransferPage.acceptCookiesIfPresent();
        // სისტემების სახელების წამოღება UI-დან
    }

    public void switchToFeeCalculationTab() {
        moneyTransferPage.clickFeeCalculationTab();
    }

    public void fillTransferDetails(String amount, String currency, String country) {
        moneyTransferPage.enterAmount(amount);
        System.out.println("ჩაიწერა თანხა: " + amount);

        moneyTransferPage.selectCurrency(currency);
        System.out.println("ვალუტა " + currency + " არჩეულია");

        moneyTransferPage.selectCountry(country);
        System.out.println("ქვეყანა " + country + " არჩეულია");
    }

    // ლოკატორების გამოტანა Test კლასისთვის ვალიდაციებისთვის
    public MoneyTransferPage getPage() {
        return moneyTransferPage;
    }
}
