/**
 * This object class holds data about a single Pokemon species.
 */

package nv.pkgostats.app;

public class PKGOStats_Species {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	
	
	//-----------------------------------------------------------------//
	
	/** Declare fields **/
	
	private int pokedexNumber;
	private String speciesName;
	private int hpSeries;
	private int attackSeries;
	private int defenseSeries;
	private int specialAttackSeries;
	private int specialDefenseSeries;
	private int speedSeries;
	private int generation;
	private boolean isLegendary;
	private boolean isMegaEvolution;
	private int attackPkgo_Base;
	private int defensePkgo_Base;
	private int staminaPkgo_Base;
	
	
	//-----------------------------------------------------------------//
	
	/** Constructors **/
	
	protected PKGOStats_Species(String inc_pokedexNumber, String inc_speciesName, String inc_hpSeries, String inc_attackSeries, String inc_defenseSeries, 
								String inc_specialAttackSeries, String inc_specialDefenseSeries, String inc_speedSeries, String inc_generation, String inc_isLegendary, 
								String inc_isMegaEvolution) {
		pokedexNumber = Integer.parseInt(inc_pokedexNumber);
		speciesName = inc_speciesName;
		hpSeries = Integer.parseInt(inc_hpSeries);
		attackSeries = Integer.parseInt(inc_attackSeries);
		defenseSeries = Integer.parseInt(inc_defenseSeries);
		specialAttackSeries = Integer.parseInt(inc_specialAttackSeries);
		specialDefenseSeries = Integer.parseInt(inc_specialDefenseSeries);
		speedSeries = Integer.parseInt(inc_speedSeries);
		generation = Integer.parseInt(inc_generation);
		isLegendary = Boolean.parseBoolean(inc_isLegendary);
		isMegaEvolution = Boolean.parseBoolean(inc_isMegaEvolution);
		attackPkgo_Base = calculateAttackPkgo_Base();
		defensePkgo_Base = calculateDefensePkgo_Base();
		staminaPkgo_Base = calculateStaminaPkgo_Base();
	}
	
	//-----------------------------------------------------------------//
	
	/** Abstract methods **/
	
	
	
	//-----------------------------------------------------------------//
	
	/** Implemented methods **/
	
	
	
	//-----------------------------------------------------------------//
	
	/** Accessor methods **/
	
	protected int getPokedexNumber() {
		return pokedexNumber;
	}
	
	protected String getSpeciesName() {
		return speciesName;
	}
	
	protected int getHpSeries() {
		return hpSeries;
	}
	
	protected int getAttackSeries() {
		return attackSeries;
	}
	
	protected int getDefenseSeries() {
		return defenseSeries;
	}
	
	protected int getSpecialAttackSeries() {
		return specialAttackSeries;
	}
	
	protected int getSpecialDefenseSeries() {
		return specialDefenseSeries;
	}
	
	protected int getSpeedSeries() {
		return speedSeries;
	}
	
	protected int getGeneration() {
		return generation;
	}
	
	protected boolean isLegendary() {
		return isLegendary;
	}
	
	protected boolean isMegaEvolution() {
		return isMegaEvolution;
	}
	
	protected int getAttackPkgo_Base() {
		return attackPkgo_Base;
	}
	
	protected int getDefensePkgo_Base() {
		return defensePkgo_Base;
	}
	
	protected int getStaminaPkgo_Base() {
		return staminaPkgo_Base;
	}
	
	//-----------------------------------------------------------------//
	
	/** Mutator methods **/
	
	
	
	//-----------------------------------------------------------------//
	
	/** Protected methods **/
	
	protected int calculateCpPkgo(double inc_cpMultiplier, int inc_attackIV, int inc_defenseIV, int inc_staminaIV) {
		int result;
		int calculatedCPValue;
		calculatedCPValue = (int) Math.floor(((attackPkgo_Base + inc_attackIV) * Math.sqrt(defensePkgo_Base + inc_defenseIV) * Math.sqrt(staminaPkgo_Base + inc_staminaIV) * Math.pow(inc_cpMultiplier, 2)) / 10.0);
		result = Math.max(calculatedCPValue, 10);
		return result;
	}
	
	protected int calculateHpPkgo(double inc_staminaPOGOActual) {
		int result;
		int calculatedHPValue;
		calculatedHPValue = (int) Math.floor(inc_staminaPOGOActual);
		result = Math.max(calculatedHPValue, 10);
		return result;
	}
	
	protected double calculateAttackPkgo_Actual(double inc_cpMultiplier, int inc_attackIV) {
		double result;
		result = inc_cpMultiplier * (attackPkgo_Base + inc_attackIV);
		return result;
	}
	
	protected double calculateDefensePkgo_Actual(double inc_cpMultiplier, int inc_defenseIV) {
		double result;
		result = inc_cpMultiplier * (defensePkgo_Base + inc_defenseIV);
		return result;
	}
	
	protected double calculateStaminaPkgo_Actual(double inc_cpMultiplier, int inc_staminaIV) {
		double result;
		result = inc_cpMultiplier * (staminaPkgo_Base + inc_staminaIV);
		return result;
	}
	
	//-----------------------------------------------------------------//
	
	/** Private methods **/
	
	private double calculate_speedModifier() {
		return (1 + ((speedSeries - 75) / 500.0));
	}
	
	private int calculateAttackPkgo_Base() {
		int result;
		int attackModifier;
		double speedModifier;
		int max;
		int min;
		
		max = Math.max(attackSeries, specialAttackSeries);
		min = Math.min(attackSeries, specialAttackSeries);
		
		attackModifier = (int) Math.round(2 * ((7.0/8.0) * max + (1.0/8.0) * min));
		speedModifier = calculate_speedModifier();
		
		result = (int) Math.round(attackModifier * speedModifier);
		
		return result;
	}
	
	private int calculateDefensePkgo_Base() {
		int result;
		int defenseModifier;
		double speedModifier;
		int max;
		int min;
		
		max = Math.max(defenseSeries, specialDefenseSeries);
		min = Math.min(defenseSeries, specialDefenseSeries);
		
		defenseModifier = (int) Math.round(2 * ((5.0/8.0) * max + (3.0/8.0) * min));
		speedModifier = calculate_speedModifier();
		
		result = (int) Math.round(defenseModifier * speedModifier);
		
		return result;
	}
	
	private int calculateStaminaPkgo_Base() {
		return (int) Math.floor(hpSeries * 1.75 + 50);
	}
	
	//-----------------------------------------------------------------//
	
}
