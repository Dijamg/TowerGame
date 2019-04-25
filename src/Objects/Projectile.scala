package Objects

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.Texture

// A superclass for all "projectile" classes which implement bullets, arrows etc, which can hit characters and inflict damage.
abstract class Projectile(private val x: Float, private val y: Float,val fromEnemy: Boolean) {
  
  def getHitbox: Rectangle
  
  def getPos: Vector3

  def getTexture: Texture
  
  def getDamage: Int

  // update method is called every frame by the PlayState's main loop and it updates the position of the projectile.
  def update(dt: Float): Unit
  
  // An explosive projectile turns into smaller projectiles upon impact.
  def canExplode: Boolean
  
  def dispose(): Unit
  
}