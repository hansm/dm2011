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

public class dbc extends Canvas
{
	
	public final static dbc canvas = new dbc();
	private static JPanel panelDcbor;
	private static JPanel panelSnn;
	
	private static Checkbox chkAirbrush;
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
	private static JSpinner snnCore;
	private static JSpinner snnNoise;
	private static JSpinner snnLink;
	
	public static String freqtable = "";

	private static JSlider sliderAirbrush;
	private static Label lblAirbrushsize; 
	
    public dbc()
    {
    }
    
    // Clustering algorithms
    // Type P: x, y, cluster (default -1)
    // ArrayList<P> points
    public static void clustering1() {
//    	kontroll.setText("DBSCAN algorithm: running");
    	ClusteringAlgorithm dbscan = new DBSCAN(points, (Integer) dbscanMinpts.getValue(), (Double) dbscanEps.getValue());
    	try {
			int clusters = dbscan.run();
		} catch (AlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    	kontroll.setText("DBSCAN algorithm: finished");
    }
    
    public static void clustering2() {
//    	kontroll.setText("DCBOR algorithm: running");
    	ClusteringAlgorithm dcbor = new DCBOR(points, (Double) dcborEps.getValue());
    	try {
			int clusters = dcbor.run();
			if (clusters > 0) dcborFreqtable.setText(freqtable);
		} catch (AlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    	kontroll.setText("DCBOR algorithm: finished");
    }
    
    public static void clustering3() {
//    	kontroll.setText("SNN algorithm: running");
    	ClusteringAlgorithm algorithm = new SNN(points, (Integer) snnK.getValue(), (Integer) snnCore.getValue(), (Integer) snnNoise.getValue(), (Integer) snnLink.getValue());
    	try {
    		int clusters = algorithm.run();
//    		kontroll.setText("SNN algorithm: finished");
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
//    		kontroll.setText(e.getMessage());
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
    
    public static void klassifitseeri() {
    	for (DataPoint point : points) {
    		point.cluster = (int)(Math.floor(point.x / 125) + 4 * Math.floor(point.y / 125));
    	}
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
    	case 4:
    		klassifitseeri();
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
     * @wbp.parser.entryPoint
     */
    @SuppressWarnings("deprecation")
	public static void main(String[] args)
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
        canvas.setBounds(367, 43, 500, 500);
    	
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		if (chkAirbrush.getState()) {
        			for (int i = 0; i < sliderAirbrush.getValue(); i++) {
        				Point tmp = e.getPoint();
        				tmp.x = tmp.x + (int)(Math.random()*6*sliderAirbrush.getValue()-3*sliderAirbrush.getValue());
        				tmp.y = tmp.y + (int)(Math.random()*6*sliderAirbrush.getValue()-3*sliderAirbrush.getValue());
        				if (tmp.x > 0 && tmp.x < 500 && tmp.y > 0 && tmp.y < 500) addPoint(tmp);
        			}
        		} else {
        			addPoint(e.getPoint());
        		}
    			canvas.repaint();
        	}
        	
        });
        JFrame frmDensitybasedClustering = new JFrame();
        frmDensitybasedClustering.setIconImage(Toolkit.getDefaultToolkit().getImage(dbc.class.getResource("/images/icon-logo.gif")));
        frmDensitybasedClustering.setTitle("Density-based clustering");
        frmDensitybasedClustering.setResizable(false);
        frmDensitybasedClustering.setSize(1000, 600);
        frmDensitybasedClustering.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmDensitybasedClustering.getContentPane().setLayout(null);
        frmDensitybasedClustering.getContentPane().add(canvas);
        
        JPanel panelCanvas = new JPanel();
        panelCanvas.setBounds(362, 38, 510, 510);
        panelCanvas.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        frmDensitybasedClustering.getContentPane().add(panelCanvas);
        panelCanvas.setLayout(null);
        
        JPanel panelInput = new JPanel();
        panelInput.setBounds(15, 163, 327, 76);
        panelInput.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        frmDensitybasedClustering.getContentPane().add(panelInput);
        panelInput.setLayout(null);
        
        JButton btnOpenFile = new JButton("Open");
        btnOpenFile.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-open.gif")));
        btnOpenFile.setBounds(114, 41, 91, 23);
        panelInput.add(btnOpenFile);
        
        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(215, 41, 91, 23);
        panelInput.add(btnClear);
        btnClear.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-delete.gif")));
        
        chkAirbrush = new Checkbox("Airbrush");
        chkAirbrush.setBounds(10, 10, 77, 22);
        panelInput.add(chkAirbrush);
        
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
        btnSaveFile.setBounds(10, 41, 91, 23);
        panelInput.add(btnSaveFile);
        
        sliderAirbrush = new JSlider();
        sliderAirbrush.addMouseMotionListener(new MouseMotionAdapter() {
        	@Override
        	public void mouseDragged(MouseEvent arg0) {
        		lblAirbrushsize.setText("Size: " + Integer.toString(sliderAirbrush.getValue()));
        	}
        });
        
        
        sliderAirbrush.setValue(5);
        sliderAirbrush.setMinimum(2);
        sliderAirbrush.setMaximum(8);
        sliderAirbrush.setBounds(157, 10, 160, 24);
        panelInput.add(sliderAirbrush);
        
        lblAirbrushsize = new Label("Size: 5");
        lblAirbrushsize.setBounds(104, 10, 47, 22);
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
        
        JButton btnTestClustering = new JButton("ClusterTest");
        btnTestClustering.setBounds(5, 0, 307, 23);
        frmDensitybasedClustering.getContentPane().add(btnTestClustering);
        btnTestClustering.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        btnTestClustering.setHorizontalAlignment(SwingConstants.LEFT);
        
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
        
        JLabel lbldcborEps = new JLabel("Eps:");
        lbldcborEps.setBounds(10, 15, 81, 14);
        panelDcbor.add(lbldcborEps);
        
        dcborEps = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.01));
        dcborEps.setBounds(83, 11, 63, 20);
        panelDcbor.add(dcborEps);
        
        dcborFreqtable = new TextArea("", 0, 0, dcborFreqtable.SCROLLBARS_VERTICAL_ONLY);
        dcborFreqtable.setBounds(154, 69, 163, 185);
        panelDcbor.add(dcborFreqtable);
        
        panelSnn = new JPanel();
        tabbedPane.addTab("SNN", new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")), panelSnn, null);
        tabbedPane.setEnabledAt(2, true);
        panelSnn.setLayout(null);
        
        JButton btnClustering3 = new JButton("SNN");
        btnClustering3.setBounds(10, 70, 307, 23);
        panelSnn.add(btnClustering3);
        btnClustering3.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        
        JLabel lblsnnK = new JLabel("K:");
        lblsnnK.setBounds(10, 15, 81, 14);
        panelSnn.add(lblsnnK);
        
        JLabel lblsnnCore = new JLabel("Core threshold:");
        lblsnnCore.setBounds(12, 43, 81, 14);
        panelSnn.add(lblsnnCore);
        
        snnCore = new JSpinner();
        snnCore.setBounds(103, 39, 42, 20);
        panelSnn.add(snnCore);
        snnCore.setModel(new SpinnerNumberModel(new Integer(5), new Integer(0), null, new Integer(1)));
        
        snnK = new JSpinner();
        snnK.setBounds(103, 11, 42, 20);
        panelSnn.add(snnK);
        snnK.setModel(new SpinnerNumberModel(new Integer(5), new Integer(0), null, new Integer(1)));
        
        JLabel lblsnnNoise = new JLabel("Noise threshold:");
        lblsnnNoise.setBounds(176, 15, 81, 14);
        panelSnn.add(lblsnnNoise);
        
        JLabel lblsnnLink = new JLabel("Link threshold:");
        lblsnnLink.setBounds(176, 43, 81, 14);
        panelSnn.add(lblsnnLink);
        
        snnNoise = new JSpinner();
        snnNoise.setBounds(267, 11, 42, 20);
        panelSnn.add(snnNoise);
        snnNoise.setModel(new SpinnerNumberModel(new Integer(3), new Integer(0), null, new Integer(1)));
        
        snnLink = new JSpinner();
        snnLink.setBounds(267, 39, 42, 20);
        panelSnn.add(snnLink);
        snnLink.setModel(new SpinnerNumberModel(new Integer(2), new Integer(0), null, new Integer(1)));
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
        btnTestClustering.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		clustering(4);
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
