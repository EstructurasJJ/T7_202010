package model.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import model.data_structures.Graph;
import model.data_structures.Vertice;


public class Modelo 
{

	private String parteDelaEstacion; 
	private Vertice vertiAgregar;
	private Vertices_Bogota_Info infoVertice;
	private Haversine costoHaversiano;

	private Graph cositaBienHecha = new Graph(1);

	///////////////////////////////////////////////////////Constructor

	public Modelo()
	{
		parteDelaEstacion = "";
	}

	//////////////////////////////////////////////////////Dar

	public Graph<Integer, Vertices_Bogota_Info> darGrafo()
	{
		return cositaBienHecha;
	}

	/////////////////////////////////////////////////////////////////////////////////LECTURA 

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
			
			//Vertices_Bogota_Info infa = (Vertices_Bogota_Info) vertiAgregar.darInfo();
			//System.out.println("ID: " +  ID + "\nLatitud: " + infa.darLat() + "\nLongitud: " + infa.darLon() + "\n------------");
			
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
						
						//System.out.println("Origen: " + origen + " ---> Final: " + vecino + "\n Costo: " + costo + "\n-----------");
					}
					
				}
			}

			cadena = b.readLine();
		}
		
		System.out.println("Numero de arcos: " + cositaBienHecha.darE() + "\n---------------");
		b.close(); 
	}


	








	//	public void leerGeoJson(String pRuta, int capIni) 
	//	{	
	//		//////NUEVO TALLER 5//////
	//		HSLBobi = new TablaHashSondeoLineal<String, Comparendo>(capIni);
	//		HSCJuanjo= new TablaHashEncSeparado<String, Comparendo>(capIni);
	//		//////////////////////////
	//		
	//		JsonParser parser = new JsonParser();
	//		FileReader fr = null;
	//
	//		try 
	//		{
	//			fr = new FileReader(pRuta);
	//		} 
	//		catch (Exception e) 
	//		{
	//			e.printStackTrace();
	//		}
	//
	//		JsonElement datos = parser.parse(fr);
	//		dumpJSONElement(datos);
	//
	//	}
	//
	//	private void dumpJSONElement(JsonElement elemento) 
	//	{
	//
	//
	//		if (elemento.isJsonObject()) 
	//		{
	//
	//			JsonObject obj = elemento.getAsJsonObject();
	//
	//			java.util.Set<java.util.Map.Entry<String,JsonElement>> entradas = obj.entrySet();
	//			java.util.Iterator<java.util.Map.Entry<String,JsonElement>> iter = entradas.iterator();
	//
	//			while (iter.hasNext()) 
	//			{
	//				java.util.Map.Entry<String,JsonElement> entrada = iter.next();
	//				componentesDelComparendo(entrada.getKey());	            
	//
	//				dumpJSONElement(entrada.getValue());
	//			}
	//
	//		}
	//		else if (elemento.isJsonArray()) 
	//		{			
	//			JsonArray array = elemento.getAsJsonArray();
	//			java.util.Iterator<JsonElement> iter = array.iterator();
	//
	//			while (iter.hasNext()) 
	//			{
	//				JsonElement entrada = iter.next();
	//				dumpJSONElement(entrada);
	//			}
	//
	//		} 
	//		else if (elemento.isJsonPrimitive()) 
	//		{
	//			JsonPrimitive valor = elemento.getAsJsonPrimitive();
	//
	//			if(compaAgregar == null)
	//			{
	//				compaAgregar = new Comparendo();
	//			}
	//
	//			if(parteDelComparendo.equals("OBJECTID"))
	//			{
	//				compaAgregar.asignarObjectid(valor.getAsInt());
	//				//System.out.println(valor);
	//				parteDelComparendo = "";
	//			}
	//			else if (parteDelComparendo.equals("FECHA_HORA"))
	//			{
	//				compaAgregar.asignarFecha_Hora(valor.getAsString());
	//				//System.out.println(compaAgregar.darFecha_Hora().toString());
	//				parteDelComparendo = "";
	//			}
	//			else if (parteDelComparendo.equals("MEDIO_DETECCION"))
	//			{
	//				compaAgregar.asignarMedio_Dete(valor.getAsString());
	//				//System.out.println(valor);
	//				parteDelComparendo = "";
	//			}
	//			else if (parteDelComparendo.equals("CLASE_VEHICULO"))
	//			{
	//				compaAgregar.asignarClase_Vehi(valor.getAsString());
	//				//System.out.println(valor);
	//				parteDelComparendo = "";
	//			}
	//			else if (parteDelComparendo.equals("TIPO_SERVICIO"))
	//			{
	//				compaAgregar.asignarTipo_Servicio(valor.getAsString());
	//				//System.out.println(valor);
	//				parteDelComparendo = "";
	//			}
	//			else if (parteDelComparendo.equals("INFRACCION"))
	//			{
	//				compaAgregar.asignarInfraccion(valor.getAsString());
	//				//System.out.println(valor);
	//				parteDelComparendo = "";
	//			}
	//			else if (parteDelComparendo.equals("DES_INFRACCION"))
	//			{
	//				compaAgregar.asignarDes_Infrac(valor.getAsString());
	//				//System.out.println(valor);
	//				parteDelComparendo = "";
	//
	//			}
	//			else if (parteDelComparendo.equals("LOCALIDAD"))
	//			{				
	//				compaAgregar.asignarLocalidad(valor.getAsString());
	//				//System.out.println(valor);	
	//				parteDelComparendo = "";
	//			}
	//			else if (parteDelComparendo.equals("MUNICIPIO"))
	//			{				
	//				compaAgregar.asignarMunicipio(valor.getAsString());
	//				//System.out.println(valor);	
	//				parteDelComparendo = "";
	//			}
	//			else if (parteDelComparendo.equals("coordinates"))
	//			{
	//				agregarCoordenada(valor.getAsDouble());				
	//			}
	//
	//		} 
	//		else if (elemento.isJsonNull()) 
	//		{
	//			System.out.println("Es NULL");
	//		} 
	//		else 
	//		{
	//			System.out.println("Es otra cosa");
	//		}
	//
	//	}
	//
	//	public void componentesDelComparendo(String palabra)
	//	{
	//		if (palabra.equals("OBJECTID"))
	//		{
	//			parteDelComparendo = "OBJECTID";
	//		}
	//		else if (palabra.equals("FECHA_HORA"))
	//		{
	//			parteDelComparendo = "FECHA_HORA";
	//		}
	//		else if (palabra.equals("MEDIO_DETECCION"))
	//		{
	//			parteDelComparendo = "MEDIO_DETECCION";
	//		}
	//		else if (palabra.equals("CLASE_VEHICULO"))
	//		{
	//			parteDelComparendo = "CLASE_VEHICULO";
	//		}
	//		else if (palabra.equals("TIPO_SERVICIO"))
	//		{
	//			parteDelComparendo = "TIPO_SERVICIO";
	//		}
	//		else if (palabra.equals("INFRACCION"))
	//		{
	//			parteDelComparendo = "INFRACCION";
	//		}
	//		else if (palabra.equals("DES_INFRACCION"))
	//		{
	//			parteDelComparendo = "DES_INFRACCION";
	//		}
	//		else if (palabra.equals("LOCALIDAD"))
	//		{
	//			parteDelComparendo = "LOCALIDAD";
	//		}
	//		else if (palabra.equals("MUNICIPIO"))
	//		{
	//			parteDelComparendo = "MUNICIPIO";
	//		}
	//		else if (palabra.equals("coordinates"))
	//		{
	//			parteDelComparendo = "coordinates";
	//		}
	//	}
	//
	//	public void agregarCoordenada(double pCor)
	//	{
	//		if(coordenadas == false)
	//		{
	//			compaAgregar.asignarLongitud(pCor);
	//			//System.out.println("Longitud: " + pCor);
	//
	//			if (pCor < minLongitud)
	//			{
	//				minLongitud = pCor;
	//			}
	//			else if (pCor > maxLongitud)
	//			{
	//				maxLongitud = pCor;
	//			}
	//
	//			coordenadas = true;
	//		}
	//
	//		else
	//		{
	//			compaAgregar.asignarLatitud(pCor);
	//			//System.out.println("Latitud: " + pCor);
	//
	//			if (pCor < minLatitud)
	//			{
	//				minLatitud = pCor;
	//			}
	//			else if (pCor > maxLatitud)
	//			{
	//				maxLatitud = pCor;
	//			}
	//
	//			//AGREGAR//
	//
	//			coordenadas = false;
	//			parteDelComparendo = "";
	//			
	//			/////NUEVO TALLER 5/////
	//	
	//			String key = getFechaMod(compaAgregar.darFecha_Hora());			
	//			key = key + "-" + compaAgregar.darClase_Vehi() + "-" + compaAgregar.darInfraccion();
	//			
	//			HSLBobi.putInSet(key, compaAgregar);
	//			HSCJuanjo.putInSet(key, compaAgregar);
	//			
	//			key = "";
	//			////////////////////////
	//			
	//			compaAgregar = null;
	//
	//			//System.out.println("///AGREGADO///");
	//
	//		}
	//	}
	//	
	//	public String getFechaMod(Date fechaMod)
	//	{
	//	    SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
	//	    return sf.format(fechaMod);
	//	}
	//
	//

}

