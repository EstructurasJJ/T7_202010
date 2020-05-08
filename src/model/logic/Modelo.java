package model.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import model.data_structures.Arco;
import model.data_structures.Graph;
import model.data_structures.ListaEnlazadaQueue;
import model.data_structures.Node;
import model.data_structures.TablaHashSondeoLineal;
import model.data_structures.Vertice;


public class Modelo 
{


	//Atributos necesarios para la lectura desde txt
	private Vertice vertiAgregar;
	private Vertices_Bogota_Info infoVertice;
	private Haversine costoHaversiano;

	//El grafo, su nombre lo dice todo
	private Graph cositaBienHecha = new Graph(1);

	//Atributos encesarios para la carga del JSON de las estaciones de polic�a
	private String parteDelaEstacion;
	private EstPol porAgregar; 
	private ListaEnlazadaQueue estaciones;
	private boolean coordenadas=false;

	//Atributos necesarios para la carga del JSON del grafo
	private String parteDelVerti;
	private Vertices_Bogota_Info infoPorAgregar;
	private Vertice vertiPorAgregar;
	private ArrayList<Vertice<Integer, Vertices_Bogota_Info>> listaVertices=new ArrayList<Vertice<Integer, Vertices_Bogota_Info>>();
	private boolean coordenadasGrafo=false;

	private int origenPorIngresar, destinoPorIngresar;
	private double costoPorAgregar;

	///////////////////////////////////////////////////////Constructor

	public Modelo()
	{
		parteDelaEstacion = "";
		parteDelVerti="";
	}

	//////////////////////////////////////////////////////Dar

	public Graph<Integer, Vertices_Bogota_Info> darGrafo()
	{
		return cositaBienHecha;
	}

	/////////////////////////////////////////////////////////////////////////////////LECTURA de TXT 

	public void leerTxtVertix(String archivo) throws FileNotFoundException, IOException 
	{
		FileReader f = new FileReader(archivo);
		BufferedReader b = new BufferedReader(f);
		String cadena = b.readLine();

		while( cadena != null) 
		{	    	  
			infoVertice = new Vertices_Bogota_Info(0, 0);
			vertiAgregar = new Vertice(0, infoVertice);

			String[] partes = cadena.split(",");

			int ID = Integer.parseInt(partes[0]);
			double Longitud = Double.parseDouble(partes[1]);
			double Latitud = Double.parseDouble(partes[2]);

			infoVertice.asignarLon(Longitud);
			infoVertice.asignarLat(Latitud);
			vertiAgregar.asignarId(ID);

			vertiAgregar.cambiarInfo(infoVertice);

			cositaBienHecha.addVertex(ID, vertiAgregar);
			cadena = b.readLine();

		}

		int totalVertices = cositaBienHecha.darV()-1;
		System.out.println("Total de Vertices: " + totalVertices + "\n--------------");
		b.close(); 
	}

	public void leerTxtArc(String archivo) throws FileNotFoundException, IOException 
	{
		costoHaversiano = new Haversine();
		double costo = 0.0;

		FileReader f = new FileReader(archivo);
		BufferedReader b = new BufferedReader(f);
		String cadena = b.readLine();

		while( cadena != null) 
		{
			if(!cadena.contains("#"))
			{
				String[] partes = cadena.split(" ");
				int origen = Integer.parseInt(partes[0]);

				for (int i = 1; i< partes.length; i++)
				{
					int vecino = Integer.parseInt(partes[i]);

					if(cositaBienHecha.existeVertice(origen) && cositaBienHecha.existeVertice(vecino))
					{
						Vertices_Bogota_Info infoOrigen = (Vertices_Bogota_Info) cositaBienHecha.getInfoVertex(origen);
						Vertices_Bogota_Info infoVecino = (Vertices_Bogota_Info) cositaBienHecha.getInfoVertex(vecino);

						costo = costoHaversiano.distance(infoOrigen.darLat(), infoOrigen.darLon(), infoVecino.darLat(), infoVecino.darLon());

						cositaBienHecha.addEdge(origen, vecino, costo);
					}
				}
			}
			cadena = b.readLine();
		}

		System.out.println("Numero de arcos: " + cositaBienHecha.darE() + "\n---------------");
		b.close(); 
	}


	/////////////////////////////////////////////////////////////////////////Lectura del JSON de las estaciones de polic�a

	public void leerGeoJsonEstaciones (String pRuta)
	{
		estaciones=new ListaEnlazadaQueue<EstPol>();
		JsonParser parser= new JsonParser();
		FileReader fr=null;

		try
		{
			fr= new FileReader(pRuta);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		JsonElement datos=parser.parse(fr);
		dumpJSONElement(datos);
	}

	private void dumpJSONElement(JsonElement elemento)
	{
		if (elemento.isJsonObject()) 
		{
			JsonObject obj = elemento.getAsJsonObject();
			java.util.Set<java.util.Map.Entry<String,JsonElement>> entradas = obj.entrySet();
			java.util.Iterator<java.util.Map.Entry<String,JsonElement>> iter = entradas.iterator();

			while (iter.hasNext()) 
			{
				java.util.Map.Entry<String,JsonElement> entrada = iter.next();
				componentesDelComparendo(entrada.getKey());	            
				dumpJSONElement(entrada.getValue());
			}
		}

		else if (elemento.isJsonArray()) 
		{			
			JsonArray array = elemento.getAsJsonArray();
			java.util.Iterator<JsonElement> iter = array.iterator();
			while (iter.hasNext()) 
			{
				JsonElement entrada = iter.next();
				dumpJSONElement(entrada);
			}
		} 
		else if (elemento.isJsonPrimitive()) 
		{
			JsonPrimitive valor = elemento.getAsJsonPrimitive();
			if(porAgregar == null)
			{
				porAgregar = new EstPol();
			}
			if(parteDelaEstacion.equals("OBJECTID"))
			{
				porAgregar.setobjetcID(valor.getAsInt());
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPODESCRIP"))
			{
				porAgregar.setdescrip(valor.getAsString());
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPODIR_SITIO"))
			{
				porAgregar.setdirSitio(valor.getAsString());
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOLATITUD"))
			{
				porAgregar.setlatitud(valor.getAsDouble());
				//System.out.println(valor);
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOLONGITU"))
			{
				porAgregar.setlongitud(valor.getAsDouble());
				//System.out.println(valor);
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOTELEFON"))
			{
				porAgregar.settel(valor.getAsString());
				//System.out.println(valor);
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOCELECTR"))
			{
				porAgregar.setmail(valor.getAsString());
				//System.out.println(valor);
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPONOMBRE"))
			{				
				porAgregar.setnombre(valor.getAsString());
				//System.out.println(valor);	
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOIDENTIF"))
			{				
				porAgregar.setidentificador(valor.getAsString());
				//System.out.println(valor);	
				parteDelaEstacion = "";
				//AGREGAR//

				coordenadas = false;
				parteDelaEstacion = "";

				estaciones.enqueue(porAgregar);
				System.out.println(porAgregar.darobjetcID());
				porAgregar = null;
				//System.out.println("///AGREGADO///");
			}
			else if (parteDelaEstacion.equals("coordinates"))
			{
				agregarCoordenada(valor.getAsDouble());				
			}
			else
			{
				//Es algo que no nos interesa
			}

		} 
		else if (elemento.isJsonNull()) 
		{
			System.out.println("Es NULL");
		} 
		else 
		{
			System.out.println("Es otra cosa");
		}
	}

	public void componentesDelComparendo(String palabra)
	{
		if (palabra.equals("OBJECTID"))
		{
			parteDelaEstacion = "OBJECTID";
		}
		else if (palabra.equals("EPODESCRIP"))
		{
			parteDelaEstacion = "EPODESCRIP";
		}
		else if (palabra.equals("EPODIR_SITIO"))
		{
			parteDelaEstacion = "EPODIR_SITIO";
		}
		else if (palabra.equals("EPOLATITUD"))
		{
			parteDelaEstacion = "EPOLATITUD";
		}
		else if (palabra.equals("EPOLONGITU"))
		{
			parteDelaEstacion = "EPOLONGITU";
		}
		else if (palabra.equals("EPOTELEFON"))
		{
			parteDelaEstacion = "EPOTELEFON";
		}
		else if (palabra.equals("EPOCELECTR"))
		{
			parteDelaEstacion = "EPOCELECTR";
		}
		else if (palabra.equals("EPONOMBRE"))
		{
			parteDelaEstacion = "EPONOMBRE";
		}
		else if (palabra.equals("EPOIDENTIF"))
		{
			parteDelaEstacion = "EPOIDENTIF";
		}
		else if (palabra.equals("coordinates"))
		{
			parteDelaEstacion = "coordinates";
		}
	}

	public void agregarCoordenada(double pCor)
	{
		if(coordenadas == false)
		{
			porAgregar.setlongitud(pCor);
			//System.out.println("Longitud: " + pCor);
			coordenadas = true;
		}
		else
		{
			porAgregar.setlatitud(pCor);
		}
	}


	//////////////////////////////////////////////////////////////////////Lectura del JSON del grafo


	public void leerJsonGrafo(String pRuta)
	{
		JsonParser parser= new JsonParser();
		FileReader fr=null;

		try
		{
			fr= new FileReader(pRuta);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		JsonElement datos=parser.parse(fr);
		dumpJSONElementGrafo(datos);


		System.out.println(cositaBienHecha.darV());
		System.out.println(cositaBienHecha.darE());
	}

	private void dumpJSONElementGrafo(JsonElement elemento)
	{
		if (elemento.isJsonObject()) 
		{
			JsonObject obj = elemento.getAsJsonObject();

			java.util.Set<java.util.Map.Entry<String,JsonElement>> entradas = obj.entrySet();
			java.util.Iterator<java.util.Map.Entry<String,JsonElement>> iter = entradas.iterator();

			while (iter.hasNext()) 
			{
				java.util.Map.Entry<String,JsonElement> entrada = iter.next();
				componentesDelGrafo(entrada.getKey());	            

				dumpJSONElementGrafo(entrada.getValue());
			}
		}
		else if (elemento.isJsonArray()) 
		{			
			JsonArray array = elemento.getAsJsonArray();
			java.util.Iterator<JsonElement> iter = array.iterator();

			while (iter.hasNext()) 
			{
				JsonElement entrada = iter.next();
				dumpJSONElementGrafo(entrada);
			}

		}
		else if (elemento.isJsonPrimitive()) 
		{
			JsonPrimitive valor = elemento.getAsJsonPrimitive();

			if(infoPorAgregar == null)
			{
				infoPorAgregar=new Vertices_Bogota_Info();
			}

			if(parteDelVerti.equals("OBJECTID"))
			{
				vertiPorAgregar=new Vertice<Integer, Vertices_Bogota_Info>(valor.getAsInt(), null);
				infoPorAgregar=new Vertices_Bogota_Info();
				//System.out.println(valor);
				parteDelVerti = "";
			}
			else if (parteDelVerti.equals("LATITUD"))
			{
				infoPorAgregar.asignarLat(valor.getAsDouble());
				//System.out.println(compaAgregar.darFecha_Hora().toString());
				parteDelVerti = "";
			}
			else if (parteDelVerti.equals("LONGITUD"))
			{
				infoPorAgregar.asignarLon(valor.getAsDouble());
				//System.out.println(valor);
				parteDelVerti = "";
				//Agregar
				parteDelVerti = "";
				vertiPorAgregar.cambiarInfo(infoPorAgregar);
				System.out.println(vertiPorAgregar.darId());
				cositaBienHecha.addVertex(vertiPorAgregar.darId(), infoPorAgregar);

				vertiPorAgregar=null;
				infoPorAgregar=null;


			}

			else if (parteDelVerti.equals("ORIGEN"))
			{

				origenPorIngresar=valor.getAsInt();
			}

			else if (parteDelVerti.equals("DESTINO"))
			{
				destinoPorIngresar=valor.getAsInt();
			}
			else if (parteDelVerti.equals("COSTO"))
			{
				costoPorAgregar=valor.getAsDouble();
				System.out.println("Llega");
				cositaBienHecha.addEdge(origenPorIngresar, destinoPorIngresar, costoPorAgregar);
				origenPorIngresar=0;
				destinoPorIngresar=0;
				costoPorAgregar=0;

			}

			else if (parteDelVerti.equals("coordinates"))
			{
				agregarCoordenadaGrafo(valor.getAsDouble());				
			}


			else
			{
				//Es algo que no nos interesa
			}
		} 
		else if (elemento.isJsonNull()) 
		{
			System.out.println("Es NULL");
		} 
		else 
		{
			System.out.println("Es otra cosa");
		}


	}


	public void componentesDelGrafo(String palabra)
	{
		if (palabra.equals("OBJECTID"))
		{
			parteDelVerti = "OBJECTID";
		}
		else if (palabra.equals("LATITUD"))
		{
			parteDelVerti = "LATITUD";
		}
		else if (palabra.equals("LONGITUD"))
		{
			parteDelVerti = "LONGITUD";
		}
		else if (palabra.equals("ORIGEN"))
		{
			parteDelVerti = "ORIGEN";
		}
		else if (palabra.equals("DESTINO"))
		{
			parteDelVerti = "DESTINO";
		}
		else if (palabra.equals("COSTO"))
		{
			parteDelVerti = "COSTO";
		}
		else if (palabra.equals("coordinates"))
		{
			parteDelVerti = "coordinates";
		}
	}

	public void agregarCoordenadaGrafo(double pCor)
	{
		if(coordenadasGrafo == false)
		{
			infoPorAgregar.asignarLat(pCor);
			//System.out.println("Longitud: " + pCor);
			coordenadasGrafo = true;
		}

		else
		{

			infoPorAgregar.asignarLon(pCor);
			//System.out.println("Latitud: " + pCor);

			coordenadasGrafo = false;
		}
	}





	///////////////////////////////////////////////////////////////////Imprimir el grafo


	public void imprimirLaCosaBienHecha()
	{

		Graph base=cositaBienHecha;

		TablaHashSondeoLineal vertex=base.vertis;
		ListaEnlazadaQueue arcos = base.arcos;
		char comillas = '"';


		try 
		{
			FileWriter file = new FileWriter("./data/grafoCreado.json");


			//Apertura del Archivo

			file.write("{");
			file.write(comillas+"type"+comillas+":"+comillas+"FeatureCollection"+comillas+",");
			file.write(comillas+"crs"+comillas+":");
			file.write("{");
			file.write(comillas+"type"+comillas+":"+comillas+"name"+comillas +",");
			file.write(comillas +"properties"+comillas+":");
			file.write("{");
			file.write(comillas +"name"+comillas+":"+comillas+"EPSG:4686"+comillas);
			file.write("}");
			file.write("},");
			file.write(comillas+"features"+comillas+":[");


			//Lo que se debe imprimir para cada v�rtice
			//vertex.darDatos()

			for (int i=0;i<2;i++)
			{
				Vertice aux = (Vertice)vertex.getSet(i);
				Integer id = (Integer)aux.darId();
				Vertices_Bogota_Info info = (Vertices_Bogota_Info)aux.darInfo();
				Double lat = info.darLat(), lon=info.darLon();

				file.write("{");	
				file.write(comillas+"type"+comillas+":"+comillas+"Feature"+comillas+",");
				file.write(comillas+"id"+comillas+":"+id+",");
				file.write(comillas+"geometry"+comillas+":{");
				file.write(comillas+"type"+comillas+":"+comillas+"Point"+comillas+",");
				file.write(comillas+"coordinates"+comillas+":[");
				file.write(lon+",");
				file.write(lat+"");
				file.write("]");
				file.write("},");
				file.write(comillas+"properties"+comillas+":");
				file.write("{");
				file.write(comillas+"OBJECTID"+comillas+":"+id+",");
				file.write(comillas+"LATITUD"+comillas+":"+lat+",");
				file.write(comillas+"LONGITUD"+comillas+":"+lon+"");
				file.write("}");
				file.write("},");
			}


			//Lo que se debe imprimir para cada arco:

			Node actual=arcos.darPrimerElemento();

			while (actual!=null)
			{
				Arco aux=(Arco)actual.data;

				file.write("{");	
				file.write(comillas+"type"+comillas+":"+comillas+"Feature"+comillas+",");
				file.write(comillas+"id"+comillas+":0,");

				file.write(comillas+"properties"+comillas+":");
				file.write("{");

				file.write(comillas+"ORIGEN"+comillas+":"+aux.darInicial().darId()+",");
				file.write(comillas+"DESTINO"+comillas+":"+aux.darFinal().darId()+",");
				file.write(comillas+"COSTO"+comillas+":"+aux.darCostoHaversiano());
				file.write("}");


				//Este depende de si es el �ltimo o de si quedan m�s con la coma

				//				if (actual.darSiguiente()==null)
				//				{
				file.write("}");
				//				}
				//				else
				//				{
				//					file.write("},");
				//				}

				actual=null;
			}


			//Cerrar el JSON
			file.write("]");
			file.write("}");


			file.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

