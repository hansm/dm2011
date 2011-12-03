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

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class dbc extends Canvas
{
	public static int loendur = 0;
	public static int maxpunkte = 10;
	private static JTextField kontroll;
	
	public static ArrayList<P> points = new ArrayList<P>();
	public static Color[] colors = new Color[24];
	private static JTextField txtFilename;
	
	public static int dotSize = 8;
	
    public dbc()
    {
    }
    public void paint(Graphics g)
    {
    	g.setColor(Color.gray);
    	for (P point : points) {
    		if (point.cluster > -1) g.setColor(colors[point.cluster%colors.length]);
    		g.fillOval(point.coordX-(int)(dotSize/2), point.coordY-(int)(dotSize/2), dotSize, dotSize);
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
    	kontroll.setText((p.x / 50) + " " + (4 * p.y / 125) + " = " + (int)(Math.floor(p.x / 125) + 4 * Math.floor(p.y / 125)));
    }
    
    public static void klassifitseeri() {
    	for (P point : points) {
    		
    		point.cluster = (int)(Math.floor(point.coordX / 125) + 4 * Math.floor(point.coordY / 125));
//    		int r = (int)((double)255 / 500 * point.coordX);
//    		int g = (int)((double)255 / 500 * point.coordY);
//    		point.cluster = 100*r + g;
    	}
    }
   
    public static void createColors() {
//    	int l = 0;
//    	float step = (360f / colors.length);
//    	for(int i = 0; i < 360; i += step) {
//    	    float hue = i;
//    	    float saturation = 0.9f + (float)(Math.random() / 10);
//    	    float lightness = 0.5f + (float)(Math.random() / 2);
//    	    colors[l] = Color.getHSBColor(hue, saturation, lightness);
//    	    l++;
//    	}

    	int[] c = {255, 192, 160, 128};
    	for (int i = 0; i < c.length; i++) {
    		colors[(0 + 6*i)] = new Color(0, 0, c[i]);
    		colors[(1 + 6*i)] = new Color(0, c[i], 0);
    		colors[(2 + 6*i)] = new Color(c[i], 0, 0);
    		colors[(3 + 6*i)] = new Color(0, c[i], c[i]);
    		colors[(4 + 6*i)] = new Color(c[i], 0, c[i]);
    		colors[(5 + 6*i)] = new Color(c[i], c[i], 0);
    	}
//    	float step = (360f / colors.length);
//    	System.out.println(step);
//    	for (int i = 0; i < colors.length; i++) {
//    		colors[i] = Color.getHSBColor((float)i * step / 360, 1f, 0.8f);
////    		colors[i] = Color.getHSBColor((float)i * step / 360, 0.9f + (float)(Math.random() / 10), 0.5f + (float)(Math.random() / 2));
//    		System.out.println(i + " (" + ((float)i * step / 360) + ") - " + colors[i]);
//    	}
//    	int l = 0;
//    	for(int r = 0; r < 255; r = r + 64) {
//    		   for(int g = 0; g < 255; g = g + 64) {
//    		      for(int b = 0; b < 255; b = b + 64) {
//    		    	  colors[l] = new Color(r,g,b);
//    		    	  l++;
//    		      }
//    		   }
//    	}
    }
    
    /**
     * @wbp.parser.entryPoint
     */
    public static void main(String[] args)
    {
    	createColors();
    	
        final dbc canvas = new dbc();
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		if (loendur < maxpunkte) {
        			addPoint(e.getPoint());
        			canvas.repaint();
        		}
        	}
        	
        });
        canvas.setBounds(273, 28, 500, 500);
        JFrame frmDensitybasedClustering = new JFrame();
        frmDensitybasedClustering.setTitle("Density-based clustering");
        frmDensitybasedClustering.setResizable(false);
        frmDensitybasedClustering.setSize(800, 600);
        frmDensitybasedClustering.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmDensitybasedClustering.getContentPane().setLayout(null);
        frmDensitybasedClustering.getContentPane().add(canvas);
        
        JButton btnKlassifitseeri = new JButton("Klassifitseeri");
        btnKlassifitseeri.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		klassifitseeri();
        		canvas.repaint();
        	}
        });
        
        btnKlassifitseeri.setBounds(22, 75, 227, 23);
        frmDensitybasedClustering.getContentPane().add(btnKlassifitseeri);
        
        kontroll = new JTextField();
        kontroll.setBounds(10, 430, 246, 20);
        frmDensitybasedClustering.getContentPane().add(kontroll);
        kontroll.setColumns(10);
        
        final JLabel lblSlidervalue = new JLabel(Integer.toString(maxpunkte));
        lblSlidervalue.setBounds(232, 28, 46, 14);
        frmDensitybasedClustering.getContentPane().add(lblSlidervalue);
        
        final JSlider sliderPunktideArv = new JSlider();
        sliderPunktideArv.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent arg0) {
        		maxpunkte = sliderPunktideArv.getValue();
        		lblSlidervalue.setText(Integer.toString(maxpunkte));
        	}
        	
        });
        sliderPunktideArv.setValue(maxpunkte);
        sliderPunktideArv.setBounds(22, 28, 200, 23);
        frmDensitybasedClustering.getContentPane().add(sliderPunktideArv);
        
        JButton btnOpenFile = new JButton("Open File");
        btnOpenFile.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
//        		points.clear();
//        		canvas.repaint();
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
        		    double changeX = 490d / maxX;
        		    double changeY = 490d / maxY;
//        		    System.out.println(changeX + " " + changeY);
        		    for (Point p : pointsfromfile) {
        		    	p.x = (int)(p.x * changeX);
        		    	p.y = (int)(p.y * changeY);
        		    	points.add(new P(p));
        		    }
        		    canvas.repaint();

				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        });
        btnOpenFile.setBounds(160, 390, 89, 23);
        frmDensitybasedClustering.getContentPane().add(btnOpenFile);
        
        txtFilename = new JTextField();
        txtFilename.setText("input.csv");
        txtFilename.setBounds(22, 359, 227, 20);
        frmDensitybasedClustering.getContentPane().add(txtFilename);
        txtFilename.setColumns(10);
        
        JButton btnClear = new JButton("Clear");
        btnClear.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		points.clear();
        		canvas.repaint();
        	}
        });
        btnClear.setBounds(22, 109, 227, 23);
        frmDensitybasedClustering.getContentPane().add(btnClear);
        
        
        frmDensitybasedClustering.setVisible(true);
        
        maxpunkte = sliderPunktideArv.getValue();
    }
}
