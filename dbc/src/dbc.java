import java.awt.*;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.BevelBorder;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyVetoException;

public class dbc extends Canvas
{
	
	public final static dbc canvas = new dbc();
	private static JPanel panelDcbor;
	private static JPanel panelSnn;
	private static JLabel lblElements;
	private static JLabel lblClusters;
	private static List listClusters;
	
	private static File currentDir;
	
	public static ArrayList<DataPoint> points = new ArrayList<DataPoint>();
	public static HashSet clusters = new HashSet();
	
	public static Color[] colors = new Color[25];
	
	
	public static int dotSize = 8;
	private static JSpinner dbscanEps;
	private static JSpinner dbscanMinpts;
	private static JSpinner dcborEps;
	private static TextArea dcborFreqtable;
	private static JSpinner snnK;
	private static JSpinner snnMinPts;
	private static JSpinner snnNoise;
	private static JSpinner snnEps;
	
	public static String freqtable = "";

	private static JSlider sliderAirbrush;
	private static Label lblAirbrushsize; 
	
	private static JInternalFrame frameHelp;
	private static TextArea textareaHelp; 
	private static String[] helpText = new String[3];
	
    public dbc()
    {
    }
    
    // Clustering algorithms
    public static void clustering1() {
//    	kontroll.setText("DBSCAN algorithm: running");
    	ClusteringAlgorithm dbscan = new DBSCAN(points, (Integer) dbscanMinpts.getValue(), (Double) dbscanEps.getValue());
    	try {
    		if(points.isEmpty())
    			throw new AlgorithmException("Nothing to cluster!");
			int clusters = dbscan.run();
		}
    	catch (Error e){
    		JOptionPane.showMessageDialog(null, e, "An error occurred!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
    	}
    	catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e, "An error occurred!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
//    	kontroll.setText("DBSCAN algorithm: finished");
    }
    
    public static void clustering2() {
//    	kontroll.setText("DCBOR algorithm: running");
    	if (points.size() < 11){
    		JOptionPane.showMessageDialog(null, "There must be at least 11 points to use DCBOR!", "Too few points", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	ClusteringAlgorithm dcbor = new DCBOR(points, (Double) dcborEps.getValue());
    	try {
			int clusters = dcbor.run();
			if (clusters > 0) dcborFreqtable.setText(freqtable);
		} 
    	catch (Error e){
    		JOptionPane.showMessageDialog(null, e, "An error occurred!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
    	}
    	catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e, "An error occurred!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
//    	kontroll.setText("DCBOR algorithm: finished");
    }
    
    public static void clustering3() {
//    	kontroll.setText("SNN algorithm: running");
    	ClusteringAlgorithm algorithm = new SNN(points, (Integer) snnK.getValue(), (Integer) snnMinPts.getValue(), (Integer) snnEps.getValue());
    	try {
    		int clusters = algorithm.run();
//    		kontroll.setText("SNN algorithm: finished");
    	} 
    	catch (Error e){
    		JOptionPane.showMessageDialog(null, e, "An error occurred!", JOptionPane.ERROR_MESSAGE);
//			e.printStackTrace();
    	}
    	catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e, "An error occurred!", JOptionPane.ERROR_MESSAGE);
//			e.printStackTrace();
		}
    }
    
    public void paint(Graphics g)
    {
    	g.setColor(Color.gray);
    	    	
    	if (clusters.size() == 0 || listClusters.getSelectedItem() == null) {
    		for (DataPoint point : points) {
        			g.setColor(Color.gray);
    	    		g.fillOval(point.x-(int)(dotSize/2), point.y-(int)(dotSize/2), dotSize, dotSize);
    	    		g.setColor(Color.gray);
        	}
    	} else if (listClusters.getSelectedIndex() == 0) {
	    	for (DataPoint point : points) {
	    			g.setColor(colors[(point.cluster+1)%colors.length]);
		    		g.fillOval(point.x-(int)(dotSize/2), point.y-(int)(dotSize/2), dotSize, dotSize);
		    		g.setColor(Color.gray);
	    		}
    	} else {
    		int filterCluster;
    		if (listClusters.getSelectedIndex() == 1) {
    			filterCluster = 0;
    		} else {
    			filterCluster = Integer.parseInt(listClusters.getSelectedItem());
    		}
	    	for (DataPoint point : points) {
	    		g.setColor(Color.lightGray);
	    		if (point.cluster == filterCluster) g.setColor(colors[(point.cluster+1)%colors.length]);
    	    	g.fillOval(point.x-(int)(dotSize/2), point.y-(int)(dotSize/2), dotSize, dotSize);
	    		g.setColor(Color.lightGray);
	    	}
	    }
    	g.setColor(Color.gray);
    	
//    	for (int i = 0; i < colors.length; i++) {
//    		g.setColor(colors[i]);
//    		g.fillOval(20, 10+15*i, 15, 15);
//    		g.drawString(" - " + i, 40, 20+15*i);
//    		
//    	}	
    }
    
    public static void addPoint(Point p) 
    {
    	points.add(new DataPoint(p)); 
    	lblElements.setText(Integer.toString(points.size()));
//    	kontroll.setText((p.x / 50) + " " + (4 * p.y / 125) + " = " + (int)(Math.floor(p.x / 125) + 4 * Math.floor(p.y / 125)));
    }
   
    public static void createColors() {
    	colors[0] = Color.gray;
    	int[] c = {255, 192, 160, 128};
    	for (int i = 0; i < c.length; i++) {
    		colors[(1 + 6*i)] = new Color(c[i], 0, 0);
    		colors[(2 + 6*i)] = new Color(0, c[i], 0);
    		colors[(3 + 6*i)] = new Color(0, 0, c[i]);
    		colors[(4 + 6*i)] = new Color(0, c[i], c[i]);
    		colors[(5 + 6*i)] = new Color(c[i], 0, c[i]);
    		colors[(6 + 6*i)] = new Color(c[i], c[i], 0);
    	}
    }
    
    public static void countClusters() {
    	clusters.clear();
		for (DataPoint p : points) {
			if (p.getCluster() == 0) {
				continue;
			}
			clusters.add(p.getCluster());
		}
		lblClusters.setText(Integer.toString(clusters.size()));
    }
    
    public static void clustering(int c) {
    	freqtable = "";
    	dcborFreqtable.setText("");
    	switch(c) {
    	case 1:
    		clustering1();
    		break;
    	case 2:
    		clustering2();
    		break;
    	case 3:
    		clustering3();
    		break;
    	}
    	countClusters();
    	listClusters.removeAll();
    	
    	listClusters.add("--- All ---");
    	listClusters.select(0);
    	listClusters.add("Noise");
    	
    	for (Object cluster : clusters) {
    		listClusters.add(cluster.toString());
    	}
    	canvas.repaint();
    }
    
    public static void clearCanvas() {
    	points.clear();
    	clusters.clear();
		lblElements.setText("0");
		lblClusters.setText("0");
		listClusters.removeAll();
		canvas.repaint();
    }

  
    /**
     * @throws PropertyVetoException 
     * @wbp.parser.entryPoint
     */
    @SuppressWarnings("deprecation")
	public static void main(String[] args) throws PropertyVetoException
    {
    	try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	createColors();
    	helpText[0] = "DBSCAN (density-based spatial clustering of applications with noise)" +
    			"is one of the most common clustering algorithms and also most cited in scientific literature." +
    			"\nUnlike partitioning or hierarchical clustering algorithms, DBSCAN is based on density " +
    			"based notion of clusters, this means that clusters are formed in most dense regions. " +
    			"It is designed to be easy to use, requires minimal domain knowledge for clustering, " +
    			"and can discover clusters of arbitrary shape. " +
    			"\nDBSCAN needs 2 parameters as input: eps, neighborhood radius, and minPts, the " +
    			"minimum number of points required to form a cluster. DBSCAN starts with a random point " +
    			"and retrieves it’s density-reachable neighborhood. If it contains at least minPts " +
    			"neighbors then a new cluster is started, otherwise point is marked as noise. Noise " +
    			"points can be later added to other clusters. If point is part of a cluster then all " +
    			"points in its neighborhood are also marked as part of this cluster. This continues until " +
    			"the whole cluster is found, then a new unvisited point is taken and processed like " +
    			"previous points." +
    			"\nDBSCAN does not require user to know the number of clusters beforehand. It can also " +
    			"find clusters of different shapes and has notion of noise."+
    			"\n"+
    			"\n"+
    			"PARAMETERS\n"+
    			"    eps - the radius of the neighborhood. This radius determines how distant points are " +
    			"checked for each datapoint's density-reachable neighborhood." +
    			"\n" +
    			"\n    minPts - the minimum number of points in a datapoints's density-neighborhood required " +
    			"to form a new cluster.";
    	helpText[1] = "DCBOR stands for Density Clustering Based on Outlier Removal. " +
    			"It is an enhanced version of the well known single link clustering algorithm. " +
    			"This algorithm provides outlier detection and data clustering simultaneously. " +
    			"The algorithm consists of two phases. During the first phase, it finds the k-nearest " +
    			"neighbors of every datapoint and removes the outliers from the data set. During the second " +
    			"phase, it uses the single link algorithm with simple modification to discover the genuine " +
    			"clusters.\n" +
    			"The whole algorithm is based on the notion of local density of datapoints." +
    			"This local density is calculated for each datapoint and is basically the sum " +
    			"of this datapoint's Euclidean distances to it's k-nearest neighbors (k=10 in this " +
    			"implementation).\n" +
    			"After this an outlier factor is calculated for each datapoint, which is that datapoint's " +
    			"local density divided by the local density of the lowest density datapoint in the whole set. " +
    			"Therefore it is a value between 0 and 1. All points having an outlier factor greater than " +
    			"the value inputted by the user are disgarded as noise.\n" +
    			"The clustering process is a middle ground between the single link algorithm and " +
    			"DBSCAN, since in single link two points are merged in each step, but here " +
    			"all points at distance from the current point that satisfy the threshold are " +
    			"assigned to the current cluster. We can deduce a suitable value for the " +
    			"threshold from the nearest neighbors of each leftover datapoint after removing " +
    			"the outliers. We search for the maximum distance between a point and its first " +
    			"nearest neighbor. This distance is the ideal distance for the threshold " +
    			"(level of dissimilarity between clusters) according to the main idea of the single " +
    			"link algorithm."+
    			"\n"+
    			"\n"+
    			"PARAMETERS\n" +
    			"    outlier factor - This allows the user to specify the outlier factor threshold." +
    			"All points having an outlier factor greater than this are disgarded as noise." +
    			"This allows to remove the low density points from the clustering process.\n" +
    			"The closer it's OF is to 1 the less dense a point is. The algorithm also helps the " +
    			"user in choosing a suitable OF by displaying the distribution of points in each OF " +
    			"range.";
		helpText[2] = "Shared nearest neighbor (SNN) is density based clustering algorithm designed to find clusters with different shapes, sizes, densities and in high dimensional data. SNN works similarly to DBSCAN, but it does not use Euclidean distance to define similarity and to find densities of points. Instead SNN defines similarity between points by the number of nearest neighbors these points share. For example if point p1 is close to point p2 and they are both close to a set of points S then their similarity is equal to the number of points in set S. Point density is defined as the number of points that are similar to point. This allows SNN to avoid problems with high dimensional data and also to identify clusters of different densities.\n"
				+ "\nPARAMETERS\n"
				+ "k - the neighborhood list size. If k is too small then even relatively uniform clusters will be broken up, if it is too big then smaller clusters will not be found.\n"
				+ "MinPts - core point density threshold, points that have at least MinPts similar points will be considered core points."
				+ "Eps - threshold for link strength, weaker links will be removed.";
        JFrame frmDensitybasedClustering = new JFrame();
        frmDensitybasedClustering.setIconImage(Toolkit.getDefaultToolkit().getImage(dbc.class.getResource("/images/icon-logo.gif")));
        frmDensitybasedClustering.setTitle("Density-based clustering");
        frmDensitybasedClustering.setResizable(false);
        frmDensitybasedClustering.setSize(1000, 600);
        frmDensitybasedClustering.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmDensitybasedClustering.getContentPane().setLayout(null);
        
        JPanel panelCanvas = new JPanel();
        panelCanvas.setBounds(362, 38, 510, 510);
        panelCanvas.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        frmDensitybasedClustering.getContentPane().add(panelCanvas);
        panelCanvas.setLayout(null);
        
        frameHelp = new JInternalFrame("Help");
        frameHelp.setBounds(34, 38, 437, 425);
        panelCanvas.add(frameHelp);
        frameHelp.setFrameIcon(new ImageIcon(dbc.class.getResource("/images/icon-help.gif")));
        frameHelp.setEnabled(false);
        frameHelp.setClosable(true);
        frameHelp.setVisible(false);
        frameHelp.getContentPane().setLayout(null);
        
        textareaHelp = new TextArea("", 0, 0, dcborFreqtable.SCROLLBARS_VERTICAL_ONLY);
        textareaHelp.setEditable(false);
        textareaHelp.setBounds(10, 11, 411, 378);
        frameHelp.getContentPane().add(textareaHelp);
        frameHelp.setVisible(true);
        frameHelp.setClosed(true);
        canvas.setBounds(5, 5, 500, 500);
        panelCanvas.add(canvas);
        
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
    			for (int i = 0; i < sliderAirbrush.getValue(); i++) {
    				Point tmp = e.getPoint();
    				tmp.x = tmp.x + (int)(Math.random()*6*sliderAirbrush.getValue()-3*sliderAirbrush.getValue());
    				tmp.y = tmp.y + (int)(Math.random()*6*sliderAirbrush.getValue()-3*sliderAirbrush.getValue());
    				if (tmp.x > 0 && tmp.x < 500 && tmp.y > 0 && tmp.y < 500) addPoint(tmp);
    			}
    			canvas.repaint();
        	}
        	
        });
        
        JPanel panelInput = new JPanel();
        panelInput.setBounds(15, 163, 327, 76);
        panelInput.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        frmDensitybasedClustering.getContentPane().add(panelInput);
        panelInput.setLayout(null);
        
        JButton btnOpenFile = new JButton("Open");
        btnOpenFile.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-open.gif")));
        btnOpenFile.setBounds(116, 41, 97, 23);
        panelInput.add(btnOpenFile);
        
        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(220, 41, 97, 23);
        panelInput.add(btnClear);
        btnClear.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-delete.gif")));
        
        JButton btnSaveFile = new JButton("Save");
        btnSaveFile.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-save.gif")));
        btnSaveFile.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		try {
        			JFileChooser fileChooser = new JFileChooser("");
        			if (currentDir != null) {
        				fileChooser.setCurrentDirectory(currentDir);
        			}
        			FileFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        			fileChooser.addChoosableFileFilter(filter);
        	        int returnVal = fileChooser.showSaveDialog(null);
        	        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	        	File file = fileChooser.getSelectedFile();
        	        	int result = JOptionPane.YES_OPTION;
//        	        	kontroll.setText(file.getAbsoluteFile() + ".csv");
        	        	if (new File(file.getAbsoluteFile() + ".csv").exists()) {
        	        		result = JOptionPane.NO_OPTION;
        	        		result = JOptionPane.showConfirmDialog(canvas,"The file exists! Overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION); 
        	        	} 
        	        	if (result == JOptionPane.YES_OPTION) {
	        	        	currentDir = file.getAbsoluteFile();
		        			BufferedWriter out = new BufferedWriter(new FileWriter(file + ".csv"));
		        			for (DataPoint dp : points) {
		        				out.write(dp.x + "," + dp.y + "\n");
		        			}
		        		    out.close();
	        		    }
        	        }
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
        	}
        });
        btnSaveFile.setBounds(12, 41, 97, 23);
        panelInput.add(btnSaveFile);
        
        sliderAirbrush = new JSlider();
        sliderAirbrush.setPaintTicks(true);
        sliderAirbrush.setMajorTickSpacing(1);
        sliderAirbrush.setMinorTickSpacing(1);
        sliderAirbrush.setSnapToTicks(true);
        sliderAirbrush.addMouseMotionListener(new MouseMotionAdapter() {
        	@Override
        	public void mouseDragged(MouseEvent arg0) {
        		lblAirbrushsize.setText("Number of points: " + Integer.toString(sliderAirbrush.getValue()));
        	}
        });
        
        
        sliderAirbrush.setValue(1);
        sliderAirbrush.setMinimum(1);
        sliderAirbrush.setMaximum(8);
        sliderAirbrush.setBounds(157, 10, 160, 24);
        panelInput.add(sliderAirbrush);
        
        lblAirbrushsize = new Label("Number of points: 1");
        lblAirbrushsize.setBounds(12, 10, 120, 22);
        panelInput.add(lblAirbrushsize);
        btnClear.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		clearCanvas();
        	}
        });
        
        JLabel lblAuthors = new JLabel("Authors: Martin Loginov, Hans M\u00E4esalu, Sven Aller");
        lblAuthors.setBounds(621, 549, 363, 14);
        lblAuthors.setHorizontalAlignment(SwingConstants.RIGHT);
        frmDensitybasedClustering.getContentPane().add(lblAuthors);
        
        JLabel lblHeader = new JLabel("");
        lblHeader.setBounds(48, 0, 250, 140);
        lblHeader.setIcon(new ImageIcon(dbc.class.getResource("/images/header.gif")));
        frmDensitybasedClustering.getContentPane().add(lblHeader);
        
        JLabel lblElementsLabel = new JLabel("Elements: ");
        lblElementsLabel.setBounds(880, 43, 62, 14);
        frmDensitybasedClustering.getContentPane().add(lblElementsLabel);
        
        lblElements = new JLabel("0");
        lblElements.setBounds(938, 43, 46, 14);
        frmDensitybasedClustering.getContentPane().add(lblElements);
        
        lblClusters = new JLabel("");
        lblClusters.setBounds(938, 68, 46, 14);
        frmDensitybasedClustering.getContentPane().add(lblClusters);
        
        JLabel lblClustersLabel = new JLabel("Clusters:");
        lblClustersLabel.setBounds(880, 68, 62, 14);
        frmDensitybasedClustering.getContentPane().add(lblClustersLabel);
        
        JLabel lblShowLabel = new JLabel("Show cluster");
        lblShowLabel.setBounds(880, 93, 94, 14);
        frmDensitybasedClustering.getContentPane().add(lblShowLabel);
        
        listClusters = new List();
        listClusters.setBounds(883, 113, 100, 430);
        listClusters.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		canvas.repaint();
        	}
        });
        
        listClusters.setMultipleSelections(false);
        frmDensitybasedClustering.getContentPane().add(listClusters);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(15, 250, 327, 293);
        frmDensitybasedClustering.getContentPane().add(tabbedPane);
        
        JPanel panelDbscan = new JPanel();
        tabbedPane.addTab("DBSCAN", new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")), panelDbscan, null);
        panelDbscan.setLayout(null);
        
        JButton btnClustering1 = new JButton("DBSCAN");
        btnClustering1.setBounds(10, 70, 307, 23);
        panelDbscan.add(btnClustering1);
        btnClustering1.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        
        JLabel lbldbscanEps = new JLabel("Eps:");
        lbldbscanEps.setBounds(10, 15, 81, 14);
        panelDbscan.add(lbldbscanEps);
        
        JLabel lbldbscanMinpts = new JLabel("minPts:");
        lbldbscanMinpts.setBounds(10, 43, 81, 14);
        panelDbscan.add(lbldbscanMinpts);
        
        dbscanEps = new JSpinner();
        dbscanEps.setBounds(83, 11, 63, 20);
        panelDbscan.add(dbscanEps);
        dbscanEps.setModel(new SpinnerNumberModel(new Double(10), new Double(0), null, new Double(1)));
        
        dbscanMinpts = new JSpinner();
        dbscanMinpts.setBounds(83, 39, 63, 20);
        panelDbscan.add(dbscanMinpts);
        dbscanMinpts.setModel(new SpinnerNumberModel(new Integer(5), new Integer(0), null, new Integer(1)));
        
        JButton btnDbscanHelp = new JButton("");
        btnDbscanHelp.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		textareaHelp.setText(helpText[0]);
        		frameHelp.setVisible(true);
        	}
        });
        btnDbscanHelp.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-help.gif")));
        btnDbscanHelp.setBounds(295, 0, 22, 23);
        panelDbscan.add(btnDbscanHelp);
        btnClustering1.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		clustering(1);
        	}
        });
        tabbedPane.setEnabledAt(0, true);
        
        panelDcbor = new JPanel();
        tabbedPane.addTab("DCBOR", new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")), panelDcbor, null);
        tabbedPane.setEnabledAt(1, true);
        panelDcbor.setLayout(null);
        
        JButton btnClustering2 = new JButton("DCBOR");
        btnClustering2.setBounds(10, 40, 307, 23);
        panelDcbor.add(btnClustering2);
        btnClustering2.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        
        JLabel lbldcborEps = new JLabel("Outlier factor:");
        lbldcborEps.setBounds(10, 15, 81, 14);
        panelDcbor.add(lbldcborEps);
        
        dcborEps = new JSpinner(new SpinnerNumberModel(new Double(0.5), new Double(0.0), new Double(1.0), new Double(0.01)));
        dcborEps.setBounds(83, 11, 63, 20);
        panelDcbor.add(dcborEps);
        
        dcborFreqtable = new TextArea("", 0, 0, dcborFreqtable.SCROLLBARS_VERTICAL_ONLY);
        dcborFreqtable.setBounds(10, 69, 307, 185);
        panelDcbor.add(dcborFreqtable);
        
        JButton btnDcborHelp = new JButton("");
        btnDcborHelp.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		textareaHelp.setText(helpText[1]);
        		frameHelp.setVisible(true);
        	}
        });
        btnDcborHelp.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-help.gif")));
        btnDcborHelp.setBounds(295, 0, 22, 23);
        panelDcbor.add(btnDcborHelp);
        
        panelSnn = new JPanel();
        tabbedPane.addTab("SNN", new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")), panelSnn, null);
        tabbedPane.setEnabledAt(2, true);
        panelSnn.setLayout(null);
        
        JButton btnClustering3 = new JButton("SNN");
        btnClustering3.setBounds(10, 107, 307, 23);
        panelSnn.add(btnClustering3);
        btnClustering3.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        
        JLabel lblsnnK = new JLabel("k:");
        lblsnnK.setBounds(10, 15, 81, 14);
        panelSnn.add(lblsnnK);
        
        JLabel lblsnnCore = new JLabel("MinPts:");
        lblsnnCore.setBounds(12, 43, 81, 14);
        panelSnn.add(lblsnnCore);
        
        snnMinPts = new JSpinner();
        snnMinPts.setBounds(103, 39, 42, 20);
        panelSnn.add(snnMinPts);
        snnMinPts.setModel(new SpinnerNumberModel(new Integer(7), new Integer(0), null, new Integer(1)));
        
        snnK = new JSpinner();
        snnK.setBounds(103, 11, 42, 20);
        panelSnn.add(snnK);
        snnK.setModel(new SpinnerNumberModel(new Integer(10), new Integer(0), null, new Integer(1)));
        
        JLabel lblsnnLink = new JLabel("Eps:");
        lblsnnLink.setBounds(10, 71, 81, 14);
        panelSnn.add(lblsnnLink);
        
        snnEps = new JSpinner();
        snnEps.setBounds(103, 67, 42, 20);
        panelSnn.add(snnEps);
        snnEps.setModel(new SpinnerNumberModel(new Integer(5), new Integer(0), null, new Integer(1)));
        
        JButton btnSnnHelp = new JButton("");
        btnSnnHelp.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		textareaHelp.setText(helpText[2]);
        		frameHelp.setVisible(true);
        	}
        });
        btnSnnHelp.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-help.gif")));
        btnSnnHelp.setBounds(295, 0, 22, 23);
        panelSnn.add(btnSnnHelp);
        btnClustering3.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		clustering(3);
        	}
        });
        btnClustering2.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		clustering(2);
        	}
        });
        btnOpenFile.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		try {
        			JFileChooser fileChooser = new JFileChooser("");
        			if (currentDir != null) {
        				fileChooser.setCurrentDirectory(currentDir);
        			}
        			FileFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        			fileChooser.addChoosableFileFilter(filter);
        	        int returnVal = fileChooser.showOpenDialog(null);
        	        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	            File file = fileChooser.getSelectedFile();
        	            if (!file.exists()) {
        	            	file = new File(file.getAbsoluteFile() + ".csv");
        	            }
        	            if (file.exists()) {
	        	        	currentDir = file.getAbsoluteFile();
	//        	            txtOpenFilename.setText(file.getName());
		        			clearCanvas();
		        			BufferedReader in = new BufferedReader(new FileReader(file));
		        			String line;
		        			ArrayList<Point> pointsfromfile = new ArrayList<Point>();
		        			int maxX = 0;
		        			int maxY = 0;
		        		    while ((line = in.readLine()) != null) {
		        		        String[] line1 = line.split(",");
		        		        Point newPoint = new Point(Integer.parseInt(line1[0]),Integer.parseInt(line1[1])); 
		        		        if (newPoint.x > maxX) maxX = newPoint.x;
		        		        if (newPoint.y > maxY) maxY = newPoint.y;
		        		        pointsfromfile.add(newPoint);
		        		    }
		        		    in.close();
		        		    double changeX = 1;
		        		    double changeY = 1;
		        		    if (maxX > 500 || maxY > 500) {
			        		    changeX = 499d / maxX;
			        		    changeY = 499d / maxY;
		        		    }    
		        		    for (Point p : pointsfromfile) {
		        		    	p.x = (int)(p.x * changeX);
		        		    	p.y = (int)(p.y * changeY);
		        		    	points.add(new DataPoint(p));
		        		    }
	
		        		    lblElements.setText(Integer.toString(points.size()));
		        		    canvas.repaint();
        	            } else {
        	            	JOptionPane.showMessageDialog(canvas,"Can't find the file!","Can't find the file",JOptionPane.OK_OPTION);
        	            }
        	        }
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
        	}
        });
          
        frmDensitybasedClustering.setVisible(true);
    }
}
