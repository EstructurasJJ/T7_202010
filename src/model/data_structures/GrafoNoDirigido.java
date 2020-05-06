package model.data_structures;

import java.io.InputStream;

public class GrafoNoDirigido <K extends Comparable<K>,V> 
{
		
	//Número de vértices
	private int V;
	
	//Número de Arcos
	private int E;
	
	//Lista de Adyacencia
	private ListaEnlazadaQueue<Integer>[] ADJ;
	
	
	//Crea un grafo no dirigido de numVertex vértices sin arcos
	public GrafoNoDirigido(int numVertex)
	{
		V=numVertex;
		E=0;
		
		ADJ = new ListaEnlazadaQueue [V];
		
		for(int i=0; i<V;i++)
		{
			ADJ[i]=new ListaEnlazadaQueue<Integer>();
		}
			
	}
	

	
	
	//Devuelve el número de vértices
	public int numVertex()
	{
		return V;
	}
	
	//Devuelve el número de arcos 
	public int numEdges()
	{
		return E;
	}
	
	//Crea un arco entre dos nodos
	
	public void addEdge (int v, int w)
	{
		ADJ[v].enqueue(w);
		ADJ[w].enqueue(v);
		E++;
	}
	
	//Devuelve los nodos adyacentes
	public Iterable<K> darAdyacentes(K v)
	{
		return ADJ[v];
	}
	
	
	//Marca todos los adyacentes al que tenga la llave K
	public void dfs(K s)
	{
		DFS proceso=new DFS(this, s);
	}
	
	
	public void uncheck()
	{
		
	}
	
	
}
