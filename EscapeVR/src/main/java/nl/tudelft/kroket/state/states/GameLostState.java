package nl.tudelft.kroket.state.states;

import nl.tudelft.kroket.audio.AudioManager;
import nl.tudelft.kroket.event.EventManager;
import nl.tudelft.kroket.input.InputHandler;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.scene.SceneManager;
import nl.tudelft.kroket.screen.HeadUpDisplay;
import nl.tudelft.kroket.screen.ScreenManager;
import nl.tudelft.kroket.state.GameState;

/**
 * State that indicates that the game is lost.
 * 
 * @author Team Kroket
 *
 */
public class GameLostState extends GameState {

  /** The unique singleton instance of this class. */
  private static GameLostState instance = new GameLostState();

  /** Current class, used as tag for logger. */
  private final String className = this.getClass().getSimpleName();

  /** Singleton logger instance. */
  private Logger log = Logger.getInstance();

  /**
   * Private constructor for the GameLostState.
   */
  private GameLostState() {}

  /**
   * Begin this state.
   */
  @Override
  public void begin(AudioManager audioManager, SceneManager sceneManager,
      ScreenManager screenManager) {

    log.debug(className, "Setting up " + className);

    audioManager.stopAudio();
    screenManager.getScreen("gameover").show();
    audioManager.play("gamelost");
  }

  /**
   * Stop this state.
   */
  @Override
  public void stop(AudioManager audioManager, SceneManager sceneManager, ScreenManager screenManager) {
    audioManager.stop("gamelost");
    screenManager.getScreen("gameover").hide();
  }

  public static GameState getInstance() {
    return instance;
  }

  /**
   * Updates the state.
   */
  @Override
  public void update(AudioManager audioManager, InputHandler inputHandler,
      ScreenManager screenManager, HeadUpDisplay hud, EventManager em, float tpf) {
    // inputHandler.handleInput(tpf);
  }

}
