package Misc

import com.badlogic.gdx.math.Rectangle
import scala.collection.mutable.Buffer
import Units._
import Objects._
import com.badlogic.gdx.graphics.Texture
/*User can bring a Marksman unit to the game by pressing this button.
 * */
class MarksmanButton(x: Float, y: Float, width: Float, height: Float, allCharacters: Buffer[Character], allProjectiles: Buffer[Projectile]) extends Button(x,y,width,height) {
  private val hitbox: Rectangle = new Rectangle(x,y,width,height)
  private val texture: Texture = new Texture("marksmanButton.png")
  private val price: Int = 25
  
  def press: Unit = this.allCharacters += new Marksman(false, allCharacters, allProjectiles)
  def isHovered(x: Float, y: Float): Boolean = hitbox.contains(x,y)
  override def toString: String = this.price + "$ - Marksman"
  def getTexture: Texture = this.texture
  def getHitbox: Rectangle = this.hitbox
  def unitPrice: Int = this.price
  def getCooldown: Int = 1
  def dispose: Unit = this.texture.dispose
}