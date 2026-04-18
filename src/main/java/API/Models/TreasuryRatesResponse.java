package API.Models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//ეს არის მთავარი კლასი, რომელიც მთლიან პასუხს კითხულობს.

public class TreasuryRatesResponse {
    private List<RateCurrency> rates;
    private String updateDate;
}