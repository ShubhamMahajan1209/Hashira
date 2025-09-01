import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        try {
            // Load input.json from resources
            InputStream fis = Main.class.getClassLoader().getResourceAsStream("input.json");
            if (fis == null) {
                throw new RuntimeException("input.json not found in resources!");
            }

            JSONTokener tokener = new JSONTokener(fis);
            JSONObject json = new JSONObject(tokener);

            // Extract n and k
            JSONObject keys = json.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k"); // minimum roots required

            // Prepare arrays
            double[][] A = new double[k][k];
            double[] B = new double[k];

            int row = 0;

            // Iterate over keys in JSON
            Iterator<String> iter = json.keys();
            while (iter.hasNext() && row < k) {
                String key = iter.next();
                if (key.equals("keys")) continue;

                JSONObject obj = json.getJSONObject(key);
                int base = Integer.parseInt(obj.getString("base"));
                String valueStr = obj.getString("value");

                // Use BigInteger for large numbers
                BigInteger value = new BigInteger(valueStr, base);

                int x = Integer.parseInt(key);

                // Fill matrix row
                for (int j = 0; j < k; j++) {
                    A[row][j] = Math.pow(x, j);
                }
                B[row] = value.doubleValue(); // convert BigInteger -> double

                row++;
            }

            // Solve system
            double[] coeff = gaussianElimination(A, B);

            // Print coefficients
            System.out.println("Polynomial Coefficients:");
            for (int i = 0; i < coeff.length; i++) {
                System.out.println("a" + i + " = " + coeff[i]);
            }

            // "c" is constant term a0
            System.out.println("\nConstant term (c) = " + coeff[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gaussian elimination solver
    public static double[] gaussianElimination(double[][] A, double[] B) {
        int n = B.length;

        for (int i = 0; i < n; i++) {
            // Pivoting
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[max][i])) {
                    max = j;
                }
            }

            // Swap rows
            double[] temp = A[i];
            A[i] = A[max];
            A[max] = temp;

            double t = B[i];
            B[i] = B[max];
            B[max] = t;

            // Eliminate
            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                B[j] -= factor * B[i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (B[i] - sum) / A[i][i];
        }
        return x;
    }
}
