package org.example.data;

import javax.swing.plaf.PanelUI;
import java.util.regex.Pattern;

// --- URLs ---
public class Constants {
    public static final String BASE_URL = "https://tbcbank.ge/ka";
    public static final String BASE_URL_ENG = "https://tbcbank.ge/en";

    public static final String CONS_LOAN_URL = "https://tbcbank.ge/ka/loans/consumer-loan";
    public static final String TBC_CREDIT_URL= "https://tbccredit.ge";
    public static final String LOAN_CALC_URL = "https://tbcbank.ge/ka/loans/consumer-loan/digital";
    public static final String MONEY_TRANSFER_URL ="https://tbcbank.ge/en/other-products/money-transfers";
    public static final String OFFER_DET_PAGE_URL = "https://tbcbank.ge/ka/offers/all-offers/1o8WOB94RBAZVUFk2uqi5l/extra-offer";
    public static final String OFFER_LIST_URL = "https://tbcbank.ge/ka/offers/all-offers";
    public static final String LOCATION_URL = "https://tbcbank.ge/ka/atms&branches";
    public static final String GEO_LANG = "ქარ";
    public static final String ENG = "Eng";

// ---URL Patterns Assert that(ვალიდაციისთვის, დავწმუნდეთ რომ საიტი აღნიშნულ სიტყვებს შეიცავს) ---
    public static final Pattern LOCATIONS_URL_PATTERN = Pattern.compile(".*atms&branches.*", Pattern.CASE_INSENSITIVE);
    public static final Pattern TBC_CREDIT_URL_PATTERN = Pattern.compile("tbccredit.ge", Pattern.CASE_INSENSITIVE);

//--- Test Data (Loan Calculator) ---
    public static final String INITIAL_LOAN_AMOUNT = "3000";
    public static final String INITIAL_LOAN_PERIOD = "48";
    public static final String UPDATED_LOAN_AMOUNT = "777";
    public static final String UPDATED_LOAN_PERIOD = "33";

// --- Timeouts (in milliseconds) ---
                    // --- Consumer Loan Page Timeouts --
    public static final double COOKIE_TIMEOUT = 15000;
    public static final int TBC_CREDIT_COOKIES = 5000;
    public static final double LOAD_TIMEOUT = 20000;
    public static final int CALC_STABILITY_WAIT = 10000; // 10 წამიანი ლოდინი
    public static final int CALC_SHORT_WAIT = 1000;
    public static final double MAP_LOAD_TIMEOUT = 15000;
    public static final int SHORT_WAIT = 1000;
    public static final int MEDIUM_WAIT = 2000;
    public static final int SCROLL_WAIT = 1500;
    public static final int SLOW_MO_DEFAULT = 500;
    public static final double SCROLL_OFFSET_Y = 300;

// ---  Mobile Viewport Settings for Locations---
    public static final int MOB_WIDTH = 390;
    public static final int MOB_HEIGHT = 844;

// --- Mobile device for Locators ----
public static final String IPHONE_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Mobile/15E148 Safari/604.1";

// ----- ქუქიები (Cookies) ----
    public static final Pattern COOKIE_ACCEPT_PATTERN = Pattern.compile(" თანხმობა | Accept All | Accept ", Pattern.CASE_INSENSITIVE);

// --- Menu & Button Texts ---
    public static final String PERSONAL_MENU = " ჩემთვის ";
    public static final String CONSUMER_LOAN_TEXT = "სამომხმარებლო";
    public static final String CONSUMER_LOAN_TERMS = "პირობები";
    public static final String CONSUMER_LOAN_APPLY= "სესხის მოთხოვნა";
    public static final String CONSUMER_INTEREST_RATE= "საპროცენტო განაკვეთი";

    public static final String CONSUMER_HERO_TITLE= "h1";
    public static final String ADDRESS_MENU_TEXT = "მისამართები";
    public static final String LOCATIONS_HEADING = "ფილიალი, ბანკომატი და თანხის მიმღები";
    public static final String ATM_TAB_NAME = "ბანკომატები";
    public static final String BRANCH_TAB_NAME = "ფილიალები";
    public static final String ATM_CARD_TEXT = "ATM";
    public static final Pattern CALC_DIGIT_PATTERN = Pattern.compile("[0-9]");
    public static final String MONEY_TRANSFER_NAME = " Ria | MoneyGram | IntelExpress | WesternUnion ";
    public static final String CLEAR_BUTTON_GEO = "გასუფთავება";
    public static final String CLEAR_BUTTON_ENG = "Clear";

// ---WORKING HOURS - სამუშაო საათები-----
    public static final String WORK_HOURS_WEEKLY = " ორშაბათი-პარასკევი: 10:00-18:00 ";
    public static final String WORK_HOURS_SAT = " შაბათი: 10:00-14:00 ";

// ----OFFERS -----
    public static final Pattern DISCOUNT_BADGE_PATTERN = Pattern.compile(".*დაიბრუნე 30%.*");
    public static final Pattern MASTERCARD_FILTER_PATTERN = Pattern.compile(" მასტერქარდი | MasterCard", Pattern.CASE_INSENSITIVE);
    public static final Pattern PARTNER_OFFERS_PATTERN = Pattern.compile(" პარტნიორების შეთავაზება | Partner Offers", Pattern.CASE_INSENSITIVE);
    public static final Pattern NOT_FOUND_PATTERN = Pattern.compile(" შეთავაზებები არ მოიძებნა | Offer wasn't found ", Pattern.CASE_INSENSITIVE);

}

