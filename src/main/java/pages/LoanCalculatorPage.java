package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.example.data.Constants;

public class LoanCalculatorPage {
    private final Page page;

    // ლოკატორების განსაზღვრა
    private final Locator acceptCookiesBtn;
    private final Locator calculatorSection;
    private final Locator amountInput;
    private final Locator periodInput;
    private final Locator monthlyPaymentLabel;

    //კონსტრუქტორი
    public LoanCalculatorPage(Page page) {
        this.page = page;
        // სელექტორების ინიციალიზაცია კონსტრუქტორში
        this.acceptCookiesBtn = page.locator(".primary.state-initial.size-s")
                .filter(new Locator.FilterOptions().setHasText(Constants.COOKIE_ACCEPT_PATTERN));
        this.calculatorSection = page.locator(".tbcx-pw-calculator");
        this.amountInput = page.locator("input[type='number']").first();
        this.periodInput = page.locator("input[type='number']").nth(1);
        this.monthlyPaymentLabel = page.locator(".tbcx-pw-calculated-info__number--new").first();
    }

    // მეთოდები მოქმედებებისთვის
    public void navigate() {
        page.navigate(Constants.LOAN_CALC_URL);
    }

    public void handleCookies() {
        try {
            acceptCookiesBtn.waitFor(new Locator.WaitForOptions().setTimeout(Constants.COOKIE_TIMEOUT));
            if (acceptCookiesBtn.isVisible()) {
                acceptCookiesBtn.click();
                System.out.println("Cookies-ზე დათანხმება წარმატებით შესრულდა.");
            }
        } catch (Exception e) {
            System.out.println("Cookies-ის ფანჯარა არ გამოჩნდა.");
        }
    }

    public void waitForCalculator() {
        calculatorSection.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        calculatorSection.scrollIntoViewIfNeeded();
        page.mouse().wheel(0, Constants.SCROLL_OFFSET_Y);
        System.out.println(" საიტი ჩამოისქროლა კალკულატორამდე.");
    }

    public void fillLoanDetails(String amount, String period) {
        amountInput.fill(amount);
        periodInput.fill(period);
        page.waitForTimeout(Constants.CALC_SHORT_WAIT);
    }
    public void stabilityWait() {
        page.waitForTimeout(Constants.CALC_STABILITY_WAIT);
    }

    public String getMonthlyPayment() {
        monthlyPaymentLabel.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        return monthlyPaymentLabel.innerText();
    }

    public Locator getAmountInput() { return amountInput; }
    public Locator getPeriodInput() { return periodInput; }
    public Locator getMonthlyPaymentLocator() { return monthlyPaymentLabel; }
}
