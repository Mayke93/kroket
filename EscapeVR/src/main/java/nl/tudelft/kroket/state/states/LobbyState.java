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
 * State that indicates that the game is in the lobby.
 * 
 * @author Team Kroket
 *
 */
public class LobbyState extends GameState {

  /** The unique singleton instance of this class. */
  private static LobbyState instance = new LobbyState();

  /** Current class, used as tag for logger. */
  private final String className = this.getClass().getSimpleName();

  /** Singleton logger instance. */
  private Logger log = Logger.getInstance();

  /**
   * Private constructor for the LobbyState.
   */
  private LobbyState() {}

  /**
   * Begin this state.
   */
  @Override
  public void begin(AudioManager audioManager, SceneManager sceneManager,
      ScreenManager screenManager) {

    log.debug(className, "Setting up " + className);

    if (screenManager == null) {
      log.error(className, "screenManager == null!");
    } else {
      audioManager.stopAudio();
      screenManager.getScreen("lobby").show();
      audioManager.play("lobby");
    }
  }

  /**
   * Stop this state.
   */
  @Override
  public void stop(AudioManager audioManager, SceneManager sceneManager, ScreenManager screenManager) {
    audioManager.stop("lobby");
    screenManager.getScreen("lobby").hide();
  }

  /**
   * Get the instance of this state.
   * 
   * @return the instance
   */
  public static GameState getInstance() {
    return instance;
  }

  /**
   * Updates this state.
   */
  @Override
  public void update(AudioManager audioManager, InputHandler inputHandler,
      ScreenManager screenManager, HeadUpDisplay hud, EventManager em, float tpf) {
    inputHandler.handleInput(tpf);
  }

}
