package it.polito.tdp.extflightdelays.model;

public class SimpleLink {

	private int origin_id;
	private int destination_id;
	private double elapsed_time;

	public SimpleLink(int origin_id, int destination_id, double elapsed_time) {
		this.origin_id = origin_id;
		this.destination_id = destination_id;
		this.elapsed_time = elapsed_time;
	}

	public double getElapsed_time() {
		return elapsed_time;
	}

	public int getOrigin_id() {
		return origin_id;
	}

	public int getDestination_id() {
		return destination_id;
	}

}
