import java.io.*;

interface NeuronInterface
{
	String serializeWightsToString();
	void initWithString(String stringOfWeights);
	void print();
}

interface NeuralNetworkInterface
{
	void print();
	void saveToFile() throws IOException;
	void loadFromFile() throws IOException;
}

class Neuron implements NeuronInterface
{
	private double w[];

	public String serializeWightsToString()
	{
		String result = new String();
		for (int wIdx = 0; wIdx < w.length; wIdx++)
		{
			result = result.concat(String.valueOf(w[wIdx]) + " ");
		}

		return result;		
	}

	public void initWithString(String stringOfWeights)
	{
		String[] stringsSplittedbySpace = stringOfWeights.split(" ");
			for(int idx = 0; idx < stringsSplittedbySpace.length; idx++)
			{
				double theWeight = Double.parseDouble(stringsSplittedbySpace[idx]);
				w[idx] = theWeight;
			}
	}

	public Neuron()
	{
		w = new double[8];
		for (int idx = 0; idx < w.length; idx++)
		{
			w[idx] = Math.random();
		}
	}

	public void print()
	{
		for (int idx = 0; idx < w.length; idx++)
		{
			System.out.printf("%f ", w[idx]);			
		}
		System.out.printf("\n");
	}
}

class NeuralNetwork implements NeuralNetworkInterface
{	
	private Neuron n[];
	public NeuralNetwork()
	{
		n = new Neuron[5];
		for (int idx = 0; idx < n.length; idx++)
		{
			n[idx] = new Neuron();
		}
	}

	public void print()
	{
		Neuron theNeuron = n[0];
		if (theNeuron == null)
		{
			System.out.printf("theNeuron == null\n");
		}
		for (int nIdx = 0; nIdx < n.length; nIdx++)
		{
			Neuron theN = n[nIdx];
			theN.print();
		}
	}

	public void saveToFile() throws IOException
	{
		File file = new File("nn.txt");
		if (!file.exists())
		{
			file.createNewFile();
		}

		FileOutputStream fileOut = new FileOutputStream(file);
		PrintStream printStream = new PrintStream(fileOut);

		for (int nIdx = 0; nIdx < n.length; nIdx++)
		{
			Neuron theNeuron = n[nIdx];	
			printStream.printf("%s\n", theNeuron.serializeWightsToString());			
		}			
		fileOut.close(); 
	}

	public void loadFromFile() throws IOException
	{
		File file = new File("nn.txt");
		if (!file.exists())
		{
			System.out.printf("file doesn't exists\n");
			return;
		}

		FileInputStream fileIn = new FileInputStream(file);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileIn));
		String stringOfWeights;
		int nIdx = 0;
		while((stringOfWeights = bufferedReader.readLine()) != null)
		{
			if (stringOfWeights.length() == 0)	{ break; }	
			n[nIdx++].initWithString(stringOfWeights);
		}
	}
}

class NeuralTest 
{
	public static void main(String[] args) throws IOException
	{		
		NeuralNetwork nn = new NeuralNetwork();
		//nn.print();
		//nn.saveToFile();
		nn.loadFromFile();
		nn.print();
	}
}