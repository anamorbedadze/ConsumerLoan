package API.Client;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import lombok.RequiredArgsConstructor; // 🟢 ახალი იმპორტი
import org.example.data.Constants;

@RequiredArgsConstructor // 🟢 ავტომატურად ქმნის კონსტრუქტორს ყველა "final" ცვლადისთვის
public class TreasuryClient {
    private final APIRequestContext request;

    public APIResponse getForwardRates() {
        RequestOptions options = RequestOptions.create()
                .setHeader("accept", Constants.ACCEPT_JSON)
                .setHeader("accept-language", Constants.ACCEPT_LANGUAGE_KA)
                .setHeader("origin", "https://tbcbank.ge");

        return request.get(Constants.TREASURY_RATES_ENDPOINT, options);
    }
}
