package tbcgeTest;

import API.Client.TreasuryClient;
import API.Models.RateCurrency;
import API.Models.TreasuryRatesDetail;
import API.Models.TreasuryRatesResponse;
import basetest.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.example.data.Constants;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.regex.Pattern;

public class TreasuryRatesApiTest extends BaseTest {

    @Test
    public void verifyTreasuryRatesApiAndUi() throws Exception {
        TreasuryClient treasuryClient = new TreasuryClient(request);
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. API ნაწილი (მუშაობს)
        APIResponse response = treasuryClient.getForwardRates();
        TreasuryRatesResponse ratesResponse = objectMapper.readValue(response.text(), TreasuryRatesResponse.class);

        RateCurrency usdRate = ratesResponse.getRates().stream()
                .filter(r -> r.getIso().equals("USD"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("USD ვერ მოიძებნა!"));

        // 2. UI ნაწილი
        page.navigate(Constants.Treasury_Rate_URL);
        page.waitForLoadState(LoadState.LOAD); // NETWORKIDLE-ის ნაცვლად, რომ არ გაიჭედოს

        if (page.locator("text=დათანხმება").isVisible()) {
            page.click("text=დათანხმება");
        }

        // 🟢 ნაბიჯი 1: ზოგადი ჩამოსქროლვა (Scroll Down)
        // ეს ბრძანება ჩამოწევს გვერდს 1200 პიქსელით, რომ ცხრილები "გააქტიურდეს"
        page.evaluate("window.scrollBy(0, 1200)");
        page.waitForTimeout(1000); // პატარა პაუზა, რომ სქროლმა მოასწროს დასრულება

        System.out.println("გვერდი ჩამოისქროლა, ვეძებ USD/GEL ცხრილს...");

        // 🟢 ნაბიჯი 2: ახლა უკვე ვეძებთ კონკრეტულად USD/GEL ცხრილს
        Locator usdContainer = page.locator(".business-treasury-product-table")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(".*USD/GEL.*")));

        // დამატებითი დაზღვევა - ჩავასქროლოთ უშუალოდ ამ ცხრილთან
        usdContainer.scrollIntoViewIfNeeded();
        usdContainer.waitFor();

        for (TreasuryRatesDetail apiDetail : usdRate.getForwardRates()) {
            String period = apiDetail.getPeriod().trim();

            //Locator row = usdContainer.locator(".tbcx-pw-table-row")
            Locator row = usdContainer.getByText(period).first();
                   // .filter(new Locator.FilterOptions().setHasText(Pattern.compile(".*" + Pattern.quote(period) + ".*")));

            try {
                // უშუალოდ სტრიქონთან ჩასქროლვა და დალოდება
                row.scrollIntoViewIfNeeded();
                row.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                String fullRowText = row.locator("..").innerText(); //„ავიდეთ ერთი დონით ზემოთ, მშობელ ელემენტზე“.- ჯასთ უჯრას პოულობს და მასში ჩაწერილ ინფოს არა
                System.out.println("მთლიანი სტრიქონის ტექსტია: " + fullRowText);
                System.out.println("✅ ნაპოვნია: " + period);
                Assert.assertTrue(fullRowText.contains(period), "სტრიქონში პერიოდი ვერ მოიძებნა!");
            } catch (Exception e) {
                System.out.println("❌ ვერ მოიძებნა პერიოდი: " + period);
                // თუ დაფეილდა, დაგვიბეჭდოს რას ხედავს იმ მომენტში
                System.out.println("ცხრილის შიგთავსი: " + usdContainer.innerText());
                Assert.fail("UI-ზე პერიოდი [" + period + "] ვერ მოიძებნა!");

            }
        }
    }
}