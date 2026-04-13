package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Getter;
import org.example.data.Constants;
import java.util.regex.Pattern;

@Getter
public class ConsumerLoanPage {
// 3. გეთერები ძირითადი გვერდისთვის
    // 1. ლოკატორების გაწერა
    private final Locator acceptCookiesBtn;
    private final Locator personalMenu;
    private final Locator consumerLoanLink;
    private final Locator conditionsBtn;
    private final Locator applyBtn;


// 2. ინიციალიზაცია კონსტრუქტორში
    //რადგან ვიყენებ .filter or .first საჭიროა კონსტრუქტორში გამოვიტანო და არა გეთერში, რადგან გამოძახება მხოლოდ ერთხელ მოხდეს
    public ConsumerLoanPage(Page page) {
        this.acceptCookiesBtn = page.locator(".primary.state-initial.size-s")
                .filter(new Locator.FilterOptions().setHasText(Constants.COOKIE_ACCEPT_PATTERN));

       this.personalMenu = page.locator(".tbcx-pw-navigation-item__link")
                .filter(new Locator.FilterOptions().setHasText(Constants.PERSONAL_MENU))
                .first();
       this.consumerLoanLink = page.getByText(Constants.CONSUMER_LOAN_TEXT).first();

        //2 (1). დამატებული ლოკატორები ტესტის მიხედვით - კონკრეტულად ამ ფეიჯზე tbccredit.ge გამოიყენება ეს ქუქი
        this.conditionsBtn = page.locator("button.primary.state-initial")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(Constants.CONSUMER_LOAN_TERMS)))
                .first();

        this.applyBtn = page.locator("a, button")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(Constants.CONSUMER_LOAN_APPLY)))
                .first();
    }
    ///-----------Click, Scroll, Evaluate , scrollIntoViewIfNeeded ცალკე მეთოდებად უნდა გაიტანო ----
    public void openConditions() {
        this.conditionsBtn.scrollIntoViewIfNeeded();
        this.conditionsBtn.evaluate("el => el.click()");
    }
    public void hoverPersonalMenu() {
        this.personalMenu.hover();
    }
    public void startApplyProcess() {
        this.applyBtn.scrollIntoViewIfNeeded(); //  სტაბილურობისთვის
        this.applyBtn.click();
    }

    public void acceptCookiesIfVisible() {
        try {
            // ველოდებით მაქსიმუმ 5 წამს, რომ ღილაკი გამოჩნდეს
            this.acceptCookiesBtn.waitFor(new Locator.WaitForOptions().setTimeout(Constants.COOKIE_TIMEOUT));

            if (this.acceptCookiesBtn.isVisible()) {
                this.acceptCookiesBtn.click();
                System.out.println("Cookies-ზე დათანხმება წარმატებით შესრულდა.");
            }
        } catch (Exception e) {
            // თუ 5 წამში არ გამოჩნდა, უბრალოდ ვაგრძელებთ - ტესტი არ ფეილდება
            System.out.println("Cookies-ის ფანჯარა არ ამოხტა ან სხვა შეცდომაა.");
        }
    }

    // 3 (1). მეთოდები ახალი ფანჯრისთვის (Popup) - დამატებითი ლოკატორების გაგეთერება
    // რადგან ახალი ფანჯარა (newPage) სხვა ობიექტია, მას პარამეტრად გადავაწვდით
    public void acceptTbcCreditCookies(Page popup) {
        // ლოკატორს ვეძებთ პირდაპირ გადმოწოდებულ popup ობიექტში
        Locator creditCookiesBtn = popup.locator("#acceptAllCookies");
        try {
            creditCookiesBtn.waitFor(new Locator.WaitForOptions().setTimeout(Constants.TBC_CREDIT_COOKIES));
            if (creditCookiesBtn.isVisible()) {
                creditCookiesBtn.click();
                System.out.println("TBC Credit-ის ქუქიებზე დათანხმება შესრულდა.");
                creditCookiesBtn.waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN));
                System.out.println("პროგრამულად დადასტურდა, რომ ქუქის ფანჯარა გაქრა.");
            }
        } catch (Exception e) {
            System.out.println("TBC Credit-ზე ქუქიების ფანჯარა არ ამოხტა.");
        }
}
    public void closeOfferPopupIfVisible(Page popup) {
            Locator closePopupBtn = popup.locator(".snrs-modal-btn-close");
            try {
        // ვაძლევთ 3 წამს, რადგან პოპ-აპები ზოგჯერ საიტის ჩატვირთვიდან 1-2 წამში ხტება
        closePopupBtn.waitFor(new Locator.WaitForOptions().setTimeout(Constants.LOAD_TIMEOUT));

        if (closePopupBtn.isVisible()) {
            closePopupBtn.click();
            System.out.println("საინფორმაციო ოფერის პოპ-აპი გამოჩნდა და დაიხურა.");
        }
    } catch (Exception e) {
        // თუ 3 წამში არ გამოჩნდა, ესე იგი ოფერი არ არის და უბრალოდ ვაგრძელებთ გზას
        System.out.println("ოფერის პოპ-აპი არ გამოსულა, ვაგრძელებთ სტანდარტულ ფლოუს.");
    }
}

    public Locator getHeroTitle(Page newPage) {
        return newPage.locator(Constants.CONSUMER_HERO_TITLE);
    }
}