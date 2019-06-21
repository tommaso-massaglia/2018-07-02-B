package it.polito.tdp.extflightdelays.model;

public class SortableAirport implements Comparable<SortableAirport> {

	private Airport airport;
	private double number;

	public SortableAirport(Airport airport, double number) {
		this.airport = airport;
		this.number = number;
	}

	public Airport getAirport() {
		return airport;
	}

	public void setAirport(Airport airport) {
		this.airport = airport;
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}

	@Override
	public int compareTo(SortableAirport arg0) {
		return (int) (this.getNumber() - arg0.getNumber());
	}

}
