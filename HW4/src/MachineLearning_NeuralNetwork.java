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

	static Map<String, Integer> ingredients = new HashMap<String, Integer>();
	static Map<String, Integer> cuisines = new HashMap<String, Integer>();
	static int indexCounter = 1;

	// Rows = 5/6 of total data. This is the training set. the remaining is used
	// for testing
	// Columns = # of features (ingredients)
	static int trainingRows = 1495;
	static int testingRows = 299;
	static int ingrNum = 2398;
	static int numPerceptrons = 50;
	static int numOutput = 20;

	static double learningRate = 0.2;
	static double sumErrors = 0;
	static double trainingCorrect = 0;

	static double[][] features = new double[trainingRows][ingrNum + 1];
	static double[][] testing = new double[testingRows][ingrNum + 1];
	static ArrayList<Perceptron> hiddenLayer = new ArrayList<Perceptron>();
	static ArrayList<OutputNode> outputLayer = new ArrayList<OutputNode>(); // Each
																			// output
																			// is
																			// a
																			// cuisine
	static double[] sigmoidOutputs = new double[numPerceptrons + 1];
	static double[] correctOutput = new double[numOutput];

	// This array holds the type of cuisine for each row of our features matrix
	static String[] rowCuisines = new String[trainingRows];
	static String[] testCuisines = new String[testingRows];

	// bounds for the testing set
	static int lowTestBound = 1495;
	static int highTestBound = 1794;
	
	static double[] deltaMatrix = new double[20];

	public static void main(String[] args) {
		// Run through ingredients and store them in a hashmap
		populateIngredientsMap();

		// Initialize features and testing matrix to all zeroes
		initMatrixZero();

		// Run through recipes and set up values in features matrix (1 if
		// contains ingr, 0 otherwise)
		initFeaturesMatrix();

		// Init the hidden layer and output layer with random weights
		initHiddenLayer();
		initOutputLayer();

		// testRecipe is an individual recipe example
		Matrix featureMatrix = new Matrix(features);
		Matrix testRecipe = new Matrix(1, trainingRows, 0);

		// Number of times we use the training set to test our system
		for (int AGAIN = 0; AGAIN < 50; AGAIN++) {

			// Reset sumErrors
			sumErrors = 0;
			trainingCorrect = 0;
			
//			for (int n = 0; n<20; n++) {
//				//System.out.println(outputLayer.get(n).weights.toString());
//				for (double x : outputLayer.get(n).weights) {
//					System.out.print(x);
//				}
//				System.out.println();
//			}
//			System.out.println();
			

			// Iterating through each training row to "TEACH" our network
			for (int i = 0; i < trainingRows; i++) {

				// Get recipe for this row into matrix format to multiply
				testRecipe = featureMatrix.getMatrix(i, i, 0, ingrNum);

				// Forward Propogation
				fowardPropogation(testRecipe);

				// Put correct cuisine value into correctOutput as 0.9
				correctOutput[cuisines.get(rowCuisines[i])] = 0.9;

				// Calculating for the delta output layer
				calculateOutputDeltas(correctOutput);
				

				// Calculating the deltas for the hidden layer
				calculateHiddenDeltas();
				

				// Now we are updating the weights for the output layer
				for (int c = 0; c < numOutput; c++) {
					for (int j = 0; j <= numPerceptrons; j++) {
						//System.out.println("old weight: " + outputLayer.get(c).weights[j]);
						outputLayer.get(c).weights[j] += outputLayer.get(c).delta * -1 * learningRate
								* sigmoidOutputs[j];
						
//						System.out.println("delta: " + outputLayer.get(c).delta);
//						System.out.println("learning rate: " + learningRate );
//						System.out.println("output: " + sigmoidOutputs[j]);
//						System.out.println("new weight = " + outputLayer.get(c).weights[j]);
//						System.out.println();
					
					}
				}

				// Now we are updating the weights for the hidden layer
				for (int h = 0; h < numPerceptrons; h++) {
					for (int j = 0; j <= ingrNum; j++) {
						hiddenLayer.get(h).weights[j] += hiddenLayer.get(h).delta * -1 * learningRate * features[i][j];
					}
				}
				// Compare Outputs
				int bestOption = -1;
				double maxVal = 0;
				for (int j = 0; j < numOutput; j++) {
					if (outputLayer.get(j).output > maxVal) {
						bestOption = j;
						maxVal = outputLayer.get(j).output;
						//System.out.println(outputLayer.get);
					}
				}
				if (cuisines.get(rowCuisines[i]) == bestOption) {
					trainingCorrect++;
				}
			}
			sumErrors /= trainingRows;
			System.out.println("Output Error " + AGAIN + " : " + sumErrors);
			System.out.println("Number of Correct Predictions " + AGAIN + " : " + trainingCorrect);
		}

		// Now we have trained the Neural Network... Lets Test!
		int correctTests = testing();
		System.out.println(correctTests);
	}
	
	// Testing with the training set
//	public static int testing2(Matrix featureMatrix) {
//		Matrix testRecipe;
//		int correctTests = 0;
//		for (int i = 0; i < trainingRows; i++) {
//
//			// Get recipe for this row into matrix format to multiply
//			testRecipe = featureMatrix.getMatrix(i, i, 0, ingrNum);
//			System.out.println(Arrays.deepToString(testRecipe.getArray()));
//
//			fowardPropogation(testRecipe);
//
//			for (int x = 0; x < 20; x++) {
//				System.out.println(outputLayer.get(x).output);
//			}
//			System.out.println();
//
//			// Compare Outputs
//			int bestOption = -1;
//			double maxVal = 0;
//			for (int j = 0; j < numOutput; j++) {
//				if (outputLayer.get(j).output > maxVal) {
//					bestOption = j;
//					maxVal = outputLayer.get(j).output;
//					// System.out.println(outputLayer.get);
//				}
//			}
//			
//			System.out.println("Real: " + rowCuisines[i]);
//			System.out.println("Best: " + bestOption);
//			System.out.println();
//			
//			if (cuisines.get(rowCuisines[i]) == bestOption) {
//				correctTests++;
//			}
//		}
//		
//		return correctTests;
//	}

	
	public static int testing() {
		
		Matrix fullMatrix = new Matrix(testing);
		Matrix singleTestRecipe = new Matrix(1, testingRows, 0);

		int correctTests = 0;

		for (int i = 0; i < testingRows; i++) {
			// Get recipe for this row into matrix format to multiply
			singleTestRecipe = fullMatrix.getMatrix(i, i, 0, ingrNum);
			
//			System.out.println(Arrays.deepToString(singleTestRecipe.getArray()));
//			System.out.println();
			
			//System.out.println(Arrays.deepToString(singleTestRecipe.getArray()));

			// Forward Propogation
			fowardPropogation(singleTestRecipe);
			
//			for (int x = 0; x <20; x++) {
//				System.out.println(outputLayer.get(x).output);
//			}
//			System.out.println();
			

			// Compare Outputs
			int bestOption = -1;
			double maxVal = 0;
			for (int j = 0; j < numOutput; j++) {
				if (outputLayer.get(j).output > maxVal) {
					bestOption = j;
					maxVal = outputLayer.get(j).output;
					//System.out.println(outputLayer.get);
				}
			}
			
			
//			System.out.println("best option " + bestOption);
//			System.out.println("Best: " + outputLayer.get(bestOption).output);
//			System.out.println("real answer: " + cuisines.get(testCuisines[i]));
//			System.out.println();
//			System.out.println();

//			System.out.println("correct option " + cuisines.get(testCuisines[i]) );
//			System.out.println("what we get: " + bestOption);
//			System.out.println();
			if (cuisines.get(testCuisines[i]) == bestOption) {
				correctTests++;
			}
		}
		return correctTests;
	}

	public static void fowardPropogation(Matrix testRecipe) {

		// Initialize First Input to Output Layer as 1
		sigmoidOutputs[0] = 1;

		// For Each Perceptron, multiply their weights by the incoming recipe
		for (int j = 0; j < numPerceptrons; j++) {
			Matrix currentWeight = new Matrix(hiddenLayer.get(j).weights, 1);

			// HiddenLayer output calculation
			Matrix output = testRecipe.times((currentWeight.transpose()));
			String outputString = Arrays.deepToString(output.getArray());
			hiddenLayer.get(j).output = Double.parseDouble(outputString.substring(2, outputString.length() - 2));
			
			// Sigmoid Function from output
			sigmoidOutputs[j + 1] = 1 / (1 + Math.pow(Math.E, hiddenLayer.get(j).output));
			hiddenLayer.get(j).output = 1 / (1 + Math.pow(Math.E, hiddenLayer.get(j).output));
			
		}
		//System.out.println();

		Matrix sigmoidMatrix = new Matrix(sigmoidOutputs, 1);

		for (int k = 0; k < numOutput; k++) {
			Matrix outputWeight = new Matrix(outputLayer.get(k).weights, 1);

			// HiddenLayer output calculation
			Matrix output = sigmoidMatrix.times((outputWeight.transpose()));
			String outputString = Arrays.deepToString(output.getArray());
			outputLayer.get(k).output = Double.parseDouble(outputString.substring(2, outputString.length() - 2));
			// Sigmoid Calculation
			outputLayer.get(k).output = 1 / (1 + Math.pow(Math.E, outputLayer.get(k).output));
			//System.out.println(outputLayer.get(k).output + " ");

			// Populate each "0" value of correctOutput
			correctOutput[k] = 0.1;
		}
	//System.out.println();
//		
//		System.out.println(Arrays.toString(deltaMatrix));
//		System.out.println();
	}

	public static void calculateOutputDeltas(double[] correctOutput) {
		double totalError = 0;
		for (int c = 0; c < numOutput; c++) {
			double Ok = outputLayer.get(c).output;
			// ERROR CALCULATION
			totalError += Math.pow(correctOutput[c] - Ok, 2);
			outputLayer.get(c).delta = Ok * (1 - Ok) * (correctOutput[c] - Ok);
			
			deltaMatrix[c] = outputLayer.get(c).delta;
			
			//System.out.println(correctOutput[c] + "        " + Ok);
		}
		//System.out.println();

		// totalError /= 20;
		sumErrors += totalError;
		//System.out.println("First Error: " + totalError);
		
	}

	public static void calculateHiddenDeltas() {
		for (int i = 0; i < numPerceptrons; i++) {
			double sum = 0;
			for (int j = 0; j < numOutput; j++) {
				sum += outputLayer.get(j).weights[i] * outputLayer.get(j).delta;
			}
			hiddenLayer.get(i).delta = hiddenLayer.get(i).output * (1 - hiddenLayer.get(i).output) * sum;
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
		for (int i = 0; i < numOutput; i++) {
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
		 * If the hashmap contains the current ingredient, then look up where
		 * the index of that ingredient by doing hashmap.get(ingredient) (return
		 * index) and set that index in the features matrix to 1. Each row
		 * corresponds to a recipe, 1 = contains ingredient, 0 = doesn't contain
		 * ingredient. go row by row.
		 */
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// Gets randomNum and if it is 4 (approx 1/6 chance) then we put
				// in testing matrix
				// If not then it goes in normal features matrix
				randomNum = random.nextInt(6);
				// Must be less than 299 testing rows, BUT ALSO must catch extra
				// ones so rowCounter not greater than 1495
				// Split into testing and training sets
				if (randomNum == 4 && (testingCounter < testingRows) || (rowCounter >= 1495)) {
					// This is where we'll do stuff with the testing data
					String[] recipes = line.split(",");
					testCuisines[testingCounter] = recipes[1];
					for (String ingr : recipes) {
						if (ingredients.containsKey(ingr)) {
							int index = ingredients.get(ingr);
							// System.out.println("row: " + rowCounter + ", col:
							// " + index);
							testing[testingCounter][index] = 1;
						}
					}
					testingCounter++;
				} else {
					String[] recipes = line.split(",");
					rowCuisines[rowCounter] = recipes[1]; // Also, populate
															// rowCuisines
					if (!(cuisines.containsKey(recipes[1]))) {
						cuisines.put(recipes[1], cuisineCounter);
						cuisineCounter++;
					}

					for (String ingr : recipes) {
						if (ingredients.containsKey(ingr)) {
							int index = ingredients.get(ingr);
							// System.out.println("row: " + rowCounter + ", col:
							// " + index);
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
			features[row][0] = 1;
			for (int col = 1; col <= ingrNum; col++) {
				features[row][col] = 0;
			}
		}

		// init testing to all zeroes
		for (int row = 0; row < testingRows; row++) {
			testing[row][0] = 1;
			for (int col = 1; col <= ingrNum; col++) {
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
		double[] weights = new double[ingrNum + 1];

		public Perceptron() {
			output = 0;
			delta = 0;
			Random rand = new Random();
			for (int i = 0; i < ingrNum + 1; i++) {
				weights[i] = -.05 + rand.nextDouble() * .1; // Random num
															// between -.05 and
															// .05
			}
		}
	}

	// Every perceptron in the hidden layer links to every OutputNode
	public static class OutputNode {
		double output; // This output will get passed to the sigmoid function
		double delta;
		double[] weights = new double[numPerceptrons + 1];

		public OutputNode() {
			output = 0;
			delta = 0;
			Random rand = new Random();
			for (int i = 0; i < numPerceptrons + 1; i++) {
				weights[i] = -.05 + rand.nextDouble() * .1; // Random num
															// between -.05 and
															// .05
			}
		}
	}
}