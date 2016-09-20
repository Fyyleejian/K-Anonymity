package App.View;

import App.Controller.DataSetController;
import App.Controller.ExecuteController;
import App.Model.AlgoType;
import App.Model.DataSetModel;
import App.Model.InputContext;
import App.UITasks.DataSetLoaderTask;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Keinan.Gilad on 9/10/2016.
 */
public class AppFrame extends JFrame {
    public static final String ORIGINAL_CHARTS = "Original Charts";
    public static final String EXECUTE = "Execute";
    private static Logger logger = Logger.getLogger(AppFrame.class);

    public static final String ALGORITHMS = "Algorithms";
    public static final String CHOOSE_K = "Choose K";
    public static final String DATA_SETS = "Data Sets";
    private static final String FRAME_TITLE = "K-Anonymity Algorithm Simulator";
    private static final String DEFAULT_VALUE = "N/A";
    @Autowired
    private ExecuteController executeController;

    @Autowired
    private DataSetController dataSetController;

    private List<String> dataSetsNames;
    private ButtonGroup buttonGroupAlgorithms;
    private ButtonGroup buttonGroupForK;
    private ButtonGroup buttonGroupForDataSets;
    private List<JRadioButton> dataSetsRadioButtons;
    private HashMap<String, JProgressBar> dataSetToProgressBar;
    private JTabbedPane tabbedPane;
    private JPanel chartsPanel;

    @SuppressWarnings("ALL")
    public void initUIComponents() {
        // set layout properties
        final Container contentPane = getContentPane();
        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        setTitle(FRAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // init controls:
        JLabel algorithmsLabel = new JLabel(ALGORITHMS);
        JLabel chooseKLabel = new JLabel(CHOOSE_K);
        JLabel chooseDataSetLabel = new JLabel(DATA_SETS);

        // radio buttons for algorithms
        buttonGroupAlgorithms = new ButtonGroup();
        JRadioButton radioButtonAlgorithms1 = new JRadioButton("K-Degree");
        radioButtonAlgorithms1.setActionCommand(AlgoType.KDegree.toString());
        radioButtonAlgorithms1.setSelected(true);
        JRadioButton radioButtonAlgorithms2 = new JRadioButton("K-Neighborhood");
        radioButtonAlgorithms2.setActionCommand(AlgoType.KNeighborhood.toString());
        buttonGroupAlgorithms.add(radioButtonAlgorithms1);
        buttonGroupAlgorithms.add(radioButtonAlgorithms2);

        // radio buttons for choosing K
        buttonGroupForK = new ButtonGroup();
        JRadioButton radioButtonForK1 = new JRadioButton("5");
        radioButtonForK1.setActionCommand("5");
        radioButtonForK1.setSelected(true);
        JRadioButton radioButtonForK2 = new JRadioButton("10");
        radioButtonForK2.setActionCommand("10");
        JRadioButton radioButtonForK3 = new JRadioButton("15");
        radioButtonForK3.setActionCommand("15");
        JRadioButton radioButtonForK4 = new JRadioButton("20");
        radioButtonForK4.setActionCommand("20");
        buttonGroupForK.add(radioButtonForK1);
        buttonGroupForK.add(radioButtonForK2);
        buttonGroupForK.add(radioButtonForK3);
        buttonGroupForK.add(radioButtonForK4);

        // radio buttons for choosing Data Sets
        initDataSetControls();

        // progress bar for loading the algorithm
        JProgressBar executeProgressBar = new JProgressBar(0, 100);
        executeProgressBar.setBounds(0, 0, 100, 100);
        executeProgressBar.setValue(0);
        executeProgressBar.setStringPainted(true);
        executeProgressBar.setMaximumSize(new Dimension(130, 25));
        executeProgressBar.setPreferredSize(new Dimension(130, 25));
        executeProgressBar.setSize(new Dimension(130, 25));

        // values for after running the algorithm
        JLabel durationLabel = new JLabel("Duration");
        JLabel durationLabelValue = new JLabel(DEFAULT_VALUE);

        JLabel afterVerticesLabel = new JLabel("Vertices total");
        JLabel afterVerticesLabelValue = new JLabel(DEFAULT_VALUE);

        JLabel afterVerticesAddedLabel = new JLabel("Vertices added");
        JLabel afterVerticesAddedLabelValue = new JLabel(DEFAULT_VALUE);

        JLabel afterVerticesRemovedLabel = new JLabel("Vertices removed");
        JLabel afterVerticesRemovedLabelValue = new JLabel(DEFAULT_VALUE);

        JLabel afterEdgesLabel = new JLabel("Edges total");
        JLabel afterEdgesLabelValue = new JLabel(DEFAULT_VALUE);

        JLabel afterEdgesAddedLabel = new JLabel("Edges added");
        JLabel afterEdgesAddedLabelValue = new JLabel(DEFAULT_VALUE);

        JLabel afterEdgesRemovedLabel = new JLabel("Edges removed");
        JLabel afterEdgesRemovedLabelValue = new JLabel(DEFAULT_VALUE);

        JLabel afterObfuscationLeveldLabel = new JLabel("Obfuscation Level");
        JLabel afterObfuscationLeveldLabelValue = new JLabel(DEFAULT_VALUE);

        JSeparator sp1 = new JSeparator();

        // graphs
        tabbedPane = new JTabbedPane();
        chartsPanel = new JPanel();
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.X_AXIS));
        tabbedPane.add(chartsPanel, ORIGINAL_CHARTS);

        // execute button
        JButton executeButton = new JButton(EXECUTE);
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String algoTypeCommand = buttonGroupAlgorithms.getSelection().getActionCommand();
                String kTypeCommand = buttonGroupForK.getSelection().getActionCommand();
                String dataSetCommand = buttonGroupForDataSets.getSelection().getActionCommand();
                logger.info(String.format("Execute Button called for {Algorithm=%s, K=%s, DataSet=%s}", algoTypeCommand, kTypeCommand, dataSetCommand));

                // create the input context with all the needed information for execute
                InputContext inputContext = new InputContext();
                inputContext.setAlgoTypeCommand(algoTypeCommand);
                inputContext.setKTypeCommand(kTypeCommand);
                inputContext.setDataSetCommand(dataSetCommand);
                executeController.execute(inputContext);
            }
        });

        // setting the Layout
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        // column 0
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(algorithmsLabel)
                                .addComponent(radioButtonAlgorithms1)
                                .addComponent(radioButtonAlgorithms2)

                                // level 2
                                .addComponent(executeButton)
                        )
                        // column 1
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(chooseKLabel)
                                .addComponent(radioButtonForK1)
                                .addComponent(radioButtonForK2)
                                .addComponent(radioButtonForK3)
                                .addComponent(radioButtonForK4)

                                // level 2
                                .addComponent(executeProgressBar)
                        )
                        // column 2
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(chooseDataSetLabel)
                                .addComponent(dataSetsRadioButtons.get(0))
                                .addComponent(dataSetsRadioButtons.get(1))
                                .addComponent(dataSetsRadioButtons.get(2))
                        )
                        // column 3
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(dataSetToProgressBar.get(dataSetsNames.get(0)))
                                .addComponent(dataSetToProgressBar.get(dataSetsNames.get(1)))
                                .addComponent(dataSetToProgressBar.get(dataSetsNames.get(2)))
                        )
                )
                .addComponent(tabbedPane)
                .addComponent(sp1)
        );

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        // row 0
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(algorithmsLabel)
                                        .addComponent(radioButtonAlgorithms1)
                                        .addComponent(radioButtonAlgorithms2)
                                )
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(chooseKLabel)
                                        .addComponent(radioButtonForK1)
                                        .addComponent(radioButtonForK2)
                                        .addComponent(radioButtonForK3)
                                        .addComponent(radioButtonForK4)
                                )

                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(chooseDataSetLabel)

                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(dataSetsRadioButtons.get(0))
                                                .addComponent(dataSetToProgressBar.get(dataSetsNames.get(0)))
                                        )

                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(dataSetsRadioButtons.get(1))
                                                .addComponent(dataSetToProgressBar.get(dataSetsNames.get(1)))
                                        )

                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(dataSetsRadioButtons.get(2))
                                                .addComponent(dataSetToProgressBar.get(dataSetsNames.get(2)))
                                        )
                                )
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(executeButton)
                                .addComponent(executeProgressBar)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(sp1)
                                .addComponent(tabbedPane)
                        )
                )
        );

        MenuBar menubar = new MenuBar();
        final Menu fileMenu = new Menu("File");
        final MenuItem aboutItem = new MenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(contentPane, "Written by Keinan Gilad (keinan.gilad@gmail.com) for MSc in open university.");
            }
        });
        fileMenu.add(aboutItem);
        menubar.add(fileMenu);
        this.setMenuBar(menubar);

        // adding 3 table view to keep the space
        /*List<String> dataSetsNames = dataSetController.getDataSetsNames();
        for (String dataSet :dataSetsNames) {
            DataSetModel dataSetToModel = new DataSetModel();
            dataSetToModel.setTitle(dataSet);
            TableView table = new TableView(dataSetToModel);
            chartsPanel.add(table);
        }*/
        pack();
    }

    private List<JRadioButton> initDataSetControls() {
        dataSetsNames = dataSetController.getDataSetsNames();

        dataSetsRadioButtons = new ArrayList<>();
        dataSetToProgressBar = new HashMap<>();

        buttonGroupForDataSets = new ButtonGroup();

        for (final String dataSet : dataSetsNames) {
            JRadioButton dataSetRadioButton = new JRadioButton(dataSet);
            dataSetRadioButton.setActionCommand(dataSet);

            buttonGroupForDataSets.add(dataSetRadioButton);
            dataSetsRadioButtons.add(dataSetRadioButton);

            // create the data set progress bar for loading
            JProgressBar progressBar = new JProgressBar();
            progressBar.setBounds(0, 0, 100, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setMaximumSize(new Dimension(130, 25));
            progressBar.setPreferredSize(new Dimension(130, 25));
            progressBar.setSize(new Dimension(130, 25));
            dataSetToProgressBar.put(dataSet, progressBar);

            // load the data set into persistence
            createDataSetTask(dataSet);
        }

        dataSetsRadioButtons.get(0).setSelected(true);
        return dataSetsRadioButtons;
    }

    private void createDataSetTask(String dataSet) {
        DataSetLoaderTask task = new DataSetLoaderTask(dataSet, dataSetController);
        task.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                final String dataSet = ((DataSetLoaderTask) evt.getSource()).getDataSetName();
                if ("progress".equals(propertyName)) {
                    // update the progress bar with recent value
                    int progress = (Integer) evt.getNewValue();
                    JProgressBar progressBar = dataSetToProgressBar.get(dataSet);
                    progressBar.setValue(progress);
                    //logger.debug("Progress DataSet Event: " + dataSet + " Progress:" + progress);
                } else if ("done".equals(propertyName)) {
                    logger.debug("Start Graph Event: " + dataSet);
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            DataSetModel dataSetToModel = dataSetController.getDataSetToModel(dataSet);
                            TableView table = new TableView(dataSetToModel);
                            chartsPanel.add(table);
                            chartsPanel.revalidate();
                            chartsPanel.repaint();
                            logger.debug("Done Graph Event: " + dataSet);
                        }
                    });
                    thread.start();
                }
            }
        });
        task.execute();
    }
}