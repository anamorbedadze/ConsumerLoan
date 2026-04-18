package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.example.data.Constants;

import java.util.List;
import java.util.regex.Pattern;

public class MoneyTransferPage {
    private final Page page;

    // ლოკატორები
    private final Locator acceptCookiesBtn;
    private final Locator mainTitle;
    private final Locator feeCalcTab;
    private final Locator amountInput;
    private final Locator currencyDropdown;
    private final Locator countryDropdown;
    private final Locator resultSection;
    private final Locator anyError;

    // 🟢 ახალი მეთოდი API სისტემების სახელებისოსთვის
    public List<String> getAllSystemNames() {
            return page.locator(".tbcx-pw-card__logo-and-text-info").allTextContents();
    }
    // კონკრეტული სისტემის ვალუტების ტექსტის წამოღება (მაგ: "currency - EUR/GBP/GEL/USD")
    public String getCurrenciesRawTextForSystem(String systemName) {
        // 1. ვპოულობთ ყველა ბარათს (tbcx-pw-card)
        // 2. ვფილტრავთ იმ ბარათს, რომელშიც წერია ჩვენი სისტემის სახელი (მაგ. IntelExpress)
        // 3. ამ კონკრეტულ ბარათში ვპოულობთ კაპშონს (tbcx-pw-card__caption) და ვიღებთ მის ტექსტს
        return page.locator("tbcx-pw-card")
                .filter(new Locator.FilterOptions().setHasText(systemName))
                .locator(".tbcx-pw-card__caption")
                .innerText();
    }

    public MoneyTransferPage(Page page) {
        this.page = page;

        // ინიციალიზაცია
        this.acceptCookiesBtn = page.locator(".primary.state-initial.size-s")
                .filter(new Locator.FilterOptions().setHasText(Constants.COOKIE_ACCEPT_PATTERN));
        this.mainTitle = page.getByText("Quick money transfers").first();
        this.feeCalcTab = page.getByText("Remittance Fee Calculation").first();

        // 💡 შენიშვნა შენს პრობლემაზე: თუ ორი ერთნაირი input-ია, ჯობს მოვძებნოთ ის, რომელიც იმ მომენტში ხილვადია (visible).
        this.amountInput = page.locator(".input-with-label input:visible");
        this.currencyDropdown = page.locator("tbcx-dropdown-selector").first();
        this.countryDropdown = page.locator("tbcx-dropdown-selector").nth(1);
        this.resultSection = page.locator(".tbcx-pw-money-transfer-fee-calculator__cards").first();
        this.anyError = page.locator("[class*='error'], [class*='Invalid']").first();
    }

    // --- Action მეთოდები ---

    public void navigateTo(String url) {
        page.navigate(Constants.MONEY_TRANSFER_URL);
    }

    public void acceptCookiesIfPresent() {
        try {
            acceptCookiesBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            if (acceptCookiesBtn.isVisible()) {
                acceptCookiesBtn.click();
            }
        } catch (Exception e) {
            System.out.println("Cookies ფანჯარა არ გამოჩენილა.");
        }
    }

    public Locator getMainTitle() {
        return mainTitle;
    }

    public void clickFeeCalculationTab() {
        feeCalcTab.scrollIntoViewIfNeeded();
        feeCalcTab.click();
    }

    public void enterAmount(String amount) {
        amountInput.fill(amount);
    }

    public void selectCurrency(String currencyCode) {
        currencyDropdown.click();
        page.locator(".tbcx-dropdown-popover-item__title")
                .filter(new Locator.FilterOptions().setHasText(currencyCode))
                .click();
    }

    public void selectCountry(String countryName) {
        countryDropdown.click();
        Locator countryOption = page.locator("div.tbcx-dropdown-popover-item__title:has-text('" + countryName + "')");
        countryOption.scrollIntoViewIfNeeded();

        // 💡 თუ რამე ეფარება ვისტს ან კლიკი ვერ ხერხდება, ვიყენებთ force: true-ს
        countryOption.click(new Locator.ClickOptions().setForce(true));
    }

    public Locator getResultSection() {
        return resultSection;
    }

    public Locator getAnyError() {
        return anyError;
    }
}