package API.Models;

import lombok.Getter;
import lombok.Setter;

// ეს კლასი აღწერს კონკრეტულ კურსს (მაგ: 1 კვირიანს)
@Getter
@Setter
public class TreasuryRatesDetail {
    private String iso1;
    private String iso2;
    private String period;
    private int day;
    private double bidForwardPoint;
    private double bidForwardInterest;
    private double bidForwardRate;
    private double askForwardPoint;
    private double askForwardInterest;
    private double askForwardRate;

}