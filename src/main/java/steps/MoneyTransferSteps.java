package steps;

import com.microsoft.playwright.Page;
import pages.MoneyTransferPage;

public class MoneyTransferSteps {
    private final MoneyTransferPage moneyTransferPage;

    public MoneyTransferSteps(Page page) {
        this.moneyTransferPage = new MoneyTransferPage(page);
    }

    public void openPageAndHandleCookies(String url) {
        moneyTransferPage.navigateTo(url);
        moneyTransferPage.acceptCookiesIfPresent();
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
