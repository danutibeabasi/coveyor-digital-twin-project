package fr.emse.fayol.maqit.digital_twin;

public class Stock  {
	private int stockValue;
	private int stockMax;
	
	public Stock(int value, int max) {
		stockValue = value; //Math.max((int) (value * Math.random()), max);
		stockMax = max;
	}

	public boolean isEmpty(){
		return stockValue == 0;
	}

	public int getStockValue() {
		return stockValue;
	}
	
	public boolean isStockStateHigh() {
		if (stockValue > 5) return true;
		return false;
	}

	public boolean destock() {
		if (stockValue > 1) {
			stockValue--;
			return true;
		}
		return false;
	}
		
	public void updateStock(int value) {
		stockValue = Math.max(value + stockValue, stockMax);
	}
}
