package nl.tudelft.kroket.escape;

import java.io.DataInputStream;
import java.io.IOException;

import nl.tudelft.kroket.audio.AudioManager;
import nl.tudelft.kroket.input.InputHandler;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.NetworkClient;
import nl.tudelft.kroket.scene.SceneManager;
import nl.tudelft.kroket.scene.scenes.EscapeScene;
import nl.tudelft.kroket.screen.ScreenManager;
import nl.tudelft.kroket.screen.screens.LobbyScreen;
import jmevr.app.VRApplication;
import jmevr.input.VRBounds;
import jmevr.util.VRGuiManager;
import jmevr.util.VRGuiManager.POSITIONING_MODE;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioSource;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;

public class EscapeVR extends VRApplication {

	private final String className = this.getClass().getSimpleName();
	private Logger log = Logger.getInstance();

	/** Hostname of the gamehost. */
	private static final String HOSTNAME = "localhost";

	/** Portnumber of the gamehost. */
	private static int PORTNUM = 1234;
	
	private static int SECRECONN = 5;

	/** Enum for all GameStates. */
	public enum GameState {
		NONE, LOBBY, INTRO, PLAYING
	}

	/** Observer object. */
	Spatial observer;

	/** Current gamestate. */
	private GameState currentState = GameState.NONE;

	/** Font used in overlays. */
	BitmapFont guiFont;

	Picture ready;

	/** The text displayed in the HUD. */
	BitmapText hudText;

	private boolean forceUpdateState = true;

	/** State to force game to. */
	private GameState insertState = GameState.PLAYING; // start in lobby

	private AudioManager audioManager;
	private InputHandler inputHandler;
	private SceneManager sceneManager;
	private ScreenManager screenManager;
	private NetworkClient client;

	private void initAudioManager() {
		audioManager = new AudioManager(getAssetManager(), rootNode, "Sound/");
		audioManager.loadFile("waiting", "Soundtrack/waiting.wav", false,
				false, 5);
		audioManager.loadFile("ambient", "Soundtrack/ambient.wav", false,
				false, 5);
		audioManager.loadFile("welcome", "Voice/intro2.wav", false, false, 5);
		audioManager.loadFile("letthegamebegin", "Voice/letthegamebegin3.wav",
				false, false, 5);
	}

	private void initInputHandler() {
		inputHandler = new InputHandler(getInputManager(), observer, false);
	}

	private void initSceneManager() {
		sceneManager = new SceneManager(getAssetManager(), rootNode,
				getViewPort());
		sceneManager.loadScene("escape", EscapeScene.class);
	}

	private void initScreenManager() {
		Vector2f guiCanvasSize = VRGuiManager.getCanvasSize();
		screenManager = new ScreenManager(getAssetManager(), guiNode,
				guiCanvasSize.getX(), guiCanvasSize.getY());
		screenManager.loadScreen("lobby", LobbyScreen.class);
	}

	private void initNetworkClient() {
		client = new NetworkClient(HOSTNAME, PORTNUM);

		Thread thread = new Thread() {
			@Override
			public void run() {

				boolean breakLoop = false;

				while (!breakLoop) {

					while (!client.isConnected()) {
						if (!client.connect()) {
							log.info(className,
									"Failed to connect. Retrying...");
							hudText.setText("Trying to connect to server...");
							client.close();
							try {
								Thread.sleep(SECRECONN * 1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					log.info(className, "Trying to register client...");

					client.sendMessage("REGISTER[Rift-User]");
					client.sendMessage("TYPE[VIRTUAL]");

					DataInputStream stream = client.getStream();

					String line;
					try {
						while ((line = stream.readLine()) != null
								&& client.isConnected()) {
							receiveLoop(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		};
		thread.start();

	}

	/**
	 * Initialize the application.
	 */
	@Override
	public void simpleInitApp() {

		if (VRApplication.getVRHardware() != null) {
			System.out.println("Attached device: "
					+ VRApplication.getVRHardware().getName());
		}

		initObjects();
		initSceneManager();
		initAudioManager();
		initInputHandler();

		initScreenManager();
		initNetworkClient();

		sceneManager.getScene("escape").createScene();

		// /** Add fog to a scene */
		// FilterPostProcessor fpp=new FilterPostProcessor(getAssetManager());
		// FogFilter fog=new FogFilter();
		// fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
		// fog.setFogDistance(155);
		// fog.setFogDensity(2.0f);
		// fpp.addFilter(fog);
		// getViewPort().addProcessor(fpp);
		
//		Vector2f playArea = VRBounds.getPlaySize();
//		
//		if (playArea == null) {
//			log.info(className, "no vrbounds");
//		}
//		else
//			log.info(className, "vrBounds");

	}

	/**
	 * Create a label (text).
	 * 
	 * @param assetManager
	 *            assetmanager instance
	 * @param fontpath
	 *            path to the font asset
	 * @param x
	 *            the x-coordinate to position the label to
	 * @param y
	 *            the y-coordinate to position the label to
	 * @param width
	 *            the width of the label
	 * @param height
	 *            the height of the label
	 * @return the bitmap object
	 */
	protected BitmapText createLabel(AssetManager assetManager,
			String fontpath, float x, float y, float width, float height) {
		BitmapFont fnt = assetManager.loadFont(fontpath);
		BitmapText txt = new BitmapText(fnt, false);
		txt.setBox(new Rectangle(0, 0, width, height));
		txt.setLocalTranslation(x, y, 0);
		return txt;
	}

	/**
	 * Initialize the scene.
	 */
	private void initObjects() {
		Vector2f guiCanvasSize = VRGuiManager.getCanvasSize();
		observer = new Node("observer");

		guiFont = getAssetManager().loadFont("Interface/Fonts/Default.fnt");

		hudText = createLabel(getAssetManager(), "Interface/Fonts/Default.fnt",
				guiCanvasSize.getX() * 0.5f - 145,
				(guiCanvasSize.getY() * 0.5f) - 145, guiCanvasSize.getX(),
				guiCanvasSize.getY());
		hudText.setSize(24);

		hudText.setText("Loading...");
		guiNode.attachChild(hudText);

		Spatial sky = SkyFactory.createSky(getAssetManager(),
				"Textures/Sky/Bright/spheremap.png",
				SkyFactory.EnvMapType.EquirectMap);
		rootNode.attachChild(sky);

		// test any positioning mode here (defaults to AUTO_CAM_ALL)
		VRGuiManager
				.setPositioningMode(POSITIONING_MODE.AUTO_CAM_ALL_SKIP_PITCH);
		VRGuiManager.setGuiScale(0.4f);
		VRGuiManager.setPositioningElasticity(10f);

		observer.setLocalTranslation(new Vector3f(0.0f, 0.0f, 0.0f));

		VRApplication.setObserver(observer);
		rootNode.attachChild(observer);

		// use magic VR mouse cusor (same usage as non-VR mouse cursor)
		getInputManager().setCursorVisible(true);
		// /** Add fog to a scene */
		// FilterPostProcessor fpp=new FilterPostProcessor(getAssetManager());
		// FogFilter fog=new FogFilter();
		// fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
		// fog.setFogDistance(155);
		// fog.setFogDensity(2.0f);
		// fpp.addFilter(fog);
		// getViewPort().addProcessor(fpp);
	}

	/**
	 * Display intro overlay images.
	 * 
	 * @param f
	 *            time into intro
	 */
	// private void displayIntro(float f) {
	//
	// if (f < 3) {
	// clearOverlays();
	// overlayImage("Textures/overlay/teamkroket.png");
	// } else if (f < 5) {
	// clearOverlays();
	// overlayImage("Textures/overlay/presents.png");
	// } else if (f < 12) {
	// clearOverlays();
	// overlayImage("Textures/overlay/escaparade.png");
	// } else if (f < 18) {
	// clearOverlays();
	// overlayImage("Textures/overlay/locked.png");
	// } else if (f < 26) {
	// clearOverlays();
	// overlayImage("Textures/overlay/toxicgas.png");
	// } else if (f < 32) {
	// clearOverlays();
	// overlayImage("Textures/overlay/onlygoal.png");
	// } else if (f < 35) {
	// clearOverlays();
	// overlayImage("Textures/overlay/getout.png");
	// } else if (f < 38) {
	// clearOverlays();
	// overlayImage("Textures/overlay/onlyway.png");
	// } else if (f < 41) {
	// clearOverlays();
	// overlayImage("Textures/overlay/byworkingtogether.png");
	// } else if (f < 45) {
	// clearOverlays();
	// overlayImage("Textures/overlay/makeitoutalive.png");
	// } else if (f < 48) {
	// clearOverlays();
	// overlayImage("Textures/overlay/getready.png");
	// } else if (f < 51) {
	// clearOverlays();
	// overlayImage("Textures/overlay/toescape.png");
	// } else {
	// clearOverlays();
	// }
	// }

	/**
	 * Main method to update the scene.
	 */
	@Override
	public void simpleUpdate(float tpf) {

		if (forceUpdateState) {
			setGameState(insertState);
			forceUpdateState = false;
		}

		switch (currentState) {

		case LOBBY:
			guiNode.attachChild(hudText);
			break;
		case INTRO:
			if (audioManager.getStatus("welcome") == AudioSource.Status.Playing) {
				// displayIntro(audioManager.getPlaybackTime("welcome"));
			} else {
				setGameState(GameState.PLAYING);
			}
			inputHandler.handleInput(tpf);
			break;

		case PLAYING:
			inputHandler.handleInput(tpf);
			break;
		case NONE:
			break;
		default:
			break;
		}

		// System.out.println();

		// Spatial painting =
		// sceneManager.getScene("escape").getRootNode().getChild("painting");
		// of
		Spatial painting = sceneManager.getScene("escape")
				.getObject("painting");

		if (painting == null)
			return;

		//System.out.println("painting = " + painting + "\n");

		float distance = VRApplication.getFinalObserverPosition().distance(
				painting.getWorldBound().getCenter());

		if (distance < 4.0f)
			log.debug(className, String.format("Player is near painting, dist = %f", distance));


		//System.out.printf("distance to painting = %f\n", distance);

		// if (observer == null) {
		// return;
		// }
		//
		//
		// if (observer.getWorldBound() != null &&
		// observer.getWorldBound().getCenter() != null) {
		//
		// Vector3f pos = observer.getWorldBound().getCenter();
		// System.out.println(pos);
		//
		// }
		// else
		// System.out.println("observer.getWorldBound() ==  null");

	}

	/**
	 * Set the current game state (not thread-safe).
	 * 
	 * @param state
	 *            the new state
	 */
	public void setGameState(GameState state) {

		// do not switch state if already in given state
		if (currentState == state)
			return;

		switchState(currentState, state);
	}

	/**
	 * Switch game states.
	 * 
	 * @param oldState
	 *            the old state
	 * @param newState
	 *            the new state
	 */
	private void switchState(GameState oldState, GameState newState) {

		// do not switch state if already in given state
		if (oldState == newState)
			return;

		currentState = newState;

		System.out.println("Switching states from " + oldState.toString()
				+ " to " + newState.toString());

		switch (oldState) {
		case LOBBY:

			audioManager.stopAudio();
			screenManager.hideScreen("lobby");
			break;
		case INTRO:
			audioManager.stopAudio();
			break;
		case PLAYING:
			// only accept input in the playing state
			inputHandler.setAcceptInput(false);
			audioManager.stopAudio();

			break;
		case NONE:
		default:
			break;
		}

		switch (newState) {
		case LOBBY:
			inputHandler.setAcceptInput(false);
			if (audioManager.getStatus("waiting") != AudioSource.Status.Playing)
				audioManager.play("waiting");
			screenManager.showScreen("lobby");
			break;
		case INTRO:
			inputHandler.setAcceptInput(true);
			if (audioManager.getStatus("welcome") != AudioSource.Status.Playing)
				audioManager.play("welcome");
			break;
		case PLAYING:
			// accept input from the user
			inputHandler.setAcceptInput(true);
			audioManager.play("letthegamebegin");
			if (audioManager.getStatus("ambient") != AudioSource.Status.Playing)
				audioManager.play("ambient");

//			System.out.println("Creating gas...");
//			EscapeScene escapeScene = ((EscapeScene) sceneManager
//					.getScene("escape"));
			// escapeScene.createGas();

			break;
		case NONE:
		default:
			break;
		}
	}

	/**
	 * Process remote input.
	 * 
	 * @param line
	 *            incoming from remote source
	 */
	public void remoteInput(String line) {

		if (line.equals("START")) {
			guiNode.detachAllChildren();

			// do not call setGameState or switchState here as those run in
			// a different thread, use updateStates and insertState instead
			forceUpdateState = true;

			// to skip the intro, set insertState to PLAYING
			// insertState = GameState.PLAYING

			insertState = GameState.PLAYING;

			hudText.setText("");
		} else {

			hudText.setText(line);
			guiNode.attachChild(hudText);
		}
	}

	/**
	 * Main callback method for handling remote input from socket.
	 * 
	 * @param message
	 *            the input received from the socket
	 */
	public void receiveLoop(String message) {
		System.out.println("Remote input: " + message);

		remoteInput(message);
	}

}