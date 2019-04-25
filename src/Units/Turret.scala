package Units

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import scala.collection.mutable.Buffer
import Objects._

/* A superclass for all tower turrets in the game.
 * Turret classes look like character ones, except they can't move or die.
 * */
abstract class Turret(private val enemy: Boolean, private val allCharacters: Buffer[Character], private val allProjectiles:Buffer[Projectile],private val yCoord: Float) {
  
  def update(dt: Float): Unit
  def getPosition: Vector2
  def getTexture: (Texture, Float, Boolean)
  def dispose(): Unit
}