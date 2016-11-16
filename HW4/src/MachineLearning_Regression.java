import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MachineLearning_Regression {

	static Map<String, Integer> ingredients = new HashMap<String, Integer>();
	static int indexCounter = 0;
	
	// Rows = 5/6 of total data. This is the training set. the remaining is used for testing
	// Columns = # of features (ingredients)
	static int trainingRows = 1495;
	static int ingrNum = 2398;
	static int numPerceptrons = 10;
	
	static int[][] features = new int[trainingRows][ingrNum];
	static ArrayList<Perceptron> hiddenLayer = new ArrayList<Perceptron>();
	static ArrayList<OutputNode> outputLayer = new ArrayList<OutputNode>();	// Each output is a cuisine
	
	// bounds for the testing set
	static int lowTestBound = 1495;
	static int highTestBound= 1794;

	public static void main(String[] args) {
		// Run through ingredients and store them in a hashmap
		populateIngredientsMap();
		
		// Initialize features matrix to all zeroes
		initMatrixZero();
		
		// Run through recipes and set up values in features matrix
		initFeaturesMatrix();
		
		// Init the hidden layer and output layer with random weights
		initHiddenLayer();
		initOutputLayer();
		
		for (int row = 0; row < 100; row++) {
			for (int col = 0; col < 1000; col++) {
				//features[row][col] = 0;
				System.out.print(features[row][col] + " ");
			}
			System.out.println();
		}
		
	}
	
	
	// Initialize 10 perceptrons for the hidden layer
	public static void initHiddenLayer() {
		for (int i = 0; i < numPerceptrons; i++) {
			hiddenLayer.add(new Perceptron());
		}
	}

	// Initialize 20 perceptrons for the hidden layer
	public static void initOutputLayer() {
		for (int i = 0; i < 20; i++) {
			outputLayer.add(new OutputNode());
		}
	}
	
	
	public static void initFeaturesMatrix() {
		String csvFile = "../HW4/src/training.csv";
		BufferedReader br = null;
		String line = "";
		
		int rowCounter = 0;

		/*
		 *  If the hashmap contains the current ingredient, then look up where the index of that ingredient by doing
		 *  hashmap.get(ingredient) (return index) and set that index in the features matrix to 1. Each row corresponds
		 *  to a recipe, 1 = contains ingredient, 0 = doesn't contain ingredient. go row by row. 
		 */
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				if (rowCounter >= lowTestBound && rowCounter < highTestBound) {
					// This is where we'll do stuff with the testing data
				} else {
					String[] recipes = line.split(",");
					for (String ingr : recipes) {
						if (ingredients.containsKey(ingr)) {
							int index = ingredients.get(ingr);
							//System.out.println("row: " + rowCounter + ", col: " + index);
							features[rowCounter][index] = 1;
						}
					}
				}

				rowCounter++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void initMatrixZero() {
		// init matrix to all zeroes
		for (int row = 0; row < trainingRows; row++) {
			for (int col = 0; col < ingrNum; col++) {
				features[row][col] = 0;
			}
		}
	}
	

	public static void populateIngredientsMap() {
		String csvFile = "../HW4/src/ingredients.txt";
		BufferedReader br = null;
		String line = "";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				
				String ingr = line;
				if (!ingredients.containsKey(ingr)) {
					ingredients.put(ingr, indexCounter);
					indexCounter++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// These are the nodes in the hidden layer
	public static class Perceptron {
		float output; // This output will get passed to the sigmoid function
		float[] weights = new float[ingrNum];

		public Perceptron() {
			output = 0;
			Random rand = new Random();
			for (int i = 0; i < ingrNum; i++) {
				weights[i] = -.05f + rand.nextFloat() * .1f; // Random num between -.05 and .05
			}
		}
	}
	
	
	// Every perceptron in the hidden layer links to every OutputNode
	public static class OutputNode {
		float output; // This output will get passed to the sigmoid function
		float[] weights = new float[numPerceptrons];

		public OutputNode() {
			output = 0;
			Random rand = new Random();
			for (int i = 0; i < numPerceptrons; i++) {
				weights[i] = -.05f + rand.nextFloat() * .1f; // Random num between -.05 and .05
			}
		}
	}
	
	
	
	

}

