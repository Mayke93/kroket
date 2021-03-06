package nl.tudelft.kroket.scene;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

import nl.tudelft.kroket.scene.scenes.EscapeScene;

/**
 * Test class for SceneManager.
 * @author Kroket
 *
 */
public class SceneManagerTest {

  private AssetManager am;

  private Node rn;

  private ViewPort vp;

  /**
   * Sets up for the tests.
   * 
   * @throws Exception
   *           the exception
   */
  @Before
  public void setUp() throws Exception {
    am = mock(AssetManager.class);
    rn = mock(Node.class);
    vp = mock(ViewPort.class);
  }

  /**
   * Test for the constructor of SceneManager.
   */
  @Test
  public void sceneManagerTest() {
    SceneManager sc = new SceneManager(am, rn, vp);
    assertNotNull(sc);
  }

  /**
   * Test for load scene.
   */
  @Test
  public void loadSceneTest() {
    SceneManager sc = new SceneManager(am, rn, vp);
    sc.loadScene("test", EscapeScene.class);
    assertTrue(sc.getScenes().containsKey("test"));
    assertNotNull(sc.getScene("test"));
  }

  /**
   * Test destroy scene method.
   * This test is not yet correct!
   * We need to find a way to get a proper non mock rootNode.
   */
  @Test
  public void destroySceneTest() {
    SceneManager sc = new SceneManager(am, rn, vp);
    sc.loadScene("test", EscapeScene.class);
    sc.destroyScene("test");
    assertNull(rn.getChild("test"));
  }

  /**
   * Test for getScene method.
   */
  @Test
  public void getSceneTest() {
    SceneManager sc = new SceneManager(am, rn, vp);
    sc.loadScene("test", EscapeScene.class);
    EscapeScene es = new EscapeScene("test", am, rn, vp);
    assertEquals(sc.getScene("test"), es);
  }

  /**
   * Test for getScene method if scene is not present.
   */
  @Test
  public void getSceneFailTest() {
    SceneManager sc = new SceneManager(am, rn, vp);;
    assertNull(sc.getScene("test"));
  }

  @Test
  public void extendEscapeSceneATest() {
    SceneManager sc = new SceneManager(am, rn, vp);
    EscapeScene scene = Mockito.mock(EscapeScene.class);  
    sc.scenes.put("escape", scene);
    Mockito.doNothing().when(scene).addOpenSafe();
    sc.extendEscapeScene("A");
    Mockito.verify(scene).addOpenSafe();

  }

  @Test
  public void extendEscapeSceneCTest() {
    SceneManager sc = new SceneManager(am, rn, vp);
    EscapeScene scene = Mockito.mock(EscapeScene.class);  
    sc.scenes.put("escape", scene);
    Mockito.doNothing().when(scene).addButtons();
    sc.extendEscapeScene("C");
    Mockito.verify(scene).addButtons();
  }

  @Test
  public void extendEscapeSceneDTest() {
    SceneManager sc = new SceneManager(am, rn, vp);
    EscapeScene scene = Mockito.mock(EscapeScene.class);  
    sc.scenes.put("escape", scene);
    Mockito.doNothing().when(scene).addCode13(Mockito.anyString(), Mockito.anyString());
    sc.extendEscapeScene("D");
    Mockito.verify(scene).addCode13(Mockito.anyString(), Mockito.anyString());
  }

}
