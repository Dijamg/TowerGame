package Misc

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
/*Button instances work as the buttons in game, what you can press to bring units in the game.
 *A unit has and price and after clicking a button in the game, you are not able to bring other units for few seconds
 * determined by the getCooldown method. 
 */

abstract class Button(private val x: Float, private val y: Float, private val width: Float, private val height: Float) {
  
  def press: Unit
  def isHovered(x: Float, y: Float): Boolean
  def toString: String  // Used to bring a description on the screen while hovering a button.
  def getHitbox: Rectangle
  def getTexture: Texture
  def unitPrice: Int
  def getCooldown: Int
  def dispose: Unit
}