package Objects

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.Texture

/*
 * Melee hits are also implemented with projectiles. Everytime a melee character attacks, it creates an invisible rectangle in front of it which then 
 * intersects with the enemy hitbox, making it take damage.
 */
class MeleeHit(xCoord: Float, yCoord: Float, fromEnemy: Boolean, private val damage: Int) extends Projectile(xCoord, yCoord, fromEnemy) {
  
  private val position: Vector3 = new Vector3(xCoord, yCoord,0)
  private val hitbox: Rectangle = new Rectangle(position.x, position.y, 50,1)
  private val texture: Texture = new Texture("blank.png")
  
  def update(dt: Float){
    // does nothing for melee.
  }
  
  def getHitbox = this.hitbox
  
  def getPos = this.position
  
  def getDamage: Int = this.damage
  
  // Returns just a transparent 1x1 image.
  def getTexture: Texture = this.texture
  
  def canExplode: Boolean = false
  
  def dispose(): Unit = this.texture.dispose()
}