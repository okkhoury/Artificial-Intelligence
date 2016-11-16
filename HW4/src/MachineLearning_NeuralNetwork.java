import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Jama.Matrix;

public class MachineLearning_NeuralNetwork {
	
	static double totalError = 0;

	static Map<String, Integer> ingredients = new HashMap<String, Integer>();
	static Map<String, Integer> cuisines = new HashMap<String, Integer>();
	static int indexCounter = 0;
	
	// Rows = 5/6 of total data. This is the training set. the remaining is used for testing
	// Columns = # of features (ingredients)
	static int trainingRows = 1495;
	static int testingRows = 299;
	static int ingrNum = 2398;
	static int numPerceptrons = 10;
	static double learningRate = 0.09;
	
	static double[][] features = new double[trainingRows][ingrNum];
	static double[][] testing = new double[testingRows][ingrNum];
	static ArrayList<Perceptron> hiddenLayer = new ArrayList<Perceptron>();
	static ArrayList<OutputNode> outputLayer = new ArrayList<OutputNode>();	// Each output is a cuisine
	static double[] sigmoidOutputs = new double[numPerceptrons];
	
	// This array holds the type of cuisine for each row of our features matrix
	static String[] rowCuisines = new String[trainingRows];	
	static String[] testCuisines = new String[testingRows];
	
	// bounds for the testing set
	static int lowTestBound = 1495;
	static int highTestBound= 1794;

	public static void main(String[] args) {
		// Run through ingredients and store them in a hashmap
		populateIngredientsMap();
		
		// Initialize features and testing matrix to all zeroes
		initMatrixZero();
		
		// Run through recipes and set up values in features matrix (1 if contains ingr, 0 otherwise)
		initFeaturesMatrix();
		
		// Init the hidden layer and output layer with random weights
		initHiddenLayer();
		initOutputLayer();
		
		// testRecipe is an individual recipe example
		Matrix featureMatrix = new Matrix(features);
		Matrix testRecipe = new Matrix(1, trainingRows, 0);
		
		//Iterating through each training row to "TEACH" our network
		for (int i = 0; i < trainingRows; i++) {
			// Get recipe for this row into matrix format to multiply
			testRecipe = featureMatrix.getMatrix(i, i, 0, ingrNum - 1);

			// For Each Perceptron, multiply their weights by the incoming
			// recipe
			for (int j = 0; j < numPerceptrons; j++) {
				Matrix currentWeight = new Matrix(hiddenLayer.get(j).weights, 1);

				// HiddenLayer output calculation
				Matrix output = testRecipe.times((currentWeight.transpose()));
				String outputString = Arrays.deepToString(output.getArray());
				hiddenLayer.get(j).output = Double.parseDouble(outputString.substring(2, outputString.length() - 2));

				// Sigmoid Function from output
				sigmoidOutputs[j] = 1 / (1 + Math.pow(Math.E, hiddenLayer.get(j).output));
				
			}

			Matrix sigmoidMatrix = new Matrix(sigmoidOutputs, 1);
			double[] correctOutput = new double[20];
			
			for (int k = 0; k < 20; k++) {
				Matrix outputWeight = new Matrix(outputLayer.get(k).weights, 1);
				
				// HiddenLayer output calculation
				Matrix output = sigmoidMatrix.times((outputWeight.transpose()));
				String outputString = Arrays.deepToString(output.getArray());
				outputLayer.get(k).output = Double.parseDouble(outputString.substring(2, outputString.length() - 2));
				//Sigmoid Calculation
				outputLayer.get(k).output = 1 / (1 + Math.pow(Math.E, outputLayer.get(k).output));
				//System.out.println(outputLayer.get(k).output);
				
				//Populate each "0" value of correctOutput
				correctOutput[k] = 0.1;
			}
			
			
			//Put correct cuisine value into correctOutput as 0.9
			correctOutput[cuisines.get(rowCuisines[i])] = 0.9;
			
			//Calculating for the delta output layer
			calculateOutputDeltas(correctOutput);
			
			//Calculating the deltas for the hidden layer
			calculateHiddenDeltas();
			
			//Now we are updating the weights for the output layer
			for(int c = 0; c < 20; c++) {
				for(int j = 0; j < numPerceptrons; j++) {
					outputLayer.get(c).weights[j] = outputLayer.get(c).weights[j] + 
							outputLayer.get(c).delta * learningRate * sigmoidOutputs[j];
				}
			}
			
			//Now we are updating the weights for the hidden layer
			for(int h = 0; h < numPerceptrons; h++) {
				for(int j = 0; j < ingrNum; j++) {
					hiddenLayer.get(h).weights[j] = hiddenLayer.get(h).weights[j] + 
							hiddenLayer.get(h).delta * learningRate * features[i][j];
				}
			}
			
			// System.out.println(Arrays.deepToString(testRecipe.getArray()));
		}
		
	}

	
	
	public static void calculateOutputDeltas(double[] correctOutput) {
		for(int c = 0; c < 20; c++) {
			double Ok = outputLayer.get(c).output;
			// ERROR CALCULATION
			totalError +=  Math.pow(correctOutput[c] - Ok, 2);
			outputLayer.get(c).delta = Ok * (1-Ok) * (correctOutput[c] - Ok);
		}
		
		totalError /= 20;
		System.out.println("Output Error: " + totalError);
	}
	
	public static void calculateHiddenDeltas() {
		for(int i = 0; i < numPerceptrons; i++) {
			double sum = 0;
			for(int j = 0; j < 20; j++) {
				sum += outputLayer.get(i).weights[i] * outputLayer.get(i).delta;
			}
			hiddenLayer.get(i).delta = sigmoidOutputs[i] * (1-sigmoidOutputs[i]) * sum;
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
	
	
	// The rowCusines array is also set up in this function
	public static void initFeaturesMatrix() {
		String csvFile = "../HW4/src/training.csv";
		BufferedReader br = null;
		String line = "";
		
		int rowCounter = 0;
		int cuisineCounter = 0;
		int testingCounter = 0;
		
		Random random = new Random();
		int randomNum = 0;

		/*
		 *  If the hashmap contains the current ingredient, then look up where the index of that ingredient by doing
		 *  hashmap.get(ingredient) (return index) and set that index in the features matrix to 1. Each row corresponds
		 *  to a recipe, 1 = contains ingredient, 0 = doesn't contain ingredient. go row by row. 
		 */
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				//Gets randomNum and if it is 4 (approx 1/6 chance) then we put in testing matrix
				//If not then it goes in normal features matrix
				randomNum = random.nextInt(6);
				//Must be less than 299 testing rows, BUT ALSO must catch extra ones so rowCounter not greater than 1495
				if (randomNum == 4 && (testingCounter < testingRows) || (rowCounter >= 1495)) {
					// This is where we'll do stuff with the testing data
					String[] recipes = line.split(",");
					for (String ingr : recipes) {
						if (ingredients.containsKey(ingr)) {
							int index = ingredients.get(ingr);
							//System.out.println("row: " + rowCounter + ", col: " + index);
							testing[testingCounter][index] = 1;
						}
					}
					testingCounter++;
				} else {
					String[] recipes = line.split(",");
					rowCuisines[rowCounter] = recipes[1]; // Also, populate rowCuisines
					if(!(cuisines.containsKey(recipes[1]))) {
						cuisines.put(recipes[1], cuisineCounter);
						cuisineCounter++;
					}
					
					for (String ingr : recipes) {
						if (ingredients.containsKey(ingr)) {
							int index = ingredients.get(ingr);
							//System.out.println("row: " + rowCounter + ", col: " + index);
							features[rowCounter][index] = 1;
						}
					}
					rowCounter++;
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
	
	
	public static void initMatrixZero() {
		// init matrix to all zeroes
		for (int row = 0; row < trainingRows; row++) {
			for (int col = 0; col < ingrNum; col++) {
				features[row][col] = 0;
			}
		}
		
		// init testing to all zeroes
		for(int row = 0; row < testingRows; row++) {
			for(int col = 0; col < ingrNum; col++) {
				testing[row][col] = 0;
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
		double output; // This output will get passed to the sigmoid function
		double delta;
		double[] weights = new double[ingrNum];

		public Perceptron() {
			output = 0;
			delta = 0;
			Random rand = new Random();
			for (int i = 0; i < ingrNum; i++) {
				weights[i] = -.05 + rand.nextDouble() * .1; // Random num between -.05 and .05
			}
		}
	}
	
	
	// Every perceptron in the hidden layer links to every OutputNode
	public static class OutputNode {
		double output; // This output will get passed to the sigmoid function
		double delta;
		double[] weights = new double[numPerceptrons];

		public OutputNode() {
			output = 0;
			delta = 0;
			Random rand = new Random();
			for (int i = 0; i < numPerceptrons; i++) {
				weights[i] = -.05 + rand.nextDouble() * .1; // Random num between -.05 and .05
			}
		}
	}
	
	
	
	

}

