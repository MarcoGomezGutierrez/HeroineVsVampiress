package isii.program;

public class NormalGame extends GameController {

	private static final long serialVersionUID = -4169160719838456617L;
	
	public NormalGame(int x, int y, int width, int height, ResultJPanel resultPanel) {
		super(x, y, width, height, resultPanel, false);
		new StartGame().start();
	}
	
	private class StartGame extends Thread {
		
		@Override
		public void run() {
			startGame();
		}
		
		private synchronized void startGame() {
			do {
				refress(); //Necesito dormir el hilo para que me haga las comprobaciones sino empieza a ejecutarse sin comprobar.
				heroineController();
				refress();
				vampiressController();
			} while(!characterDead());
			
			// Detectar quien es el que ha muerto para mostrar si el jugador ha ganado o perdido.
			if (heroine.isDead()) winOrDead("GAME OVER");
			else winOrDead("YOU WIN");
		}

		private void winOrDead(String string) {
			setVisible(false);
			resultPanel.setText(string);
			resultPanel.setVisible(true);
		}

		/**
		 * Controlador de la heroina
		 */
		private synchronized void heroineController() {
			if (turn.getTurn() == 0 && action) {
				if (isCharacterRecovered() && isAnyAttack()) vampiressChangeTurnController();
			}
		}
		
		/**
		 * Controlador para cambiar de turno.
		 * 	- Si la vampiresa esta desmayada, recupera vida.
		 * 	- Si no esta desmayada ataca.
		 */
		private synchronized void vampiressChangeTurnController() {
			turn.changeTurn();
			if (vampiress_1.isFainting()) {
				vampiress_1.recoverEnergyFainting();
			} else {
				newAttack();
				vampiress_1.yourTurn(numAttack, heroine);
			}
		}

		/**
		 * Controlador de la vampiresa.
		 */
		private synchronized void vampiressController() {
			if(turn.getTurn() == 1) {
				if (isCharacterRecovered() && isAnyAttack()) {
					if (heroine.isDrinkPotion()) heroineDrinkPotionController();
					else heroineChangeTurnController();
				}
			}
		}
		
		/**
		 * Cuando la heroina esta tomando la pocion puede pasar lo siguiente:
		 * 	- Puede estar desmayada; recupera 2 de vida.
		 * 	- Cambio de turno. En este turno si la heroina ha llegado al turno 3 desde que se tomo la pocion recupero toda la vida y cambio "drinkPotion" a false.
		 * 	- Como he cambiado de turno esta la posibilidad de que este bebiendo todavia la pocion o ya haya recuperado la vida por la pocion:
		 * 		- Esta bebiendo la pocion "action" sigue en true para que la heroina de paso a cambiar al turno de la vampiresa y sigo ocultando los botones.
		 * 		- No esta bebiendo, significa que se ha curado al cambiar de turno. Muestro los botones y cambio "action" a false para que el controlador de la 
		 * 		heroina no cambie directamente al turno de la vampiresa sin atacar antes.
		 */
		private synchronized void heroineDrinkPotionController() {
			if (heroine.isFainting()) {
				heroine.recoverEnergyFainting(); // Recuperar vida desmayo cuando esta tomando la pocion
			}
			turn.changeTurn(); // Cambio de turno y puede que sea el turno 3 para curarse con la pocion.
			if (heroine.isDrinkPotion()) showPanels(false); //HIDE si sigue tomandose la pocion
			else showPanels(true); // SHOW si se ha tomado la pocion
		}
		
		/**
		 * Controlador de cambio de turno para la heroina.
		 * 	- Si estaba defendiendose, lo desabilito para que no pinte la animacion de defenderse.
		 * 	- Si esta desmayada, se cura y oculto los paneles.
		 * 	- Si no esta desmayada, muestro los paneles.
		 */
		private synchronized void heroineChangeTurnController() {
			turn.changeTurn();
			heroine.setDefend(false); // En el caso de estar defendiendose quitarle la animacion de defensa.
			if (heroine.isFainting()) {
				heroine.recoverEnergyFainting();
				showPanels(false); // HIDE si esta desmayada
			} else {
				showPanels(true); // SHOW si no esta desmayada
			}
		}

		/*
		 * Si no hay ningun personaje recuperando vida devuelve true, sino false
		 */
		private synchronized boolean isCharacterRecovered() {
			if (heroine.isEnergyRecovered() && vampiress_1.isEnergyRecovered()) return true; 
			else return false;
		}

		/**
		 * Si no hay ningun personaje atacando devuelve true, sino false
		 * @return boolean
		 */
		private synchronized boolean isAnyAttack() {
			if (heroine.isAttackFinish() && vampiress_1.isAttackFinish()) return true;
			else return false;
		}

		//Metodo necesario para que el bucle no se colapse
		private void refress() {
			try {Thread.sleep(1);} catch (InterruptedException e) {	e.printStackTrace();}
		}
		
		/**
		 * ?Algun personaje muerto?
		 * @return
		 */
		private synchronized boolean characterDead() {
			if (heroine.isDead() || vampiress_1.isDead()) return true;
			else return false;
		}
		
	}
	
}
