import java.io.*;
import p79068.bmpio.*;

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
	void readImage(String imageName) throws IOException;
}

class Neuron implements NeuronInterface
{
	private double weights[];

	public String serializeWightsToString()
	{
		String result = new String();
		for (int wIdx = 0; wIdx < weights.length; wIdx++)
		{
			result = result.concat(String.valueOf(weights[wIdx]) + " ");
		}

		return result;		
	}

	public void initWithString(String stringOfWeights)
	{
		String[] stringsSplittedbySpace = stringOfWeights.split(" ");
			for(int idx = 0; idx < stringsSplittedbySpace.length; idx++)
			{
				double theWeight = Double.parseDouble(stringsSplittedbySpace[idx]);
				weights[idx] = theWeight;
			}
	}

	public Neuron()
	{
		weights = new double[8];
		for (int idx = 0; idx < weights.length; idx++)
		{
			weights[idx] = Math.random();
		}
	}

	public void print()
	{
		for (int idx = 0; idx < weights.length; idx++)
		{
			System.out.printf("%f ", weights[idx]);			
		}
		System.out.printf("\n");
	}
}

class NeuralNetwork implements NeuralNetworkInterface
{	
	private Neuron neurons[];
	private static int N = 30;
	private static int M = 26;
	public NeuralNetwork()
	{
		neurons = new Neuron[5];
		for (int idx = 0; idx < neurons.length; idx++)
		{
			neurons[idx] = new Neuron();
		}
	}

	public void print()
	{
		Neuron theNeuron = neurons[0];
		if (theNeuron == null)
		{
			System.out.printf("theNeuron == null\n");
		}
		for (int nIdx = 0; nIdx < neurons.length; nIdx++)
		{
			Neuron theN = neurons[nIdx];
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

		for (int nIdx = 0; nIdx < neurons.length; nIdx++)
		{
			Neuron theNeuron = neurons[nIdx];	
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
			neurons[nIdx++].initWithString(stringOfWeights);
		}
	}

	public void readImage(String imageName) throws IOException
	{
		InputStream in = new FileInputStream(imageName);
		BmpImage bmp;
		try
		{
			bmp = BmpReader.read(in);
		}
		finally
		{
			in.close();
		}
		int bmpWidth = bmp.image.getWidth();
		int bmpHeight = bmp.image.getHeight();

		System.out.printf("width: %d\nheight: %d\n", bmpWidth, bmpHeight);

		for (int i = 0; i < bmpHeight; i++)
		{
			for (int j = 0; j < bmpWidth; j++)
			{
				System.out.printf("%6x ", bmp.image.getRgb888Pixel(j, i));
			}
			System.out.printf("\n");
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
		//nn.loadFromFile();
		//nn.print();
		nn.readImage("test.bmp");

	}
}