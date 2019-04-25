package Objects

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.Texture
import scala.util.Random
import States.ExceptionState
import Game.Game

/*Projectiles which appear when an explosive projectiles impacts with an unit or hits the ground.
 * Their path resembles a parabola. Their horizontal speed and path is determined randomly.
 * */
class Explosion(x: Float, y: Float, fromEnemy: Boolean) extends Projectile(x,y,fromEnemy)  {
  private val random = new Random()
  private val right: Boolean = random.nextInt(2) == 1    // If true, projectiles moves right else left.
  private val GRAVITY: Float = -5f
  private val initialYSpeed = 300
  private val xSpeed = if(right) random.nextInt(200) + 30 else -30 - random.nextInt(200)
  private val position: Vector3 = new Vector3(x,y+25,0)
  private val velocity: Vector3 = new Vector3(xSpeed,initialYSpeed,0)
  private val hitbox: Rectangle = new Rectangle(x,y +10,5,5)
  private var damage = 25
  
  private val texture: Texture = new Texture("bullet.png")
  
  
  def update(dt: Float): Unit = {
    this.velocity.add(0,this.GRAVITY,0)                                     //Increment the value of y-speed by gravity.
    this.velocity.y = this.velocity.scl(dt).y                               //Scale the Y velocity by the change of time, so it falls faster the longer time goes.
    this.position.add(this.velocity)                                        //Update the position.
    this.hitbox.setPosition(this.position.x, this.position.y)
    this.velocity.scl(1/dt)                                                 // Reverses what was scaled previously.
    if(this.position.x >= Game.GAME_WORLD_WIDTH - 175) this.damage = 0      // We dont want this to do damage to castles, so it does 0 damage, when it's xCoord is the same as enemy castle.
  }
  
  def getHitbox: Rectangle = this.hitbox
  
  def getPos: Vector3 = this.position

  def getTexture: Texture = this.texture
  
  def getDamage: Int = this.damage
  
  def canExplode: Boolean = false
  
  def dispose(): Unit = this.texture.dispose()
  
}