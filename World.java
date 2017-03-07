import java.awt.Color;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

public class World extends JFrame {
	static int currGen;
	static ArrayList<Life> population = new ArrayList<Life>();
	static Leaf worldTree = new Leaf(-1);
	static Life[] stats = new Life[26];
	public static JFrame world;
	public static final JTextArea output = new JTextArea();

	// bananas is love bananas is life
	public static void main(String[] args) {
		System.out.println("Starting initialization.");
		initStats();
		initPopulation();
		System.out.println("Finished initialization.");
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					BufferedWriter bf = new BufferedWriter(new FileWriter("log.txt"));
					bf.write(currGen + "\n");
					int i = 0;
					for (; i < population.size(); i++) {
						Life curr = population.get(i);
						bf.write(curr.DNA + " " + curr.initSurvivability + " " + curr.currSurvivability + " "
								+ curr.breedingPercentage + " " + curr.eggs + " " + curr.startingGen + " "
								+ curr.currGen + "\n");
					}
					System.out.println("Wrote(ShutDown) " + i + " out of " + population.size());
					bf.close();
					bf = new BufferedWriter(new FileWriter("treeLog.txt"));
					writeTree(bf, worldTree, "");
					System.out.println("Updated (ShutDown) Tree Log");
					bf.close();
					for (Thread t : Thread.getAllStackTraces().keySet()) 
					{  if (t.getState()==Thread.State.RUNNABLE) 
					     t.interrupt(); 
					} 

					for (Thread t : Thread.getAllStackTraces().keySet()) 
					{  if (t.getState()==Thread.State.RUNNABLE) 
					     t.stop(); 
					}
				} catch (Exception e) {
				}
			}

		}));
		new Thread() {
			public void run() {
				while (true) {
					try {
						BufferedWriter bf = new BufferedWriter(new FileWriter("log.txt"));
						bf.write(currGen + "\n");
						int i = 0;
						for (; i < population.size(); i++) {
							Life curr = population.get(i);
							bf.write(curr.DNA + " " + curr.initSurvivability + " " + curr.currSurvivability + " "
									+ curr.breedingPercentage + " " + curr.eggs + " " + curr.startingGen + " "
									+ curr.currGen + "\n");
						}
						System.out.println("Wrote " + i + " out of " + population.size());
						bf.close();
					} catch (Exception e) {
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
		new Thread() {
			public void run() {
				while (true) {
					try {
						BufferedWriter bf = new BufferedWriter(new FileWriter("treeLog.txt"));
						writeTree(bf, worldTree, "");
						System.out.println("Updated Tree Log");
						bf.close();
					} catch (Exception e) {
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
		new Thread() {
			public void run() {
				while (true) {
					for (int i = 0; i < population.size(); i++)
						if (population.get(i).currGen != currGen)
							update(population.get(i));
					currGen++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

		createInterface();
		new Thread() {
			public void run() {
				while (true) {
					output.setText(currGen+"\n");
					printWorldTree(worldTree, "");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public static void createInterface() {
		world = new JFrame();
		world.setVisible(true);
		world.setLayout(null);
		world.setDefaultCloseOperation(EXIT_ON_CLOSE);
		world.setTitle("World Tree");
		// world.setResizable(false);
		world.getContentPane().setBackground(Color.DARK_GRAY);
		world.setBounds(0,0,Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height);
		output.setEditable(true);
		output.setVisible(true);
		JScrollPane scroll = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(30,0,world.getWidth()-60,world.getHeight());
		world.add(scroll);

	}

	public static void evolve(Life l) {
		char mutation = (char) ((int) (Math.random() * 26) + 'a');
		String newDNA = l.DNA + mutation;
		Life genes = stats[mutation - 'a'];
		double newinitSurvivability = l.initSurvivability + (genes.initSurvivability - l.initSurvivability) / 4;
		double newcurrSurvivability = newinitSurvivability;
		double newbreedingPercentage = l.breedingPercentage + (genes.breedingPercentage - l.breedingPercentage) / 4;
		int neweggs = (int) (l.eggs + Math.floor((genes.eggs - l.eggs) / 3));
		int newstartingGen = getGen(newDNA);
		int newcurrGen = currGen;
		Life newChild = new Life(newDNA, newinitSurvivability, newcurrSurvivability, newbreedingPercentage, neweggs,
				newstartingGen, newcurrGen);
		populate(newChild);
	}

	public static void breed(Life l) {
		int r = (int) (Math.random() * l.eggs) + 1;
		for (int i = 0; i < r; i++) {
			if (Math.random() < 0.021)
				evolve(l);
			else {
				Life newChild = new Life(l.DNA, l.initSurvivability, l.currSurvivability, l.breedingPercentage, l.eggs,
						l.startingGen, currGen);
				populate(newChild);
			}
		}
	}

	public static void kill(Life l) {
		updateTree(l, -1);
		population.remove(l);
	}

	public static void update(Life l) {
		if (Math.random() > l.currSurvivability)
			kill(l);
		else if (Math.random() >= l.breedingPercentage)
			breed(l);
		l.currSurvivability -= 0.01;
	}

	public static void populate(Life l) {
		int i = 0;
		for (; i < population.size(); i++) {
			if (compare(population.get(i).DNA, l.DNA)) {
				population.add(i, l);
				break;
			}
		}
		if (i == population.size())
			population.add(l);
		updateTree(l, 1);
	}

	public static boolean compare(String s1, String s2) {
		for (int i = 0; i < s1.length(); i++) {
			if (i == s2.length())
				return true;
			if (s2.charAt(i) < s1.charAt(i))
				return true;
			if (s1.charAt(i) < s2.charAt(i))
				return false;
		}
		return false;
	}

	// public static void initTree() {
	// Leaf curr = worldTree;
	// for (int i = 0; i < population.size(); i++) {
	// String dna = population.get(i).DNA;
	// curr=worldTree;
	// int j = 0;
	// for (; j < dna.length() - 1; j++) {
	// curr = curr.branches[dna.charAt(j)- 'a'];
	// }
	// try{
	// if (curr.branches[dna.charAt(j) - 'a'] == null)
	// curr.branches[dna.charAt(j) - 'a'] = new
	// Leaf(population.get(i).startingGen);
	// else
	// curr.branches[dna.charAt(j) - 'a'].count++;}
	// catch(NullPointerException e){
	// System.out.println(dna+" "+dna.charAt(j));
	// curr.branches[dna.charAt(j) - 'a'] = new
	// Leaf(population.get(i).startingGen);
	// }
	// }
	// }

	public static void initTree() {
		Leaf curr = worldTree;
		try {
			Scanner in = new Scanner(new File("treeLog.txt"));
			while (in.hasNext()) {
				String l[] = in.nextLine().split(" ");
				curr = worldTree;
				int j = 0;
				String dna = l[0];
				for (; j < dna.length() - 1; j++) {
					curr = curr.branches[dna.charAt(j) - 'a'];
				}
				try {
					if (curr.branches[dna.charAt(j) - 'a'] == null)
						curr.branches[dna.charAt(j) - 'a'] = new Leaf(Integer.parseInt(l[1]), Integer.parseInt(l[2]));
					else
						curr.branches[dna.charAt(j) - 'a'].count++;
				} catch (NullPointerException e) {
					System.out.println(dna + " " + dna.charAt(j));
					curr.branches[dna.charAt(j) - 'a'] = new Leaf(Integer.parseInt(l[1]), Integer.parseInt(l[2]));
				}
			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void updateTree(Life child, int add) {
		Leaf curr = worldTree;
		String dna = child.DNA;
		int j = 0;
		for (; j < dna.length() - 1; j++) {
			curr = curr.branches[dna.charAt(j) - 'a'];
		}
		try {
			if (curr.branches[dna.charAt(j) - 'a'] == null)
				curr.branches[dna.charAt(j) - 'a'] = new Leaf(child.startingGen);
			else
				curr.branches[dna.charAt(j) - 'a'].count += add;
		} catch (NullPointerException e) {
			curr.branches[dna.charAt(j) - 'a'] = new Leaf(child.startingGen);
		}
	}

	public static int getGen(String dna) {
		Leaf curr = worldTree;
		int j = 0;
		for (; j < dna.length() - 1; j++) {
			curr = curr.branches[dna.charAt(j) - 'a'];
		}
		try {
			if (curr.branches[dna.charAt(j) - 'a'] == null)
				return currGen;
			else
				return curr.branches[dna.charAt(j) - 'a'].gen;
		} catch (NullPointerException e) {
			return currGen;
		}
	}

	public static void initPopulation() {
		try {
			Scanner in = new Scanner(new File("log.txt"));
			currGen = Integer.parseInt(in.nextLine());
			while (in.hasNext()) {
				String info[] = in.nextLine().split(" ");
				population.add(new Life(info[0], Double.parseDouble(info[1]), Double.parseDouble(info[2]),
						Double.parseDouble(info[3]), Integer.parseInt(info[4]), Integer.parseInt(info[5]),
						Integer.parseInt(info[6])));
			}
			initTree();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void initStats() {
		try {
			Scanner in = new Scanner(new File("stats"));
			for (int i = 0; i < 26; i++) {
				String info[] = in.nextLine().split(" ");
				stats[i] = (new Life(info[0], Double.parseDouble(info[1]), Double.parseDouble(info[2]),
						Double.parseDouble(info[3]), Integer.parseInt(info[4]), Integer.parseInt(info[5]),
						Integer.parseInt(info[6])));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void writeTree(BufferedWriter bf, Leaf l, String s) {
		if (l == null)
			return;
		for (int i = 0; i < l.branches.length; i++) {
			if (l.branches[i] != null)
				try {
					bf.write(s + (char) (i + 'a') + " " + l.branches[i].gen + " " + l.branches[i].count + "\n");
					writeTree(bf, l.branches[i], s + (char) (i + 'a'));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public static void printWorldTree(Leaf l, String s) {
		if (l == null)
			return;
		for (int i = 0; i < l.branches.length; i++) {
			if (l.branches[i] != null)
				output.setText(output.getText()+ (s + (char) (i + 'a') + " " + l.branches[i].gen + " " + l.branches[i].count + "\n"));
			printWorldTree(l.branches[i], s + (char) (i + 'a'));

		}
	}
}
