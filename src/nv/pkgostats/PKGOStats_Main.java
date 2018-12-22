/**
 * This class allows the user to enter visible stats of a Pokemon, and calculates its possible IVs.
 * Alternatively, the user can enter an individual Pokemon's species name and IVs, and see that Pokemon's stats at every level and half level.
 * Alternatively, the user can see Pokemon rankings for all species, based on various stats. 
 */

package nv.pkgostats;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class PKGOStats_Main implements ItemListener, ActionListener, FocusListener {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	// Data structures
	private final String speciesStatsFile = "species_stats.txt";
	private final String cpMultipliersFile = "cp_multipliers.txt";
	
	// Frame variables
	private final String frameTitle = "PKGO Stats";
	private final String discoverTitle = "Discover";
	private final String potentialTitle = "Potential";
	private final String rankingsTitle = "Rankings";
	private final String longestSpeciesNameString = "XXX Kangaskhan (Mega Kangaskhan)";
	private final String noFilterString = "NO FILTER SELECTED";
	private final String longestSortOptionString = "Max Potential CP";
	
	// Discover panel variables
	private final String[] discoverPowerUpOptions = {"10000/15", "9000/12", "8000/10", "7000/8", "6000/6", "5000/4", "4500/4", "4000/4", 
													 "4000/3", "3500/3", "3000/3", "2500/2", "2200/2", "1900/2", "1600/2", "1300/2", 
													 "1000/1", "800/1", "600/1", "400/1", "200/1"};
	private final String[] discoverOverallAnalysisOptions = {"Amazing", "Strong", "Average", "Poor"};
	private final String[] discoverHighestIVOptions = {"Attack", "Defense", "HP", "Attack/Defense", "HP/Attack", 
													   "HP/Defense", "HP/Attack/Defense"};
	private final String[] discoverHighestAnalysisOptions = {"Amazing", "Strong", "Average", "Poor"};
	private final String[] discoverTableColumns = {"Level", "CP", "HP", "Attack IV", "Defense IV", "Stamina IV", "IV Total"};
	
	private final JLabel discoverSpeciesNameLabel = new JLabel("Species Name:");
	private final JLabel discoverCPLabel = new JLabel("CP:");
	private final JLabel discoverHPLabel = new JLabel("HP:");
	private final JLabel discoverPowerUpLabel = new JLabel("Power Up Requirements:");
	private final JLabel discoverOverallAnalysisLabel = new JLabel("Leader's Overall Stat Analysis:");
	private final JLabel discoverHighestIVLabel = new JLabel("Highest Stat:");
	private final JLabel discoverHighestAnalysisLabel = new JLabel("Leader's Highest Stat Analysis:");
	
	// Potential panel variables
	private final Integer[] potentialIVOptions = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
	private final String[] potentialTableColumns = {"Level", "CP", "HP", "Attack", "Defense", "Stamina"};
	private final int potentialMaxLevel = 40;
	
	private final JLabel potentialSpeciesNameLabel = new JLabel("Species Name:");
	private final JLabel potentialAttackIVLabel = new JLabel("Attack IV:");
	private final JLabel potentialDefenseIVLabel = new JLabel("Defense IV:");
	private final JLabel potentialStaminaIVLabel = new JLabel("Stamina IV:");
	
	// Rankings panel variables
	private final String[] rankingsSortOptions = {"PokeDex #", "Max Potential CP", "Base Attack", "Base Defense", "Base Stamina", "Sum of Stats"};
	private final String[] rankingsTableColumns = {"Rank", "PokeDex #", "Species Name", "Max Potential CP", "Base Attack", "Base Defense", "Base Stamina"};
	
	private final JLabel rankingsSortLabel = new JLabel("Sort By:");
	
	//-----------------------------------------------------------------//
	
	/** Declare global variables **/
	
	private ArrayList<PKGOStats_Species> speciesMasterList;
	private ArrayList<PKGOStats_Species> speciesFilteredList;
	
	private ArrayList<Double> cpMultipliersList;
	
	private JCheckBox genICheckBox;
	private JCheckBox genIICheckBox;
	private JCheckBox genIIICheckBox;
	private JCheckBox genIVCheckBox;
	private JCheckBox genVCheckBox;
	private JCheckBox genVICheckBox;
	private JCheckBox genVIICheckBox;
	private JCheckBox legendariesCheckBox;
	private JCheckBox megaEvolutionsCheckBox;
	
	private JComboBox<String> discoverSpeciesNameComboBox;
	
	private JTextField discoverCPTextField;
	private JTextField discoverHPTextField;
	
	private JComboBox<String> discoverPowerUpComboBox;
	
	private JComboBox<String> discoverOverallAnalysisComboBox;
	private JComboBox<String> discoverHighestIVComboBox;
	private JComboBox<String> discoverHighestAnalysisComboBox;
	
	private JButton discoverCalculateButton;
	
	private JTable discoverTable;
	
	private JComboBox<String> potentialSpeciesNameComboBox;
	
	private JComboBox<Integer> potentialAttackIVComboBox;
	private JComboBox<Integer> potentialDefenseIVComboBox;
	private JComboBox<Integer> potentialStaminaIVComboBox;
	
	private JTable potentialTable;
	
	private JComboBox<String> rankingsSortComboBox;
	
	private JTable rankingsTable;
	
	//-----------------------------------------------------------------//
	
	/** Initialize global variables **/
	
	private void initVars() {
		generateSpeciesMasterList();
		speciesFilteredList = new ArrayList<PKGOStats_Species>();
		
		generateCPMultipliersList();
		
		genICheckBox = new JCheckBox("Include Generation I Pokemon");
		genICheckBox.addItemListener(this);
		genIICheckBox = new JCheckBox("Include Generation II Pokemon");
		genIICheckBox.addItemListener(this);
		genIIICheckBox = new JCheckBox("Include Generation III Pokemon");
		genIIICheckBox.addItemListener(this);
		genIVCheckBox = new JCheckBox("Include Generation IV Pokemon");
		genIVCheckBox.addItemListener(this);
		genVCheckBox = new JCheckBox("Include Generation V Pokemon");
		genVCheckBox.addItemListener(this);
		genVICheckBox = new JCheckBox("Include Generation VI Pokemon");
		genVICheckBox.addItemListener(this);
		genVIICheckBox = new JCheckBox("Include Generation VII Pokemon");
		genVIICheckBox.addItemListener(this);
		legendariesCheckBox = new JCheckBox("Include Legendary/Mythical Pokemon");
		legendariesCheckBox.addItemListener(this);
		megaEvolutionsCheckBox = new JCheckBox("Include Mega Evolutions");
		megaEvolutionsCheckBox.addItemListener(this);
		
		discoverSpeciesNameComboBox = new JComboBox<String>();
		discoverSpeciesNameComboBox.setPrototypeDisplayValue(longestSpeciesNameString);
		discoverSpeciesNameComboBox.setMaximumRowCount(16);
		
		discoverCPTextField = new JTextField("10", 5);
		discoverCPTextField.addFocusListener(this);
		discoverHPTextField = new JTextField("10", 5);
		discoverHPTextField.addFocusListener(this);
		
		discoverPowerUpComboBox = new JComboBox<String>(discoverPowerUpOptions);
		discoverPowerUpComboBox.setMaximumRowCount(16);
		
		discoverOverallAnalysisComboBox = new JComboBox<String>(discoverOverallAnalysisOptions);
		discoverHighestIVComboBox = new JComboBox<String>(discoverHighestIVOptions);
		discoverHighestAnalysisComboBox = new JComboBox<String>(discoverHighestAnalysisOptions);
		
		discoverCalculateButton = new JButton("Calculate");
		discoverCalculateButton.addActionListener(this);
		
		discoverTable = new JTable();
		JTableHeader discoverTableHeader = discoverTable.getTableHeader();
		discoverTableHeader.setReorderingAllowed(false);
		discoverTableHeader.setResizingAllowed(false);
		discoverTable.setFillsViewportHeight(true);
		discoverTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		potentialSpeciesNameComboBox = new JComboBox<String>();
		potentialSpeciesNameComboBox.setPrototypeDisplayValue(longestSpeciesNameString);
		potentialSpeciesNameComboBox.setMaximumRowCount(16);
		potentialSpeciesNameComboBox.addItemListener(this);
		
		potentialAttackIVComboBox = new JComboBox<Integer>(potentialIVOptions);
		potentialAttackIVComboBox.setMaximumRowCount(16);
		potentialAttackIVComboBox.addItemListener(this);
		potentialDefenseIVComboBox = new JComboBox<Integer>(potentialIVOptions);
		potentialDefenseIVComboBox.setMaximumRowCount(16);
		potentialDefenseIVComboBox.addItemListener(this);
		potentialStaminaIVComboBox = new JComboBox<Integer>(potentialIVOptions);
		potentialStaminaIVComboBox.setMaximumRowCount(16);
		potentialStaminaIVComboBox.addItemListener(this);
		
		potentialTable = new JTable();
		JTableHeader potentialTableHeader = potentialTable.getTableHeader();
		potentialTableHeader.setReorderingAllowed(false);
		potentialTableHeader.setResizingAllowed(false);
		potentialTable.setFillsViewportHeight(true);
		potentialTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		updatePotentialTable();
		
		rankingsSortComboBox = new JComboBox<String>(rankingsSortOptions);
		rankingsSortComboBox.setPrototypeDisplayValue(longestSortOptionString);
		rankingsSortComboBox.addItemListener(this);
		
		rankingsTable = new JTable();
		JTableHeader rankingsTableHeader = rankingsTable.getTableHeader();
		rankingsTableHeader.setReorderingAllowed(false);
		rankingsTableHeader.setResizingAllowed(false);
		rankingsTable.setFillsViewportHeight(true);
		rankingsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		rankingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		updateRankingsTable();
		
		genICheckBox.setSelected(true);
	}
	
	//-----------------------------------------------------------------//
	
	/** Main method and class declaration/initialization **/
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new PKGOStats_Main().start();
			}
		});
	}
	
	//-----------------------------------------------------------------//
	
	/** Abstract methods **/
	
	
	
	//-----------------------------------------------------------------//
	
	/** Implemented methods **/
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource().getClass().getSimpleName().equals("JComboBox")) {
			// Source of ItemEvent is a JComboBox
			if (e.getSource() == potentialSpeciesNameComboBox || e.getSource() == potentialAttackIVComboBox || 
				e.getSource() == potentialDefenseIVComboBox || e.getSource() == potentialStaminaIVComboBox) {
				if (e.getStateChange() == ItemEvent.SELECTED) {		// itemStateChanged is flagged twice when selecting a new element in JComboBox (one for select and one for deselect).
					if (speciesFilteredList.size() == 0) {
						// Do nothing.
					}
					else {
						updatePotentialTable();
					}
				}
			}
			
			else if (e.getSource() == rankingsSortComboBox) {
				if (e.getStateChange() == ItemEvent.SELECTED) {		// itemStateChanged is flagged twice when selecting a new element in JComboBox (one for select and one for deselect).
					if (speciesFilteredList.size() == 0) {
						// Do nothing.
					}
					else {
						updateRankingsTable();
					}
				}
			}
		}
		else if (e.getSource().getClass().getSimpleName().equals("JCheckBox")) {
			// Source of ItemEvent is a JCheckBox
			updateFilter();
			
			updateDiscoverSpeciesNameComboBox();
			
			updateDiscoverTable();
			
			updatePotentialSpeciesNameComboBox();
			
			updateRankingsTable();
		}
		
	}
	
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == discoverCalculateButton) {
			boolean validValues = true;
			try {
				int cpValue = Integer.parseInt(discoverCPTextField.getText());
				int hpValue = Integer.parseInt(discoverHPTextField.getText());
			}
			catch (Exception ex) {
				validValues = false;
				discoverCPTextField.setText("10");
				discoverHPTextField.setText("10");
			}
			if (validValues) {
				updateDiscoverTable();
			}
		}
	}
	
	public void focusGained(FocusEvent e) {
		JTextField textField = (JTextField) e.getSource();
		textField.selectAll();
	}

	public void focusLost(FocusEvent e) {
		// Do nothing on focus lost
	}
	
	//-----------------------------------------------------------------//
	
	/** Create and manage GUI components **/
	
	private void createAndShowGUI() {
		JFrame frame = new JFrame(frameTitle);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent wE) {
				end();
			}
		});
		frame.setResizable(false);
		frame.getContentPane().add(buildMainPanel());
		frame.pack();
		frame.validate();
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameDim = frame.getSize();
		int heightWithoutTaskbar = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
		frame.setLocation((screenDim.width - frameDim.width) / 2, (heightWithoutTaskbar - frameDim.height) / 2);		//Centers application in user's display
		frame.setVisible(true);
	}
	
	private JPanel buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Filter panel
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 10, 10, 10);
		panel.add(buildFilterPanel(), constraints);
		
		// Tabbed pane
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 0, 10, 10);
		panel.add(buildTabbedPane(), constraints);
		
		return panel;
	}
	
	private JPanel buildFilterPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Generation I CheckBox
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 10, 0, 10);
		panel.add(genICheckBox, constraints);
		
		// Generation II CheckBox
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 0, 10);
		panel.add(genIICheckBox, constraints);
		
		// Generation III CheckBox
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 0, 10);
		panel.add(genIIICheckBox, constraints);
		
		// Generation IV CheckBox
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 0, 10);
		panel.add(genIVCheckBox, constraints);
		
		// Generation V CheckBox
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 0, 10);
		panel.add(genVCheckBox, constraints);
		
		// Generation VI CheckBox
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 0, 10);
		panel.add(genVICheckBox, constraints);
		
		// Generation VII CheckBox
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 0, 10);
		panel.add(genVIICheckBox, constraints);
		
		// Legendaries CheckBox
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 0, 10);
		panel.add(legendariesCheckBox, constraints);
		
		// Mega Evolutions CheckBox
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 10, 10);
		panel.add(megaEvolutionsCheckBox, constraints);
				
		return panel;
	}
	
	private JTabbedPane buildTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		
		// Discover panel
		tabbedPane.addTab(discoverTitle, buildDiscoverPanel());
		
		// Potential panel
		tabbedPane.addTab(potentialTitle, buildPotentialPanel());
		
		// Rankings panel
		tabbedPane.addTab(rankingsTitle, buildRankingsPanel());
		
		return tabbedPane;
	}
	
	private JPanel buildDiscoverPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Input panel 1
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 10, 2, 10);
		panel.add(buildDiscoverInputPanel1(), constraints);
		
		// Input panel 2
		currentGridX = 0;
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 2, 10);
		panel.add(buildDiscoverInputPanel2(), constraints);
		
		// Discover table panel
		currentGridX = 0;
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 10, 10);
		panel.add(buildDiscoverTablePanel(), constraints);
		
		return panel;
	}
	
	private JPanel buildDiscoverInputPanel1() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Species name label
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(discoverSpeciesNameLabel, constraints);
		
		// Species name combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(discoverSpeciesNameComboBox, constraints);
		
		// CP label
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(discoverCPLabel, constraints);
		
		// CP textfield
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(discoverCPTextField, constraints);
		
		// HP label
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(discoverHPLabel, constraints);
		
		// HP textfield
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(discoverHPTextField, constraints);
		
		// Power Up label
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(discoverPowerUpLabel, constraints);
		
		// Power Up combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(discoverPowerUpComboBox, constraints);
		
		return panel;
	}
	
	private JPanel buildDiscoverInputPanel2() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Overall Analysis label
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(discoverOverallAnalysisLabel, constraints);
		
		// Overall Analysis combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(discoverOverallAnalysisComboBox, constraints);
		
		// Highest IV label
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(discoverHighestIVLabel, constraints);
		
		// Highest IV combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(discoverHighestIVComboBox, constraints);
		
		// Highest Analysis label
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(discoverHighestAnalysisLabel, constraints);
		
		// Highest Analysis combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(discoverHighestAnalysisComboBox, constraints);
		
		return panel;
	}
	
	private JPanel buildDiscoverTablePanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Discover table scrollpane
		JScrollPane scrollPane = new JScrollPane(discoverTable);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(scrollPane, constraints);
		
		// Calculate button
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 0, 0, 0);
		panel.add(discoverCalculateButton, constraints);
		
		return panel;
	}
	
	private JPanel buildPotentialPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Input panel 1
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 10, 2, 10);
		panel.add(buildPotentialInputPanel1(), constraints);
		
		// Input panel 2
		currentGridX = 0;
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 2, 10);
		panel.add(buildPotentialInputPanel2(), constraints);
		
		// Potential table panel
		currentGridX = 0;
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 8;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 10, 10);
		panel.add(buildPotentialTablePanel(), constraints);
		
		return panel;
	}
	
	private JPanel buildPotentialInputPanel1() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Species name label
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(potentialSpeciesNameLabel, constraints);
		
		// Species name combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(potentialSpeciesNameComboBox, constraints);
		
		return panel;
	}
	
	private JPanel buildPotentialInputPanel2() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Attack IV label
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(potentialAttackIVLabel, constraints);
		
		// Attack IV combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(potentialAttackIVComboBox, constraints);
		
		// Defense IV label
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(potentialDefenseIVLabel, constraints);
		
		// Defense IV combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(potentialDefenseIVComboBox, constraints);
		
		// Stamina IV label
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(potentialStaminaIVLabel, constraints);
		
		// Stamina IV combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(potentialStaminaIVComboBox, constraints);
		
		return panel;
	}
	
	private JPanel buildPotentialTablePanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Potential table scrollpane
		JScrollPane scrollPane = new JScrollPane(potentialTable);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(scrollPane, constraints);
		
		return panel;
	}
	
	private JPanel buildRankingsPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Input panel 1
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 10, 29, 10);
		panel.add(buildRankingsInputPanel(), constraints);
		
		// Rankings table panel
		currentGridX = 0;
		currentGridY++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 10, 10);
		panel.add(buildRankingsTablePanel(), constraints);
		
		return panel;
	}
	
	private JPanel buildRankingsInputPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		int currentGridX = 0;
		int currentGridY = 0;
		
		// Sort label
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 2);
		panel.add(rankingsSortLabel, constraints);
		
		// Sort combobox
		currentGridX++;
		constraints.gridx = currentGridX;
		constraints.gridy = currentGridY;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(rankingsSortComboBox, constraints);
		
		return panel;
	}
	
	private JPanel buildRankingsTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		
		// Rankings table scrollpane
		JScrollPane scrollPane = new JScrollPane(rankingsTable);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		return panel;
	}
	
	private void updateFilter() {
		speciesFilteredList = new ArrayList<PKGOStats_Species>();
		boolean[] filter = {genICheckBox.isSelected(), genIICheckBox.isSelected(), genIIICheckBox.isSelected(), genIVCheckBox.isSelected(), 
							genVCheckBox.isSelected(), genVICheckBox.isSelected(), genVIICheckBox.isSelected(), legendariesCheckBox.isSelected(), 
							megaEvolutionsCheckBox.isSelected()};
		
		PKGOStats_Species tempSpecies;
		for (int i = 0; i < speciesMasterList.size(); i++) {
			tempSpecies = speciesMasterList.get(i);
			if (filter[tempSpecies.getGeneration() - 1]) {		// Only show Pokemon from the selected generations.
				if (!filter[7] && tempSpecies.isLegendary()) {
					continue;
				}
				else if (!filter[8] && tempSpecies.isMegaEvolution()) {
					continue;
				}
				else {
					speciesFilteredList.add(tempSpecies);
				}
			}
		}
	}
	
	private void updateDiscoverSpeciesNameComboBox() {
		String[] filteredSpeciesNames = new String[speciesFilteredList.size()];
		for (int i = 0; i < speciesFilteredList.size(); i++) {
			filteredSpeciesNames[i] = String.format("%03d", speciesFilteredList.get(i).getPokedexNumber()) + " " + speciesFilteredList.get(i).getSpeciesName();
		}
		
		if (filteredSpeciesNames.length > 0) {
			discoverSpeciesNameComboBox.setModel(new DefaultComboBoxModel<String>(filteredSpeciesNames));
		}
		else {
			discoverSpeciesNameComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {noFilterString}));
		}
		discoverSpeciesNameComboBox.setSelectedIndex(0);
	}
	
	private void updateDiscoverTable() {
		String[][] discoverTableData;
		if (speciesFilteredList.size() == 0) {
			discoverTableData = new String[1][discoverTableColumns.length];
			discoverTableData[0] = new String[] {"No", "filter", "selected"};
		}
		else {
			discoverTableData = getPossibleStats();
		}
		
		if (discoverTableData.length == 0) {
			discoverTableData = new String[1][discoverTableColumns.length];
			discoverTableData[0] = new String[] {"No", "possible", "IV", "combos"};
		}
		
		@SuppressWarnings("serial")
		DefaultTableModel discoverTableModel = new DefaultTableModel(discoverTableData, discoverTableColumns) {
			// Must use this custom overidden DefaultTableModel in order to render table cells un-editable by the user.
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		discoverTableModel.setColumnIdentifiers(discoverTableColumns);
		discoverTable.setModel(discoverTableModel);
		
		TableColumnModel discoverTableColumnModel = discoverTable.getColumnModel();
		discoverTableColumnModel.getColumn(0).setPreferredWidth(40);
		discoverTableColumnModel.getColumn(1).setPreferredWidth(30);
		discoverTableColumnModel.getColumn(2).setPreferredWidth(30);
		discoverTableColumnModel.getColumn(3).setPreferredWidth(50);
		discoverTableColumnModel.getColumn(4).setPreferredWidth(50);
		discoverTableColumnModel.getColumn(5).setPreferredWidth(50);
		discoverTableColumnModel.getColumn(6).setPreferredWidth(50);
		
		DefaultTableCellRenderer discoverTableCellRenderer = new DefaultTableCellRenderer();
		discoverTableCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		discoverTableColumnModel.getColumn(0).setCellRenderer(discoverTableCellRenderer);
		discoverTableColumnModel.getColumn(1).setCellRenderer(discoverTableCellRenderer);
		discoverTableColumnModel.getColumn(2).setCellRenderer(discoverTableCellRenderer);
		discoverTableColumnModel.getColumn(3).setCellRenderer(discoverTableCellRenderer);
		discoverTableColumnModel.getColumn(4).setCellRenderer(discoverTableCellRenderer);
		discoverTableColumnModel.getColumn(5).setCellRenderer(discoverTableCellRenderer);
		discoverTableColumnModel.getColumn(6).setCellRenderer(discoverTableCellRenderer);
	}
	
	private void updatePotentialSpeciesNameComboBox() {
		String[] filteredSpeciesNames = new String[speciesFilteredList.size()];
		for (int i = 0; i < speciesFilteredList.size(); i++) {
			filteredSpeciesNames[i] = String.format("%03d", speciesFilteredList.get(i).getPokedexNumber()) + " " + speciesFilteredList.get(i).getSpeciesName();
		}
		
		if (filteredSpeciesNames.length > 0) {
			potentialSpeciesNameComboBox.setModel(new DefaultComboBoxModel<String>(filteredSpeciesNames));
			updatePotentialTable();
		}
		else {
			potentialSpeciesNameComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {noFilterString}));
			updatePotentialTable();
		}
		potentialSpeciesNameComboBox.setSelectedIndex(0);
	}
	
	private void updatePotentialTable() {
		String[][] potentialTableData;
		if (speciesFilteredList.size() == 0) {
			potentialTableData = new String[1][potentialTableColumns.length];
			potentialTableData[0] = new String[] {"No", "filter", "selected"};
		}
		else {
			int numberOfRows = (potentialMaxLevel * 2) - 1;
			potentialTableData = new String[numberOfRows][potentialTableColumns.length];
			
			ArrayList<String[]> ascendingData = new ArrayList<String[]>();
			PKGOStats_Species monToDisplay = speciesFilteredList.get(potentialSpeciesNameComboBox.getSelectedIndex());
			int attackIV = (int) potentialAttackIVComboBox.getSelectedItem();
			int defenseIV = (int) potentialDefenseIVComboBox.getSelectedItem();
			int staminaIV = (int) potentialStaminaIVComboBox.getSelectedItem();
			
			double cpMultiplier;
			
			double level;
			int cp;
			int hp;
			double attackActual;
			double defenseActual;
			double staminaActual;
			
			for (int i = 0; i < numberOfRows; i++) {
				cpMultiplier = cpMultipliersList.get(i);
				
				level = ((double)i + 2) / 2;
				cp = monToDisplay.calculateCpPkgo(cpMultiplier, attackIV, defenseIV, staminaIV);
				attackActual = monToDisplay.calculateAttackPkgo_Actual(cpMultiplier, attackIV);
				defenseActual = monToDisplay.calculateDefensePkgo_Actual(cpMultiplier, defenseIV);
				staminaActual = monToDisplay.calculateStaminaPkgo_Actual(cpMultiplier, staminaIV);
				hp = monToDisplay.calculateHpPkgo(staminaActual);
				
				ascendingData.add(new String[] {level + "", cp + "", hp + "", String.format("%.3f", attackActual), String.format("%.3f", defenseActual), String.format("%.3f", staminaActual)});
			}
			
			for (int i = 0; i < ascendingData.size(); i++) {
				potentialTableData[i] = ascendingData.get((ascendingData.size() - i) - 1);
			}
		}
		
		@SuppressWarnings("serial")
		DefaultTableModel potentialTableModel = new DefaultTableModel(potentialTableData, potentialTableColumns) {
			// Must use this custom overidden DefaultTableModel in order to render table cells un-editable by the user.
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		potentialTableModel.setColumnIdentifiers(potentialTableColumns);
		potentialTable.setModel(potentialTableModel);
		
		TableColumnModel potentialTableColumnModel = potentialTable.getColumnModel();
		potentialTableColumnModel.getColumn(0).setPreferredWidth(50);
		potentialTableColumnModel.getColumn(1).setPreferredWidth(75);
		potentialTableColumnModel.getColumn(2).setPreferredWidth(50);
		potentialTableColumnModel.getColumn(3).setPreferredWidth(125);
		potentialTableColumnModel.getColumn(4).setPreferredWidth(125);
		potentialTableColumnModel.getColumn(5).setPreferredWidth(125);
		
		DefaultTableCellRenderer potentialTableCellRenderer = new DefaultTableCellRenderer();
		potentialTableCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		potentialTableColumnModel.getColumn(0).setCellRenderer(potentialTableCellRenderer);
		potentialTableColumnModel.getColumn(1).setCellRenderer(potentialTableCellRenderer);
		potentialTableColumnModel.getColumn(2).setCellRenderer(potentialTableCellRenderer);
		potentialTableColumnModel.getColumn(3).setCellRenderer(potentialTableCellRenderer);
		potentialTableColumnModel.getColumn(4).setCellRenderer(potentialTableCellRenderer);
		potentialTableColumnModel.getColumn(5).setCellRenderer(potentialTableCellRenderer);
	}
	
	private void updateRankingsTable() {
		String[][] rankingsTableData;
		if (speciesFilteredList.size() == 0) {
			rankingsTableData = new String[1][rankingsTableColumns.length];
			rankingsTableData[0] = new String[] {"No", "filter", "selected"};
		}
		else {
			int numberOfRows = speciesFilteredList.size();
			rankingsTableData = new String[numberOfRows][rankingsTableColumns.length];
			
			ArrayList<ArrayList<String>> unsortedData = new ArrayList<ArrayList<String>>();
			ArrayList<String> speciesData;
			PKGOStats_Species mon;
			int pokedexNumber;
			String speciesName;
			int maxPotentialCP;
			int baseAttack;
			int baseDefense;
			int baseStamina;
			for (int i = 0; i < speciesFilteredList.size(); i++) {
				speciesData = new ArrayList<String>();
				mon = speciesFilteredList.get(i);
				pokedexNumber = mon.getPokedexNumber();
				speciesName = mon.getSpeciesName();
				baseAttack = mon.getAttackPkgo_Base();
				baseDefense = mon.getDefensePkgo_Base();
				baseStamina = mon.getStaminaPkgo_Base();
				maxPotentialCP = mon.calculateCpPkgo(cpMultipliersList.get(cpMultipliersList.size() - 1), 15, 15, 15);
				speciesData.add(String.format("%03d", pokedexNumber));
				speciesData.add(speciesName);
				speciesData.add(maxPotentialCP + "");
				speciesData.add(baseAttack + "");
				speciesData.add(baseDefense + "");
				speciesData.add(baseStamina + "");
				unsortedData.add(speciesData);
			}
		
			rankingsTableData = sortRankingsData(unsortedData, rankingsSortComboBox.getSelectedIndex());
		}
		
		@SuppressWarnings("serial")
		DefaultTableModel rankingsTableModel = new DefaultTableModel(rankingsTableData, rankingsTableColumns) {
			// Must use this custom overidden DefaultTableModel in order to render table cells un-editable by the user.
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		rankingsTableModel.setColumnIdentifiers(rankingsTableColumns);
		rankingsTable.setModel(rankingsTableModel);
		
		TableColumnModel rankingsTableColumnModel = rankingsTable.getColumnModel();
		rankingsTableColumnModel.getColumn(0).setPreferredWidth(50);
		rankingsTableColumnModel.getColumn(1).setPreferredWidth(70);
		rankingsTableColumnModel.getColumn(2).setPreferredWidth(200);
		rankingsTableColumnModel.getColumn(3).setPreferredWidth(100);
		rankingsTableColumnModel.getColumn(4).setPreferredWidth(85);
		rankingsTableColumnModel.getColumn(5).setPreferredWidth(85);
		rankingsTableColumnModel.getColumn(6).setPreferredWidth(85);
		
		DefaultTableCellRenderer rankingsTableCellRenderer = new DefaultTableCellRenderer();
		rankingsTableCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		rankingsTableColumnModel.getColumn(0).setCellRenderer(rankingsTableCellRenderer);
		rankingsTableColumnModel.getColumn(1).setCellRenderer(rankingsTableCellRenderer);
		rankingsTableColumnModel.getColumn(2).setCellRenderer(rankingsTableCellRenderer);
		rankingsTableColumnModel.getColumn(3).setCellRenderer(rankingsTableCellRenderer);
		rankingsTableColumnModel.getColumn(4).setCellRenderer(rankingsTableCellRenderer);
		rankingsTableColumnModel.getColumn(5).setCellRenderer(rankingsTableCellRenderer);
		rankingsTableColumnModel.getColumn(6).setCellRenderer(rankingsTableCellRenderer);
	}
	
	private String[][] sortRankingsData(ArrayList<ArrayList<String>> inc_data, int inc_sortOption) {
		String[][] sortedData = new String[inc_data.size()][rankingsTableColumns.length];
		if (inc_sortOption == 0) {
			// Sort by PokeDex Number.
			Collections.sort(inc_data, new Comparator<ArrayList<String>> () {
			    @Override
			    public int compare(ArrayList<String> a, ArrayList<String> b) {
			    	Integer first = Integer.valueOf(a.get(0));
			    	Integer second = Integer.valueOf(b.get(0));
			        return first.compareTo(second);
			    }
			});
		}
		else if (inc_sortOption == 1) {
			// Sort by Max Potential CP.
			Collections.sort(inc_data, new Comparator<ArrayList<String>> () {
			    @Override
			    public int compare(ArrayList<String> a, ArrayList<String> b) {
			    	Integer first = Integer.valueOf(a.get(2));
			    	Integer second = Integer.valueOf(b.get(2));
			        return second.compareTo(first);
			    }
			});
		}
		else if (inc_sortOption == 2) {
			// Sort by Base Attack.
			Collections.sort(inc_data, new Comparator<ArrayList<String>> () {
			    @Override
			    public int compare(ArrayList<String> a, ArrayList<String> b) {
			    	Integer first = Integer.valueOf(a.get(3));
			    	Integer second = Integer.valueOf(b.get(3));
			        return second.compareTo(first);
			    }
			});
		}
		else if (inc_sortOption == 3) {
			// Sort by Base Defense.
			Collections.sort(inc_data, new Comparator<ArrayList<String>> () {
			    @Override
			    public int compare(ArrayList<String> a, ArrayList<String> b) {
			    	Integer first = Integer.valueOf(a.get(4));
			    	Integer second = Integer.valueOf(b.get(4));
			        return second.compareTo(first);
			    }
			});
		}
		else if (inc_sortOption == 4) {
			// Sort by Base Stamina.
			Collections.sort(inc_data, new Comparator<ArrayList<String>> () {
			    @Override
			    public int compare(ArrayList<String> a, ArrayList<String> b) {
			    	Integer first = Integer.valueOf(a.get(5));
			    	Integer second = Integer.valueOf(b.get(5));
			        return second.compareTo(first);
			    }
			});
		}
		else if (inc_sortOption == 5) {
			// Sort by Sum of Stats.
			Collections.sort(inc_data, new Comparator<ArrayList<String>> () {
			    @Override
			    public int compare(ArrayList<String> a, ArrayList<String> b) {
			    	Integer first = Integer.valueOf(Integer.parseInt(a.get(3)) + Integer.parseInt(a.get(4)) + Integer.parseInt(a.get(5)));
			    	Integer second = Integer.valueOf(Integer.parseInt(b.get(3)) + Integer.parseInt(b.get(4)) + Integer.parseInt(b.get(5)));
			        return second.compareTo(first);
			    }
			});
		}
		
		for (int i = 0; i < sortedData.length; i++) {
			sortedData[i] = new String[] {(i + 1) + "", inc_data.get(i).get(0), inc_data.get(i).get(1), inc_data.get(i).get(2),inc_data.get(i).get(3), 
										  inc_data.get(i).get(4), inc_data.get(i).get(5)};
		}
		
		return sortedData;
	}
	
	//-----------------------------------------------------------------//
	
	/** Protected methods **/
	
	
	
	//-----------------------------------------------------------------//
	
	/** Private methods **/
	
	private void start() {
		initVars();
		createAndShowGUI();
	}
	
	private void end() {
		System.exit(0);
	}
	
	private void generateSpeciesMasterList() {
		speciesMasterList = new ArrayList<PKGOStats_Species>();
		
		ArrayList<String[]> lines = new ArrayList<String[]>();
		BufferedReader inStream = null;
		try {
			inStream = new BufferedReader(new FileReader(speciesStatsFile));
			String[] tokens;
			String nextLine = inStream.readLine();
			while (nextLine != null) {
				String parsedLine = nextLine;
				parsedLine = parsedLine.trim();
				tokens = parsedLine.split("\t");
				lines.add(tokens);
				nextLine = inStream.readLine();
			}
		}
		catch (Exception e) {
			// Do nothing
		}
		finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					// Do nothing
				}
			}
		}
		
		String[] data;
		PKGOStats_Species tempSpecies;
		for (int i = 0; i < lines.size(); i++) {
			data = lines.get(i);
			tempSpecies = new PKGOStats_Species(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], data[10]);
			speciesMasterList.add(tempSpecies);
		}
	}
	
	private void generateCPMultipliersList() {
		cpMultipliersList = new ArrayList<Double>();
		
		ArrayList<String[]> lines = new ArrayList<String[]>();
		BufferedReader inStream = null;
		try {
			inStream = new BufferedReader(new FileReader(cpMultipliersFile));
			String[] tokens;
			String nextLine = inStream.readLine();
			while (nextLine != null) {
				String parsedLine = nextLine;
				parsedLine = parsedLine.trim();
				tokens = parsedLine.split("\t");
				lines.add(tokens);
				nextLine = inStream.readLine();
			}
		}
		catch (Exception e) {
			// Do nothing
		}
		finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					// Do nothing
				}
			}
		}
		
		for (int i = 0; i < lines.size(); i++) {
			cpMultipliersList.add(Double.parseDouble(lines.get(i)[1]));
		}
	}
	
	private double lookUpCPMultiplier(double inc_level) {
		return cpMultipliersList.get((int) (inc_level * 2) - 2);
	}
	
	private String[][] getPossibleStats() {
		ArrayList<ArrayList<String>> statsList;
		
		PKGOStats_Species monToDisplay = speciesFilteredList.get(discoverSpeciesNameComboBox.getSelectedIndex());
		int cp = Integer.parseInt(discoverCPTextField.getText());
		int hp = Integer.parseInt(discoverHPTextField.getText());
		double[] levelRange = getLevelRange(discoverPowerUpComboBox.getSelectedIndex());
		int[] ivsSumRange = getIVsSumRange(discoverOverallAnalysisComboBox.getSelectedIndex());
		int highestIV = discoverHighestIVComboBox.getSelectedIndex();
		int[] highestIVRange = getHighestIVRange(discoverHighestAnalysisComboBox.getSelectedIndex());
		
		ArrayList<ArrayList<Integer>> possibleIVCombos = new ArrayList<ArrayList<Integer>>();
		if (highestIV == 0) {
			possibleIVCombos = calculateIVCombos_Attack(ivsSumRange, highestIVRange);
		}
		else if (highestIV == 1) {
			possibleIVCombos = calculateIVCombos_Defense(ivsSumRange, highestIVRange);
		}
		else if (highestIV == 2) {
			possibleIVCombos = calculateIVCombos_Stamina(ivsSumRange, highestIVRange);
		}
		else if (highestIV == 3) {
			possibleIVCombos = calculateIVCombos_AttackDefense(ivsSumRange, highestIVRange);
		}
		else if (highestIV == 4) {
			possibleIVCombos = calculateIVCombos_AttackStamina(ivsSumRange, highestIVRange);
		}
		else if (highestIV == 5) {
			possibleIVCombos = calculateIVCombos_DefenseStamina(ivsSumRange, highestIVRange);
		}
		else {		// highestIV == 6
			possibleIVCombos = calculateIVCombos_AttackDefenseStamina(ivsSumRange, highestIVRange);
		}
		
		statsList = calculateStats(monToDisplay, cp, hp, levelRange, possibleIVCombos);
		
		Collections.sort(statsList, new Comparator<ArrayList<String>>() {
			    @Override
			    public int compare(ArrayList<String> a, ArrayList<String> b) {
			    	Integer first = Integer.valueOf(a.get(6));
			    	Integer second = Integer.valueOf(b.get(6));
			        return second.compareTo(first);
			    }
		});
		
		String[][] statsData = new String[statsList.size()][discoverTableColumns.length];
		String[] dataRow;
		for (int i = 0; i < statsList.size(); i++) {
			dataRow = new String[discoverTableColumns.length];
			for (int j = 0; j < statsList.get(i).size(); j++) {
				dataRow[j] = statsList.get(i).get(j);
			}
			statsData[i] = dataRow;
		}
		
		return statsData;
	}
	
	private double[] getLevelRange(int inc_index) {
		ArrayList<Double> levels = new ArrayList<Double>();
		if (inc_index == 0) {		// 10000/15
			levels.add(39.0);
			levels.add(39.5);
			levels.add(40.0);
		}
		else if (inc_index == 1) {		// 9000/12
			levels.add(37.0);
			levels.add(37.5);
			levels.add(38.0);
			levels.add(38.5);
		}
		else if (inc_index == 2) {		// 8000/10
			levels.add(35.0);
			levels.add(35.5);
			levels.add(36.0);
			levels.add(36.5);
		}
		else if (inc_index == 3) {		// 7000/8
			levels.add(33.0);
			levels.add(33.5);
			levels.add(34.0);
			levels.add(34.5);
		}
		else if (inc_index == 4) {		// 6000/6
			levels.add(31.0);
			levels.add(31.5);
			levels.add(32.0);
			levels.add(32.5);
		}
		else if (inc_index == 5) {		// 5000/4
			levels.add(29.0);
			levels.add(29.5);
			levels.add(30.0);
			levels.add(30.5);
		}
		else if (inc_index == 6) {		// 4500/4
			levels.add(27.0);
			levels.add(27.5);
			levels.add(28.0);
			levels.add(28.5);
		}
		else if (inc_index == 7) {		// 4000/4
			levels.add(26.0);
			levels.add(26.5);
		}
		else if (inc_index == 8) {		// 4000/3
			levels.add(25.0);
			levels.add(25.5);
		}
		else if (inc_index == 9) {		// 3500/3
			levels.add(23.0);
			levels.add(23.5);
			levels.add(24.0);
			levels.add(24.5);
		}
		else if (inc_index == 10) {		// 3000/3
			levels.add(21.0);
			levels.add(21.5);
			levels.add(22.0);
			levels.add(22.5);
		}
		else if (inc_index == 11) {		// 2500/2
			levels.add(19.0);
			levels.add(19.5);
			levels.add(20.0);
			levels.add(20.5);
		}
		else if (inc_index == 12) {		// 2200/2
			levels.add(17.0);
			levels.add(17.5);
			levels.add(18.0);
			levels.add(18.5);
		}
		else if (inc_index == 13) {		// 1900/2
			levels.add(15.0);
			levels.add(15.5);
			levels.add(16.0);
			levels.add(16.5);
		}
		else if (inc_index == 14) {		// 1600/2
			levels.add(13.0);
			levels.add(13.5);
			levels.add(14.0);
			levels.add(14.5);
		}
		else if (inc_index == 15) {		// 1300/2
			levels.add(11.0);
			levels.add(11.5);
			levels.add(12.0);
			levels.add(12.5);
		}
		else if (inc_index == 16) {		// 1000/1
			levels.add(9.0);
			levels.add(9.5);
			levels.add(10.0);
			levels.add(10.5);
		}
		else if (inc_index == 17) {		// 800/1
			levels.add(7.0);
			levels.add(7.5);
			levels.add(8.0);
			levels.add(8.5);
		}
		else if (inc_index == 18) {		// 600/1
			levels.add(5.0);
			levels.add(5.5);
			levels.add(6.0);
			levels.add(6.5);
		}
		else if (inc_index == 19) {		// 400/1
			levels.add(3.0);
			levels.add(3.5);
			levels.add(4.0);
			levels.add(4.5);
		}
		else if (inc_index == 20) {		// 200/1
			levels.add(1.0);
			levels.add(1.5);
			levels.add(2.0);
			levels.add(2.5);
		}
		
		double[] levelRange = new double[levels.size()];
		for (int i = 0; i < levels.size(); i++) {
			levelRange[i] = levels.get(i);
		}
		
		return levelRange;
	}
	
	private int[] getIVsSumRange(int inc_index) {
		ArrayList<Integer> sums = new ArrayList<Integer>();
		if (inc_index == 0) {
			sums.add(45);
			sums.add(44);
			sums.add(43);
			sums.add(42);
			sums.add(41);
			sums.add(40);
			sums.add(39);
			sums.add(38);
			sums.add(37);
		}
		else if (inc_index == 1) {
			sums.add(36);
			sums.add(35);
			sums.add(34);
			sums.add(33);
			sums.add(32);
			sums.add(31);
			sums.add(30);
		}
		else if (inc_index == 2) {
			sums.add(29);
			sums.add(28);
			sums.add(27);
			sums.add(26);
			sums.add(25);
			sums.add(24);
			sums.add(23);
		}
		else if (inc_index == 3) {
			sums.add(22);
			sums.add(21);
			sums.add(20);
			sums.add(19);
			sums.add(18);
			sums.add(17);
			sums.add(16);
			sums.add(15);
			sums.add(14);
			sums.add(13);
			sums.add(12);
			sums.add(11);
			sums.add(10);
			sums.add(9);
			sums.add(8);
			sums.add(7);
			sums.add(6);
			sums.add(5);
			sums.add(4);
			sums.add(3);
			sums.add(2);
			sums.add(1);
			sums.add(0);
		}
		
		int[] statsSumRange = new int[sums.size()];
		for (int i = 0; i < sums.size(); i++) {
			statsSumRange[i] = sums.get(i);
		}
		
		return statsSumRange;
	}
	
	private int[] getHighestIVRange(int inc_index) {
		ArrayList<Integer> ivs = new ArrayList<Integer>();
		if (inc_index == 0) {
			ivs.add(15);
		}
		else if (inc_index == 1) {
			ivs.add(14);
			ivs.add(13);
		}
		else if (inc_index == 2) {
			ivs.add(12);
			ivs.add(11);
			ivs.add(10);
			ivs.add(9);
			ivs.add(8);
		}
		else if (inc_index == 3) {
			ivs.add(7);
			ivs.add(6);
			ivs.add(5);
			ivs.add(4);
			ivs.add(3);
			ivs.add(2);
			ivs.add(1);
			ivs.add(0);
		}
		
		int[] highestIVRange = new int[ivs.size()];
		for (int i = 0; i < ivs.size(); i++) {
			highestIVRange[i] = ivs.get(i);
		}
		
		return highestIVRange;
	}
	
	private ArrayList<ArrayList<Integer>> calculateIVCombos_Attack(int[] inc_ivsSumRange, int[] inc_highestIVRange) {
		ArrayList<ArrayList<Integer>> possibleIVCombos = new ArrayList<ArrayList<Integer>>();
		
		int[] fullIVsRange = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
		ArrayList<Integer> ivCombo;
		int attack;
		int defense;
		int stamina;
		int sum;
		for (int a = 0; a < inc_highestIVRange.length; a++) {
			// Attack IVs
			attack = inc_highestIVRange[a];
			for (int d = 0; d < fullIVsRange.length; d++) {
				// Defense IVs
				defense = fullIVsRange[d];
				if (defense >= attack) {
					continue;
				}
				for (int s = 0; s < fullIVsRange.length; s++) {
					// Stamina IVs
					stamina = fullIVsRange[s];
					if (stamina >= attack) {
						continue;
					}
					for (int i = 0; i < inc_ivsSumRange.length; i++) {
						// IV Sum
						sum = inc_ivsSumRange[i];
						if (attack + defense + stamina == sum) {
							ivCombo = new ArrayList<Integer>();
							ivCombo.add(attack);
							ivCombo.add(defense);
							ivCombo.add(stamina);
							possibleIVCombos.add(ivCombo);
						}
					}
				}
			}
		}
		
		return possibleIVCombos;
	}
	
	private ArrayList<ArrayList<Integer>> calculateIVCombos_Defense(int[] inc_ivsSumRange, int[] inc_highestIVRange) {
		ArrayList<ArrayList<Integer>> possibleIVCombos = new ArrayList<ArrayList<Integer>>();
		
		int[] fullIVsRange = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
		ArrayList<Integer> ivCombo;
		int attack;
		int defense;
		int stamina;
		int sum;
		for (int d = 0; d < inc_highestIVRange.length; d++) {
			// Defense IVs
			defense = inc_highestIVRange[d];
			for (int a = 0; a < fullIVsRange.length; a++) {
				// Attack IVs
				attack = fullIVsRange[a];
				if (attack >= defense) {
					continue;
				}
				for (int s = 0; s < fullIVsRange.length; s++) {
					// Stamina IVs
					stamina = fullIVsRange[s];
					if (stamina >= defense) {
						continue;
					}
					for (int i = 0; i < inc_ivsSumRange.length; i++) {
						// IV Sum
						sum = inc_ivsSumRange[i];
						if (attack + defense + stamina == sum) {
							ivCombo = new ArrayList<Integer>();
							ivCombo.add(attack);
							ivCombo.add(defense);
							ivCombo.add(stamina);
							possibleIVCombos.add(ivCombo);
						}
					}
				}
			}
		}
		
		return possibleIVCombos;
	}
	
	private ArrayList<ArrayList<Integer>> calculateIVCombos_Stamina(int[] inc_ivsSumRange, int[] inc_highestIVRange) {
		ArrayList<ArrayList<Integer>> possibleIVCombos = new ArrayList<ArrayList<Integer>>();
		
		int[] fullIVsRange = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
		ArrayList<Integer> ivCombo;
		int attack;
		int defense;
		int stamina;
		int sum;
		for (int s = 0; s < inc_highestIVRange.length; s++) {
			// Stamina IVs
			stamina = inc_highestIVRange[s];
			for (int a = 0; a < fullIVsRange.length; a++) {
				// Attack IVs
				attack = fullIVsRange[a];
				if (attack >= stamina) {
					continue;
				}
				for (int d = 0; d < fullIVsRange.length; d++) {
					// Defense IVs
					defense = fullIVsRange[d];
					if (defense >= stamina) {
						continue;
					}
					for (int i = 0; i < inc_ivsSumRange.length; i++) {
						// IV Sum
						sum = inc_ivsSumRange[i];
						if (attack + defense + stamina == sum) {
							ivCombo = new ArrayList<Integer>();
							ivCombo.add(attack);
							ivCombo.add(defense);
							ivCombo.add(stamina);
							possibleIVCombos.add(ivCombo);
						}
					}
				}
			}
		}
		
		return possibleIVCombos;
	}
	
	private ArrayList<ArrayList<Integer>> calculateIVCombos_AttackDefense(int[] inc_ivsSumRange, int[] inc_highestIVRange) {
		ArrayList<ArrayList<Integer>> possibleIVCombos = new ArrayList<ArrayList<Integer>>();
		
		int[] fullIVsRange = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
		ArrayList<Integer> ivCombo;
		int attack;
		int defense;
		int stamina;
		int sum;
		for (int a = 0; a < inc_highestIVRange.length; a++) {
			// Attack and Defense IVs
			attack = inc_highestIVRange[a];
			defense = inc_highestIVRange[a];
			for (int s = 0; s < fullIVsRange.length; s++) {
				// Stamina IVs
				stamina = fullIVsRange[s];
				if (stamina >= attack) {
					continue;
				}
				for (int i = 0; i < inc_ivsSumRange.length; i++) {
					// IV Sum
					sum = inc_ivsSumRange[i];
					if (attack + defense + stamina == sum) {
						ivCombo = new ArrayList<Integer>();
						ivCombo.add(attack);
						ivCombo.add(defense);
						ivCombo.add(stamina);
						possibleIVCombos.add(ivCombo);
					}
				}
			}
		}
		
		return possibleIVCombos;
	}
	
	private ArrayList<ArrayList<Integer>> calculateIVCombos_AttackStamina(int[] inc_ivsSumRange, int[] inc_highestIVRange) {
		ArrayList<ArrayList<Integer>> possibleIVCombos = new ArrayList<ArrayList<Integer>>();
		
		int[] fullIVsRange = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
		ArrayList<Integer> ivCombo;
		int attack;
		int defense;
		int stamina;
		int sum;
		for (int a = 0; a < inc_highestIVRange.length; a++) {
			// Attack and Stamina IVs
			attack = inc_highestIVRange[a];
			stamina = inc_highestIVRange[a];
			for (int d = 0; d < fullIVsRange.length; d++) {
				// Defense IVs
				defense = fullIVsRange[d];
				if (defense >= attack) {
					continue;
				}
				for (int i = 0; i < inc_ivsSumRange.length; i++) {
					// IV Sum
					sum = inc_ivsSumRange[i];
					if (attack + defense + stamina == sum) {
						ivCombo = new ArrayList<Integer>();
						ivCombo.add(attack);
						ivCombo.add(defense);
						ivCombo.add(stamina);
						possibleIVCombos.add(ivCombo);
					}
				}
			}
		}
		
		return possibleIVCombos;
	}
	
	private ArrayList<ArrayList<Integer>> calculateIVCombos_DefenseStamina(int[] inc_ivsSumRange, int[] inc_highestIVRange) {
		ArrayList<ArrayList<Integer>> possibleIVCombos = new ArrayList<ArrayList<Integer>>();
		
		int[] fullIVsRange = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
		ArrayList<Integer> ivCombo;
		int attack;
		int defense;
		int stamina;
		int sum;
		for (int d = 0; d < inc_highestIVRange.length; d++) {
			// Defense and Stamina IVs
			defense = inc_highestIVRange[d];
			stamina = inc_highestIVRange[d];
			for (int a = 0; a < fullIVsRange.length; a++) {
				// Attack IV
				attack = fullIVsRange[a];
				if (attack >= defense) {
					continue;
				}
				for (int i = 0; i < inc_ivsSumRange.length; i++) {
					// IV Sum
					sum = inc_ivsSumRange[i];
					if (attack + defense + stamina == sum) {
						ivCombo = new ArrayList<Integer>();
						ivCombo.add(attack);
						ivCombo.add(defense);
						ivCombo.add(stamina);
						possibleIVCombos.add(ivCombo);
					}
				}
			}
		}
		
		return possibleIVCombos;
	}
	
	private ArrayList<ArrayList<Integer>> calculateIVCombos_AttackDefenseStamina(int[] inc_ivsSumRange, int[] inc_highestIVRange) {
		ArrayList<ArrayList<Integer>> possibleIVCombos = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> ivCombo;
		int attack;
		int defense;
		int stamina;
		int sum;
		for (int a = 0; a < inc_highestIVRange.length; a++) {
			// Attack, Defense, and Stamina IVs
			attack = inc_highestIVRange[a];
			defense = inc_highestIVRange[a];
			stamina = inc_highestIVRange[a];
			for (int i = 0; i < inc_ivsSumRange.length; i++) {
				// IV Sum
				sum = inc_ivsSumRange[i];
				if (attack + defense + stamina == sum) {
					ivCombo = new ArrayList<Integer>();
					ivCombo.add(attack);
					ivCombo.add(defense);
					ivCombo.add(stamina);
					possibleIVCombos.add(ivCombo);
				}
			}
		}
		
		return possibleIVCombos;
	}
	
	private ArrayList<ArrayList<String>> calculateStats(PKGOStats_Species inc_monToDisplay, int inc_cp, int inc_hp, 
														double[] inc_levelRange, ArrayList<ArrayList<Integer>> inc_possibleIVCombos) {
		ArrayList<ArrayList<String>> statsList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> validIVCombo;
		int attackIV;
		int defenseIV;
		int staminaIV;
		double level;
		double cpMultiplier;
		double staminaActual;
		
		for (int i = 0; i < inc_possibleIVCombos.size(); i++) {
			attackIV = inc_possibleIVCombos.get(i).get(0);
			defenseIV = inc_possibleIVCombos.get(i).get(1);
			staminaIV = inc_possibleIVCombos.get(i).get(2);
			for (int j = 0; j < inc_levelRange.length; j++) {
				level = inc_levelRange[j];
				cpMultiplier = lookUpCPMultiplier(level);
				staminaActual = inc_monToDisplay.calculateStaminaPkgo_Actual(cpMultiplier, staminaIV);
				
				// Check if staminaIV, level yield correct HP
				if (inc_monToDisplay.calculateHpPkgo(staminaActual) != inc_hp) {
					continue;
				}
				
				// Check if attackIV, defenseIV, staminaIV, level yield correct CP
				if (inc_monToDisplay.calculateCpPkgo(cpMultiplier, attackIV, defenseIV, staminaIV) != inc_cp) {
					continue;
				}
				
				// Add IV combo to statsList
				validIVCombo = new ArrayList<String>();
				validIVCombo.add(Double.toString(level));
				validIVCombo.add(Integer.toString(inc_cp));
				validIVCombo.add(Integer.toString(inc_hp));
				validIVCombo.add(Integer.toString(attackIV));
				validIVCombo.add(Integer.toString(defenseIV));
				validIVCombo.add(Integer.toString(staminaIV));
				validIVCombo.add(Integer.toString(attackIV + defenseIV + staminaIV));
				statsList.add(validIVCombo);
			}
		}
		
		return statsList;
	}

	
	//-----------------------------------------------------------------//
	
}
