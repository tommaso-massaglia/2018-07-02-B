package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private Map<Integer, Airport> airportIdMap;
	private List<Flight> flightsList;
	private Graph<Airport, DefaultWeightedEdge> grafo;

	public Map<Integer, Airport> getAirportIdMap() {
		return airportIdMap;
	}

	public List<Flight> getFlightsList() {
		return flightsList;
	}

	public void selezionaAeroporti(int x) {

		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		this.flightsList = new ArrayList<>(dao.loadAllFlights());
		this.airportIdMap = new HashMap<>();
		for (Airport a : dao.loadAllAirports()) {
			int count = 0;
			for (Flight f : this.flightsList) {
				if (f.getDestinationAirportId() == a.getId() || f.getOriginAirportId() == a.getId()
						&& f.getDestinationAirportId() != f.getOriginAirportId()) {
					count++;
				}
			}
			if (count >= x) {
				this.airportIdMap.put(a.getId(), a);
			}
			count = 0;
		}

	}

	public void CreaGrafo() {

		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, airportIdMap.values());

		for (SimpleLink sl : dao.loadAllLinks()) {
			if (!grafo.containsEdge(airportIdMap.get(sl.getOrigin_id()), airportIdMap.get(sl.getDestination_id()))
					&& this.airportIdMap.containsKey(sl.getDestination_id())
					&& this.airportIdMap.containsKey(sl.getOrigin_id())) {
				grafo.addEdge(airportIdMap.get(sl.getOrigin_id()), airportIdMap.get(sl.getDestination_id()));
				grafo.setEdgeWeight(
						grafo.getEdge(airportIdMap.get(sl.getOrigin_id()), airportIdMap.get(sl.getDestination_id())),
						sl.getElapsed_time());
			}
		}

	}

	public String getConnessi(int airport_id) {
		String resultstring = "Elenco aeroporti connessi a " + this.airportIdMap.get(airport_id).getAirportName()
				+ ": \n";
		List<SortableAirport> result = new LinkedList<>();
		for (Airport a : Graphs.neighborListOf(grafo, this.airportIdMap.get(airport_id))) {
			result.add(
					new SortableAirport(a, grafo.getEdgeWeight(grafo.getEdge(this.airportIdMap.get(airport_id), a))));
		}
		Collections.sort(result);
		for (SortableAirport sa : result) {
			resultstring += sa.getAirport().getAirportName() + " " + sa.getNumber() + "\n";
		}
		return resultstring;
	}

	// RICORSIONE
	// Dato un tot di ore di volo (primo vincolo ) devo massimizzare l'utilizzo
	// delle ore di volo che mi danno
	// partendo da un certo aeroporto a1 andando in un altro e tornando sempre in
	// quello di partenza, non devo mai visitare
	// più di due volte la stessa città (esclusa a1)

	private Set<Airport> bestDestinazioni;
	private double best_miglia_rimanenti = Double.MAX_VALUE;
	private Set<Airport> parziale;
	private Airport citta_partenza;

	public String getBestUso(int miglia, int id_partenza) {
		this.bestDestinazioni = new LinkedHashSet<Airport>();
		this.parziale = new LinkedHashSet<Airport>();
		this.citta_partenza = this.airportIdMap.get(id_partenza);
		String result = "\nMiglior impiego delle miglia disponibili partendo da "
				+ this.airportIdMap.get(id_partenza).getAirportName() + ": \n\n";
		this.recursive(miglia, parziale);
		int n = 1;
		for (Airport a : this.bestDestinazioni) {
			result += n + " " + a.getAirportName() + "\n";
			n++;
		}
		result += "\nMiglia Avanzate: " + this.best_miglia_rimanenti + "\n";
		return result;
	}

	private void recursive(double miglia_rimanenti, Set<Airport> parziale) {
		// Condizione di terminamento
		if (miglia_rimanenti < 0 || this.best_miglia_rimanenti<=0.004) {
			return;
		}
		// Controllo soluzione ottima
		if (miglia_rimanenti < this.best_miglia_rimanenti) {
			this.best_miglia_rimanenti = miglia_rimanenti;
			this.bestDestinazioni = new LinkedHashSet<>(parziale);
		}
		// Ricorsione
		for (Airport a : Graphs.neighborListOf(grafo, this.citta_partenza)) {
			if (!parziale.contains(a) && !a.equals(this.citta_partenza)) {
				parziale.add(a);
				this.recursive((miglia_rimanenti - this.get_distanza(citta_partenza, a)*2) , parziale);
				parziale.remove(a);
			}
		}

	}

	private double get_distanza(Airport a1, Airport a2) {
		return this.grafo.getEdgeWeight(this.grafo.getEdge(a1, a2));
	}

}
