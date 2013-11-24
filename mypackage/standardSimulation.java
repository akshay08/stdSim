package mypackage;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;

import mypackage.QGate;
import mypackage.Complex;

public class standardSimulation {

	static int n;
	static int size;
	static Complex state[];

	public static final Complex one = new Complex(1, 0);
	public static final Complex zero = new Complex(0, 0);
	public static final Complex iota = new Complex(0, 1);
	public static final Complex minusOne = new Complex(-1, 0);

	public static final Complex I[][] = { { one, one }, { one, one } };
	public static final Complex H[][] = { { one, one }, { one, minusOne } };
	public static final Complex C[][] = { { one, zero, zero, zero },
			{ zero, one, zero, zero }, { zero, zero, zero, one },
			{ zero, zero, one, zero } };
	public static final Complex S[][] = { { one, zero }, { zero, iota } };

	public static void main(String args[]) {

		// Input circuit
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(
							"Input.txt"));
			n = Integer.parseInt(br.readLine().trim()); // number of qubits

			// String line1 = "H(1), H(2), H(3)";
			// String line2 = "C(1-3), S(2)";

			// ArrayList<String> lines = new ArrayList<String>();
			// lines.add(line1);
			// lines.add(line2);

			// initial state matrix 2^n X 1
			size = (int) Math.pow(2.0, (double) n);
			state = new Complex[size];
			for (int i = 0; i < size; i++) {
				if (i == 0) {
					state[i] = one;
				} else {
					state[i] = zero;
				}
			}

			//

			ArrayList<String> gatePos = new ArrayList<String>();
			String line;
			// displayState();
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ",");

				while (st.hasMoreElements()) {
					gatePos.add(st.nextElement().toString().toUpperCase()
							.trim());
				}
				System.out.println("\n" + gatePos);

				ArrayList<QGate> gates = new ArrayList<QGate>();
				for (int i = 0; i < n; i++) {
					gates.add(i, QGate.I);
				}

				for (String gate : gatePos) {
					if (gate.startsWith(QGate.H.name())) {
						String temp = gate.substring(gate.indexOf("(") + 1,
								gate.indexOf(")"));
						gates.remove(Integer.parseInt(temp) - 1);
						gates.add(Integer.parseInt(temp) - 1, QGate.H);
					}
					if (gate.startsWith(QGate.S.name())) {
						String temp = gate.substring(gate.indexOf("(") + 1,
								gate.indexOf(")"));
						gates.remove(Integer.parseInt(temp) - 1);
						gates.add(Integer.parseInt(temp) - 1, QGate.S);
					}
					if (gate.startsWith(QGate.C.name())) {
						String t = gate.substring(gate.indexOf("(") + 1,
								gate.indexOf(")"));
						Integer c[] = new Integer[2];
						c[0] = (Integer
								.parseInt(t.substring(0, t.indexOf("-"))));
						gates.remove(c[0] - 1);
						gates.add(c[0] - 1, QGate.C1);
						c[1] = (Integer
								.parseInt(t.substring(t.indexOf("-") + 1)));
						gates.remove(c[1] - 1);
						gates.add(c[1] - 1, QGate.C2);
					}
				}

				displayState();
				state = getFinalState(gates);

				gates.removeAll(gates);
				gatePos.removeAll(gatePos);
				// display the state vector
				displayState();
				state = normalizeState(state);
				displayState();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File Not Found!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static Complex[] getFinalState(ArrayList<QGate> gates) {
		Complex mat[][] = new Complex[1][1];
		mat[0][0] = one;
		/*switch (gates.get(0).toString().charAt(0)) {
		case 'H':
			mat = H;
			break;
		case 'S':
			mat = S;
		default:
			mat = I;
		}*/

		System.out.print("\n");

		for (int i = 0; i < n; i++) {
			QGate gate = gates.get(i);
			if (gate == QGate.H) {
				mat = matrixTensor(mat, H);
			} else if (gate == QGate.S) {
				mat = matrixTensor(mat, S);
			} else {
				mat = matrixTensor(mat, I);
			}
		}

		// for (int i = 0; i < mat.length; i++) {
		// for (int j = 0; j < mat[0].length; j++) {
		// System.out.print(mat[i][j] + " ");
		// }
		// System.out.println("\n");
		// }

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				state[i] =	state[i].plus(mat[i][j].times(state[j]));
			}
		}
		return state;
	}

	static Complex[][] matrixTensor(Complex[][] mat1, Complex[][] mat2) {
		int x = mat1.length * mat2.length;
		int y = mat1[0].length * mat2[0].length;
		Complex result[][] = new Complex[x][y];
		for (int i = 0; i < mat1.length; i++) {
			for (int j = 0; j < mat2.length; j++) {
				for (int k = 0; k < mat1[0].length; k++) {
					for (int l = 0; l < mat2[0].length; l++) {
						result[i * mat2.length + j][k * mat2[0].length + l] = mat1[i][k]
								.times(mat2[j][l]);
					}
				}
			}
		}
		return result;
	}

	static void displayState() {
		for (int i = 0; i < size; i++) {
			System.out.print(state[i] + " ");
		}
		System.out.println("\n");
	}

	static Complex[] normalizeState(Complex[] state) {
		double modSquare = 0.0;
		for (int i = 0; i < state.length; i++) {
			modSquare = modSquare + Math.pow(state[i].mod(), 2.0);
		}
		for (int i = 0; i < state.length; i++) {
			state[i] = state[i].times(new Complex(1.0 / (Math.sqrt(modSquare)),
					0.0));
		}
		return state;
	}
}
