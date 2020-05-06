package model.data_structures;

public class InformacionVertice implements Comparable<InformacionVertice> {

	private double longitud;
	private double latitud;
	
	public InformacionVertice(double iLongitud, double iLatitud)
	{
		longitud = iLongitud;
		latitud = iLatitud;
	}
	
	
	public double darLon()
	{
		return longitud;
	}

	public double darLat()
	{
		return latitud;
	}
	

	///////////////////////////////////////////////////////////////NO LOS NECESITAMOS.
	@Override
	public int compareTo(InformacionVertice o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}