package it.polito.tdp.extflightdelays.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();

		model.selezionaAeroporti(1000);
		for (Airport a : model.getAirportIdMap().values())
		System.out.println(a);
		model.CreaGrafo();
		System.out.println("");
		System.out.println(model.getConnessi(176));
		System.out.println("");
		System.out.println(model.getBestUso(10000, 176));
	}

}
