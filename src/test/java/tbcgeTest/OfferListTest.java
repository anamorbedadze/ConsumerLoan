package tbcgeTest;

import basetest.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import steps.OfferListSteps;
// სხვა იმპორტები...

public class OfferListTest extends BaseTest {

    @Test
    public void validateEmptyOffersListWithFilters() {
        OfferListSteps offerListSteps = new OfferListSteps(page);

        // ვასრულებთ მოქმედებებს
        offerListSteps
                .navigateToOffersPage()
                .handleCookies()
                .verifyPageElementsVisible()
                .selectFiltersForEmptyResult()
                .validateEmptyState()
                .clearFilters();

        // ვიღებთ შედეგს Step-იდან
        int cardsCount = offerListSteps.getVisibleOfferCardsCount();

        // ვაკეთებთ Assert-ს ტესტში (Best Practice)
        Assert.assertTrue(cardsCount > 0, "შეცდომა: ფილტრების გასუფთავების შემდეგ სია ცარიელია.");
        System.out.println("გასუფთავების შემდეგ გამოჩნდა " + cardsCount + " შეთავაზება. ტესტი წარმატებულია!");
    }
}