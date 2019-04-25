package Misc

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle

/*Pressing this button brings fire support for user's side.
 * Press method does nothing, because the wanted things are
 * done in the PlayState class, we only need to know if the
 * button is being pressed.
 */
class SupportButton(x: Float, y: Float, width: Float, height: Float) extends Button(x,y,width,height) {
  private val hitbox: Rectangle = new Rectangle(x,y,width,height)
  private val texture: Texture = new Texture("cannonB.png")
  
  def press: Unit = {} // Does nothing.
  def isHovered(x: Float, y: Float): Boolean = hitbox.contains(x,y)
  override def toString: String = "CANNON BARRAGE"
  def getTexture: Texture = this.texture
  def getHitbox: Rectangle = this.hitbox
  def unitPrice: Int = 0
  def getCooldown: Int = 40
  def dispose: Unit = this.texture.dispose
}