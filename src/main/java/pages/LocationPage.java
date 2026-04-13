package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.example.data.Constants;

public class LocationPage {
    private final Page page;

    // ლოკატორების გამოცხადება
    private final Locator cookieAcceptBtn;
    private final Locator burgerMenu;
    private final Locator addressesBtn;
    private final Locator pageHeading;
    private final Locator mapContainer;
    private final Locator atmsTab;
    private final Locator branchesTab;
    private final Locator firstCardList;
    private final Locator firstAtmAddress;

    public LocationPage(Page page) {
        this.page = page;
        this.cookieAcceptBtn = page.locator("button:visible")
                .filter(new Locator.FilterOptions().setHasText(Constants.COOKIE_ACCEPT_PATTERN))
                .first();
        this.burgerMenu = page.locator(".tbcx-pw-hamburger-menu__button.icon-flat.size-s.state-initial, button:has(.icon-burger)").first();
        this.addressesBtn = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Constants.ADDRESS_MENU_TEXT)).last();
        this.pageHeading = page.getByText(Constants.LOCATIONS_HEADING).first();
        this.mapContainer = page.locator(".tbcx-pw-atm-branches-section-map").first();
        this.atmsTab = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Constants.ATM_TAB_NAME));
        this.branchesTab = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Constants.BRANCH_TAB_NAME));

        // პირველი ქარდი და მისი მისამართი
        this.firstCardList = page.locator(".tbcx-pw-atm-branches-section__list").first();
        this.firstAtmAddress = firstCardList.locator(".tbcx-pw-atm-branches-section__list-item-title.tbcx-pw-title").first();
    }

    // --- მოქმედებების მეთოდები (Actions) ---

    public void acceptCookies() {
        try {
            cookieAcceptBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            if (cookieAcceptBtn.isVisible()) {
                cookieAcceptBtn.click();
                page.waitForTimeout(1000);
            }
        } catch (Exception e) {
            System.out.println("ქუქიების ფანჯარა არ გამოჩნდა.");
        }
    }

    public void openAddressesFromMenu() {
        burgerMenu.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        burgerMenu.click();
        addressesBtn.evaluate("el => el.scrollIntoView({ behavior: 'smooth', block: 'center' })");
        page.waitForTimeout(2000);
        addressesBtn.click();
    }

    public void clickAtmsTab() {
        atmsTab.click();
        page.waitForTimeout(1000);
    }

    public void clickBranchesTab() {
        branchesTab.click();
        page.waitForTimeout(1000);
    }

    public void scrollToFirstCard() {
        firstCardList.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        firstCardList.evaluate("el => el.scrollIntoView({ behavior: 'smooth', block: 'center' })");
        page.waitForTimeout(1500);
    }

    public void scrollToTop() {
        page.evaluate("window.scrollTo({ top: 0, behavior: 'smooth' })");
        page.waitForTimeout(1500);
    }

    // --- გეტერები ტესტში ასერტებისთვის (Getters) ---
    // რადგან Assert-ები ტესტში უნდა იყოს, ტესტს სჭირდება წვდომა ლოკატორებზე

    public Locator getPageHeading() { return pageHeading; }
    public Locator getMapContainer() { return mapContainer; }
    public Locator getFirstCardList() { return firstCardList; }
    public Locator getFirstAtmAddress() { return firstAtmAddress; }
}
