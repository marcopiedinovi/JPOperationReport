package operationreport;

public enum Currency {
	SGP(0.50),
	AED(0.22),
	SAR(0.27),
	EUR(1.10),
	CAD(0.75);
	
	public final double rate;
	
	Currency( double rate) {
		this.rate = rate;
	}
}
