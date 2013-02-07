class Neuron
{
	private double w[];

	public Neuron() {
		w = new double[8];
		for (int idx = 0; idx < w.length; idx++)
		{
			w[idx] = Math.random();
		}
	}

	public void print() {
		for (int idx = 0; idx < w.length; idx++)
		{
			System.out.printf("%f ", w[idx]);			
		}
		System.out.printf("\n");
	}
}

class NeuralNetwork
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
}

class NeuralTest
{
	public static void main(String[] args)
	{		
		NeuralNetwork nn = new NeuralNetwork();
		nn.print();
	}
}