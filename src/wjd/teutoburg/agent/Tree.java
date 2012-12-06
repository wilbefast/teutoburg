/*
 Copyright (C) 2012 William James Dyce

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wjd.teutoburg.agent;

import wjd.amb.view.Colour;
import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.Rect;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 6, 2012
 */
public class Tree implements IVisible, IPhysical
{
  /* CONSTANTS */
  public static final float HEIGHT_BASE = 20.0f;
  public static final float HEIGHT_VAR = HEIGHT_BASE*0.2f; // 20% random

  public static final float COLLISION_RADIUS = 10.0f;
  
  /* ATTRIBUTES */
  private V2 position, summit, left, right;
  private Rect trunk;
  
  
  /* METHODS */
  
  // constructors
  public Tree(V2 position)
  {
    this.position = position;
    summit = new V2().xy(position.x, position.y - HEIGHT_BASE - HEIGHT_VAR*0.5f 
                                      + (float)(Math.random()*HEIGHT_VAR));
  }
  
  // accessors
  public V2 getPosition()
  {
    return position;
  }
  
  /* IMPLEMENTS -- IVISIBLE */

  @Override
  public void render(ICanvas canvas)
  {
    if(canvas.getCamera().canSee(position))
    {
      canvas.setColour(Colour.BLACK);
      canvas.circle(position, 10.0f, true);
    }
  }
  
  /* IMPLEMENTS -- IPHYSICAL */

  @Override
  public boolean isColliding(IPhysical other)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
