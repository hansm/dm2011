package ut.sm2011;
import java.awt.*;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.BevelBorder;

import ut.sm2011.algorithms.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class dbc extends Canvas
{
	private static JTextField kontroll;
	
	public final static dbc canvas = new dbc();
	
	private static JTextField txtFilename;
	private static JLabel lblElements;
	private static JLabel lblClusters;
	private static Choice choiceClusters;
	
	private static File currentDir;
	
	public static ArrayList<DataPoint> points = new ArrayList<DataPoint>();
	public static HashSet clusters = new HashSet();
	
	public static Color[] colors = new Color[24];
	
	
	public static int dotSize = 8;
	
    public dbc()
    {
    }
    
    // Clustering algorithms
    // Type P: x, y, cluster (default -1)
    // ArrayList<P> points
    public static void clustering1() {
    	kontroll.setText("Clustering 1 algorithm: running");
    }
    
    public static void clustering2() {
    	kontroll.setText("Clustering 2 algorithm: running");
    }
    
    public static void clustering3() {
    	kontroll.setText("SNN algorithm: running");
    	ClusteringAlgorithm algorithm = new SNN(points, 5, 3, 3, 3);
    	try {
    		int clusters = algorithm.run();
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		kontroll.setText(e.getMessage());
    	}
    }
    
    public void paint(Graphics g)
    {
    	g.setColor(Color.gray);
    	for (DataPoint point : points) {
    		if (clusters.size() == 0 || choiceClusters.getSelectedIndex() == 0 || point.cluster == Integer.parseInt(choiceClusters.getSelectedItem())-1) {
	    		if (point.cluster > -1) g.setColor(colors[point.cluster%colors.length]);
	    		g.fillOval(point.x-(int)(dotSize/2), point.y-(int)(dotSize/2), dotSize, dotSize);
	    		g.setColor(Color.gray);
    		}
    	}
    	
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
    	int[] c = {255, 192, 160, 128};
    	for (int i = 0; i < c.length; i++) {
    		colors[(0 + 6*i)] = new Color(0, 0, c[i]);
    		colors[(1 + 6*i)] = new Color(0, c[i], 0);
    		colors[(2 + 6*i)] = new Color(c[i], 0, 0);
    		colors[(3 + 6*i)] = new Color(0, c[i], c[i]);
    		colors[(4 + 6*i)] = new Color(c[i], 0, c[i]);
    		colors[(5 + 6*i)] = new Color(c[i], c[i], 0);
    	}
    }
    
    public static void countClusters() {
    	clusters.clear();
		for (DataPoint p : points) {
			clusters.add(p.cluster+1);
		}
		lblClusters.setText(Integer.toString(clusters.size()));
    }
    
    public static void clustering(int c) {
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
    	choiceClusters.removeAll();
    	choiceClusters.add("--- All ---");
    	for (Object cluster : clusters) {
    		choiceClusters.add(cluster.toString());
    	}
    	canvas.repaint();
    }
    
    public static void clearCanvas() {
    	points.clear();
    	clusters.clear();
		lblElements.setText("0");
		lblClusters.setText("0");
		choiceClusters.removeAll();
		canvas.repaint();
    }

  
    /**
     * @wbp.parser.entryPoint
     */
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
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
    			addPoint(e.getPoint());
    			canvas.repaint();
        	}
        	
        });
        canvas.setBounds(275, 40, 500, 500);
        JFrame frmDensitybasedClustering = new JFrame();
        frmDensitybasedClustering.setIconImage(Toolkit.getDefaultToolkit().getImage(dbc.class.getResource("/images/icon-logo.gif")));
        frmDensitybasedClustering.setTitle("Density-based clustering");
        frmDensitybasedClustering.setResizable(false);
        frmDensitybasedClustering.setSize(800, 600);
        frmDensitybasedClustering.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmDensitybasedClustering.getContentPane().setLayout(null);
        frmDensitybasedClustering.getContentPane().add(canvas);
        
        kontroll = new JTextField();
        kontroll.setBounds(5, 555, 246, 20);
        frmDensitybasedClustering.getContentPane().add(kontroll);
        kontroll.setColumns(10);
        
        JButton btnClear = new JButton("Clear");
        btnClear.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-delete.gif")));
        btnClear.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		clearCanvas();
        	}
        });
        btnClear.setBounds(24, 428, 227, 23);
        frmDensitybasedClustering.getContentPane().add(btnClear);
        
        JPanel panelStat = new JPanel();
        panelStat.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panelStat.setBounds(24, 462, 227, 75);
        frmDensitybasedClustering.getContentPane().add(panelStat);
        panelStat.setLayout(null);
        
        JPanel panelCanvas = new JPanel();
        panelCanvas.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        panelCanvas.setBounds(270, 35, 510, 510);
        frmDensitybasedClustering.getContentPane().add(panelCanvas);
        panelCanvas.setLayout(null);
        
        JPanel panelInput = new JPanel();
        panelInput.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        panelInput.setBounds(24, 181, 227, 75);
        frmDensitybasedClustering.getContentPane().add(panelInput);
        panelInput.setLayout(null);
        
        txtFilename = new JTextField();
        txtFilename.setBounds(10, 11, 207, 20);
        panelInput.add(txtFilename);
        txtFilename.setColumns(10);
        
        JButton btnOpenFile = new JButton("Open CSV File");
        btnOpenFile.setHorizontalAlignment(SwingConstants.LEFT);
        btnOpenFile.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-open.gif")));
        btnOpenFile.setBounds(10, 40, 207, 23);
        panelInput.add(btnOpenFile);
        
        JPanel panelCluster = new JPanel();
        panelCluster.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        panelCluster.setBounds(24, 267, 227, 150);
        frmDensitybasedClustering.getContentPane().add(panelCluster);
        panelCluster.setLayout(null);
        
        JButton btnTestClustering = new JButton("ClusterTest");
        btnTestClustering.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        btnTestClustering.setHorizontalAlignment(SwingConstants.LEFT);
        btnTestClustering.setBounds(10, 11, 207, 23);
        panelCluster.add(btnTestClustering);
        
        JButton btnClustering1 = new JButton("Clustering 1");
        btnClustering1.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        btnClustering1.setHorizontalAlignment(SwingConstants.LEFT);
        btnClustering1.setBounds(10, 45, 207, 23);
        panelCluster.add(btnClustering1);
        
        JButton btnClustering2 = new JButton("Clustering 2");
        btnClustering2.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        btnClustering2.setHorizontalAlignment(SwingConstants.LEFT);
        btnClustering2.setBounds(12, 79, 205, 23);
        panelCluster.add(btnClustering2);
        
        JButton btnClustering3 = new JButton("SNN");
        btnClustering3.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        btnClustering3.setHorizontalAlignment(SwingConstants.LEFT);
        btnClustering3.setBounds(12, 113, 205, 23);
        panelCluster.add(btnClustering3);
        
        JLabel lblAuthors = new JLabel("Authors: Martin Loginov, Hans M\u00E4esalu, Sven Aller");
        lblAuthors.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAuthors.setBounds(415, 552, 363, 14);
        frmDensitybasedClustering.getContentPane().add(lblAuthors);
        
        JLabel lblHeader = new JLabel("");
        lblHeader.setIcon(new ImageIcon(dbc.class.getResource("/images/header.gif")));
        lblHeader.setBounds(10, 25, 250, 140);
        frmDensitybasedClustering.getContentPane().add(lblHeader);
        
        JLabel lblElementsLabel = new JLabel("Elements: ");
        lblElementsLabel.setBounds(339, 11, 62, 14);
        frmDensitybasedClustering.getContentPane().add(lblElementsLabel);
        
        lblElements = new JLabel("0");
        lblElements.setBounds(400, 11, 46, 14);
        frmDensitybasedClustering.getContentPane().add(lblElements);
        
        lblClusters = new JLabel("");
        lblClusters.setBounds(526, 11, 46, 14);
        frmDensitybasedClustering.getContentPane().add(lblClusters);
        
        JLabel lblClustersLabel = new JLabel("Clusters:");
        lblClustersLabel.setBounds(468, 11, 62, 14);
        frmDensitybasedClustering.getContentPane().add(lblClustersLabel);
        
        JLabel lblShowLabel = new JLabel("Show cluster");
        lblShowLabel.setBounds(582, 11, 94, 14);
        frmDensitybasedClustering.getContentPane().add(lblShowLabel);
        
        choiceClusters = new Choice();
        choiceClusters.setBounds(678, 8, 101, 20);
        frmDensitybasedClustering.getContentPane().add(choiceClusters);
        choiceClusters.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent arg0) {
//        		System.out.println(choiceClusters.getSelectedIndex());
        		canvas.repaint();
        	}
        });
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
        btnClustering1.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		clustering(1);
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
        	        	currentDir = file.getAbsoluteFile();
        	            txtFilename.setText(file.getName());
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
	        		    double changeX = 499d / maxX;
	        		    double changeY = 499d / maxY;
	        		    for (Point p : pointsfromfile) {
	        		    	p.x = (int)(p.x * changeX);
	        		    	p.y = (int)(p.y * changeY);
	        		    	points.add(new DataPoint(p));
	        		    }
	        		    lblElements.setText(Integer.toString(points.size()));
	        		    canvas.repaint();
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
