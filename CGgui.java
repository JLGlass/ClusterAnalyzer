/*    This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


//Create interactive java gui to display CG cluster data
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//import io libraries individually
//to avoid conflicting with swing filechooser
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.*;
import java.lang.Math.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.filechooser.*;

//for java 1.6
import javax.swing.GroupLayout.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.title.*;
import org.jfree.data.xy.*;
import org.jfree.data.general.*;
import org.jfree.data.statistics.*;
import org.jfree.data.function.*;
import org.jfree.ui.*;
import org.jfree.chart.axis.*;

//project classes
import sequences.*;
import fragments.*;

public class CGgui {
    
    /*debug switch*/
    boolean DEBUG = true;
    
    //initialize swing frame
    private JFrame f = new JFrame("Cluster Analyzer");
    private JPanel fCenter = new JPanel();
    private JPanel fEast = new JPanel();
    
    //initialize menu
    private JMenuBar mb = new JMenuBar();
    private JMenu mnuFile = new JMenu("File");
    private JMenuItem mnuLoad = new JMenu("Load");
    private JMenuItem mnuItemOpenFasta = new JMenuItem("Load Fasta");
    private JMenuItem mnuItemOpenBed = new JMenuItem("Load Bed");
    private JMenuItem mnuSaveData = new JMenu("Save Data");
    private JMenuItem mnuItemSavePositions = new JMenuItem("Save Positions");
    private JMenuItem mnuItemSaveClusters = new JMenuItem("Save Clusters");
    private JMenuItem mnuSaveCharts = new JMenu("Save Chart");
    private JMenuItem mnuItemSaveChrt = new JMenuItem("Save Histogram");
    private JMenuItem mnuItemSaveGrayChrt = new JMenuItem("Save Grayscale Histogram");
    private JMenuItem mnuItemSaveMinChrt = new JMenuItem("Save Minima Chart");
    private JMenuItem mnuItemSaveGrayMinChrt = new JMenuItem("Save Grayscale Minima Chart");
    private JMenuItem mnuItemSaveClusterChrt = new JMenuItem("Save Optimization Chart");
    private JMenuItem mnuItemSaveGrayClusterChrt = new JMenuItem("Save Grayscale Optimization Chart");
    private JMenuItem mnuItemClearData = new JMenuItem("Clear Data");
    private JMenuItem mnuItemQuit = new JMenuItem("Quit");
    private JMenu mnuEdit = new JMenu("Edit");
    private JMenuItem mnuItemChartProps = new JMenuItem("Histogram Properties");
    private JMenuItem mnuItemMinimaProps = new JMenuItem("Minima Chart Properties");
    private JMenuItem mnuItemClusterProps = new JMenuItem("Optimization Chart Properties");
    private JCheckBoxMenuItem mnuItemFindMin = new JCheckBoxMenuItem("Find Minimum", true);
    private JCheckBoxMenuItem mnuItemSetAxes = new JCheckBoxMenuItem("Set Domain");
    private JCheckBoxMenuItem mnuItemShowGrid = new JCheckBoxMenuItem("Show Gridlines", true);
    //private JMenu mnuHelp = new JMenu("Help");
    //private JMenuItem mnuItemMan = new JMenuItem("Manual");
    //private JMenuItem mnuItemAbout = new JMenuItem("About");
    
    //initialize progress bar
    private JProgressBar jprogressbar = new JProgressBar();
    
    //initialize text area
    private JPanel textPanel = new JPanel();
    private JTextArea SeqText = new JTextArea("Welcome to Cluster Analyzer\nBuild Date Dec. 28th 2009\n\n",10,60);
    private JScrollPane jScrollPane = new JScrollPane(SeqText);
    
    //initialize CG Settings Panel
    private String regex = "CG";
    private int MinCG = 15;
    private int MaxCG = 60;
    private int CurrCG = 30;
    private int CGstep = 5;
    private JPanel CGPanel = new JPanel();
    private JPanel CGsubPanel1 = new JPanel();
    private JPanel CGsubPanel2 = new JPanel();
    private JPanel CGsubPanel3 = new JPanel();
    private JLabel minCGsLabel = new JLabel("Minimum    ");
    private JLabel maxCGsLabel = new JLabel("Maximum    ");
    private JLabel CGstepLabel = new JLabel("Step Size    ");
    private JLabel CGsetCurrLabel = new JLabel("Graphed");
    private JLabel searchPatternLabel = new JLabel("Search pattern");
    private JCheckBox caseCheckBox = new JCheckBox("Case Sensitve", false);
    private JCheckBox smoothCheckBox = new JCheckBox("Smoothing Factor", true);
    private JTextField minCGsText = new JTextField(Integer.toString(MinCG),17);
    private JTextField maxCGsText = new JTextField(Integer.toString(MaxCG),17);
    private JTextField CGstepText = new JTextField(Integer.toString(CGstep),17);
    private JTextField smoothText = new JTextField(null,17);
    private JTextField searchPatternText = new JTextField(regex, 17);
    private JTextField CGcurrText = new JTextField(Integer.toString(CurrCG),5);
    private JButton CGApplyButton = new JButton("Apply");
    private JButton CGResetButton = new JButton("Reset");
    private JButton CGsetCurrButton = new JButton("Go");
    
    //initialize minimum panel
    private JPanel findminPanel = new JPanel();
    private JButton minGrabButton = new JButton("Grab");
    private JButton maxGrabButton = new JButton("Grab");
    private JButton ClearButton = new JButton("Clear");
    private JButton CalcButton = new JButton("Find");
    private JButton CalcAllButton = new JButton("Find All");
    private JButton CancelButton = new JButton("Cancel");
    private JLabel minLabel = new JLabel("Lower Bound");
    private JLabel maxLabel = new JLabel("Upper Bound");
    private JTextField minText = new JTextField(null,5);
    private JTextField maxText = new JTextField(null,5);
    
    //axes tool
    private JPanel setaxesPanel = new JPanel();
    private JLabel mindomainLabel = new JLabel("Minimum");
    private JLabel maxdomainLabel = new JLabel("Maximum");
    private JTextField mindomainText = new JTextField(null,5);
    private JTextField maxdomainText = new JTextField(null,5);
    private JButton SetDomainButton = new JButton("Set Domain");
    private JButton ResetDomainButton = new JButton("Reset Domain");
    
    //initialize chart area
    private JTabbedPane ChartArea = new JTabbedPane();
    private XYSeriesCollection histogramdataset = new XYSeriesCollection();
    private CategoryTableXYDataset minimadataset = new CategoryTableXYDataset();
    private CategoryTableXYDataset clusterdataset = new CategoryTableXYDataset();
    private ChartPanel chartpanel, minchartpanel, clusterchartpanel;
    private JFreeChart chart, minchart, clusterchart;
    
    //initialize axes info, data to be graphed
    private double MIN = Double.MAX_VALUE;
    private double MAX = 0;
    private double CurrMin;
    private int BINS = 10000;
    private Comparable key = "";
    private Function2D regLine;
    
    //data to be processed
    private IntervalMarker intervalmarker;
    
    private HashMap<String, Fragment> fragmentMap = new HashMap<String, Fragment>();
    private XYSeries lengthHist;
	private Cluster cluster;
    private File[] fileList;
    
    //constructor
    public CGgui()
    {
        //main window frame
        f.setResizable(false);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(fCenter, BorderLayout.CENTER);
        f.getContentPane().add(fEast, BorderLayout.EAST);
        
        //menu
        mnuFile.add(mnuLoad);
        mnuLoad.add(mnuItemOpenFasta);
        mnuLoad.add(mnuItemOpenBed);
        mnuFile.add(mnuSaveData);
        mnuSaveData.add(mnuItemSavePositions);
        mnuSaveData.add(mnuItemSaveClusters);
        
        //mnuFile.add(mnuItemOpenDataFile);
        mnuFile.add(mnuSaveCharts);
        mnuSaveCharts.add(mnuItemSaveChrt);
        mnuSaveCharts.add(mnuItemSaveMinChrt);
        mnuSaveCharts.add(mnuItemSaveClusterChrt);
        mnuSaveCharts.add(mnuItemSaveGrayChrt);
        mnuSaveCharts.add(mnuItemSaveGrayMinChrt);
        mnuSaveCharts.add(mnuItemSaveGrayClusterChrt);
        mnuFile.add(mnuItemClearData);
        mnuFile.add(mnuItemQuit);
        mnuEdit.add(mnuItemChartProps);
        mnuEdit.add(mnuItemMinimaProps);
        mnuEdit.add(mnuItemClusterProps);
        mnuEdit.add(mnuItemFindMin);
        mnuEdit.add(mnuItemSetAxes);
        mnuEdit.add(mnuItemShowGrid);
        //	mnuHelp.add(mnuItemMan);
        //	mnuHelp.add(mnuItemAbout);
        mb.add(mnuFile);
        mb.add(mnuEdit);
        //	mb.add(mnuHelp);
        f.setJMenuBar(mb);
        
        //progressbar
        jprogressbar.setVisible(false);
        jprogressbar.setBorderPainted(false);
        f.getContentPane().add(jprogressbar, BorderLayout.SOUTH);
        
        //chart area
        //histogram
        ChartArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        chart = ChartFactory.createXYAreaChart("Fragment Length Histogram",
                                             "Fragment Length (l)", "Frequency f(l)", histogramdataset,
                                             PlotOrientation.VERTICAL, true, true, false);
        chart.addSubtitle(0,new TextTitle("n = " + CurrCG));
        XYPlot xyplot = (XYPlot)chart.getPlot();
        xyplot.setForegroundAlpha(0.85F);
        xyplot.setDomainCrosshairVisible(false);
        chartpanel = new ChartPanel(chart);
        ChartArea.add("Fragment Length",chartpanel);
        
        //minima
        minchart = ChartFactory.createXYLineChart("Local Minima",
                                                  "Fragment Length (l)", "Number of Matches per Fragment", minimadataset,
                                                  PlotOrientation.VERTICAL, true, true, false);
        XYPlot minxyplot = (XYPlot)minchart.getPlot();
        minxyplot.setForegroundAlpha(0.85F);
        minchartpanel = new ChartPanel(minchart);
        ChartArea.add("Minima",minchartpanel);
        
        //optimization
        clusterchart = ChartFactory.createScatterPlot("Average Cluster Size vs. Maximum Fragment Length",
                                                      "Number (n) of Matches per Fragment", "Number of Overlapping Fragments per Cluster/Max Fragment Length", clusterdataset,
                                                      PlotOrientation.VERTICAL, true, true, false);

		XYPlot clusterplot = (XYPlot)clusterchart.getPlot();
        clusterplot.setForegroundAlpha(0.85F);
        clusterchartpanel = new ChartPanel(clusterchart);
        ChartArea.add("Optimization", clusterchartpanel);
        
        //text area
        SeqText.setLineWrap(true);
        SeqText.setWrapStyleWord(true);
        SeqText.setEditable(false);
        jScrollPane.setBorder(
                              BorderFactory.createCompoundBorder(
                                                                 BorderFactory.createCompoundBorder(
                                                                                                    BorderFactory.createTitledBorder("Debug"),
                                                                                                    BorderFactory.createEmptyBorder(5,5,5,5)),
                                                                 jScrollPane.getBorder()));
        textPanel.add(jScrollPane);
        //this next line is a hack to fix the ScrollPane size when the chart is scrolled
        jScrollPane.setPreferredSize(jScrollPane.getPreferredSize());
        //auto scroll to added text
        SeqText.setAutoscrolls(true);
        jScrollPane.setAutoscrolls(true);
        
        //center panel layout
        GroupLayout centerlayout = new GroupLayout(fCenter);
        fCenter.setLayout(centerlayout);
        ////Create a sequential and a parallel groups
        SequentialGroup h1 = centerlayout.createSequentialGroup();
        SequentialGroup v1 = centerlayout.createSequentialGroup();
        ////grouping
        h1.addGroup(centerlayout.createParallelGroup().addComponent(ChartArea).
                    addComponent(textPanel));
        centerlayout.setHorizontalGroup(h1);
        ////more grouping
        v1.addGroup(centerlayout.createParallelGroup().addComponent(ChartArea));
        v1.addGroup(centerlayout.createParallelGroup().addComponent(textPanel));
        centerlayout.setVerticalGroup(v1);
        
        //CG Panel (search settings panel)
        CGPanel.setBorder(BorderFactory.createCompoundBorder(
                                                             BorderFactory.createCompoundBorder(
                                                                                                BorderFactory.createTitledBorder("Search Settings"),
                                                                                                BorderFactory.createEmptyBorder(5,5,5,5)),
                                                             CGPanel.getBorder()));
        //set subpanel1 layout
        GroupLayout CGsublayout1 = new GroupLayout(CGsubPanel1);
        CGsubPanel1.setLayout(CGsublayout1);
        ////Create a sequential and a parallel groups
        SequentialGroup h4 = CGsublayout1.createSequentialGroup();
        SequentialGroup v4 = CGsublayout1.createSequentialGroup();
        ////grouping
        h4.addGroup(CGsublayout1.createParallelGroup().
                    addComponent(searchPatternLabel).addComponent(minCGsLabel).
                    addComponent(maxCGsLabel).addComponent(CGstepLabel).
                    addComponent(smoothCheckBox).addComponent(CGApplyButton).
                    addComponent(CGsetCurrLabel));
        h4.addGroup(CGsublayout1.createParallelGroup().
                    addComponent(searchPatternText).addComponent(minCGsText).
                    addComponent(maxCGsText).addComponent(CGstepText).
                    addComponent(smoothText).addComponent(CGResetButton).
                    addComponent(CGcurrText));
        CGsublayout1.setHorizontalGroup(h4);
        ////more grouping
        v4.addGroup(CGsublayout1.createParallelGroup(
                                                     GroupLayout.Alignment.BASELINE).
                    addComponent(searchPatternLabel).addComponent(searchPatternText));
        v4.addContainerGap(5,5);
        v4.addGroup(CGsublayout1.createParallelGroup(
                                                     GroupLayout.Alignment.BASELINE).
                    addComponent(minCGsLabel).addComponent(minCGsText));
        v4.addContainerGap(5,5);
        v4.addGroup(CGsublayout1.createParallelGroup(
                                                     GroupLayout.Alignment.BASELINE).
                    addComponent(maxCGsLabel).addComponent(maxCGsText));
        v4.addContainerGap(5,5);
        v4.addGroup(CGsublayout1.createParallelGroup(
                                                     GroupLayout.Alignment.BASELINE).
                    addComponent(CGstepLabel).addComponent(CGstepText));
        v4.addContainerGap(5,5);
        v4.addGroup(CGsublayout1.createParallelGroup(
                                                     GroupLayout.Alignment.BASELINE).
                    addComponent(smoothCheckBox).addComponent(smoothText));
        v4.addContainerGap(5,5);
        v4.addGroup(CGsublayout1.createParallelGroup(
                                                     GroupLayout.Alignment.BASELINE).
                    addComponent(CGApplyButton).addComponent(CGResetButton));
        v4.addContainerGap(15,15);
        v4.addGroup(CGsublayout1.createParallelGroup(
                                                     GroupLayout.Alignment.BASELINE).
                    addComponent(CGsetCurrLabel).addComponent(CGcurrText));
        CGsublayout1.setVerticalGroup(v4);
        
        //set subpanel2 layout
        GroupLayout CGsublayout2 = new GroupLayout(CGsubPanel2);
        CGsubPanel2.setLayout(CGsublayout2);
        ////Create a sequential and a parallel groups
        SequentialGroup hSettingsSub2 = CGsublayout2.createSequentialGroup();
        SequentialGroup vSettingsSub2 = CGsublayout2.createSequentialGroup();
        
        ////horizontal grouping
        hSettingsSub2.addGroup(CGsublayout2.createParallelGroup().
                               addComponent(CGsetCurrLabel));
        hSettingsSub2.addContainerGap(25,25);
        hSettingsSub2.addGroup(CGsublayout2.createParallelGroup().
                               addComponent(CGcurrText));
        hSettingsSub2.addContainerGap(25,25);
        hSettingsSub2.addGroup(CGsublayout2.createParallelGroup().
                               addComponent(CGsetCurrButton));
        CGsublayout2.setHorizontalGroup(hSettingsSub2);
        ////vertical grouping
        vSettingsSub2.addGroup(CGsublayout2.createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE).
                               addComponent(CGsetCurrLabel).addComponent(CGcurrText).
                               addComponent(CGsetCurrButton));
        CGsublayout2.setVerticalGroup(vSettingsSub2);
        
        //set overall layout (subpanel3)
        GroupLayout CGsublayout3 = new GroupLayout(CGsubPanel3);
        CGsubPanel3.setLayout(CGsublayout3);
        ////Create a sequential and a parallel groups
        SequentialGroup h5 = CGsublayout3.createSequentialGroup();
        SequentialGroup v5 = CGsublayout3.createSequentialGroup();
        ////grouping
        h5.addGroup(CGsublayout3.createParallelGroup().
                    addComponent(caseCheckBox).addComponent(CGsubPanel1).
                    addComponent(CGsubPanel2));
        CGsublayout3.setHorizontalGroup(h5);
        ////more grouping
        v5.addGroup(CGsublayout3.createParallelGroup().addComponent(caseCheckBox));
        v5.addContainerGap(5,5);
        v5.addGroup(CGsublayout3.createParallelGroup().addComponent(CGsubPanel1));
        v5.addContainerGap(5,5);
        v5.addGroup(CGsublayout3.createParallelGroup().addComponent(CGsubPanel2));
        CGsublayout3.setVerticalGroup(v5);
        //add to resizable container
        CGPanel.add(CGsubPanel3);
        //button and slider listeners
        CGApplyButton.addActionListener(new ListenCGApplyButton());
        CGResetButton.addActionListener(new ListenCGResetButton());
        CGsetCurrButton.addActionListener(new ListenCGsetCurrButton());
        CGcurrText.addActionListener(new ListenCGsetCurrButton());
        
        //minimum area
        findminPanel.setVisible(true);
        findminPanel.setBorder(
                               BorderFactory.createCompoundBorder(
                                                                  BorderFactory.createCompoundBorder(
                                                                                                     BorderFactory.createTitledBorder("Find Minimum"),
                                                                                                     BorderFactory.createEmptyBorder(5,5,5,5)),
                                                                  findminPanel.getBorder()));
        GroupLayout minlayout = new GroupLayout(findminPanel);
        findminPanel.setLayout(minlayout);
        
        ////Create a sequential and a parallel groups
        SequentialGroup h2 = minlayout.createSequentialGroup();
        SequentialGroup v2 = minlayout.createSequentialGroup();
        
        ////group find minimum and find all buttons
        SequentialGroup hFindMinButtons = minlayout.createSequentialGroup();
        SequentialGroup vFindMinButtons = minlayout.createSequentialGroup();
        hFindMinButtons.addGroup(minlayout.createParallelGroup().
                                 addComponent(CalcButton));
        hFindMinButtons.addGroup(minlayout.createParallelGroup().
                                 addComponent(CalcAllButton));
        vFindMinButtons.addGroup(minlayout.createParallelGroup(
                                                               GroupLayout.Alignment.BASELINE).
                                 addComponent(CalcButton).addComponent(CalcAllButton));
        ////general grouping
        h2.addGroup(minlayout.createParallelGroup().
                    addComponent(minLabel).addComponent(maxLabel).
                    addComponent(ClearButton));
        h2.addContainerGap(5,5);
        h2.addGroup(minlayout.createParallelGroup().
                    addComponent(minText).addComponent(maxText).
                    addGroup(hFindMinButtons));
        h2.addContainerGap(5,5);
        h2.addGroup(minlayout.createParallelGroup().
                    addComponent(minGrabButton).addComponent(maxGrabButton).
                    addComponent(CancelButton));
        minlayout.setHorizontalGroup(h2);
        ////more grouping
        v2.addGroup(minlayout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                    addComponent(minLabel).addComponent(minText).
                    addComponent(minGrabButton));
        v2.addContainerGap(5,5);
        v2.addGroup(minlayout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                    addComponent(maxLabel).addComponent(maxText).
                    addComponent(maxGrabButton));
        v2.addContainerGap(5,5);
        v2.addContainerGap(5,5);
        v2.addGroup(minlayout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                    addComponent(ClearButton).addGroup(vFindMinButtons).
                    addComponent(CancelButton));
        minlayout.setVerticalGroup(v2);
        //add button listeners
        ClearButton.addActionListener(new ListenClearButton());
        CalcButton.addActionListener(new ListenCalcButton());
        CalcAllButton.addActionListener(new ListenCalcAllButton());
        CancelButton.addActionListener(new ListenCancelButton());
        minGrabButton.addActionListener(new ListenminGrabButton());
        maxGrabButton.addActionListener(new ListenmaxGrabButton());
        
        //axes panel
        mnuItemSetAxes.setState(false);
        setaxesPanel.setVisible(false);
        setaxesPanel.setBorder(
                               BorderFactory.createCompoundBorder(
                                                                  BorderFactory.createCompoundBorder(
                                                                                                     BorderFactory.createTitledBorder("Set Domain"),
                                                                                                     BorderFactory.createEmptyBorder(5,5,5,5)),
                                                                  setaxesPanel.getBorder()));
        GroupLayout setaxeslayout = new GroupLayout(setaxesPanel);
        setaxesPanel.setLayout(setaxeslayout);
        ////Create a sequential and a parallel groups
        SequentialGroup h6 = setaxeslayout.createSequentialGroup();
        SequentialGroup v6 = setaxeslayout.createSequentialGroup();
        ////grouping
        h6.addGroup(setaxeslayout.createParallelGroup().
                    addComponent(mindomainLabel).addComponent(maxdomainLabel).
                    addComponent(SetDomainButton));
        h6.addContainerGap(5,5);
        h6.addGroup(setaxeslayout.createParallelGroup().
                    addComponent(mindomainText).addComponent(maxdomainText).
                    addComponent(ResetDomainButton));
        setaxeslayout.setHorizontalGroup(h6);
        ////more grouping
        v6.addGroup(setaxeslayout.createParallelGroup(
                                                      GroupLayout.Alignment.BASELINE).
                    addComponent(mindomainLabel).addComponent(mindomainText));
        v6.addContainerGap(5,5);
        v6.addGroup(setaxeslayout.createParallelGroup(
                                                      GroupLayout.Alignment.BASELINE).
                    addComponent(maxdomainLabel).addComponent(maxdomainText));
        v6.addContainerGap(5,5);
        v6.addGroup(setaxeslayout.createParallelGroup(
                                                      GroupLayout.Alignment.BASELINE).
                    addComponent(SetDomainButton).addComponent(ResetDomainButton));
        setaxeslayout.setVerticalGroup(v6);
        //add button listeners
        SetDomainButton.addActionListener(new ListenSetDomainButton());
        ResetDomainButton.addActionListener(new ListenResetDomainButton());
        
        //east panel layout
        GroupLayout eastlayout = new GroupLayout(fEast);
        fEast.setLayout(eastlayout);
        ////Create a sequential and a parallel groups
        SequentialGroup h3 = eastlayout.createSequentialGroup();
        SequentialGroup v3 = eastlayout.createSequentialGroup();
        ////grouping
        h3.addGroup(eastlayout.createParallelGroup()
                    .addComponent(CGPanel).addComponent(findminPanel)
                    .addComponent(setaxesPanel));
        eastlayout.setHorizontalGroup(h3);
        ////more grouping
        v3.addGroup(eastlayout.createParallelGroup().addComponent(CGPanel));
        v3.addGroup(eastlayout.createParallelGroup().addComponent(findminPanel));
        v3.addGroup(eastlayout.createParallelGroup().addComponent(setaxesPanel));
        eastlayout.setVerticalGroup(v3);
        
        //listen for exit signals
        f.addWindowListener(new ListenCloseWdw());
        mnuItemQuit.addActionListener(new ListenMenuQuit());
        
        //listen for exit signals
        f.addWindowListener(new ListenCloseWdw());
        mnuItemClearData.addActionListener(new ListenClearData());
        
        //listen for open signal
        mnuItemOpenFasta.addActionListener(new ListenMenuOpenFasta());

        //listen for open signal
        mnuItemOpenBed.addActionListener(new ListenMenuOpenBed());

        //listen for save position signal
        mnuItemSavePositions.addActionListener(new ListenMenuSavePositions());
        
        //listen for save cluster signal
        mnuItemSaveClusters.addActionListener(new ListenMenuSaveClusters());

        //listen for save histogram chart signal
        mnuItemSaveChrt.addActionListener(new ListenMenuSaveChrt());
        
        //listen for save chart signal
        mnuItemSaveMinChrt.addActionListener(new ListenMenuSaveMinChrt());
        
        //listen for save cluster chart signal
        mnuItemSaveClusterChrt.addActionListener(new ListenMenuSaveClusterChrt());
        
        //listen for save histogram chart in grayscale signal;
        mnuItemSaveGrayChrt.addActionListener(new ListenMenuSaveGrayChrt());
        
        //listen for save chart signal
        mnuItemSaveGrayMinChrt.addActionListener(new ListenMenuSaveGrayMinChrt());
        
        //listen for save cluster chart signal
        mnuItemSaveGrayClusterChrt.addActionListener(
                                                     new ListenMenuSaveGrayClusterChrt());
        
        //listen for edit histogram properties signal
        mnuItemChartProps.addActionListener(new ListenMenuChartProps());
        
        //listen for edit minima chart properties signal
        mnuItemMinimaProps.addActionListener(new ListenMenuMinimaProps());
        
        //listen for edit optimization chart properties signal
        mnuItemClusterProps.addActionListener(new ListenMenuClusterProps());
                
        //listen for find minimum signal
        mnuItemFindMin.addActionListener(new ListenMenuFindMin());
        
        //listen for find minimum signal
        mnuItemSetAxes.addActionListener(new ListenMenuSetAxes());
        
        //listen for show gridlines
        mnuItemShowGrid.addActionListener(new ListenMenuShowGrid());
        
        //other menu items
        
        //garbage collect
        //System.gc();
    }
    
    public class ListenCGApplyButton implements ActionListener, Runnable{
        public void actionPerformed(ActionEvent e){
            ListenCGApplyButton t = new ListenCGApplyButton();
            new Thread(t).start();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    jprogressbar.updateUI();
                }
            });
        }
        public void run() {
            
            //set up progress bar
            showProgressbar("Applying changes...");
            
            try {
                
                if (!regex.equals(searchPatternText.getText())) {
                    regex = searchPatternText.getText();
                    loadFiles(fileList);
                }

                //set new boundaries
                MinCG = Integer.parseInt(minCGsText.getText());
                MaxCG = Integer.parseInt(maxCGsText.getText());
                CGstep = Integer.parseInt(CGstepText.getText());
                
                //make sure current is not too high
                if (CurrCG>MaxCG)
                    CurrCG = MaxCG;
                
                //debug lines
                if (DEBUG) {
                    PrintText("Searching for fragments between " + MinCG);
                    PrintText(" and " + MaxCG + " long\n");
                    PrintText("Display: " + CurrCG + " " + regex + " \n");
                }

                updateData();
                
                hideProgressbar();
            }
            catch (NumberFormatException err) {
                //print error
                if (DEBUG) PrintText("Error: " + err + "\n");
                
                //force reset
                minCGsText.setText(Integer.toString(MinCG));
                maxCGsText.setText(Integer.toString(MaxCG));
                CGstepText.setText(Integer.toString(CGstep));
                return;
            }
            catch (Exception err) {
                if (DEBUG) PrintText("Error: " + err + "\n");
                System.err.println("Exception caught: " + err.getMessage());
                return;
            }
             
        }
    }
    
    public class ListenCGResetButton implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //set text
            minCGsText.setText(Integer.toString(MinCG));
            maxCGsText.setText(Integer.toString(MaxCG));
            CGstepText.setText(Integer.toString(CGstep));
        }
    }
    
    public class ListenCGsetCurrButton implements ActionListener,Runnable
    {
        public void actionPerformed(ActionEvent e)
        {
            ListenCGsetCurrButton t = new ListenCGsetCurrButton();
            new Thread(t).start();
            javax.swing.SwingUtilities.invokeLater(new Runnable()
                                                   {
                public void run()
                {
                    jprogressbar.updateUI();
                }
            });
        }
        public void run()
        {
            try
            {
                int newCG = Integer.parseInt(CGcurrText.getText());
                //check for no change or negative value
                if ((CurrCG != newCG) && (newCG > 0))
                {
                    CurrCG = Integer.parseInt(CGcurrText.getText());
                }
                else return;
                
                //passed checks so update
                updateData();
            }
            catch (NumberFormatException err)
            {
                if (DEBUG) PrintText("Error: " + err + "\n");
            }
            catch (IndexOutOfBoundsException err)
            {
                if (DEBUG) PrintText("Error: " + err + "\n");
            }
        }
    }
    
    private void updateData() {
        //set up progress bar
        updateProgressbar("Updating data...");
        
        //recalculate data
        //rebuild arrays
        MIN = Double.MAX_VALUE;
        MAX = 0;
        
        //grab last min for adjusting bounds
        double prevMin = CurrMin;
                
        if (fragmentMap.size() > 0) {
                        
            //get lengths, use iterator to avoid concurrent modification exception
            Iterator it = fragmentMap.entrySet().iterator();
            
            while(it.hasNext()) {
                Map.Entry mapEntry = (Map.Entry)it.next();
                Fragment f = (Fragment)mapEntry.getValue();
                f.setPatternsPerFragment(CurrCG); //passed by reference
            }
        }
        
        
        //graph
        replaceData(key);
        
        //label chart
        chart.clearSubtitles();
        chart.addSubtitle(0,new TextTitle("n = " + CurrCG));
        
        //adjust min and max bounds
        adjustBounds(prevMin);
        
        //report new domain
        mindomainText.setText(Double.toString(MIN));
        maxdomainText.setText(Double.toString(MAX));
        
        //garbage collect
        //System.gc();
         
    }
    
    public void adjustBounds(double prevMin) {
        //recalculate the min finding bounds
        if ((mnuItemFindMin.getState() == true) && (regLine != null)) {
            //keep boundary range, but recenter. Default radius: 100
            double lowbound,highbound;
            try {
                lowbound = prevMin - Double.parseDouble(minText.getText());
                highbound = Double.parseDouble(maxText.getText()) - prevMin;
            }
            catch (NumberFormatException err) {
                lowbound = 100;
                highbound = 100;
            }
            double center = regLine.getValue(CurrCG);
            if (DEBUG) {
                PrintText("With center " + center + "\n");
            }
            minText.setText(Double.toString(center - lowbound));
            maxText.setText(Double.toString(center + highbound));
            intervalmarker.setStartValue(center - lowbound);
            intervalmarker.setEndValue(center + highbound);
            PrintText("Bounds: " + (center - lowbound) + ":" + (center + highbound) + "\n");
        }
    }
    
    public class ListenCalcAllButton implements ActionListener, Runnable {
    	public void actionPerformed(ActionEvent e){
            ListenCalcAllButton t = new ListenCalcAllButton();
            new Thread(t).start();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    jprogressbar.updateUI();
                }
            });
        }
        public void run() {
            for (CurrCG = MinCG; CurrCG <= MaxCG; CurrCG += CGstep) {
                //update data (automatically adjusts bounds)
                CGcurrText.setText(""+CurrCG);
                updateData();
                
                //find min
                FindMin();
            }
            
            //garbage collect
            //System.gc();
        }
    }
    
    public class ListenCalcButton implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            FindMin();
            
            //garbage collect
            //System.gc();
        }
    }
    
    public void FindMin ()
    {
		
        try
        {
            //get the data
            XYPlot xyplot = (XYPlot)chart.getPlot();
            XYDataset xydataset = xyplot.getDataset();			
										  
            //search through and find lowest minimum
            double MinXVal = -1;
            double MinYVal = -1;
            double lowbound = Double.parseDouble(minText.getText());
            double highbound = Double.parseDouble(maxText.getText());
            if (DEBUG) PrintText("Minimum of " + xydataset.getItemCount(0) + " datapoints\n");
            for (int i = 0; i < xydataset.getItemCount(0); i++)
            {
                double XVal = xydataset.getXValue(0,i);
                if ((XVal > lowbound) && (XVal < highbound))
                {
                    double YValCurr = xydataset.getYValue(0,i);
                    if ((YValCurr < MinYVal) || (MinYVal == -1))
                    {
                        MinYVal = YValCurr;
                        MinXVal = xydataset.getXValue(0,i);
                    }
                }
            }
            
            //print out, save, and mark off min
            if (DEBUG) PrintText("Minimum located at (" + MinXVal + "," + MinYVal + ")\n");
						
            addMinima(MinXVal);
            CurrMin = MinXVal;
            xyplot.setDomainCrosshairValue(MinXVal);

            cluster = new Cluster(fragmentMap, CurrCG, MinXVal);

            double AveLength = cluster.getAvgClusterLength();
            if (DEBUG) PrintText("Average Number of " + regex + "s in Cluster: " + AveLength + "\n");
            addClusterSize(AveLength);
        }
        catch (NumberFormatException err)
        {
            if (DEBUG) PrintText("Error: " + err + "\n");
        }
        catch (IndexOutOfBoundsException err)
        {
            if (DEBUG) PrintText("Error: " + err + "\n");
        }

		
    }
    
    public class ListenCancelButton implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            findminPanel.setVisible(false);
            //f.pack();
            mnuItemFindMin.setState(false);
        }
    }
    
    public class ListenClearButton implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //remove marker
            XYPlot xyplot = (XYPlot)chart.getPlot();
            xyplot.removeDomainMarker(intervalmarker, Layer.BACKGROUND);
            intervalmarker = null;
            
            //clear text
            minText.setText(null);
            maxText.setText(null);
        }
    }
    
    public class ListenmaxGrabButton implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            XYPlot xyplot = (XYPlot)chart.getPlot();
            double X = xyplot.getDomainCrosshairValue();
            maxText.setText(Double.toString(X));
            
            if (intervalmarker == null)
            {
                intervalmarker = new IntervalMarker(X, X);
                intervalmarker.setPaint(Color.LIGHT_GRAY);
                xyplot.addDomainMarker(intervalmarker, Layer.BACKGROUND);
            }
            intervalmarker.setEndValue(X);
        }
    }
    
    public class ListenminGrabButton implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            XYPlot xyplot = (XYPlot)chart.getPlot();
            double X = xyplot.getDomainCrosshairValue();
            minText.setText(Double.toString(X));
            
            if (intervalmarker == null)
            {
                intervalmarker = new IntervalMarker(X, X);
                intervalmarker.setPaint(Color.LIGHT_GRAY);
                xyplot.addDomainMarker(intervalmarker, Layer.BACKGROUND);
                
            }
            intervalmarker.setStartValue(X);
        }
    }
    
    public class ListenSetDomainButton implements ActionListener{
        public void actionPerformed(ActionEvent e){
            try
            {				
                MIN = Double.parseDouble(mindomainText.getText());
                MAX = Double.parseDouble(maxdomainText.getText());
                if (MIN < MAX)
                    replaceData(key);
                else if (DEBUG) PrintText("Error: Min !< Max\n");
            }
            catch (NumberFormatException err) {
                if (DEBUG) PrintText("Error: " + err + "\n");
            }
            catch (IndexOutOfBoundsException err) {
                if (DEBUG) PrintText("Error: " + err + "\n");
            }
        }
    }
    
    public class ListenResetDomainButton implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //reset text
            mindomainText.setText(Double.toString(MIN));
            maxdomainText.setText(Double.toString(MAX));
        }
    }
    
    public class ListenMenuFindMin implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (mnuItemFindMin.getState() == true)
            {
                if (DEBUG) PrintText("Opening minimum finder\n");
                XYPlot xyplot = (XYPlot)chart.getPlot();
                xyplot.setDomainCrosshairVisible(true);
                xyplot.setDomainCrosshairLockedOnData(false);
                findminPanel.setVisible(true);
                ChartArea.add("Minima",minchartpanel);
                ChartArea.add("Optimization", clusterchartpanel);
                //f.pack();
            }
            else
            {
                if (DEBUG) PrintText("Closing minimum finder\n");
                findminPanel.setVisible(false);
                ChartArea.remove(minchartpanel);
                ChartArea.remove(clusterchartpanel);
                XYPlot xyplot = (XYPlot)chart.getPlot();
                xyplot.setDomainCrosshairVisible(false);
                //f.pack();
            }
            //garbage collect
            //System.gc();
        }
    }
    
    public class ListenMenuSetAxes implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (mnuItemSetAxes.getState() == true)
            {
                if (DEBUG) PrintText("Opening domain settings\n");
                mindomainText.setText(Double.toString(MIN));
                maxdomainText.setText(Double.toString(MAX));
                setaxesPanel.setVisible(true);
                //f.pack();
            }
            else
            {
                if (DEBUG) PrintText("Closing domain settings\n");
                setaxesPanel.setVisible(false);
                //f.pack();
            }
            //garbage collect
            //System.gc();
        }
    }
    
    //toggle gridlines on and off
    public class ListenMenuShowGrid implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //get plot
            XYPlot xyplot = (XYPlot)chart.getPlot();
            
            //find menu item state and act accordingly
            if(mnuItemShowGrid.getState() == true)
            {
                if (DEBUG) PrintText("Turning gridlines on\n");
                xyplot.setDomainGridlinesVisible(true);
                xyplot.setRangeGridlinesVisible(true);
            }
            else
            {
                if (DEBUG) PrintText("Turning gridlines off\n");
                xyplot.setDomainGridlinesVisible(false);
                xyplot.setRangeGridlinesVisible(false);
            }
        }
    }
    
    //save cluster locations
    public class ListenMenuSaveClusters implements ActionListener,Runnable
    {
        public void actionPerformed(ActionEvent e)
        {
            ListenMenuSaveClusters t = new ListenMenuSaveClusters();
            new Thread(t).start();
            javax.swing.SwingUtilities.invokeLater(new Runnable()
                                                   {
                public void run()
                {
                    jprogressbar.updateUI();
                }
            });
        }
        public void run()
        {
            String wd = System.getProperty("user.dir");
            JFileChooser fc;
            int rc;
            
            //save cluster data
            fc = new JFileChooser(wd);
            fc.setDialogType(fc.SAVE_DIALOG);
            rc = fc.showDialog(null, "Save " + regex + " Clusters");
            if (rc == JFileChooser.APPROVE_OPTION)
            {
                //set up progress bar
                showProgressbar("Saving " + regex + " cluster locations...");

                if (DEBUG) PrintText("Saving " + regex + " Clusters for N = " + CurrCG + "\n");
                File file = fc.getSelectedFile();
                
                //locate the minimum on the chart
                if (minimadataset.getItemCount() != 0)
                {
                    boolean hasMin = false;
                    for (int i = 0; i < minimadataset.getItemCount(); i++)
                    {
                        if (minimadataset.getYValue(0,i) == CurrCG)
                        {
                            SaveCGCluster(minimadataset.getXValue(0,i),file);
                            hasMin = true;
                            break;
                        }
                    }
                    if (!hasMin) PrintText("No minimum recorded for the current number of CGs\n");
                }
                else if (DEBUG) PrintText("No minima data for computation\n");
            }
            else if(DEBUG) PrintText("Cancel Save\n");
            
            //get rid of progress bar
            hideProgressbar();
            
            //garbage collect
            //System.gc();
            return;
        }
    }
    
    public class ListenMenuChartProps implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            chartpanel.doEditChartProperties();
        }
    }
    
    public class ListenMenuMinimaProps implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            minchartpanel.doEditChartProperties();
        }
    }
    
    public class ListenMenuClusterProps implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            clusterchartpanel.doEditChartProperties();
        }
    }
    
    private class FileExtFilter extends FileFilter
    {
        //filter for *.fa files
        public boolean accept(File f)
        {
            if (f.isDirectory())
            {
                return true;
            }
            
            String extension = getExtension(f);
            if (extension != null)
            {
                if (extension.equals("fa")) return true;
                else return false;
            }
            return false;
        }
        
        //Filter name/description
        public String getDescription()
        {
            return "Fasta Files";
        }
    }
    
    //find the extension of a given file
    private String getExtension(File f)
    {
        if(f != null)
        {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if(i>0 && i<filename.length()-1)
            {
                return filename.substring(i+1).toLowerCase();
            }
        }
        return null;
    }
    
    
    public class ListenMenuOpenFasta implements ActionListener, Runnable
    {
        public void actionPerformed(ActionEvent e)
        {
            ListenMenuOpenFasta t = new ListenMenuOpenFasta();
            new Thread(t).start();
            javax.swing.SwingUtilities.invokeLater(new Runnable()
                                                   {
                public void run()
                {
                    jprogressbar.updateUI();
                }
            });
        }
        public void run()
        {
            String wd = System.getProperty("user.dir");
            JFileChooser fc = new JFileChooser(wd);
            
            //allow for multiple files
            fc.setMultiSelectionEnabled(true);
            
            //set filter
            fc.addChoosableFileFilter(new FileExtFilter());
            fc.setAcceptAllFileFilterUsed(false);
            
            int rc = fc.showDialog(null, "Open");
            if (rc == JFileChooser.APPROVE_OPTION)
            {
                if (!regex.equals(searchPatternText.getText())) {
                    regex = searchPatternText.getText();
                }
                
                showProgressbar("Loading files...");
                loadFiles(fc.getSelectedFiles());
                
                updateProgressbar("Generating graph...");
                plotData();

                //enable pattern searching
                searchPatternLabel.setVisible(true);
                searchPatternText.setVisible(true);
                caseCheckBox.setVisible(true);

                hideProgressbar();
            }
            else if(DEBUG) PrintText("Cancel Open\n");
            
            //garbage collect
            //System.gc();
            return;
        }
    }

    public class ListenMenuOpenBed implements ActionListener, Runnable
    {
        public void actionPerformed(ActionEvent e)
        {
            ListenMenuOpenBed t = new ListenMenuOpenBed();
            new Thread(t).start();
            javax.swing.SwingUtilities.invokeLater(new Runnable()
                                                   {
                public void run()
                {
                    jprogressbar.updateUI();
                }
            });
        }
        public void run()
        {
            String wd = System.getProperty("user.dir");
            JFileChooser fc = new JFileChooser(wd);
            
            //allow for multiple files
            fc.setMultiSelectionEnabled(true);
            
            //set filter
            fc.addChoosableFileFilter(new FileExtFilter());
            fc.setAcceptAllFileFilterUsed(false);
            
            int rc = fc.showDialog(null, "Open");
            if (rc == JFileChooser.APPROVE_OPTION)
            {
                if (!regex.equals(searchPatternText.getText())) {
                    regex = searchPatternText.getText();
                }
                
                showProgressbar("Loading files...");
                loadBedFiles(fc.getSelectedFiles());
                
                updateProgressbar("Generating graph...");
                plotData();
                
                //disable pattern searching
                searchPatternLabel.setVisible(false);
                searchPatternText.setVisible(false);
                caseCheckBox.setVisible(false);

                hideProgressbar();                
            }
            else if(DEBUG) PrintText("Cancel Open\n");
            
            //garbage collect
            //System.gc();
            return;
        }
    }
    
    
    private void showProgressbar(String text) {
        //set up progress bar
        jprogressbar.setVisible(true);
        jprogressbar.setIndeterminate(true);
        jprogressbar.setStringPainted(true);
        f.pack();
        
        PrintText(text + "\n");
    }
    
    private void updateProgressbar (String text) {
        jprogressbar.setString(text);
        PrintText(text + "\n");
    }
    
    private void hideProgressbar() {
        jprogressbar.setVisible(false);
        f.pack();
    }
    
    public void loadFiles(File[] files)
    {
        fileList = files;

        //clear previous graph
        clearData();
        
        //set up files to be loaded
        String fileName;
        Fasta fasta = new Fasta(files);

        Fragment fragment;
        Integer[] posList;
        int numFeatures = Integer.parseInt(CGcurrText.getText());        
        boolean caseSensitive = caseCheckBox.isSelected();
 
        //calculate fragment lengths and build histogram
        updateProgressbar("Calculating fragment lengths...\n");
        
        key = fasta.getNameList();
        lengthHist = new XYSeries(key, true, false); // autosort, no duplicates
        fasta.setRegex(regex, caseSensitive);
        
        for (String name : fasta.getNames()) {
                        
            posList = fasta.getPositionList(name);
            
            
            if (posList.length > 0) {
                fragment = new Fragment(posList, numFeatures);
                addData(key, fragment);
                fragmentMap.put(name, fragment);
            }

            if (DEBUG) PrintText("Completed loading " + name + " (" + posList.length + " positions)\n");
        }
        
        
    }

    public void loadBedFiles(File[] files)
    {
        fileList = files;
        
        //clear previous graph
        clearData();
        
        //set up files to be loaded
        String fileName;
        Bed bed = new Bed(files);
        
        Fragment fragment;
        Integer[] posList;
        int numFeatures = Integer.parseInt(CGcurrText.getText());
        
        //calculate fragment lengths and build histogram
        updateProgressbar("Calculating fragment lengths...\n");
        
        key = bed.getNameList();
        
        lengthHist = new XYSeries(key, true, false); // autosort, no duplicates
        
        for (String name : bed.getNames()) {
            
            posList = bed.getPositionList(name);

            if (posList.length > 0) {
                fragment = new Fragment(posList, numFeatures);
                addData(key, fragment);
                fragmentMap.put(name, fragment);
            }
            
            if (DEBUG) PrintText("Completed loading " + name + " (" + posList.length + " positions)\n");
        }
        
        
    }
    
    //export data
    public class ListenMenuSavePositions implements ActionListener,Runnable
    {
        public void actionPerformed(ActionEvent e)
        {
            ListenMenuSavePositions t = new ListenMenuSavePositions();
            new Thread(t).start();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    jprogressbar.updateUI();
                }
            });
        }
        public void run()
        {
            String wd = System.getProperty("user.dir");
            JFileChooser fc;
            int rc;
            
            //save position data
            fc = new JFileChooser(wd);
            rc = fc.showDialog(null, "Export " + regex + " positions");
            if (rc == JFileChooser.APPROVE_OPTION)
            {
                //set up progress bar
                showProgressbar("Exporting " + regex + " fragment locations...");
                
                File file = fc.getSelectedFile();
                savePosData(file);
                
                //get rid of progress bar
                hideProgressbar();
            }
            else if(DEBUG) PrintText("Cancel Save\n");
            
            //garbage collect
            //System.gc();
            return;
        }
    }
    
    //This class save the histogram plot in grayscal
    public class ListenMenuSaveGrayChrt implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SaveGrayChrt();
        }
    }
    
    //This class saves the histogram plot
    public class ListenMenuSaveChrt implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SaveChrt();
        }
    }
    
    public class ListenMenuSaveGrayMinChrt implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SaveGrayMinChrt();
        }
    }
    
    public class ListenMenuSaveMinChrt implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SaveMinChrt();
        }
    }
    
    public class ListenMenuSaveGrayClusterChrt implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //save
            SaveGrayClusterChrt();
        }
    }
    
    public class ListenMenuSaveClusterChrt implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SaveClusterChrt();
        }
    }
    
    public class ListenMenuQuit implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //System.gc();
            System.exit(0);
        }
    }
    
    public class ListenClearData implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            clearData();
            if (DEBUG) PrintText("Data Cleared\n");
        }
    }
    
    public class ListenCloseWdw extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            //System.gc();
            System.exit(0);
        }
    }
    
    public void loadText(String FileName)
    {
        InputStream in = getClass().getResourceAsStream(FileName);
        try
        {
            SeqText.read(new InputStreamReader(in), null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //System.gc();
    }
    
    public void PrintText(String text) {
        if (text != null) {
            SeqText.append(text);
            SeqText.setCaretPosition(SeqText.getDocument().getLength());
        }
    }
    
    //Displays the main frame of the program
    public void show(){
        //show window frame
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack(); //Adjusts panel to components for display
        f.setVisible(true);
    }
    
    private void SaveChrt() {
        try {
            //remove crosshairs
            XYPlot xyplot = (XYPlot)chart.getPlot();
            xyplot.setDomainCrosshairVisible(false);
            
            //remove marker
            if (intervalmarker != null) {
                xyplot.removeDomainMarker(intervalmarker, Layer.BACKGROUND);
                intervalmarker = null;
            }
            
            //save
            chartpanel.doSaveAs();
        }
        catch (IOException err) {
            if(DEBUG) PrintText("Error: " + err);
        }
        
        //garbage collect
        //System.gc();
    }
    
    private void SaveClusterChrt() {
        if (mnuItemFindMin.getState() == false) {
            PrintText("Error: No optimization chart present\n");
            return;
        }
        try {
            clusterchartpanel.doSaveAs();
        }
        catch (IOException err) {
            if(DEBUG) PrintText("Error: " + err);
        }
    }
    
    private void SaveMinChrt() {
        if (mnuItemFindMin.getState() == false) {
            PrintText("Error: No minima chart present\n");
            return;
        }
        
        try {
            minchartpanel.doSaveAs();
        }
        catch (IOException err) {
            if(DEBUG) PrintText("Error: " + err);
        }
    }
    
    private void SaveGrayChrt() {
        //get chart and renderer
        XYPlot xyplot = (XYPlot)chart.getPlot();
        XYItemRenderer renderer = xyplot.getRenderer();
        
        //set gray
        Paint defaultPaint = renderer.getSeriesPaint(0);
        renderer.setSeriesPaint(0,Color.GRAY);
        xyplot.setRenderer(0,renderer);
        
        //save
        SaveChrt();
        
        //restore color
        renderer.setSeriesPaint(0,defaultPaint);
        xyplot.setRenderer(0,renderer);
    }
    
    private void SaveGrayClusterChrt() {
        //get chart and renderer
        XYPlot xyplot = (XYPlot)clusterchart.getPlot();
        XYItemRenderer renderer = xyplot.getRenderer();
        
        //set gray
        Paint defaultPaint = renderer.getSeriesPaint(0);
        renderer.setSeriesPaint(0,Color.GRAY);
        xyplot.setRenderer(0,renderer);
        
        //save
        SaveClusterChrt();
        
        //restore color
        renderer.setSeriesPaint(0,defaultPaint);
        xyplot.setRenderer(0,renderer);
    }
    
    private void SaveGrayMinChrt() {
        //get chart and renderer
        XYPlot xyplot = (XYPlot)minchart.getPlot();
        XYItemRenderer renderer0 = xyplot.getRenderer(0);
        XYItemRenderer renderer1 = xyplot.getRenderer(1);
        
        //set gray
        Paint defaultPaint0 = renderer0.getSeriesPaint(0);
        Paint defaultPaint1 = renderer1.getSeriesPaint(0);
        renderer0.setSeriesPaint(0,Color.LIGHT_GRAY);
        renderer1.setSeriesPaint(0,Color.DARK_GRAY);
        xyplot.setRenderer(0,renderer0);
        xyplot.setRenderer(1,renderer1);
        
        //save
        SaveMinChrt();
        
        //restore color
        renderer0.setSeriesPaint(0,defaultPaint0);
        renderer1.setSeriesPaint(0,defaultPaint1);
        xyplot.setRenderer(0,renderer0);
        xyplot.setRenderer(1,renderer1);
    }
    
    //clears all the data
    public void clearData()
    {
        //global variables reset
        MIN = Double.MAX_VALUE;
        MAX = 0;
        BINS = 10000;
        key = "";
        regLine = null;
        
        //clear min settings
        minText.setText("");
        maxText.setText("");
        smoothText.setText("");
        fragmentMap.clear();
		
        //new data sets
        lengthHist = null;
        histogramdataset = new XYSeriesCollection();
        minimadataset = new CategoryTableXYDataset();
        clusterdataset = new CategoryTableXYDataset();
        
        //chart area reset
        XYPlot xyplot = (XYPlot)chart.getPlot();
        xyplot.setDataset(new XYSeriesCollection());
        chartpanel.restoreAutoBounds();
        
        XYPlot minxyplot = (XYPlot)minchart.getPlot();
        minxyplot.setDataset(0, minimadataset);
        minxyplot.setDataset(1, new DefaultXYDataset());
        minchart.clearSubtitles();
        minchartpanel.restoreAutoBounds();
        
        XYPlot clusterxyplot = (XYPlot)clusterchart.getPlot();
        clusterxyplot.setDataset(clusterdataset);
        clusterchartpanel.restoreAutoBounds();
        
        //remove marker
        if (intervalmarker != null) {
            xyplot = (XYPlot)chart.getPlot();
            xyplot.removeDomainMarker(intervalmarker, Layer.BACKGROUND);
            intervalmarker = null;
        }
        
        //remove crosshair
        xyplot.setDomainCrosshairVisible(false);
        
        //clear text
        minText.setText(null);
        maxText.setText(null);
        smoothText.setText(null);
        
        //f.pack();
        //System.gc();
    }
    
    //replaces the data currently in the chart with new data
    //Variables in: series, data
    //Variables out: lengthHist
    public void replaceData(Comparable series) {
        //clear memory first
        lengthHist = new XYSeries(series, true, false);

        //add data

        if (fragmentMap.size() > 0) {
            Iterator it = fragmentMap.entrySet().iterator();
            
            while(it.hasNext()) {
                Map.Entry mapEntry = (Map.Entry)it.next();
                Fragment f = (Fragment)mapEntry.getValue();
                addData(series, f);
            }
        }
        
		plotData();
        
        
    }
    
    public void addData(Comparable series, Fragment fragment)
    {
        int index;

        if (fragment.getNumFragments() > 0) {
            
            for (double length : fragment.getLengths()) {
                index = lengthHist.indexOf(length);
                
                if (index >= 0) {
                    lengthHist.update(lengthHist.getX(index), lengthHist.getY(index).doubleValue() + 1.0);
                }
                else {
                    lengthHist.add(length, 1.0);
                }
            }
        }
        
    }
	
    
	public void plotData() {

        int smoothParam;
        
        XYSeries xyseries = lengthHist;
        histogramdataset = new XYSeriesCollection();

        if (lengthHist.getItemCount() > 0) {

            MIN = lengthHist.getMinX();
            MAX = lengthHist.getX((int)(2 * lengthHist.getItemCount() / 3)).doubleValue(); // 75th percentile
            
            if (smoothCheckBox.isSelected()) {

                if (smoothText.getText().length() == 0) {
                    smoothParam = (int)(20*(BINS/(MAX -  MIN + 1)));
                    smoothText.setText(Integer.toString(smoothParam));
                }

                smoothParam = Integer.parseInt(smoothText.getText());
                if (DEBUG) PrintText("Smoothing parameter:" + smoothParam + "\n");
                
                xyseries = MovingAverage(smoothParam);
            }

            
            // plotting
            XYPlot xyplot = (XYPlot)chart.getPlot();

            // adjust axes
            ValueAxis domainAxis = xyplot.getDomainAxis();
            domainAxis.setRange(MIN, MAX);

            // add data
            histogramdataset.addSeries(xyseries);
            xyplot.setDataset(histogramdataset);
            
            //turn on crosshairs
            xyplot.setDomainCrosshairVisible(true);
            xyplot.setDomainCrosshairLockedOnData(false);
            
        }
		
	}
    
    public void addMinima(double value)
    {        
        //updata dataset and graph
        if (value != -1)
        {            
            //remove previous minima for this number of CGs
            for (int i = 0; i < minimadataset.getItemCount(); i++)
            {                
                if (minimadataset.getYValue(0,i) == CurrCG)
                {
                    minimadataset.remove(minimadataset.getX(0,i), (String)key, true);
                    break;
                }
            }
            
            //add new minima
            minimadataset.add(value, (double) CurrCG, (String)key);
        }
        else return;
        
         
        //get plot
        XYPlot xyplot = minchart.getXYPlot();
        minchart.clearSubtitles();

        
        //set render options
        XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();        
        
        Shape shape = xyplot.getRenderer().getSeriesShape(0);
        renderer1.setSeriesLinesVisible(0, false);
        renderer1.setSeriesPaint(0,Color.RED);
        renderer1.setSeriesShape(0, shape);
        if (minimadataset.getItemCount() > 1)
        {
            renderer2.setSeriesLinesVisible(0, true);
            renderer2.setSeriesShapesVisible(0, false);
            renderer2.setBaseSeriesVisibleInLegend(false);
            renderer2.setSeriesPaint(0,Color.BLUE);
        }
 

        //get regression
        LineFunction2D localregline = null;
        XYDataset regseries;
        if (minimadataset.getItemCount() > 1)
        {
            //create the regression
            double[] reg = Regression.getOLSRegression(minimadataset,0);
            //looking for x values so put x in terms of y
            //y = a + bx 	 for local
            //x = y/b - a/b  for global
            if (DEBUG) PrintText("Regression: y = " + reg[0] + " + " + reg[1] + "x\n");
            
            //global (in terms of y)
            regLine = new LineFunction2D(-reg[0]/reg[1], 1/reg[1]);
            
            //local (in terms of x)
            localregline = new LineFunction2D(reg[0], reg[1]);
            regseries = DatasetUtilities.sampleFunction2D(localregline,  minimadataset.getDomainLowerBound(false),
                                                          minimadataset.getDomainUpperBound(false), 2, "Linear Regression");
            
            //plot line
            xyplot.setDataset(1,regseries);
            xyplot.setRenderer(1,renderer2);
            minchart.addSubtitle(0, new TextTitle("Regression: y = " + reg[0] + " + " + reg[1] + "x"));			
        }
        
        xyplot.setDataset(0,minimadataset);
        xyplot.setRenderer(0,renderer1);
       
        //f.pack();
        
        //System.gc();
    }
    
    public void addClusterSize (double value)
    {
        //updata dataset and graph
        if (value != -1)
        {
            clusterdataset.add((double) CurrCG, value, (String)key);
        }
        clusterchartpanel.restoreAutoBounds();
        XYPlot xyplot = clusterchart.getXYPlot();
        xyplot.setDataset(clusterdataset);
    }
    
    //as of now seperator must be '\n', so the input seperator is tossed
    private void openCGdata (File file, String seperator)
    {
        String FileName = file.getAbsolutePath();
        try
        {
            //Create data stream from buffered input
            BufferedReader b = new BufferedReader(new FileReader(FileName));
            
            //clear previous graph
            clearData();
            
            //initialize data
            Fragment fragment;
			Integer[] posList;
			int numFeatures = Integer.parseInt(CGcurrText.getText());
			String name;
			
            fragmentMap.clear();

            int TotSeries = Integer.parseInt(b.readLine());
            key = "";
            for (int j = 0; j < TotSeries; j++)
            {
                //get header
				name = b.readLine();
                key = key + name;
				
                //initialize CGdata
                int CGdatalength = Integer.parseInt(b.readLine());
				posList = new Integer[CGdatalength];
				
                for (int i = 0; i < CGdatalength; i++)
                {
					posList[i] = Integer.parseInt(b.readLine());
                }

				fragment = new Fragment(posList, numFeatures);
				fragmentMap.put(name, fragment);

            }
            if (DEBUG) PrintText("Opened " + regex + " locations: " + FileName + "\n");
            b.close();
        }
        catch (IOException err)
        {
            if (DEBUG) PrintText("Error: " + err + "\n");
        }
        //garbage collect
        //System.gc();
    }
    
    public void savePosData (File file)
    {
        String FileName = file.getAbsolutePath();
        try
        {
            //Start permanent file
            FileOutputStream fos = new FileOutputStream(FileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            PrintWriter pw = new PrintWriter(bos);

            for (Map.Entry<String, Fragment> mapEntry : fragmentMap.entrySet()) {
                String name = mapEntry.getKey();
                Fragment f = mapEntry.getValue();

                pw.print("track name=\"" + mapEntry.getKey() + "\" description=\"" + regex + " positions\"\n");
				
				for (Integer pos : f.getPositions()) {
					pw.print(mapEntry.getKey() + "\t" + Integer.toString(pos) + "\t" + Integer.toString(pos) + "\n");
				}
            }
            
            if (DEBUG) PrintText("Saved " + regex + " locations: " + FileName + "\n");
            pw.close();
            bos.close();
            fos.close();
        }
        catch (IOException err)
        {
            if (DEBUG) PrintText("Error: " + err + "\n");
        }
        //garbage collect
        //System.gc();
    }
    
    public void SaveCGCluster(double minimum, File file)
    {
        
        String FileName = file.getAbsolutePath();
        String ext = getExtension(file);
        int numFeatures = Integer.parseInt(CGcurrText.getText());
        
        if (ext == null)
        {
            FileName = FileName + ".bed";
        }
        try
        {
            //Start permanent file
            FileOutputStream fos = new FileOutputStream(FileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            PrintWriter pw = new PrintWriter(bos);
            
            Cluster cluster = new Cluster(fragmentMap, numFeatures, minimum);
			Fragment fragment;
			String chrm;			
            int NumClusters = 0;
            
            //        pw.print("Start Position \t End Position\n");
            
            pw.print("track name=\"CG clusters\" description=\"CG clusters (" + numFeatures + "," + minimum + "\"\n");
            
			for (String[] row : cluster.getClusterList(minimum)) {
					pw.print(row[0] + "\t" + row[1] + "\t" + row[2] + "\n");
			}
			
            if (DEBUG) PrintText("Total number of clusters found: " + cluster.getNumClusters() + "\n");
            
            pw.close();
            bos.close();
            fos.close();
        }
        catch (IOException err)
        {
            if (DEBUG) PrintText("Error: " + err + "\n");
        }
        
        //garbage collect
        //System.gc();
    }
    
    
    //calculates a moving average of window size M on the Lengths array
    //Variables in: Lengths, chart
    //Variables out: N/A it changes the chart display only
    public XYSeries MovingAverage(int M) {
        //get chart

        int N = lengthHist.getItemCount();
        Double NewX, NewY;
        
        XYPlot xyplot = (XYPlot)chart.getPlot();
        XYSeries xyseries = new XYSeries(key);

        for (int i = 0; i < N - M; i++) {
            NewY = 0.0;
            NewX = (Double)lengthHist.getX(i);

            for (int j = 0; j < M; j++) {
                NewY += (Double)lengthHist.getY(i+j);
            }
            NewY /= M;
            
            xyseries.add(NewX, NewY);
            
        }
                
        return(xyseries);
    }
    
	
    public XYSeries MovingMedian(int M) {
        //get chart
        int N = lengthHist.getItemCount();
        Double[] mArray = new Double[M];
		Double NewX, NewY;
		
        XYPlot xyplot = (XYPlot)chart.getPlot();
        XYSeries xyseries = new XYSeries(key);
        
        for (int i = 0; i < N - M; i++) {

			NewX = (Double)lengthHist.getX(i);
			
            for (int j = 0; j < M; j++) {
				mArray[j] = (Double)lengthHist.getY(i + j);
            }
            
            Arrays.sort(mArray);
            
            if (mArray.length % 2 == 0) {
                NewY = mArray[mArray.length / 2];
            }
            else {
                NewY = (mArray[mArray.length / 2] + mArray[(mArray.length / 2) + 1]) / 2;
            }
            
            xyseries.add(NewX,NewY);
        }
        
        return(xyseries);
    }
	
	public XYSeries LowPass(int M) {
		int N = lengthHist.getItemCount();
        int i = 0;
        Double NewX, NewY, CurrY;
		
        XYPlot xyplot = (XYPlot)chart.getPlot();
        XYSeries xyseries = new XYSeries(key);
		NewY = (Double)lengthHist.getX(0);
		
        
        // add pad
		while (i < M) {
            xyseries.add(i, 0);
        }
        
        // add rest of data
        for (i = M; i < N; i++) {
            xyseries.add(lengthHist.getX(i), lengthHist.getY(i));
        }
		
        // filter
        for (i = 1; i < N; ++i) {
			
			CurrY = (Double)xyseries.getY(i);
			
            NewX = (Double)xyseries.getX(i);
			NewY += (CurrY - NewY) / M;
			
            xyseries.update(NewX, NewY);
        }
        
        return(xyseries);
	}
	
	
      
    
    public static void main(String [ ] args)
    {
        //initialize gui
        
        try {
            CGgui CGinterface = new CGgui();
            CGinterface.show();

        /*Code for Debugging purposes*/
        
        //garbage collect
        Runtime r = Runtime.getRuntime();
        r.gc();
        }
        catch (IndexOutOfBoundsException e) {
            System.err.println("Caught out of bounds exception");
            System.exit(-1);
        }
        
        //grab file
        //	CGinterface.CGdata = CGinterface.LoadSeq("chr21_random.fa");
        
        //process data
        //find distances of 30
        //	double[] length = CGinterface.getLengths(30);
        
        //graph data
        //	CGinterface.addData(CGinterface.header,length,100000,CGinterface.MIN,CGinterface.MAX);
        //	CGinterface.MovingAverage(150);
        //	CGinterface.Bezier(.01);
    }
}
