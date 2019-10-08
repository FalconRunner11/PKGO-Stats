/**
 * This object class holds data about a single Pokemon species.
 */

package nv.pkgostats;

public class PKGOStats_Species {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	
	
	//-----------------------------------------------------------------//
	
	/** Declare fields **/
	
	private int pokedexNumber;
	private String speciesName;
	private double hpSeries;
	private double attackSeries;
	private double defenseSeries;
	private double specialAttackSeries;
	private double specialDefenseSeries;
	private double speedSeries;
	private int generation;
	private boolean isLegendary;
	private boolean isMegaEvolution;
	private int nerfType;
	private int attackPkgo_Base;
	private int defensePkgo_Base;
	private int staminaPkgo_Base;
	
	
	//-----------------------------------------------------------------//
	
	/** Constructors **/
	
	protected PKGOStats_Species(String inc_pokedexNumber, String inc_speciesName, String inc_hpSeries, String inc_attackSeries, String inc_defenseSeries, 
								String inc_specialAttackSeries, String inc_specialDefenseSeries, String inc_speedSeries, String inc_generation, String inc_isLegendary, 
								String inc_isMegaEvolution, String inc_nerfType) {
		pokedexNumber = Integer.parseInt(inc_pokedexNumber);
		speciesName = inc_speciesName;
		hpSeries = Double.parseDouble(inc_hpSeries);
		attackSeries = Double.parseDouble(inc_attackSeries);
		defenseSeries = Double.parseDouble(inc_defenseSeries);
		specialAttackSeries = Double.parseDouble(inc_specialAttackSeries);
		specialDefenseSeries = Double.parseDouble(inc_specialDefenseSeries);
		speedSeries = Double.parseDouble(inc_speedSeries);
		generation = Integer.parseInt(inc_generation);
		isLegendary = Boolean.parseBoolean(inc_isLegendary);
		isMegaEvolution = Boolean.parseBoolean(inc_isMegaEvolution);
		nerfType = Integer.parseInt(inc_nerfType);
		
//		if (speciesName.equals("Melmetal")) {
//			isNerfed = false;
//			hpSeries *= 0.91;
//			attackSeries *= 0.91;
//			defenseSeries *= 0.91;
//			specialAttackSeries *= 0.91;
//			specialDefenseSeries *= 0.91;
//		}
		
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
	
	protected double getHpSeries() {
		return hpSeries;
	}
	
	protected double getAttackSeries() {
		return attackSeries;
	}
	
	protected double getDefenseSeries() {
		return defenseSeries;
	}
	
	protected double getSpecialAttackSeries() {
		return specialAttackSeries;
	}
	
	protected double getSpecialDefenseSeries() {
		return specialDefenseSeries;
	}
	
	protected double getSpeedSeries() {
		return speedSeries;
	}
	
	protected int getGeneration() {
		return generation;
	}
	
	protected boolean isLegendary() {
		return isLegendary;
	}
	
	protected int getNerfType() {
		return nerfType;
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
		double result = 0;
		double scaledAttack;
		double speedModifier;
		double strongAttack;
		double weakAttack;
		
		if (nerfType == 0) {
			// No nerf
			
			strongAttack = Math.max(attackSeries, specialAttackSeries);
			weakAttack = Math.min(attackSeries, specialAttackSeries);
			
			scaledAttack = Math.round(2 * (((7.0/8.0) * strongAttack) + ((1.0/8.0) * weakAttack)));
			speedModifier = calculate_speedModifier();
			
			result = scaledAttack * speedModifier;
			result = Math.round(result);
		}
		
		else if (nerfType == 1) {
			// Standard nerf
			
			strongAttack = Math.max(attackSeries, specialAttackSeries);
			weakAttack = Math.min(attackSeries, specialAttackSeries);
			
			scaledAttack = Math.round(2 * (((7.0/8.0) * strongAttack) + ((1.0/8.0) * weakAttack)));
			speedModifier = calculate_speedModifier();
			
			result = scaledAttack * speedModifier;
			result = result * 0.91;
			result = Math.round(result);
		}
		
		else if (nerfType == 2) {
			// Melmetal nerf
			
			attackSeries *= 0.91;
			specialAttackSeries *= 0.91;
			
			strongAttack = Math.max(attackSeries, specialAttackSeries);
			weakAttack = Math.min(attackSeries, specialAttackSeries);
			
			scaledAttack = Math.round(2 * (((7.0/8.0) * strongAttack) + ((1.0/8.0) * weakAttack)));
			speedModifier = calculate_speedModifier();
			
			result = scaledAttack * speedModifier;
			result = Math.round(result);
		}
		
		return (int) result;
	}
	
	private int calculateDefensePkgo_Base() {
		double result = 0;
		double scaledDefense;
		double speedModifier;
		double strongDefense;
		double weakDefense;
		
		if (nerfType == 0) {
			// No nerf
			
			strongDefense = Math.max(defenseSeries, specialDefenseSeries);
			weakDefense = Math.min(defenseSeries, specialDefenseSeries);
			
			scaledDefense = Math.round(2 * (((5.0/8.0) * strongDefense) + ((3.0/8.0) * weakDefense)));
			speedModifier = calculate_speedModifier();
			
			result = scaledDefense * speedModifier;
			result = Math.round(result);
		}
		
		else if (nerfType == 1) {
			// Standard nerf
			
			strongDefense = Math.max(defenseSeries, specialDefenseSeries);
			weakDefense = Math.min(defenseSeries, specialDefenseSeries);
			
			scaledDefense = Math.round(2 * (((5.0/8.0) * strongDefense) + ((3.0/8.0) * weakDefense)));
			speedModifier = calculate_speedModifier();
			
			result = scaledDefense * speedModifier;
			result = result * 0.91;
			result = Math.round(result);
		}
		
		else if (nerfType == 2) {
			// Melmetal nerf
			
			defenseSeries *= 0.91;
			specialDefenseSeries *= 0.91;
		
			strongDefense = Math.max(defenseSeries, specialDefenseSeries);
			weakDefense = Math.min(defenseSeries, specialDefenseSeries);
			
			scaledDefense = Math.round(2 * (((5.0/8.0) * strongDefense) + ((3.0/8.0) * weakDefense)));
			speedModifier = calculate_speedModifier();
			
			result = scaledDefense * speedModifier;
			result = Math.round(result);
		}
		
		return (int) result;
	}
	
	private int calculateStaminaPkgo_Base() {
		double result = 0;
		
		if (nerfType == 0) {
			// No nerf
			
			result = hpSeries * 1.75 + 50;
			result = Math.round(result);
		}
		
		else if (nerfType == 1) {
			// Standard nerf
			
			result = hpSeries * 1.75 + 50;
			result = result * 0.91;
			result = Math.round(result);
		}
		
		else if (nerfType == 2) {
			// Melmetal nerf
			
			hpSeries *= 0.91;
			
			result = hpSeries * 1.75 + 50;
			result = Math.floor(result);
		}
		
		return (int) result;
	}
	
	//-----------------------------------------------------------------//
	
}
