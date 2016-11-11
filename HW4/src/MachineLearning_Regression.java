import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MachineLearning_Regression {

	static Map<String, Integer> ingredients = new HashMap<String, Integer>();
	static int indexCounter = 0;

	public static void main(String[] args) {
		populateIngredientsMap();

	}
	
	
	
	
	public static void populateIngredientsMap() {
		String csvFile = "../HW4/src/training.csv";
		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// First pass through training set: Put every ingredient into an arraylist
				String[] recipe = line.split(",");
				for (int i = 2; i < recipe.length; i++) {
					if (!ingredients.containsKey(recipe[i])) {
						ingredients.put(recipe[i], indexCounter);
						indexCounter++;
					}
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

}

