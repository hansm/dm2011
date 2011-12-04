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
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class dbc extends Canvas
{
	private static JTextField kontroll;
	
	public final static dbc canvas = new dbc();
	
	private static JTextField txtFilename;
	private static JLabel lblElements;
	private static JLabel lblClusters;

	
	public static ArrayList<P> points = new ArrayList<P>();
	
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
    	kontroll.setText("Clustering 3 algorithm: running");
    }
    
    public void paint(Graphics g)
    {
    	g.setColor(Color.gray);
    	for (P point : points) {
    		if (point.cluster > -1) g.setColor(colors[point.cluster%colors.length]);
    		g.fillOval(point.x-(int)(dotSize/2), point.y-(int)(dotSize/2), dotSize, dotSize);
    		g.setColor(Color.gray);
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
    	points.add(new P(p)); 
    	lblElements.setText(Integer.toString(points.size()));
//    	kontroll.setText((p.x / 50) + " " + (4 * p.y / 125) + " = " + (int)(Math.floor(p.x / 125) + 4 * Math.floor(p.y / 125)));
    }
    
    public static void klassifitseeri() {
    	for (P point : points) {
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
    	HashSet hs = new HashSet();
		for (P p : points) {
			hs.add(p.cluster);
		}
		lblClusters.setText(Integer.toString(hs.size()));
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
    	canvas.repaint();
    }
    
    public static void clearCanvas() {
    	points.clear();
		lblElements.setText("0");
		lblClusters.setText("0");
		canvas.repaint();
    }
    
    
 

    
  
    /**
     * @wbp.parser.entryPoint
     */
    public static void main(String[] args)
    {
    	createColors();
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
    			addPoint(e.getPoint());
    			canvas.repaint();
        	}
        	
        });
        canvas.setBounds(273, 50, 500, 500);
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
        btnClear.setBounds(24, 408, 227, 23);
        frmDensitybasedClustering.getContentPane().add(btnClear);
        
        JPanel panelStat = new JPanel();
        panelStat.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panelStat.setBounds(24, 442, 227, 108);
        frmDensitybasedClustering.getContentPane().add(panelStat);
        panelStat.setLayout(null);
        
        JLabel lblElementsLabel = new JLabel("Elements: ");
        lblElementsLabel.setBounds(10, 11, 62, 14);
        panelStat.add(lblElementsLabel);
        
        lblElements = new JLabel("0");
        lblElements.setBounds(82, 11, 46, 14);
        panelStat.add(lblElements);
        
        JLabel lblClustersLabel = new JLabel("Clusters:");
        lblClustersLabel.setBounds(10, 28, 62, 14);
        panelStat.add(lblClustersLabel);
        
        lblClusters = new JLabel("");
        lblClusters.setBounds(82, 28, 46, 14);
        panelStat.add(lblClusters);
        
        JPanel panelCanvas = new JPanel();
        panelCanvas.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        panelCanvas.setBounds(268, 45, 510, 510);
        frmDensitybasedClustering.getContentPane().add(panelCanvas);
        panelCanvas.setLayout(null);
        
        JPanel panelInput = new JPanel();
        panelInput.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        panelInput.setBounds(24, 161, 227, 75);
        frmDensitybasedClustering.getContentPane().add(panelInput);
        panelInput.setLayout(null);
        
        txtFilename = new JTextField();
        txtFilename.setBounds(10, 11, 167, 20);
        panelInput.add(txtFilename);
        txtFilename.setText("input.csv");
        txtFilename.setColumns(10);
        
        JButton btnReadFile = new JButton("Read Data From File");
        btnReadFile.setHorizontalAlignment(SwingConstants.LEFT);
        btnReadFile.setIcon(null);
        btnReadFile.setBounds(10, 40, 207, 23);
        panelInput.add(btnReadFile);
        
        JButton btnOpenFile = new JButton("");
        btnOpenFile.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		
        	}
        });
        btnOpenFile.addMouseListener(new MouseAdapter() {
        	
        });
        btnOpenFile.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-open.gif")));
        btnOpenFile.setBounds(187, 10, 30, 23);
        panelInput.add(btnOpenFile);
        
        JPanel panelCluster = new JPanel();
        panelCluster.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        panelCluster.setBounds(24, 247, 227, 150);
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
        
        JButton btnClustering3 = new JButton("Clustering 3");
        btnClustering3.setIcon(new ImageIcon(dbc.class.getResource("/images/icon-cluster.gif")));
        btnClustering3.setHorizontalAlignment(SwingConstants.LEFT);
        btnClustering3.setBounds(12, 113, 205, 23);
        panelCluster.add(btnClustering3);
        
        JLabel lblAuthors = new JLabel("Authors: Martin Loginov, Hans M\u00E4esalu, Sven Aller");
        lblAuthors.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAuthors.setBounds(415, 555, 363, 14);
        frmDensitybasedClustering.getContentPane().add(lblAuthors);
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
        btnReadFile.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		try {
        			BufferedReader in = new BufferedReader(new FileReader(txtFilename.getText()));
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
        		    	points.add(new P(p));
        		    }
        		    lblElements.setText(Integer.toString(points.size()));
        		    canvas.repaint();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
        	}
        });
          
        frmDensitybasedClustering.setVisible(true);
    }
}
