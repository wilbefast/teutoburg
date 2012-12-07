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

import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.Rect;
import wjd.math.V2;
import wjd.teutoburg.Palette;

/**
 *
 * @author wdyce
 * @since Dec 6, 2012
 */
public class Tree implements IVisible, IPhysical
{
  /* CONSTANTS */
  // top of the tree
  public static final float SUMMIT_H = 30.0f;
  public static final float SUMMIT_H_VAR = SUMMIT_H*0.3f; // 30% random
  // trunk
  public static final float TRUNK_W = 5.0f;
  public static final float TRUNK_RADIUS = TRUNK_W*0.5f;
  public static final float TRUNK_H = SUMMIT_H*0.3f;
  // branches
  public static final float BRANCHES_W = 16.0f;
  public static final float BRANCHES_RADIUS = BRANCHES_W*0.5f;
  // model
  public static final float COLLISION_RADIUS = 10.0f;
  // visibility 
  private static final float ZOOM_IMPOSTER_THRESHOLD = 0.7f;
  
  /* ATTRIBUTES */
  private V2 position, summit, left, right;
  private Rect trunk;
  private boolean nearby = true;
  
  
  /* METHODS */
  
  // constructors
  public Tree(V2 position)
  {
    this.position = position;
    summit = new V2().xy(position.x, position.y - SUMMIT_H - SUMMIT_H_VAR*0.5f 
                                      + (float)(Math.random()*SUMMIT_H_VAR));
    left = new V2().xy(position.x - BRANCHES_RADIUS, position.y - TRUNK_H);
    right = new V2().xy(position.x + BRANCHES_RADIUS, position.y - TRUNK_H);
    trunk = new Rect(TRUNK_W, TRUNK_H).xy(position.x - TRUNK_RADIUS, 
                                          position.y - TRUNK_H - 1);
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
      nearby = (canvas.getCamera().getZoom() >= ZOOM_IMPOSTER_THRESHOLD);
      
      if(nearby)
      {
        // shadow
        canvas.setColour(Palette.GRASS_SHADOW);
        canvas.circle(position, BRANCHES_RADIUS, true);
        // trunk
        canvas.setColour(Palette.TREE_TRUNK);
        canvas.box(trunk, true);
        // leaves
        canvas.setColour(Palette.TREE_LEAVES);
        canvas.triangle(summit, left, right, true);
      }
      else
      {
        left.y = right.y = position.y;
        
        
        // imposter
        canvas.setColour(Palette.TREE_LEAVES);
        canvas.triangle(summit, left, right, true);
        
        left.xy(position.x - BRANCHES_RADIUS, position.y - TRUNK_H);
        right.xy(position.x + BRANCHES_RADIUS, position.y - TRUNK_H);
      }
    }
  }
  
  /* IMPLEMENTS -- IPHYSICAL */

  @Override
  public boolean isColliding(IPhysical other)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
