package isii.characters;

import java.awt.Graphics2D;
import java.util.Random;
import isii.attacks.Attack;
import isii.characters.energy.Energy;
import isii.images.characters.ImageHeroine;
import isii.other.Dimensions;

public class Heroine extends Character { //TODO Cambiar drinkPotion y numDrinkPotion con sus respectivos metodos a esta clase
	
	private boolean defend = false;
	//private boolean doAction = false;
	private boolean drinkPotion;
	private int numSprite = 1;
	private int numAttack;
	
	public Heroine(Attack ataque1, Attack ataque2, Attack ataque3, Energy energy) {
		super(ataque1, ataque2, ataque3, Dimensions.heroineDimension(), 
				new ImageHeroine(Dimensions.heroineDimension()), energy);
		this.drinkPotion = false;
		numAttack = 1;
	}
	
	
	/**
	 * Metodo para pintar la heroine, todas las acciones
	 * @param g
	 */
	public synchronized void paint(Graphics2D g) {
		if (!this.weapon.isAttackFinish()) this.paintAttack(numAttack, g);
		else if (this.isDrinkPotion()) g.drawImage(((ImageHeroine) this.weapon.getImageCharacter()).getImagePotion(numSprite), X, Y, WIDTH, HEIGHT, null);
		else if (this.isDefend()) g.drawImage(((ImageHeroine) this.weapon.getImageCharacter()).getImageDefend(), X, Y, WIDTH, HEIGHT, null);
		else if (this.getEnergy().isFainting()) g.drawImage(this.weapon.getImageCharacter().getImageFainting(), X, Y, WIDTH, HEIGHT, null);
		else paintHalt(g);
	}
	
	/**
	 * Primera condicion, tiene que recuperar vida, cuando:
	 * - Esta en los turnos de beber la pocion y tambien esta desmayado.
	 * - Esta desmayado
	 * @param numAttack
	 * @param character
	 */
	public synchronized void yourTurn(int numAttack, Character character) {
		heroineAttack(numAttack, character);
	}
	
	private synchronized void heroineAttack(int numAttack, Character character) {
		this.numAttack = numAttack;
		this.attackEnemy(numAttack, character);
	}
	
	public synchronized boolean isStucked() {
		if (isDrinkPotion() || this.getEnergy().isFainting()) return true;
		else return false;
	}
	
	public synchronized void recoverEnergyPotion() {
		this.setDrinkPotion(false);
		this.recoverEnergy(this.getEnergy().getEnergyBar().getMaximum());
	}
	
	public synchronized boolean isDrinkPotion() {
		return drinkPotion;
	}

	public  void setDrinkPotion(boolean drinkPotion) {
		this.drinkPotion = drinkPotion;
	}
	
	public void setDefend(boolean defend) {
		this.defend = defend ? get80Percent() ? true : false : false;
	}
	
	public boolean isDefend() {
		return this.defend;
	}
	
	private boolean get80Percent() {
		if(new Random().nextInt(101) <= 80) return true;
		else return false;
	}
	
	/*public boolean isDoAction() {
		return doAction;
	}

	public void setDoAction(boolean doAction) {
		this.doAction = doAction;
	}*/
	
	public void printAnimationPotion() {
		SwapImagePotion swapImagePotion = new SwapImagePotion(12);
		swapImagePotion.start();
	}
	
	private class SwapImagePotion extends Thread {
		
		private int finalSprite;
		
		public SwapImagePotion(int finalSprite) {
			this.finalSprite = finalSprite;
		}
		
		@Override
		public synchronized void run() {
			for (numSprite = 1; numSprite < finalSprite; numSprite++) {
				try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		
	}

}
