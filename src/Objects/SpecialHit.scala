package Objects

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.Texture

/* SpecialHit projectiles are projectiles which fall from sky, when fire support is called.
 */

class SpecialHit(x: Float, y: Float, fromEnemy: Boolean) extends Projectile(x,y,fromEnemy)  {
  private val GRAVITY: Float = -5f
  
  private val texture: Texture =if(!fromEnemy){ new Texture("flame.png") }else{ new Texture("verticalArrow.png") }
  private val position: Vector3 = new Vector3(x,y,0)
  private val velocity: Vector3 = new Vector3(0,0,0)
  private val hitbox: Rectangle = new Rectangle(x,y,25,20)
  
  def update(dt: Float): Unit = {
    this.velocity.add(0,this.GRAVITY,0)        //Increment the value of y-speed by gravity.
    this.velocity.scl(dt)                 //Scale the velocity by the change of time, so it falls faster the longer time goes.
    this.position.add(0,this.velocity.y,0)     //Update the position.
    this.hitbox.setPosition(this.position.x, this.position.y)
    this.velocity.scl(1/dt)               // Reverses what was scaled previously.
  }
  
  def getHitbox: Rectangle = this.hitbox
  
  def getPos: Vector3 = this.position

  def getTexture: Texture = this.texture
  
  def getDamage: Int = if(!fromEnemy) 100 else 50
  
  def canExplode: Boolean = if(!fromEnemy) true else false
  
  def dispose(): Unit = this.texture.dispose()
  
}