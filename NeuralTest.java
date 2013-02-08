import java.io.*;
import p79068.bmpio.*;

interface NeuronInterface
{
	String serializeWightsToString();
	void initWithString(String stringOfWeights);
	void initWithMathRandom();
	void print();
	double evaluate(double[] vector);
	void tuneToVector(double[] vector);
	void dumpNeuronAsBmp(String filename, int width, int height) throws IOException;
}

interface NeuralNetworkInterface
{
	void print();
	void saveToFile() throws IOException;
	void loadFromFile() throws IOException;
	void initWithMathRandom();
	void tuneNeuronIdxToBufferedImage(int targetNueronIdx);
	int evaluateNeuronsWithBufferImage();
	void bufferImage(String imageName) throws IOException;
	void dumpNeuronAsBmp(int nIdx, String fileName, int width, int height) throws IOException;
}

class Utilities
{
	public static double[] sumVectors(double[] vector1, double[] vector2)
	{
		if (vector1.length != vector2.length)
		{
			System.out.println("summing vectors have different number of components");
			return null;
		}
		double[] result = new double[vector1.length];
		System.arraycopy(vector1, 0, result, 0, vector1.length);

		for (int idx = 0; idx < vector1.length; idx++)
		{
			result[idx] += vector2[idx]; 
		}

		return result;
	}

	public static double[] substructVectors(double[] vector1, double[] vector2)
	{
		if (vector1.length != vector2.length)
		{
			System.out.printf("substructing vectors have different number of components (%d vs %d)", vector1.length, vector2.length);
			return null;
		}
		double[] result = new double[vector1.length];
		System.arraycopy(vector1, 0, result, 0, vector1.length);

		for (int idx = 0; idx < vector1.length; idx++)
		{
			result[idx] -= vector2[idx]; 
		}

		return result;
	}

	public static double[] multiplyVectorsOnScalar(double[] vector, double k)
	{
		double[] result = new double[vector.length];
		System.arraycopy(vector, 0, result, 0, vector.length);

		for (int idx = 0; idx < vector.length; idx++)
		{
			result[idx] *= k; 
		}

		return result;
	}

	public static void printVector(double[] vector)
	{
		System.out.printf("Printing vector:\n");
		for (int idx = 0; idx < vector.length; idx++)
		{
			System.out.printf("%f, ", vector[idx]);
		}
		System.out.printf("\n");
	}
}

class Neuron implements NeuronInterface
{
	private double weights[];
	private double beta = 0.7;

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

	public Neuron(int n)
	{
		weights = new double[n];
		
	}

	public void initWithMathRandom()
	{
		for (int idx = 0; idx < weights.length; idx++)
		{
			weights[idx] = Math.random() * 16777215;
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

	public double evaluate(double[] vector)
	{
		if (vector.length != weights.length)
		{
			System.out.printf("input vector dimension differs from neuron dimension (weights num)\n%d against %d\n", vector.length, weights.length);
			assert(false);
		}
		double sum = 0;
		for (int idx = 0; idx < vector.length; idx ++)
		{
			sum += vector[idx] * weights[idx];
		}

		return sum;
	}

	public void tuneToVector(double[] vector)
	{
		double[] diffVector = Utilities.substructVectors(vector, weights);
		double[] nVector = Utilities.multiplyVectorsOnScalar(diffVector, beta);		
		weights = Utilities.sumVectors(weights, nVector);
	}

	public void dumpNeuronAsBmp(String filename, int width, int height) throws IOException
	{
		File file = new File(filename);
		if (!file.exists())
		{
			file.createNewFile();
		}

		FileOutputStream fileOut = new FileOutputStream(file);

		BufferedRgb888Image bufferedRgb888Image = new BufferedRgb888Image(width, height);

		for (int wIdx = 0; wIdx < weights.length; wIdx++)
		{
			int x = wIdx % width;
			int y = wIdx / width;

			bufferedRgb888Image.setRgb888Pixel(x, y, (int)weights[wIdx]);
		}		

		BmpImage bmpImage = new BmpImage();
		bmpImage.image = bufferedRgb888Image;
		BmpWriter.write(fileOut, bmpImage);
	}
}

class NeuralNetwork implements NeuralNetworkInterface
{	
	private Neuron neurons[];
	private double[] vector;

	public NeuralNetwork(int m, int n)
	{
		neurons = new Neuron[m];
		for (int idx = 0; idx < neurons.length; idx++)
		{
			neurons[idx] = new Neuron(n);			
		}
	}

	public void initWithMathRandom()
	{
		for (int idx = 0; idx < neurons.length; idx++)
		{
			neurons[idx].initWithMathRandom();			
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

	public void tuneNeuronIdxToBufferedImage(int targetNueronIdx)
	{
		neurons[targetNueronIdx].tuneToVector(vector);
		System.out.printf("now %d neuron is (%f)", targetNueronIdx, neurons[targetNueronIdx].evaluate(vector));	
	}

	public void bufferImage(String imageName) throws IOException
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

		//System.out.printf("width: %d\nheight: %d\n", bmpWidth, bmpHeight);

		vector = new double[bmpWidth * bmpHeight];

		for (int y = 0; y < bmpHeight; y++)
		{
			for (int x = 0; x < bmpWidth; x++)
			{
				int idx = y * bmpWidth + x;
				vector[idx] = bmp.image.getRgb888Pixel(x, y);
				//System.out.printf("%6x ", bmp.image.getRgb888Pixel(j, i));
			}
			//System.out.printf("\n");
		}
	}

	public int evaluateNeuronsWithBufferImage()
	{
		double max = 0;
		int winnerIdx = -1;

		for (int nIdx = 0; nIdx < neurons.length; nIdx++)
		{
			double eval = neurons[nIdx].evaluate(vector);
			if (eval > max)
			{
				max = eval;
				winnerIdx = nIdx;
			}
			//System.out.printf("%d neuron evaluate vector as %f\n", nIdx, eval);			
		}
		System.out.printf("stronger neuron: %d (%f)\n", winnerIdx, max);
		return winnerIdx;
	}

	public void dumpNeuronAsBmp(int nIdx, String fileName, int width, int height) throws IOException
	{
		Neuron theNeuron = neurons[nIdx];
		theNeuron.dumpNeuronAsBmp(fileName, width, height);
	}
}

class NeuralTest 
{
	public static void main(String[] args) throws IOException
	{	
		String fileName;
		if (args.length > 0)
		{
			fileName = args[0];
		}
		else
		{
			fileName = "test.bmp";
		}
		System.out.println(fileName);

		NeuralNetwork nn = new NeuralNetwork(26, 300);
		//nn.initWithMathRandom();
		nn.loadFromFile();
		nn.bufferImage(fileName);

		if (args.length == 2)
		{			
			int targetNeuron = Integer.parseInt(args[1]);
			nn.tuneNeuronIdxToBufferedImage(targetNeuron);
			nn.saveToFile();
			System.out.printf("%d neuron tuned to image\n", targetNeuron);
		}
		else if (args.length == 1)
		{
			nn.evaluateNeuronsWithBufferImage();
		}

		nn.dumpNeuronAsBmp(0, "n0.bmp", 15, 20);
		nn.dumpNeuronAsBmp(1, "n1.bmp", 15, 20);
		nn.dumpNeuronAsBmp(2, "n2.bmp", 15, 20);
		nn.dumpNeuronAsBmp(3, "n3.bmp", 15, 20);		
	}
}