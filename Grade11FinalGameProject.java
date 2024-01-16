package FinalGameProject;
import java.util.Scanner;
import java.util.Random;

public class Grade11FinalGameProject {
	static Random random = new Random();
	static Scanner numInputs = new Scanner(System.in);
	//Holds all the stats names and values
	static String statNames[] = {"Strenght", "Agility", "Wits", "Block"};
	static int statLevel[] = new int[statNames.length];
	static String enemyNames[] = {"Decaying Corpse", "Massive Blob", "Small Slime", "Rest Site", "Rest Site"};

	static int statPoint = 5;
	static String playerName;
	//Arrays that hold then characters info
	//First slot [0] = the base(never changes)
	//Second slot [1] = the max(changes when stat points are entered)
	//Third slot [2] = the current info of the player(changes when damage is taken, block is added, etc)
	static int chracterHealth[] = {60, 60, 60};
	static int characterAdditionalDamage[] = {0, 0, 0};
	static int characterEnergy[] = {3, 3, 3};
	static int characterBlock[] = {0, 0, 0};
	static boolean hasBlock = false;
	static boolean restSite = false;
	//Holds the last stat entered to allow the redo option
	static int lastEnteredStat;
	static int roomCounter = 0;
	//Holds the max and current health of the enemies
	static int decayingCorpseHealth[] = {50, 50};
	static int massieBlobHealth[] = {65, 65};
	static int smallSlimeHealth[] = {20,20};
	//Holds the temperary health of the enemy used for combat
	static int currentEnemyHealth[] = new int[2];
		
	public static void main(String[] args) throws Exception {
		int playerDamage;
		String menuChoice;
		Scanner stringInputs = new Scanner(System.in);
		startScreen();
		consoleDelay();
		//ask for inputs 1 to play the game and 2 for game info
		System.out.println("[1]Play, [2]How to play");
		do {
			menuChoice = numInputs.next();
			if(menuChoice.equals("1")) {
				consoleDelay();
			} else if(menuChoice.equals("2")) {
				System.out.println("\n" + "***This game is a rouge like without end and will continue until the player dies***");
				System.out.println("\n" + "Starting the game the player will be sent into combat agenst a random enemy");
				System.out.println("\n" + "During the combat the player will be displayed a list of action, entering the number in the ");
				System.out.println("\n" + "square bracket corresponding to the action will perform that action");
				System.out.println("\n" + "These actions are limited by the amount of energy the actions cost and the amount of energy");
				System.out.println("\n" + "the player currently has, reseting after every turn");
				System.out.println("\n" + "Druring the enemies turn, they will perform a random attack onto the player dealing damage");
				System.out.println("\n" + "depending on their attack, lowering the player's hit points");
				System.out.println("\n" + "Hit points of the player will NOT reset after every battle, the only way for the player to heal");
				System.out.println("\n" + "is the rest at rest sites occuring as a room every so often");
				System.out.println("\n" + "After every 10 rooms the enemies states will scale, increasing their damage and max hit points");
				System.out.println("\n" + "Stat level effect:");
				System.out.println("\n" + "For every point in Strength the player will gain +5 to their max health");
				System.out.println("\n" + "For every point in Agility the player will gain +1 damgae to all their attacks");
				System.out.println("\n" + "For every 3 points in Wits the player will gain +1 max energy");
				System.out.println("\n" + "For every 3 points im Block the player will gain +1 additional block" + "\n");
				System.out.println("[1]Play, [2]How to play");
			} else {
				System.out.println("\n" + "Invalid entry, try again" + "\n");
				System.out.println("[1]Play, [2]How to play");
			}
		} while(!menuChoice.equals("1"));
		//Gets the player to input a name and checks if the input is longer than 10 characters or less then 1 characters
		do {		
			System.out.println("Enter your character's name:");
			playerName = stringInputs.nextLine();
			if(playerName.length() <= 1 || playerName.length() > 10) {
				System.out.println("\n" + "Username must be longer than 1 character and less than 10" + "\n");
			} 
		} while(playerName.length() <= 1 || playerName.length() > 10);
		//Before the actual game starts the player will be able to enter stats for their character
		consoleDelay();
		System.out.println("Welcome " + playerName);
		consoleDelay();
		displayCharacterInfo();
		consoleDelay();
		statPointEnter();
		statConfirmation();
		System.out.println("Onto the adventure");
		consoleDelay();
		//While player is alive the game will continue to run
		while(chracterHealth[2] > 0) {
			//randomizes the enemy combat for the room
			int tempx = enemyRandomizer();
			do {
				if(restSite == true) {
					restSiteOptions();
					break;
				}
				do {
					//prints out the enemy and player stat at the beginning and after each action
					enemyStats(tempx);
					displayCharacterInfo();
					playerDamage = getPlayerAction();
					calculateEnemyDamage(playerDamage);
					consoleDelay();
					//checks if the players health is 0 and will break from this loop which will lead to the break from the main while loop
					if(currentEnemyHealth[1] == 0) {
						enemyStats(tempx);
						displayCharacterInfo();
						consoleDelay();
						break;
					}
				} while(playerDamage != 0);
				//after caculating the damge on the enemy, it will check if the enemy's health is zero and break from this loop allowing the player to enter a stat and the game to enter another room
				if(currentEnemyHealth[1] == 0) {
					victoryScreen();
					break;
				}
				//If the enemy isn't dead, the enemy will perform an action
				emenysActions(tempx);
				consoleDelay();
				//checks after taking damage from the enemy if the players health is 0
				if(chracterHealth[2] <= 0) {
					break;
				}
				//resets the energy after every turn
				characterEnergy[2] = characterEnergy[1];
			} while(currentEnemyHealth[1] > 0);
			//checks if the player is dead which will break the main while loop for the game
			if(chracterHealth[2] <= 0) {
				System.out.println("Defeat...");
				System.out.println("Score: " + (roomCounter*100));
				System.out.println("Rooms Cleared: " + roomCounter);
				consoleDelay();
				break;
			}
			//after every combat the player will be able to enter the stat point they gained
			if(restSite == false) {
				statPointEnter();
				statConfirmation();
			}
		}
		numInputs.close();
		stringInputs.close();
	}
	//Entering 1 or 2, 1 will allow the player to heal for a percentage of their max health, 2 to gain a stat point to use
	public static void restSiteOptions() throws Exception {
		boolean confirm = false;
		double healPercent = (int)chracterHealth[1] * 0.20;
		int hpHeal = (int)healPercent;
		int playerChoice = 0;
		System.out.println("[1]Rest " + hpHeal + " [2]Statpoint +1");
		do {
			try {
				playerChoice = numInputs.nextInt();
				if(playerChoice == 1) {
					chracterHealth[2] = chracterHealth[2] + hpHeal;
					if(chracterHealth[2] > chracterHealth[1]) {
						chracterHealth[2] = chracterHealth[1];
					}
					consoleDelay();
					confirm= true;
				} else if(playerChoice == 2) {
					System.out.println("Stat point +1");
					statPoint++;
					consoleDelay();
					statPointEnter();
					statConfirmation();
					confirm= true;
				} else if (playerChoice != 1 || playerChoice != 2) {
					System.out.println("\n" + "Invalid entry, try again" + "\n");
					System.out.println("[1]Rest " + hpHeal + " [2]Statpoint +1");
				}
			} catch (Exception e) {
				System.out.println("\n" + "Invalid entry, try again" + "\n");
				System.out.println("[1]Rest " + hpHeal + " [2]Statpoint +1");
				numInputs.nextLine();
			}
		} while (confirm == false);
	}
	//Prints out the victory screen at the end of every combat and awading 1 stat point
	public static void victoryScreen() throws Exception {
		System.out.println("Victory!");
		System.out.println("Stat point +1");
		statPoint++;
		consoleDelay();
	}
	
	//Randomizes the enemies encounted and keeps the variable for room count
	public static int enemyRandomizer() {
		int healthIncrease = (int) 5*(roomCounter/10);
		restSite = false;
		int enemyRandom = random.nextInt(enemyNames.length);
		if(enemyNames[enemyRandom].equals("Rest Site")) {
			restSite = true;
		} else if(enemyNames[enemyRandom].equals("Decaying Corpse")) {
		
			for(int i = 0; i < decayingCorpseHealth.length; i++) {
				currentEnemyHealth[i] = decayingCorpseHealth[i]+healthIncrease;
			}
		} else if(enemyNames[enemyRandom].equals("Massive Blob")) {
			for(int i = 0; i < massieBlobHealth.length; i++) {
				currentEnemyHealth[i] = massieBlobHealth[i]+healthIncrease;
			}
		} else if(enemyNames[enemyRandom].equals("Small Slime")) {
			for(int i = 0; i < smallSlimeHealth.length; i++) {
				currentEnemyHealth[i] = smallSlimeHealth[i]+healthIncrease;
			}
		}

		roomCounter++;
		System.out.println("Room: " + roomCounter + "\n");
		return enemyRandom;
	}
	//Displays enemies stats
	public static void enemyStats(int tempx) {
		System.out.println(enemyNames[tempx] + "'s Info:" + "\n");
		System.out.print("HP:");
		for(int i = 0; i < currentEnemyHealth[1]; i++) {
			System.out.print("|");
		}
		System.out.print(" " + currentEnemyHealth[1] + "/" + currentEnemyHealth[0]);
		System.out.println("\n");

		
	}
	//Decides on the action the enemy will make with a randomizer
	public static void emenysActions(int tempx) {
		int damageIncrease = (int) roomCounter/10;
		int enemyActionRandom;
		int attackDamage = 0;
		if(enemyNames[tempx].equals("Decaying Corpse")) {
			String decayingCorpseActionName[] = {"Bite", "Slash", "Strike"};
			int enemyAttackDamage[] = {6+damageIncrease, 8+damageIncrease, 12+damageIncrease}; 
			enemyActionRandom = random.nextInt(decayingCorpseActionName.length);
			System.out.println(enemyNames[tempx] + " attacks with " + decayingCorpseActionName[enemyActionRandom] + " for " + enemyAttackDamage[enemyActionRandom] + " damage" + "\n");
			attackDamage = enemyAttackDamage[enemyActionRandom];
			
		} else if(enemyNames[tempx].equals("Massive Blob")) {
			String decayingCorpseActionName[] = {"Smash", "Smash"};
			int enemyAttackDamage[] = {6+damageIncrease, 5+damageIncrease}; 
			enemyActionRandom = random.nextInt(decayingCorpseActionName.length);
			System.out.println(enemyNames[tempx] + " attacks with " + decayingCorpseActionName[enemyActionRandom] + " for " + enemyAttackDamage[enemyActionRandom] + " damage" + "\n");
			attackDamage = enemyAttackDamage[enemyActionRandom];
			
		} else if(enemyNames[tempx].equals("Small Slime")) {
			String decayingCorpseActionName[] = {"Strike", "Strike", "Strike"};
			int enemyAttackDamage[] = {7+damageIncrease, 7+damageIncrease, 7+damageIncrease}; 
			enemyActionRandom = random.nextInt(decayingCorpseActionName.length);
			System.out.println(enemyNames[tempx] + " attacks with " + decayingCorpseActionName[enemyActionRandom] + " for " + enemyAttackDamage[enemyActionRandom] + " damage" + "\n");
			attackDamage = enemyAttackDamage[enemyActionRandom];
		}
		calculateDamage(attackDamage, tempx);

	}
	//Gets the players action for a list of available ones
	public static int getPlayerAction() {
		//Defines some variables used including the array holding the attack names, energy cost, and damage of them
		boolean hasEnergy = true;
		int damage;
		System.out.println("");
		String attacks[] = {"Light Attack", "Normal Attack", "Heavy Attack", "Block Attack"};
		int energyCost[] = {1, 2, 3, 1};
		int damageAndBlock[] = {3+characterAdditionalDamage[2], 7+characterAdditionalDamage[2], 14+characterAdditionalDamage[2], 5+characterBlock[1]};
		//This code will repeat until hasEnergy is false or until a the damage is returned
		do {
			try {
				//Checks if the players energy is zero to end the loop
				if(characterEnergy[2] == 0) {
					hasEnergy = false;
				}
				//out puts the following moves
				for(int i = 0; i < attacks.length; i++) {
					if(attacks[i].equals("Block Attack")) {
						System.out.println("[" + (i+1) + "]" + attacks[i] + "|" + "Energy:" + energyCost[i] + "|Blocks: " + damageAndBlock[i]);
					} else {
						System.out.println("[" + (i+1) + "]" + attacks[i] + "|" + "Energy:" + energyCost[i] + "|Damage: " + damageAndBlock[i]);
					}
					
				}
				System.out.println("[" + (attacks.length+1) + "]End Turn");
				//Gets input from player
				int playerChoice = numInputs.nextInt();
				//Checks if the player ended their turn
				if(playerChoice == (attacks.length+1)) {
					damage = 0;
					return damage;
				} else {
					//else if they dont have enough energy they will be asked to enter another action
					if(characterEnergy[2] - energyCost[playerChoice-1] < 0) {
						System.out.println("\n" + "Not enough energy" + "\n");
						damage = 0;
						
					} else {
						//else if the have energy damage of the attack will be returned
						damage = damageAndBlock[playerChoice-1];
						if(attacks[playerChoice-1].equals("Block Attack")) {
							hasBlock = true;
						}
						characterEnergy[2] = characterEnergy[2] - (energyCost[playerChoice-1]);
						return damage;
					}
				}
			} catch (Exception e) {
				System.out.println("\n" + "Invalid entry, try again" + "\n");
				numInputs.nextLine();
			}
		} while(hasEnergy);
		//damge will return 0 when energy is 0 and the player can't make an action
		System.out.println("Turn ended");
		damage = 0;
		return damage;
	}
	
	//Displays the character stats
	public static void displayCharacterInfo() {
	
		System.out.println(playerName + " Info: ");
		//
		System.out.print("\n" + "HP:");
		for(int i = 0; i < chracterHealth[2]; i++) {
			System.out.print("|");
		}
		System.out.print(" " + chracterHealth[2] + "/" + chracterHealth[1]);
		//
		System.out.print("\n" + "Energy:" );
		for(int i = 0; i < characterEnergy[2]; i++) {
			System.out.print("|");
		}
		System.out.print(" " + characterEnergy[2] + "/" + characterEnergy[1]);
		//
		System.out.print("\n" + "Base Damage:");
		for(int i = 0; i < characterAdditionalDamage[2]; i++) {
			System.out.print("|");
		}
		System.out.print(" " + characterAdditionalDamage[2]);
		//
		System.out.print("\n" + "Additional Block:");
		for(int i = 0; i < characterBlock[1]; i++) {
			System.out.print("|");
		}
		System.out.print(" " + characterBlock[1]);
		//
		System.out.print("\n" + "Current Block:");
		for(int i = 0; i < characterBlock[2]; i++) {
			System.out.print("|");
		}
		System.out.print(" " + characterBlock[2]);
		//
		System.out.println("");
	}
	//Displays the stats level
	public static void displayCharacterStats() {
		System.out.println("Enter your stats:");
		for(int i = 0; i < statNames.length; i++) {
			System.out.println("[" + (i+1) +"]" + statNames[i] + " [" + statLevel[i] + "]");
		}
		System.out.println("Total points: " + statPoint);
	}
	
	//calculates the stats
	public static void calculateStats() {
		//for every stat in strength +5 hp
		chracterHealth[1] = chracterHealth[0];
		chracterHealth[1] = chracterHealth[1] + statLevel[0]*5 ;
		if(roomCounter == 0) {
			chracterHealth[2] = chracterHealth[1];
		} 
		//for every adgility +1 base damage
		characterAdditionalDamage[1] = characterAdditionalDamage[0];
		characterAdditionalDamage[1] = characterAdditionalDamage[1] + statLevel[1];
		characterAdditionalDamage[2] = characterAdditionalDamage[1];
		//for every 2 wit +1 energy
		characterEnergy[1] = characterEnergy[0];
		characterEnergy[1] = (int) characterEnergy[1] + statLevel[2]/3;
		characterEnergy[2] = characterEnergy[1];
		//for every 2 block +1 additional block
		characterBlock[1] = characterBlock[0];
		characterBlock[1] = (int) characterBlock[1] + statLevel[3]/3;
		
	}
	//Calculates the damage the character gets
	public static void calculateDamage(int damage, int tempx) {
		//if the character's current block is 0 hp will be removed
		if(characterBlock[2] == 0) {
			chracterHealth[2] = chracterHealth[2]-damage;
		} else if (characterBlock[2] > 0) {
			//else if the chracter has bloock damage will be removed with the current block
			characterBlock[2] = characterBlock[2]-damage;
			//decides to remove hp when block is 0
			if(characterBlock[2] < 0) {
				int unblockedDamage = characterBlock[2]*(-1);
				characterBlock[2] = 0;
				chracterHealth[2] = chracterHealth[2]-unblockedDamage;
			}
		}
		if(chracterHealth[2] < 0) {
			chracterHealth[2] = 0;
		}
		enemyStats(tempx);
		displayCharacterInfo();
		//resets block after every enemy turn
		characterBlock[2] = 0;
		
	}
	//Calculates the players damage on the enemy
	public static void calculateEnemyDamage(int damage) {
		//Checks if character action is block and if its false hp will be removed from the enemies health
		if(hasBlock == false) {
			currentEnemyHealth[1] = currentEnemyHealth[1] - damage;
			//if the damage results in negative health in the enemy it will display as 0
			if(currentEnemyHealth[1] < 0) {
				currentEnemyHealth[1] = 0;
			}
		//if the player is blocking it will add to the current block instead and resets the hasBlock boolean
		} else if(hasBlock == true) {
			characterBlock[2] = characterBlock[2] + damage;
			hasBlock = false;
		}

	}

	
	//Adds points to the stat point array
	public static void statPointEnter() {
		displayCharacterStats();
		//repeats until the total stat points the player has is 0
		do {
			try {
				//get input untill stat point is 0;
				for(int i = 0; i < statPoint; i++) {
					lastEnteredStat = numInputs.nextInt();
					System.out.println("");
					if(lastEnteredStat == 1) {
						statPoint--;
						statLevel[0]++;
						displayCharacterStats();
					} else if(lastEnteredStat == 2) {
						statPoint--;
						statLevel[1]++;
						displayCharacterStats();
					} else if(lastEnteredStat == 3) {
						statPoint--;
						statLevel[2]++;
						displayCharacterStats();
					} else if(lastEnteredStat == 4) {
						statPoint--;
						statLevel[3]++;
						displayCharacterStats();
					} else {
						System.out.println("Invalid entry, try again" + "\n");
						displayCharacterStats();
					}
				}
			}catch (Exception e) {
				System.out.println("\n" + "Invalid entry, try again" + "\n");
				numInputs.nextLine();
				displayCharacterStats();
			}
		} while(statPoint > 0);
		calculateStats();
	}
	//Confirms the user's stats after they input a number
	public static void statConfirmation() {
		//Boolean that will reset at the start only turning true when the player confirms
		boolean confirmation = false;
		do {
			//temp variable for holding their response to confirm or redo
			int tempk;
			//Try loop to catch any invalid inputs
			try {
				System.out.println("");
				displayCharacterInfo();
				System.out.println("\n" + "[1]Confrim [2]Redo");
				tempk = numInputs.nextInt();
				//if input is confirm the boolean will turn true and end the do loop
				if(tempk == 1) {
					confirmation = true;
					consoleDelay();
				} else if (tempk == 2) {
					//Resets the the global variables for stats
					if(roomCounter == 0) {
						chracterHealth[1] = chracterHealth[0];
						chracterHealth[2] = chracterHealth[1];
						characterEnergy[1] = characterEnergy[0];
						characterEnergy[2] = characterEnergy[1];
						characterAdditionalDamage[1] = characterAdditionalDamage[0];
						characterAdditionalDamage[2] = characterAdditionalDamage[1];
						
						System.out.println("");
						statPoint = 5;
						for(int i = 0; i < statLevel.length; i++) {
							statLevel[i] = 0;
						}
					} else if(tempk == 2){
						//if its not the start room it will only remove one the players most resent input and give back the stat point to be re-entered
						statLevel[lastEnteredStat-1]--;
						statPoint++;
					}
					//Runs the method that gets the stat points inputs and adds them to the stat array
					statPointEnter();
				} else if(tempk != 1 || tempk !=2) {
					System.out.println("\n" + "Invalid entry, try again");
				}
			} catch (Exception e) {
				System.out.println("\n" + "Invalid entry, try again");
				numInputs.nextLine();
			}
		} while(confirmation == false);
	}

	//Creates the delay and dividers of the game
	public static void consoleDelay() throws Exception {
		Thread.sleep(500);
		System.out.println("");
		for(int i = 0 ; i < 96; i++) {
			System.out.print("-");
		}
		System.out.println("");
		System.out.println("");
		Thread.sleep(500);
	}
	//Holds the start screen 
	public static void startScreen() { 
		System.out.println("________________________________________________________________________________________________");
		System.out.println("|                                                                                              |");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|////////// //////// ///// //////// //////// ///// ///////// //////// ///// //////// //////////|");
		System.out.println("|///////// A ////// Y /// Y ////// A ////// Y /// Y /////// A ////// Y /// Y ////// A /////////|");
		System.out.println("|//////// A A ////// Y / Y ////// A A ////// Y / Y /////// A A ////// Y / Y ////// A A ////////|");
		System.out.println("|/////// A   A ////// Y Y ////// A   A ////// Y Y /////// A   A ////// Y Y ////// A   A ///////|");
 		System.out.println("|////// AAAAAAA ////// Y ////// AAAAAAA ////// Y /////// AAAAAAA ////// Y ////// AAAAAAA //////|");
		System.out.println("|///// A       A ///// Y ///// A       A ///// Y ////// A       A ///// Y ///// A       A /////|");
		System.out.println("|//// A         A //// Y //// A         A //// Y ///// A         A //// Y //// A         A ////|");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|//////////////////////////////////////////////////////////////////////////////////////////////|");
		System.out.println("|______________________________________________________________________________________________|");
		
	}

}