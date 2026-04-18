package API.Models;

import java.util.List;

public class MoneyTransferSystem {
    private String mtSystem;
    private String name;
    private String imageUrl;
    private List<String> currencies;

    // Getters
    public String getMtSystem() { return mtSystem; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getCurrencies() { return currencies; }

    // Setters
    public void setMtSystem(String mtSystem) { this.mtSystem = mtSystem; }
    public void setName(String name) { this.name = name; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCurrencies(List<String> currencies) { this.currencies = currencies; }
}
