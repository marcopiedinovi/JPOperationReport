package operationreport;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Instruction {
	
	private StringBuffer buffInstruction;	// simulate sending by string
	
	private String entity;				// Financial name's handled shares
	private String operationType;		// Buy or Sell
	private double agreedFx;			// Foreign exchange rate to USD
	private Currency currency;			// Currency
	private LocalDate insDate;	// Instruction sending date
	private LocalDate setDate;	// Wished settlement date
	private int unitQty;				// Handled units
	private double unitPrice;			// Price per unit

	private Locale locale = new Locale("en", "US");
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", locale);

	//to create incoming messages
	public Instruction() {
		
		setEntity( Name.values()[ Helper.getRandom( 0, Name.values().length-1)].toString());
		
		int type = Helper.getRandom(0, 1);
		setOperationType( (type == 0 ? "B" : "S"));
		setCurrency( Currency.values()[ Helper.getRandom( 0, Currency.values().length-1)]);
		
		//on sell random plus from 2% to 10%, on buy the same but 'minus'
		double percent = getCurrency().rate * (double) Helper.getRandom( 2, 10) / 100.0;
		if( getOperationType() == "B") percent = -percent;
		double sellRate = getCurrency().rate + percent;
		setAgreedFx( sellRate);
		
		//instruction date random from 1 to 30 days from today + skip weekend
		LocalDate instrDt = LocalDate.now();
		instrDt = instrDt.plusDays( Helper.getRandom( 1, 30));
		//instrDt = checkWeekend( instrDt);
		setInsDate( instrDt);

		//settlement date random from 1 to 3 days after instruction date + skip weekend
		LocalDate settleDt = getInsDate();
		settleDt = settleDt.plusDays( Helper.getRandom( 1, 3));
		//settleDt = checkWeekend( settleDt);
		setSetDate( settleDt);
		
		setUnitQty( Helper.getRandom(1, 10) * 50);
		double price = (Helper.getRandom(1, 10) * 500 + Helper.getRandom(1, 20) * 5) / 100.0;
		setUnitPrice(price);
		
		appendChunks();
	}
	
	private void appendChunks(){
		appendChunk( getEntity()+"-");
		appendChunk( getOperationType()+"-");
		appendChunk( String.format(locale, "%.2f-", getAgreedFx()));
		appendChunk( getCurrency().toString() + "-");
		
		appendChunk( getInsDate().format(formatter) + "-");
		appendChunk( getSetDate().format(formatter) + "-");
		appendChunk(Integer.toString(getUnitQty()) + "-");
		appendChunk(String.format(locale, "%.2f", getUnitPrice()));

	}
	//to handle string messages
	public Instruction(String instruction) {
		
		String[] parts = instruction.split("-");

		setEntity(parts[0]);
		setOperationType(parts[1]);
		setCurrency( Currency.valueOf( parts[3]));
		
		try {
			setInsDate( LocalDate.parse(  parts[4], formatter));
			
			setSetDate( LocalDate.parse(  parts[5], formatter));
			setSetDate( checkWeekend( getSetDate()));

		} catch (DateTimeParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			setAgreedFx(Double.parseDouble(parts[2]));
			setUnitQty(Integer.parseInt(parts[6]));
			setUnitPrice(Double.parseDouble(parts[7]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		appendChunks();
	}
	
	LocalDate checkWeekend( LocalDate date) {
		
		if(getCurrency().equals(Currency.AED) || getCurrency().equals(Currency.SAR)) {
			if(date.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
				date = date.plusDays(2);
			} else if( date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
				date = date.plusDays(1);
			}
		} else {
			if(date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
				date = date.plusDays(2);
			} else if( date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
				date = date.plusDays(1);
			}
		}
		
		return date;
	}

	
	public String getEntity() {
		return entity;
	}
	public String getOperationType() {
		return operationType;
	}
	public double getAgreedFx() {
		return agreedFx;
	}
	public Currency getCurrency() {
		return currency;
	}
	public LocalDate getInsDate() {
		return insDate;
	}
	public LocalDate getSetDate() {
		return setDate;
	}
	public int getUnitQty() {
		return unitQty;
	}
	public double getUnitPrice() {
		return unitPrice;
	}
	
	public Double getInstrValue() {
		return new Double( unitQty * unitPrice * agreedFx);
	}
	
	protected void setEntity(String entity) {
		this.entity = entity;
	}
	protected void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	protected void setAgreedFx(double agreedFx) {
		this.agreedFx = agreedFx;
	}
	protected void setCurrency(Currency currency) {
		this.currency = currency;
	}
	protected void setInsDate(LocalDate instructionDate) {
		this.insDate = instructionDate;
	}
	protected void setSetDate(LocalDate settlementDate) {
		this.setDate = settlementDate;
	}
	protected void setUnitQty(int unitQty) {
		this.unitQty = unitQty;
	}
	protected void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	protected void appendChunk(String chunk) {
		if(buffInstruction == null) {
			buffInstruction = new StringBuffer();
		}
		buffInstruction.append(chunk);
	}
	
	protected String getInstructionAsString() {
		return buffInstruction.toString();
	};

}
