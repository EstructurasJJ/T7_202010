package model.data_structures;

public class DFS 
{
	private boolean[] marked;
	private Integer[] edgeTo;
	private int count;
	private int s;

	public DFS(GrafoNoDirigido G, int s)
	{
		marked=new boolean [G.numVertex()];
		edgeTo=new Integer [G.numVertex()];
		this.s=s;
		dfs(G,s);
	}


	private void dfs(GrafoNoDirigido G, int v)
	{
		marked[v] = true;
		count++;


		for (int w : G.darAdyacentes(v))
			if (!marked[w])
			{
				
				dfs(G, w);
				
			}
				
	}


	public boolean marked(int w)
	{ 
		return marked[w]; 
	}
	
	public int count()
	{ 
		return count; 
	}

}
