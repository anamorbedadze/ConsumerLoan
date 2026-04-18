package API.Models;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
//ეს კლასი აღწერს კონკრეტულ ვალუტას (მაგ: EUR ან USD) და ინახავს მის კურსებს (ForwardRateDetail-ების სიას)
public class RateCurrency {
        private String iso;
        private List<TreasuryRatesDetail> forwardRates;
    }

