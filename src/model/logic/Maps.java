package model.logic;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import com.teamdev.jxmaps.Circle;
import com.teamdev.jxmaps.CircleOptions;
import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.InfoWindow;
import com.teamdev.jxmaps.InfoWindowOptions;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapReadyHandler;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.Polyline;
import com.teamdev.jxmaps.PolylineOptions;
import com.teamdev.jxmaps.swing.MapView;

import model.data_structures.Arco;
import model.data_structures.Graph;
import model.data_structures.TablaHashSondeoLineal;
import model.data_structures.Vertice;

public class Maps<K extends Comparable<K>,V extends Comparable<V>> extends MapView {

	// Objeto Google Maps
	private Map map;
	private Modelo modelo;
	
	//Coordenadas a pintar
	private LatLng[] locations = {new LatLng(4.6285797,-74.0649341), new LatLng(4.608550, -74.076443), new LatLng(4.601363, -74.0661), new LatLng(4.5954979,-74.068708) }; //Coordenadas de los vertices inicio, intermedio y fin.		

	public Maps(Modelo modelo)
	{	
		//Recupero el grafo
		Graph grafo = modelo.darGrafo();
		//Recupero los bertices
		TablaHashSondeoLineal vertices = grafo.vertis;
		
		
		setOnMapReadyHandler( new MapReadyHandler() 
		{
				@Override
				public void onMapReady(MapStatus status)
				{
			         if ( status == MapStatus.MAP_STATUS_OK )
			         {
			        	 map = getMap();

			        	 // Configuracion de localizaciones intermedias del path (circulos)
			        	 CircleOptions middleLocOpt= new CircleOptions(); 
			        	 middleLocOpt.setFillColor("#00FF00");  // color de relleno
			        	 middleLocOpt.setFillOpacity(0.5);
			        	 middleLocOpt.setStrokeWeight(1.0);
			        	 
			        	//Configuracion de la linea del camino
			        	 PolylineOptions pathOpt = new PolylineOptions();
			        	 pathOpt.setStrokeColor("#0000FF");	  // color de linea	
			        	 pathOpt.setStrokeOpacity(1.75);
			        	 pathOpt.setStrokeWeight(1.5);
			        	 pathOpt.setGeodesic(false);

			        	 // Recorrer los vertices
			        	 Iterator vertex = vertices.keys();
			        	 
			        	 int cont = 0;
			        	 while(vertex.hasNext() && cont<500)
			        	 {
			        		 //Recupero el vertice y su información
			        		 K vertiID = (K) vertex.next();
			        		 Vertice vertiActual = (Vertice) vertices.getSet(vertiID);
			        		 Vertices_Bogota_Info infoVertiActu = (Vertices_Bogota_Info) vertiActual.darInfo();
			        		 
			        		 
			        		 double lon = infoVertiActu.darLon();
			        		 double lat = infoVertiActu.darLat();
			        		 
			        		 //Si esta dentro del rango
			        		 if(lon>=-74.094723 && lon<=-74.062707 && lat>=4.597714 && lat<=4.621360)
			        		 {
			        			 //Añado el vertice
			        			 LatLng coor = new LatLng(lat, lon);
			        			 
				        		 Circle middleLoc1 = new Circle(map);
					        	 middleLoc1.setOptions(middleLocOpt);
					        	 middleLoc1.setCenter(coor); 
					        	 middleLoc1.setRadius(20);
					        	 
					        	 //Recupero los vecinos del vertice actual.
					        	 Iterator vecinos = vertiActual.iterator();
					        	 
					        	 //Guardo los vecinos
					        	 ArrayList<LatLng> veci = new ArrayList<LatLng>();
					        	 veci.add(coor);
					        	 
					        	 while(vecinos.hasNext())
					        	 {
					        		 //Recorro los vecinos
					        		 Arco veciActual = (Arco) vecinos.next();
					        		 
					        		 //Recupero el vertice del vecino y su info.
					        		 Vertice veciActu = veciActual.darFinal();
					        		 Vertices_Bogota_Info infoVeciActu = (Vertices_Bogota_Info) veciActu.darInfo();
					        		 
					        		 lon = infoVeciActu.darLon();
					        		 lat = infoVeciActu.darLat();
					        		 
					        		 //Si esta en el rango lo añado.
					        		 if(lon>=-74.094723 && lon<=-74.062707 && lat>=4.597714 && lat<=4.621360)
					        		 {
					        			 coor = new LatLng(lat, lon);
					        			 veci.add(coor);
					        		 }
					        	 }
					        	 
					        	 
					        	 LatLng[] locat = new LatLng[veci.size()];
					        	 
					        	 for(int i=0;i<veci.size();i++)
					        	 {
					        		 locat[i] = veci.get(i);
					        	 }
					        	 
					        	// Linea del camino
					        	 Polyline path = new Polyline(map); 														
					        	 path.setOptions(pathOpt); 
					        	 path.setPath(locat);
					        	 cont++;
			        		 }
			        	 }
			        	 initMap( map );
			         }
				}

		} );
		
				
	}
	
	public void initMap(Map map)
	{
		MapOptions mapOptions = new MapOptions();
		MapTypeControlOptions controlOptions = new MapTypeControlOptions();
		controlOptions.setPosition(ControlPosition.BOTTOM_LEFT);
		mapOptions.setMapTypeControlOptions(controlOptions);

		map.setOptions(mapOptions);
        map.setCenter(locations[2]);
		map.setZoom(14.0);
		
	}
	
	public void initFrame(String titulo)
	{
		JFrame frame = new JFrame(titulo);
		frame.setSize(800, 800);
		frame.add(this, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
