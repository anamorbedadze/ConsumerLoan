package API.Client;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import org.example.data.Constants;

public class MoneyTransferClient {
    private final APIRequestContext request;

    public MoneyTransferClient(APIRequestContext request) {
        this.request = request;
    }

    public APIResponse getTransferSystems() {
        RequestOptions options = RequestOptions.create()
                .setHeader("accept", Constants.ACCEPT_JSON)
                .setHeader("accept-language", Constants.ACCEPT_LANGUAGE_KA)
                .setHeader("origin", "https://tbcbank.ge");

        return request.get(Constants.MONEY_TRANSFER_SYSTEMS_ENDPOINT, options);
    }
}
